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

import com.formdev.flatlaf.util.StringUtils;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonPrimitive;

import java.util.*;

import me.theentropyshard.jsonviewer.utils.Utils;

public class JsonToJava {
    private final String fieldAccessModifier;
    private final NumberType numberType;
    private final String indent;

    private final List<ClassDef> classes;

    public JsonToJava() {
        this(AccessModifier.PRIVATE, NumberType.INT_AND_FLOAT, " ".repeat(4));
    }

    public JsonToJava(AccessModifier fieldAccessModifier, NumberType numberType, String indent) {
        this.fieldAccessModifier = fieldAccessModifier.toString();
        this.numberType = numberType;
        this.indent = indent;

        this.classes = new ArrayList<>();
    }

    public static final class Builder {
        private AccessModifier fieldAccessModifier = AccessModifier.PRIVATE;
        private NumberType numberType = NumberType.INT_AND_FLOAT;
        private String indent = " ".repeat(4);

        public Builder() {

        }

        public Builder accessModifier(AccessModifier modifier) {
            this.fieldAccessModifier = Objects.requireNonNull(modifier);

            return this;
        }

        public Builder numberType(NumberType numberType) {
            this.numberType = Objects.requireNonNull(numberType);

            return this;
        }

        public Builder indent(String indent) {
            this.indent = Objects.requireNonNull(indent);

            return this;
        }

        public JsonToJava build() {
            return new JsonToJava(this.fieldAccessModifier, this.numberType, this.indent);
        }
    }

    public String generate(String topLevelName, JsonObject rootObject) {
        this.parseJsonObject(topLevelName, rootObject);

        Collections.reverse(this.classes);

        return this.generateTopLevelClass(this.generateInnerClasses());
    }

    private String generateTopLevelClass(String innerClasses) {
        ClassDef root = this.classes.get(0);

        StringBuilder rootBuilder = new StringBuilder().append("public class ").append(root.getName()).append(" {\n");

        this.generateClassContent(root, rootBuilder);

        rootBuilder.append("\n");

        for (String line : innerClasses.split("\n")) {
            rootBuilder.append(this.indent).append(line).append("\n");
        }

        rootBuilder.append("}");

        return rootBuilder.toString();
    }

    private String generateInnerClasses() {
        StringBuilder builder = new StringBuilder();

        List<ClassDef> otherTypes = this.classes.subList(1, this.classes.size());

        for (ClassDef clz : otherTypes) {
            builder.append("public static class ").append(clz.getName()).append(" {\n");

            this.generateClassContent(clz, builder);

            builder.append("}\n\n");
        }

        return builder.toString();
    }

    private void generateClassContent(ClassDef clz, StringBuilder builder) {
        for (FieldDef fld : clz.getFields()) {
            this.generateField(fld, builder);
        }

        builder.append("\n");

        this.generateConstructor(clz, builder);

        for (FieldDef fld : clz.getFields()) {
            this.generateGetter(fld, builder);
        }
    }

    private void generateField(FieldDef fld, StringBuilder builder) {
        builder
            .append(this.indent)
            .append(fld.getModifier())
            .append(" ")
            .append(fld.getType())
            .append(" ")
            .append(fld.getName())
            .append(";\n");
    }

    private void generateConstructor(ClassDef clz, StringBuilder builder) {
        builder.append(this.indent).append("public ").append(clz.getName()).append("() {\n");
        builder.append(this.indent).append("\n");
        builder.append(this.indent).append("}\n");
    }

    private void generateGetter(FieldDef fld, StringBuilder builder) {
        builder.append("\n").append(this.indent).append("public").append(" ").append(fld.getType()).append(" ")
            .append("get").append(Utils.capitalize(fld.getName())).append("() {\n")
            .append(this.indent.repeat(2)).append("return this.").append(fld.getName()).append(";\n")
            .append(this.indent).append("}\n");
    }

    public void parseJsonObject(String name, JsonObject object) {
        List<FieldDef> fields = new ArrayList<>();

        for (Map.Entry<String, JsonElement> entry : object.entrySet()) {
            String fieldName = entry.getKey();
            JsonElement element = entry.getValue();
            if (element.isJsonPrimitive()) {
                fields.add(new FieldDef(this.fieldAccessModifier, this.getPrimitiveType(element.getAsJsonPrimitive()), fieldName));
            } else if (element.isJsonNull()) {
                fields.add(new FieldDef(this.fieldAccessModifier, "Object", fieldName));
            } else if (element.isJsonObject()) {
                String className = Utils.capitalize(fieldName);

                fields.add(new FieldDef(this.fieldAccessModifier, className, fieldName));

                this.parseJsonObject(className, element.getAsJsonObject());
            } else if (element.isJsonArray()) {
                JsonArray array = element.getAsJsonArray();

                this.parseJsonArray(fieldName, array, fields);
            }
        }

        this.classes.add(new ClassDef(name, fields));
    }

    private void parseJsonArray(String fieldName, JsonArray array, List<FieldDef> fields) {
        if (array.isEmpty()) {
            fields.add(new FieldDef(this.fieldAccessModifier, "Object[]", fieldName));
        } else {
            JsonElement arrayElement = array.get(0);

            if (arrayElement.isJsonPrimitive()) {
                fields.add(new FieldDef(this.fieldAccessModifier, this.getPrimitiveType(arrayElement.getAsJsonPrimitive()) + "[]", fieldName));
            } else if (arrayElement.isJsonNull()) {
                fields.add(new FieldDef(this.fieldAccessModifier, "Object", fieldName));
            } else if (arrayElement.isJsonObject()) {
                String className = Utils.capitalize(fieldName);

                if (className.endsWith("s")) {
                    className = className.substring(0, className.length() - 1);
                }

                fields.add(new FieldDef(this.fieldAccessModifier, className + "[]", fieldName));

                this.parseJsonObject(className, arrayElement.getAsJsonObject());
            } else if (arrayElement.isJsonArray()) {
                this.parseJsonArray(fieldName, array, fields);
            }
        }
    }

    private String getPrimitiveType(JsonPrimitive primitive) {
        if (primitive.isString()) {
            return "String";
        } else if (primitive.isBoolean()) {
            return "boolean";
        } else if (primitive.isNumber()) {
            return this.getNumberType(primitive.getAsNumber());
        } else {
            throw new RuntimeException("Unreachable");
        }
    }

    private String getNumberType(Number number) {
        return switch (this.numberType) {
            case INT_AND_FLOAT -> {
                if (number.intValue() == (int) Math.ceil(number.floatValue())) {
                    yield "int";
                } else {
                    yield "float";
                }
            }

            case LONG_AND_DOUBLE -> {
                if (number.longValue() == (long) Math.ceil(number.doubleValue())) {
                    yield "long";
                } else {
                    yield "double";
                }
            }
        };
    }
}
