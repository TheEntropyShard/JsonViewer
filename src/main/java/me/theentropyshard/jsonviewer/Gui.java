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

package me.theentropyshard.jsonviewer;

import com.formdev.flatlaf.FlatIntelliJLaf;
import com.google.gson.Gson;
import com.google.gson.JsonElement;
import org.fife.ui.rsyntaxtextarea.RSyntaxTextArea;
import org.fife.ui.rsyntaxtextarea.SyntaxScheme;
import org.fife.ui.rsyntaxtextarea.Token;
import org.fife.ui.rtextarea.RTextScrollPane;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.filechooser.FileNameExtensionFilter;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.TreeNode;
import javax.swing.tree.TreePath;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.File;
import java.util.Enumeration;

public class Gui {
    private final CardLayout cardLayout;
    private final JPanel root;
    private final JPanel view;

    private final RSyntaxTextArea textArea;
    private final JTree jsonTree;

    public Gui() {
        this.initGui();

        JFrame frame = new JFrame("JsonViewer");

        this.root = new JPanel(new BorderLayout());
        this.root.setPreferredSize(new Dimension(1088, 576));

        this.cardLayout = new CardLayout();
        this.view = new JPanel(this.cardLayout);

        JPanel borderPanel = new JPanel(new BorderLayout());
        JPanel leftPanel = new JPanel(new GridLayout(0, 1, 0, 1));
        borderPanel.add(leftPanel, BorderLayout.PAGE_START);

        int height = 35;

        JPanel sourceButtons = new JPanel(new FlowLayout(FlowLayout.CENTER, 0, 0));

        JButton file = new JButton("File");
        file.setPreferredSize(new Dimension(file.getPreferredSize().width, height));
        sourceButtons.add(file, BorderLayout.WEST);
        JButton url = new JButton("URL");
        url.setPreferredSize(new Dimension(url.getPreferredSize().width, height));
        sourceButtons.add(url, BorderLayout.EAST);
        leftPanel.add(sourceButtons, BorderLayout.PAGE_START);

        JButton treeViewer = new JButton("Tree Viewer");
        treeViewer.setPreferredSize(new Dimension(treeViewer.getPreferredSize().width, height));
        leftPanel.add(treeViewer);
        JButton beautify = new JButton("Beautify");

        beautify.setPreferredSize(new Dimension(beautify.getPreferredSize().width, height));
        leftPanel.add(beautify);
        JComboBox<String> comp = new JComboBox<>(new String[]{"1 Space", "2 Space", "3 Space", "4 Space"});
        comp.setSelectedIndex(3);
        comp.setPreferredSize(new Dimension(comp.getPreferredSize().width, height));
        leftPanel.add(comp);
        JButton minify = new JButton("Minify");
        minify.setPreferredSize(new Dimension(minify.getPreferredSize().width, height));
        leftPanel.add(minify);

        leftPanel.setBorder(new EmptyBorder(3, 3, 3, 3));

        this.textArea = new RSyntaxTextArea();
        this.textArea.setLineWrap(true);
        this.textArea.setBracketMatchingEnabled(true);
        this.textArea.setShowMatchedBracketPopup(true);
        //this.textArea.setEditable(false);
        this.textArea.setSyntaxEditingStyle(RSyntaxTextArea.SYNTAX_STYLE_JSON);
        RTextScrollPane scrollPane = new RTextScrollPane(this.textArea, true);

        this.setSyntaxScheme();

        file.addActionListener(e -> {
            new SwingWorker<Void, Void>() {
                @Override
                protected Void doInBackground() throws Exception {
                    Gui.this.cardLayout.show(Gui.this.view, "textarea");

                    JFileChooser fileChooser = new JFileChooser(System.getProperty("user.dir"));

                    fileChooser.setFileFilter(new FileNameExtensionFilter("JSON Files (*.json)", "json"));
                    int option = fileChooser.showOpenDialog(frame);
                    if (option == JFileChooser.APPROVE_OPTION) {
                        File selectedFile = fileChooser.getSelectedFile();
                        if (selectedFile == null) {
                            return null;
                        }

                        Gui.this.textArea.setText(Utils.readFile(selectedFile));
                    }

                    return null;
                }
            }.execute();
        });

        url.addActionListener(e -> {
            new SwingWorker<Void, Void>() {
                @Override
                protected Void doInBackground() throws Exception {
                    Gui.this.cardLayout.show(Gui.this.view, "textarea");

                    String input = JOptionPane.showInputDialog(frame, "Enter the URL");
                    Gui.this.textArea.setText(Utils.readURL(input));

                    return null;
                }
            }.execute();
        });

        beautify.addActionListener(e -> {
            this.cardLayout.show(this.view, "textarea");

            String text = this.textArea.getText();

            if (!JsonFormatter.isJsonValid(text)) {
                JOptionPane.showMessageDialog(frame, "JSON is not valid", "Error", JOptionPane.ERROR_MESSAGE);

                return;
            }

            this.textArea.setText(JsonFormatter.formatJson(text, comp.getSelectedIndex() + 1));
            this.textArea.setCaretPosition(0);
        });

        minify.addActionListener(e -> {
            this.cardLayout.show(this.view, "textarea");

            String text = this.textArea.getText();

            if (!JsonFormatter.isJsonValid(text)) {
                JOptionPane.showMessageDialog(frame, "JSON is not valid", "Error", JOptionPane.ERROR_MESSAGE);

                return;
            }

            this.textArea.setText(JsonFormatter.minifyJson(text));
            this.textArea.setCaretPosition(0);
        });

        this.view.add(scrollPane, "textarea");

        this.jsonTree = new JTree();
        this.jsonTree.setShowsRootHandles(true);
        this.jsonTree.setRootVisible(true);
        JScrollPane treeScrollPane = new JScrollPane(this.jsonTree);

        MouseAdapter ma = new MouseAdapter() {
            // http://www.java2s.com/example/java/swing/expand-all-jtree-children-node.html
            public void expandAllChildren(JTree tree, TreePath parent,
                                          boolean expand) {
                TreeNode node = (TreeNode) parent.getLastPathComponent();
                if (node.getChildCount() > 0) {
                    Enumeration e = node.children();
                    while (e.hasMoreElements()) {
                        TreeNode n = (TreeNode) e.nextElement();
                        TreePath path = parent.pathByAddingChild(n);
                        this.expandAllChildren(tree, path, expand);
                    }
                }

                if (expand) {
                    tree.expandPath(parent);
                } else {
                    tree.collapsePath(parent);
                }
            }

            private void myPopupEvent(MouseEvent e) {
                int x = e.getX();
                int y = e.getY();
                JTree tree = (JTree) e.getSource();
                TreePath path = tree.getPathForLocation(x, y);
                if (path == null)
                    return;

                tree.setSelectionPath(path);

                JPopupMenu popup = new JPopupMenu();
                JMenuItem expandItem = new JMenuItem("Expand");
                expandItem.addActionListener(ae -> {
                    //jsonTree.expandPath(jsonTree.getSelectionPath());
                    this.expandAllChildren(jsonTree, jsonTree.getSelectionPath(), true);
                });
                popup.add(expandItem);
                JMenuItem collapseItem = new JMenuItem("Collapse");
                collapseItem.addActionListener(ae -> {
                    this.expandAllChildren(jsonTree, jsonTree.getSelectionPath(), false);
                });
                popup.add(collapseItem);
                popup.show(tree, x, y);
            }

            public void mousePressed(MouseEvent e) {
                if (e.isPopupTrigger()) this.myPopupEvent(e);
            }

            public void mouseReleased(MouseEvent e) {
                if (e.isPopupTrigger()) this.myPopupEvent(e);
            }
        };
        this.jsonTree.addMouseListener(ma);

        this.view.add(treeScrollPane, "treeview");

        treeViewer.addActionListener(e -> {
            this.cardLayout.show(this.view, "treeview");

            String text = this.textArea.getText();

            if (!JsonFormatter.isJsonValid(text)) {
                JOptionPane.showMessageDialog(frame, "JSON is not valid", "Error", JOptionPane.ERROR_MESSAGE);

                return;
            }

            this.jsonTree.setModel(new DefaultTreeModel(JTreeBuilder.buildTree("object", new Gson().fromJson(text, JsonElement.class))));
        });

        this.cardLayout.show(this.view, "textarea");

        this.root.add(this.view, BorderLayout.CENTER);
        this.root.add(borderPanel, BorderLayout.WEST);


        frame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        frame.add(this.root, BorderLayout.CENTER);
        frame.pack();
        Utils.centerWindow(frame, 0);
        frame.setVisible(true);
    }

    private void initGui() {
        UIManager.put("FileChooser.readOnly", Boolean.TRUE);
        JDialog.setDefaultLookAndFeelDecorated(true);
        JFrame.setDefaultLookAndFeelDecorated(true);
        FlatIntelliJLaf.setup();
    }

    private void setSyntaxScheme() {
        SyntaxScheme scheme = this.textArea.getSyntaxScheme();

        scheme.getStyle(Token.LITERAL_STRING_DOUBLE_QUOTE).foreground = Color.decode("#2a9152");
        scheme.getStyle(Token.SEPARATOR).foreground = Color.GRAY;
        scheme.getStyle(Token.VARIABLE).foreground = Color.DARK_GRAY;

        this.textArea.setFont(this.textArea.getFont().deriveFont(14.0f));

        this.textArea.revalidate();
    }
}
