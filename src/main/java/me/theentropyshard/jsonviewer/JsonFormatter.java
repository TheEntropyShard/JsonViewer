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

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonElement;
import com.google.gson.JsonSyntaxException;
import com.google.gson.stream.JsonWriter;

import java.io.StringWriter;

public class JsonFormatter {
    public static boolean isJsonValid(String json) {
        try {
            new Gson().fromJson(json, JsonElement.class);
        } catch (JsonSyntaxException ignored) {
            return false;
        }

        return true;
    }

    private static String getIndent(int indent) {
        StringBuilder builder = new StringBuilder();
        for (int i = 0; i < indent; i++) {
            builder.append(" ");
        }

        return builder.toString();
    }

    public static String formatJson(String json, int indent) {
        try (StringWriter stringWriter = new StringWriter()) {
            Gson gson = new GsonBuilder().setPrettyPrinting().create();
            JsonWriter writer = gson.newJsonWriter(stringWriter);
            writer.setIndent(JsonFormatter.getIndent(indent));
            JsonElement element = gson.fromJson(json, JsonElement.class);
            gson.toJson(element, writer);
            return stringWriter.toString();
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    public static String minifyJson(String json) {
        Gson gson = new Gson();
        JsonElement element = gson.fromJson(json, JsonElement.class);
        return gson.toJson(element);
    }
}
