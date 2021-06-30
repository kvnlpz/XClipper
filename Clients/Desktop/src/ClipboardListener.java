import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.DataFlavor;
import java.util.Observable;

import java.awt.HeadlessException;
import java.awt.Toolkit;
import java.awt.datatransfer.UnsupportedFlavorException;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;


class ClipboardTextListener extends Observable implements Runnable {

    Clipboard clipboard = Toolkit.getDefaultToolkit().getSystemClipboard();

    private volatile boolean running = true;

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
                        setChanged();
                        notifyObservers(data);
                    }
                }

            } catch (HeadlessException | UnsupportedFlavorException | IOException e1) {
                e1.printStackTrace();
            }
        }
    }
}