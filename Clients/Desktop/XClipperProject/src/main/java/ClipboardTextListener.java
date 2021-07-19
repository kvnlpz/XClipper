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

    public ClipboardTextListener(JPanel mainList, JFrame frame, Color themeColor) throws NoSuchAlgorithmException {
        clientManager = new ClientManager();
        this.mainList = mainList;
        this.frame = frame;
        this.themeColor = themeColor;
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
                        JPanel panel = ComponentHelper.createPanel();
                        GridBagConstraints gbc = ComponentHelper.createGridBagConstraints();
                        JTextArea textArea = ComponentHelper.createTextArea(data);
                        final JPopupMenu popup = ComponentHelper.createPopupMenu();
                        JButton button = ComponentHelper.createButton(popup);
                        panel.add(textArea, BorderLayout.CENTER);
                        panel.add(button, BorderLayout.EAST);
                        mainList.add(panel, gbc, 0);
                        GUI.serverHandler.uploadText(data);
                        frame.validate();
                        frame.repaint();
                    }
                }

            } catch (Exception e1) {
                e1.printStackTrace();
            }
        }
    }

//    private JPopupMenu createPopupMenu() {
//        //Create the popup menu.
//        final JPopupMenu popup = new JPopupMenu();
//        popup.setBackground(themeColor);
//        JMenuItem deleteItem = new JMenuItem(new AbstractAction("Delete") {
//            @Override
//            public void actionPerformed(ActionEvent e) {
//                JOptionPane.showMessageDialog(frame, "Delete selected");
//
//            }
//        });
//        deleteItem.setBackground(themeColor);
//        deleteItem.setForeground(Color.white);
//        popup.add(deleteItem);
//        JMenuItem pinItem = new JMenuItem(new AbstractAction("Pin") {
//            @Override
//            public void actionPerformed(ActionEvent e) {
//                JOptionPane.showMessageDialog(frame, "Pin selected");
//
//            }
//        });
//        pinItem.setBackground(themeColor);
//        pinItem.setForeground(Color.white);
//        popup.add(pinItem);
//
//        return popup;
//    }

//    private GridBagConstraints createGridBagConstraints() {
//        GridBagConstraints gbc = new GridBagConstraints();
//        gbc.gridwidth = GridBagConstraints.REMAINDER;
//        gbc.weightx = 1;
//        gbc.fill = GridBagConstraints.HORIZONTAL;
//        gbc.insets = new Insets(3, 3, 3, 3);
//        return gbc;
//    }

//    private JPanel createPanel() {
//        JPanel panel = new JPanel();
//        panel.setLayout(new BorderLayout());
//        panel.setPreferredSize(new Dimension(200, 50));
//        panel.setBorder(new MatteBorder(0, 0, 1, 0, themeColor));
//        panel.setBackground(themeColor);
//        panel.setForeground(Color.white);
//        return panel;
//    }
//
//    private JTextArea createTextArea(String data) {
//        JTextArea textArea = new JTextArea(data);
//        textArea.setBackground(themeColor);
//        textArea.setForeground(Color.white);
//        textArea.setLineWrap(true);
//        textArea.setEditable(false);
//        return textArea;
//    }
//
//    private JButton createButton(JPopupMenu popup) {
//
//        JButton button = new JButton("...");
//        button.setOpaque(true);
//        button.setBackground(new Color(55, 62, 65));
//        button.setForeground(Color.white);
//        button.addMouseListener(new MouseAdapter() {
//            public void mousePressed(MouseEvent e) {
//                popup.show(e.getComponent(), e.getX(), e.getY());
//            }
//        });
//
//
//        return button;
//    }
}