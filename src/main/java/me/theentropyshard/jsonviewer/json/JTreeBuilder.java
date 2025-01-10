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

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

import javax.swing.tree.DefaultMutableTreeNode;
import java.util.Map;

public class JTreeBuilder {
    public static DefaultMutableTreeNode buildTree(String name, JsonElement root) {
        DefaultMutableTreeNode treeNode = new DefaultMutableTreeNode(name);

        if (root == null) {
            return treeNode;
        }

        if (root.isJsonArray()) {
            JsonArray jsonArray = root.getAsJsonArray();
            for (int i = 0; i < jsonArray.size(); i++) {
                JsonElement jsonElement = jsonArray.get(i);
                if (jsonElement.isJsonPrimitive()) {
                    treeNode.add(new DefaultMutableTreeNode("[" + i + "]: " + jsonElement));
                } else {
                    treeNode.add(buildTree(String.format("[%d]", i), jsonElement));
                }
            }
        } else if (root.isJsonObject()) {
            JsonObject jsonObject = root.getAsJsonObject();
            Map<String, JsonElement> map = jsonObject.asMap();
            for (Map.Entry<String, JsonElement> entry : map.entrySet()) {
                JsonElement value = entry.getValue();
                if (value.isJsonPrimitive()) {
                    treeNode.add(new DefaultMutableTreeNode(new JsonPair<>(entry.getKey(), value)));
                } else {
                    String nodeName = entry.getKey();
                    if (value.isJsonArray()) {
                        nodeName = entry.getKey() + " [" + value.getAsJsonArray().size() + "]";
                    }
                    treeNode.add(buildTree(nodeName, value));
                }
            }
        }

        return treeNode;
    }
}
