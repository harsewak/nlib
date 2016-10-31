package com.network;

import com.google.gson.reflect.TypeToken;

import org.json.JSONObject;

import java.io.File;
import java.util.HashMap;

/**
 * Created by harsewaksingh on 18/03/16.
 */
public class Request {

    String url;
    JSONObject payload;
    HashMap<String, String> header;
    HashMap<String, String> data;
    MethodType methodType;
    File file;
    String fileParameter;
    TypeToken<?> type;

    public void setFileParameter(String fileParameter) {
        this.fileParameter = fileParameter;
    }

    public String getFileParameter() {
        return fileParameter;
    }

    public void setType(TypeToken<?> type) {
        this.type = type;
    }

    public TypeToken<?> getType() {
        return type;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public JSONObject getPayload() {
        return payload;
    }

    public void setPayload(JSONObject payload) {
        this.payload = payload;
    }

    public HashMap<String, String> getHeader() {
        return header;
    }

    public void setHeader(HashMap<String, String> header) {
        this.header = header;
    }

    public MethodType getMethodType() {
        return methodType;
    }

    public void setMethodType(MethodType methodType) {
        this.methodType = methodType;
    }

    public void setFile(File file) {
        this.file = file;
    }

    public File getFile() {
        return file;
    }

    public void setData(HashMap<String, String> data) {
        this.data = data;
    }

    public HashMap<String, String> getData() {
        return data;
    }

    public static Request postRawRequest(String url, HashMap<String, String> data) {
        Request request = new Request();
        request.setUrl(url);
        request.setData(data);
        request.setMethodType(MethodType.POST_RAW);
        return request;
    }

    public static Request postRequest(String url, JSONObject payload) {
        Request request = new Request();
        request.setUrl(url);
        request.setPayload(payload);
        request.setMethodType(MethodType.POST);
        return request;
    }

    public static Request postHeaderRequest(String url, JSONObject jsonObject, HashMap<String, String> header) {
        Request request = new Request();
        request.setUrl(url);
        request.setPayload(jsonObject);
        request.setHeader(header);
        request.setMethodType(MethodType.POST_HEADER);
        return request;
    }

    public static Request postDeleteRequest(String url, HashMap<String, String> header) {
        Request request = new Request();
        request.setUrl(url);
        request.setHeader(header);
        request.setMethodType(MethodType.POST_DELETE);
        return request;
    }

    public static Request getRequest(String url) {
        Request request = new Request();
        request.setUrl(url);
        request.setMethodType(MethodType.GET);
        return request;
    }

    public static Request getHeaderRequest(String url, HashMap<String, String> header) {
        Request request = new Request();
        request.setUrl(url);
        request.setHeader(header);
        request.setMethodType(MethodType.GET_HEADER);
        return request;
    }


    public static Request postFileUpload(String url, String fileParameter, File file, JSONObject jsonObject) {
        Request request = new Request();
        request.setUrl(url);
        request.setFile(file);
        request.setPayload(jsonObject);
        request.setFileParameter(fileParameter);
        request.setMethodType(MethodType.POST_UPLOAD_FILE);
        return request;
    }
}
