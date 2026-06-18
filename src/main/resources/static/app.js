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
let selectedMoveId = null;
let discardSelection = emptyTokenSelection();
let discardGoldSelection = 0;

document.querySelector("#newGame").addEventListener("click", newGame);
document.querySelector("#midGame").addEventListener("click", loadMidGamePreset);
document.querySelector("#refresh").addEventListener("click", loadGame);
document.querySelector("#playRecommended").addEventListener("click", () => {
  const move = game?.recommendation?.recommendedMove;
  if (move) {
    playMove(move.id);
  }
});
document.querySelector("#playSelected").addEventListener("click", () => {
  if (selectedMoveId) {
    playMove(selectedMoveId);
  }
});
document.querySelector("#discardSubmit").addEventListener("click", discardSelectedTokens);

loadGame();

async function loadGame() {
  clearError();
  const response = await fetch("/api/game");
  game = await response.json();
  resetDiscardSelection();
  selectRecommendedMove();
  render();
}

async function newGame() {
  clearError();
  const response = await fetch("/api/game/new", { method: "POST" });
  game = await response.json();
  resetDiscardSelection();
  selectRecommendedMove();
  render();
}

async function loadMidGamePreset() {
  clearError();
  const response = await fetch("/api/game/mid-game", { method: "POST" });
  game = await response.json();
  resetDiscardSelection();
  selectRecommendedMove();
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
  resetDiscardSelection();
  selectRecommendedMove();
  render();
}

async function discardSelectedTokens() {
  clearError();
  const response = await fetch("/api/game/discard", {
    method: "POST",
    headers: { "Content-Type": "application/json" },
    body: JSON.stringify({
      tokens: discardSelection,
      goldTokens: discardGoldSelection
    })
  });

  if (!response.ok) {
    showError(await response.text());
    return;
  }

  game = await response.json();
  resetDiscardSelection();
  selectRecommendedMove();
  render();
}

function render() {
  renderStatus();
  renderPlayers();
  renderBoard();
  renderDiscardPanel();
  renderRecommendation();
}

function renderStatus() {
  const status = document.querySelector("#turnStatus");
  const event = document.querySelector("#event");
  if (game.finished) {
    status.textContent = `Winner: Player ${game.winnerPlayerNumber}`;
  } else if (game.waitingForDiscard) {
    status.textContent = `Player ${game.discardPlayerNumber} must discard ${game.discardCount} token(s)`;
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
    node.innerHTML = `
      <div class="card-top">
        <strong>${noble.id}</strong>
      </div>
      <div class="card-meta">
        <span>Points</span>
        <strong>${noble.prestigePoints}</strong>
      </div>
      <div class="card-meta price">
        <span>Requires</span>
        ${renderColorCounts(noble.requiredBonuses)}
      </div>
    `;
    nobles.appendChild(node);
  });
}

function renderRecommendation() {
  const recommendation = game.recommendation || {};
  const best = recommendation.recommendedMove;
  document.querySelector("#bestMove").textContent = game.waitingForDiscard
    ? "Discard tokens to continue"
    : formatMove(best);
  document.querySelector("#playRecommended").disabled = !best || game.finished || game.waitingForDiscard;
  renderMoves(recommendation.rankedMoves || []);
  renderSelectedMove();
}

function renderMoves(moves) {
  const container = document.querySelector("#moves");
  container.innerHTML = "";
  if (game.waitingForDiscard) {
    container.innerHTML = '<p class="empty">Resolve token discard before playing another move.</p>';
    return;
  }
  if (!moves.length) {
    container.innerHTML = '<p class="empty">No legal moves found.</p>';
    return;
  }

  moves.slice(0, 20).forEach((move, index) => {
    const button = document.createElement("button");
    button.type = "button";
    button.className = move.id === selectedMoveId ? "move-item selected" : "move-item";
    button.innerHTML = `
      <span>
        <strong>${index + 1}. ${escapeHtml(formatMove(move))}</strong>
        ${move.id === game.recommendation?.recommendedMove?.id ? '<em>Recommended</em>' : ""}
      </span>
      <b>${move.score}</b>
    `;
    button.addEventListener("click", () => {
      selectedMoveId = move.id;
      renderRecommendation();
    });
    container.appendChild(button);
  });
}

function renderSelectedMove() {
  const move = selectedMove();
  const title = document.querySelector("#selectedMoveTitle");
  const details = document.querySelector("#selectedMoveDetails");
  const playSelected = document.querySelector("#playSelected");

  details.innerHTML = "";
  playSelected.disabled = !move || game.finished || game.waitingForDiscard;
  if (!move) {
    title.textContent = "No move selected";
    details.innerHTML = '<p class="empty">Select a move from the list.</p>';
    return;
  }

  title.textContent = `${formatMove(move)} - score ${move.score}`;
  const explanation = move.explanation || {};
  details.appendChild(renderScoreLines("Positive reasons", explanation.positiveScoreLines || []));
  details.appendChild(renderScoreLines("Negative reasons", explanation.negativeScoreLines || []));
  details.appendChild(renderImpactChecks(explanation.impactChecks || []));
  details.appendChild(renderDecisionTree(explanation.scoreTree));
}

