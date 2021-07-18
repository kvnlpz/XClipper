import com.fasterxml.jackson.core.JsonProcessingException;
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.datatransfer.FlavorEvent;
import java.awt.datatransfer.FlavorListener;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.PrintWriter;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;
import java.util.prefs.Preferences;

public class GUI {
    static JList centerPanel;
    static DefaultListModel defaultListModel;
    static Color themeColor = new Color(55, 62, 65);
    static public JPanel mainList;
    static EmptyBorder border;
    static ServerHandler serverHandler;
    static  JFrame frame;
    static JPanel northPanel;
    static JScrollPane centerPanelScrollPane;
    static Path currentRelativePath = Paths.get("");
    static String s = currentRelativePath.toAbsolutePath().toString();
    static String path = s + File.separator + "run.txt";
    static File f = new File(path);
    //    String[] credentials = new String[3];
    static List<String> credentials = new ArrayList<>();


    public static void main(String[] args) throws NoSuchAlgorithmException {
        int width = 600, height = 400;
//        UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        Preferences prefs = Preferences.userNodeForPackage(GUI.class);
        final String PREF_NAME = "open_minimized";
        String defaultValue = "false";
        String propertyValue = prefs.get(PREF_NAME, defaultValue);
        String programText = "";
        f.getParentFile().mkdirs();


        System.out.println();
        System.out.println("Your operating system is: " + System.getProperty("os.name"));



        defaultListModel = new DefaultListModel();
        mainList = new JPanel(new GridBagLayout());
        mainList.setBackground(themeColor);
        serverHandler = new ServerHandler();


        frame = new JFrame("XClipper");
        ClipboardTextListener clipboardTextListener = new ClipboardTextListener(mainList, frame, themeColor);
        Thread thread = new Thread(clipboardTextListener);
        frame.setBackground(themeColor);
        frame.setLayout(new BorderLayout());


        createNorthPanel();


        JLabel titleLabel = new JLabel("Clipboard History");
        titleLabel.setFont(new Font("Calibri", Font.BOLD, 20));
        titleLabel.setForeground(Color.white);




        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridwidth = GridBagConstraints.REMAINDER;
        gbc.weightx = 1;
        gbc.weighty = 1;
//        mainList.add(new JPanel(), gbc);












        //setting the font size

//        Font labelFont = titleLabel.getFont();
//        String labelText = titleLabel.getText();
//
//        int stringWidth = titleLabel.getFontMetrics(labelFont).stringWidth(labelText);
//        int componentWidth = titleLabel.getWidth();
//
//        // Find out how much the font can grow in width.
//        double widthRatio = (double) componentWidth / (double) stringWidth;
//
//        int newFontSize = (int) (labelFont.getSize() * widthRatio);
//        int componentHeight = titleLabel.getHeight();
//
//        // Pick a new font size so it will not be larger than the height of label.
//        int fontSizeToUse = Math.min(newFontSize, componentHeight);
//
//        // Set the label's font size to the newly determined size.
//        titleLabel.setFont(new Font(labelFont.getName(), Font.PLAIN, fontSizeToUse));


//        JTextField usernameField = new JTextField("\t\t");
//        JTextField passwordField = new JTextField("\t\t");
//
//        usernameField.addFocusListener(new FocusListener() {
//            @Override
//            public void focusGained(FocusEvent e) {
//                usernameField.setText("");
//                usernameField.setForeground(new Color(50, 50, 50));
//            }
//
//            @Override
//            public void focusLost(FocusEvent e) {
//
//                if (usernameField.getText().length() == 0) {
//                    usernameField.setText("Username");
//                    usernameField.setForeground(new Color(150, 150, 150));
//                }
//
//            }
//        });
//        passwordField.addFocusListener(new FocusListener() {
//            @Override
//            public void focusGained(FocusEvent e) {
//                passwordField.setText("");
//                passwordField.setForeground(new Color(50, 50, 50));
//            }
//
//            @Override
//            public void focusLost(FocusEvent e) {
//
//                if (passwordField.getText().length() == 0) {
//                    passwordField.setText("Password");
//                    passwordField.setForeground(new Color(150, 150, 150));
//                }
//
//            }
//        });

//        JButton signUpButton = new JButton("Sign Up");
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

//        JButton loginButton = new JButton("Log In");
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

        JButton trashButton = ComponentHelper.createButton("trash");
        northPanel.add(titleLabel, BorderLayout.WEST);
        northPanel.add(trashButton, BorderLayout.EAST);
//        northPanel.add(usernameField);
//        northPanel.add(passwordField);
//        northPanel.add(signUpButton);
//        northPanel.add(loginButton);


        createCenterPanel();


        createCenterPanelScrollPane();

//        centerPanelScrollPane.setPreferredSize(new Dimension(300, 80));
//

        frame.setResizable(false);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        if (checkFirstTime()) {
            //if true, then it's the first time, so show the sign up page
            JComponent gridBagLayout = buildGridBagLayout();
            frame.add(gridBagLayout);
        } else {
            frame.add(northPanel, BorderLayout.NORTH);
            frame.add(centerPanelScrollPane, BorderLayout.CENTER);            //show the other page
        }


        frame.setPreferredSize(new Dimension(400, 400));
        frame.setSize(400, 400);
        frame.setVisible(true);
        frame.validate();
        thread.start();

    }

