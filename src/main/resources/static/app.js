const COLORS = ["WHITE", "BLUE", "GREEN", "RED", "BLACK"];
const COLOR_LABELS = {
  WHITE: "White",
  BLUE: "Blue",
  GREEN: "Green",
  RED: "Red",
  BLACK: "Black"
};
const COLOR_SYMBOLS = {
  WHITE: "W",
  BLUE: "U",
  GREEN: "G",
  RED: "R",
  BLACK: "B",
  GOLD: "Y"
};

let game = null;

document.querySelector("#newGame").addEventListener("click", newGame);
document.querySelector("#refresh").addEventListener("click", loadGame);
document.querySelector("#playRecommended").addEventListener("click", () => {
  const move = game?.recommendation?.recommendedMove;
  if (move) {
    playMove(move.id);
  }
});

loadGame();

async function loadGame() {
  clearError();
  const response = await fetch("/api/game");
  game = await response.json();
  render();
}

async function newGame() {
  clearError();
  const response = await fetch("/api/game/new", { method: "POST" });
  game = await response.json();
  render();
}

async function playMove(moveId) {
  clearError();
  const response = await fetch("/api/game/moves", {
    method: "POST",
    headers: { "Content-Type": "application/json" },
    body: JSON.stringify({ moveId })
  });

  if (!response.ok) {
    showError(await response.text());
    return;
  }

  game = await response.json();
  render();
}

function render() {
  renderStatus();
  renderPlayers();
  renderBoard();
  renderRecommendation();
}

function renderStatus() {
  const status = document.querySelector("#turnStatus");
  const event = document.querySelector("#event");
  if (game.finished) {
    status.textContent = `Winner: Player ${game.winnerPlayerNumber}`;
  } else {
    status.textContent = `Player ${game.currentPlayerNumber} is on turn`;
  }
  event.textContent = game.lastEvent || "";
}

function renderPlayers() {
  const players = document.querySelector("#players");
  players.innerHTML = "";
  players.appendChild(renderPlayer("Player 1", game.playerOne, game.currentPlayerNumber === 1));
  players.appendChild(renderPlayer("Player 2", game.playerTwo, game.currentPlayerNumber === 2));
}

function renderPlayer(title, player, active) {
  const node = document.createElement("article");
  node.className = active ? "player active" : "player";
  node.innerHTML = `
    <div class="player-head">
      <h2>${title}</h2>
      <strong>${player.prestigePoints} pts</strong>
    </div>
    <div class="stat-row"><span>Tokens</span>${renderTokenStrip(player.tokens, player.goldTokens)}</div>
    <div class="stat-row"><span>Bonuses</span>${renderColorCounts(player.bonuses)}</div>
    <div class="stat-row"><span>Reserved</span><strong>${(player.reservedCards || []).length}/3</strong></div>
  `;

  const reserved = document.createElement("div");
  reserved.className = "reserved";
  (player.reservedCards || []).forEach(card => reserved.appendChild(renderCard(card, false)));
  node.appendChild(reserved);
  return node;
}

function renderBoard() {
  renderNobles();
  for (const level of [1, 2, 3]) {
    document.querySelector(`#deck${level}`).textContent = game.deckCounts[level] || 0;
    const row = document.querySelector(`#level${level}`);
    row.innerHTML = "";
    visibleCards(level).forEach(card => row.appendChild(renderCard(card, true)));
  }
  document.querySelector("#bankTokens").innerHTML = renderTokenStrip(game.board.bankTokens, game.board.bankGoldTokens);
}

function renderNobles() {
  const nobles = document.querySelector("#nobles");
  nobles.innerHTML = "";
  (game.board.nobles || []).forEach(noble => {
    const node = document.createElement("article");
    node.className = "noble";
    node.innerHTML = `<strong>${noble.id}</strong><span>${noble.prestigePoints} pts</span>${renderColorCounts(noble.requiredBonuses)}`;
    nobles.appendChild(node);
  });
}

function renderRecommendation() {
  const recommendation = game.recommendation || {};
  const best = recommendation.recommendedMove;
  document.querySelector("#bestMove").textContent = formatMove(best);
  document.querySelector("#playRecommended").disabled = !best || game.finished;
  renderMoves(recommendation.rankedMoves || []);
}

