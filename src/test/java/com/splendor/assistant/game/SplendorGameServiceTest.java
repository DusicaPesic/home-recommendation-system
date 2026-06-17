package com.splendor.assistant.game;

import com.splendor.assistant.model.GemColor;
import com.splendor.assistant.model.PlayerState;
import org.junit.jupiter.api.Test;

import java.util.EnumMap;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class SplendorGameServiceTest {
    @Test
    void letsPlayerDiscardAfterExceedingTokenLimit() {
        SplendorGameService service = new SplendorGameService();
        SplendorGame game = service.currentGame();
        PlayerState player = game.currentPlayer();
        player.setToken(GemColor.BLUE, 3);
        player.setToken(GemColor.GREEN, 3);
        player.setToken(GemColor.RED, 3);

        service.play("take:WHITE+WHITE");

        assertTrue(game.isWaitingForDiscard());
        assertEquals(1, game.getDiscardPlayerNumber());
        assertEquals(1, game.getDiscardCount());
        assertEquals(11, player.getTotalTokens());

        EnumMap<GemColor, Integer> discard = new EnumMap<>(GemColor.class);
        discard.put(GemColor.WHITE, 1);
        service.discardTokens(discard, 0);

        assertFalse(game.isWaitingForDiscard());
        assertEquals(10, player.getTotalTokens());
        assertEquals(2, game.getCurrentPlayerNumber());
        assertEquals(3, game.getBoard().bankTokenCount(GemColor.WHITE));
    }
}
