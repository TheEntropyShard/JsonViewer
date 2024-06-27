/*
 * JsonViewer - https://github.com/TheEntropyShard/JsonViewer
 * Copyright (C) 2023-2024 TheEntropyShard
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

import me.theentropyshard.jsonviewer.gui.textview.JsonTextView;
import me.theentropyshard.jsonviewer.gui.treeview.JsonTreeView;

import javax.swing.*;
import javax.swing.tree.TreeModel;
import java.awt.*;

public class JsonView extends JPanel {
    private final JsonTextView textView;
    private final JsonTreeView treeView;

    private final CardLayout cardLayout;
    private final JPanel view;
    private final StatusBar statusBar;

    public JsonView(MainView mainView) {
        super(new BorderLayout());

        this.cardLayout = new CardLayout();
        this.view = new JPanel(this.cardLayout);

        this.textView = new JsonTextView(mainView);
        this.view.add(this.textView, "textView");

        this.treeView = new JsonTreeView(mainView);
        this.view.add(this.treeView, "treeView");

        this.add(this.view, BorderLayout.CENTER);

        this.statusBar = new StatusBar();

        this.textView.addCaretUpdateListener((lineNumber, columnNumber) -> {
            this.statusBar.setLine(lineNumber);
            this.statusBar.setColumn(columnNumber);
            this.statusBar.updateStatus();
        });

        this.add(this.statusBar, BorderLayout.SOUTH);
    }

    public void scrollToTop() {
        this.textView.scrollToTop();
    }

    public void switchToTextView() {
        this.cardLayout.show(this.view, "textView");
    }

    public void switchToTreeView() {
        this.cardLayout.show(this.view, "treeView");
    }

    public void setText(String text) {
        this.textView.setText(text);
    }

    public String getText() {
        return this.textView.getText();
    }

    public void setModel(TreeModel model) {
        this.treeView.setModel(model);
    }

    public void setSizeBytes(int bytes) {
        this.statusBar.setBytes(bytes);
        this.statusBar.updateStatus();
    }
}