function renderDiscardPanel() {
  const panel = document.querySelector("#discardPanel");
  const title = document.querySelector("#discardTitle");
  const controls = document.querySelector("#discardControls");
  const submit = document.querySelector("#discardSubmit");

  panel.hidden = !game.waitingForDiscard;
  controls.innerHTML = "";
  if (!game.waitingForDiscard) {
    return;
  }

  const player = game.discardPlayerNumber === 1 ? game.playerOne : game.playerTwo;
  title.textContent = `Player ${game.discardPlayerNumber}, discard ${game.discardCount} token(s)`;

  COLORS.forEach(color => {
    controls.appendChild(renderDiscardControl(
      color,
      player.tokens?.[color] || 0,
      discardSelection[color] || 0,
      value => {
        discardSelection[color] = value;
        renderDiscardPanel();
      }
    ));
  });
  controls.appendChild(renderDiscardControl(
    "GOLD",
    player.goldTokens || 0,
    discardGoldSelection,
    value => {
      discardGoldSelection = value;
      renderDiscardPanel();
    }
  ));

  const selected = selectedDiscardCount();
  submit.disabled = selected !== game.discardCount;
  submit.textContent = `Discard selected (${selected}/${game.discardCount})`;
}

function renderDiscardControl(color, available, selected, onChange) {
  const node = document.createElement("div");
  node.className = "discard-control";
  node.innerHTML = `
    <strong class="${color.toLowerCase()}">${COLOR_SYMBOLS[color]} ${COLOR_LABELS[color] || "Gold"}</strong>
    <span>Available: ${available}</span>
  `;

  const actions = document.createElement("div");
  actions.className = "stepper";
  const minus = document.createElement("button");
  minus.type = "button";
  minus.textContent = "-";
  minus.disabled = selected <= 0;
  minus.addEventListener("click", () => onChange(selected - 1));

  const value = document.createElement("b");
  value.textContent = selected;

  const plus = document.createElement("button");
  plus.type = "button";
  plus.textContent = "+";
  plus.disabled = selected >= available || selectedDiscardCount() >= game.discardCount;
  plus.addEventListener("click", () => onChange(selected + 1));

  actions.append(minus, value, plus);
  node.appendChild(actions);
  return node;
}

function renderCard(card, boardCard) {
  const node = document.createElement("article");
  node.className = `card ${String(card.colorBonus || "").toLowerCase()}`;
  node.innerHTML = `
    <div class="card-top">
      <strong>${escapeHtml(card.id)}</strong>
      <span>Level ${card.level}</span>
    </div>
    <div class="card-meta">
      <span>Points</span>
      <strong>${card.prestigePoints}</strong>
    </div>
    <div class="card-meta">
      <span>Bonus</span>
      <strong>${COLOR_LABELS[card.colorBonus] || card.colorBonus}</strong>
    </div>
    <div class="card-meta price">
      <span>Price</span>
      ${renderColorCounts(card.cost)}
    </div>
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

function renderDecisionTree(tree) {
  const section = document.createElement("section");
  section.className = "block tree-block";
  section.innerHTML = "<h3>Decision tree</h3>";

  if (!tree) {
    const empty = document.createElement("p");
    empty.className = "muted";
    empty.textContent = "No tree available.";
    section.appendChild(empty);
    return section;
  }

  const root = document.createElement("ul");
  root.className = "decision-tree";
  root.appendChild(renderTreeNode(tree));
  section.appendChild(root);
  return section;
}

function renderTreeNode(node) {
  const item = document.createElement("li");
  const label = document.createElement("span");
  label.textContent = node.label || "";
  item.appendChild(label);

  const children = node.children || [];
  if (children.length) {
    const list = document.createElement("ul");
    children.forEach(child => list.appendChild(renderTreeNode(child)));
    item.appendChild(list);
  }
  return item;
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

function selectRecommendedMove() {
  selectedMoveId = game?.waitingForDiscard ? null : game?.recommendation?.recommendedMove?.id || null;
}

function selectedMove() {
  const moves = game?.recommendation?.rankedMoves || [];
  return moves.find(move => move.id === selectedMoveId) || game?.recommendation?.recommendedMove || null;
}

function emptyTokenSelection() {
  return COLORS.reduce((result, color) => {
    result[color] = 0;
    return result;
  }, {});
}

function resetDiscardSelection() {
  discardSelection = emptyTokenSelection();
  discardGoldSelection = 0;
}

function selectedDiscardCount() {
  return COLORS.reduce((sum, color) => sum + (discardSelection[color] || 0), discardGoldSelection || 0);
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
