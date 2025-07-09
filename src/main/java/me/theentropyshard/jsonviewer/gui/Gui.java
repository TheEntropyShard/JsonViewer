/*
 * JsonViewer - https://github.com/TheEntropyShard/JsonViewer
 * Copyright (C) 2023-2025 TheEntropyShard
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program. If not, see <https://www.gnu.org/licenses/>.
 */

package me.theentropyshard.jsonviewer.gui;

import com.formdev.flatlaf.FlatIntelliJLaf;

import javax.swing.*;
import java.awt.*;
import java.io.File;
import java.util.List;

import me.theentropyshard.jsonviewer.JsonViewer;
import me.theentropyshard.jsonviewer.utils.SwingUtils;

public class Gui {
    private static Gui instance;

    private final JsonViewer jsonViewer;

    private final MainView mainView;
    public static JFrame frame;

    private JMenu recentUrlsMenu;
    private JMenu recentFilesMenu;

    public Gui(JsonViewer jsonViewer) {
        this.jsonViewer = jsonViewer;

        this.initGui();

        this.mainView = new MainView(jsonViewer.getConfig(), jsonViewer.getJsonService(), this);

        frame = new JFrame("JsonViewer");
        frame.addWindowListener(new SaveConfigListener(jsonViewer.getConfigSavePath(), jsonViewer.getConfig()));
        frame.setJMenuBar(this.makeJMenuBar());
        frame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        frame.add(this.mainView, BorderLayout.CENTER);
        frame.pack();
        SwingUtils.centerWindow(frame, 0);
        frame.setVisible(true);
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
                //urlItem.addActionListener(e -> SwingUtils.startWorker(() -> this.mainView.getFromUrl(url, null)));

                this.recentUrlsMenu.add(urlItem);
            }
        }

        fileMenu.add(this.recentUrlsMenu);

        this.recentFilesMenu = new JMenu("Recent Files");

        this.makeRecentFilesMenu();

        fileMenu.add(this.recentFilesMenu);

        return menuBar;
    }

    public void makeRecentUrlsMenu() {
        this.recentUrlsMenu.removeAll();

        List<String> recentUrls = this.jsonViewer.getConfig().getRecentUrls();

        for (String url : recentUrls) {
            JMenuItem urlItem = new JMenuItem(url);
            //urlItem.addActionListener(e -> SwingUtils.startWorker(() -> this.mainView.getFromUrl(url, null)));

            this.recentUrlsMenu.add(urlItem);
        }
    }

    public void makeRecentFilesMenu() {
        this.recentFilesMenu.removeAll();

        List<String> recentFiles = this.jsonViewer.getConfig().getRecentFiles();

        for (String path : recentFiles) {
            JMenuItem fileItem = new JMenuItem(path);
            fileItem.addActionListener(e -> SwingUtils.startWorker(() -> this.mainView.getFromFile(new File(path), null)));

            this.recentFilesMenu.add(fileItem);
        }
    }

    public void addRecentUrl(String url) {
        this.jsonViewer.getConfig().addRecentUrl(url);
        this.makeRecentUrlsMenu();
    }

    public void removeRecentUrl(String url) {
        this.jsonViewer.getConfig().removeRecentUrl(url);
        this.makeRecentUrlsMenu();
    }

    public void addRecentFile(String path) {
        this.jsonViewer.getConfig().addRecentFile(path);
        this.makeRecentFilesMenu();
    }

    public void removeRecentFile(String absolutePath) {
        this.jsonViewer.getConfig().removeRecentFile(absolutePath);
        this.makeRecentFilesMenu();
    }

    public static void showErrorDialog(String message) {
        JOptionPane.showMessageDialog(Gui.instance.getFrame(), message, "Error", JOptionPane.ERROR_MESSAGE);
    }

    public static String showInputDialog(String message, String title) {
        return JOptionPane.showInputDialog(Gui.instance.getFrame(), message, title, JOptionPane.PLAIN_MESSAGE);
    }

    public static boolean showConfirmDialog(String message, String title) {
        int option = JOptionPane.showConfirmDialog(Gui.instance.getFrame(), message, title, JOptionPane.YES_NO_OPTION,
            JOptionPane.QUESTION_MESSAGE);

        return option == JOptionPane.OK_OPTION;
    }

    public JFrame getFrame() {
        return frame;
    }
}
