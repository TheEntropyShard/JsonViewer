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

public class FieldDef {
    private final String modifier;
    private final String type;
    private final String name;

    public FieldDef(String modifier, String type, String name) {
        this.modifier = modifier;
        this.type = type;
        this.name = name;
    }

    @Override
    public String toString() {
        return "FieldDef{" +
            "modifier='" + this.modifier + '\'' +
            ", type='" + this.type + '\'' +
            ", name='" + this.name + '\'' +
            '}';
    }

    public String getModifier() {
        return this.modifier;
    }

    public String getType() {
        return this.type;
    }

    public String getName() {
        return this.name;
    }
}
