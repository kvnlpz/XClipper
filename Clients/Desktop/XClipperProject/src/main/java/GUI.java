import javax.swing.*;
import java.awt.*;
import java.awt.datatransfer.FlavorEvent;
import java.awt.datatransfer.FlavorListener;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.security.NoSuchAlgorithmException;
import java.util.prefs.Preferences;

public class GUI {
    static JPanel centerPanel;

    public static void main(String[] args) throws UnsupportedLookAndFeelException, ClassNotFoundException, InstantiationException, IllegalAccessException, NoSuchAlgorithmException {
        int width = 600, height = 400;
        UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
//        ClientManager clientManager = new ClientManager();

        // Retrieve the user preference node for the package com.mycompany
        Preferences prefs = Preferences.userNodeForPackage(GUI.class);

        // Preference key name
        final String PREF_NAME = "open_minimized";


        String defaultValue = "false";

        // default value is returned if the preference does not exist
        String propertyValue = prefs.get(PREF_NAME, defaultValue);
        // Get the value of the preference;


        String programText = "";
        if (propertyValue.equals("false")) {
            programText = "the program is set to open up normally";

        } else {
            programText = "The program is set to open up minimized";
        }


        //check for OS
        //System.getProperties().list(System.out);  //<- more information
        System.out.println();
        System.out.println("Your operating system is: " + System.getProperty("os.name"));

        ClipboardTextListener clipboardTextListener = new ClipboardTextListener();
        Thread thread = new Thread(clipboardTextListener);
        // start event listener
        //startEventListener();
        //first time running application?
        //  prompt user for info
        //no?
        //  run in background at bootup


        JFrame frame = new JFrame("XClipper");
        frame.setBackground(new Color(55, 62, 65));
//        frame.setBackground(Color.getHSBColor(198, 15, 25));
        frame.setLayout(new BorderLayout());

        JPanel northPanel = new JPanel(new FlowLayout());
        northPanel.setBackground(new Color(55, 62, 65));

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


        usernameField.setSize(width / 3, 10);
        passwordField.setSize(width / 3, 10);
        JButton loginButton = new JButton("Log In");

        northPanel.add(usernameField);
        northPanel.add(passwordField);
        northPanel.add(loginButton);

        centerPanel = new JPanel();
        centerPanel.setLayout(new BoxLayout(centerPanel, BoxLayout.Y_AXIS));
        centerPanel.setBackground(new Color(55, 62, 65));
        JScrollPane centerPanelScrollPane = new JScrollPane(centerPanel);
        centerPanel.setPreferredSize(new Dimension(400, 200));
        centerPanelScrollPane.setPreferredSize(new Dimension(400, 200));

        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
//        JTextPane tp = new JTextPane();
//        tp.setPreferredSize(new Dimension(384, 256));

//        JButton button = new JButton("minimized mode");
//        button.addActionListener(new ActionListener() {
//            public void actionPerformed(ActionEvent e) {
//                 Set the value of the preference
//                String newValue = "true";
//                prefs.put(PREF_NAME, newValue);
//            }
//        });
//        JTextArea textArea = new JTextArea(programText);

        frame.add(northPanel, BorderLayout.NORTH);
        frame.add(centerPanelScrollPane, BorderLayout.CENTER);


//        frame.add(button);
//        frame.add(textArea);
//        frame.getContentPane().add(textArea);
        frame.setPreferredSize(new Dimension(400, 400));
        frame.setSize(400, 400);
//        frame.pack();
        frame.setVisible(true);


        if (propertyValue.equals("true")) {
            frame.setState(Frame.ICONIFIED);
        }


        thread.start();
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