    private static void createNorthPanel() {
        northPanel = new JPanel(new BorderLayout());
        northPanel.setBackground(themeColor);
        northPanel.setPreferredSize(new Dimension(400, 50));
        border = new EmptyBorder(0, 0, 0, 0);
        northPanel.setBorder(border);
    }

    private static void createCenterPanelScrollPane() {
        centerPanelScrollPane = new JScrollPane(mainList);
        centerPanelScrollPane.getVerticalScrollBar().setPreferredSize(new Dimension(0, 0));
        centerPanelScrollPane.setBorder(border);
        centerPanelScrollPane.getVerticalScrollBar().setUnitIncrement(16);
    }

    private static void createCenterPanel() {
        centerPanel = new JList(defaultListModel);
        centerPanel.setForeground(Color.white);
        centerPanel.setSelectionMode(ListSelectionModel.SINGLE_INTERVAL_SELECTION);
        centerPanel.setLayoutOrientation(JList.HORIZONTAL_WRAP);
        centerPanel.setVisibleRowCount(-1);
        centerPanel.setBackground(themeColor);
    }

    private static void loggedIn() {
//        frame.removeAll();

        frame.getContentPane().removeAll();
//        frame.getContentPane().add(northPanel, BorderLayout.NORTH);
//        frame.getContentPane().add(centerPanelScrollPane, BorderLayout.CENTER);
        frame.add(northPanel, BorderLayout.NORTH);
        frame.add(centerPanelScrollPane, BorderLayout.CENTER);
        frame.validate();
        frame.repaint();
//        frame.repaint();
    }

//    protected static ImageIcon createImageIcon(String path) {
//        java.net.URL imgURL = getResource(path);
//        if (imgURL != null) {
//            return new ImageIcon(imgURL);
//        } else {
//            System.err.println("Couldn't find file: " + path);
//            return null;
//        }
//    }

    //    private static JButton createButton() {
//        JButton button = new JButton("Delete", new ImageIcon(GUI.class.getClassLoader().getResource("trash.png")));
////        ImageIcon icon = createImageIcon("/trash.png");
//
////        button.setIcon(icon);
//        button.setOpaque(true);
//        button.setBorder(border);
//        button.setBackground(themeColor);
//        button.setForeground(Color.white);
//        button.addMouseListener(new MouseAdapter() {
//            public void mousePressed(MouseEvent e) {
//                System.out.println("Trash clicked");
//            }
//        });
//
//
//        return button;
//    }
//
    private static boolean checkFirstTime() {
        //we check if it's the first time running the program by checking if a file exists in the path
//        Path currentRelativePath = Paths.get("");


        //        System.out.println("Current absolute path is: " + s);

        // Use relative path for Unix systems



        if (!f.exists()) {
            System.out.println("File does not exist, so first time running.");
            try {
                f.createNewFile();
                return true;
            } catch (IOException e) {
                e.printStackTrace();
            }
        } else {
            System.out.println("File exists, so program has been run before.");
            checkFile(f);
        }
        return false;
    }

