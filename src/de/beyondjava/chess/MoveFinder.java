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
public class MoveFinder extends PositionalValueEvaluator {
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
        List<Move> possibleMoves = findBestMoves(4,5);

        if (null != possibleMoves && possibleMoves.size() > 0) {
            Move bestMove = possibleMoves.get(possibleMoves.size() - 1);
            return moveChessPiece(bestMove.fromRow, bestMove.fromColumn, bestMove.toRow, bestMove.toColumn);
        }
        if (isKingThreatened(!activePlayerIsWhite)) {
            System.out.println("Checkmate!");
        } else
            System.out.println("Stalemate!");

        return (Chessboard) this;
    }

    public List<Move> findBestMoves(int lookAhead, int count) {
        List<Move> myBestMoves = findBestMoves(count);
        if (lookAhead <= 1) {
            return myBestMoves;
        }
        for (Move m : myBestMoves) {
            Move mostInterestingMove = null; // only for debugging purposes
            Chessboard b = moveChessPiece(m);
            List<Move> opponentsBestMoves = b.findBestMoves(lookAhead - 1, count);
            int material = -10000000;
            int positional = -10000000;
            for (Move o : opponentsBestMoves) {
                if (material + positional < o.materialValueAfterMove + o.positionalValue) {
                    mostInterestingMove = o;
                    material = o.materialValueAfterMove;
                    positional = o.positionalValue;
                }
            }
            m.materialValueAfterMove = -material;
            m.positionalValue = -positional;
            m.opponentInCheck = false; // we aren't interested in this information
        }
        Collections.sort(myBestMoves);
        List<Move> interestingMoves = new ArrayList<>();
        for (int i = myBestMoves.size() - 1; i >= myBestMoves.size() - count; i--) {
            if (i >= 0) {
                interestingMoves.add(myBestMoves.get(i));
                String leer = "";
                for (int k=lookAhead; k < 6; k++)
                {
                    leer +="  ";
                }
                System.out.println(leer + "findBestMoves(" + lookAhead + ", " + count + ") = "+myBestMoves.get(i).getNotation());
            }
        }
        return interestingMoves;
    }

    public List<Move> findBestMoves(int count) {
        List<Move> possibleMoves = getLegalMoves(true);
        for (Move m : possibleMoves) {
            Chessboard b = moveChessPiece(m);
            m.positionalValue = b.evalPositionalValue();
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
            System.out.println("Checkmate!");
        } else
            System.out.println("Stalemate!");

        return null;
    }


}
