package com.network;

import android.util.Log;

import com.google.gson.Gson;
import com.network.background.Task;
import com.network.background.TaskManager;
import com.network.background.TaskType;

import java.io.IOException;
import java.lang.reflect.Type;

/**
 * Created by harsewaksingh on 18/03/16.
 */
public class RequestTask extends Task {

    OnResponse onResponse;
    Request request;

    public static final String NETWORK_ERROR = "";
    public static final String UNKNOWN_ERROR = "";

    public RequestTask(Request request, OnResponse onResponse) {
        super(TaskType.DEFAULT);
        this.request = request;
        this.onResponse = onResponse;
    }

    public static RequestTask execute(Request request, OnResponse onResponse) {
        RequestTask requestTask = new RequestTask(request, onResponse);
        TaskManager.getInstance().execute(requestTask);
        return requestTask;
    }

    @Override
    protected void runInBackground() {
        try {
            String response = processRequest(request);
//            Type type1 = ((ParameterizedType) onResponse.getClass().getGenericSuperclass()).getActualTypeArguments()[0];
            // T type = response(response, request.getType());
            onSuccess(response);
        } catch (Exception e) {
            onError(e.fillInStackTrace());
        }
    }

//    public interface OnResponse {
//        void response(Object object);
//
//        void error(Throwable throwable);
//    }
//
//
//    Class<?> aClass;
//
//    public void setaClass(Class<?> aClass) {
//        this.aClass = aClass;
//    }
//
//    private void onSuccess(final Object response) {
//        runOnUiThread(new Runnable() {
//            @Override
//            public void run() {
//                try {
//                    onResponse.response(response);
//                } catch (NullPointerException e) {
//                    e.printStackTrace();
//                }
//            }
//        });
//    }


//    public <T> T fromJson(String json, Class<T> classOfT) throws JsonSyntaxException {
//        return new Gson().fromJson(json, classOfT);
//    }

    private void onError(final Throwable error) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                try {
                    onResponse.error(error);
                } catch (NullPointerException e) {
                    e.printStackTrace();
                }
            }
        });
    }

    private void onSuccess(final String response) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                try {
                    onResponse.response(response);
                } catch (NullPointerException e) {
                    e.printStackTrace();
                }
            }
        });
    }

    public static <T> T response(String response, Type typeOfDest) {
        //Type type = Response.class.getClass();
        return (T) new Gson().fromJson(response, typeOfDest);
    }


    /**
     * Process request on any background thread
     */
    public static String processRequest(Request request) throws IOException {


        String res = "";
        Log.d("RequestTask", "request " + toJson(request));
        switch (request.getMethodType()) {
            case GET:
                res = processGetRequest(request);
                break;
            case POST_RAW:
                res = processPostRawRequest(request);
                break;
            case POST:
                res = processPostRequest(request);
                break;
            case GET_HEADER:
                res = processGetHeaderRequest(request);
                break;
            case POST_HEADER:
                res = processPostHeaderRequest(request);
                break;
            case POST_UPLOAD_FILE:
                res = processPostFileUpload(request);
                break;
            case POST_DELETE:
                res = processPostDeleteRequest(request);
                break;
        }
        Log.d("RequestTask", "response is = " + res);
        return res;

//        catch (Exception e) {
//            //any other exception
//            response = new Response();
//            response.setSuccess(false);
//            response.setError(UNKNOWN_ERROR);
//        }
        //  return response;

    }

    public static String toJson(Request request) {
        return new Gson().toJson(request);
    }

    public static String processPostHeaderRequest(Request request) throws IOException {
        if ((request.getPayload() == null)) {
            throw new NullPointerException("Payload shouldn't be null");
        }
        if (request.getHeader() == null) {
            throw new NullPointerException("Header Map shouldn't be null");
        }
        return NetworkUtils.requestWithPostMethod(request.getUrl(), request.getPayload(), request.getHeader());
    }

    public static String processPostDeleteRequest(Request request) throws IOException {
        return NetworkUtils.requestPostDelete(request.getUrl(), request.getHeader());
    }

    public static String processPostFileUpload(Request request) throws IOException {
        return NetworkUtils.uploadFile(request.getFileParameter(),request.getFile(), request.getUrl(), request.getPayload(), request.getHeader());
    }

    /**
     * a post request with raw data
     */
    public static String processPostRawRequest(Request request) throws IOException {
        if ((request.getData() == null)) {
            throw new NullPointerException("Data shouldn't be null");
        }
        return NetworkUtils.requestRawPostMethod(request.getUrl(), request.getData(), null);
    }

    /**
     * a post request with json data
     */
    public static String processPostRequest(Request request) throws IOException {
        if ((request.getPayload() == null)) {
            throw new NullPointerException("Payload shouldn't be null");
        }
        return NetworkUtils.requestWithPostMethod(request.getUrl(), request.getPayload(), null);
    }

    public static String processGetRequest(Request request) throws IOException {
        return NetworkUtils.requestGet(request.getUrl());
    }

    public static String processGetHeaderRequest(Request request) throws IOException {
        return NetworkUtils.getRequestHeader(request.getUrl(), request.getHeader());
    }
}
