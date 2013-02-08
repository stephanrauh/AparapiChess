package de.beyondjava.chess;

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
        List<Move> possibleMoves = getLegalMoves(true);
        for (Move m : possibleMoves) {
            Chessboard b = moveChessPiece(m);
            m.positionalValue = b.evalPositionalValue();
        }
        Collections.sort(possibleMoves);
        for (Move m: possibleMoves)
        {
            System.out.println(m.getNotation() + " M:" + m.materialValueAfterMove + " P: " + m.positionalValue + "Sum: " + (m.materialValueAfterMove+m.positionalValue));
        }
        if (possibleMoves.size()>0)
        {
            Move bestMove = possibleMoves.get(possibleMoves.size()-1);
            return moveChessPiece(bestMove.fromRow, bestMove.fromColumn, bestMove.toRow, bestMove.toColumn);
        }
        System.out.println("Matt oder Patt");
        return (Chessboard) this;
    }

    public Chessboard findOpponentsMove(int foresight) {
        List<Move> possibleMoves = getLegalMoves(true);
        for (Move m : possibleMoves) {
            Chessboard b = moveChessPiece(m);
            m.positionalValue = b.evalPositionalValue();
        }
        Collections.sort(possibleMoves);
        for (Move m: possibleMoves)
        {
            System.out.println(m.getNotation() + " M:" + m.materialValueAfterMove + " P: " + m.positionalValue + "Sum: " + (m.materialValueAfterMove+m.positionalValue));
        }
        if (possibleMoves.size()>0)
        {
            Move bestMove = possibleMoves.get(possibleMoves.size()-1);
            return moveChessPiece(bestMove.fromRow, bestMove.fromColumn, bestMove.toRow, bestMove.toColumn);
        }
        System.out.println("Matt oder Patt");
        return (Chessboard) this;
    }

}
