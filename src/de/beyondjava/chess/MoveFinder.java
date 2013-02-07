package de.beyondjava.chess;

/**
 * Created with IntelliJ IDEA.
 * User: SoyYo
 * Date: 08.02.13
 * Time: 00:13
 * To change this template use File | Settings | File Templates.
 */
public class MoveFinder extends LegalMoves {
    public MoveFinder() {
        super();
    }

    public MoveFinder(boolean activePlayerIsWhite, ChessboardBasis board) {
        super(activePlayerIsWhite, board);
    }

    public MoveFinder(boolean activePlayerIsWhite, Piece... pieces)
    {
        super(activePlayerIsWhite, pieces);
    }


    public MoveFinder(ChessboardBasis oldBoard, int fromRow, int fromColumn, int toRow, int toColumn) {
        super(oldBoard, fromRow, fromColumn, toRow, toColumn);
    }
}
