package com.test.ffmpegdemo.compression;

import android.content.Context;
import android.database.Cursor;
import android.media.MediaDataSource;
import android.media.MediaMetadataRetriever;
import android.net.Uri;
import android.provider.OpenableColumns;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;


import java.io.Closeable;
import java.io.File;
import java.io.FileDescriptor;
import java.io.FileInputStream;
import java.io.IOException;
import java.text.NumberFormat;
import java.util.Locale;

@RequiresApi(26)
public final class InMemoryTranscoder implements Closeable {

  private static final String TAG = "InMemoryTranscoder";

  private final           Context                        context;
  private            MediaDataSource                dataSource;
  private File fileSource;
  private Uri uriSource;
  private final           long                           upperSizeLimit;
  private final           long                           inSize;
  private final           long                           duration;
  private final           int                            inputBitRate;
  private final           VideoBitRateCalculator.Quality targetQuality;
  private final           long                           memoryFileEstimate;
  private final           boolean                        transcodeRequired;
  private final           long                           fileSizeEstimate;
  private final @Nullable Options                        options;
  public String compressionErrorMsg = "";

  private @Nullable MemoryFileDescriptor memoryFile;

  /**
   * @param upperSizeLimit A upper size to transcode to. The actual output size can be up to 10% smaller.
   */
  public InMemoryTranscoder(@NonNull Context context, @NonNull Uri dataSource, @Nullable Options options, long upperSizeLimit) throws IOException, VideoSourceException {
    this.context    = context;
    this.uriSource = dataSource;
    this.options    = options;

    final MediaMetadataRetriever mediaMetadataRetriever = new MediaMetadataRetriever();
    try {
      mediaMetadataRetriever.setDataSource(context,uriSource);
    } catch (RuntimeException e) {
      Log.w(TAG, "Unable to read datasource", e);
      throw new VideoSourceException("Unable to read datasource", e);
    }

    this.inSize         = getFileSize(uriSource); //new File(uriSource.getPath()).length();
    this.duration       = getDuration(mediaMetadataRetriever);
    this.inputBitRate   = VideoBitRateCalculator.bitRate(inSize, duration);
    this.targetQuality  = new VideoBitRateCalculator(upperSizeLimit).getTargetQuality(duration, inputBitRate);
    this.upperSizeLimit = upperSizeLimit;
    Log.e(TAG, "InMemoryTranscoder: TargetQuality : "+targetQuality+" InputBitrate : "+inputBitRate+" Size : "+inSize+" Duration : "+duration +" upperSizeLimit : "+upperSizeLimit+" locationMetaData : "+containsLocation(mediaMetadataRetriever));

    this.transcodeRequired = inputBitRate >= targetQuality.getTargetTotalBitRate() * 1.2 || inSize > upperSizeLimit;
    if (!transcodeRequired)
    {
      compressionErrorMsg = "Video is within 20% of target bitrate, below the size limit, contained no location metadata or custom options.";
      Log.i(TAG, compressionErrorMsg);
    }
    //transcodeRequired  = true;

    this.fileSizeEstimate   = targetQuality.getFileSizeEstimate();
    this.memoryFileEstimate = (long) (fileSizeEstimate * 1.1);
  }

