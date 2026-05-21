package com.splendor.assistant.service;

import org.kie.api.builder.Message;
import org.kie.api.builder.Results;
import org.kie.api.io.ResourceType;
import org.kie.api.runtime.KieSession;
import org.kie.internal.utils.KieHelper;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.List;

public class SplendorKieSessionFactory {
    private static final String[] RULE_RESOURCES = {
            "/rules/01-analysis-rules.drl",
            "/rules/02-strategy-rules.drl",
            "/rules/03-scoring-rules.drl",
            "/rules/04-explanation-queries.drl"
    };

    private final ScoringTemplateRules scoringTemplateRules;

    public SplendorKieSessionFactory() {
        this(new ScoringTemplateRules());
    }

    public SplendorKieSessionFactory(ScoringTemplateRules scoringTemplateRules) {
        this.scoringTemplateRules = scoringTemplateRules;
    }

    public KieSession newKieSession() {
        KieHelper helper = new KieHelper();
        for (String path : RULE_RESOURCES) {
            helper.addContent(readResource(path), ResourceType.DRL);
        }
        helper.addContent(scoringTemplateRules.generateDrl(), ResourceType.DRL);

        Results results = helper.verify();
        if (results.hasMessages(Message.Level.WARNING, Message.Level.ERROR)) {
            List<Message> messages = results.getMessages(Message.Level.WARNING, Message.Level.ERROR);
            throw new IllegalStateException("Rule compilation failed: " + messages);
        }
        return helper.build().newKieSession();
    }

    private String readResource(String path) {
        try (InputStream stream = SplendorKieSessionFactory.class.getResourceAsStream(path)) {
            if (stream == null) {
                throw new IllegalStateException("Missing resource " + path);
            }
            byte[] bytes = stream.readAllBytes();
            return new String(bytes, StandardCharsets.UTF_8);
        } catch (IOException e) {
            throw new IllegalStateException("Could not read rule resource " + path, e);
        }
    }
}
