/*
 * JsonViewer - https://github.com/TheEntropyShard/JsonViewer
 * Copyright (C) 2023-2025 TheEntropyShard
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

public class JsonService {
    private final JsonFormatter jsonFormatter;
    private final JsonValidator jsonValidator;

    public JsonService(JsonFormatter jsonFormatter, JsonValidator jsonValidator) {
        this.jsonFormatter = jsonFormatter;
        this.jsonValidator = jsonValidator;
    }

    public boolean isJsonValid(String json) {
        return this.jsonValidator.isJsonValid(json);
    }

    public String formatJson(String json, int indent) {
        return this.jsonFormatter.formatJson(json, indent);
    }

    public String minifyJson(String json) {
        return this.jsonFormatter.minifyJson(json);
    }
}
