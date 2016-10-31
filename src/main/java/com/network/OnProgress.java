package com.network;

/**
 * Created by harsewaksingh on 02/03/16.
 */
public interface OnProgress {
    void progress(int progress);

    void onCompleted(String url);

    void onFailed();
}