package com.network;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.util.Log;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLEncoder;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;

public class NetworkUtils {
    private static final String TAG = NetworkUtils.class.getSimpleName();
    public static final String NETWORK_FAILURE = "networkFailure";
    public static final int TIMEOUT = 60000;

    // check availability of Internet
    public static boolean isInternetAvailable(Context context) {
        boolean haveConnectedWifi = false;
        boolean haveConnectedMobile = false;

        ConnectivityManager cm = (ConnectivityManager) context
                .getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo[] netInfo = cm.getAllNetworkInfo();
        for (NetworkInfo ni : netInfo) {
            if (ni.getTypeName().equalsIgnoreCase("WIFI"))
                if (ni.isConnected())
                    haveConnectedWifi = true;
            if (ni.getTypeName().equalsIgnoreCase("MOBILE"))
                if (ni.isConnected())
                    haveConnectedMobile = true;
        }
        return haveConnectedWifi || haveConnectedMobile;
    }

    public static String convertStreamToString(InputStream is) throws IOException {
//        BufferedReader reader = new BufferedReader(new InputStreamReader(is));
        BufferedReader reader = new BufferedReader(new InputStreamReader(is, "UTF-8"));
        StringBuilder sb = new StringBuilder();
        String line = null;
        try {
            while ((line = reader.readLine()) != null) {
                sb.append(line).append("\n");
            }
        } finally {
            try {
                is.close();
                reader.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return sb.toString();
    }

    private static String handleResponse(HttpURLConnection connection) throws IOException {
        int serverResponseCode = connection.getResponseCode();
        if (serverResponseCode == 200) {
            return convertStreamToString(connection.getInputStream());
        } else if (serverResponseCode == 0) {
            throw new IOException("Server is down");
        } else {
            return convertStreamToString(connection.getErrorStream());
        }
    }


    public static enum ErrorType {
        TIME_OUT, UNKNOWN_HOST, CONNECTION_ERROR
    }

    public static class Response {
        int status = 0;
        String response;

    }

    /** */
    public static String getRequestHeader(String url, HashMap<String, String> headerMap) throws IOException {
        HttpURLConnection connection = createConnection(url);
        connection.setRequestMethod("GET");
        connection.setRequestProperty("Connection", "close");
        for (Entry<String, String> entry : headerMap.entrySet()) {
            connection.setRequestProperty(entry.getKey(),
                    entry.getValue());
        }
        connection.connect();
        return handleResponse(connection);
    }

    public static String requestPostDelete(String url,
                                           HashMap<String, String> headerMap) throws IOException {
        HttpURLConnection connection = createConnection(url);
        //connection.setDoOutput(false);
        connection.setRequestMethod("DELETE");
        connection.setRequestProperty(
                "Content-Type", "application/x-www-form-urlencoded");
        if (headerMap != null) {
            for (Entry<String, String> entry : headerMap.entrySet()) {
                connection.setRequestProperty(entry.getKey(),
                        entry.getValue());
            }
        }
        connection.connect();
        return handleResponse(connection);
    }

//    /**
//     * This method is used for user verification
//     *
//     * @param url       an absolute URL giving the base location
//     * @param headerMap jsonData data required for server
//     * @return
//     */
//    public static String requestWithHeader(String url,
//                                           HashMap<String, String> headerMap) throws IOException {
//        HttpURLConnection urlConnection;
//        String result = null;
//        // Connect
//        URL uri = new URL(url);
//        urlConnection = (HttpURLConnection) uri.openConnection();
//        urlConnection.setRequestMethod("GET");
//        //if (Build.VERSION.SDK != null && Build.VERSION.SDK_INT > 13) {
//        urlConnection.setRequestProperty("Connection", "close");
//        // }
//        for (Entry<String, String> entry : headerMap.entrySet()) {
//            urlConnection.setRequestProperty(entry.getKey(),
//                    entry.getValue());
//        }
//
//        urlConnection.connect();
//
//        InputStream inputStream = null;
//        int responseCode = urlConnection.getResponseCode();
//        if (responseCode == HttpURLConnection.HTTP_OK) {
//            // Read
//            inputStream = urlConnection.getInputStream();
//        } else {
//            inputStream = urlConnection.getErrorStream();
//        }
//        BufferedReader bufferedReader = new BufferedReader(
//                new InputStreamReader(inputStream,
//                        "UTF-8"));
//
//        String line = null;
//        StringBuilder sb = new StringBuilder();
//
//        while ((line = bufferedReader.readLine()) != null) {
//            sb.append(line);
//        }
//
//        bufferedReader.close();
//
//        result = sb.toString();
//
//        return result;
//    }

    /**
     * This method is used to sending user verification to server.
     *
     * @param url       an absolute URL giving the base location
     * @param jsonData  data required for server
     * @param headerMap connection request property
     * @return request is successful or not
     */
    public static String requestWithPostMethod(String url, JSONObject jsonData,
                                               HashMap<String, String> headerMap) throws IOException {
        HttpURLConnection urlConnection;

        String result = null;
        // try {
        // Connect
        urlConnection = createPostConnection(url);
        urlConnection.setRequestMethod("POST");
        urlConnection
                .setRequestProperty("Content-Type", "application/json");
        urlConnection.setRequestProperty("Accept", "application/json");
        if (headerMap != null) {
            for (Entry<String, String> entry : headerMap.entrySet()) {
                urlConnection.setRequestProperty(entry.getKey(),
                        entry.getValue());
            }
        }

        urlConnection.connect();

        // Write
        OutputStream outputStream = urlConnection.getOutputStream();
        BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(
                outputStream, "UTF-8"));
        writer.write(jsonData.toString());
        writer.close();
        outputStream.close();

        InputStream inputStream = null;
        int responseCode = urlConnection.getResponseCode();
        if (responseCode == HttpURLConnection.HTTP_OK) {
            // Read
            inputStream = urlConnection.getInputStream();
        } else {
            inputStream = urlConnection.getErrorStream();
        }
        BufferedReader bufferedReader = new BufferedReader(
                new InputStreamReader(inputStream,
                        "UTF-8"));
        String line = null;
        StringBuilder sb = new StringBuilder();

        while ((line = bufferedReader.readLine()) != null) {
            sb.append(line);
        }

        bufferedReader.close();

        // {"success":true,"result":[],"error":"","error_key":"email_validation_code"}

        result = sb.toString();

//        } catch (UnsupportedEncodingException e) {
//            e.printStackTrace();
//        } catch (IOException e) {
//            e.printStackTrace();
//            return NETWORK_FAILURE;
//        }
        return result;
    }


    public static String toRawPostData(HashMap<String, String> params) throws UnsupportedEncodingException {
        StringBuilder result = new StringBuilder();
        boolean first = true;
        for (Map.Entry<String, String> entry : params.entrySet()) {
            if (first)
                first = false;
            else
                result.append("&");

            result.append(URLEncoder.encode(entry.getKey(), "UTF-8"));
            result.append("=");
            result.append(URLEncoder.encode(entry.getValue(), "UTF-8"));
        }
        return result.toString();
    }

    static class Result {
        String result;
        int statusCode;
    }

    /**
     * This method is used to sending user verification to server.
     *
     * @param url       an absolute URL giving the base location
     * @param postData  data required for server
     * @param headerMap connection request property
     * @return request is successful or not
     */
    public static String requestRawPostMethod(String url, HashMap<String, String> postData,
                                              HashMap<String, String> headerMap) throws IOException {
        HttpURLConnection urlConnection;

        String result = null;
        // try {
        // Connect
        urlConnection = createPostConnection(url);
        urlConnection.setRequestMethod("POST");
        urlConnection.setReadTimeout(TIMEOUT);
//        urlConnection
//                .setRequestProperty("Content-Type", "application/json");
//        urlConnection.setRequestProperty("Accept", "application/json");
        if (headerMap != null) {
            for (Entry<String, String> entry : headerMap.entrySet()) {
                urlConnection.setRequestProperty(entry.getKey(),
                        entry.getValue());
            }
        }

        urlConnection.connect();

        // Write
        OutputStream outputStream = urlConnection.getOutputStream();
        BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(
                outputStream, "UTF-8"));
        writer.write(toRawPostData(postData));
        writer.close();
        outputStream.close();

        InputStream inputStream = null;
        int responseCode = urlConnection.getResponseCode();
        if (responseCode == HttpURLConnection.HTTP_OK) {
            // Read
            inputStream = urlConnection.getInputStream();
        } else {
            inputStream = urlConnection.getErrorStream();
        }
        BufferedReader bufferedReader = new BufferedReader(
                new InputStreamReader(inputStream,
                        "UTF-8"));
        String line = null;
        StringBuilder sb = new StringBuilder();

        while ((line = bufferedReader.readLine()) != null) {
            sb.append(line);
        }

        bufferedReader.close();

        // {"success":true,"result":[],"error":"","error_key":"email_validation_code"}

        result = sb.toString();

//        } catch (UnsupportedEncodingException e) {
//            e.printStackTrace();
//        } catch (IOException e) {
//            e.printStackTrace();
//            return NETWORK_FAILURE;
//        }
        return result;
    }

