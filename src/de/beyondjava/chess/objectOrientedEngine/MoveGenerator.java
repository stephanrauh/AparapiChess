package de.beyondjava.chess.objectOrientedEngine;

import de.beyondjava.chess.common.Move;
import de.beyondjava.chess.common.Piece;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * User: SoyYo
 * Date: 08.02.13
 * Time: 00:13
 * To change this template use File | Settings | File Templates.
 */
public class MoveGenerator extends ChessboardBasis {

    public static final Move CHECKMATEMOVE = new Move(0, 0, 0, 0, 0, -1000000, false, false, 0);
    public static final Move STALEMATEMOVE = new Move(0, 0, 0, 0, 0, 0, false, false, 0);
    public static final ArrayList<Move> CHECKMATEMOVELIST = new ArrayList<Move>() {{
        add(CHECKMATEMOVE);
    }};
    public static final ArrayList<Move> STALEMATEMOVELIST = new ArrayList<Move>() {{
        add(STALEMATEMOVE);
    }};


    public MoveGenerator() {
        super();
    }

    public MoveGenerator(boolean activePlayerIsWhite, ChessboardBasis board) {
        super(activePlayerIsWhite, board);
    }

    public MoveGenerator(boolean activePlayerIsWhite, Piece... pieces) {
        super(activePlayerIsWhite, pieces);
    }


    public MoveGenerator(ChessboardBasis oldBoard, int fromRow, int fromColumn, int toRow, int toColumn) {
        super(oldBoard, fromRow, fromColumn, toRow, toColumn);
    }

    public Move findBestMove() {
        long start = System.nanoTime();
        int[] bestMoves = findBestBlackMoves(6, 5);
        long dauer = System.nanoTime() - start;
        System.out.println("Calculation took " + ((dauer / 1000) / 1000.0d) + "ms");
        int move = bestMoves[0];
        int fromRow = (move >> 12) & 0x000F;
        int fromColumn = (move >> 8) & 0x000F;
        int toRow = (move >> 4) & 0x000F;
        int toColumn = move & 0x000F;
        int promotion = (move >> 16) & 0x00FF;
        int capturedValue = (move>>24);

        return new Move(getChessPiece(fromColumn, fromRow), fromRow, fromColumn, toRow, toColumn, 0, false, false, 0);
    }

    public int[] findBestBlackMoves(int lookAhead, int movesToConsider) {
        int[] moves = Arrays.copyOf(blackMoves, numberOfBlackMoves);
        Arrays.sort(moves);
        List<XMove> evaluatedMoves = new ArrayList<>(moves.length);
        for (int i = 0; i < moves.length; i++) {
            int move = moves[i];
            int capturedValue = (move>>24);
            int promotion = (move >> 16) & 0x00FF;
            int fromRow = (move >> 12) & 0x000F;
            int fromColumn = (move >> 8) & 0x000F;
            int toRow = (move >> 4) & 0x000F;
            int toColumn = move & 0x000F;
            Chessboard afterMove = new Chessboard(this, fromRow, fromColumn, toRow, toColumn);
            XMove e = new XMove();
            e.move = move;
            e.piece = getChessPiece(fromRow, fromColumn);
            e.whiteMaterialValue = afterMove.whiteMaterialValue;
            e.blackMaterialValue = afterMove.blackMaterialValue;
            e.whiteFieldPositionValue = afterMove.whiteFieldPositionValue;
            e.blackFieldPositionValue = afterMove.blackFieldPositionValue;
            e.whiteMoveValue = afterMove.whiteMoveValue;
            e.blackMoveValue = afterMove.blackMoveValue;
            e.whiteCoverageValue = afterMove.whiteCoverageValue;
            e.blackCoverageValue = afterMove.blackCoverageValue;
            e.isWhiteKingThreatened = afterMove.isWhiteKingThreatened;
            e.isBlackKingThreatened = afterMove.isBlackKingThreatened;
            e.whiteTotalValue = afterMove.whiteTotalValue;
            e.blackTotalValue = afterMove.blackTotalValue;
            e.numberOfWhiteMoves = afterMove.numberOfWhiteMoves;
            e.numberOfBlackMoves = afterMove.numberOfBlackMoves;
            evaluatedMoves.add(e);
        }
        Collections.sort(evaluatedMoves);
        int size = movesToConsider;
        if (evaluatedMoves.size() < size) size = evaluatedMoves.size();
        int[] bestMoves = new int[size];
        for (int i = 0; i < movesToConsider && i < size; i++) {
            bestMoves[i] = evaluatedMoves.get(i).move;
        }
        return bestMoves;
    }

}
