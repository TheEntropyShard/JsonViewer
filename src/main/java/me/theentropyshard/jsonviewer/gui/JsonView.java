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
import javax.swing.event.CaretEvent;
import javax.swing.event.CaretListener;
import javax.swing.text.BadLocationException;
import javax.swing.tree.TreeModel;
import javax.swing.tree.TreeNode;
import javax.swing.tree.TreePath;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.Enumeration;

public class JsonView extends JPanel {
    private final CardLayout cardLayout;
    private final JPanel view;
    private final RSyntaxTextArea textArea;
    private final JTree jsonTree;
    private final JLabel lineLabel;
    private final JLabel columnLabel;
    private final JLabel sizeLabel;

    private int line;
    private int column;
    private double sizeInKBs;

    public JsonView() {
        super(new BorderLayout());

        this.cardLayout = new CardLayout();
        this.view = new JPanel(this.cardLayout);

        this.textArea = this.makeTextArea();
        RTextScrollPane textScrollPane = new RTextScrollPane(this.textArea);
        this.view.add(textScrollPane, "textView");

        this.jsonTree = this.makeJsonTree();
        JScrollPane treeScrollPane = new JScrollPane(this.jsonTree);
        this.view.add(treeScrollPane, "treeView");

        this.add(this.view, BorderLayout.CENTER);

        JPanel statusBar = new JPanel(new FlowLayout(FlowLayout.LEFT));

        this.lineLabel = new JLabel("Line: " + this.line + ",");
        statusBar.add(this.lineLabel);
        this.columnLabel = new JLabel("Column: " + this.line + ",");
        statusBar.add(this.columnLabel);
        this.sizeLabel = new JLabel("Size: " + this.line + " KB");
        statusBar.add(this.sizeLabel);

        this.add(statusBar, BorderLayout.SOUTH);

        this.textArea.addCaretListener(e -> {
            int lineNum = 1;
            int columnNum = 1;

            int caretPosition = JsonView.this.textArea.getCaretPosition();
            int lineOfOffset = 1;
            try {
                lineOfOffset = JsonView.this.textArea.getLineOfOffset(caretPosition);
            } catch (BadLocationException ignored) {
                ignored.printStackTrace();
            }

            lineNum = lineOfOffset;
            try {
                columnNum = caretPosition - JsonView.this.textArea.getLineStartOffset(lineNum);
            } catch (BadLocationException ex) {
                throw new RuntimeException(ex);
            }

            // We have to add one here because line numbers start at 0 for getLineOfOffset and we want it to start at 1 for display.
            lineNum += 1;
            columnNum += 1;

            JsonView.this.setLine(lineNum);
            JsonView.this.setColumn(columnNum);
        });
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
        this.cardLayout.show(this.view, "textView");
    }

    public void switchToTreeView() {
        this.cardLayout.show(this.view, "treeView");
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

    public int getLine() {
        return this.line;
    }

    public void setLine(int line) {
        this.line = line;
        this.lineLabel.setText("Line: " + this.line + ",");
    }

    public int getColumn() {
        return this.column;
    }

    public void setColumn(int column) {
        this.column = column;
        this.columnLabel.setText("Column: " + this.column + ",");
    }

    public double getSizeInKBs() {
        return this.sizeInKBs;
    }

    public void setSizeInKBs(double sizeInKBs) {
        this.sizeInKBs = sizeInKBs;
        this.sizeLabel.setText("Size: " + this.sizeInKBs + " KB");
    }
}
