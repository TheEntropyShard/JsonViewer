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

package me.theentropyshard.jsonviewer.gui;

import me.theentropyshard.jsonviewer.config.Config;

import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.IOException;
import java.nio.file.Path;

public class SaveConfigListener extends WindowAdapter {
    private final Path savePath;
    private final Config config;

    public SaveConfigListener(Path savePath, Config config) {
        this.savePath = savePath;
        this.config = config;
    }

    @Override
    public void windowClosing(WindowEvent e) {
        try {
            Config.save(this.savePath, this.config);
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }
}
