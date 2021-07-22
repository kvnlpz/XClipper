import com.fasterxml.jackson.core.JsonProcessingException;

import javax.swing.*;
import javax.swing.border.Border;
import javax.swing.border.EmptyBorder;
import javax.swing.border.LineBorder;
import java.awt.*;
import java.awt.datatransfer.FlavorEvent;
import java.awt.datatransfer.FlavorListener;
import java.awt.datatransfer.StringSelection;
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
    static public JPanel mainList;
    static JList centerPanel;
    static DefaultListModel defaultListModel;
    //    static Color themeColor = new Color(55, 62, 65);
//    static Color themeColor = new Color(31, 31, 34);
    static Color themeColor = new Color(26, 17, 64);
    //    static Color themeColorTwo = new Color(81, 94, 96, 253);
    static Color themeColorTwo = new Color(47, 41, 73);
    static Border borderRed;
    static Border borderBlue;
    static Border borderCyan;
    static Border borderYellow;
    static Border borderGreen;
    static Border borderPurple;
    static Border borderPink;
    static Border borderWhite;
    static EmptyBorder border;
    static ServerHandler serverHandler;
    static JFrame frame;
    static JPanel northPanel;
    static JScrollPane centerPanelScrollPane;
    static Path currentRelativePath = Paths.get("");
    static String s = currentRelativePath.toAbsolutePath().toString();
    static String path = s + File.separator + "run.txt";
    static File f = new File(path);
    //    String[] credentials = new String[3];
    static List<String> credentials = new ArrayList<>();


    public static void main(String[] args) throws NoSuchAlgorithmException {
        //Program dimensions, need to make these dependent on the monitor later
        int width = 600, height = 400;

        //UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());

        //clearing the clipboard to avoid exceptions
        clearClipboard();
        //line borders for the GUI
        createLineBorders();

        //For future changes, want to save settings for the next usage
        Preferences prefs = Preferences.userNodeForPackage(GUI.class);
        final String PREF_NAME = "open_minimized";
        String defaultValue = "false";
        String propertyValue = prefs.get(PREF_NAME, defaultValue);
        String programText = "";

        //for our File, creates the directory
        f.getParentFile().mkdirs();


        //for future changes, different behavior based on the OS at use
        System.out.println();
        System.out.println("Your operating system is: " + System.getProperty("os.name"));


        //GUI code
        defaultListModel = new DefaultListModel();
        mainList = new JPanel(new GridBagLayout());
        mainList.setBackground(themeColor);

        //creating our ServerHandler object for usability
        serverHandler = new ServerHandler();

        frame = new JFrame("XClipper");
        frame.setBackground(themeColor);
        frame.setLayout(new BorderLayout());

        //ClipboardTextListener is the class I created for taking care of the clipboard events on the desktop
        ClipboardTextListener clipboardTextListener = new ClipboardTextListener(mainList, frame, themeColor);
        //has to run in the background so I use a thread
        Thread thread = new Thread(clipboardTextListener);

        //more GUI code
        createNorthPanel();
        JLabel titleLabel = new JLabel("Clipboard History", SwingConstants.CENTER);
        titleLabel.setFont(new Font("Calibri", Font.BOLD, 20));
        titleLabel.setForeground(Color.white);


        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridwidth = GridBagConstraints.REMAINDER;
        gbc.weightx = 1;
        gbc.weighty = 1;

        JButton trashButton = ComponentHelper.createButton("trash");
        northPanel.add(titleLabel, BorderLayout.WEST);
        northPanel.add(trashButton, BorderLayout.EAST);
        createCenterPanel();
        createCenterPanelScrollPane();

        //dont want the user to be able to resize the program
        frame.setResizable(false);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        //checking if the user is new or if they are returning
        if (checkFirstTime()) {
            //if true, then it's the first time, so show the sign up page
            JComponent gridBagLayout = buildGridBagLayout();
            frame.add(gridBagLayout);
            frame.setSize(400, height / 3);
        } else {
            //show the other page
            frame.add(northPanel, BorderLayout.NORTH);
            frame.add(centerPanelScrollPane, BorderLayout.CENTER);
            frame.setSize(400, height);
        }

        //final GUI code
        frame.setPreferredSize(new Dimension(400, 400));
        frame.setVisible(true);
        frame.validate();

        //after all of the other things are taken care of, start the thread.
        thread.start();

    }

    //self-explanatory
    private static void clearClipboard() {
        StringSelection stringSelection = new StringSelection("");
        Toolkit.getDefaultToolkit().getSystemClipboard().setContents(stringSelection, null);
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

    //just changes the UI if we are logged in
    private static void loggedIn() {
        frame.setSize(400, 400);
        frame.getContentPane().removeAll();
        frame.add(northPanel, BorderLayout.NORTH);
        frame.add(centerPanelScrollPane, BorderLayout.CENTER);
        frame.validate();
        frame.repaint();
    }



    private static boolean checkFirstTime() {
        //we check if it's the first time running the program by checking if a file exists in the path
        if (!f.exists()) {
            System.out.println("File does not exist, so first time running.");
            try {
                //since it doesnt exist, it's our first time running it, so we make a new file for the next time we run
                f.createNewFile();
                return true;
            } catch (IOException e) {
                e.printStackTrace();
            }
        } else {
            //since the file exists, we have run the program before, but now we need to check if it has the right
            //credentials. if any
            System.out.println("File exists, so program has been run before.");
            return checkFile(f);
        }
        return false;
    }

    //method for checking if we have the right login credentials in the file
    //we will most certainly change this in the future because I don't want to keep credentials in a text file
    private static Boolean checkFile(File f) {
        //if the file has credentials, return true because although it's been run before, it has no credentials
        StringBuilder fileContents = new StringBuilder((int) f.length());
        if (f.length() == 0) {
            //if there's nothing in the file, dont even bother doing anything else
            System.out.println("file has no credentials.");
            return true;
        } else {
            try (Scanner scanner = new Scanner(f)) {
                credentials.add(scanner.next());
                while (scanner.hasNextLine()) {
                    //we dont even need to add it to the StringBuilder but I might have a use for this in the  future
                    fileContents.append(scanner.nextLine() + System.lineSeparator());
                    //storing the credentials to a List of Strings
                    credentials.add(scanner.nextLine());
                }
//            return fileContents.toString();
                try {
                    //if the credentials list is bigger than 1 it means it has more than 1 credential so we try and run
                    if (credentials.size() > 1) {
                        int code = serverHandler.logIn(credentials.get(0), credentials.get(1));
                        System.out.println("the code is: " + code);
                        if (code == 200) {
                            System.out.println("logged in");
                            //since we got response code 200, it was a success, so change the UI
                            loggedIn();
                            return false;
                        } else {
                            System.out.println("failed to log in.");
                            return true;
                        }
                    }

                } catch (JsonProcessingException jsonProcessingException) {
                    jsonProcessingException.printStackTrace();
                }

            } catch (FileNotFoundException e) {
                e.printStackTrace();
            }
        }
        return true;
    }

    //from a previous iteration of the program, no longer in use, but might revert back to it in the future
    private static void startEventListener() {
        Toolkit.getDefaultToolkit().getSystemClipboard().addFlavorListener(new FlavorListener() {
            @Override
            public void flavorsChanged(FlavorEvent e) {
                System.out.println("Text in the clipboard is: " + e.getSource() + " " + e);
            }
        });
    }

    //the bulk of the GUI sign up code is here
    //TODO() make the program take an email address as well
    //TODO() remove hard coded email values from the ServerHandler Class
    private static JComponent buildGridBagLayout() {
        JTextField email = new JTextField(20);
        JLabel emailLabel = new JLabel("Email:");
        emailLabel.setForeground(Color.white);
        emailLabel.setDisplayedMnemonic('N');
        emailLabel.setLabelFor(email);
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

    private static void createLineBorders() {
        borderGreen = new LineBorder(Color.GREEN, 4, true);
        borderWhite = new LineBorder(Color.white, 4, true);
        borderPink = new LineBorder(Color.pink, 4, true);
        borderPurple = new LineBorder(Color.MAGENTA, 4, true);
        borderBlue = new LineBorder(Color.BLUE, 4, true);
        borderCyan = new LineBorder(Color.cyan, 4, true);
        borderRed = new LineBorder(Color.RED, 4, true);
        borderYellow = new LineBorder(Color.yellow, 4, true);
    }
}