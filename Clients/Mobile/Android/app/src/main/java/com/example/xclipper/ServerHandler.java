package com.example.xclipper;

import android.annotation.SuppressLint;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.IntentSender;
import android.content.ServiceConnection;
import android.content.SharedPreferences;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.content.res.AssetManager;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.database.DatabaseErrorHandler;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.UserHandle;
import android.util.Log;
import android.view.Display;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.RequestQueue;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.fasterxml.jackson.core.JsonProcessingException;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.net.CookieHandler;
import java.net.CookieManager;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.URI;
import java.util.HashMap;
import java.util.Map;

import io.socket.client.IO;
import io.socket.client.Socket;
import io.socket.emitter.Emitter;
import io.socket.engineio.client.transports.Polling;
import io.socket.engineio.client.transports.WebSocket;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;

import static java.util.Collections.singletonList;
import static java.util.Collections.singletonMap;


/*
refresh - SERVER SENDS TO CLIENT every 10 seconds. Gets all the clips that exists for the user and sends it to the client
sendNewClip - CLIENT SENDS TO SERVER when a new clip is added.
recieveNewClip - SERVER SENDS TO CLIENT when a new clip is added. (sends to other clients to notify that a new clip is sent)
clipSaveStatus - SERVER SENDS TO CLIENT to notify the client that the clip was saved successfully or failed
requestRefresh - CLIENT SENDS TO SERVER to notify the server that the clients wants a full clip refresh for their client

 */
public class ServerHandler extends Context {
    static MediaType JSON;
    OkHttpClient client;
    String cookieString = "";
    //Clipboard clipboard = Toolkit.getDefaultToolkit().getSystemClipboard();
    // DefaultListModel defaultListModel;
    //JPanel mainList;
    Color themeColor;
    IO.Options options;
    ServerSocket serverSocket;
    io.socket.client.Socket socket;
    String URL = "http://10.248.1.132:3000";
    String[] endpoints = {
            "http://192.168.1.20:3000/login",
            "http://10.248.1.132:3000/signup"
    };
    CookieManager cm;
    private volatile boolean running = true;
    Context context;

//    java.net.CookieHandler.setDefault();


    public ServerHandler(Context applicationContext) {

        //this.mainList = mainList;
        //this.themeColor = themeColor;

        this.context = applicationContext;
        CookieHandler.setDefault(new CookieManager());
//        URI uri = URI.create(endpoints[0]);
//        javax.net.ssl.HttpsURLConnection.setDefaultHostnameVerifier(
//                new javax.net.ssl.HostnameVerifier(){
//
//                    public boolean verify(String hostname, javax.net.ssl.SSLSession sslSession) {
//                        if (hostname.equals("localhost:3000")) {
//                            return true;
//                        }
//                        return false;
//                    }
//                });


//        try {
//            System.out.println("Connecting to server ");
//            serverSocket = new ServerSocket(9000, 0, InetAddress.getLoopbackAddress());
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
    }


    public void connectToServer() {
        String hostname = "localhost";
        int port = 3000;
//        Map<String, String> cook = new HashMap<>();
//        cook.put("cookie", cookieString);

        System.out.println("creating options object");
        options = createOptions();

        try {
            System.out.println("inside the try block");
            InetAddress addr = InetAddress.getByName(hostname);

//            IO.socket(URI.create(endpoints[0]), options);
            socket = IO.socket(URI.create(URL), options); // the main namespace
            socket.connect();
            addSocketEventListeners(socket);


        } catch (IOException e) {
            e.printStackTrace();
        }


    }

