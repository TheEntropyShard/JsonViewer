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
