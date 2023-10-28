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

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;

public class ControlsPanel extends JPanel {
    public static final int BUTTON_HEIGHT = 35;

    private final JButton fileButton;
    private final JButton urlButton;
    private final JButton treeViewerButton;
    private final JButton beautifyButton;
    private final JComboBox<String> indentCombo;
    private final JButton minifyButton;

    public ControlsPanel() {
        super(new BorderLayout());

        JPanel buttonsPanel = new JPanel(new GridLayout(0, 1, 0, 1));
        this.add(buttonsPanel, BorderLayout.PAGE_START);

        JPanel sourceButtons = new JPanel(new FlowLayout(FlowLayout.CENTER, 0, 0));

        this.fileButton = new JButton("File");
        this.fileButton.setPreferredSize(this.getDimension(this.fileButton));
        sourceButtons.add(this.fileButton, BorderLayout.WEST);

        this.urlButton = new JButton("URL");
        this.urlButton.setPreferredSize(this.getDimension(this.urlButton));
        sourceButtons.add(this.urlButton, BorderLayout.EAST);

        buttonsPanel.add(sourceButtons, BorderLayout.PAGE_START);

        this.treeViewerButton = new JButton("Tree Viewer");
        this.treeViewerButton.setPreferredSize(this.getDimension(this.treeViewerButton));
        buttonsPanel.add(this.treeViewerButton);

        this.beautifyButton = new JButton("Beautify");
        this.beautifyButton.setPreferredSize(this.getDimension(this.beautifyButton));
        buttonsPanel.add(this.beautifyButton);

        this.indentCombo = new JComboBox<>(new String[]{"1 Space", "2 Space", "3 Space", "4 Space"});
        this.indentCombo.setPreferredSize(this.getDimension(this.indentCombo));
        buttonsPanel.add(this.indentCombo);

        this.minifyButton = new JButton("Minify");
        this.minifyButton.setPreferredSize(this.getDimension(this.minifyButton));
        buttonsPanel.add(this.minifyButton);

        buttonsPanel.setBorder(new EmptyBorder(3, 3, 3, 3));
    }

    private Dimension getDimension(Component c) {
        return new Dimension(c.getPreferredSize().width, ControlsPanel.BUTTON_HEIGHT);
    }

    public JButton getFileButton() {
        return this.fileButton;
    }

    public JButton getUrlButton() {
        return this.urlButton;
    }

    public JButton getTreeViewerButton() {
        return this.treeViewerButton;
    }

    public JButton getBeautifyButton() {
        return this.beautifyButton;
    }

    public JComboBox<String> getIndentCombo() {
        return this.indentCombo;
    }

    public JButton getMinifyButton() {
        return this.minifyButton;
    }
}
