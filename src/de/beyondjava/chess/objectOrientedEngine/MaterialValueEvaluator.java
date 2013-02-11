package de.beyondjava.chess.objectOrientedEngine;

import de.beyondjava.chess.common.Piece;

/**
 * Created with IntelliJ IDEA.
 * User: SoyYo
 * Date: 08.02.13
 * Time: 00:11
 * To change this template use File | Settings | File Templates.
 */
public class MaterialValueEvaluator extends ChessboardBasis
{

    public MaterialValueEvaluator() {
        super();
    }

    public MaterialValueEvaluator(boolean activePlayerIsWhite, ChessboardBasis board) {
        super(activePlayerIsWhite, board);
    }

    public MaterialValueEvaluator(boolean activePlayerIsWhite, Piece... pieces)
    {
        super(activePlayerIsWhite, pieces);
    }


    public MaterialValueEvaluator(ChessboardBasis oldBoard, int fromRow, int fromColumn, int toRow, int toColumn) {
        super(oldBoard, fromRow, fromColumn, toRow, toColumn);
    }

    public int evalMaterialPosition() {
        int value = evalMaterialPositionFromWhitePointOfView();
        if (!activePlayerIsWhite) return -value;
        else return value;
    }

    public int evalMaterialPositionFromWhitePointOfView() {
        int value = 0;
        for (int fromRow = 0; fromRow < 8; fromRow++)
            for (int toColumn = 0; toColumn < 8; toColumn++) {
                int piece = board[fromRow][toColumn];
                if (piece >= 2) {
//                    System.out.println(toColumn + "," +fromRow + ": " +piece + "=" + s_MATERIAL_VALUE[piece] );
                    value += s_MATERIAL_VALUE[piece];
                }
            }
        return value;
    }

}
