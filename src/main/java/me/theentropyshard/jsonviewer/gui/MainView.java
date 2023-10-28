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
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
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

        this.controlsPanel = new ControlsPanel();
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

    public void newTab() {
        JsonView jsonView = new JsonView();
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
    }

    private void setJsonText(String json) {
        JsonView view = this.getCurrentView();

        int tSize = json.getBytes(StandardCharsets.UTF_8).length;
        view.setSizeInKBs(Utils.round(tSize / 1000.0, 2));

        view.setText(json);
        view.scrollToTop();
    }

    private void onFileButtonPressed(ActionEvent e) {
        SwingUtils.startWorker(() -> {
            JsonView view = this.getCurrentView();

            JFileChooser fileChooser = new JFileChooser(System.getProperty("user.dir"));
            fileChooser.setFileFilter(new FileNameExtensionFilter("JSON Files (*.json)", "json"));

            if (fileChooser.showOpenDialog(this.gui.getFrame()) != JFileChooser.APPROVE_OPTION) {
                return;
            }

            File selectedFile = fileChooser.getSelectedFile();
            if (selectedFile == null) {
                return;
            }

            String fileText = "[]";
            try {
                fileText = Utils.readFile(selectedFile);
            } catch (IOException ex) {
                Gui.showErrorDialog("Unable to load JSON from File");
                ex.printStackTrace();
            }

            this.setJsonText(fileText);
            this.names.get(view).setText(selectedFile.getName());
        });
    }

    private void onUrlButtonPressed(ActionEvent e) {
        SwingUtils.startWorker(() -> {
            JsonView view = this.getCurrentView();

            String input = Gui.showInputDialog("Enter the URL", "Url");
            if (input.isEmpty()) {
                return;
            }

            if (Utils.isUrlInvalid(input)) {
                Gui.showErrorDialog("Invalid URL: " + input);
                return;
            }

            String urlText = "[]";
            try {
                urlText = Utils.readURL(input);
            } catch (NonJsonContentTypeException ex) {
                Gui.showErrorDialog("Expected JSON, but got " + ex.getMessage());
            } catch (IOException ex) {
                Gui.showErrorDialog("Unable to load JSON from URL");
                ex.printStackTrace();
            }

            this.setJsonText(urlText);
            this.names.get(view).setText(Utils.getLastPathComponent(input));
        });
    }

    private void onTreeViewerButtonPressed(ActionEvent e) {
        JsonView view = this.getCurrentView();
        view.switchToTreeView();

        JsonElement rootElement = new Gson().fromJson(view.getText(), JsonElement.class);

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

        int selectedIndex = this.controlsPanel.getIndentCombo().getSelectedIndex();
        String formattedJson = this.jsonService.formatJson(view.getText(), selectedIndex + 1);
        this.setJsonText(formattedJson);
    }

    private void onIndentComboSelected(ItemEvent e) {
        if (e.getStateChange() != ItemEvent.SELECTED) {
            return;
        }

        int selectedIndex = this.controlsPanel.getIndentCombo().getSelectedIndex();
        this.jsonViewer.getConfig().setValue("beautifySpace", String.valueOf(selectedIndex));

        JsonView view = this.getCurrentView();
        this.setJsonText(this.jsonService.formatJson(view.getText(), selectedIndex + 1));
    }

    private void onMinifyButtonPressed(ActionEvent e) {
        JsonView view = this.getCurrentView();
        view.switchToTextView();

        String minifiedJson = this.jsonService.minifyJson(view.getText());
        this.setJsonText(minifiedJson);
    }
}
