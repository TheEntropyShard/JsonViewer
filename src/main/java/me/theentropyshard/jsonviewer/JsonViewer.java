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
