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
