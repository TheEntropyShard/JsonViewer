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
import me.theentropyshard.jsonviewer.json.GsonJsonFormatter;
import me.theentropyshard.jsonviewer.json.GsonJsonValidator;
import me.theentropyshard.jsonviewer.json.JsonService;

import java.io.IOException;
import java.net.http.HttpClient;
import java.nio.file.Path;
import java.nio.file.Paths;

public class JsonViewer {
    private final JsonService jsonService;
    private final Path configSavePath;
    private final HttpClient httpClient;

    private Config config;

    public JsonViewer() {
        this.configSavePath = Paths.get(System.getProperty("user.home"), "JsonViewer.json");

        try {
            this.config = Config.load(this.configSavePath);
        } catch (IOException e) {
            System.err.println("Unable to load config");
            e.printStackTrace();
            this.config = new Config();
        }

        this.jsonService = new JsonService(new GsonJsonFormatter(), new GsonJsonValidator());

        System.setProperty("https.protocols", "TLSv1,TLSv1.1,TLSv1.2,TLSv1.3");

        this.httpClient = HttpClient.newHttpClient();

        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            try {
                Config.save(this.configSavePath, this.config);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }));

        new Gui(this);
    }

    public static void start() {
        new JsonViewer();
    }

    public Config getConfig() {
        return this.config;
    }

    public JsonService getJsonService() {
        return this.jsonService;
    }

    public HttpClient getHttpClient() {
        return this.httpClient;
    }

    public Path getConfigSavePath() {
        return this.configSavePath;
    }
}
