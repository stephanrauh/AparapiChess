package de.beyondjava.chess.aparapiEngine.linearEngine;

/**
 * Represents the chess board and provide a couple of methods on possible moves.
 * Date: 02.02.13
 * Time: 19:17
 */
public abstract class AparapiChessboardsEvaluator extends AparapiChessboardsBoard {


    protected void evaluateBoard() {
        evaluateMaterialValue(0);
//            evaluateFieldPositionalValue();
//            findLegalMovesIgnoringCheck();
//            evaluateMobility();
//            evaluateThreats();
//            whiteTotalValue = (whiteMaterialValue-whiteExpectedLoss) * 10 + (whitePotentialMaterialValue >> 2) + whiteFieldPositionValue + whiteMoveValue + whiteCoverageValue;
//            blackTotalValue = (blackMaterialValue-blackExpectedLoss) * 10 + (blackPotentialMaterialValue >> 2) + blackFieldPositionValue + blackMoveValue + blackCoverageValue;
    }

    private void evaluateMaterialValue(int currentBoard) {
        int whiteValue = 0;
        int blackValue = 0;
        for (int cell=0; cell<64; cell++)
        {
            int piece = readChessCell(currentBoard, cell);
            if (whitePiece(piece)) {
                whiteValue += s_MATERIAL_VALUE[piece];
            }
            if (blackPiece(piece)) {
                blackValue += s_MATERIAL_VALUE[piece];
            }
        }
        whiteMaterialValue[currentBoard]=whiteValue;
        blackMaterialValue[currentBoard]=blackValue;
//        if (whiteMaterialValue != whiteValue - 13999)
//        {
//            System.out.println("Error evaluating material" + whiteMaterialValue + "<>" +(whiteValue - 13999));
//        }
//        if (blackMaterialValue != blackValue - 13999)
//        {
//            System.out.println("Error evaluating material");
//        }
    }



}
