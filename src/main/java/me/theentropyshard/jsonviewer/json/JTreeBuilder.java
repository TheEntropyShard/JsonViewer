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
                    treeNode.add(new DefaultMutableTreeNode(entry.getKey() + ": " + value));
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
