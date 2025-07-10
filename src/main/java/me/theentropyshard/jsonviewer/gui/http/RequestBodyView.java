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

import org.fife.ui.rsyntaxtextarea.RSyntaxTextArea;
import org.fife.ui.rsyntaxtextarea.SyntaxConstants;
import org.fife.ui.rtextarea.RTextScrollPane;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.util.Map;

import me.theentropyshard.jsonviewer.gui.NameValueView;
import me.theentropyshard.jsonviewer.gui.textview.JsonTextView;

public class RequestBodyView extends JPanel {
    private final RSyntaxTextArea textArea;
    private final NameValueView formView;

    private BodyType bodyType = BodyType.JSON;

    public RequestBodyView() {
        super(new BorderLayout());

        JPanel topPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));

        ButtonGroup group = new ButtonGroup();

        JRadioButton jsonBodyButton = new JRadioButton("JSON");
        jsonBodyButton.setSelected(true);
        group.add(jsonBodyButton);

        JRadioButton formBodyButton = new JRadioButton("Form (url-encoded)");
        group.add(formBodyButton);

        topPanel.add(jsonBodyButton);
        topPanel.add(formBodyButton);

        this.add(topPanel, BorderLayout.NORTH);

        CardLayout layout = new CardLayout();
        JPanel bodyPanel = new JPanel(layout);
        bodyPanel.setBorder(new EmptyBorder(0, 4, 4, 4));
        this.add(bodyPanel, BorderLayout.CENTER);

        this.textArea = JsonTextView.makeTextArea();
        RTextScrollPane scrollPane = new RTextScrollPane(this.textArea, true);
        bodyPanel.add(scrollPane, "json");

        this.formView = new NameValueView();
        bodyPanel.add(this.formView, "form");

        jsonBodyButton.addActionListener(e -> {
            this.bodyType = BodyType.JSON;
            layout.show(bodyPanel, "json");
        });

        formBodyButton.addActionListener(e -> {
            this.bodyType = BodyType.FORM;
            layout.show(bodyPanel, "form");
        });
    }

    public enum BodyType {
        JSON,
        FORM
    }

    public BodyType getBodyType() {
        return this.bodyType;
    }

    public String getJson() {
        return this.textArea.getText();
    }

    public Map<String, String> getFormData() {
        return this.formView.getPairs();
    }
}
