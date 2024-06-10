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

package me.theentropyshard.jsonviewer.codegen;

import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonPrimitive;

import java.util.Map;

public class PojoGen {
    private final Gson gson;
    private final AccessModifier accessModifier;

    public PojoGen(AccessModifier accessModifier) {
        this.gson = new Gson();
        this.accessModifier = accessModifier;
    }

    private PrimitiveType getPrimitiveType(JsonPrimitive primitive) {
        if (primitive.isNumber()) {
            try {
                Integer.parseInt(primitive.getAsString());
                return PrimitiveType.INT;
            } catch (NumberFormatException ignored) {
                try {
                    Long.parseLong(primitive.getAsString());
                    return PrimitiveType.LONG;
                } catch (NumberFormatException ignored1) {
                    return PrimitiveType.DOUBLE;
                }
            }
        } else if (primitive.isString()) {
            return PrimitiveType.STRING;
        } else if (primitive.isBoolean()) {
            return PrimitiveType.BOOLEAN;
        }

        throw new IllegalArgumentException("unexpected json primitive: " + primitive);
    }

    private String generateFields(JsonObject jsonObject) {
        StringBuilder builder = new StringBuilder();

        Map<String, JsonElement> map = jsonObject.asMap();

        for (Map.Entry<String, JsonElement> entry : map.entrySet()) {
            String fieldName = entry.getKey();
            JsonElement jsonElement = entry.getValue();

            builder.append(this.accessModifier.toString()).append(" ");

            if (jsonElement.isJsonPrimitive()) {

                PrimitiveType type = this.getPrimitiveType(jsonElement.getAsJsonPrimitive());
                switch (type) {
                    case INT:
                        builder.append("int");
                        break;
                    case LONG:
                        builder.append("long");
                        break;
                    case DOUBLE:
                        builder.append("double");
                        break;
                    case STRING:
                        builder.append("String");
                        break;
                    case BOOLEAN:
                        builder.append("boolean");
                        break;
                }
            }

            builder.append(" ").append(fieldName).append(";").append("\n");
        }

        return builder.toString();
    }

    public String generate(String rootName, String json) {
        JsonElement jsonElement = this.gson.fromJson(json, JsonElement.class);

        if (!jsonElement.isJsonObject()) {
            return null;
        }

        JsonObject jsonObject = jsonElement.getAsJsonObject();

        return "public class " + rootName + "{\n" + this.generateFields(jsonObject) + "\n}";
    }
}
