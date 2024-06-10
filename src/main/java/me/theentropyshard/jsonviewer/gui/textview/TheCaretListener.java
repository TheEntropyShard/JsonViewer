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

import org.fife.ui.rsyntaxtextarea.RSyntaxTextArea;

import javax.swing.*;
import javax.swing.event.CaretEvent;
import javax.swing.event.CaretListener;
import javax.swing.text.BadLocationException;
import java.util.List;

final class TheCaretListener implements CaretListener {
    private final RSyntaxTextArea textArea;
    private final List<CaretUpdateListener> caretListeners;

    public TheCaretListener(RSyntaxTextArea textArea, List<CaretUpdateListener> caretListeners) {
        this.textArea = textArea;
        this.caretListeners = caretListeners;
    }

    @Override
    public void caretUpdate(CaretEvent e) {
        int caretPosition = this.textArea.getCaretPosition();

        int lineNumber = TheCaretListener.getLineOfOffset(this.textArea, caretPosition);
        int columnNumber = TheCaretListener.getColumnNumber(this.textArea, caretPosition, lineNumber);

        for (CaretUpdateListener listener : this.caretListeners) {
            listener.onCaretUpdate(++lineNumber, ++columnNumber);
        }
    }

    private static int getLineOfOffset(JTextArea textArea, int caretPosition) {
        int lineOfOffset = 1;

        try {
            lineOfOffset = textArea.getLineOfOffset(caretPosition);
        } catch (BadLocationException ignored) {

        }

        return lineOfOffset;
    }

    private static int getColumnNumber(JTextArea textArea, int caretPosition, int lineNum) {
        int columnNum = 0;

        try {
            columnNum = caretPosition - textArea.getLineStartOffset(lineNum);
        } catch (BadLocationException ignored) {

        }

        return columnNum;
    }
}