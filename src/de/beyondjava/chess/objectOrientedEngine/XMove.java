package de.beyondjava.chess.objectOrientedEngine;

/**
 * Created with IntelliJ IDEA.
 * User: SoyYo
 * Date: 14.02.13
 * Time: 21:24
 * To change this template use File | Settings | File Templates.
 */
public class XMove implements Comparable<XMove> {
    int move;
    public int whiteMaterialValue;
    public int blackMaterialValue;
    public int whiteFieldPositionValue;
    public int blackFieldPositionValue;
    public int whiteMoveValue;
    public int blackMoveValue;
    public int whiteCoverageValue;
    public int blackCoverageValue;
    public boolean isWhiteKingThreatened;
    public boolean isBlackKingThreatened;
    public int whiteTotalValue;
    public int blackTotalValue;
    public int numberOfWhiteMoves;
    public int numberOfBlackMoves ;

    @Override
    public int compareTo(XMove o) {
        return whiteTotalValue-blackTotalValue;
    }
}
