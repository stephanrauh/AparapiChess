package de.beyondjava.chess.objectOrientedEngine;

import de.beyondjava.chess.common.Move;
import de.beyondjava.chess.common.Piece;

import java.util.*;

/**
 * Created with IntelliJ IDEA.
 * User: SoyYo
 * Date: 08.02.13
 * Time: 00:13
 * To change this template use File | Settings | File Templates.
 */
public class MoveGenerator extends ChessboardBasis {
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

    public Move findBestMove() throws EndOfGameException {
        long start = System.nanoTime();
        int[] bestMoves = activePlayerIsWhite ? findBestWhiteMoves(2, 6) : findBestBlackMoves(2, 6);
        long dauer = System.nanoTime() - start;
        System.out.println("Calculation took " + ((dauer / 1000) / 1000.0d) + "ms");
        int move = bestMoves[0];
        int fromRow = (move >> 12) & 0x000F;
        int fromColumn = (move >> 8) & 0x000F;
        int toRow = (move >> 4) & 0x000F;
        int toColumn = move & 0x000F;
        int promotion = (move >> 16) & 0x00FF;
        int capturedValue = (move >> 24);

        return new Move(getChessPiece(fromColumn, fromRow), fromRow, fromColumn, toRow, toColumn, 0, false, false, 0);
    }

    public int[] findBestBlackMoves(int lookAhead, int movesToConsider) throws EndOfGameException {
        Comparator moveComparator = new BlackMoveComparator();
        int[] moves = Arrays.copyOf(blackMoves, numberOfBlackMoves);
        try {
            List<XMove> evaluatedMoves = findBestMovesWithoutRecursion(moves, s_BLACK);
            // eliminate silly moves
            if (lookAhead >= 0) {
                evaluatedMoves = findBestBlackMovesRecursively(0, evaluatedMoves.size(), moveComparator, evaluatedMoves);
            }
            List<XMove> bestEvaluatedMoves = new ArrayList<>();

            if (lookAhead > 0) {
                bestEvaluatedMoves = findBestBlackMovesRecursively(lookAhead, movesToConsider, moveComparator, evaluatedMoves);
            } else {
                for (int i = 0; i < evaluatedMoves.size() && bestEvaluatedMoves.size() < movesToConsider; i++) {
                    XMove e = (XMove) evaluatedMoves.get(i);
                    bestEvaluatedMoves.add(e);
                }
            }
            int size = bestEvaluatedMoves.size();
            if (size == 0) {
                throw new StaleMateException();
            }
            int[] bestMoves = new int[size];
            for (int i = 0; i < movesToConsider && i < size; i++) {
                bestMoves[i] = bestEvaluatedMoves.get(i).move;
            }
            return bestMoves;
        } catch (KingLostException p_checkmate) {
            throw new WhiteIsCheckMateException();
        }
    }

    private List<XMove> findBestBlackMovesRecursively(int lookAhead, int movesToConsider, Comparator moveComparator, List<XMove> evaluatedMoves) throws EndOfGameException {
        List<XMove> bestEvaluatedMoves = new ArrayList<>();
        for (int i = 0; i < evaluatedMoves.size() && bestEvaluatedMoves.size() < movesToConsider; i++) {
            XMove e = (XMove) evaluatedMoves.get(i);
            try {
                int[] whiteMoves = e.boardAfterMove.findBestWhiteMoves(lookAhead - 1, movesToConsider);
                convertFirstMoveToXMove(e, whiteMoves);
                bestEvaluatedMoves.add(e);
            } catch (BlackIsCheckMateException p_impossibleMove) {
                // forget about this move
            } catch (WhiteIsCheckMateException p_winningMove) {
                e.checkmate = true;
                e.whiteTotalValue = -1000000;
                e.blackTotalValue = 1000000;
                bestEvaluatedMoves.add(e);
                break; // no need to look at the other moves
            }
        }
        Collections.sort(bestEvaluatedMoves, moveComparator);
        return bestEvaluatedMoves;
    }

    public int[] findBestWhiteMoves(int lookAhead, int movesToConsider) throws EndOfGameException {
        Comparator moveComparator = new WhiteMoveComparator();
        int[] moves = Arrays.copyOf(whiteMoves, numberOfWhiteMoves);
        try {
            List<XMove> evaluatedMoves = findBestMovesWithoutRecursion(moves, s_WHITE);
            // elimitate silly moves
            if (lookAhead >= 0) {
                evaluatedMoves = findBestBlackMovesRecursively(0, evaluatedMoves.size(), moveComparator, evaluatedMoves);
            }
            List<XMove> bestEvaluatedMoves = new ArrayList<>();

            if (lookAhead > 0) {
                bestEvaluatedMoves = findBestWhiteMovesRecursively(lookAhead, movesToConsider, moveComparator, evaluatedMoves);
            } else {
                for (int i = 0; i < evaluatedMoves.size() && bestEvaluatedMoves.size() < movesToConsider; i++) {
                    XMove e = (XMove) evaluatedMoves.get(i);
                    bestEvaluatedMoves.add(e);
                }
            }
            int size = bestEvaluatedMoves.size();
            if (0 == size) throw new StaleMateException();
            int[] bestMoves = new int[size];
            for (int i = 0; i < movesToConsider && i < size; i++) {
                bestMoves[i] = bestEvaluatedMoves.get(i).move;
            }
            return bestMoves;
        } catch (KingLostException p_checkmate) {
            throw new BlackIsCheckMateException();
        }
    }

