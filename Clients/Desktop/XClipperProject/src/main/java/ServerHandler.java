import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.IOException;
import java.net.*;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;
import java.util.HashMap;
import java.util.concurrent.CompletableFuture;

public class ServerHandler {

    ServerSocket serverSocket;
    Socket socket;


    public ServerHandler() {

        String hostname = "localhost";
        int port = 9000;

        try {
            InetAddress addr = InetAddress.getByName(hostname);
            socket = new Socket(addr, port);
        } catch (IOException e) {
            e.printStackTrace();
        }


//        try {
//            System.out.println("Connecting to server ");
//            serverSocket = new ServerSocket(9000, 0, InetAddress.getLoopbackAddress());
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
    }

    public void connectToServer() {

    }


    public void logIn(String username, String password) throws JsonProcessingException {
        System.out.println("logging in");
//        String email = password + "@lol.com";
        HashMap<String, String> map = new HashMap<>();
        map.put("username", username);
        map.put("password", password);

        ObjectMapper objectMapper = new ObjectMapper();
        String requestBody = objectMapper
                .writerWithDefaultPrettyPrinter()
                .writeValueAsString(map);

        CookieManager cm = new CookieManager();

        CompletableFuture.runAsync(() -> {
            HttpRequest request = HttpRequest.newBuilder(URI.create("http://localhost:9000/login"))
                    .header("Content-Type", "application/json")
                    .POST(HttpRequest.BodyPublishers.ofString(requestBody))
                    .build();

            HttpClient httpClient = HttpClient.newBuilder()
                    .version(HttpClient.Version.HTTP_1_1)
                    .followRedirects(HttpClient.Redirect.NORMAL)
                    .connectTimeout(Duration.ofSeconds(20))
                    .cookieHandler(cm)
//                .authenticator(Authenticator.getDefault())
                    .build();

            httpClient.sendAsync(request, HttpResponse.BodyHandlers.ofString())
                    .thenApply(HttpResponse::statusCode)
                    .thenAccept(System.out::println);

        });

//        CompletableFuture.runAsync(() -> {
//             get Cookies
//            System.out.println("Here are the cookies: ");
//            System.out.println(cm.getCookieStore().getCookies().toString());
//        });

    }

    public void signUp(String username, String password) throws JsonProcessingException {
        System.out.println("signing up");
        String email = password + "@lol.com";
        HashMap<String, String> map = new HashMap<>();
        map.put("username", username);
        map.put("email", email);
        map.put("password", password);

        ObjectMapper objectMapper = new ObjectMapper();
        String requestBody = objectMapper
                .writerWithDefaultPrettyPrinter()
                .writeValueAsString(map);

        CookieManager cm = new CookieManager();

        CompletableFuture.runAsync(() -> {
            HttpRequest request = HttpRequest.newBuilder(URI.create("http://localhost:9000/signup"))
                    .header("Content-Type", "application/json")
                    .POST(HttpRequest.BodyPublishers.ofString(requestBody))
                    .build();

            HttpClient httpClient = HttpClient.newBuilder()
                    .version(HttpClient.Version.HTTP_1_1)
                    .followRedirects(HttpClient.Redirect.NORMAL)
                    .connectTimeout(Duration.ofSeconds(20))
                    .cookieHandler(cm)
//                .authenticator(Authenticator.getDefault())
                    .build();

            httpClient.sendAsync(request, HttpResponse.BodyHandlers.ofString())
                    .thenApply(HttpResponse::statusCode)
                    .thenAccept(System.out::println);

        });

//        CompletableFuture.runAsync(() -> {
//             get Cookies
//
//            System.out.println("Here are the cookies: ");
//            System.out.println(cm.getCookieStore().getCookies().toString());
//        });


    }

    public void disconnectFromServer() {

    }

    public void uploadClipboardHistory(String history) {

    }


}
