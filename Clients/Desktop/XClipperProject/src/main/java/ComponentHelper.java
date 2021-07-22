import javax.swing.*;
import javax.swing.border.Border;
import javax.swing.border.EmptyBorder;
import javax.swing.border.MatteBorder;
import javax.swing.border.TitledBorder;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

/*
THIS FILE IS ONLY FOR MY GUI, USED TO HELP ME SIMPLIFY THE CODE BY REMOVING BOILERPLATE CODE AND USING A CLASS
FOR THE GENERATION OF UI COMPONENTS
 */
public class ComponentHelper {
    static JList centerPanel;
    static DefaultListModel defaultListModel;
    static Color themeColor = new Color(55, 62, 65);
    //TODO() MAKE THIS PRIVATE AGAIN
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
        button.setBackground(GUI.themeColor);
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
        popup.setBackground(GUI.themeColorTwo);

        JMenuItem deleteItem = new JMenuItem(new AbstractAction("Delete") {
            @Override
            public void actionPerformed(ActionEvent e) {
                JOptionPane.showMessageDialog(GUI.frame, "Delete selected");
                System.out.println("deleting item");
            }
        });
        deleteItem.setBackground(GUI.themeColor);
        deleteItem.setForeground(Color.white);
        popup.add(deleteItem);
        JMenuItem pinItem = new JMenuItem(new AbstractAction("Pin") {
            @Override
            public void actionPerformed(ActionEvent e) {
                JOptionPane.showMessageDialog(GUI.frame, "Pin selected");
                System.out.println("pinning item");
            }
        });
        pinItem.setBackground(GUI.themeColorTwo);
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

        JButton button = new JButton("");
        button.setOpaque(true);
        button.setBackground(GUI.themeColorTwo);
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
        //textArea.setBackground(GUI.themeColor);
        textArea.setOpaque(false);
        textArea.setForeground(Color.white);
        textArea.setLineWrap(true);
        textArea.setEditable(false);
        return textArea;
    }

    public static JPanel createPanel() {
        JPanel panel = new JPanel();
        panel.setLayout(new BorderLayout());
        panel.setPreferredSize(new Dimension(200, 80));
        panel.setBorder(new MatteBorder(1, 1, 1, 1, GUI.themeColorTwo));
        panel.setBackground(GUI.themeColor);
        panel.setForeground(Color.white);
        return panel;
    }

    public static RoundedPanel createRoundedPanel(){
        RoundedPanel panel = new RoundedPanel();
        panel.setLayout(new BorderLayout());
        panel.setPreferredSize(new Dimension(200, 80));
        //panel.setBorder(new MatteBorder(1, 1, 1, 1, GUI.themeColorTwo));
        panel.setBackground(GUI.themeColor);
        panel.setForeground(Color.white);
        return panel;
    }

    public static void addTitledBorder(JPanel panel, Border border, String title, Color color) {

        TitledBorder titledBorder = BorderFactory.createTitledBorder(border, title);
        //titledBorder.setTitleColor(color);
        //panel.setBorder(border);
        panel.setBackground(GUI.themeColorTwo);
        //panel.setBorder(titledBorder);

    }

}
