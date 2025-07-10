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

import java.util.List;

public class ClassDef {
    private final String name;
    private final List<FieldDef> fields;

    public ClassDef(String name, List<FieldDef> fields) {
        this.name = name;
        this.fields = fields;
    }

    @Override
    public String toString() {
        return "ClassDef{" +
            "name='" + this.name + '\'' +
            ", fields=" + this.fields +
            '}';
    }

    public String getName() {
        return this.name;
    }

    public List<FieldDef> getFields() {
        return this.fields;
    }
}
