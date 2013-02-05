package de.beyondjava.chess;

/**
 * Created with IntelliJ IDEA.
 * User: SoyYo
 * Date: 02.02.13
 * Time: 23:23
 * To change this template use File | Settings | File Templates.
 */
public class Move {
    int fromColumn;
    int fromRow;
    int toColumn;

    int toRow;


    public Move(int fromRow, int fromColumn, int toRow, int toColumn)
    {
        this.fromColumn=fromColumn;
        this.fromRow=fromRow;
        this.toRow=toRow;
        this.toColumn=toColumn;
    }

}
