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

package me.theentropyshard.jsonviewer.gui.textview;

import me.theentropyshard.jsonviewer.gui.FileDropTarget;
import me.theentropyshard.jsonviewer.gui.MainView;
import org.fife.ui.rsyntaxtextarea.RSyntaxTextArea;
import org.fife.ui.rsyntaxtextarea.SyntaxScheme;
import org.fife.ui.rsyntaxtextarea.Token;
import org.fife.ui.rtextarea.ExpandedFoldRenderStrategy;
import org.fife.ui.rtextarea.RTextScrollPane;

import java.awt.*;
import java.util.ArrayList;
import java.util.List;

public class JsonTextView extends RTextScrollPane {
    private final MainView mainView;
    private final List<CaretUpdateListener> caretListeners;
    private final RSyntaxTextArea textArea;

    public JsonTextView(MainView mainView) {
        this.mainView = mainView;
        this.caretListeners = new ArrayList<>();
        this.textArea = this.makeTextArea();
        this.setViewportView(this.textArea);
        this.setLineNumbersEnabled(true);
        this.setFoldIndicatorEnabled(true);
        this.getGutter().setExpandedFoldRenderStrategy(ExpandedFoldRenderStrategy.ALWAYS);
    }

    public void clear() {
        this.setText("");
    }

    private RSyntaxTextArea makeTextArea() {
        RSyntaxTextArea textArea = new RSyntaxTextArea();
        textArea.setLineWrap(true);
        textArea.setCodeFoldingEnabled(true);
        textArea.setBracketMatchingEnabled(true);
        textArea.setShowMatchedBracketPopup(true);
        textArea.setSyntaxEditingStyle(RSyntaxTextArea.SYNTAX_STYLE_JSON);

        SyntaxScheme scheme = textArea.getSyntaxScheme();
        scheme.getStyle(Token.LITERAL_STRING_DOUBLE_QUOTE).foreground = Color.decode("#2a9152");
        scheme.getStyle(Token.SEPARATOR).foreground = Color.GRAY;
        scheme.getStyle(Token.VARIABLE).foreground = Color.DARK_GRAY;

        textArea.setFont(textArea.getFont().deriveFont(14.0f));
        textArea.revalidate();

        textArea.addCaretListener(new TheCaretListener(textArea, this.caretListeners));
        textArea.setDropTarget(new FileDropTarget(this.mainView::addTab));

        return textArea;
    }

    public void addCaretUpdateListener(CaretUpdateListener listener) {
        this.caretListeners.add(listener);
    }

    public void scrollToTop() {
        this.textArea.setCaretPosition(0);
    }

    public String getText() {
        return this.textArea.getText();
    }

    public void setText(String text) {
        this.textArea.setText(text);
    }
}
