package com.arthenica.mobileffmpeg.usecase;

public class Task{
    String[] cmd;
    ExecuteBinaryResponseHandler responseHandler;

    public Task(String[] cmd, ExecuteBinaryResponseHandler responseHandler) {
        this.cmd = cmd;
        this.responseHandler = responseHandler;
    }

    public String[] getCmd(){
        return cmd;
    }

    public ExecuteBinaryResponseHandler getResponseHandler(){
        return responseHandler;
    }
}