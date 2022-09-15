package org.top.log;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

public class FileLog {
    private static final File LOG_FILE = new File("log.txt");

    public static void writeLog(String str) {
        try (BufferedWriter bw = new BufferedWriter(new FileWriter(LOG_FILE, true))) {
            bw.write(str);
            bw.newLine();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
