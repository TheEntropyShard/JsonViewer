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

package me.theentropyshard.jsonviewer.gui.treeview;

import com.google.gson.JsonElement;
import me.theentropyshard.jsonviewer.json.JsonPair;

import javax.swing.*;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.TreeNode;
import javax.swing.tree.TreePath;
import java.awt.*;
import java.awt.datatransfer.StringSelection;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.Enumeration;

final class TheMouseListener extends MouseAdapter {
    private final JTree jsonTree;

    public TheMouseListener(JTree jsonTree) {
        this.jsonTree = jsonTree;
    }

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

        DefaultMutableTreeNode node = (DefaultMutableTreeNode) path.getPathComponent(path.getPathCount() - 1);
        if (node.getChildCount() == 0) {
            @SuppressWarnings("unchecked")
            JsonPair<String, JsonElement> pair = (JsonPair<String, JsonElement>) node.getUserObject();

            JMenuItem expandItem = new JMenuItem("Copy Key");
            expandItem.addActionListener(ae -> {
                StringSelection selection = new StringSelection(pair.getLeft());
                Toolkit.getDefaultToolkit().getSystemClipboard().setContents(selection, selection);
            });
            popup.add(expandItem);

            JMenuItem collapseItem = new JMenuItem("Copy Value");
            collapseItem.addActionListener(ae -> {
                StringSelection selection = new StringSelection(pair.getRight().getAsString());
                Toolkit.getDefaultToolkit().getSystemClipboard().setContents(selection, selection);
            });
            popup.add(collapseItem);
        } else {
            JMenuItem expandItem = new JMenuItem("Expand");
            expandItem.addActionListener(ae -> this.expandAllChildren(this.jsonTree, this.jsonTree.getSelectionPath(), true));
            popup.add(expandItem);

            JMenuItem collapseItem = new JMenuItem("Collapse");
            collapseItem.addActionListener(ae -> this.expandAllChildren(this.jsonTree, this.jsonTree.getSelectionPath(), false));
            popup.add(collapseItem);

            JMenuItem copyTextItem = new JMenuItem("Copy Text");
            copyTextItem.addActionListener(ae -> {
                StringSelection selection = new StringSelection(String.valueOf(node.getUserObject()));
                Toolkit.getDefaultToolkit().getSystemClipboard().setContents(selection, selection);
            });
            popup.add(copyTextItem);
        }

        popup.show(tree, x, y);
    }

    @Override
    public void mousePressed(MouseEvent e) {
        if (e.isPopupTrigger()) {
            this.myPopupEvent(e);
        }
    }

    @Override
    public void mouseReleased(MouseEvent e) {
        if (e.isPopupTrigger()) {
            this.myPopupEvent(e);
        }
    }
}