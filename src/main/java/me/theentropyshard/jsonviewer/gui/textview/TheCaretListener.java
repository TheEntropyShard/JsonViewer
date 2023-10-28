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