package com.splendor.assistant.service;

import org.drools.template.DataProviderCompiler;
import org.drools.template.objects.ArrayDataProvider;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

public class ScoringTemplateRules {
    private static final String TEMPLATE_PATH = "/templates/scoring-rules.drt";
    private static final String DATA_PATH = "/templates/scoring-template-data.csv";

    public String generateDrl() {
        try (InputStream template = resource(TEMPLATE_PATH)) {
            DataProviderCompiler compiler = new DataProviderCompiler();
            return compiler.compile(new ArrayDataProvider(readRows()), template);
        } catch (IOException e) {
            throw new IllegalStateException("Could not generate scoring rules from template", e);
        }
    }

    private String[][] readRows() throws IOException {
        List<String[]> rows = new ArrayList<>();
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(resource(DATA_PATH), StandardCharsets.UTF_8))) {
            String line;
            boolean header = true;
            while ((line = reader.readLine()) != null) {
                if (line.trim().isEmpty()) {
                    continue;
                }
                if (header) {
                    header = false;
                    continue;
                }
                rows.add(parseRow(line));
            }
        }
        return rows.toArray(new String[0][]);
    }

    private String[] parseRow(String line) {
        String[] columns = line.split("\\|", -1);
        for (int i = 0; i < columns.length; i++) {
            columns[i] = columns[i].replace("\\n", System.lineSeparator());
        }
        return columns;
    }

    private InputStream resource(String path) {
        InputStream stream = ScoringTemplateRules.class.getResourceAsStream(path);
        if (stream == null) {
            throw new IllegalStateException("Missing resource " + path);
        }
        return stream;
    }
}
