package com.splendor.assistant.game;

import com.splendor.assistant.model.Card;
import com.splendor.assistant.model.GemColor;
import com.splendor.assistant.model.Noble;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.EnumMap;
import java.util.List;
import java.util.Map;

public class SplendorCatalog {
    private static final String CARDS_PATH = "/data/splendor-cards.csv";
    private static final String NOBLES_PATH = "/data/nobles.csv";

    public List<Card> loadCards() {
        List<Card> cards = new ArrayList<>();
        try (BufferedReader reader = reader(CARDS_PATH)) {
            String line;
            boolean header = true;
            int index = 1;
            while ((line = reader.readLine()) != null) {
                if (header) {
                    header = false;
                    continue;
                }
                if (line.trim().isEmpty()) {
                    continue;
                }
                String[] columns = line.split(",", -1);
                int level = Integer.parseInt(columns[0].trim());
                GemColor bonus = color(columns[1]);
                int points = Integer.parseInt(columns[2].trim());
                Map<GemColor, Integer> cost = counts(
                        columns[7], columns[4], columns[5], columns[6], columns[3]);
                cards.add(new Card("L" + level + "-" + bonus + "-" + index, level, bonus, points, cost));
                index++;
            }
            return cards;
        } catch (IOException e) {
            throw new IllegalStateException("Could not load development cards", e);
        }
    }

    public List<Noble> loadNobles() {
        List<Noble> nobles = new ArrayList<>();
        try (BufferedReader reader = reader(NOBLES_PATH)) {
            String line;
            boolean header = true;
            while ((line = reader.readLine()) != null) {
                if (header) {
                    header = false;
                    continue;
                }
                if (line.trim().isEmpty()) {
                    continue;
                }
                String[] columns = line.split(",", -1);
                nobles.add(new Noble(
                        "NOBLE-" + columns[0].trim(),
                        counts(columns[2], columns[3], columns[4], columns[5], columns[6]),
                        Integer.parseInt(columns[1].trim())));
            }
            return nobles;
        } catch (IOException e) {
            throw new IllegalStateException("Could not load nobles", e);
        }
    }

    private BufferedReader reader(String path) {
        InputStream stream = SplendorCatalog.class.getResourceAsStream(path);
        if (stream == null) {
            throw new IllegalStateException("Missing resource " + path);
        }
        return new BufferedReader(new InputStreamReader(stream, StandardCharsets.UTF_8));
    }

    private GemColor color(String value) {
        return GemColor.valueOf(value.trim().toUpperCase());
    }

    private Map<GemColor, Integer> counts(String white, String blue, String green, String red, String black) {
        EnumMap<GemColor, Integer> counts = new EnumMap<>(GemColor.class);
        counts.put(GemColor.WHITE, Integer.parseInt(white.trim()));
        counts.put(GemColor.BLUE, Integer.parseInt(blue.trim()));
        counts.put(GemColor.GREEN, Integer.parseInt(green.trim()));
        counts.put(GemColor.RED, Integer.parseInt(red.trim()));
        counts.put(GemColor.BLACK, Integer.parseInt(black.trim()));
        return counts;
    }
}
