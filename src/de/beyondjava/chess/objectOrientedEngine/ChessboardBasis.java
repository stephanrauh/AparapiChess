package de.beyondjava.chess.objectOrientedEngine;

import de.beyondjava.chess.common.ChessConstants;
import de.beyondjava.chess.common.Move;
import de.beyondjava.chess.common.Piece;

/**
 * Represents the chess board and provide a couple of methods on possible moves.
 * Date: 02.02.13
 * Time: 19:17
 */
public class ChessboardBasis implements ChessConstants {
    public final int[][] board;
    public final boolean activePlayerIsWhite;
    public int whiteMaterialValue;
    public int blackMaterialValue;
    public int whiteFieldPositionValue; // effectively final
    public int blackFieldPositionValue; // effectively final
    public int whiteMoveValue;
    public int blackMoveValue;
    public boolean isWhiteKingThreatened;
    public boolean isBlackKingThreatened;
    public boolean isWhiteCheckmate;
    public boolean isBlackCheckmate;
    public int whiteTotalValue;
    public int blackTotalValue;
    public int[] whiteMoves;
    public int[] blackMoves;

    private ChessboardBasis(boolean activePlayerIsWhite, int[][] board)
    {
        this.activePlayerIsWhite=activePlayerIsWhite;
        this.board=board;
        evaluateMaterialValue();
    }
    public ChessboardBasis() {
       this(true, ChessConstants.initialBoard);
    }

    public ChessboardBasis(boolean activePlayerIsWhite, ChessboardBasis board) {
        this(activePlayerIsWhite, board.board);
    }

    public ChessboardBasis(boolean activePlayerIsWhite, Piece... pieces) {
        this(activePlayerIsWhite, getBoardFromPieces(pieces));
    }

    private static int[][] getBoardFromPieces(Piece[] pieces) {
        int[][] board = new int[8][8];
        for (Piece p : pieces) {
            board[p.row][p.column] = p.piece;
        }
        return board;
    }


    public ChessboardBasis(ChessboardBasis oldBoard, int fromRow, int fromColumn, int toRow, int toColumn) {
        int[][] newBoard = new int[8][8];
        for (int row = 0; row < 8; row++) {
            newBoard[row] = new int[8];
            for (int y = 0; y < 8; y++) {
                int piece = oldBoard.board[row][y];
                if (piece < 0) piece = 0; // forget that en passant once was possible
                newBoard[row][y] = piece;
            }
        }

        if (oldBoard.board[toRow][toColumn] == -1) {
            // capture en passant
            if (oldBoard.activePlayerIsWhite) {
                newBoard[fromRow][toColumn] = 0;
            } else {
                newBoard[fromRow][toColumn] = 0;
            }
        }
        int piece = newBoard[fromRow][fromColumn];
        if (piece == w_bauer)
            if (fromRow - toRow == 2)
                newBoard[fromRow - 1][toColumn] = -1; // allow for en passant
        if (piece == s_bauer)
            if (fromRow - toRow == -2)
                newBoard[fromRow + 1][toColumn] = -1; // allow for en passant
        newBoard[toRow][toColumn] = piece;
        newBoard[fromRow][fromColumn] = 0;
        board = newBoard;
        activePlayerIsWhite = !oldBoard.activePlayerIsWhite;
    }

    public int getChessPiece(int row, int column) {
        return board[row][column];
    }

    public Chessboard moveChessPiece(int fromRow, int fromColumn, int toRow, int toColumn) {
        return new Chessboard(this, fromRow, fromColumn, toRow, toColumn);
    }

    public Chessboard moveChessPiece(Move move) {
        return new Chessboard(this, move.fromRow, move.fromColumn, move.toRow, move.toColumn);
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

    protected boolean isActivePlayersPiece(int piece) {
        return activePlayerIsWhite == isWhitePiece(piece);
    }

    protected boolean isInsideBoard(int row, int column) {
        if (row < 0 || row > 7) return false;
        if (column < 0 || column > 7) return false;
        return true;
    }

    protected boolean isEmptyField(int row, int column) {
        if (!isInsideBoard(row, column)) return false;
        if (board[row][column] <= 0) return true;
        return false;
    }

    protected boolean isOpponentsPiece(int row, int column) {
        if (!isInsideBoard(row, column)) return false;
        if (board[row][column] <= 0) return false;
        if (activePlayerIsWhite) {
            return (isBlackPiece(board[row][column]));
        } else {
            return (isWhitePiece(board[row][column]));
        }
    }

    protected boolean isEmptyOrCanBeCaptured(int row, int column) {
        if (!isInsideBoard(row, column)) return false;
        int piece = board[row][column];
        if (piece <= 0) return true;
        if (activePlayerIsWhite) {
            return (isBlackPiece(piece));
        } else {
            return (isWhitePiece(piece));
        }
    }

    private void evaluateMaterialValue() {
        int whiteValue = 0;
        int blackValue = 0;
        for (int fromRow = 0; fromRow < 8; fromRow++)
            for (int toColumn = 0; toColumn < 8; toColumn++) {
                int piece = board[fromRow][toColumn];
                if (isWhitePiece(piece))
                {
                    whiteValue+= s_MATERIAL_VALUE[piece];
                }
                if (isBlackPiece(piece))
                {
                    blackValue += s_MATERIAL_VALUE[piece];
                }
            }
        whiteMaterialValue=whiteValue;
        blackMaterialValue=blackValue;
    }

}
