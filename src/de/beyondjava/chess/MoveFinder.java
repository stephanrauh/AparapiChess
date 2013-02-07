package de.beyondjava.chess;

import java.util.List;

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

    public MoveFinder(boolean activePlayerIsWhite, Piece... pieces) {
        super(activePlayerIsWhite, pieces);
    }


    public MoveFinder(ChessboardBasis oldBoard, int fromRow, int fromColumn, int toRow, int toColumn) {
        super(oldBoard, fromRow, fromColumn, toRow, toColumn);
    }

    public Chessboard findOpponentsMove() {
        Move bestMove = null;

        List<Move> possibleMoves = getLegalMoves(true);
        for (Move m : possibleMoves) {
            if (bestMove == null) bestMove = m;
            else {
                if (m.materialValueAfterMove > bestMove.materialValueAfterMove)
                    bestMove = m;
            }
        }
        if (null != bestMove)
        {
            return moveChessPiece(bestMove.fromRow, bestMove.fromColumn, bestMove.toRow, bestMove.toColumn);
        }
        return (Chessboard) this;
    }
}
