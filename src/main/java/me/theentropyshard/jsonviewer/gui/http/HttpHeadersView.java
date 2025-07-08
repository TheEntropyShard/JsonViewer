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

package me.theentropyshard.jsonviewer.gui.http;

import com.formdev.flatlaf.FlatClientProperties;
import com.formdev.flatlaf.extras.FlatSVGIcon;
import net.miginfocom.swing.MigLayout;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class HttpHeadersView extends JPanel {
    private final Map<Integer, List<Component>> rows;

    private final JPanel root;
    private final JButton addHeaderButton;

    private int row;

    private static class ScrollablePanel extends JPanel implements Scrollable {

        @Override
        public Dimension getPreferredScrollableViewportSize() {
            return this.getPreferredSize();
        }

        @Override
        public int getScrollableUnitIncrement(Rectangle visibleRect, int orientation, int direction) {
            return 10;
        }

        @Override
        public int getScrollableBlockIncrement(Rectangle visibleRect, int orientation, int direction) {
            return 10;
        }

        @Override
        public boolean getScrollableTracksViewportWidth() {
            return true;
        }

        @Override
        public boolean getScrollableTracksViewportHeight() {
            return false;
        }
    }

    public HttpHeadersView() {
        this.setLayout(new BorderLayout());

        this.rows = new HashMap<>();

        this.root = new ScrollablePanel();
        this.root.setBorder(new EmptyBorder(0, 0, 0, 4));
        this.root.setLayout(new MigLayout("fill, insets 2, gap 5 5", "[50%][50%][]", "[top]"));

        this.addHeaderButton = new JButton("Add");
        this.addHeaderButton.addActionListener(e -> {
            this.addHeaderRow();
        });

        JScrollPane scrollPane = new JScrollPane(
            this.root,
            JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED,
            JScrollPane.HORIZONTAL_SCROLLBAR_NEVER
        );
        scrollPane.setBorder(null);
        scrollPane.getVerticalScrollBar().setPreferredSize(new Dimension(0,0));
        this.add(scrollPane, BorderLayout.CENTER);

        this.addHeaderRow();
    }

    public Map<String, String> getHeaders() {
        Map<String, String> headers = new HashMap<>();

        for (Map.Entry<Integer, List<Component>> entry : this.rows.entrySet()) {
            List<Component> components = entry.getValue();

            String name = ((JTextField) components.get(0)).getText().trim();
            String value = ((JTextField) components.get(1)).getText().trim();

            if (name.isEmpty() || value.isEmpty()) {
                continue;
            }

            headers.put(name, value);
        }

        return headers;
    }

    public void addHeaderRow() {
        this.remove(this.addHeaderButton);

        JTextField nameField = new JTextField();
        nameField.putClientProperty(FlatClientProperties.PLACEHOLDER_TEXT, "Name");
        this.root.add(nameField, "growx");

        JTextField valueField = new JTextField();
        valueField.putClientProperty(FlatClientProperties.PLACEHOLDER_TEXT, "Value");
        this.root.add(valueField, "growx");

        JButton deleteButton = new JButton(new FlatSVGIcon(HttpHeadersView.class.getResource("/assets/images/delete.svg")));
        deleteButton.putClientProperty("row", this.row);
        deleteButton.addActionListener(e -> {
            int row = Integer.parseInt(String.valueOf(deleteButton.getClientProperty("row")));
            this.rows.get(row).forEach(this.root::remove);
            this.root.revalidate();
        });
        this.root.add(deleteButton, "wrap");

        this.root.add(this.addHeaderButton, "span 4, growx, pushy");
        this.root.revalidate();

        this.rows.put(this.row, Arrays.asList(nameField, valueField, deleteButton));

        this.row++;
    }
}