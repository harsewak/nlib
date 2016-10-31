package com.network;


public interface OnResponse {
    void response(String response);

    void error(Throwable error);


}
