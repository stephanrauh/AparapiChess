package de.beyondjava.chess;

import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * User: SoyYo
 * Date: 08.02.13
 * Time: 21:23
 * To change this template use File | Settings | File Templates.
 */
public class PositionalValueEvaluator extends LegalMoves {
    public PositionalValueEvaluator() {
        super();
    }

    public PositionalValueEvaluator(boolean activePlayerIsWhite, ChessboardBasis board) {
        super(activePlayerIsWhite, board);
    }

    public PositionalValueEvaluator(boolean activePlayerIsWhite, Piece... pieces) {
        super(activePlayerIsWhite, pieces);
    }


    public PositionalValueEvaluator(ChessboardBasis oldBoard, int fromRow, int fromColumn, int toRow, int toColumn) {
        super(oldBoard, fromRow, fromColumn, toRow, toColumn);
    }

    public int evalPositionalValue() {
        int value = evalPositionalValueFromWhitePointOfView();
        if (!activePlayerIsWhite) return -value;
        else return value;
    }

    public int evalPositionalValueFromWhitePointOfView() {
        int currentMaterial = evalMaterialPosition();
        int value = 0;
        // which moves are possible if the player could move again?
        List<Move> moves = getLegalMoves(true);
        for (Move m : moves) {
            if (m.opponentInCheck) {
                value += 100;
            } else {
                int mv = currentMaterial - m.materialValueAfterMove;
                if (mv != 0) {
                    value += mv / 10;
                }
            }
            value += 20;
        }
        return value;
    }

}