    private List<XMove> findBestWhiteMovesRecursively(int lookAhead, int movesToConsider, Comparator moveComparator, List<XMove> evaluatedMoves) throws EndOfGameException {
        List<XMove> bestEvaluatedMoves = new ArrayList<>();
        for (int i = 0; i < evaluatedMoves.size() && bestEvaluatedMoves.size() < movesToConsider; i++) {
            XMove e = (XMove) evaluatedMoves.get(i);
            try {
                int[] blackMoves = e.boardAfterMove.findBestBlackMoves(lookAhead - 1, movesToConsider);
                convertFirstMoveToXMove(e, blackMoves);
                bestEvaluatedMoves.add(e);
            } catch (WhiteIsCheckMateException p_impossibleMove) {
                // forget about this move
            } catch (BlackIsCheckMateException p_winningMove) {
                e.checkmate = true;
                e.whiteTotalValue = 1000000;
                e.blackTotalValue = -1000000;
                bestEvaluatedMoves.add(e);
                break; // no need to look at the other moves
            }
        }
        Collections.sort(bestEvaluatedMoves, moveComparator);
        return bestEvaluatedMoves;
    }

    private void convertFirstMoveToXMove(XMove e, int[] whiteMoves) {
        if (null != whiteMoves && whiteMoves.length > 0) {
            int whiteMove = whiteMoves[0];
            int fromRow = (whiteMove >> 12) & 0x000F;
            int fromColumn = (whiteMove >> 8) & 0x000F;
            int toRow = (whiteMove >> 4) & 0x000F;
            int toColumn = whiteMove & 0x000F;
            Chessboard afterWhiteMove = e.boardAfterMove.moveChessPiece(fromRow, fromColumn, toRow, toColumn);
            e.whiteMaterialValue = afterWhiteMove.whiteMaterialValue;
            e.blackMaterialValue = afterWhiteMove.blackMaterialValue;
            e.whiteFieldPositionValue = afterWhiteMove.whiteFieldPositionValue;
            e.blackFieldPositionValue = afterWhiteMove.blackFieldPositionValue;
            e.whiteMoveValue = afterWhiteMove.whiteMoveValue;
            e.blackMoveValue = afterWhiteMove.blackMoveValue;
            e.whiteCoverageValue = afterWhiteMove.whiteCoverageValue;
            e.blackCoverageValue = afterWhiteMove.blackCoverageValue;
            e.isWhiteKingThreatened = afterWhiteMove.isWhiteKingThreatened;
            e.isBlackKingThreatened = afterWhiteMove.isBlackKingThreatened;
            e.whiteTotalValue = afterWhiteMove.whiteTotalValue;
            e.blackTotalValue = afterWhiteMove.blackTotalValue;
            e.numberOfWhiteMoves = afterWhiteMove.numberOfWhiteMoves;
            e.numberOfBlackMoves = afterWhiteMove.numberOfBlackMoves;
            e.boardAfterMove = afterWhiteMove;
        }
    }

    protected List<XMove> findBestMovesWithoutRecursion(int[] moves, boolean p_activePlayerIsWhite) throws KingLostException {
        List<XMove> evaluatedMoves = new ArrayList<>(moves.length);
        for (int i = 0; i < moves.length; i++) {
            int move = moves[i];
            int capturedValue = (move >> 24);
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
            e.boardAfterMove = afterMove;
            if (activePlayerIsWhite) {
                if (!afterMove.isWhiteKingThreatened) {
                    evaluatedMoves.add(e);
                }
            } else if (!afterMove.isBlackKingThreatened) {
                evaluatedMoves.add(e);
            }
            if ((e.whiteMaterialValue <= -9999) || (e.blackMaterialValue <= -9999)) {
                throw new KingLostException();
            }

        }
        if (p_activePlayerIsWhite)
            Collections.sort(evaluatedMoves, new WhiteMoveComparator());
        else
            Collections.sort(evaluatedMoves, new BlackMoveComparator());
        return evaluatedMoves;
    }

}
