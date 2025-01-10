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

import javax.swing.*;
import javax.swing.plaf.LayerUI;
import java.awt.*;
import java.awt.event.MouseEvent;

// https://stackoverflow.com/a/38525967/19857533
final class DispatchEventLayerUI extends LayerUI<JPanel> {
    @Override
    public void installUI(JComponent c) {
        super.installUI(c);
        if (c instanceof JLayer) {
            ((JLayer<?>) c).setLayerEventMask(AWTEvent.MOUSE_EVENT_MASK);
        }
    }

    @Override
    public void uninstallUI(JComponent c) {
        if (c instanceof JLayer) {
            ((JLayer<?>) c).setLayerEventMask(0);
        }
        super.uninstallUI(c);
    }

    @Override
    protected void processMouseEvent(MouseEvent e, JLayer<? extends JPanel> l) {
        Component src = e.getComponent();
        Container tgt = SwingUtilities.getAncestorOfClass(JTabbedPane.class, src);
        tgt.dispatchEvent(SwingUtilities.convertMouseEvent(src, e, tgt));
    }
}