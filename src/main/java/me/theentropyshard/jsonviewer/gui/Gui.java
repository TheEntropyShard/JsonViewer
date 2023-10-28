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
import com.formdev.flatlaf.FlatIntelliJLaf;
import com.google.gson.Gson;
import com.google.gson.JsonElement;
import me.theentropyshard.jsonviewer.JTreeBuilder;
import me.theentropyshard.jsonviewer.JsonFormatter;
import me.theentropyshard.jsonviewer.JsonViewer;
import me.theentropyshard.jsonviewer.utils.Utils;
import me.theentropyshard.jsonviewer.exception.NonJsonContentTypeException;
import me.theentropyshard.jsonviewer.utils.SwingUtils;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.filechooser.FileNameExtensionFilter;
import javax.swing.plaf.LayerUI;
import javax.swing.tree.DefaultTreeModel;
import java.awt.*;
import java.awt.event.*;
import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;
import java.util.function.IntConsumer;

public class Gui {
    public static final int BUTTON_HEIGHT = 35;

    private final JFrame frame;
    private final JPanel root;
    private final JTabbedPane views;

    private final Map<JPanel, JLabel> titles;
    private final Map<JsonView, JLabel> names;

    private int tabCounter;

    public Gui(JsonViewer jsonViewer) {
        this.initGui();

        this.titles = new HashMap<>();
        this.names = new HashMap<>();

        this.frame = new JFrame("JsonViewer");
        this.frame.addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                try {
                    jsonViewer.getConfig().save();
                } catch (IOException ex) {
                    System.err.println("Unable to save config");
                    ex.printStackTrace();
                }
            }
        });

        this.root = new JPanel(new BorderLayout());
        this.root.setPreferredSize(new Dimension(1088, 576));

        this.views = new JTabbedPane(JTabbedPane.TOP);
        this.views.putClientProperty(FlatClientProperties.TABBED_PANE_TAB_CLOSABLE, Boolean.TRUE);
        this.views.putClientProperty(FlatClientProperties.TABBED_PANE_TAB_CLOSE_CALLBACK, (IntConsumer) tab -> {
            this.tabCounter--;
            this.views.removeTabAt(tab);
        });
        this.views.setBorder(new EmptyBorder(3, 0, 3, 3));
        this.newTab();

        JMenuBar menuBar = new JMenuBar();
        this.frame.setJMenuBar(menuBar);

        JMenu fileMenu = new JMenu("File");
        menuBar.add(fileMenu);

        JMenuItem newTabItem = new JMenuItem("New tab");
        newTabItem.addActionListener(e -> this.newTab());
        fileMenu.add(newTabItem);

        JPanel borderPanel = new JPanel(new BorderLayout());
        JPanel leftPanel = new JPanel(new GridLayout(0, 1, 0, 1));
        borderPanel.add(leftPanel, BorderLayout.PAGE_START);

        JPanel sourceButtons = new JPanel(new FlowLayout(FlowLayout.CENTER, 0, 0));

        JButton file = new JButton("File");
        file.setActionCommand("fromfile");
        file.setPreferredSize(new Dimension(file.getPreferredSize().width, Gui.BUTTON_HEIGHT));
        sourceButtons.add(file, BorderLayout.WEST);

        JButton url = new JButton("URL");
        url.setActionCommand("fromurl");
        url.setPreferredSize(new Dimension(url.getPreferredSize().width, Gui.BUTTON_HEIGHT));
        sourceButtons.add(url, BorderLayout.EAST);

        leftPanel.add(sourceButtons, BorderLayout.PAGE_START);

        JButton treeViewer = new JButton("Tree Viewer");
        treeViewer.setActionCommand("treeview");
        treeViewer.setPreferredSize(new Dimension(treeViewer.getPreferredSize().width, Gui.BUTTON_HEIGHT));
        leftPanel.add(treeViewer);

        JButton beautify = new JButton("Beautify");
        beautify.setActionCommand("beautify");
        beautify.setPreferredSize(new Dimension(beautify.getPreferredSize().width, Gui.BUTTON_HEIGHT));
        leftPanel.add(beautify);

        JComboBox<String> comp = new JComboBox<>(new String[]{"1 Space", "2 Space", "3 Space", "4 Space"});
        comp.addItemListener(e -> {
            if (e.getStateChange() == ItemEvent.SELECTED) {
                jsonViewer.getConfig().setValue("beautifySpace", String.valueOf(comp.getSelectedIndex()));
            }
        });

        int space = Integer.parseInt(jsonViewer.getConfig().getValue("beautifySpace", "3"));
        if (space < 0 || space > 3) {
            space = 3;
        }
        comp.setSelectedIndex(space);
        comp.setPreferredSize(new Dimension(comp.getPreferredSize().width, Gui.BUTTON_HEIGHT));
        leftPanel.add(comp);

        JButton minify = new JButton("Minify");
        minify.setActionCommand("minify");
        minify.setPreferredSize(new Dimension(minify.getPreferredSize().width, Gui.BUTTON_HEIGHT));
        leftPanel.add(minify);

        leftPanel.setBorder(new EmptyBorder(3, 3, 3, 3));

        ActionListener buttonListener = e -> {
            JsonView view = this.getCurrentView();
            String text = view.getText();

            if (!JsonFormatter.isJsonValid(text)) {
                JOptionPane.showMessageDialog(this.frame, "JSON is not valid", "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }

            switch (e.getActionCommand()) {
                case "beautify":
                    view.switchToTextView();
                    String beautyText = JsonFormatter.formatJson(text, comp.getSelectedIndex() + 1);

                    if (beautyText == null) {
                        return;
                    }

                    int textSize = beautyText.getBytes(StandardCharsets.UTF_8).length;
                    view.setSizeInKBs(Utils.round(textSize / 1000.0, 2));
                    view.setText(beautyText);
                    break;
                case "minify":
                    view.switchToTextView();
                    String minifiedText = JsonFormatter.minifyJson(text);
                    int size = minifiedText.getBytes(StandardCharsets.UTF_8).length;
                    view.setSizeInKBs(Utils.round(size / 1000.0, 2));
                    view.setText(minifiedText);
                    break;
                case "treeview":
                    view.switchToTreeView();
                    JsonElement root = new Gson().fromJson(text, JsonElement.class);

                    if (root == null) {
                        break;
                    }

                    String name;
                    if (root.isJsonObject()) {
                        name = "object";
                    } else if (root.isJsonArray()) {
                        name = "array [" + root.getAsJsonArray().size() + "]";
                    } else {
                        name = "unknown";
                    }
                    view.setModel(new DefaultTreeModel(JTreeBuilder.buildTree(name, root)));
                    break;
                case "fromfile":
                    view.switchToTextView();
                    SwingUtils.startWorker(() -> {
                        JFileChooser fileChooser = new JFileChooser(System.getProperty("user.dir"));
                        fileChooser.setFileFilter(new FileNameExtensionFilter("JSON Files (*.json)", "json"));

                        if (fileChooser.showOpenDialog(this.frame) != JFileChooser.APPROVE_OPTION) {
                            return;
                        }

                        File selectedFile = fileChooser.getSelectedFile();
                        if (selectedFile == null) {
                            return;
                        }

                        try {
                            String fileText = Utils.readFile(selectedFile);

                            int tSize = fileText.getBytes(StandardCharsets.UTF_8).length;
                            view.setSizeInKBs(Utils.round(tSize / 1000.0, 2));

                            view.setText(fileText);
                            this.names.get(view).setText(selectedFile.getName());
                        } catch (IOException ex) {
                            JOptionPane.showMessageDialog(this.frame, "Unable to load JSON from File", "Error", JOptionPane.ERROR_MESSAGE);
                            ex.printStackTrace();
                        }
                    });
                    break;
                case "fromurl":
                    view.switchToTextView();
                    SwingUtils.startWorker(() -> {
                        String input = JOptionPane.showInputDialog(this.frame, "Enter the URL", "Url", JOptionPane.PLAIN_MESSAGE);
                        if (input.isEmpty()) {
                            return;
                        }

                        if (Utils.isUrlInvalid(input)) {
                            JOptionPane.showMessageDialog(this.frame, "Invalid URL: " + input, "Error", JOptionPane.ERROR_MESSAGE);
                            return;
                        }

                        try {
                            String urlText = Utils.readURL(input);

                            int tSize = urlText.getBytes(StandardCharsets.UTF_8).length;
                            view.setSizeInKBs(Utils.round(tSize / 1000.0, 2));

                            view.setText(urlText);
                            this.names.get(view).setText(Utils.getLastPathComponent(input));
                        } catch (NonJsonContentTypeException ex) {
                            JOptionPane.showMessageDialog(this.frame, "Expected JSON, but got " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
                        } catch (IOException ex) {
                            JOptionPane.showMessageDialog(this.frame, "Unable to load JSON from URL", "Error", JOptionPane.ERROR_MESSAGE);
                            ex.printStackTrace();
                        }
                    });
                    break;
            }

            view.scrollToTop();
        };

        file.addActionListener(buttonListener);
        url.addActionListener(buttonListener);
        beautify.addActionListener(buttonListener);
        minify.addActionListener(buttonListener);
        treeViewer.addActionListener(buttonListener);

        this.root.add(this.views, BorderLayout.CENTER);
        this.root.add(borderPanel, BorderLayout.WEST);

        this.frame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        this.frame.add(this.root, BorderLayout.CENTER);
        this.frame.pack();
        SwingUtils.centerWindow(this.frame, 0);
        this.frame.setVisible(true);
    }

    public JsonView getCurrentView() {
        int selectedIndex = this.views.getSelectedIndex();
        if (selectedIndex == -1) {
            JOptionPane.showMessageDialog(this.frame, "There are no open tabs", "Error", JOptionPane.ERROR_MESSAGE);
            return null;
        }

        return (JsonView) this.views.getComponentAt(selectedIndex);
    }

    public void newTab() {
        JsonView jsonView = new JsonView();
        this.views.addTab(null, jsonView);
        JPanel tabComponent = new JPanel();
        JLabel label = new JLabel("Tab " + ++this.tabCounter);
        label.setOpaque(false);
        tabComponent.add(label);
        tabComponent.setOpaque(false);
        tabComponent.addMouseListener(new MouseAdapter() {
            private void myPopupEvent(MouseEvent e) {
                int x = e.getX();
                int y = e.getY();

                JPanel panel = (JPanel) e.getSource();

                JPopupMenu popup = new JPopupMenu();

                JMenuItem renameItem = new JMenuItem("Rename");
                renameItem.addActionListener(al -> {
                    String newName = JOptionPane.showInputDialog(Gui.this.frame, "Enter new name", "Rename", JOptionPane.PLAIN_MESSAGE);

                    if (newName == null || newName.isEmpty()) {
                        return;
                    }

                    Gui.this.titles.get(panel).setText(newName);
                });

                popup.add(renameItem);

                popup.show(panel, x, y);
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

        this.titles.put(tabComponent, label);
        this.names.put(jsonView, label);

        JLayer<JPanel> component = new JLayer<>(tabComponent, new DispatchEventLayerUI());
        this.views.setTabComponentAt(this.views.indexOfComponent(jsonView), component);
    }

    private void initGui() {
        UIManager.put("FileChooser.readOnly", Boolean.TRUE);
        JDialog.setDefaultLookAndFeelDecorated(true);
        JFrame.setDefaultLookAndFeelDecorated(true);
        FlatIntelliJLaf.setup();
    }

    // https://stackoverflow.com/a/38525967/19857533
    static class DispatchEventLayerUI extends LayerUI<JPanel> {
        @Override
        public void installUI(JComponent c) {
            super.installUI(c);
            if (c instanceof JLayer) {
                ((JLayer<?>) c).setLayerEventMask(AWTEvent.MOUSE_EVENT_MASK);
            }
        }

        @Override
        public void uninstallUI(JComponent c) {
            if (c instanceof JLayer) {
                ((JLayer<?>) c).setLayerEventMask(0);
            }
            super.uninstallUI(c);
        }

        @Override
        protected void processMouseEvent(MouseEvent e, JLayer<? extends JPanel> l) {
            this.dispatchEvent(e);
        }

        private void dispatchEvent(MouseEvent e) {
            Component src = e.getComponent();
            Container tgt = SwingUtilities.getAncestorOfClass(JTabbedPane.class, src);
            tgt.dispatchEvent(SwingUtilities.convertMouseEvent(src, e, tgt));
        }
    }
}
