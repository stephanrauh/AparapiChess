package de.beyondjava.chess;

/**
 * Represents the chess board and provide a couple of methods on possible moves.
 * Date: 02.02.13
 * Time: 19:17
 */
public class ChessboardBasis implements ChessConstants {
    final int[][] board;
    final boolean activePlayerIsWhite;

    public ChessboardBasis() {
        activePlayerIsWhite = true;
        board = ChessConstants.initialBoard;
    }

    public ChessboardBasis(boolean activePlayerIsWhite, ChessboardBasis board) {
        this.board = board.board;
        this.activePlayerIsWhite = activePlayerIsWhite;
    }

    public ChessboardBasis(boolean activePlayerIsWhite, Piece... pieces)
    {
        this.activePlayerIsWhite=activePlayerIsWhite;
        board = new int[8][8];
        for (Piece p: pieces)
        {
            board[p.getRow()][p.getColumn()] = p.getPiece();
        }
    }


    public ChessboardBasis(ChessboardBasis oldBoard, int fromRow, int fromColumn, int toRow, int toColumn) {
        int[][] newBoard = new int[8][8];
        for (int row = 0; row < 8; row++) {
            newBoard[row] = new int[8];
            for (int y = 0; y < 8; y++) {
                int piece = oldBoard.board[row][y];
                if (piece < 0) piece = 0; // forget en passant
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
                newBoard[fromRow - 1][toColumn] = -1; // make en passant possible in the opponents move
        if (piece == s_bauer)
            if (fromRow - toRow == -2)
                newBoard[fromRow + 1][toColumn] = -1; // make en passant possible in the opponents move
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


    private boolean isWhitePiece(int piece) {
        if (piece < 2) {
            return false;
        }
        return (((piece / 2) % 2) == 0);
    }

    private boolean isBlackPiece(int piece) {
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



}
