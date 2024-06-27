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

import me.theentropyshard.jsonviewer.utils.MathUtils;

import javax.swing.*;
import java.awt.*;

public class StatusBar extends JPanel {
    private final JLabel label;

    private int line;
    private int column;
    private int bytes;

    public StatusBar() {
        super(new FlowLayout(FlowLayout.LEFT));

        this.add(this.label = new JLabel());

        this.updateStatus();
    }

    public void updateStatus() {
        this.label.setText(
                "Line: " + this.line + ", " +
                "Column: " + this.column + ", " +
                "Size: " + MathUtils.round(this.bytes / 1024.0, 2) + " KBs"
        );
    }

    public void setLine(int line) {
        this.line = line;
    }

    public void setColumn(int column) {
        this.column = column;
    }

    public void setBytes(int bytes) {
        this.bytes = bytes;
    }
}
