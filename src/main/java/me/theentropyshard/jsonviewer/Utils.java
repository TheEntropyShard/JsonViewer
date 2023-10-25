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

package me.theentropyshard.jsonviewer;

import me.theentropyshard.jsonviewer.exception.NonJsonContentTypeException;

import java.awt.*;
import java.io.*;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.util.Objects;

public class Utils {
    public static final String USER_AGENT = "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/117.0.0.0 Safari/537.36";

    public static void centerWindow(Window window, int screen) {
        GraphicsEnvironment env = GraphicsEnvironment.getLocalGraphicsEnvironment();
        GraphicsDevice[] allDevices = env.getScreenDevices();

        if (screen < 0 || screen >= allDevices.length) {
            screen = 0;
        }

        Rectangle bounds = allDevices[screen].getDefaultConfiguration().getBounds();
        window.setLocation(
                ((bounds.width - window.getWidth()) / 2) + bounds.x,
                ((bounds.height - window.getHeight()) / 2) + bounds.y
        );
    }

    public static String readResource(String path) throws IOException {
        try (InputStream inputStream = Utils.class.getResourceAsStream(path)) {
            return Utils.inputStreamToString(Objects.requireNonNull(inputStream));
        }
    }

    public static String readFile(File file) throws IOException {
        return Utils.inputStreamToString(Files.newInputStream(file.toPath()));
    }

    public static String readURL(String url) throws IOException, NonJsonContentTypeException {
        HttpURLConnection c = (HttpURLConnection) new URL(url).openConnection();
        c.setRequestMethod("GET");
        c.setRequestProperty("Accept", "application/json");
        c.setRequestProperty("User-Agent", Utils.USER_AGENT);

        int responseCode = c.getResponseCode();
        if (responseCode == HttpURLConnection.HTTP_OK) {
            String headerField = c.getHeaderField("Content-Type");
            if (!headerField.startsWith("application/json")) {
                throw new NonJsonContentTypeException(headerField);
            }
            InputStream inputStream = c.getInputStream();
            return Utils.inputStreamToString(inputStream);
        } else {
            throw new IOException("Server returned status code " + responseCode);
        }
    }

    public static double round(double value, int places) {
        if (places < 0) throw new IllegalArgumentException();

        long factor = (long) Math.pow(10, places);
        value = value * factor;
        long tmp = Math.round(value);
        return (double) tmp / factor;
    }

    public static String inputStreamToString(InputStream inputStream) throws IOException {
        if (!(inputStream instanceof BufferedInputStream)) {
            inputStream = new BufferedInputStream(inputStream);
        }

        try (ByteArrayOutputStream outputStream = new ByteArrayOutputStream()) {
            byte[] buffer = new byte[2048];
            int numRead;
            while ((numRead = inputStream.read(buffer)) != -1) {
                outputStream.write(buffer, 0, numRead);
            }

            return new String(outputStream.toByteArray(), StandardCharsets.UTF_8);
        }
    }

    public static String getLastPathComponent(String name) {
        return name.substring(name.lastIndexOf("/") + 1);
    }

    public static boolean isUrlInvalid(String url) {
        try {
            new URL(url).toURI();
        } catch (URISyntaxException | MalformedURLException e) {
            return true;
        }

        return false;
    }
}
