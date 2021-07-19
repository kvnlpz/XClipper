import javax.swing.*;
import javax.swing.border.MatteBorder;
import java.awt.*;

public class ComponentBuilder {

    static private JPanel createPanel() {
        JPanel panel = new JPanel();
        panel.setLayout(new BorderLayout());
        panel.setPreferredSize(new Dimension(200, 50));
        panel.setBorder(new MatteBorder(0, 0, 1, 0, GUI.themeColor));
        panel.setBackground(GUI.themeColor);
        panel.setForeground(Color.white);
        return panel;
    }
}