    private static void checkFile(File f) {
        StringBuilder fileContents = new StringBuilder((int)f.length());
        try (Scanner scanner = new Scanner(f)) {
            credentials.add(scanner.next());
            while(scanner.hasNextLine()) {
                fileContents.append(scanner.nextLine() + System.lineSeparator());
                credentials.add(scanner.nextLine());
            }
//            return fileContents.toString();
            try {
                int code = serverHandler.logIn(credentials.get(0), credentials.get(1));
                System.out.println("the code is: " + code);
                if (code == 200) {
                    System.out.println("logged in");
                    loggedIn();
                }
            } catch (JsonProcessingException jsonProcessingException) {
                jsonProcessingException.printStackTrace();
            }

        } catch (FileNotFoundException e) {
            e.printStackTrace();
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

    private static JComponent buildGridBagLayout() {
        JTextField email = new JTextField(20);
        JLabel emailLabel = new JLabel("Email:");
        emailLabel.setForeground(Color.white);
        emailLabel.setDisplayedMnemonic('N');
        emailLabel.setLabelFor(email);
//        JTextField areaCode = new JTextField(3);
//        JLabel phoneLabel = new JLabel("Phone:");
//        phoneLabel.setDisplayedMnemonic('P');
//        phoneLabel.setLabelFor(areaCode);
//        JTextField prefix = new JTextField(3);
//        JTextField number = new JTextField(3);
        JTextField password = new JTextField(20);
        JLabel passwordLabel = new JLabel("Password:");
        passwordLabel.setForeground(Color.white);
        passwordLabel.setDisplayedMnemonic('E');
        passwordLabel.setLabelFor(password);
        JButton okButton = new JButton("Sign up");
        okButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                try {
                    int code = serverHandler.signUp(email.getText(), password.getText());
                    System.out.println("the code is: " + code);
                    if (code == 200) {
                        System.out.println("signed up");
                        saveCredentials(email.getText(), password.getText());
                        loggedIn();
                    }
                } catch (JsonProcessingException jsonProcessingException) {
                    jsonProcessingException.printStackTrace();
                }
            }
        });
        okButton.setPreferredSize(new Dimension(100, (int) okButton.getPreferredSize().getHeight()));
        JButton cancelButton = new JButton("Log in");
        cancelButton.setPreferredSize(new Dimension(100, (int) cancelButton.getPreferredSize().getHeight()));
        cancelButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                try {
                    int code = serverHandler.logIn(email.getText(), password.getText());
                    System.out.println("the code is: " + code);
                    if (code == 200) {
                        System.out.println("logged in");
                        saveCredentials(email.getText(), password.getText());
                        loggedIn();
                    }
                } catch (JsonProcessingException jsonProcessingException) {
                    jsonProcessingException.printStackTrace();
                }
            }
        });
        JPanel panel = new JPanel(new GridBagLayout());
        panel.setBorder(BorderFactory.createEmptyBorder(4, 10, 10, 10));
        panel.setBackground(themeColor);
        panel.setForeground(Color.white);
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.anchor = GridBagConstraints.WEST;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(6, 6, 0, 0);
        gbc.gridx = GridBagConstraints.RELATIVE;
        gbc.gridy = 0;

        panel.add(emailLabel, gbc);
        gbc.gridwidth = GridBagConstraints.REMAINDER;
        panel.add(email, gbc);

        gbc.gridy++;
        gbc.gridwidth = 1;
//        panel.add(phoneLabel, gbc);
//        panel.add(areaCode, gbc);
//        panel.add(prefix, gbc);
//        panel.add(number, gbc);

        gbc.gridy++;
        panel.add(passwordLabel, gbc);
        gbc.gridwidth = GridBagConstraints.REMAINDER;
        gbc.weightx = 1;
        panel.add(password, gbc);

        JPanel buttonPanel = new JPanel(new GridBagLayout());
        buttonPanel.setBackground(themeColor);
        buttonPanel.setForeground(Color.white);
        gbc.gridwidth = 1;
        gbc.gridy = 0;
        buttonPanel.add(okButton, gbc);
        buttonPanel.add(cancelButton, gbc);

        panel.add(buttonPanel,
                new GridBagConstraints(1, 3, 4, 1, 0, 0,
                        GridBagConstraints.EAST, GridBagConstraints.NONE,
                        new Insets(0, 0, 0, 0), 0, 0));

        panel.add(Box.createGlue(),
                new GridBagConstraints(0, 4, 4, 1, 0, 1,
                        GridBagConstraints.EAST, GridBagConstraints.NONE,
                        new Insets(0, 0, 0, 0), 0, 0));

        return panel;
    }

    private static void saveCredentials(String text, String passwordText) {
        System.out.println("Saving Credentials");
        PrintWriter writer = null;
        try {
            writer = new PrintWriter(f);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }

        writer.write(text + "\n");
        writer.write(passwordText + "\n");
        writer.close();

    }


}