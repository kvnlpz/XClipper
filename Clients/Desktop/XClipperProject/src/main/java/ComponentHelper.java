import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.border.MatteBorder;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

public class ComponentHelper {
    static JList centerPanel;
    static DefaultListModel defaultListModel;
    static Color themeColor = new Color(55, 62, 65);
    //MAKE THIS PRIVATE AGAIN
    static public JPanel mainList;

    static EmptyBorder border;
    static ServerHandler serverHandler;
    static JFrame frame;
    static JPanel northPanel;
    static JScrollPane centerPanelScrollPane;


    public static JButton createButton(String title) {
        JButton button = new JButton(title, new ImageIcon(GUI.class.getClassLoader().getResource("trash.png")));
        button.setOpaque(true);
        button.setBorder(border);
        button.setBackground(themeColor);
        button.setForeground(Color.white);
        button.addMouseListener(new MouseAdapter() {
            public void mousePressed(MouseEvent e) {
                System.out.println("Trash clicked");
            }
        });

        return button;

    }

    public static JPopupMenu createPopupMenu() {
        //Create the popup menu.
        final JPopupMenu popup = new JPopupMenu();
        popup.setBackground(GUI.themeColor);
        JMenuItem deleteItem = new JMenuItem(new AbstractAction("Delete") {
            @Override
            public void actionPerformed(ActionEvent e) {
                JOptionPane.showMessageDialog(GUI.frame, "Delete selected");

            }
        });
        deleteItem.setBackground(GUI.themeColor);
        deleteItem.setForeground(Color.white);
        popup.add(deleteItem);
        JMenuItem pinItem = new JMenuItem(new AbstractAction("Pin") {
            @Override
            public void actionPerformed(ActionEvent e) {
                JOptionPane.showMessageDialog(GUI.frame, "Pin selected");

            }
        });
        pinItem.setBackground(themeColor);
        pinItem.setForeground(Color.white);
        popup.add(pinItem);

        return popup;
    }

    public static GridBagConstraints createGridBagConstraints() {
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridwidth = GridBagConstraints.REMAINDER;
        gbc.weightx = 1;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(3, 3, 3, 3);
        return gbc;
    }

    public static JButton createButton(JPopupMenu popup) {

        JButton button = new JButton("...");
        button.setOpaque(true);
        button.setBackground(new Color(55, 62, 65));
        button.setForeground(Color.white);
        button.addMouseListener(new MouseAdapter() {
            public void mousePressed(MouseEvent e) {
                popup.show(e.getComponent(), e.getX(), e.getY());
            }
        });


        return button;
    }

    public static JTextArea createTextArea(String data) {
        JTextArea textArea = new JTextArea(data);
        textArea.setBackground(GUI.themeColor);
        textArea.setForeground(Color.white);
        textArea.setLineWrap(true);
        textArea.setEditable(false);
        return textArea;
    }

    public static JPanel createPanel() {
        JPanel panel = new JPanel();
        panel.setLayout(new BorderLayout());
        panel.setPreferredSize(new Dimension(200, 50));
        panel.setBorder(new MatteBorder(0, 0, 1, 0, GUI.themeColor));
        panel.setBackground(GUI.themeColor);
        panel.setForeground(Color.white);
        return panel;
    }


}
