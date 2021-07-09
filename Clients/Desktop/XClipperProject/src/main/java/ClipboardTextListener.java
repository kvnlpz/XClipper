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

        String lastContent = "";

        while (running) {
            try {
                Thread.sleep(200);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            try {

                List<DataFlavor> flavors = Arrays.asList(clipboard.getAvailableDataFlavors());

                if (flavors.contains(DataFlavor.stringFlavor)) {
                    String data = (String) clipboard.getData(DataFlavor.stringFlavor);
                    if (!data.equals(lastContent)) {
                        lastContent = data;

                        System.out.println("New clipboard text detected: " + data);
                        clientManager.encryptText(data);

                        setChanged();
                        notifyObservers(data);


                        GUI.defaultListModel.addElement(data);

                        GUI.centerPanel.revalidate();
                    }
                }

            } catch (Exception e1) {
                e1.printStackTrace();
            }
        }
    }
}