    /**
     * @param urlToDownlod     an absolute URL giving the base location
     * @param pathToSave       path where file will be save
     * @param name             name of file
     * @param progressCallback progressCallback return result
     * @return return path where file is saved
     * @throws Exception
     */
    public static String downloadVideo(String urlToDownlod, String pathToSave,
                                       String name, OnProgress progressCallback) throws Exception {
        int TIMEOUT_CONNECTION = 5000;// 5sec
        int TIMEOUT_SOCKET = 30000;// 30sec
        URL url = new URL(urlToDownlod);
        // long startTime = System.currentTimeMillis();
        // Open a connection to that URL.
        URLConnection ucon = url.openConnection();
        // this timeout affects how long it takes for the app to realize there's
        // a connection problem
        ucon.setReadTimeout(TIMEOUT_CONNECTION);
        ucon.setConnectTimeout(TIMEOUT_SOCKET);
        // getting file length
        int lenghtOfFile = ucon.getContentLength();
        // Define InputStreams to read from the URLConnection.
        // uses 3KB download buffer
        File file = new File(pathToSave + "/" + name);
        file.createNewFile();
        InputStream is = ucon.getInputStream();
        BufferedInputStream inStream = new BufferedInputStream(is, 1024 * 5);
        FileOutputStream outStream = new FileOutputStream(file);
        byte[] buff = new byte[5 * 1024];
        // Read bytes (and store them) until there is nothing more to read(-1)
        int len;
        long total = 0;
        while ((len = inStream.read(buff)) != -1) {
            total += len;
            if (progressCallback != null) {
                progressCallback
                        .progress((int) ((total * 100) / lenghtOfFile));
            }
            outStream.write(buff, 0, len);
        }
        // clean up
        outStream.flush();
        outStream.close();
        inStream.close();

        return file.getAbsolutePath();
    }


