import { Chess } from 'https://cdn.jsdelivr.net/npm/chess.js@1.4.0/+esm';

const boardEl = document.getElementById('board');
const statusEl = document.getElementById('status');
const capturedWhiteEl = document.getElementById('capturedWhite');
const capturedBlackEl = document.getElementById('capturedBlack');
const pgnEl = document.getElementById('pgn');
const resetBtn = document.getElementById('resetBtn');

const files = ['a', 'b', 'c', 'd', 'e', 'f', 'g', 'h'];
const unicodeByPiece = {
  wp: '♙', wn: '♘', wb: '♗', wr: '♖', wq: '♕', wk: '♔',
  bp: '♟', bn: '♞', bb: '♝', br: '♜', bq: '♛', bk: '♚'
};

const game = new Chess();
let selectedSquare = null;
let legalTargets = [];
let capturedWhite = [];
let capturedBlack = [];

function squareName(row, col) {
  return `${files[col]}${8 - row}`;
}

function pieceUnicode(piece) {
  if (!piece) return '';
  return unicodeByPiece[`${piece.color}${piece.type}`];
}

function renderBoard() {
  const board = game.board();
  boardEl.innerHTML = '';

  for (let row = 0; row < 8; row += 1) {
    for (let col = 0; col < 8; col += 1) {
      const sq = squareName(row, col);
      const piece = board[row][col];
      const button = document.createElement('button');

      button.className = `square ${(row + col) % 2 === 0 ? 'light' : 'dark'}`;
      if (selectedSquare === sq) button.classList.add('selected');
      if (legalTargets.includes(sq)) button.classList.add('possible');

      button.type = 'button';
      button.dataset.square = sq;
      button.textContent = pieceUnicode(piece);
      button.title = sq;
      button.addEventListener('click', () => onSquareClick(sq));

      boardEl.appendChild(button);
    }
  }

  renderStatus();
}

function choosePromotion() {
  const value = prompt('Promoção: escolha q (rainha), r (torre), b (bispo) ou n (cavalo)', 'q');
  const v = (value || 'q').toLowerCase();
  return ['q', 'r', 'b', 'n'].includes(v) ? v : 'q';
}

function onSquareClick(square) {
  if (game.isGameOver()) return;

  if (!selectedSquare) {
    const piece = game.get(square);
    if (!piece || piece.color !== game.turn()) return;

    selectedSquare = square;
    legalTargets = game.moves({ square, verbose: true }).map((m) => m.to);
    renderBoard();
    return;
  }

  if (selectedSquare === square) {
    selectedSquare = null;
    legalTargets = [];
    renderBoard();
    return;
  }

  const promotionNeeded = game.get(selectedSquare)?.type === 'p' && (square.endsWith('8') || square.endsWith('1'));
  const move = game.move({
    from: selectedSquare,
    to: square,
    promotion: promotionNeeded ? choosePromotion() : 'q'
  });

  if (!move) {
    const piece = game.get(square);
    if (piece && piece.color === game.turn()) {
      selectedSquare = square;
      legalTargets = game.moves({ square, verbose: true }).map((m) => m.to);
      renderBoard();
      return;
    }

    alert('Jogada inválida.');
    return;
  }

  if (move.captured) {
    const symbol = unicodeByPiece[`${move.color === 'w' ? 'b' : 'w'}${move.captured}`];
    if (move.color === 'w') {
      capturedBlack.push(symbol);
    } else {
      capturedWhite.push(symbol);
    }
  }

  selectedSquare = null;
  legalTargets = [];
  renderBoard();
}

function renderStatus() {
  if (game.isCheckmate()) {
    statusEl.textContent = `CHECKMATE! Vencedor: ${game.turn() === 'w' ? 'Pretas' : 'Brancas'}`;
  } else if (game.isDraw()) {
    statusEl.textContent = 'Empate.';
  } else {
    const turn = game.turn() === 'w' ? 'Brancas' : 'Pretas';
    statusEl.textContent = `Turno: ${turn}${game.isCheck() ? ' (CHECK)' : ''}`;
  }

  capturedWhiteEl.textContent = capturedWhite.join(' ') || '-';
  capturedBlackEl.textContent = capturedBlack.join(' ') || '-';
  pgnEl.textContent = game.pgn() || '-';
}

resetBtn.addEventListener('click', () => {
  game.reset();
  selectedSquare = null;
  legalTargets = [];
  capturedWhite = [];
  capturedBlack = [];
  renderBoard();
});

renderBoard();
