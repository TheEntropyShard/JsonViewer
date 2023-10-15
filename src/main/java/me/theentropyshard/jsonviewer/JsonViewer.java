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

package me.theentropyshard.jsonviewer;

import me.theentropyshard.jsonviewer.config.Config;
import me.theentropyshard.jsonviewer.gui.Gui;

import java.io.IOException;
import java.nio.file.Paths;

public class JsonViewer {
    private final Config config;

    public JsonViewer() {
        this.config = new Config(Paths.get(System.getProperty("user.home"), "JsonViewer.json"));

        try {
            this.config.load();
        } catch (IOException e) {
            System.err.println("Unable to load config");
            e.printStackTrace();
        }

        new Gui(this);
    }

    public static void start() {
        new JsonViewer();
    }

    public Config getConfig() {
        return this.config;
    }
}
