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

import me.theentropyshard.jsonviewer.JsonViewer;

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

    public ControlsPanel(JsonViewer jsonViewer) {
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

        int indent = jsonViewer.getConfig().getBeautifyIndent();
        this.indentCombo.setSelectedIndex(Math.min(3, Math.max(0, indent)));

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
