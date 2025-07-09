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
import okhttp3.HttpUrl;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import java.awt.*;
import java.util.Map;

import me.theentropyshard.jsonviewer.gui.NameValueView;

public class HttpRequestView extends JPanel {
    private JTextField urlField;
    private JComboBox<String> httpMethodCombo;
    private JButton sendButton;
    private NameValueView headersView;
    private NameValueView queryParamsView;
    private RequestBodyView requestBodyView;

    private HttpUrl url;

    public HttpRequestView() {
        super(new BorderLayout());

        this.setPreferredSize(new Dimension(720, 405));

        this.add(this.makeTopPanel(), BorderLayout.NORTH);
        this.add(this.makeCenterPanel(), BorderLayout.CENTER);

        this.urlField.getDocument().addDocumentListener(new DocumentListener() {
            @Override
            public void insertUpdate(DocumentEvent e) {
                HttpRequestView.this.updateUrl();
            }

            @Override
            public void removeUpdate(DocumentEvent e) {
                HttpRequestView.this.updateUrl();
            }

            @Override
            public void changedUpdate(DocumentEvent e) {

            }
        });
    }

    private void updateUrl() {
        HttpUrl url = HttpUrl.parse(this.urlField.getText());

        if (url == null) {
            return;
        }

        this.url = url;

        this.updateQueryParamsView();
    }

    private void updateQueryParamsView() {
        this.queryParamsView.clear();

        for (int i = 0; i < this.url.querySize(); i++) {
            this.queryParamsView.addRow();
            this.queryParamsView.setNameValue(i, this.url.queryParameterName(i), this.url.queryParameterValue(i));
        }
    }

    private JPanel makeTopPanel() {
        JPanel topPanel = new JPanel(new GridBagLayout());
        topPanel.setBorder(new EmptyBorder(0, 4, 4, 4));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.anchor = GridBagConstraints.NORTH;
        gbc.fill = GridBagConstraints.HORIZONTAL;

        gbc.gridy = 0;
        gbc.gridx = 0;
        gbc.weightx = 1;

        this.urlField = new JTextField();
        this.urlField.putClientProperty(FlatClientProperties.PLACEHOLDER_TEXT, "https://example.com/file.json");
        topPanel.add(this.urlField, gbc);

        gbc.gridx = 1;
        gbc.weightx = 0;

        this.httpMethodCombo = new JComboBox<>(new String[]{"GET", "POST", "PUT", "PATCH", "DELETE"});
        topPanel.add(this.httpMethodCombo, gbc);

        gbc.gridx = 2;

        this.sendButton = new JButton("Send");
        topPanel.add(this.sendButton, gbc);

        return topPanel;
    }

    private JPanel makeCenterPanel() {
        JPanel centerPanel = new JPanel(new BorderLayout());

        JTabbedPane tabbedPane = new JTabbedPane();
        centerPanel.add(tabbedPane, BorderLayout.CENTER);

        this.headersView = new NameValueView();
        this.headersView.setBorder(new EmptyBorder(4, 4, 4, 0));
        tabbedPane.addTab("Headers", this.headersView);

        this.queryParamsView = new NameValueView();
        this.queryParamsView.setBorder(new EmptyBorder(4, 4, 4, 0));
        tabbedPane.addTab("Query Params", this.queryParamsView);

        this.requestBodyView = new RequestBodyView();
        tabbedPane.addTab("Request Body", this.requestBodyView);

        return centerPanel;
    }

    public JButton getSendButton() {
        return this.sendButton;
    }

    public HttpUrl getUrl() {
        return this.url;
    }

    public String getMethod() {
        return String.valueOf(this.httpMethodCombo.getSelectedItem());
    }

    public Map<String, String> getHeaders() {
        return this.headersView.getPairs();
    }
}
