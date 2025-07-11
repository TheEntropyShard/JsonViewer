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

package me.theentropyshard.jsonviewer.gui;

import com.formdev.flatlaf.FlatClientProperties;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import net.miginfocom.swing.MigLayout;
import org.fife.ui.rsyntaxtextarea.RSyntaxTextArea;
import org.fife.ui.rtextarea.RTextScrollPane;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.text.DefaultCaret;
import java.awt.*;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.StringSelection;
import java.awt.event.ItemEvent;

import me.theentropyshard.jsonviewer.JsonViewer;
import me.theentropyshard.jsonviewer.codegen.*;
import me.theentropyshard.jsonviewer.utils.Json;
import me.theentropyshard.jsonviewer.utils.SwingUtils;

public class GeneratePOJOView extends JPanel {
    public GeneratePOJOView(String json) {
        super(new BorderLayout());

        this.setPreferredSize(new Dimension(1040, 540));

        JPanel settingsPanel = new JPanel();
        settingsPanel.setLayout(new MigLayout("gap 5 5, insets 6 6 5 6", "[][][][][][][][][][]", "[][][][]"));
        this.add(settingsPanel, BorderLayout.NORTH);

        settingsPanel.add(new JLabel("Field access modifier: "));
        JComboBox<AccessModifier> accessModifierCombo = new JComboBox<>(AccessModifier.values());
        accessModifierCombo.setSelectedIndex(JsonViewer.instance.getConfig().getPojoAccessModifier());
        settingsPanel.add(accessModifierCombo, "growx");

        settingsPanel.add(new JLabel("Number type: "));
        JComboBox<NumberType> numberTypeCombo = new JComboBox<>(NumberType.values());
        numberTypeCombo.setSelectedIndex(JsonViewer.instance.getConfig().getPojoNumberType());
        settingsPanel.add(numberTypeCombo, "growx");

        settingsPanel.add(new JLabel("Indent: "));
        JComboBox<String> indentCombo = new JComboBox<>(new String[]{"1 Space", "2 Space", "3 Space", "4 Space"});
        indentCombo.setSelectedIndex(JsonViewer.instance.getConfig().getPojoIndent());
        settingsPanel.add(indentCombo, "growx");

        settingsPanel.add(new JLabel("Boolean getter prefix: "));
        JComboBox<BooleanGetterPrefix> booleanGetterPrefixCombo = new JComboBox<>(BooleanGetterPrefix.values());
        booleanGetterPrefixCombo.setSelectedIndex(JsonViewer.instance.getConfig().getPojoBooleanGetterPrefix());
        settingsPanel.add(booleanGetterPrefixCombo, "growx");

        settingsPanel.add(new JLabel("Array style: "));
        JComboBox<ArrayStyle> arrayStyleCombo = new JComboBox<>(ArrayStyle.values());
        arrayStyleCombo.setSelectedIndex(JsonViewer.instance.getConfig().getPojoArrayStyle());
        settingsPanel.add(arrayStyleCombo, "wrap, growx");

        JCheckBox useAnnotationsCheckbox = new JCheckBox("Use annotations");
        useAnnotationsCheckbox.setSelected(JsonViewer.instance.getConfig().isPojoUseAnnotations());
        settingsPanel.add(useAnnotationsCheckbox, "span 2, wrap");

        JCheckBox generateGettersCheckbox = new JCheckBox("Generate getters");
        generateGettersCheckbox.setSelected(JsonViewer.instance.getConfig().isPojoGenerateGetters());
        settingsPanel.add(generateGettersCheckbox, "span 2, wrap");

        settingsPanel.add(new JLabel("Top-level class name: "));

        JTextField classNameField = new JTextField("Model");
        classNameField.putClientProperty(FlatClientProperties.PLACEHOLDER_TEXT, "Top-level class name");
        settingsPanel.add(classNameField, "span 3, growx");

        RSyntaxTextArea textArea = new RSyntaxTextArea();
        ((DefaultCaret) textArea.getCaret()).setUpdatePolicy(DefaultCaret.NEVER_UPDATE);
        textArea.setLineWrap(true);
        textArea.setCodeFoldingEnabled(true);
        textArea.setBracketMatchingEnabled(true);
        textArea.setShowMatchedBracketPopup(true);
        textArea.setSyntaxEditingStyle(RSyntaxTextArea.SYNTAX_STYLE_JAVA);
        RTextScrollPane scrollPane = new RTextScrollPane(textArea, true);

        JPanel scrollPanel = new JPanel(new BorderLayout());
        scrollPanel.setBorder(new EmptyBorder(0, 4, 0, 4));
        scrollPanel.add(scrollPane);

        this.add(scrollPanel, BorderLayout.CENTER);

        JPanel bottomPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 4, 4));
        this.add(bottomPanel, BorderLayout.SOUTH);

        JButton copyButton = new JButton("Copy");
        bottomPanel.add(copyButton);
        copyButton.addActionListener(e -> {
            Clipboard clipboard = Toolkit.getDefaultToolkit().getSystemClipboard();
            clipboard.setContents(new StringSelection(textArea.getText()), null);
        });

        Runnable generate = () -> {
            if (json == null || json.trim().isEmpty()) {
                return;
            }

            SwingUtils.startWorker(() -> {
                JsonToJava jsonToJava = new JsonToJava.Builder()
                    .accessModifier((AccessModifier) accessModifierCombo.getSelectedItem())
                    .numberType((NumberType) numberTypeCombo.getSelectedItem())
                    .indent(" ".repeat(JsonViewer.instance.getConfig().getPojoIndent() + 1))
                    .booleanGetterPrefix((BooleanGetterPrefix) booleanGetterPrefixCombo.getSelectedItem())
                    .arrayStyle((ArrayStyle) arrayStyleCombo.getSelectedItem())
                    .useAnnotations(useAnnotationsCheckbox.isSelected())
                    .generateGetters(generateGettersCheckbox.isSelected())
                    .build();

                JsonElement element = Json.parse(json, JsonElement.class);

                JsonObject jsonObject;

                if (element.isJsonObject()) {
                    jsonObject = element.getAsJsonObject();
                } else if (element.isJsonArray()) {
                    JsonArray array = element.getAsJsonArray();

                    if (array.isEmpty()) {
                        return;
                    } else {
                        JsonElement arrayElement = array.get(0);

                        if (arrayElement.isJsonObject()) {
                            jsonObject = arrayElement.getAsJsonObject();
                        } else {
                            return;
                        }
                    }
                } else {
                    return;
                }

                SwingUtilities.invokeLater(() -> {
                    textArea.setText(jsonToJava.generate(classNameField.getText(), jsonObject));
                });
            });
        };

        generate.run();

        accessModifierCombo.addItemListener(e -> {
            if (e.getStateChange() != ItemEvent.SELECTED) {
                return;
            }

            JsonViewer.instance.getConfig().setPojoAccessModifier(accessModifierCombo.getSelectedIndex());

            generate.run();
        });

        numberTypeCombo.addItemListener(e -> {
            if (e.getStateChange() != ItemEvent.SELECTED) {
                return;
            }

            JsonViewer.instance.getConfig().setPojoNumberType(numberTypeCombo.getSelectedIndex());

            generate.run();
        });

        indentCombo.addItemListener(e -> {
            if (e.getStateChange() != ItemEvent.SELECTED) {
                return;
            }

            JsonViewer.instance.getConfig().setPojoIndent(indentCombo.getSelectedIndex());

            generate.run();
        });

        booleanGetterPrefixCombo.addItemListener(e -> {
            if (e.getStateChange() != ItemEvent.SELECTED) {
                return;
            }

            JsonViewer.instance.getConfig().setPojoBooleanGetterPrefix(booleanGetterPrefixCombo.getSelectedIndex());

            generate.run();
        });

        arrayStyleCombo.addItemListener(e -> {
            if (e.getStateChange() != ItemEvent.SELECTED) {
                return;
            }

            JsonViewer.instance.getConfig().setPojoArrayStyle(arrayStyleCombo.getSelectedIndex());

            generate.run();
        });

        useAnnotationsCheckbox.addActionListener(e -> {
            JsonViewer.instance.getConfig().setPojoUseAnnotations(useAnnotationsCheckbox.isSelected());

            generate.run();
        });

        generateGettersCheckbox.addActionListener(e -> {
            JsonViewer.instance.getConfig().setPojoGenerateGetters(generateGettersCheckbox.isSelected());

            generate.run();
        });

        classNameField.getDocument().addDocumentListener(new DocumentListener() {
            @Override
            public void insertUpdate(DocumentEvent e) {
                this.update();
            }

            @Override
            public void removeUpdate(DocumentEvent e) {
                this.update();
            }

            @Override
            public void changedUpdate(DocumentEvent e) {

            }

            private void update() {
                generate.run();
            }
        });
    }
}
