package application;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.GridLayout;
import java.util.ArrayList;
import java.util.List;

import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.SwingConstants;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;

import chess.ChessException;
import chess.ChessMatch;
import chess.ChessPiece;
import chess.ChessPosition;

public class ChessGUI {

    private static final Color LIGHT_SQUARE = new Color(240, 217, 181);
    private static final Color DARK_SQUARE = new Color(181, 136, 99);
    private static final Color SELECTED_SQUARE = new Color(246, 246, 105);
    private static final Color POSSIBLE_MOVE_SQUARE = new Color(143, 188, 143);

    private final ChessMatch chessMatch;
    private final List<ChessPiece> captured;

    private JFrame frame;
    private JLabel statusLabel;
    private JLabel capturedWhiteLabel;
    private JLabel capturedBlackLabel;
    private JButton[][] boardButtons;

    private ChessPosition selectedSource;
    private boolean[][] currentPossibleMoves;

    public ChessGUI() {
        this.chessMatch = new ChessMatch();
        this.captured = new ArrayList<>();
    }

    public void show() {
        SwingUtilities.invokeLater(() -> {
            initLookAndFeel();
            buildUI();
            refreshBoard();
            frame.setVisible(true);
        });
    }

    private void initLookAndFeel() {
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        }
        catch (Exception e) {
            // keep default look and feel
        }
    }

    private void buildUI() {
        frame = new JFrame("Chess System - Interface Visual");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setLayout(new BorderLayout(12, 12));

        JPanel mainPanel = new JPanel(new BorderLayout(10, 10));
        mainPanel.setBorder(BorderFactory.createEmptyBorder(12, 12, 12, 12));

        JPanel boardPanel = new JPanel(new GridLayout(8, 8));
        boardButtons = new JButton[8][8];

        Font pieceFont = new Font("SansSerif", Font.BOLD, 26);

        for (int row = 0; row < 8; row++) {
            for (int col = 0; col < 8; col++) {
                JButton button = new JButton();
                button.setFocusPainted(false);
                button.setFont(pieceFont);
                button.setPreferredSize(new Dimension(70, 70));
                button.setOpaque(true);
                button.setBorderPainted(false);

                final int currentRow = row;
                final int currentCol = col;
                button.addActionListener(e -> onSquareClicked(currentRow, currentCol));

                boardButtons[row][col] = button;
                boardPanel.add(button);
            }
        }

        JPanel sidePanel = new JPanel();
        sidePanel.setLayout(new BoxLayout(sidePanel, BoxLayout.Y_AXIS));

        statusLabel = new JLabel("", SwingConstants.LEFT);
        statusLabel.setBorder(BorderFactory.createEmptyBorder(0, 0, 8, 0));

        capturedWhiteLabel = new JLabel("White capturadas: []");
        capturedBlackLabel = new JLabel("Black capturadas: []");

        sidePanel.add(statusLabel);
        sidePanel.add(capturedWhiteLabel);
        sidePanel.add(capturedBlackLabel);

        mainPanel.add(boardPanel, BorderLayout.CENTER);
        mainPanel.add(sidePanel, BorderLayout.EAST);

        frame.add(mainPanel, BorderLayout.CENTER);
        frame.pack();
        frame.setLocationRelativeTo(null);
    }

    private void onSquareClicked(int row, int col) {
        if (chessMatch.getCheckMate()) {
            return;
        }

        ChessPosition position = toChessPosition(row, col);

        try {
            if (selectedSource == null) {
                currentPossibleMoves = chessMatch.possibleMoves(position);
                selectedSource = position;
            }
            else {
                ChessPiece capturedPiece = chessMatch.performChessMove(selectedSource, position);
                if (capturedPiece != null) {
                    captured.add(capturedPiece);
                }
                handlePromotionIfNeeded();
                selectedSource = null;
                currentPossibleMoves = null;
            }
            refreshBoard();
        }
        catch (ChessException ex) {
            if (selectedSource != null) {
                tryReselectSource(position, ex);
            }
            else {
                showError(ex.getMessage());
            }
        }
    }

    private void tryReselectSource(ChessPosition candidateSource, ChessException previousException) {
        try {
            currentPossibleMoves = chessMatch.possibleMoves(candidateSource);
            selectedSource = candidateSource;
            refreshBoard();
        }
        catch (ChessException ex) {
            showError(previousException.getMessage());
        }
    }

    private void handlePromotionIfNeeded() {
        if (chessMatch.getPromoted() == null) {
            return;
        }

        String[] options = { "Rainha (Q)", "Torre (R)", "Bispo (B)", "Cavalo (N)" };
        int choice = JOptionPane.showOptionDialog(
            frame,
            "Escolha a peça para promoção:",
            "Promoção de peão",
            JOptionPane.DEFAULT_OPTION,
            JOptionPane.QUESTION_MESSAGE,
            null,
            options,
            options[0]
        );

        String type;
        switch (choice) {
            case 1:
                type = "R";
                break;
            case 2:
                type = "B";
                break;
            case 3:
                type = "N";
                break;
            default:
                type = "Q";
                break;
        }

        chessMatch.replacePromotedPiece(type);
    }

    private void refreshBoard() {
        ChessPiece[][] pieces = chessMatch.getPieces();

        for (int row = 0; row < 8; row++) {
            for (int col = 0; col < 8; col++) {
                JButton button = boardButtons[row][col];
                ChessPiece piece = pieces[row][col];

                button.setText(piece == null ? "" : pieceSymbol(piece));
                button.setForeground(piece == null ? Color.BLACK : piece.getColor() == chess.Color.WHITE ? Color.WHITE : Color.BLACK);
                button.setBackground(squareColor(row, col));
            }
        }

        updateStatus();
        updateCapturedPieces();

        if (chessMatch.getCheckMate()) {
            JOptionPane optionPane = new JOptionPane(
                "CHECKMATE! Vencedor: " + chessMatch.getCurrentPlayer(),
                JOptionPane.INFORMATION_MESSAGE
            );
            JDialog dialog = optionPane.createDialog(frame, "Fim de jogo");
            dialog.setModal(false);
            dialog.setVisible(true);
        }
    }

    private Color squareColor(int row, int col) {
        if (selectedSource != null) {
            int selectedRow = 8 - selectedSource.getRow();
            int selectedCol = selectedSource.getColumn() - 'a';
            if (selectedRow == row && selectedCol == col) {
                return SELECTED_SQUARE;
            }
        }

        if (currentPossibleMoves != null && currentPossibleMoves[row][col]) {
            return POSSIBLE_MOVE_SQUARE;
        }

        return (row + col) % 2 == 0 ? LIGHT_SQUARE : DARK_SQUARE;
    }

    private void updateStatus() {
        if (chessMatch.getCheckMate()) {
            statusLabel.setText("CHECKMATE - vencedor: " + chessMatch.getCurrentPlayer());
            return;
        }

        String text = "Turno " + chessMatch.getTurn() + " - Jogador: " + chessMatch.getCurrentPlayer();
        if (chessMatch.getCheck()) {
            text += " (CHECK)";
        }
        statusLabel.setText(text);
    }

    private void updateCapturedPieces() {
        List<String> whiteCaptured = new ArrayList<>();
        List<String> blackCaptured = new ArrayList<>();

        for (ChessPiece piece : captured) {
            if (piece == null) {
                continue;
            }
            if (piece.getColor() == chess.Color.WHITE) {
                whiteCaptured.add(piece.toString());
            }
            else {
                blackCaptured.add(piece.toString());
            }
        }

        capturedWhiteLabel.setText("White capturadas: " + whiteCaptured);
        capturedBlackLabel.setText("Black capturadas: " + blackCaptured);
    }

    private ChessPosition toChessPosition(int row, int col) {
        char column = (char) ('a' + col);
        int chessRow = 8 - row;
        return new ChessPosition(column, chessRow);
    }

    private String pieceSymbol(ChessPiece piece) {
        String p = piece.toString();
        if ("K".equals(p)) return "♔";
        if ("Q".equals(p)) return "♕";
        if ("R".equals(p)) return "♖";
        if ("B".equals(p)) return "♗";
        if ("N".equals(p)) return "♘";
        if ("P".equals(p)) return "♙";
        return p;
    }

    private void showError(String message) {
        JOptionPane.showMessageDialog(frame, message, "Jogada inválida", JOptionPane.ERROR_MESSAGE);
    }
}
