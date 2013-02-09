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

    public Chessboard findBestMove() {
        Move bestMove = findBestMove(1);
        if (null != bestMove) {
            return moveChessPiece(bestMove.fromRow, bestMove.fromColumn, bestMove.toRow, bestMove.toColumn);
        } else {
            return (Chessboard) this;
        }
    }

    public Move findBestMove(int lookahead) {
        List<Move> possibleMoves = findBestMoves(lookahead, 5);

        if (null != possibleMoves && possibleMoves.size() > 0) {
            Move bestMove = possibleMoves.get(0);
            return bestMove;
        }
        if (isKingThreatened(!activePlayerIsWhite)) {
            System.out.println("Checkmate!");
        } else
            System.out.println("Stalemate!");

        return null;
    }

    public List<Move> findBestMoves(int lookAhead, int count) {
        List<Move> myBestMoves = findBestMoves(count);
        if (lookAhead <= 1) {
            return myBestMoves;
        }
        for (Move m : myBestMoves) {
            Chessboard b = moveChessPiece(m);
            Move opponentsBestMove = b.findBestMove(lookAhead - 1);
            b = moveChessPiece(opponentsBestMove)         ;
            int mat = b.evalMaterialPosition();
            int pos = b.evalPositionalValue();
            m.materialValueAfterMove=mat;
            m.positionalValue=pos;
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
            System.out.println("Checkmate!");
        } else
            System.out.println("Stalemate!");

        return null;
    }


}