    /**
     * @param sourceFile      sourceFile to upload
     * @param uploadServerUri url for upload file
     * @param headerData
     * @return
     */
    public static String uploadFile(String fileParameter, File sourceFile,
                                    String uploadServerUri, JSONObject formData, Map<String, String> headerData) throws IOException {


        HttpURLConnection connection = null;
        DataOutputStream outputStream = null;
        String lineEnd = "\r\n";
        String twoHyphens = "--";
        String boundary = "*****";
        int bytesRead, bytesAvailable, bufferSize;
        byte[] buffer;
        int maxBufferSize = 1 * 1024 * 1024;
        try {
            // open a URL connection to the Servlet
            FileInputStream fileInputStream = null;
            String fileName = "";
            if (sourceFile != null) {
                fileInputStream = new FileInputStream(sourceFile);
                fileName = sourceFile.getName();
            }
            URL url = new URL(uploadServerUri);
            connection = (HttpURLConnection) url.openConnection();

            connection.setDoInput(true);
            connection.setDoOutput(true);
            connection.setUseCaches(false);

            connection.setRequestMethod("POST");
            connection.setRequestProperty("Connection", "Keep-Alive");
            connection.setRequestProperty("User-Agent", "Android Multipart HTTP Client 1.0");
            connection.setRequestProperty("Content-Type", "multipart/form-data; boundary=" + boundary);

            outputStream = new DataOutputStream(connection.getOutputStream());
            outputStream.writeBytes(twoHyphens + boundary + lineEnd);
            if (sourceFile != null)
                outputStream.writeBytes("Content-Disposition: form-data; name=\"" + fileParameter + "\"; filename=\"" + fileName + "\"" + lineEnd);
            //outputStream.writeBytes("Content-Type: " + fileMimeType + lineEnd);
            outputStream.writeBytes("Content-Transfer-Encoding: binary" + lineEnd);

            outputStream.writeBytes(lineEnd);

            if (fileInputStream != null) {
                bytesAvailable = fileInputStream.available();
                bufferSize = Math.min(bytesAvailable, maxBufferSize);
                buffer = new byte[bufferSize];

                bytesRead = fileInputStream.read(buffer, 0, bufferSize);
                while (bytesRead > 0) {
                    outputStream.write(buffer, 0, bufferSize);
                    bytesAvailable = fileInputStream.available();
                    bufferSize = Math.min(bytesAvailable, maxBufferSize);
                    bytesRead = fileInputStream.read(buffer, 0, bufferSize);
                }
            }
            outputStream.writeBytes(lineEnd);
            try {
                // Upload POST Data
                Iterator<String> keys = formData.keys();
                while (keys.hasNext()) {
                    String key = keys.next();
                    String value = formData.getString(key);

                    outputStream.writeBytes(twoHyphens + boundary + lineEnd);
                    outputStream.writeBytes("Content-Disposition: form-data; name=\"" + key + "\"" + lineEnd);
                    outputStream.writeBytes("Content-Type: text/plain" + lineEnd);
                    outputStream.writeBytes(lineEnd);
                    outputStream.writeBytes(value);
                    outputStream.writeBytes(lineEnd);
                }
            } catch (JSONException e) {
            }

            outputStream.writeBytes(twoHyphens + boundary + twoHyphens + lineEnd);


            // Responses from the server (code and message)
            int serverResponseCode = connection.getResponseCode();
            // String serverResponseMessage = conn.getResponseMessage();
            String response = convertStreamToString(connection.getInputStream());

            // close the streams //
            if (fileInputStream != null)
                fileInputStream.close();
            outputStream.flush();
            Log.d(TAG, "dos " + outputStream.toString());
            outputStream.close();

            if (serverResponseCode == 200) {
                return response;
            }

        } catch (MalformedURLException ex) {
            Log.e(NetworkUtils.class.getSimpleName(), ex.getMessage());
        }  // End else block
        return "";
    }


