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

package me.theentropyshard.jsonviewer.gui;

import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.Transferable;
import java.awt.dnd.DnDConstants;
import java.awt.dnd.DropTarget;
import java.awt.dnd.DropTargetDropEvent;
import java.io.File;
import java.util.List;

public class FileDropTarget extends DropTarget {
    private final FileDroppedListener listener;

    public FileDropTarget(FileDroppedListener listener) {
        this.listener = listener;
    }

    @Override
    public synchronized void drop(DropTargetDropEvent event) {
        event.acceptDrop(DnDConstants.ACTION_COPY);

        Transferable transferable = event.getTransferable();
        DataFlavor[] flavors = transferable.getTransferDataFlavors();

        for (DataFlavor flavor : flavors) {
            try {
                if (flavor.isFlavorJavaFileListType()) {
                    @SuppressWarnings("unchecked")
                    List<File> files = (List<File>) transferable.getTransferData(flavor);

                    for (File file : files) {
                        this.listener.onFileDropped(file);
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        event.dropComplete(true);
    }

    public interface FileDroppedListener {
        void onFileDropped(File file);
    }
}
