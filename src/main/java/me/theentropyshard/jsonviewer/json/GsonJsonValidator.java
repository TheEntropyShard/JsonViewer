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

import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;

public class GsonJsonValidator implements JsonValidator {
    private final Gson gson;

    public GsonJsonValidator() {
        this.gson = new Gson();
    }

    @Override
    public boolean isJsonValid(String json) {
        try {
            this.gson.fromJson(json, Object.class);
        } catch (JsonSyntaxException e) {
            return false;
        }

        return true;
    }
}
