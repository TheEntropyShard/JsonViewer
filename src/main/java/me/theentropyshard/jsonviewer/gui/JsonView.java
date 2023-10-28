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
    private final JLabel lineLabel;
    private final JLabel columnLabel;
    private final JLabel sizeLabel;

    private boolean isTextViewCurrent;

    public JsonView() {
        super(new BorderLayout());

        this.cardLayout = new CardLayout();
        this.view = new JPanel(this.cardLayout);

        this.textView = new JsonTextView();
        this.view.add(this.textView, "textView");

        this.treeView = new JsonTreeView();
        this.view.add(this.treeView, "treeView");

        this.add(this.view, BorderLayout.CENTER);

        JPanel statusBar = new JPanel(new FlowLayout(FlowLayout.LEFT));

        this.lineLabel = new JLabel("Line: 0,");
        statusBar.add(this.lineLabel);
        this.columnLabel = new JLabel("Column: 0,");
        statusBar.add(this.columnLabel);
        this.sizeLabel = new JLabel("Size: 0 KB");
        statusBar.add(this.sizeLabel);

        this.textView.addCaretUpdateListener((lineNumber, columnNumber) -> {
            this.lineLabel.setText("Line: " + lineNumber + ",");
            this.columnLabel.setText("Column: " + columnNumber + ",");
        });

        this.add(statusBar, BorderLayout.SOUTH);
    }

    public void scrollToTop() {
        this.textView.scrollToTop();
    }

    public void switchToTextView() {
        this.cardLayout.show(this.view, "textView");
        this.isTextViewCurrent = true;
    }

    public void switchToTreeView() {
        this.cardLayout.show(this.view, "treeView");
        this.isTextViewCurrent = false;
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

    public void setSizeInKBs(double sizeInKBs) {
        this.sizeLabel.setText("Size: " + sizeInKBs + " KB");
    }

    public boolean isTextViewCurrent() {
        return this.isTextViewCurrent;
    }
}
