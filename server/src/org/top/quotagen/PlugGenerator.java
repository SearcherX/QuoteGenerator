package org.top.quotagen;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class PlugGenerator implements IGenerator {
    ArrayList<String> plugQuotes;

    public PlugGenerator() {
        try {
            plugQuotes = new ArrayList<>(Files.readAllLines(Paths.get("quotes.txt")));
        } catch (IOException e) {
            throw new RuntimeException("Error. Can't read quotes.txt");
        }
    }

    @Override
    public String getRandomQuota() {
        Random r = new Random();
        return plugQuotes.get(r.nextInt(plugQuotes.size()));
    }
}
