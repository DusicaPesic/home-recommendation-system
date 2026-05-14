package com.splendor.assistant.model.explanation;

public class ScoreLine {
    private final int points;
    private final String reason;

    public ScoreLine(int points, String reason) {
        this.points = points;
        this.reason = reason;
    }

    public int getPoints() {
        return points;
    }

    public String getReason() {
        return reason;
    }

    public boolean isPositive() {
        return points >= 0;
    }

    public String getFormattedPoints() {
        if (points > 0) {
            return "+" + points;
        }
        return String.valueOf(points);
    }
}