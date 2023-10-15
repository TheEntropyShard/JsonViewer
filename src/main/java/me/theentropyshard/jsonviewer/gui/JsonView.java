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

import org.fife.ui.rsyntaxtextarea.RSyntaxTextArea;
import org.fife.ui.rsyntaxtextarea.SyntaxScheme;
import org.fife.ui.rsyntaxtextarea.Token;
import org.fife.ui.rtextarea.RTextScrollPane;

import javax.swing.*;
import javax.swing.tree.TreeModel;
import javax.swing.tree.TreeNode;
import javax.swing.tree.TreePath;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.Enumeration;

public class JsonView extends JPanel {
    private final CardLayout cardLayout;
    private final RSyntaxTextArea textArea;
    private final JTree jsonTree;

    public JsonView() {
        this.cardLayout = new CardLayout();
        this.setLayout(this.cardLayout);

        this.textArea = this.makeTextArea();
        RTextScrollPane textScrollPane = new RTextScrollPane(this.textArea);
        this.add(textScrollPane, "textView");

        this.jsonTree = this.makeJsonTree();
        JScrollPane treeScrollPane = new JScrollPane(this.jsonTree);
        this.add(treeScrollPane, "treeView");
    }

    private RSyntaxTextArea makeTextArea() {
        RSyntaxTextArea textArea = new RSyntaxTextArea();
        textArea.setLineWrap(true);
        textArea.setBracketMatchingEnabled(true);
        textArea.setShowMatchedBracketPopup(true);
        textArea.setSyntaxEditingStyle(RSyntaxTextArea.SYNTAX_STYLE_JSON);

        SyntaxScheme scheme = textArea.getSyntaxScheme();
        scheme.getStyle(Token.LITERAL_STRING_DOUBLE_QUOTE).foreground = Color.decode("#2a9152");
        scheme.getStyle(Token.SEPARATOR).foreground = Color.GRAY;
        scheme.getStyle(Token.VARIABLE).foreground = Color.DARK_GRAY;

        textArea.setFont(textArea.getFont().deriveFont(14.0f));
        textArea.revalidate();

        return textArea;
    }

    private JTree makeJsonTree() {
        JTree jsonTree = new JTree();
        jsonTree.setShowsRootHandles(true);
        jsonTree.setRootVisible(true);

        MouseAdapter ma = new MouseAdapter() {
            // http://www.java2s.com/example/java/swing/expand-all-jtree-children-node.html
            public void expandAllChildren(JTree tree, TreePath parent, boolean expand) {
                if (parent == null) {
                    return;
                }

                TreeNode node = (TreeNode) parent.getLastPathComponent();
                if (node.getChildCount() > 0) {
                    @SuppressWarnings("rawtypes")
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
                if (path == null) {
                    return;
                }

                tree.setSelectionPath(path);

                JPopupMenu popup = new JPopupMenu();

                JMenuItem expandItem = new JMenuItem("Expand");
                expandItem.addActionListener(ae -> {
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
                if (e.isPopupTrigger()) {
                    this.myPopupEvent(e);
                }
            }

            public void mouseReleased(MouseEvent e) {
                if (e.isPopupTrigger()) {
                    this.myPopupEvent(e);
                }
            }
        };
        jsonTree.addMouseListener(ma);

        return jsonTree;
    }

    public void scrollToTop() {
        this.textArea.setCaretPosition(0);
    }

    public void switchToTextView() {
        this.cardLayout.show(this, "textView");
    }

    public void switchToTreeView() {
        this.cardLayout.show(this, "treeView");
    }

    public void changeFontSize(int size) {
        this.textArea.setFont(this.textArea.getFont().deriveFont((float) size));
    }

    public void setText(String text) {
        this.textArea.setText(text);
    }

    public String getText() {
        return this.textArea.getText();
    }

    public void setModel(TreeModel model) {
        this.jsonTree.setModel(model);
    }
}