    /**
     * Create a new HttpURLConnection instance by parsing url
     */
    public static HttpURLConnection createPostConnection(String url)
            throws IOException {
        HttpURLConnection connection = createConnection(url);
        // conn.setUseCaches(false);
        connection.setDoInput(true);
        connection.setDoOutput(true);
        return connection;
    }

    public static HttpURLConnection createConnection(String url)
            throws IOException {
        URL urlUrl = new URL(url);
        HttpURLConnection connection = (HttpURLConnection) urlUrl.openConnection();
        return connection;
    }

    /**
     * This method is used for user verification
     *
     * @param url an absolute URL giving the base location
     * @return
     */
    public static String requestGet(String url) throws IOException {
        HttpURLConnection urlConnection;
        String result = null;

        // Connect
        url = url.replaceAll(" ", "%20");
        URL uri = new URL(url);
        urlConnection = (HttpURLConnection) uri.openConnection();
        urlConnection.setRequestMethod("GET");
        // for (Entry<String, String> entry : headerMap.entrySet()) {
        // urlConnection.setRequestProperty(entry.getKey(),
        // entry.getValue());
        // }
        // urlConnection.setConnectTimeout(10000);
        urlConnection.connect();

        // Read
        BufferedReader bufferedReader = new BufferedReader(
                new InputStreamReader(urlConnection.getInputStream(),
                        "UTF-8"));

        String line = null;
        StringBuilder sb = new StringBuilder();

        while ((line = bufferedReader.readLine()) != null) {
            sb.append(line);
        }

        bufferedReader.close();

        result = sb.toString();

        return result;
    }
}
