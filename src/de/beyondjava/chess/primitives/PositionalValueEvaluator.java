package de.beyondjava.chess.primitives;

import de.beyondjava.chess.common.Move;
import de.beyondjava.chess.common.Piece;
import de.beyondjava.chess.objectOrientedEngine.Chessboard;
import de.beyondjava.chess.objectOrientedEngine.ChessboardBasis;
import de.beyondjava.chess.objectOrientedEngine.LegalMoves;

import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * User: SoyYo
 * Date: 08.02.13
 * Time: 21:23
 * To change this template use File | Settings | File Templates.
 */
public class PositionalValueEvaluator extends LegalMoves {

    public static final int[][] positions =
            {{0, 10, 20, 30, 30, 20, 10, 0},
                    {10, 20, 30, 40, 40, 30, 20, 10},
                    {20, 30, 40, 50, 50, 40, 30, 20},
                    {30, 40, 50, 70, 70, 50, 40, 30},
                    {30, 40, 50, 70, 70, 50, 40, 30},
                    {20, 30, 40, 50, 50, 40, 30, 20},
                    {10, 20, 30, 40, 40, 30, 20, 10},
                    {0, 10, 20, 30, 30, 20, 10, 0},
            };
    public static final int[][] whitePawnPositions =
            {{200, 200, 200, 200, 200, 200, 200, 200},
                    {60, 70, 80, 110, 110, 80, 70, 60},
                    {50, 60, 70, 100, 100, 70, 60, 50},
                    {40, 50, 60, 90, 90, 60, 50, 40},
                    {30, 40, 50, 80, 80, 50, 40, 30},
                    {20, 30, 40, 50, 50, 40, 30, 20},
                    {10, 20, 30, 40, 40, 30, 20, 10},
                    {0, 10, 20, 30, 30, 20, 10, 0},
            };


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
        if (true) {
            return evalFieldPositionalValue();
        } else {
            int currentMaterial = evalMaterialPosition();
            int fieldPosition = evalFieldPositionalValue();
            // which moves are possible if the player could move again?
            int value = evalValueOfLegalMoves(currentMaterial);
            Chessboard opponent = new Chessboard(!activePlayerIsWhite, this);
            int opponentValue = opponent.evalValueOfLegalMoves(-currentMaterial);
            value = value - opponentValue;
            return value + fieldPosition;
        }
    }

    public int evalValueOfLegalMoves(int currentMaterial) {
        int value = 0;
        List<Move> moves = getLegalMoves(true);
        for (Move m : moves) {
            if (m.opponentInCheck) {
                value += 100;
            } else {
                int mvGain = m.materialValueAfterMove - currentMaterial;
                if (mvGain != 0) {
                    if (mvGain < 0) {
                        System.out.println("Häh?");
                    }
                    value += mvGain / 10;
                }
            }
            value += 20;
        }
        return value;
    }

    private int evalFieldPositionalValue() {
        return activePlayerIsWhite ? evalFieldPositionalValueFromWhitePointOfView() : -evalFieldPositionalValueFromWhitePointOfView();
    }

    private int evalFieldPositionalValueFromWhitePointOfView() {
        int value = 0;
        for (int row = 0; row < 8; row++)
            for (int col = 0; col < 8; col++) {
                value = evalFieldPositionOfPiece(value, row, col);
            }
        return value;
    }

    private int evalFieldPositionOfPiece(int value, int row, int col) {
        int piece = board[row][col];
        if (piece == w_bauer) {
            value += whitePawnPositions[row][col];
        } else if (piece == s_bauer) {
            value -= whitePawnPositions[7 - row][col];
        } else if (isWhitePiece(piece)) {
            value += positions[row][col];
        } else if (isBlackPiece(piece)) {
            value -= positions[row][col];
        }
        return value;
    }

}
