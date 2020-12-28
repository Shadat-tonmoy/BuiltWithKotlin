package com.arthenica.mobileffmpeg.usecase;

/**
 * Created by Mafhujur Rahman Arif on 23/7/2019.
 */

public interface FFcommandExecuteResponseHandler extends ResponseHandler {

    /**
     * on Success
     *
     * @param message complete output of the binary command
     */
    void onSuccess(String message);

    /**
     * on Progress
     *
     * @param message current output of binary command
     */
    void onProgress(String message);

    /**
     * on Failure
     *
     * @param message complete output of the binary command
     */
    void onFailure(boolean isCanceled, String message);

}