    private void addSocketEventListeners(Socket socket) {

        //requestRefresh - CLIENT SENDS TO SERVER to notify the server that the clients wants a full clip refresh for their client
        socket.emit("requestRefresh");
        socket.on("recieveNewClip", new Emitter.Listener() {
            @Override
            public void call(Object... args) {
                System.out.println(" COPY GOT A TEXT GOTA TEXT!!!!!!!");
                String text = (String) args[0];
                //ADD CLIPBOARD ITEM TO MESSAGES RECYCLERVIEW
            }
        });
        socket.on("refresh", new Emitter.Listener() {
            @Override
            public void call(Object... args) {
                System.out.println("THIS IS THE REFRESH FUNCTION");
                System.out.println(args[0]);
//                    String[] s = (String[]) args[0];
//                    System.out.println(" COPY GOT A TEXT GOTA TEXT!!!!!!!");
//                    String text = (String) args[0];
//
            }
        });
    }

    private IO.Options createOptions() {
        return IO.Options.builder()
                // IO factory options
                .setForceNew(false)
                .setMultiplex(true)

                // low-level engine options
                .setTransports(new String[]{Polling.NAME, WebSocket.NAME})
                .setUpgrade(true)
                .setRememberUpgrade(false)
//                .setPath("/socket.io/")
                .setQuery(null)
                .setExtraHeaders(singletonMap("cookie", singletonList(cookieString)))

                // Manager options
                .setReconnection(true)
                .setReconnectionAttempts(Integer.MAX_VALUE)
                .setReconnectionDelay(1_000)
                .setReconnectionDelayMax(5_000)
                .setRandomizationFactor(0.5)
                .setTimeout(20_000)


                // Socket options
                .setAuth(null)
                .build();
    }


    public void uploadText(String text) {
        System.out.println("uploading text");
        socket.emit("sendNewClip", text);
        //clipSaveStatus - SERVER SENDS TO CLIENT to notify the client that the clip was saved successfully or failed
//        socket.on("clipSaveStatus", new Emitter.Listener() {
//            @Override
//            public void call(Object... args) {
//                System.out.println("THIS IS THE REFRESH FUNCTION");
//                System.out.println(args[0]);
//            }
//        });
    }


    public void testRequest(){
        RequestQueue queue = Volley.newRequestQueue(context);
        String url ="https://www.google.com";

// Request a string response from the provided URL.
        StringRequest stringRequest = new StringRequest(com.android.volley.Request.Method.GET, url,
                new com.android.volley.Response.Listener<String>(){
                    @Override
                    public void onResponse(String response) {
                        // Display the first 500 characters of the response string.
                        System.out.println(response.substring(0,500));
                    }
                }, new com.android.volley.Response.ErrorListener(){
            @Override
            public void onErrorResponse(VolleyError error) {
                System.out.println("That didn't work!");
            }
        });

// Add the request to the RequestQueue.
        queue.add(stringRequest);


    }

    public int logIn(String username, String password) throws JsonProcessingException, JSONException {
        final int[] responseCode = {0};
        System.out.println("logging in");
        String email = password + "@lol.com";
        HashMap<String, String> map = new HashMap<>();
//        map.put("username", username);
//        map.put("email", email);
//        map.put("password", password);
//
//        ObjectMapper objectMapper = new ObjectMapper();
//        String requestBody1 = objectMapper
//                .writerWithDefaultPrettyPrinter()
//                .writeValueAsString(map);

//        cm = new CookieManager();
//        cm.setCookiePolicy(CookiePolicy.ACCEPT_ALL);

        System.out.println("making request");


//        JSON = MediaType.get("application/json; charset=utf-8");

//        client = new OkHttpClient();

//        JSONObject jsonBody = new JSONObject();
//        jsonBody.put("Title", "Android Volley Demo");
//        jsonBody.put("Author", "BNK");

//        jsonBody.put("username", username);
//        jsonBody.put("email", email);
//        jsonBody.put("password", password);
//        final String requestBody = jsonBody.toString();

        // Instantiate the RequestQueue.
        RequestQueue queue = Volley.newRequestQueue(context);
        //        String url ="https://www.google.com";

        // Request a string response from the provided URL.
        StringRequest stringRequest = new StringRequest(com.android.volley.Request.Method.POST, endpoints[0], new com.android.volley.Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        // Display the first 500 characters of the response string.
//                        textView.setText("Response is: " + response.substring(0, 500));
                        responseCode[0] = Integer.parseInt(response.substring(0, 500));
                    }
                },
                new com.android.volley.Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                System.out.println(error.toString());
                System.out.println("Error");

            }
        }
        )

