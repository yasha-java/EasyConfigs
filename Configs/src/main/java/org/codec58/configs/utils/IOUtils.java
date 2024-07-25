package org.codec58.configs.utils;

import org.bukkit.Bukkit;
import org.bukkit.plugin.Plugin;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.*;
import java.nio.file.Path;
import java.util.Date;

public class IOUtils {
    public static JSONObject loadConfig(File f, Plugin p, String n) {
        try {
            if (!f.exists())
                return null;

            return new JSONObject(readString(f));
        } catch (JSONException ignored) {
            createInvalidBackup(f, p, n, "invalid_config");
            return null;
        } catch (Throwable ignored) {
            return null;
        }
    }

    public static String readString(File f) {
        try(FileInputStream stream = new FileInputStream(f)) {
            return new String(stream.readAllBytes());
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public static void writeString(File f, String content) {
        try {
            if (!f.exists())
                createFile(f);
            try (FileOutputStream stream = new FileOutputStream(f)) {
                stream.write(content.getBytes());
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public static void createFile(File f) throws IOException {
        var ignored = f.getParentFile().mkdirs();
        ignored = f.createNewFile();
    }

    public static void createInvalidBackup(File whatCreating, Plugin plugin, String configName, String reason) {
        String compiledName =  "%s-%s-%s-%s".formatted(
                plugin.getName(),
                configName,
                new Date().toString(),
                reason
        );
        File fBackup = Path.of(plugin.getDataFolder().toPath().toString(), compiledName).toFile();

        if (fBackup.exists()) {
            var ignored = fBackup.delete();
        }

        try {
            createFile(fBackup);
        } catch (IOException e) {
            Bukkit.getConsoleSender().sendMessage("Can't create '%s' of invalid config!".formatted(compiledName));
            e.printStackTrace(System.err);
            return;
        }

        try(FileOutputStream oStream = new FileOutputStream(fBackup);
            FileInputStream  iStream = new FileInputStream(whatCreating)) {
            oStream.write(iStream.readAllBytes());
        } catch (IOException e) {
            Bukkit.getConsoleSender().sendMessage("Can't create '%s' of invalid config!".formatted(compiledName));
            e.printStackTrace(System.err);
        }
    }
}
