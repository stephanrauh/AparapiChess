package de.beyondjava.chess.common;

public class Move implements Comparable<Move> {
    public int fromColumn;
    public int fromRow;
    public int toColumn;
    public int toRow;
    public int materialValueAfterMove;
    public boolean opponentInCheck;
    public int positionalValue = 0;
    public boolean capture;
    public int piece;
    public int capturedPiece;
    String[] columns = {"A", "B", "C", "D", "E", "F", "G", "H"};
    String[] rows = {"8", "7", "6", "5", "4", "3", "2", "1"};
    String[] pieceName={"P" /* en passant */,
            " ", " ", "P", "P", "P", "P",
                                  "R", "R", "R", "R",
                                  "B", "B", "B", "B",
                                  "N", "N", "N", "N",
                                  "Q", "Q", "Q", "Q",
                                  "K", "K", "K", "K"};


    public Move(int piece, int fromRow, int fromColumn, int toRow, int toColumn, int materialValueAfterMove, boolean opponentInCheck, boolean capture, int capturedPiece) {
        this.piece=piece;
        this.fromColumn = fromColumn;
        this.fromRow = fromRow;
        this.toRow = toRow;
        this.toColumn = toColumn;
        this.materialValueAfterMove = materialValueAfterMove;
        this.opponentInCheck = opponentInCheck;
        this.capture = capture;
        this.capturedPiece=capturedPiece;
    }

    @Override
    public int compareTo(Move o) {
        if (null == o) return -1;
        int m = o.materialValueAfterMove - materialValueAfterMove;
        int p = o.positionalValue - positionalValue;
        return -(m + p);
    }

    public String getNotation() {
        String check = opponentInCheck ? "+" : " ";
        String s = capture ? "x" : "-";
        s += pieceName[capturedPiece+1];
        return pieceName[piece] + columns[fromColumn] + rows[fromRow] + s + columns[toColumn] + rows[toRow];
    }

    public String toString()
    {
        String m = String.format("%5d", materialValueAfterMove);
        String p = String.format("%5d", positionalValue);
        String s = String.format("%5d", materialValueAfterMove+positionalValue);
        return getNotation() + " Value: M: " + m + " P: " + p + " Sum: " + s;
    }
}
