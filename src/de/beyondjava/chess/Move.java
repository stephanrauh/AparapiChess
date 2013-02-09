package de.beyondjava.chess;

public class Move implements Comparable<Move> {
    int fromColumn;
    int fromRow;
    int toColumn;
    int toRow;
    int materialValueAfterMove;
    boolean opponentInCheck;
    int positionalValue = 0;
    boolean capture;
    String[] columns = {"A", "B", "C", "D", "E", "F", "G", "H"};
    String[] rows = {"8", "7", "6", "5", "4", "3", "2", "1"};


    public Move(int fromRow, int fromColumn, int toRow, int toColumn, int materialValueAfterMove, boolean opponentInCheck, boolean capture) {
        this.fromColumn = fromColumn;
        this.fromRow = fromRow;
        this.toRow = toRow;
        this.toColumn = toColumn;
        this.materialValueAfterMove = materialValueAfterMove;
        this.opponentInCheck = opponentInCheck;
        this.capture = capture;
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
        return columns[fromColumn] + rows[fromRow] + s + columns[toColumn] + rows[toRow];
    }

    public String toString()
    {
        String m = String.format("%5d", materialValueAfterMove);
        String p = String.format("%5d", positionalValue);
        String s = String.format("%5d", materialValueAfterMove+positionalValue);
        return getNotation() + " Value: M: " + m + " P: " + p + " Sum: " + s;
    }
}