//{
//            @Override
//            protected Map<String, String> getParams() {
//                // below line we are creating a map for
//                // storing our values in key and value pair.
//                Map<String, String> params = new HashMap<String, String>();
//
//                // on below line we are passing our key
//                // and value pair to our parameters.
//                params.put("username", username);
//                params.put("email", email);
//                params.put("password", password);
////                params.put("name", name);
////                params.put("job", job);
//
//                // at last we are
//                // returning our params.
//                return params;
//            }
//        };
        {


            @Override
            public byte[] getBody() {

                JSONObject jsonObject = new JSONObject();
                String body = null;
                try {
//                    jsonObject.put("username", "user123");
//                    jsonObject.put("password", "Pass123");
                    jsonObject.put("username", username);
                    jsonObject.put("email", email);
                    jsonObject.put("password", password);
                    body = jsonObject.toString();
                } catch (JSONException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }

                try {
                    return body.toString().getBytes("utf-8");
                } catch (UnsupportedEncodingException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }
                return null;
            }

            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                Map<String, String>  params = new HashMap<String, String>();
                params.put("Content-Type", "application/json");
                return params;
            }

        };


        int MY_SOCKET_TIMEOUT_MS=20000;

        stringRequest.setRetryPolicy(new DefaultRetryPolicy(
                MY_SOCKET_TIMEOUT_MS,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));


        Log.i("VOL", "SENDING REQ");
        // Add the request to the RequestQueue.
        queue.add(stringRequest);


//        try {
//            String responseBody = post("http://localhost:3000/signup", requestBody);
//            System.out.println(responseBody);
//        } catch (IOException e) {
//            e.printStackTrace();
//        }


//        cm.getCookieStore().getCookies().forEach(System.out::println);
//        cookieString = cm.getCookieStore().getCookies().get(0).toString();
//        System.out.println(cookieString);
//
//        System.out.println("trying to connect to server");
//        connectToServer();
        return responseCode[0];
    }

//    public int signUp(String username, String password) throws JsonProcessingException {
//        final int[] responseCode = {0};
//        System.out.println("signing up");
//        String email = password + "@lol.com";
//        HashMap<String, String> map = new HashMap<>();
//        map.put("username", username);
//        map.put("email", email);
//        map.put("password", password);
////        http://localhost:3000/signup&username=kevin12&email=kevinemail@email.com&password=nib
//
//
//        ObjectMapper objectMapper = new ObjectMapper();
//        String requestBody = objectMapper
//                .writerWithDefaultPrettyPrinter()
//                .writeValueAsString(map);
//
//        cm = new CookieManager();
//        cm.setCookiePolicy(CookiePolicy.ACCEPT_ALL);
//
//        System.out.println("making request");
//        System.out.println("making request");
//
//
//        JSON = MediaType.get("application/json; charset=utf-8");
//
//        client = new OkHttpClient();
//
//        try {
//            String responseBody = post("http://localhost:3000/signup", requestBody);
//            System.out.println(responseBody);
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
//
////httpClient.sendAsync(request, HttpResponse.BodyHandlers.ofString())
////                    .thenApply(HttpResponse::statusCode)
////                    .thenAccept(System.out::println);
//
//
//        cm.getCookieStore().getCookies().forEach(System.out::println);
//        cookieString = cm.getCookieStore().getCookies().get(0).toString();
//        System.out.println(cookieString);
//
////            System.out.println("Here are the cookies: ");
////            System.out.println(cm.getCookieStore().getCookies().toString());
////            System.out.println(            cm.getCookieStore().getCookies().size());
//
//
////        CompletableFuture.runAsync(()->{
////            System.out.println(CookieHandler.getDefault().g;
////        });
////        CompletableFuture.supplyAsync(()->         CookieHandler.getDefault().toString());
//        System.out.println("trying to connect to server");
//        connectToServer();
//        return responseCode[0];
//    }

    public void disconnectFromServer() {

    }

    public void uploadClipboardHistory(String history) {

    }

