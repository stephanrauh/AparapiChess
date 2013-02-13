package de.beyondjava.chess.objectOrientedEngine;

import de.beyondjava.chess.common.Move;
import de.beyondjava.chess.common.Piece;

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
public class MoveGenerator extends ChessboardBasis {

    public static final Move CHECKMATEMOVE = new Move(0,0, 0, 0, 0, -1000000, false, false,0);
    public static final Move STALEMATEMOVE = new Move(0,0, 0, 0, 0, 0, false, false,0);
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
        Move bestMove = findBestMove(6);
        long dauer = System.nanoTime() - start;
        System.out.println("Calculation took " + ((dauer / 1000) / 1000.0d) + "ms");
        if (bestMove == STALEMATEMOVE) {
            setStalemate(true);
        } else if (bestMove == CHECKMATEMOVE) {
            setCheckmate(true);
        }
        return bestMove;
    }

    public Move findBestMove(int lookahead) {
        List<Move> possibleMoves = findBestMoves(lookahead, 5);
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
            int pos = -b.evalPositionalValue();
 //           if (m.fromColumn == 4 || m.fromColumn == 3)            // DEBUG
//                if (m.toRow == 3) // DEBUG
//                   pos = bb.evalPositionalValue(); // DEBUG

            m.positionalValue = pos;
        }
        Collections.sort(possibleMoves);
//        for (Move m : possibleMoves) {
//            System.out.println(m);
//        }
        if (possibleMoves.size() > 0) {
            List<Move> interestingMoves = new ArrayList<>();
            for (int i = possibleMoves.size() - 1; i >= possibleMoves.size() - count; i--) {
                if (i >= 0)
//                    if (activePlayerIsWhite || possibleMoves.get(i).fromColumn == 4 || possibleMoves.get(i).fromColumn == 3)            // DEBUG
//                        if (activePlayerIsWhite || possibleMoves.get(i).toRow == 3) // DEBUG
                            interestingMoves.add(possibleMoves.get(i));
            }

            return interestingMoves;
        }
        if (isOwnKingThreatened(activePlayerIsWhite)) {
            return CHECKMATEMOVELIST;
        } else {
            return STALEMATEMOVELIST;
        }
    }


}
