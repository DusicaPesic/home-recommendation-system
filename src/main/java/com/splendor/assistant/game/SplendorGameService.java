package com.splendor.assistant.game;

import com.splendor.assistant.model.BoardState;
import com.splendor.assistant.model.Card;
import com.splendor.assistant.model.GameState;
import com.splendor.assistant.model.GemColor;
import com.splendor.assistant.model.Move;
import com.splendor.assistant.model.MoveType;
import com.splendor.assistant.model.Noble;
import com.splendor.assistant.model.PlayerState;
import com.splendor.assistant.model.Recommendation;
import com.splendor.assistant.service.MoveGenerator;
import com.splendor.assistant.service.RecommendationService;
import org.springframework.stereotype.Service;

import java.util.Comparator;
import java.util.List;
import java.util.Optional;

@Service
public class SplendorGameService {
    private final SplendorGameFactory gameFactory;
    private final RecommendationService recommendationService;
    private final MoveGenerator moveGenerator;
    private SplendorGame game;

    public SplendorGameService() {
        this(new SplendorGameFactory(), new RecommendationService(), new MoveGenerator());
    }

    public SplendorGameService(
            SplendorGameFactory gameFactory,
            RecommendationService recommendationService,
            MoveGenerator moveGenerator) {
        this.gameFactory = gameFactory;
        this.recommendationService = recommendationService;
        this.moveGenerator = moveGenerator;
        this.game = gameFactory.newGame();
    }

    public synchronized SplendorGame newGame() {
        game = gameFactory.newGame();
        return game;
    }

    public synchronized SplendorGame currentGame() {
        return game;
    }

    public synchronized Recommendation recommendation() {
        return recommendationService.recommend(game.recommendationState());
    }

    public synchronized MoveApplicationResult play(String moveId) {
        if (game.isFinished()) {
            return new MoveApplicationResult("Game is already finished.");
        }

        GameState state = game.recommendationState();
        List<Move> legalMoves = moveGenerator.generate(state);
        Optional<Move> selected = legalMoves.stream()
                .filter(move -> move.getId().equals(moveId))
                .findFirst();
        if (!selected.isPresent()) {
            throw new IllegalArgumentException("Illegal move: " + moveId);
        }

        Move move = selected.get();
        PlayerState player = game.currentPlayer();
        BoardState board = game.getBoard();

        if (move.getType() == MoveType.TAKE_TOKENS) {
            applyTakeTokens(player, board, move);
        } else if (move.getType() == MoveType.RESERVE_CARD) {
            applyReserve(player, board, move.getCard());
        } else if (move.getType() == MoveType.BUY_CARD) {
            applyBuy(player, board, move);
        }

        String visit = applyNobleVisit(player, board);
        int movedPlayer = game.getCurrentPlayerNumber();
        String message = "Player " + movedPlayer + " played " + move.getId() + "." + visit;
        if (player.getPrestigePoints() >= 15) {
            game.finishWithWinner(movedPlayer);
            message += " Player " + movedPlayer + " wins with " + player.getPrestigePoints() + " points.";
        } else {
            game.switchTurn();
            message += " Player " + game.getCurrentPlayerNumber() + " is on turn.";
        }
        game.setLastEvent(message);
        return new MoveApplicationResult(message);
    }

    private void applyTakeTokens(PlayerState player, BoardState board, Move move) {
        if (player.getTotalTokens() + move.getTakenTokenCount() > 10) {
            throw new IllegalArgumentException("Token limit would be exceeded.");
        }
        for (GemColor color : GemColor.values()) {
            int count = move.getTakenTokens().getOrDefault(color, 0);
            if (count > 0) {
                board.addBankToken(color, -count);
                player.addToken(color, count);
            }
        }
    }

    private void applyReserve(PlayerState player, BoardState board, Card card) {
        board.removeVisibleCard(card);
        player.reserve(card);
        if (board.getBankGoldTokens() > 0) {
            board.addBankGoldTokens(-1);
            player.addGoldTokens(1);
        }
        refill(card.getLevel());
    }

    private void applyBuy(PlayerState player, BoardState board, Move move) {
        Card card = move.getCard();
        payFor(player, board, card);
        if (move.getId().startsWith("buy-reserved:")) {
            player.removeReserved(card);
        } else {
            board.removeVisibleCard(card);
            refill(card.getLevel());
        }
        player.buy(card);
    }

    private void payFor(PlayerState player, BoardState board, Card card) {
        int goldNeeded = 0;
        for (GemColor color : GemColor.values()) {
            int required = Math.max(0, card.costOf(color) - player.bonusCount(color));
            int coloredPayment = Math.min(required, player.tokenCount(color));
            if (coloredPayment > 0) {
                player.addToken(color, -coloredPayment);
                board.addBankToken(color, coloredPayment);
            }
            goldNeeded += required - coloredPayment;
        }
        if (goldNeeded > 0) {
            player.addGoldTokens(-goldNeeded);
            board.addBankGoldTokens(goldNeeded);
        }
    }

    private String applyNobleVisit(PlayerState player, BoardState board) {
        Optional<Noble> visitingNoble = board.getNobles().stream()
                .filter(noble -> qualifiesForNoble(player, noble))
                .sorted(Comparator.comparing(Noble::getId))
                .findFirst();
        if (!visitingNoble.isPresent()) {
            return "";
        }
        Noble noble = visitingNoble.get();
        board.removeNoble(noble);
        player.addPrestigePoints(noble.getPrestigePoints());
        return " Noble " + noble.getId() + " visits for +" + noble.getPrestigePoints() + " points.";
    }

    private boolean qualifiesForNoble(PlayerState player, Noble noble) {
        for (GemColor color : GemColor.values()) {
            if (player.bonusCount(color) < noble.getRequiredBonuses().getOrDefault(color, 0)) {
                return false;
            }
        }
        return true;
    }

    private void refill(int level) {
        if (!game.getDecksByLevel().get(level).isEmpty()) {
            game.getBoard().addVisibleCard(game.getDecksByLevel().get(level).removeFirst());
        }
    }
}
