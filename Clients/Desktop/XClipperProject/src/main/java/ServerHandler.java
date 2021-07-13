import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.socket.client.IO;
import io.socket.client.Socket;
import io.socket.engineio.client.transports.Polling;
import io.socket.engineio.client.transports.WebSocket;

import javax.swing.*;
import javax.swing.border.MatteBorder;
import java.awt.*;
import java.awt.datatransfer.Clipboard;
import java.awt.event.ActionEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.IOException;
import java.net.*;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;
import java.util.HashMap;
import java.util.concurrent.CompletableFuture;

import static io.socket.client.IO.socket;
import static java.util.Collections.singletonList;
import static java.util.Collections.singletonMap;

public class ServerHandler {
    String cookieString = "";
    ClientManager clientManager;
    Clipboard clipboard = Toolkit.getDefaultToolkit().getSystemClipboard();
    DefaultListModel defaultListModel;
    private volatile boolean running = true;
    private Frame frame;
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

//    java.net.CookieHandler.setDefault();


    public ServerHandler() {

        this.mainList = mainList;
        this.frame = frame;
        this.themeColor = themeColor;


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
        options = IO.Options.builder()
                // IO factory options
                .setForceNew(false)
                .setMultiplex(true)

                // low-level engine options
                .setTransports(new String[] { Polling.NAME, WebSocket.NAME })
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

        try {
            System.out.println("inside the try block");
            InetAddress addr = InetAddress.getByName(hostname);

//            IO.socket(URI.create(endpoints[0]), options);
            socket = IO.socket(URI.create("http://localhost:3000"), options); // the main namespace
            socket.connect();

//            socket.on("recieveNewClip", new Emitter.Listener() {
//                @Override
//                public void call(Object... args) {
//                    String text = (String) args[0];
//                    JPanel panel = createPanel();
//                    JTextArea textArea = createTextArea(text);
//                    GridBagConstraints gbc = createGridBagConstraints();
//                    JTextArea textArea = createTextArea(data);
//                    final JPopupMenu popup = createPopupMenu();
//                    JButton button = createButton(popup);
//                    panel.add(textArea, BorderLayout.CENTER);
//                    panel.add(button, BorderLayout.EAST);
//                    mainList.add(panel, gbc, 0);
//                    frame.validate();
//                    frame.repaint();
//                }
//            });

        } catch (IOException e) {
            e.printStackTrace();
        }



    }



    public void uploadText(String text){
        System.out.println("uploading text");
        socket.emit("sendNewClip", text);
    }
    private JPopupMenu createPopupMenu() {
        //Create the popup menu.
        final JPopupMenu popup = new JPopupMenu();
        popup.setBackground(GUI.themeColor);
        JMenuItem deleteItem = new JMenuItem(new AbstractAction("Delete") {
            @Override
            public void actionPerformed(ActionEvent e) {
                JOptionPane.showMessageDialog(GUI.frame, "Delete selected");

            }
        });
        deleteItem.setBackground(GUI.themeColor);
        deleteItem.setForeground(Color.white);
        popup.add(deleteItem);
        JMenuItem pinItem = new JMenuItem(new AbstractAction("Pin") {
            @Override
            public void actionPerformed(ActionEvent e) {
                JOptionPane.showMessageDialog(GUI.frame, "Pin selected");

            }
        });
        pinItem.setBackground(themeColor);
        pinItem.setForeground(Color.white);
        popup.add(pinItem);

        return popup;
    }

    private GridBagConstraints createGridBagConstraints() {
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridwidth = GridBagConstraints.REMAINDER;
        gbc.weightx = 1;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(3, 3, 3, 3);
        return gbc;
    }


    private JButton createButton(JPopupMenu popup) {

        JButton button = new JButton("...");
        button.setOpaque(true);
        button.setBackground(new Color(55, 62, 65));
        button.setForeground(Color.white);
        button.addMouseListener(new MouseAdapter() {
            public void mousePressed(MouseEvent e) {
                popup.show(e.getComponent(), e.getX(), e.getY());
            }
        });


        return button;
    }


    private JTextArea createTextArea(String data) {
        JTextArea textArea = new JTextArea(data);
        textArea.setBackground(GUI.themeColor);
        textArea.setForeground(Color.white);
        textArea.setLineWrap(true);
        textArea.setEditable(false);
        return textArea;
    }
     private JPanel createPanel() {
        JPanel panel = new JPanel();
        panel.setLayout(new BorderLayout());
        panel.setPreferredSize(new Dimension(200, 50));
        panel.setBorder(new MatteBorder(0, 0, 1, 0, GUI.themeColor));
        panel.setBackground(GUI.themeColor);
        panel.setForeground(Color.white);
        return panel;
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


        CompletableFuture.runAsync(() -> {
            CookieManager cm = new CookieManager();
            HttpRequest request = HttpRequest.newBuilder(URI.create(endpoints[0]))
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
//                    .headers("Content-Type", "application/json", "")

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

//            httpClient.sendAsync(request, HttpResponse.BodyHandlers.ofString())
//                    .thenApply(HttpResponse::statusCode)
//                    .thenAccept(System.out::println);


        System.out.println("trying to get response");
        HttpResponse<String> response = null;
        try {
            response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
            System.out.println(response.statusCode());
            System.out.println(response.body());
            if(response != null){
                responseCode[0] = response.statusCode();
            }
        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
        }


//httpClient.sendAsync(request, HttpResponse.BodyHandlers.ofString())
//                    .thenApply(HttpResponse::statusCode)
//                    .thenAccept(System.out::println);




        cm.getCookieStore().getCookies().forEach(System.out::println);
        cookieString = cm.getCookieStore().getCookies().get(0).toString();
        System.out.println(cookieString);

//            System.out.println("Here are the cookies: ");
//            System.out.println(cm.getCookieStore().getCookies().toString());
//            System.out.println(            cm.getCookieStore().getCookies().size());



//        CompletableFuture.runAsync(()->{
//            System.out.println(CookieHandler.getDefault().g;
//        });
//        CompletableFuture.supplyAsync(()->         CookieHandler.getDefault().toString());
        System.out.println("trying to connect to server");
        connectToServer();
        return responseCode[0];
    }

    public void disconnectFromServer() {

    }

    public void uploadClipboardHistory(String history) {

    }


}
