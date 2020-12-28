package com.arthenica.mobileffmpeg.usecase;

import android.os.AsyncTask;
import android.os.Handler;

import com.arthenica.mobileffmpeg.AsyncFFmpegExecuteTask;
import com.arthenica.mobileffmpeg.AsyncGetMediaInformationTask;
import com.arthenica.mobileffmpeg.Config;
import com.arthenica.mobileffmpeg.ExecuteCallback;
import com.arthenica.mobileffmpeg.FFmpeg;
import com.arthenica.mobileffmpeg.GetMediaInformationCallback;
import com.arthenica.mobileffmpeg.Level;
import com.arthenica.mobileffmpeg.LogCallback;
import com.arthenica.mobileffmpeg.LogMessage;
import com.arthenica.mobileffmpeg.MediaInformation;
import com.arthenica.mobileffmpeg.Statistics;
import com.arthenica.mobileffmpeg.StatisticsCallback;

import java.util.LinkedList;
import java.util.Queue;

/**
 * Created by Mafhujur Rahman Arif on 23/7/2019.
 */

public class MobileFFmpeg {
    private static MobileFFmpeg mobileFFmpeg;
    private static final String TAG = "MAHFUJ_FFMPEG";
    private boolean avoidFirstResponse;
    private boolean isFFmpegRunning;
    private Queue<Task> taskQueue;
    private boolean isCanceled;


    private MobileFFmpeg() {
        Config.setLogLevel(Level.AV_LOG_VERBOSE);
    }

    public static MobileFFmpeg getInstance() {
        if (mobileFFmpeg == null) {
            mobileFFmpeg = new MobileFFmpeg();
        }
        return mobileFFmpeg;
    }

    public synchronized void execute(String[] cmd, ExecuteBinaryResponseHandler executeBinaryResponseHandler) {
        /*
         * todo: the ffmpeg we are using here does't support multithreading. FFmpeg crashes in this senario.
         * to prevent the crash added a task queue to hold the given task until the previous task is complete.
         * In the avm audio cutter  getFileInformation() being called before ffmpeg finishes executing extractFileFormat() call.
         * */
        getTaskQueue().add(new Task(cmd, executeBinaryResponseHandler));
        checkAndStartNextTask();
    }
    public void getMediaInfo(String filePath, GetMediaInformationCallback responseHandler){
        executeMediaAsyncTask(filePath, responseHandler);
    }
    private void executeMediaAsyncTask(String filePath, final GetMediaInformationCallback responseHandler) {
        AsyncGetMediaInformationTask ffTask = new AsyncGetMediaInformationTask(filePath, new GetMediaInformationCallback(){
            @Override
            public void apply(MediaInformation mediaInformation) {
                if(responseHandler != null){
                    responseHandler.apply(mediaInformation);
                }
            }
        });
        ffTask.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
    }
    private void executeAsyncTask(String[] cmd, final ExecuteBinaryResponseHandler responseHandler) {
        try {
            AsyncFFmpegExecuteTask ffTask = new AsyncFFmpegExecuteTask(cmd, new ExecuteCallback() {
                @Override
                public void apply(long executionId, final int returnCode) {
                    if (responseHandler == null) return;

                    Handler handler = new Handler();
                    disableStatistics();
                    handler.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            String output = Config.getLastCommandOutput();
                            switch (returnCode) {
                                case Config.RETURN_CODE_SUCCESS:
                                    responseHandler.onSuccess(output);
                                    break;
                                case Config.RETURN_CODE_CANCEL:
                                default:
                                    responseHandler.onFailure(isCanceled, output);
                                    break;
                            }

                            responseHandler.onFinish();

                            isFFmpegRunning = false;
                            checkAndStartNextTask();
                        }
                    }, isCanceled ? 0 : 500);
                }
            });
            ffTask.execute();

            responseHandler.onStart();
            isCanceled = false;
        } catch (Exception e) {
            e.printStackTrace();
            if (responseHandler != null) {
                responseHandler.onFailure(isCanceled,e.getMessage());
            }

            isFFmpegRunning = false;
            checkAndStartNextTask();
        }

    }

    private void checkAndStartNextTask() {
        //Log.d(TAG, "checkAndStartNextTask: called");
        if (!isFFmpegRunning && !getTaskQueue().isEmpty()) {
            Task task = getTaskQueue().peek();
            getTaskQueue().remove();

            isFFmpegRunning = true;
            enableStatistics(task.getResponseHandler());
            executeAsyncTask(task.getCmd(), task.getResponseHandler());
        }
    }

    private static void enableLog() {
        Config.enableLogCallback(new LogCallback() {
            @Override
            public void apply(LogMessage message) {
                String msg = message.getText();
                Level level = message.getLevel();
            }
        });
    }

    private void enableStatistics(final ExecuteBinaryResponseHandler responseHandler) {
        avoidFirstResponse = true;
        Config.enableStatisticsCallback(new StatisticsCallback() {
            @Override
            public void apply(Statistics statistics) {
                if (avoidFirstResponse) {
                    avoidFirstResponse = false;
                    return;
                }
                if (responseHandler != null && !isCanceled) {
                    String msg = String.format("%s %s", statistics.getTime(), statistics.getSize());
                    responseHandler.onProgress(msg);
                }
            }
        });
    }

    private void disableStatistics() {
        Config.enableStatisticsCallback(null);
    }

    public void cancelTask() {
        isCanceled = true;
        FFmpeg.cancel();
        getTaskQueue().clear();
    }
    public boolean isRunning(){
        return isFFmpegRunning;
    }

    private Queue<Task> getTaskQueue() {
        if (taskQueue == null) {
            taskQueue = new LinkedList<>();
        }
        return taskQueue;
    }
}
