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
import com.google.gson.JsonObject;
import net.miginfocom.swing.MigLayout;
import org.fife.ui.rsyntaxtextarea.RSyntaxTextArea;
import org.fife.ui.rtextarea.RTextScrollPane;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
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
    private final JButton generateButton;

    public GeneratePOJOView(String json) {
        super(new BorderLayout());

        this.setPreferredSize(new Dimension(1040, 540));

        JPanel settingsPanel = new JPanel(new MigLayout(
            "gap 5 5, insets 6 6 5 6",
            "[][][][][][][][][][]",
            "[][][][]"
        ));
        this.add(settingsPanel, BorderLayout.NORTH);

        settingsPanel.add(new JLabel("Field access modifier: "));
        JComboBox<AccessModifier> accessModifierCombo = new JComboBox<>(AccessModifier.values());
        accessModifierCombo.setSelectedIndex(JsonViewer.instance.getConfig().getPojoAccessModifier());
        accessModifierCombo.addItemListener(e -> {
            if (e.getStateChange() != ItemEvent.SELECTED) {
                return;
            }

            JsonViewer.instance.getConfig().setPojoAccessModifier(accessModifierCombo.getSelectedIndex());
        });
        settingsPanel.add(accessModifierCombo, "growx");

        settingsPanel.add(new JLabel("Number type: "));
        JComboBox<NumberType> numberTypeCombo = new JComboBox<>(NumberType.values());
        numberTypeCombo.setSelectedIndex(JsonViewer.instance.getConfig().getPojoNumberType());
        numberTypeCombo.addItemListener(e -> {
            if (e.getStateChange() != ItemEvent.SELECTED) {
                return;
            }

            JsonViewer.instance.getConfig().setPojoNumberType(numberTypeCombo.getSelectedIndex());
        });
        settingsPanel.add(numberTypeCombo, "growx");

        settingsPanel.add(new JLabel("Indent: "));
        JComboBox<String> indentCombo = new JComboBox<>(new String[]{"1 Space", "2 Space", "3 Space", "4 Space"});
        indentCombo.setSelectedIndex(JsonViewer.instance.getConfig().getPojoIndent());
        indentCombo.addItemListener(e -> {
            if (e.getStateChange() != ItemEvent.SELECTED) {
                return;
            }

            JsonViewer.instance.getConfig().setPojoIndent(indentCombo.getSelectedIndex());
        });
        settingsPanel.add(indentCombo, "growx");

        settingsPanel.add(new JLabel("Boolean getter prefix: "));
        JComboBox<BooleanGetterPrefix> booleanGetterPrefixCombo = new JComboBox<>(BooleanGetterPrefix.values());
        booleanGetterPrefixCombo.setSelectedIndex(JsonViewer.instance.getConfig().getPojoBooleanGetterPrefix());
        booleanGetterPrefixCombo.addItemListener(e -> {
            if (e.getStateChange() != ItemEvent.SELECTED) {
                return;
            }

            JsonViewer.instance.getConfig().setPojoBooleanGetterPrefix(booleanGetterPrefixCombo.getSelectedIndex());
        });
        settingsPanel.add(booleanGetterPrefixCombo, "growx");

        settingsPanel.add(new JLabel("Array style: "));
        JComboBox<ArrayStyle> arrayStyleCombo = new JComboBox<>(ArrayStyle.values());
        arrayStyleCombo.setSelectedIndex(JsonViewer.instance.getConfig().getPojoArrayStyle());
        arrayStyleCombo.addItemListener(e -> {
            if (e.getStateChange() != ItemEvent.SELECTED) {
                return;
            }

            JsonViewer.instance.getConfig().setPojoArrayStyle(arrayStyleCombo.getSelectedIndex());
        });
        settingsPanel.add(arrayStyleCombo, "wrap, growx");

        JCheckBox useAnnotationsCheckbox = new JCheckBox("Use annotations");
        useAnnotationsCheckbox.setSelected(JsonViewer.instance.getConfig().isPojoUseAnnotations());
        useAnnotationsCheckbox.addActionListener(e -> {
            JsonViewer.instance.getConfig().setPojoUseAnnotations(useAnnotationsCheckbox.isSelected());
        });
        settingsPanel.add(useAnnotationsCheckbox, "span 2, wrap");

        JCheckBox generateGettersCheckbox = new JCheckBox("Generate getters");
        generateGettersCheckbox.setSelected(JsonViewer.instance.getConfig().isPojoGenerateGetters());
        generateGettersCheckbox.addActionListener(e -> {
            JsonViewer.instance.getConfig().setPojoGenerateGetters(generateGettersCheckbox.isSelected());
        });
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

        this.generateButton = new JButton("Generate");
        bottomPanel.add(this.generateButton);
        this.generateButton.addActionListener(e -> {
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

                String javaCode = jsonToJava.generate(classNameField.getText(), Json.parse(json, JsonObject.class));

                SwingUtilities.invokeLater(() -> {
                    textArea.setText(javaCode);
                });
            });
        });
    }

    public JButton getGenerateButton() {
        return this.generateButton;
    }
}
