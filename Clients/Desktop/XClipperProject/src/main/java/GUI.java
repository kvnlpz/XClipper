import com.fasterxml.jackson.core.JsonProcessingException;

import javax.swing.*;
import java.awt.*;
import java.awt.datatransfer.FlavorEvent;
import java.awt.datatransfer.FlavorListener;
import java.awt.event.*;
import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.security.NoSuchAlgorithmException;
import java.util.prefs.Preferences;

public class GUI {
    static JList centerPanel;
    static DefaultListModel defaultListModel;

    public static void main(String[] args) throws UnsupportedLookAndFeelException, ClassNotFoundException, InstantiationException, IllegalAccessException, NoSuchAlgorithmException {
        int width = 600, height = 400;
        UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        Preferences prefs = Preferences.userNodeForPackage(GUI.class);
        final String PREF_NAME = "open_minimized";
        String defaultValue = "false";
        String propertyValue = prefs.get(PREF_NAME, defaultValue);
        String programText = "";
        if (propertyValue.equals("false")) {
            programText = "the program is set to open up normally";

        } else {
            programText = "The program is set to open up minimized";
        }

        checkFirstTime();








        System.out.println();
        System.out.println("Your operating system is: " + System.getProperty("os.name"));

        ClipboardTextListener clipboardTextListener = new ClipboardTextListener();
        Thread thread = new Thread(clipboardTextListener);
//        ServerHandler serverHandler = new ServerHandler();



        JFrame frame = new JFrame("XClipper");
        frame.setBackground(new Color(55, 62, 65));
        frame.setLayout(new BorderLayout());


        JPanel northPanel = new JPanel(new FlowLayout());
        northPanel.setBackground(new Color(55, 62, 65));
        northPanel.setPreferredSize(new Dimension(400, 100));


        JTextField usernameField = new JTextField("\t\t");
        JTextField passwordField = new JTextField("\t\t");

        usernameField.addFocusListener(new FocusListener() {
            @Override
            public void focusGained(FocusEvent e) {
                usernameField.setText("");
                usernameField.setForeground(new Color(50, 50, 50));
            }

            @Override
            public void focusLost(FocusEvent e) {

                if (usernameField.getText().length() == 0) {
                    usernameField.setText("Username");
                    usernameField.setForeground(new Color(150, 150, 150));
                }

            }
        });
        passwordField.addFocusListener(new FocusListener() {
            @Override
            public void focusGained(FocusEvent e) {
                passwordField.setText("");
                passwordField.setForeground(new Color(50, 50, 50));
            }

            @Override
            public void focusLost(FocusEvent e) {

                if (passwordField.getText().length() == 0) {
                    passwordField.setText("Password");
                    passwordField.setForeground(new Color(150, 150, 150));
                }

            }
        });

        JButton signUpButton = new JButton("Sign Up");
//        signUpButton.addActionListener(new ActionListener() {
//            @Override
//            public void actionPerformed(ActionEvent e) {
//                try {
//                    serverHandler.signUp(usernameField.getText(), passwordField.getText());
//                } catch (JsonProcessingException jsonProcessingException) {
//                    jsonProcessingException.printStackTrace();
//                }
//            }
//        });

        JButton loginButton = new JButton("Log In");
//        loginButton.addActionListener(new ActionListener() {
//            @Override
//            public void actionPerformed(ActionEvent e) {
//                try {
//                    serverHandler.logIn(usernameField.getText(), passwordField.getText());
//                } catch (JsonProcessingException jsonProcessingException) {
//                    jsonProcessingException.printStackTrace();
//                }
//            }
//        });

        northPanel.add(usernameField);
        northPanel.add(passwordField);
        northPanel.add(signUpButton);
        northPanel.add(loginButton);


        defaultListModel = new DefaultListModel();
        centerPanel = new JList(defaultListModel);
        centerPanel.setForeground(Color.white);
        centerPanel.setSelectionMode(ListSelectionModel.SINGLE_INTERVAL_SELECTION);
        centerPanel.setLayoutOrientation(JList.HORIZONTAL_WRAP);
        centerPanel.setVisibleRowCount(-1);
        centerPanel.setBackground(new Color(55, 62, 65));


        JScrollPane centerPanelScrollPane = new JScrollPane(centerPanel);
        centerPanelScrollPane.setPreferredSize(new Dimension(300, 80));


        frame.setResizable(false);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.add(northPanel, BorderLayout.NORTH);
        frame.add(centerPanelScrollPane, BorderLayout.CENTER);
        frame.setPreferredSize(new Dimension(400, 400));
        frame.setSize(400, 400);
        frame.setVisible(true);
        thread.start();

    }

    private static void checkFirstTime() {
        //we check if it's the first time running the program by checking if a file exists in the path
        Path currentRelativePath = Paths.get("");
        String s = currentRelativePath.toAbsolutePath().toString();
        System.out.println("Current absolute path is: " + s);

        String path = s + File.separator + "run.txt";
        // Use relative path for Unix systems
        File f = new File(path);

        f.getParentFile().mkdirs();


        if(!f.exists()){
            System.out.println("File does not exist, so first time running.");
            try {
                f.createNewFile();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        else{
            System.out.println("File exists, so program has been run before.");
        }



    }

    private static void startEventListener() {
        Toolkit.getDefaultToolkit().getSystemClipboard().addFlavorListener(new FlavorListener() {
            @Override
            public void flavorsChanged(FlavorEvent e) {
                System.out.println("Text in the clipboard is: " + e.getSource() + " " + e);
            }
        });
    }
}