function renderMoves(moves) {
  const container = document.querySelector("#moves");
  container.innerHTML = "";
  if (!moves.length) {
    container.innerHTML = '<p class="empty">No legal moves found.</p>';
    return;
  }

  moves.slice(0, 20).forEach((move, index) => {
    const details = document.createElement("details");
    details.className = "move";
    details.open = index === 0;

    const summary = document.createElement("summary");
    summary.innerHTML = `<span>${index + 1}. ${escapeHtml(formatMove(move))}</span><strong>${move.score}</strong>`;
    details.appendChild(summary);

    const play = document.createElement("button");
    play.type = "button";
    play.className = "play";
    play.textContent = "Play move";
    play.disabled = game.finished;
    play.addEventListener("click", () => playMove(move.id));
    details.appendChild(play);

    const explanation = move.explanation || {};
    details.appendChild(renderScoreLines("Positive reasons", explanation.positiveScoreLines || []));
    details.appendChild(renderScoreLines("Negative reasons", explanation.negativeScoreLines || []));
    details.appendChild(renderImpactChecks(explanation.impactChecks || []));
    container.appendChild(details);
  });
}

function renderCard(card, boardCard) {
  const node = document.createElement("article");
  node.className = `card ${String(card.colorBonus || "").toLowerCase()}`;
  node.innerHTML = `
    <div class="card-top">
      <strong>${escapeHtml(card.id)}</strong>
      <span>L${card.level}</span>
    </div>
    <div class="points">${card.prestigePoints}</div>
    <div class="bonus">${COLOR_LABELS[card.colorBonus] || card.colorBonus}</div>
    <div class="cost">${renderColorCounts(card.cost)}</div>
  `;
  if (boardCard) {
    const playableMoves = game.recommendation?.rankedMoves || [];
    const move = playableMoves.find(item => item.card && item.card.id === card.id);
    if (move) {
      node.title = `${move.id}, score ${move.score}`;
    }
  }
  return node;
}

function renderScoreLines(title, lines) {
  const section = document.createElement("section");
  section.className = "block";
  section.innerHTML = `<h3>${title}</h3>`;
  const list = document.createElement("ul");
  if (!lines.length) {
    list.innerHTML = '<li class="muted">None</li>';
  } else {
    lines.forEach(line => {
      const item = document.createElement("li");
      item.innerHTML = `<strong>${line.formattedPoints}</strong> ${escapeHtml(line.reason)}`;
      list.appendChild(item);
    });
  }
  section.appendChild(list);
  return section;
}

function renderImpactChecks(checks) {
  const section = document.createElement("section");
  section.className = "block";
  section.innerHTML = "<h3>Impact checks</h3>";
  const list = document.createElement("ul");
  checks.forEach(check => {
    const item = document.createElement("li");
    item.innerHTML = `<strong>${check.answer}</strong> ${escapeHtml(check.question)}`;
    list.appendChild(item);
  });
  section.appendChild(list);
  return section;
}

function visibleCards(level) {
  return (game.board.visibleCards || []).filter(card => card.level === level);
}

function renderTokenStrip(tokens, gold) {
  const parts = COLORS.map(color => tokenPill(color, tokens?.[color] || 0));
  parts.push(tokenPill("GOLD", gold || 0));
  return `<div class="tokens">${parts.join("")}</div>`;
}

function renderColorCounts(counts) {
  return `<div class="counts">${COLORS.map(color => {
    const value = counts?.[color] || 0;
    return value > 0 ? `<span class="${color.toLowerCase()}">${COLOR_SYMBOLS[color]} ${value}</span>` : "";
  }).join("")}</div>`;
}

function tokenPill(color, count) {
  return `<span class="token ${color.toLowerCase()}">${COLOR_SYMBOLS[color]} ${count}</span>`;
}

function formatMove(move) {
  if (!move) {
    return game?.finished ? "Game finished" : "No legal move";
  }
  if (move.type === "TAKE_TOKENS") {
    return `Take ${tokenSummary(move.takenTokens)}`;
  }
  if (move.type === "RESERVE_CARD") {
    return `Reserve ${move.card.id}`;
  }
  return `Buy ${move.card.id}`;
}

function tokenSummary(tokens) {
  return COLORS
    .filter(color => (tokens?.[color] || 0) > 0)
    .map(color => `${COLOR_SYMBOLS[color]}x${tokens[color]}`)
    .join(" ");
}

function showError(message) {
  const errorBox = document.querySelector("#error");
  errorBox.hidden = false;
  errorBox.textContent = message;
}

function clearError() {
  const errorBox = document.querySelector("#error");
  errorBox.hidden = true;
  errorBox.textContent = "";
}

function escapeHtml(value) {
  return String(value)
    .replaceAll("&", "&amp;")
    .replaceAll("<", "&lt;")
    .replaceAll(">", "&gt;")
    .replaceAll('"', "&quot;")
    .replaceAll("'", "&#039;");
}
