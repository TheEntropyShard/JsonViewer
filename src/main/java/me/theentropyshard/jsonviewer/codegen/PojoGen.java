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
