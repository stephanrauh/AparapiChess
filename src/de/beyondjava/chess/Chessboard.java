package de.beyondjava.chess;

/**
 * Represents the chess board and provide a couple of methods on possible moves.
 * Date: 02.02.13
 * Time: 19:17
 */
public class Chessboard extends MoveGenerator {

    public Chessboard() {
        super();
    }

    public Chessboard(boolean activePlayerIsWhite, ChessboardBasis board) {
        super(activePlayerIsWhite, board);
    }

    public Chessboard(boolean activePlayerIsWhite, Piece... pieces)
    {
        super(activePlayerIsWhite, pieces);
    }


    public Chessboard(ChessboardBasis oldBoard, int fromRow, int fromColumn, int toRow, int toColumn) {
        super(oldBoard, fromRow, fromColumn, toRow, toColumn);
    }
}
