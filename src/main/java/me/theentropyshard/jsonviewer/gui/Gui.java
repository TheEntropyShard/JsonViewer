package me.theentropyshard.jsonviewer.gui;

import com.formdev.flatlaf.FlatIntelliJLaf;
import me.theentropyshard.jsonviewer.JsonViewer;
import me.theentropyshard.jsonviewer.utils.SwingUtils;

import javax.swing.*;
import java.awt.*;

public class Gui {
    private static Gui instance;

    private final MainView mainView;
    private final JFrame frame;

    public Gui(JsonViewer jsonViewer) {
        this.initGui();

        this.mainView = new MainView(jsonViewer, this);

        this.frame = new JFrame("JsonViewer");
        this.frame.addWindowListener(new SaveConfigListener(jsonViewer.getConfig()));
        this.frame.setJMenuBar(this.makeJMenuBar());
        this.frame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        this.frame.add(this.mainView, BorderLayout.CENTER);
        this.frame.pack();
        SwingUtils.centerWindow(this.frame, 0);
        this.frame.setVisible(true);
    }

    private void initGui() {
        Gui.instance = this;

        UIManager.put("FileChooser.readOnly", Boolean.TRUE);
        JDialog.setDefaultLookAndFeelDecorated(true);
        JFrame.setDefaultLookAndFeelDecorated(true);
        FlatIntelliJLaf.setup();
    }

    private JMenuBar makeJMenuBar() {
        JMenuBar menuBar = new JMenuBar();

        JMenu fileMenu = new JMenu("File");
        menuBar.add(fileMenu);

        JMenuItem newTabItem = new JMenuItem("New tab");
        newTabItem.addActionListener(e -> this.mainView.newTab());
        fileMenu.add(newTabItem);

        return menuBar;
    }

    public static void showErrorDialog(String message) {
        JOptionPane.showMessageDialog(Gui.instance.getFrame(), message, "Error", JOptionPane.ERROR_MESSAGE);
    }

    public static String showInputDialog(String message, String title) {
        return JOptionPane.showInputDialog(Gui.instance.getFrame(), message, title, JOptionPane.PLAIN_MESSAGE);
    }

    public JFrame getFrame() {
        return this.frame;
    }
}
