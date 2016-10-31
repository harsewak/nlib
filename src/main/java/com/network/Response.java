package com.network;

/**
 * Created by harsewaksingh on 29/02/16.
 */


/**
 * Created by harsewaksingh on 12/02/16.
 * {"success":true,"result":[],"error":"","error_key":"sms_validation_code"}
 */
public class Response<T> {

    Throwable error;

    public void setError(Throwable error) {
        this.error = error;
    }

    public Throwable getError() {
        return error;
    }

    //    @SerializedName("status")
//    Boolean status;
//    @SerializedName("result")
//    JsonElement result;
//    @SerializedName("error")
//    String error;

//    public Boolean getSuccess() {
//        return status;
//    }
//
//
//    public JsonElement getResult() {
//        return result;
//    }
//
//
//    @Override
//    public String toString() {
//        return new Gson().toJson(this);
//    }
//
//    public void setSuccess(Boolean status) {
//        this.status = status;
//    }

    // public void setResult(ArrayList<T> result) {
    //    this.result = result;
    // }
//
//    public void setError(String error) {
//        this.error = error;
//    }

//    public void setErrorKey(String errorKey) {
//        this.errorKey = errorKey;
//    }


}