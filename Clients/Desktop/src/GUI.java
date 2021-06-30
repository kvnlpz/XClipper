import javax.swing.*;
import java.awt.*;
import java.awt.datatransfer.*;
import java.io.IOException;
import java.util.Arrays;
import java.util.Observable;

public class GUI {
    public static void main(String[] args) {
        //check for OS
        //System.getProperties().list(System.out);  //<- more information
        System.out.println();
        System.out.println("Your operating system is: " + System.getProperty("os.name"));

        ClipboardTextListener clipboardTextListener = new ClipboardTextListener();
        Thread thread = new Thread(clipboardTextListener);
        thread.start();
        // start event listener
        //startEventListener();
        //first time running application?
        //  prompt user for info
        //no?
        //  run in background at bootup


        JFrame frame = new JFrame("XClipper");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        JTextPane tp = new JTextPane();
        tp.setPreferredSize(new Dimension(384, 256));
        frame.getContentPane().add(new JScrollPane(tp));
        frame.pack();
        frame.setVisible(true);


    }

    private static void startEventListener() {
        Toolkit.getDefaultToolkit().getSystemClipboard().addFlavorListener(new FlavorListener() {
            @Override
            public void flavorsChanged(FlavorEvent e) {
                System.out.println("Text in the clipboard is: " + e.getSource() + " " + e.toString());
            }
        });
    }
    }

