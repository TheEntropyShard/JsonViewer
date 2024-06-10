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

package me.theentropyshard.jsonviewer.json;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonElement;
import com.google.gson.stream.JsonWriter;

import java.io.StringWriter;

public class GsonJsonFormatter implements JsonFormatter {
    private final Gson gson;
    private final Gson prettyGson;

    public GsonJsonFormatter() {
        this.gson = new GsonBuilder()
                .disableHtmlEscaping()
                .create();
        this.prettyGson = this.gson.newBuilder()
                .setPrettyPrinting()
                .create();
    }

    private static String getIndent(int indent) {
        if (indent < 1) {
            throw new IllegalArgumentException("Indent cannot be less than 1 space");
        }

        switch (indent) {
            case 1:
                return " ";
            case 2:
                return "  ";
            case 3:
                return "   ";
            case 4:
                return "    ";
        }

        StringBuilder builder = new StringBuilder();
        for (int i = 0; i < indent; i++) {
            builder.append(" ");
        }

        return builder.toString();
    }

    @Override
    public String formatJson(String json, int indent) {
        try (StringWriter stringWriter = new StringWriter()) {
            JsonWriter writer = this.prettyGson.newJsonWriter(stringWriter);
            writer.setIndent(GsonJsonFormatter.getIndent(indent));
            JsonElement element = this.prettyGson.fromJson(json, JsonElement.class);
            this.prettyGson.toJson(element, writer);
            return stringWriter.toString();
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    @Override
    public String minifyJson(String json) {
        return this.gson.toJson(this.gson.fromJson(json, JsonElement.class));
    }
}
