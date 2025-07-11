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

package me.theentropyshard.jsonviewer.gui.treeview;

import com.formdev.flatlaf.FlatClientProperties;

import me.theentropyshard.jsonviewer.gui.FileDropTarget;
import me.theentropyshard.jsonviewer.gui.MainView;

import javax.swing.*;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.TreeModel;

public class JsonTreeView extends JScrollPane {
    private final MainView mainView;
    private final JTree jsonTree;

    public JsonTreeView(MainView mainView) {
        this.mainView = mainView;
        this.jsonTree = this.makeJsonTree();
        this.setViewportView(this.jsonTree);
        this.putClientProperty(FlatClientProperties.STYLE, "focusWidth: 0; focusedBorderColor: $Component.borderColor");
    }

    public void clear() {
        this.setModel(JsonTreeView.getEmptyTreeModel());
    }

    private JTree makeJsonTree() {
        JTree jsonTree = new JTree(JsonTreeView.getEmptyTreeModel());
        jsonTree.setShowsRootHandles(true);
        jsonTree.setRootVisible(true);
        jsonTree.addMouseListener(new TheMouseListener(jsonTree));

        jsonTree.setDropTarget(new FileDropTarget(this.mainView::addTab));

        return jsonTree;
    }

    private static TreeModel getEmptyTreeModel() {
        return new DefaultTreeModel(new DefaultMutableTreeNode("empty"));
    }

    public void setModel(TreeModel treeModel) {
        this.jsonTree.setModel(treeModel);
    }
}
