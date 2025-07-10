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

package me.theentropyshard.jsonviewer.config;

import me.theentropyshard.jsonviewer.utils.IOUtils;
import me.theentropyshard.jsonviewer.utils.Json;
import me.theentropyshard.jsonviewer.utils.MathUtils;

import java.io.IOException;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

public class Config {
    private int beautifyIndent = 3;
    private List<String> recentUrls = new ArrayList<>();
    private List<String> recentFiles = new ArrayList<>();

    private int pojoAccessModifier = 1;
    private int pojoNumberType = 0;
    private int pojoIndent = 3;
    private int pojoBooleanGetterPrefix = 1;
    private boolean pojoUseAnnotations = false;
    private boolean pojoGenerateGetters = true;

    public Config() {

    }

    public static Config load(Path file) throws IOException {
        return Json.parse(IOUtils.readUtf8String(file), Config.class);
    }

    public static void save(Path file, Config config) throws IOException {
        IOUtils.writeUtf8String(file, Json.write(config));
    }

    public int getBeautifyIndent() {
        return this.beautifyIndent;
    }

    public void setBeautifyIndent(int beautifyIndent) {
        this.beautifyIndent = beautifyIndent;
    }

    public List<String> getRecentUrls() {
        return this.recentUrls;
    }

    public List<String> getRecentFiles() {
        return this.recentFiles;
    }

    public void addRecentUrl(String url) {
        if (this.recentUrls.contains(url)) {
            this.recentUrls.remove(url);
            this.recentUrls.add(0, url);

            return;
        }

        this.recentUrls.add(0, url);
        if (this.recentUrls.size() > 10) {
            this.recentUrls = this.recentUrls.subList(0, 10);
        }
    }

    public void removeRecentUrl(String url) {
        this.recentUrls.removeIf(s -> s.equals(url));
    }

    public void addRecentFile(String path) {
        if (this.recentFiles.contains(path)) {
            this.recentFiles.remove(path);
            this.recentFiles.add(0, path);

            return;
        }

        this.recentFiles.add(0, path);
        if (this.recentFiles.size() > 10) {
            this.recentFiles = this.recentFiles.subList(0, 10);
        }
    }

    public void removeRecentFile(String path) {
        this.recentFiles.removeIf(s -> s.equals(path));
    }

    public int getPojoAccessModifier() {
        return MathUtils.clamp(0, 1, this.pojoAccessModifier);
    }

    public void setPojoAccessModifier(int pojoAccessModifier) {
        this.pojoAccessModifier = MathUtils.clamp(0, 1, pojoAccessModifier);
    }

    public int getPojoNumberType() {
        return MathUtils.clamp(0, 1, this.pojoNumberType);
    }

    public void setPojoNumberType(int pojoNumberType) {
        this.pojoNumberType = MathUtils.clamp(0, 1, pojoNumberType);
    }

    public int getPojoIndent() {
        return MathUtils.clamp(0, 3, this.pojoIndent);
    }

    public void setPojoIndent(int pojoIndent) {
        this.pojoIndent = MathUtils.clamp(0, 3, pojoIndent);
    }

    public int getPojoBooleanGetterPrefix() {
        return MathUtils.clamp(0, 1, this.pojoBooleanGetterPrefix);
    }

    public void setPojoBooleanGetterPrefix(int pojoBooleanGetterPrefix) {
        this.pojoBooleanGetterPrefix = MathUtils.clamp(0, 1, pojoBooleanGetterPrefix);
    }

    public boolean isPojoUseAnnotations() {
        return this.pojoUseAnnotations;
    }

    public void setPojoUseAnnotations(boolean pojoUseAnnotations) {
        this.pojoUseAnnotations = pojoUseAnnotations;
    }

    public boolean isPojoGenerateGetters() {
        return this.pojoGenerateGetters;
    }

    public void setPojoGenerateGetters(boolean pojoGenerateGetters) {
        this.pojoGenerateGetters = pojoGenerateGetters;
    }
}
