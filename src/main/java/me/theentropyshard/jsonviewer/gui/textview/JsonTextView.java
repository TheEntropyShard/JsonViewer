/*
 * Copyright 2023 TheEntropyShard (https://github.com/TheEntropyShard)
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package me.theentropyshard.jsonviewer.gui.textview;

import me.theentropyshard.jsonviewer.gui.MainView;
import org.fife.ui.rsyntaxtextarea.RSyntaxTextArea;
import org.fife.ui.rsyntaxtextarea.SyntaxScheme;
import org.fife.ui.rsyntaxtextarea.Token;
import org.fife.ui.rtextarea.RTextScrollPane;

import java.awt.*;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.Transferable;
import java.awt.dnd.DnDConstants;
import java.awt.dnd.DropTarget;
import java.awt.dnd.DropTargetDropEvent;
import java.io.File;
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
    }

    private RSyntaxTextArea makeTextArea() {
        RSyntaxTextArea textArea = new RSyntaxTextArea();
        textArea.setLineWrap(true);
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

        textArea.setDropTarget(new DropTarget() {
            @Override
            public void drop(DropTargetDropEvent event) {
                event.acceptDrop(DnDConstants.ACTION_COPY);
                Transferable transferable = event.getTransferable();
                DataFlavor[] flavors = transferable.getTransferDataFlavors();
                for (DataFlavor flavor : flavors) {
                    try {
                        if (flavor.isFlavorJavaFileListType()) {
                            @SuppressWarnings("unchecked")
                            List<File> files = (List<File>) transferable.getTransferData(flavor);
                            for (File file : files) {
                                JsonTextView.this.mainView.addTab(file);
                            }
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }

                event.dropComplete(true);
            }
        });

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
