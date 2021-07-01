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
    private volatile boolean running = true;

    public ClipboardTextListener() throws NoSuchAlgorithmException {
        clientManager = new ClientManager();
    }

    public ClipboardTextListener(ClientManager clientManager) {
        this.clientManager = clientManager;
    }

    public void terminate() {
        running = false;
    }

    public void run() {
        System.out.println("Listening to clipboard...");
        // the first output will be when a non-empty text is detected
        String lastContent = "";
        // continuously perform read from clipboard
        while (running) {
            try {
                Thread.sleep(200);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            try {
                // request what kind of data-flavor is supported
                List<DataFlavor> flavors = Arrays.asList(clipboard.getAvailableDataFlavors());
                // this implementation only supports string-flavor
                if (flavors.contains(DataFlavor.stringFlavor)) {
                    String data = (String) clipboard.getData(DataFlavor.stringFlavor);
                    if (!data.equals(lastContent)) {
                        lastContent = data;
                        // Do whatever you want to do when a clipboard change was detected, e.g.:
                        System.out.println("New clipboard text detected: " + data);
                        clientManager.encryptText(data);

                        setChanged();
                        notifyObservers(data);
                        JLabel label = new JLabel(data, JLabel.LEFT);
                        label.setForeground(Color.white);
                        GUI.centerPanel.add(label);
                    }
                }

            } catch (Exception e1) {
                e1.printStackTrace();
            }
        }
    }
}