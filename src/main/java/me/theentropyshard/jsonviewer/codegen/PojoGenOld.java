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

package me.theentropyshard.jsonviewer.codegen;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonPrimitive;
import me.theentropyshard.jsonviewer.utils.Utils;

import java.util.*;

public class PojoGenOld {
    private static final String INDENT = "    ";

    private final Queue<String> clazzDefs;

    public PojoGenOld() {
        this.clazzDefs = new ArrayDeque<>();
    }

    private void indent(int times, StringBuilder builder) {
        for (int i = 0; i < times; i++) {
            builder.append(PojoGenOld.INDENT);
        }
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

    private void generatePrimitiveField(String key, JsonElement value, int times, StringBuilder builder) {
        this.indent(times, builder);

        PrimitiveType type = this.getPrimitiveType(value.getAsJsonPrimitive());

        switch (type) {
            case INT:
                builder.append("int ");
                break;
            case DOUBLE:
                builder.append("double ");
                break;
            case LONG:
                builder.append("long ");
                break;
            case STRING:
                builder.append("String ");
                break;
            case BOOLEAN:
                builder.append("boolean ");
                break;
        }

        builder.append(key).append(";\n");
    }

    private void generateListField(String key, JsonElement value, int times, String fieldName, StringBuilder builder) {
        this.indent(times, builder);

        builder.append("List<");

        JsonArray jsonArray = value.getAsJsonArray();
        if (jsonArray.size() > 0) {
            JsonElement jsonElement = jsonArray.get(0);
            if (jsonElement.isJsonPrimitive()) {
                PrimitiveType type = this.getPrimitiveType(jsonElement.getAsJsonPrimitive());
                switch (type) {
                    case INT:
                        builder.append("Integer");
                        break;
                    case LONG:
                        builder.append("Long");
                        break;
                    case DOUBLE:
                        builder.append("Double");
                        break;
                    case STRING:
                        builder.append("String");
                        break;
                    case BOOLEAN:
                        builder.append("Boolean");
                        break;
                }
            } else if (jsonElement.isJsonObject()) {
                JsonObject jsonObject = jsonElement.getAsJsonObject();

                String clazzName = Utils.capitalize(fieldName);
                if (clazzName.endsWith("ies")) {
                    clazzName = clazzName.substring(0, clazzName.length() - 3) + "y";
                } else if (clazzName.endsWith("s")) {
                    clazzName = clazzName.substring(0, clazzName.length() - 1);
                }

                builder.append(clazzName);
                this.clazzDefs.add(this.generateClassDef(clazzName, jsonObject, times + 1));
            }
        } else {
            builder.append("Object");
        }

        builder.append("> ").append(key).append(";\n");
    }

    public String generateClassDef(String name, JsonElement root, int indent) {
        StringBuilder builder = new StringBuilder();

        if (indent == 1) {
            builder.append("public class ").append(name).append(" {\n");
        } else {
            this.indent(1, builder);
            builder.append("public static final class ").append(name).append(" {\n");
        }

        if (root.isJsonObject()) {
            JsonObject jsonObject = root.getAsJsonObject();
            Map<String, JsonElement> map = jsonObject.asMap();

            for (Map.Entry<String, JsonElement> entry : map.entrySet()) {
                String key = entry.getKey();
                JsonElement value = entry.getValue();

                if (value.isJsonPrimitive()) {
                    this.generatePrimitiveField(key, value, indent, builder);
                } else if (value.isJsonArray()) {
                    this.generateListField(key, value, indent, key, builder);
                } else if (value.isJsonObject()) {
                    this.indent(indent, builder);

                    String clazzName = Utils.capitalize(key);

                    builder.append(clazzName).append(" ").append(key).append(";\n");
                    this.clazzDefs.add(this.generateClassDef(clazzName, value, ++indent));

                    indent--;
                }
            }
        }

        if (indent > 1) {
            this.indent(1, builder);
            builder.append("}");
        } else {
            if (this.clazzDefs.size() > 0) {
                builder.append("\n");

                for (String clazzDef : this.clazzDefs) {
                    builder.append(clazzDef).append("\n\n");
                }

                builder.deleteCharAt(builder.length() - 1);
                builder.append("}");
            }
        }

        return builder.toString();
    }

    public String generate(JsonElement root) {
        return this.generateClassDef("Root", root, 1);
    }
}
