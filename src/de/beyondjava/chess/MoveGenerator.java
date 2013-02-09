package de.beyondjava.chess;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * User: SoyYo
 * Date: 08.02.13
 * Time: 00:13
 * To change this template use File | Settings | File Templates.
 */
public class MoveGenerator extends PositionalValueEvaluator {

    public static final Move CHECKMATEMOVE = new Move(0, 0, 0, 0, -1000000, false, false);
    public static final Move STALEMATEMOVE = new Move(0, 0, 0, 0, 0, false, false);
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

    public Chessboard findBestMove() {
        long start = System.nanoTime();
        Move bestMove = findBestMove(6);
        long dauer = System.nanoTime()-start;
        System.out.println("Calculation took " + ((dauer/1000)/1000.0d) + "ms");
        if (bestMove == STALEMATEMOVE) {
            setStalemate(true);
            return (Chessboard) this;
        } else if (bestMove == CHECKMATEMOVE) {
            setCheckmate(true);
            return (Chessboard) this;
        }
        if (null != bestMove) {
            return moveChessPiece(bestMove.fromRow, bestMove.fromColumn, bestMove.toRow, bestMove.toColumn);
        } else {
            return (Chessboard) this;
        }
    }

    public Move findBestMove(int lookahead) {
        List<Move> possibleMoves = findBestMoves(lookahead, 3);
        if (possibleMoves == STALEMATEMOVELIST) return STALEMATEMOVE;
        if (possibleMoves == CHECKMATEMOVELIST) return CHECKMATEMOVE;

        if (possibleMoves.size() > 0) {
            Move bestMove = possibleMoves.get(0);
            return bestMove;
        }
        System.out.println("This line shouldn't be reached! (MoveGenerator.findBestMove)");
        return null;
    }

    public List<Move> findBestMoves(int lookAhead, int count) {
        List<Move> myBestMoves = findBestMoves(count);
        if (lookAhead <= 1) {
            return myBestMoves;
        }
        if (myBestMoves == STALEMATEMOVELIST || myBestMoves == CHECKMATEMOVELIST) {
            return myBestMoves;
        }
        for (Move m : myBestMoves) {
            Chessboard b = moveChessPiece(m);
            Move opponentsBestMove = b.findBestMove(lookAhead - 1);
            b = moveChessPiece(opponentsBestMove);
            int mat = b.evalMaterialPosition();
            int pos = b.evalPositionalValue();
            m.materialValueAfterMove = -mat;
            m.positionalValue = -pos;
        }
        Collections.sort(myBestMoves);
        List<Move> interestingMoves = new ArrayList<>();
        for (int i = 0; i < count && i < myBestMoves.size(); i++) {
            if (i >= 0) {
                interestingMoves.add(myBestMoves.get(i));
                String leer = "";
                for (int k = lookAhead; k < 6; k++) {
                    leer += "  ";
                }
                System.out.println(leer + "findBestMoves(" + lookAhead + ", " + count + ") = " + myBestMoves.get(i));
            }
        }
        return interestingMoves;
    }

    public List<Move> findBestMoves(int count) {
        List<Move> possibleMoves = getLegalMoves(true);
        for (Move m : possibleMoves) {
            Chessboard b = moveChessPiece(m);
            int pos = b.evalPositionalValueFromWhitePointOfView();
            if (!activePlayerIsWhite) pos = -pos;
            m.positionalValue = pos;
        }
        Collections.sort(possibleMoves);
//        for (Move m : possibleMoves) {
//            System.out.println(m.getNotation() + " M:" + m.materialValueAfterMove + " P: " + m.positionalValue + "Sum: " + (m.materialValueAfterMove + m.positionalValue));
//        }
        if (possibleMoves.size() > 0) {
            List<Move> interestingMoves = new ArrayList<>();
            for (int i = possibleMoves.size() - 1; i >= possibleMoves.size() - count; i--) {
                if (i >= 0)
                    interestingMoves.add(possibleMoves.get(i));
            }

            return interestingMoves;
        }
        if (isKingThreatened(!activePlayerIsWhite)) {

            return CHECKMATEMOVELIST;
        } else {
            return STALEMATEMOVELIST;
        }
    }


}
