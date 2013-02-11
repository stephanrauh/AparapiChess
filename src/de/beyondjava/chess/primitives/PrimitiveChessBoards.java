package de.beyondjava.chess.primitives;

import de.beyondjava.chess.common.ChessConstants;
import de.beyondjava.chess.common.Move;

/**
 * Collection of chess boards, using as primitive data types as possible.
 */
public class PrimitiveChessBoards implements ChessConstants {
    public static final int s_MAX_BOARDS = 2500;
    public static final boolean s_WHITE = true;
    public static final boolean s_BLACK = false;
    int[][][] chessboards = new int[s_MAX_BOARDS][][];
    boolean[] activePlayerisWhite = new boolean[s_MAX_BOARDS];
    int count = 0;
    int[] materialValueFromActivePlayersPointOfView = new int[s_MAX_BOARDS];
    int[] currentMaterialLossFromActivePlayersPointOfView = new int[s_MAX_BOARDS];

    public int getMaterialValueFromActivePlayersPointOfView(int board) {
        return materialValueFromActivePlayersPointOfView[board];
    }

    public int getCurrentMaterialLossFromActivePlayersPointOfView(int board) {
        return currentMaterialLossFromActivePlayersPointOfView[board];
    }

    public int getChessPiece(int board, int row, int column) {
        return chessboards[board][row][column];
    }

    public boolean activePlayerIsWhite(int board) {
        return activePlayerisWhite[board];
    }

    public boolean activePlayerIsBlack(int board) {
        return !activePlayerisWhite[board];
    }

    /**
     * Adds a new chess board, make the move on the new board and returns the boards index
     */
    public int moveChessPiece(int board, int fromRow, int fromColumn, int toRow, int toColumn) {
        int materialGain = 0;
        int[][] newBoard = new int[8][8];
        for (int row = 2; row < 8; row += 3) {
            newBoard[row] = new int[8];
            for (int col = 0; col < 8; col++) {
                int piece = getChessPiece(board, row, col);
                if (piece < 0) piece = 0; // forget that en passant move was possible
                newBoard[row][col] = piece;
            }
        }

        int capturedPiece = 0;
        if (getChessPiece(board, toRow, toColumn) == -1) {
            // capture en passant
            capturedPiece = newBoard[fromRow][toColumn];
            newBoard[fromRow][toColumn] = 0;
        }
        int piece = newBoard[fromRow][fromColumn];
        if (piece == w_bauer)
            if (fromRow - toRow == 2)
                newBoard[fromRow - 1][toColumn] = -1; // make en passant possible in the opponents move
        if (piece == s_bauer)
            if (fromRow - toRow == -2)
                newBoard[fromRow + 1][toColumn] = -1; // make en passant possible in the opponents move
        if (capturedPiece == 0)
            capturedPiece = newBoard[toRow][toColumn];
        materialGain = s_MATERIAL_VALUE[capturedPiece];
        int materialValueAfterMove = materialValueFromActivePlayersPointOfView[board] + materialGain;

        newBoard[toRow][toColumn] = piece;
        newBoard[fromRow][fromColumn] = 0;
        return addChessboard(!activePlayerIsWhite(board), newBoard, -materialValueAfterMove, -materialGain);
    }

    public int moveChessPiece(int board, Move move) {
        return moveChessPiece(board, move.fromRow, move.fromColumn, move.toRow, move.toColumn);
    }

    /**
     * Adds a new chess board and returns its index
     */
    public int addChessboard(boolean activePlayerIsWhite, int[][] newboard, int materialValue, int materialLoss) {
        synchronized (this) {
            chessboards[count] = newboard;
            activePlayerisWhite[count] = activePlayerIsWhite;
            materialValueFromActivePlayersPointOfView[count] = materialValue;
            currentMaterialLossFromActivePlayersPointOfView[count] = materialLoss;
            count++;
            return count - 1;
        }
    }

    protected boolean isWhitePiece(int piece) {
        if (piece < 2) {
            return false;
        }
        return (((piece / 2) % 2) == 0);
    }

    protected boolean isBlackPiece(int piece) {
        if (piece < 2) {
            return false;
        }
        return (((piece / 2) % 2) == 1);
    }

    protected boolean isActivePlayersPiece(int board, int piece) {
        return isPlayersPiece(activePlayerIsWhite(board), piece);
    }

    protected boolean isPlayersPiece(boolean p_activePlayerIsWhite, int piece) {
        if (p_activePlayerIsWhite)
            return isWhitePiece(piece);
        else
            return isBlackPiece(piece);
    }

    protected boolean isInsideBoard(int board, int row, int column) {
        if (row < 0 || row > 7) return false;
        if (column < 0 || column > 7) return false;
        return true;
    }

    protected boolean isEmptyField(int board, int row, int column) {
        if (!isInsideBoard(board, row, column)) return false;
        if (getChessPiece(board, row, column) <= 0) return true;
        return false;
    }

    protected boolean isOpponentsPiece(int board, int row, int column) {
        if (!isInsideBoard(board, row, column)) return false;
        if (getChessPiece(board, row, column) <= 0) return false;
        if (activePlayerIsWhite(board)) {
            return (isBlackPiece(getChessPiece(board, row, column)));
        } else {
            return (isWhitePiece(getChessPiece(board, row, column)));
        }
    }

    protected boolean isEmptyOrCanBeCaptured(int board, int row, int column) {
        if (!isInsideBoard(board, row, column)) return false;
        int piece = getChessPiece(board, row, column);
        if (piece <= 0) return true;
        if (activePlayerIsWhite(board)) {
            return (isBlackPiece(piece));
        } else {
            return (isWhitePiece(piece));
        }
    }
}
