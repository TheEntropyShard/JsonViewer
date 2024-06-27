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

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.ActionListener;
import java.awt.event.ItemListener;

public class ControlPanel extends JPanel {
    public static final int BUTTON_HEIGHT = 35;

    private final JComboBox<String> indentCombo;

    public ControlPanel(
            ActionListener onFile, ActionListener onUrl, ActionListener onTreeView,
            ActionListener onBeautify, ItemListener onIndentCombo, ActionListener onMinify
    ) {
        super(new BorderLayout());

        JPanel buttonsPanel = new JPanel(new GridLayout(0, 1, 0, 1));
        buttonsPanel.setBorder(new EmptyBorder(3, 3, 3, 3));
        this.add(buttonsPanel, BorderLayout.PAGE_START);

        JPanel sourceButtons = new JPanel(new FlowLayout(FlowLayout.CENTER, 0, 0));
        buttonsPanel.add(sourceButtons, BorderLayout.PAGE_START);

        JButton fileButton = new JButton("File");
        fileButton.addActionListener(onFile);
        fileButton.setPreferredSize(this.getDimension(fileButton));
        sourceButtons.add(fileButton, BorderLayout.WEST);

        JButton urlButton = new JButton("URL");
        urlButton.addActionListener(onUrl);
        urlButton.setPreferredSize(this.getDimension(urlButton));
        sourceButtons.add(urlButton, BorderLayout.EAST);

        JButton treeViewerButton = new JButton("Tree Viewer");
        treeViewerButton.addActionListener(onTreeView);
        treeViewerButton.setPreferredSize(this.getDimension(treeViewerButton));
        buttonsPanel.add(treeViewerButton);

        JButton beautifyButton = new JButton("Beautify");
        beautifyButton.addActionListener(onBeautify);
        beautifyButton.setPreferredSize(this.getDimension(beautifyButton));
        buttonsPanel.add(beautifyButton);

        this.indentCombo = new JComboBox<>(new String[]{"1 Space", "2 Space", "3 Space", "4 Space"});
        this.indentCombo.addItemListener(onIndentCombo);
        this.indentCombo.setPreferredSize(this.getDimension(this.indentCombo));
        buttonsPanel.add(this.indentCombo);

        JButton minifyButton = new JButton("Minify");
        minifyButton.addActionListener(onMinify);
        minifyButton.setPreferredSize(this.getDimension(minifyButton));
        buttonsPanel.add(minifyButton);
    }

    private Dimension getDimension(Component c) {
        return new Dimension(c.getPreferredSize().width, ControlPanel.BUTTON_HEIGHT);
    }

    public JComboBox<String> getIndentCombo() {
        return this.indentCombo;
    }
}
