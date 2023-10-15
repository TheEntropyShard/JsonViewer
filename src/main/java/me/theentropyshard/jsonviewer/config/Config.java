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

import com.google.gson.Gson;

import java.io.BufferedReader;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.Map;

public class Config {
    private final Path file;
    private final Gson gson;
    private final Map<String, String> data;

    public Config(Path file) {
        this.file = file;
        this.gson = new Gson();
        this.data = new HashMap<>();
    }

    public String getValue(String key) {
        return this.data.get(key);
    }

    public String getValue(String key, String def) {
        String value = this.getValue(key);
        if (value == null) {
            return def;
        } else {
            return value;
        }
    }

    public void setValue(String key, String value) {
        this.data.put(key, value);
    }

    private void prepareFile() throws IOException {
        Files.createDirectories(this.file.getParent());
        if (!Files.exists(this.file)) {
            Files.createFile(this.file);
        }
    }

    public void load() throws IOException {
        this.prepareFile();
        this.clear();

        BufferedReader reader = Files.newBufferedReader(this.file, StandardCharsets.UTF_8);
        @SuppressWarnings("unchecked")
        Map<String, String> data = this.gson.fromJson(reader, Map.class);
        if (data == null) {
            return;
        }
        this.data.putAll(data);
    }

    public void save() throws IOException {
        this.prepareFile();
        Files.write(this.file, this.gson.toJson(this.data).getBytes(StandardCharsets.UTF_8));
    }

    public void clear() {
        this.data.clear();
    }
}