//    String post(String url, String json) throws IOException {
//        RequestBody body = RequestBody.create(JSON, json);
//        Request request = new Request.Builder()
//                .url(url)
//                .post(body)
//                .build();
//        try (Response response = client.newCall(request).execute()) {
//            return response.body().string();
//        }
//    }


    @Override
    public AssetManager getAssets() {
        return null;
    }

    @Override
    public Resources getResources() {
        return null;
    }

    @Override
    public PackageManager getPackageManager() {
        return null;
    }

    @Override
    public ContentResolver getContentResolver() {
        return null;
    }

    @Override
    public Looper getMainLooper() {
        return null;
    }

    @Override
    public Context getApplicationContext() {
        return null;
    }

    @Override
    public void setTheme(int resid) {

    }

    @Override
    public Resources.Theme getTheme() {
        return null;
    }

    @Override
    public ClassLoader getClassLoader() {
        return null;
    }

    @Override
    public String getPackageName() {
        return null;
    }

    @Override
    public ApplicationInfo getApplicationInfo() {
        return null;
    }

    @Override
    public String getPackageResourcePath() {
        return null;
    }

    @Override
    public String getPackageCodePath() {
        return null;
    }

    @Override
    public SharedPreferences getSharedPreferences(String name, int mode) {
        return null;
    }

    @Override
    public boolean moveSharedPreferencesFrom(Context sourceContext, String name) {
        return false;
    }

    @Override
    public boolean deleteSharedPreferences(String name) {
        return false;
    }

    @Override
    public FileInputStream openFileInput(String name) throws FileNotFoundException {
        return null;
    }

    @Override
    public FileOutputStream openFileOutput(String name, int mode) throws FileNotFoundException {
        return null;
    }

    @Override
    public boolean deleteFile(String name) {
        return false;
    }

    @Override
    public File getFileStreamPath(String name) {
        return null;
    }

    @Override
    public File getDataDir() {
        return null;
    }

    @Override
    public File getFilesDir() {
        return null;
    }

    @Override
    public File getNoBackupFilesDir() {
        return null;
    }

    @Nullable
    @Override
    public File getExternalFilesDir(@Nullable String type) {
        return null;
    }

    @Override
    public File[] getExternalFilesDirs(String type) {
        return new File[0];
    }

    @Override
    public File getObbDir() {
        return null;
    }

    @Override
    public File[] getObbDirs() {
        return new File[0];
    }

    @Override
    public File getCacheDir() {
        return null;
    }

    @Override
    public File getCodeCacheDir() {
        return null;
    }

    @Nullable
    @Override
    public File getExternalCacheDir() {
        return null;
    }

    @Override
    public File[] getExternalCacheDirs() {
        return new File[0];
    }

    @Override
    public File[] getExternalMediaDirs() {
        return new File[0];
    }

    @Override
    public String[] fileList() {
        return new String[0];
    }

    @Override
    public File getDir(String name, int mode) {
        return null;
    }

    @Override
    public SQLiteDatabase openOrCreateDatabase(String name, int mode, SQLiteDatabase.CursorFactory factory) {
        return null;
    }

    @Override
    public SQLiteDatabase openOrCreateDatabase(String name, int mode, SQLiteDatabase.CursorFactory factory, @Nullable DatabaseErrorHandler errorHandler) {
        return null;
    }

    @Override
    public boolean moveDatabaseFrom(Context sourceContext, String name) {
        return false;
    }

    @Override
    public boolean deleteDatabase(String name) {
        return false;
    }

    @Override
    public File getDatabasePath(String name) {
        return null;
    }

    @Override
    public String[] databaseList() {
        return new String[0];
    }

    @Override
    public Drawable getWallpaper() {
        return null;
    }

    @Override
    public Drawable peekWallpaper() {
        return null;
    }

    @Override
    public int getWallpaperDesiredMinimumWidth() {
        return 0;
    }

    @Override
    public int getWallpaperDesiredMinimumHeight() {
        return 0;
    }

    @Override
    public void setWallpaper(Bitmap bitmap) throws IOException {

    }

    @Override
    public void setWallpaper(InputStream data) throws IOException {

    }

    @Override
    public void clearWallpaper() throws IOException {

    }

    @Override
    public void startActivity(Intent intent) {

    }

    @Override
    public void startActivity(Intent intent, @Nullable Bundle options) {

    }

    @Override
    public void startActivities(Intent[] intents) {

    }

    @Override
    public void startActivities(Intent[] intents, Bundle options) {

    }

    @Override
    public void startIntentSender(IntentSender intent, @Nullable Intent fillInIntent, int flagsMask, int flagsValues, int extraFlags) throws IntentSender.SendIntentException {

    }

    @Override
    public void startIntentSender(IntentSender intent, @Nullable Intent fillInIntent, int flagsMask, int flagsValues, int extraFlags, @Nullable Bundle options) throws IntentSender.SendIntentException {

    }

    @Override
    public void sendBroadcast(Intent intent) {

    }

    @Override
    public void sendBroadcast(Intent intent, @Nullable String receiverPermission) {

    }

    @Override
    public void sendOrderedBroadcast(Intent intent, @Nullable String receiverPermission) {

    }

    @Override
    public void sendOrderedBroadcast(@NonNull Intent intent, @Nullable String receiverPermission, @Nullable BroadcastReceiver resultReceiver, @Nullable Handler scheduler, int initialCode, @Nullable String initialData, @Nullable Bundle initialExtras) {

    }

    @Override
    public void sendBroadcastAsUser(Intent intent, UserHandle user) {

    }

    @Override
    public void sendBroadcastAsUser(Intent intent, UserHandle user, @Nullable String receiverPermission) {

    }

    @Override
    public void sendOrderedBroadcastAsUser(Intent intent, UserHandle user, @Nullable String receiverPermission, BroadcastReceiver resultReceiver, @Nullable Handler scheduler, int initialCode, @Nullable String initialData, @Nullable Bundle initialExtras) {

    }

    @Override
    public void sendStickyBroadcast(Intent intent) {

    }

    @Override
    public void sendStickyOrderedBroadcast(Intent intent, BroadcastReceiver resultReceiver, @Nullable Handler scheduler, int initialCode, @Nullable String initialData, @Nullable Bundle initialExtras) {

    }

    @Override
    public void removeStickyBroadcast(Intent intent) {

    }

    @Override
    public void sendStickyBroadcastAsUser(Intent intent, UserHandle user) {

    }

    @Override
    public void sendStickyOrderedBroadcastAsUser(Intent intent, UserHandle user, BroadcastReceiver resultReceiver, @Nullable Handler scheduler, int initialCode, @Nullable String initialData, @Nullable Bundle initialExtras) {

    }

    @Override
    public void removeStickyBroadcastAsUser(Intent intent, UserHandle user) {

    }

    @Nullable
    @Override
    public Intent registerReceiver(@Nullable BroadcastReceiver receiver, IntentFilter filter) {
        return null;
    }

    @Nullable
    @Override
    public Intent registerReceiver(@Nullable BroadcastReceiver receiver, IntentFilter filter, int flags) {
        return null;
    }

    @Nullable
    @Override
    public Intent registerReceiver(BroadcastReceiver receiver, IntentFilter filter, @Nullable String broadcastPermission, @Nullable Handler scheduler) {
        return null;
    }

    @Nullable
    @Override
    public Intent registerReceiver(BroadcastReceiver receiver, IntentFilter filter, @Nullable String broadcastPermission, @Nullable Handler scheduler, int flags) {
        return null;
    }

    @Override
    public void unregisterReceiver(BroadcastReceiver receiver) {

    }

    @Nullable
    @Override
    public ComponentName startService(Intent service) {
        return null;
    }

    @Nullable
    @Override
    public ComponentName startForegroundService(Intent service) {
        return null;
    }

    @Override
    public boolean stopService(Intent service) {
        return false;
    }

    @Override
    public boolean bindService(Intent service, @NonNull ServiceConnection conn, int flags) {
        return false;
    }

    @Override
    public void unbindService(@NonNull ServiceConnection conn) {

    }

    @Override
    public boolean startInstrumentation(@NonNull ComponentName className, @Nullable String profileFile, @Nullable Bundle arguments) {
        return false;
    }

    @Override
    public Object getSystemService(@NonNull String name) {
        return null;
    }

    @Nullable
    @Override
    public String getSystemServiceName(@NonNull Class<?> serviceClass) {
        return null;
    }

    @Override
    public int checkPermission(@NonNull String permission, int pid, int uid) {
        return 0;
    }

    @Override
    public int checkCallingPermission(@NonNull String permission) {
        return 0;
    }

    @Override
    public int checkCallingOrSelfPermission(@NonNull String permission) {
        return 0;
    }

    @Override
    public int checkSelfPermission(@NonNull String permission) {
        return 0;
    }

    @Override
    public void enforcePermission(@NonNull String permission, int pid, int uid, @Nullable String message) {

    }

    @Override
    public void enforceCallingPermission(@NonNull String permission, @Nullable String message) {

    }

    @Override
    public void enforceCallingOrSelfPermission(@NonNull String permission, @Nullable String message) {

    }

    @Override
    public void grantUriPermission(String toPackage, Uri uri, int modeFlags) {

    }

    @Override
    public void revokeUriPermission(Uri uri, int modeFlags) {

    }

    @Override
    public void revokeUriPermission(String toPackage, Uri uri, int modeFlags) {

    }

    @Override
    public int checkUriPermission(Uri uri, int pid, int uid, int modeFlags) {
        return 0;
    }

    @Override
    public int checkCallingUriPermission(Uri uri, int modeFlags) {
        return 0;
    }

    @Override
    public int checkCallingOrSelfUriPermission(Uri uri, int modeFlags) {
        return 0;
    }

    @SuppressLint("WrongConstant")
    @Override
    public int checkUriPermission(@Nullable Uri uri, @Nullable String readPermission, @Nullable String writePermission, int pid, int uid, int modeFlags) {
        return 0;
    }

    @Override
    public void enforceUriPermission(Uri uri, int pid, int uid, int modeFlags, String message) {

    }

    @Override
    public void enforceCallingUriPermission(Uri uri, int modeFlags, String message) {

    }

    @Override
    public void enforceCallingOrSelfUriPermission(Uri uri, int modeFlags, String message) {

    }

    @Override
    public void enforceUriPermission(@Nullable Uri uri, @Nullable String readPermission, @Nullable String writePermission, int pid, int uid, int modeFlags, @Nullable String message) {

    }

    @Override
    public Context createPackageContext(String packageName, int flags) throws PackageManager.NameNotFoundException {
        return null;
    }

    @Override
    public Context createContextForSplit(String splitName) throws PackageManager.NameNotFoundException {
        return null;
    }

    @Override
    public Context createConfigurationContext(@NonNull Configuration overrideConfiguration) {
        return null;
    }

    @Override
    public Context createDisplayContext(@NonNull Display display) {
        return null;
    }

    @Override
    public Context createDeviceProtectedStorageContext() {
        return null;
    }

    @Override
    public boolean isDeviceProtectedStorage() {
        return false;
    }
}
