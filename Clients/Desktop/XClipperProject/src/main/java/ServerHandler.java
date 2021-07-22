import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.socket.client.IO;
import io.socket.client.Socket;
import io.socket.emitter.Emitter;
import io.socket.engineio.client.transports.Polling;
import io.socket.engineio.client.transports.WebSocket;

import javax.swing.*;
import java.awt.*;
import java.awt.datatransfer.Clipboard;
import java.io.IOException;
import java.net.*;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;
import java.util.HashMap;

import static java.util.Collections.singletonList;
import static java.util.Collections.singletonMap;


/*
refresh - SERVER SENDS TO CLIENT every 10 seconds. Gets all the clips that exists for the user and sends it to the client
sendNewClip - CLIENT SENDS TO SERVER when a new clip is added.
recieveNewClip - SERVER SENDS TO CLIENT when a new clip is added. (sends to other clients to notify that a new clip is sent)
clipSaveStatus - SERVER SENDS TO CLIENT to notify the client that the clip was saved successfully or failed
requestRefresh - CLIENT SENDS TO SERVER to notify the server that the clients wants a full clip refresh for their client

 */
public class ServerHandler {
    String cookieString = "";
    ClientManager clientManager;
    Clipboard clipboard = Toolkit.getDefaultToolkit().getSystemClipboard();
    DefaultListModel defaultListModel;
    JPanel mainList;
    Color themeColor;
    IO.Options options;
    ServerSocket serverSocket;
    Socket socket;
    String URL = "http://localhost:3000";
    String[] endpoints = {
            "http://localhost:3000/login",
            "http://localhost:3000/signup"
    };
    CookieManager cm;
    private volatile boolean running = true;
    private Frame frame;


    public ServerHandler() {

        this.mainList = mainList;
        this.frame = frame;
        this.themeColor = themeColor;


        CookieHandler.setDefault(new CookieManager());


    }

    public void connectToServer() {
        String hostname = "localhost";
        int port = 3000;

        System.out.println("creating options object");
        options = createOptions();

        try {
            System.out.println("inside the try block");
            InetAddress addr = InetAddress.getByName(hostname);

            socket = IO.socket(URI.create(URL), options);
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
                JPanel panel = ComponentHelper.createPanel();
                JTextArea textArea = ComponentHelper.createTextArea(text);
                GridBagConstraints gbc = ComponentHelper.createGridBagConstraints();
                final JPopupMenu popup = ComponentHelper.createPopupMenu();
                JButton button = ComponentHelper.createButton(popup);
                panel.add(textArea, BorderLayout.CENTER);
                panel.add(button, BorderLayout.EAST);
                ComponentHelper.addTitledBorder(panel, GUI.borderWhite, "Clip", Color.GREEN);
                GUI.mainList.add(panel, gbc, 0);

                GUI.frame.validate();
                GUI.frame.repaint();
            }
        });
        socket.on("refresh", new Emitter.Listener() {
            @Override
            public void call(Object... args) {
                System.out.println("THIS IS THE REFRESH FUNCTION");
                System.out.println(args[0]);
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
    }


    public int logIn(String username, String password) throws JsonProcessingException {
        final int[] responseCode = {0};
        System.out.println("logging in");
        String email = password + "@lol.com";
        HashMap<String, String> map = new HashMap<>();
        map.put("username", username);
        map.put("email", email);
        map.put("password", password);

        ObjectMapper objectMapper = new ObjectMapper();
        String requestBody = objectMapper
                .writerWithDefaultPrettyPrinter()
                .writeValueAsString(map);

        cm = new CookieManager();
        cm.setCookiePolicy(CookiePolicy.ACCEPT_ALL);

        System.out.println("making request");
        HttpRequest request = HttpRequest.newBuilder(URI.create(endpoints[0]))
                .header("Content-Type", "application/json")

                .POST(HttpRequest.BodyPublishers.ofString(requestBody))
                .build();

        System.out.println("making client");
        HttpClient httpClient = HttpClient.newBuilder()
                .version(HttpClient.Version.HTTP_1_1)
                .followRedirects(HttpClient.Redirect.NORMAL)
                .connectTimeout(Duration.ofSeconds(20))
                .cookieHandler(cm)
//                .authenticator(Authenticator.getDefault())
                .build();


        System.out.println("trying to get response");
        HttpResponse<String> response = null;
        try {
            response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
            System.out.println(response.statusCode());
            System.out.println(response.body());
            if (response != null) {
                responseCode[0] = response.statusCode();
            }
        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
        }


        cm.getCookieStore().getCookies().forEach(System.out::println);
        cookieString = cm.getCookieStore().getCookies().get(0).toString();
        System.out.println(cookieString);


        System.out.println("trying to connect to server");
        connectToServer();
        return responseCode[0];
    }

    public int signUp(String username, String password) throws JsonProcessingException {
        final int[] responseCode = {0};
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

        cm = new CookieManager();
        cm.setCookiePolicy(CookiePolicy.ACCEPT_ALL);

        System.out.println("making request");
        HttpRequest request = HttpRequest.newBuilder(URI.create(endpoints[1]))
                .header("Content-Type", "application/json")

                .POST(HttpRequest.BodyPublishers.ofString(requestBody))
                .build();

        System.out.println("making client");
        HttpClient httpClient = HttpClient.newBuilder()
                .version(HttpClient.Version.HTTP_1_1)
                .followRedirects(HttpClient.Redirect.NORMAL)
                .connectTimeout(Duration.ofSeconds(20))
                .cookieHandler(cm)
//                .authenticator(Authenticator.getDefault())
                .build();


        System.out.println("trying to get response");
        HttpResponse<String> response = null;
        try {

            response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
            System.out.println(response.statusCode());
            System.out.println(response.body());
            if (response != null) {
                responseCode[0] = response.statusCode();
            }
        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
        }


        cm.getCookieStore().getCookies().forEach(System.out::println);
        cookieString = cm.getCookieStore().getCookies().get(0).toString();
        System.out.println(cookieString);


        System.out.println("trying to connect to server");
        connectToServer();
        return responseCode[0];
    }

    public void disconnectFromServer() {

    }

    public void uploadClipboardHistory(String history) {

    }


}
