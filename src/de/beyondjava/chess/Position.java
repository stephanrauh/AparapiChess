package de.beyondjava.chess;

/**
 * Created with IntelliJ IDEA.
 * User: SoyYo
 * Date: 02.02.13
 * Time: 23:23
 * To change this template use File | Settings | File Templates.
 */
public class Position {
    int row;

    public int getColumn() {
        return column;
    }

    public int getRow() {
        return row;
    }

    int column;

    public Position(int row, int column)
    {
        this.row=row;
        this.column=column;
    }

    public boolean equals(int row, int column)
    {
        return row==this.row && column == this.column;
    }
}
