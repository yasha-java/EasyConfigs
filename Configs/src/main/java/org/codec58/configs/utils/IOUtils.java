package org.codec58.configs.utils;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;

public class IOUtils {
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
}
