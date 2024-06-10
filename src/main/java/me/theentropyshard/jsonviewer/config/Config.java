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

package me.theentropyshard.jsonviewer.config;

import me.theentropyshard.jsonviewer.utils.IOUtils;
import me.theentropyshard.jsonviewer.utils.Json;

import java.io.IOException;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

public class Config {
    private int beautifyIndent = 3;
    private List<String> recentUrls = new ArrayList<>();
    private List<String> recentFiles = new ArrayList<>();

    public Config() {

    }

    public static Config load(Path file) throws IOException {
        return Json.parse(IOUtils.readUtf8String(file), Config.class);
    }

    public static void save(Path file, Config config) throws IOException {
        IOUtils.writeUtf8String(file, Json.write(config));
    }

    public int getBeautifyIndent() {
        return this.beautifyIndent;
    }

    public void setBeautifyIndent(int beautifyIndent) {
        this.beautifyIndent = beautifyIndent;
    }

    public List<String> getRecentUrls() {
        return this.recentUrls;
    }

    public void setRecentUrls(List<String> recentUrls) {
        this.recentUrls = recentUrls;
    }

    public List<String> getRecentFiles() {
        return this.recentFiles;
    }

    public void setRecentFiles(List<String> recentFiles) {
        this.recentFiles = recentFiles;
    }
}