  public @NonNull MediaStream transcode(@NonNull Progress progress,
                                        @Nullable CancelationSignal cancelationSignal)
      throws IOException, EncodingException, VideoSizeException
  {
    if (memoryFile != null) throw new AssertionError("Not expecting to reuse transcoder");

    float durationSec = duration / 1000f;

    NumberFormat numberFormat = NumberFormat.getInstance(Locale.US);

    Log.i(TAG, String.format(Locale.US,
                             "Transcoding:\n" +
                             "Target bitrate : %s + %s = %s\n" +
                             "Target format  : %dp\n" +
                             "Video duration : %.1fs\n" +
                             "Size limit     : %s kB\n" +
                             "Estimate       : %s kB\n" +
                             "Input size     : %s kB\n" +
                             "Input bitrate  : %s bps",
                             numberFormat.format(targetQuality.getTargetVideoBitRate()),
                             numberFormat.format(targetQuality.getTargetAudioBitRate()),
                             numberFormat.format(targetQuality.getTargetTotalBitRate()),
                             targetQuality.getOutputResolution(),
                             durationSec,
                             numberFormat.format(upperSizeLimit / 1024),
                             numberFormat.format(fileSizeEstimate / 1024),
                             numberFormat.format(inSize / 1024),
                             numberFormat.format(inputBitRate)));

    if (fileSizeEstimate > upperSizeLimit) {
      throw new VideoSizeException("Size constraints could not be met!");
    }

    memoryFile = MemoryFileDescriptor.newMemoryFileDescriptor(context,
                                                              "TRANSCODE",
                                                              memoryFileEstimate);
    final long startTime = System.currentTimeMillis();

    final FileDescriptor memoryFileFileDescriptor = memoryFile.getFileDescriptor();

    final MediaConverter converter = new MediaConverter();

    //converter.setInput(new MediaInput.MediaDataSourceMediaInput(dataSource));
    converter.setInput(new MediaInput.UriMediaInput(context,uriSource));
    converter.setOutput(memoryFileFileDescriptor);
    converter.setVideoResolution(targetQuality.getOutputResolution());
    converter.setVideoBitrate(targetQuality.getTargetVideoBitRate());
    converter.setAudioBitrate(targetQuality.getTargetAudioBitRate());

    if (options != null) {
      if (options.endTimeUs > 0) {
        long timeFrom = options.startTimeUs / 1000;
        long timeTo   = options.endTimeUs   / 1000;
        converter.setTimeRange(timeFrom, timeTo);
        Log.i(TAG, String.format(Locale.US, "Trimming:\nTotal duration: %d\nKeeping: %d..%d\nFinal duration:(%d)", duration, timeFrom, timeTo, timeTo - timeFrom));
      }
    }

    converter.setListener(percent -> {
      progress.onProgress(percent);
      return cancelationSignal != null && cancelationSignal.isCanceled();
    });

    converter.convert();

    // output details of the transcoding
    long  outSize           = memoryFile.size();
    float encodeDurationSec = (System.currentTimeMillis() - startTime) / 1000f;
    String successMessage =  String.format(Locale.US,
            "Transcoding complete:\n" +
                    "Transcode time : %.1fs (%.1fx)\n" +
                    "Output size    : %s kB\n" +
                    "  of Original  : %.1f%%\n" +
                    "  of Estimate  : %.1f%%\n" +
                    "  of Memory    : %.1f%%\n" +
                    "Output bitrate : %s bps",
            encodeDurationSec,
            durationSec / encodeDurationSec,
            numberFormat.format(outSize / 1024),
            (outSize * 100d) / inSize,
            (outSize * 100d) / fileSizeEstimate,
            (outSize * 100d) / memoryFileEstimate,
            numberFormat.format(VideoBitRateCalculator.bitRate(outSize, duration)));

    Log.i(TAG,successMessage);
    progress.onSuccess(successMessage);


    if (outSize > upperSizeLimit) {
      throw new VideoSizeException("Size constraints could not be met!");
    }

    memoryFile.seek(0);

    return new MediaStream(new FileInputStream(memoryFileFileDescriptor), "video/mp4", 0, 0);
  }

  public boolean isTranscodeRequired() {
    return transcodeRequired;
  }

  private long getFileSize(Uri fileUri) {
    Cursor returnCursor = context.getContentResolver().
            query(fileUri, null, null, null, null);
    int sizeIndex = returnCursor.getColumnIndex(OpenableColumns.SIZE);
    returnCursor.moveToFirst();

    return returnCursor.getLong(sizeIndex);
  }

  @Override
  public void close() throws IOException {
    if (memoryFile != null) {
      memoryFile.close();
    }
  }

  private static long getDuration(MediaMetadataRetriever mediaMetadataRetriever) throws VideoSourceException {
    String durationString = mediaMetadataRetriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_DURATION);
    if (durationString == null) {
      throw new VideoSourceException("Cannot determine duration of video, null meta data");
    }
    try {
      long duration = Long.parseLong(durationString);
      if (duration <= 0) {
        throw new VideoSourceException("Cannot determine duration of video, meta data: " + durationString);
      }
      return duration;
    } catch (NumberFormatException e) {
      throw new VideoSourceException("Cannot determine duration of video, meta data: " + durationString, e);
    }
  }

  private static boolean containsLocation(MediaMetadataRetriever mediaMetadataRetriever) {
    String locationString = mediaMetadataRetriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_LOCATION);
    return locationString != null;
  }

  public interface Progress {
    void onProgress(int percent);
    void onSuccess(String successMessage);
  }

  public interface CancelationSignal {
    boolean isCanceled();
  }

  public final static class Options {
    final long startTimeUs;
    final long endTimeUs;

    public Options(long startTimeUs, long endTimeUs) {
      this.startTimeUs = startTimeUs;
      this.endTimeUs   = endTimeUs;
    }
  }
}
