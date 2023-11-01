/*
 * Copyright 2023 TheEntropyShard (https://github.com/TheEntropyShard)
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package me.theentropyshard.jsonviewer.gui;

import com.formdev.flatlaf.FlatClientProperties;
import com.google.gson.Gson;
import com.google.gson.JsonElement;
import me.theentropyshard.jsonviewer.JsonViewer;
import me.theentropyshard.jsonviewer.exception.NonJsonContentTypeException;
import me.theentropyshard.jsonviewer.json.JTreeBuilder;
import me.theentropyshard.jsonviewer.json.JsonService;
import me.theentropyshard.jsonviewer.utils.SwingUtils;
import me.theentropyshard.jsonviewer.utils.Utils;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.filechooser.FileNameExtensionFilter;
import javax.swing.tree.DefaultTreeModel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ItemEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.IntConsumer;

public class MainView extends JPanel {
    private final JsonViewer jsonViewer;
    private final JsonService jsonService;
    private final Gui gui;

    private final Map<JPanel, JLabel> titles;
    private final Map<JsonView, JLabel> names;

    private final ControlsPanel controlsPanel;
    private final JTabbedPane viewSelector;

    private int tabCounter;

    public MainView(JsonViewer jsonViewer, Gui gui) {
        super(new BorderLayout());

        this.jsonViewer = jsonViewer;
        this.jsonService = jsonViewer.getJsonService();
        this.gui = gui;

        this.titles = new HashMap<>();
        this.names = new HashMap<>();

        this.controlsPanel = new ControlsPanel(jsonViewer);
        this.controlsPanel.getFileButton().addActionListener(this::onFileButtonPressed);
        this.controlsPanel.getUrlButton().addActionListener(this::onUrlButtonPressed);
        this.controlsPanel.getTreeViewerButton().addActionListener(this::onTreeViewerButtonPressed);
        this.controlsPanel.getBeautifyButton().addActionListener(this::onBeautifyButtonPressed);
        this.controlsPanel.getIndentCombo().addItemListener(this::onIndentComboSelected);
        this.controlsPanel.getMinifyButton().addActionListener(this::onMinifyButtonPressed);

        this.viewSelector = new JTabbedPane(JTabbedPane.TOP);
        this.viewSelector.setBorder(new EmptyBorder(3, 0, 3, 3));
        this.viewSelector.putClientProperty(FlatClientProperties.TABBED_PANE_TAB_CLOSABLE, Boolean.TRUE);
        this.viewSelector.putClientProperty(FlatClientProperties.TABBED_PANE_TAB_CLOSE_CALLBACK, (IntConsumer) tab -> {
            this.tabCounter--;
            this.viewSelector.removeTabAt(tab);
        });

        this.newTab();

        this.setPreferredSize(new Dimension(1088, 576));
        this.add(this.viewSelector, BorderLayout.CENTER);
        this.add(this.controlsPanel, BorderLayout.WEST);
    }

    public JsonView getCurrentView() {
        int selectedIndex = this.viewSelector.getSelectedIndex();
        if (selectedIndex == -1) {
            Gui.showErrorDialog("There are no open tabs");
            return null;
        }

        return (JsonView) this.viewSelector.getComponentAt(selectedIndex);
    }

    public void addRecentUrl(String url) {
        List<String> recentUrls = this.jsonViewer.getConfig().getRecentUrls();

        if (recentUrls.contains(url)) {
            return;
        }

        recentUrls.add(url);
        this.gui.addRecentUrl(url);
    }

    public void addRecentFile(String path) {
        List<String> recentFiles = this.jsonViewer.getConfig().getRecentFiles();

        if (recentFiles.contains(path)) {
            return;
        }

        recentFiles.add(path);
        this.gui.addRecentFile(path);
    }

    public void addTab(File file) {
        JsonView view = this.newTab();
        this.getFromFile(file, view);
    }

    public void addTab(String url) {
        JsonView view = this.newTab();
        this.getFromUrl(url, view);
    }

    public JsonView newTab() {
        JsonView jsonView = new JsonView(this);
        this.viewSelector.addTab(null, jsonView);

        JLabel tabTitle = new JLabel("Tab " + ++this.tabCounter);
        tabTitle.setOpaque(false);
        this.names.put(jsonView, tabTitle);

        JPanel tabComponent = new JPanel();
        tabComponent.add(tabTitle);
        tabComponent.setOpaque(false);
        tabComponent.addMouseListener(new MouseAdapter() {
            private void myPopupEvent(MouseEvent e) {
                JPanel panel = (JPanel) e.getSource();

                JPopupMenu popup = new JPopupMenu();

                JMenuItem renameItem = new JMenuItem("Rename");
                renameItem.addActionListener(al -> {
                    String newName = Gui.showInputDialog("Enter new name", "Rename");

                    if (newName == null || newName.isEmpty()) {
                        return;
                    }

                    MainView.this.titles.get(panel).setText(newName);
                });

                popup.add(renameItem);
                popup.show(panel, e.getX(), e.getY());
            }

            public void mousePressed(MouseEvent e) {
                if (e.isPopupTrigger()) {
                    this.myPopupEvent(e);
                }
            }

            public void mouseReleased(MouseEvent e) {
                if (e.isPopupTrigger()) {
                    this.myPopupEvent(e);
                }
            }
        });

        this.titles.put(tabComponent, tabTitle);

        this.viewSelector.setTabComponentAt(
                this.viewSelector.indexOfComponent(jsonView),
                new JLayer<>(tabComponent, new DispatchEventLayerUI())
        );

        return jsonView;
    }

    public void getFromUrl(String url, JsonView view) {
        if (url.isEmpty()) {
            return;
        }

        if (Utils.isUrlInvalid(url)) {
            Gui.showErrorDialog("Invalid URL: " + url);
            return;
        }

        String urlText = "[]";
        try {
            urlText = Utils.readURL(url);
        } catch (NonJsonContentTypeException ex) {
            Gui.showErrorDialog("Expected JSON, but got " + ex.getMessage());
        } catch (IOException ex) {
            Gui.showErrorDialog("Unable to load JSON from URL");
            ex.printStackTrace();
        }

        if (!this.jsonService.isJsonValid(urlText)) {
            Gui.showErrorDialog("Got invalid JSON from Url. Check console.");
            System.err.println(urlText);
            return;
        }

        this.addRecentUrl(url);

        if (view == null) {
            view = this.getCurrentView();
        }

        this.setJsonText(urlText, view);
        this.names.get(view).setText(Utils.getLastPathComponent(url));
    }

    public void getFromFile(File file, JsonView view) {
        if (!file.exists()) {
            boolean remove = Gui.showConfirmDialog("File '" + file.getAbsolutePath() +
                            " does not exist. Remove from recent files?", "File does not exist");
            if (remove) {
                this.jsonViewer.getConfig().getRecentFiles().remove(file.getAbsolutePath());
                this.gui.removeRecentFile(file.getAbsolutePath());
            }

            return;
        }

        String fileText = "[]";
        try {
            fileText = Utils.readFile(file);
        } catch (IOException ex) {
            Gui.showErrorDialog("Unable to load JSON from File");
            ex.printStackTrace();
        }

        if (!this.jsonService.isJsonValid(fileText)) {
            Gui.showErrorDialog("Got invalid JSON from File. Check console.");
            System.err.println(fileText);
            return;
        }

        this.addRecentFile(file.getAbsolutePath());

        if (view == null) {
            view = this.getCurrentView();
        }

        this.setJsonText(fileText, view);
        this.names.get(view).setText(file.getName());
    }

    private void setJsonText(String json, JsonView view) {
        if (view == null) {
            view = this.getCurrentView();
        }

        int tSize = json.getBytes(StandardCharsets.UTF_8).length;
        view.setSizeInKBs(Utils.round(tSize / 1000.0, 2));

        view.setText(json);
        view.scrollToTop();
    }

    private void onFileButtonPressed(ActionEvent e) {
        SwingUtils.startWorker(() -> {
            JFileChooser fileChooser = new JFileChooser(System.getProperty("user.dir"));
            fileChooser.setFileFilter(new FileNameExtensionFilter("JSON Files (*.json)", "json"));

            if (fileChooser.showOpenDialog(this.gui.getFrame()) != JFileChooser.APPROVE_OPTION) {
                return;
            }

            File selectedFile = fileChooser.getSelectedFile();
            if (selectedFile == null) {
                return;
            }

            this.getFromFile(selectedFile, null);
        });
    }

    private void onUrlButtonPressed(ActionEvent e) {
        SwingUtils.startWorker(() -> {
            String input = Gui.showInputDialog("Enter the URL", "Url");

            this.getFromUrl(input, null);
        });
    }

    private void onTreeViewerButtonPressed(ActionEvent e) {
        JsonView view = this.getCurrentView();
        view.switchToTreeView();

        String text = view.getText();

        if (!this.jsonService.isJsonValid(text)) {
            Gui.showErrorDialog("Got invalid JSON. Check console.");
            System.err.println(text);
            return;
        }

        JsonElement rootElement = new Gson().fromJson(text, JsonElement.class);

        if (rootElement == null) {
            return;
        }

        String name;
        if (rootElement.isJsonObject()) {
            name = "object";
        } else if (rootElement.isJsonArray()) {
            name = "array [" + rootElement.getAsJsonArray().size() + "]";
        } else {
            name = "unknown";
        }

        view.setModel(new DefaultTreeModel(JTreeBuilder.buildTree(name, rootElement)));
    }

    private void onBeautifyButtonPressed(ActionEvent e) {
        JsonView view = this.getCurrentView();
        view.switchToTextView();

        String text = view.getText();
        if (!this.jsonService.isJsonValid(text)) {
            Gui.showErrorDialog("Got invalid JSON. Check console.");
            System.err.println(text);
            return;
        }

        int selectedIndex = this.controlsPanel.getIndentCombo().getSelectedIndex();
        String formattedJson = this.jsonService.formatJson(text, selectedIndex + 1);
        this.setJsonText(formattedJson, view);
    }

    private void onIndentComboSelected(ItemEvent e) {
        if (e.getStateChange() != ItemEvent.SELECTED) {
            return;
        }

        int selectedIndex = this.controlsPanel.getIndentCombo().getSelectedIndex();
        this.jsonViewer.getConfig().setBeautifyIndent(selectedIndex);

        JsonView view = this.getCurrentView();
        String text = view.getText();

        if (!this.jsonService.isJsonValid(text)) {
            Gui.showErrorDialog("Got invalid JSON. Check console.");
            System.err.println(text);
            return;
        }

        this.setJsonText(this.jsonService.formatJson(text, selectedIndex + 1), view);
    }

    private void onMinifyButtonPressed(ActionEvent e) {
        JsonView view = this.getCurrentView();
        view.switchToTextView();

        String text = view.getText();

        if (!this.jsonService.isJsonValid(text)) {
            Gui.showErrorDialog("Got invalid JSON. Check console.");
            System.err.println(text);
            return;
        }

        String minifiedJson = this.jsonService.minifyJson(text);
        this.setJsonText(minifiedJson, view);
    }
}
