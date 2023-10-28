package me.theentropyshard.jsonviewer.gui;

import com.formdev.flatlaf.FlatIntelliJLaf;
import me.theentropyshard.jsonviewer.JsonViewer;
import me.theentropyshard.jsonviewer.utils.SwingUtils;

import javax.swing.*;
import java.awt.*;
import java.io.File;
import java.util.List;

public class Gui {
    private static Gui instance;

    private final JsonViewer jsonViewer;

    private final MainView mainView;
    private final JFrame frame;

    private JMenu recentUrlsMenu;
    private JMenu recentFilesMenu;

    public Gui(JsonViewer jsonViewer) {
        this.jsonViewer = jsonViewer;

        this.initGui();

        this.mainView = new MainView(jsonViewer, this);

        this.frame = new JFrame("JsonViewer");
        this.frame.addWindowListener(new SaveConfigListener(jsonViewer.getConfigSavePath(), jsonViewer.getConfig()));
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

    // maybe flatten all menus?
    private JMenuBar makeJMenuBar() {
        JMenuBar menuBar = new JMenuBar();

        JMenu fileMenu = new JMenu("File");
        menuBar.add(fileMenu);

        JMenuItem newTabItem = new JMenuItem("New tab");
        newTabItem.addActionListener(e -> this.mainView.newTab());
        fileMenu.add(newTabItem);

        this.recentUrlsMenu = new JMenu("Recent URLs");

        List<String> recentUrls = this.jsonViewer.getConfig().getRecentUrls();

        if (!recentUrls.isEmpty()) {
            if (recentUrls.size() < 10) {
                recentUrls = recentUrls.subList(0, recentUrls.size());
            } else {
                recentUrls = recentUrls.subList(0, 10);
            }

            for (String url : recentUrls) {
                JMenuItem urlItem = new JMenuItem(url);
                urlItem.addActionListener(e -> SwingUtils.startWorker(() -> this.mainView.getFromUrl(url)));

                this.recentUrlsMenu.add(urlItem);
            }
        }

        fileMenu.add(this.recentUrlsMenu);

        this.recentFilesMenu = new JMenu("Recent Files");

        List<String> recentFiles = this.jsonViewer.getConfig().getRecentFiles();

        if (!recentFiles.isEmpty()) {
            if (recentFiles.size() < 10) {
                recentFiles = recentFiles.subList(0, recentFiles.size());
            } else {
                recentFiles = recentFiles.subList(0, 10);
            }

            for (String path : recentFiles) {
                JMenuItem urlItem = new JMenuItem(path);
                urlItem.addActionListener(e -> SwingUtils.startWorker(() -> this.mainView.getFromFile(new File(path))));

                this.recentFilesMenu.add(urlItem);
            }
        }

        fileMenu.add(this.recentFilesMenu);

        return menuBar;
    }

    public void addRecentUrl(String url) {
        JMenuItem urlItem = new JMenuItem(url);
        urlItem.addActionListener(e -> SwingUtils.startWorker(() -> this.mainView.getFromUrl(url)));

        this.recentUrlsMenu.add(urlItem);
    }

    public void addRecentFile(String path) {
        JMenuItem urlItem = new JMenuItem(path);
        urlItem.addActionListener(e -> SwingUtils.startWorker(() -> this.mainView.getFromFile(new File(path))));

        this.recentFilesMenu.add(urlItem);
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
