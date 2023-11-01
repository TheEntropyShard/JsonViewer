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

package me.theentropyshard.jsonviewer.gui.treeview;

import me.theentropyshard.jsonviewer.gui.MainView;

import javax.swing.*;
import javax.swing.tree.TreeModel;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.Transferable;
import java.awt.dnd.DnDConstants;
import java.awt.dnd.DropTarget;
import java.awt.dnd.DropTargetDropEvent;
import java.io.File;
import java.util.List;

public class JsonTreeView extends JScrollPane {
    private final MainView mainView;
    private final JTree jsonTree;

    public JsonTreeView(MainView mainView) {
        this.mainView = mainView;
        this.jsonTree = this.makeJsonTree();
        this.setViewportView(this.jsonTree);
    }

    private JTree makeJsonTree() {
        JTree jsonTree = new JTree();
        jsonTree.setShowsRootHandles(true);
        jsonTree.setRootVisible(true);
        jsonTree.addMouseListener(new TheMouseListener(jsonTree));

        jsonTree.setDropTarget(new DropTarget() {
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
                                JsonTreeView.this.mainView.addTab(file);
                            }
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }

                event.dropComplete(true);
            }
        });

        return jsonTree;
    }

    public void setModel(TreeModel treeModel) {
        this.jsonTree.setModel(treeModel);
    }
}
