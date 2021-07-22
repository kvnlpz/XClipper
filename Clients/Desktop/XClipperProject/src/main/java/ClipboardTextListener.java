import javax.swing.*;
import java.awt.*;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.DataFlavor;
import java.security.NoSuchAlgorithmException;
import java.util.Arrays;
import java.util.List;
import java.util.Observable;


class ClipboardTextListener extends Observable implements Runnable {
    ClientManager clientManager;
    Clipboard clipboard = Toolkit.getDefaultToolkit().getSystemClipboard();
    DefaultListModel defaultListModel;
    JPanel mainList;
    Color themeColor;
    private volatile boolean running = true;
    private Frame frame;

    //constructor
    public ClipboardTextListener(ClientManager clientManager) {
        this.clientManager = clientManager;
    }

    //overloaded constructor
    public ClipboardTextListener(JPanel mainList, JFrame frame, Color themeColor) throws NoSuchAlgorithmException {
        clientManager = new ClientManager();
        this.mainList = mainList;
        this.frame = frame;
        this.themeColor = themeColor;
    }

    //For future changes
    public void terminate() {
        running = false;
    }

    //for the thread start
    public void run() {
        System.out.println("Listening to clipboard...");
        String lastContent = "";
        while (running) {
            try {
                Thread.sleep(200);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            try {

                List<DataFlavor> flavors = Arrays.asList(clipboard.getAvailableDataFlavors());

                //checking to see if we have new strings in the clipboard
                if (flavors.contains(DataFlavor.stringFlavor)) {
                    String data = (String) clipboard.getData(DataFlavor.stringFlavor);
                    if (!data.equals(lastContent)) {
                        lastContent = data;
                        System.out.println("New clipboard text detected: " + data);
//                        clientManager.encryptText(data);
                        setChanged();
                        notifyObservers(data);
                        //GUI CODE
                        JPanel panel = ComponentHelper.createPanel();
                        GridBagConstraints gbc = ComponentHelper.createGridBagConstraints();
                        JTextArea textArea = ComponentHelper.createTextArea(data);
                        final JPopupMenu popup = ComponentHelper.createPopupMenu();
                        JButton button = ComponentHelper.createButton(popup);
                        panel.add(textArea, BorderLayout.CENTER);
                        panel.add(button, BorderLayout.EAST);
                        ComponentHelper.addTitledBorder(panel, GUI.borderBlue, "Clip", GUI.themeColorTwo);
                        //adding the clip to the list (GUI)
                        mainList.add(panel, gbc, 0);
                        //uploading the clip to the server
                        GUI.serverHandler.uploadText(data);
                        //updating the GUI
                        frame.validate();
                        frame.repaint();
                    }
                }

            } catch (Exception e1) {
                e1.printStackTrace();
            }
        }
    }
}