package com.test.ffmpegdemo.compression;

import android.content.Context;

public abstract class MediaConstraints {
  private static final String TAG = "MediaConstraints";

  /*public static MediaConstraints getPushMediaConstraints() {
    return new PushMediaConstraints();
  }

  public static MediaConstraints getMmsMediaConstraints(int subscriptionId) {
    return new MmsMediaConstraints(subscriptionId);
  }*/

  public abstract int getImageMaxWidth(Context context);
  public abstract int getImageMaxHeight(Context context);
  public abstract int getImageMaxSize(Context context);

  public abstract int getGifMaxSize(Context context);
  public abstract int getVideoMaxSize(Context context);

  public int getUncompressedVideoMaxSize(Context context) {
    return getVideoMaxSize(context);
  }

  public int getCompressedVideoMaxSize(Context context) {
    return getVideoMaxSize(context);
  }

  public abstract int getAudioMaxSize(Context context);
  public abstract int getDocumentMaxSize(Context context);

  /*public boolean isSatisfied(@NonNull Context context, @NonNull Attachment attachment) {
    try {
      return (MediaUtil.isGif(attachment)    && attachment.getSize() <= getGifMaxSize(context)   && isWithinBounds(context, attachment.getUri())) ||
             (MediaUtil.isImage(attachment)  && attachment.getSize() <= getImageMaxSize(context) && isWithinBounds(context, attachment.getUri())) ||
             (MediaUtil.isAudio(attachment)  && attachment.getSize() <= getAudioMaxSize(context)) ||
             (MediaUtil.isVideo(attachment)  && attachment.getSize() <= getVideoMaxSize(context)) ||
             (MediaUtil.isFile(attachment) && attachment.getSize() <= getDocumentMaxSize(context));
    } catch (IOException ioe) {
      Log.w(TAG, "Failed to determine if media's constraints are satisfied.", ioe);
      return false;
    }
  }*/

  /*private boolean isWithinBounds(Context context, Uri uri) throws IOException {
    try {
      InputStream is = PartAuthority.getAttachmentStream(context, uri);
      Pair<Integer, Integer> dimensions = BitmapUtil.getDimensions(is);
      return dimensions.first  > 0 && dimensions.first  <= getImageMaxWidth(context) &&
             dimensions.second > 0 && dimensions.second <= getImageMaxHeight(context);
    } catch (BitmapDecodingException e) {
      throw new IOException(e);
    }
  }*/

  /*public boolean canResize(@NonNull Attachment attachment) {
    return MediaUtil.isImage(attachment) && !MediaUtil.isGif(attachment) ||
           MediaUtil.isVideo(attachment) && isVideoTranscodeAvailable();
  }*/

  /*public static boolean isVideoTranscodeAvailable() {
    return Build.VERSION.SDK_INT >= 26 && MemoryFileDescriptor.supported();
  }*/
}
