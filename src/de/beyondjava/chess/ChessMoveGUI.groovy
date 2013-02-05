package de.beyondjava.chess

import javafx.scene.image.ImageView

/**
 * This class tries to make sense out of the users mouse clicks.
 * User: SoyYo
 * Date: 02.02.13
 * Time: 21:40
 * To change this template use File | Settings | File Templates.
 */
class ChessMoveGUI {
    public static void initiateTouch(int row, int column, ImageView[][] fields, Chessboard brett, ChessGUIState guiState) {
        int piece = brett.getChessPiece(row, column)
        if (brett.isActivePlayersPiece(piece)) {
            println "Starting move"
            fields[row][column].opacity = 0.7
            guiState.currentlyTouchedPieceY = row
            guiState.currentlyTouchedPieceX = column
        } else {
            println "Sorry, that's your opponent's piece"
        }
    }

    public static void jadoube(int row, int column, ImageView[][] fields, Chessboard brett, ChessGUIState guiState) {
        println "J'a doube"
        fields[row][column].opacity = 1.0
        guiState.currentlyTouchedPieceY = -1
        guiState.currentlyTouchedPieceX = -1
    }

    public static Chessboard onClick(int row, int column, ImageView[][] fields, Chessboard brett, ChessGUIState guiState, ChessImages images) {
        println "Hallo $row $column";
        int piece = brett.getChessPiece(row, column)

        if (guiState.currentlyTouchedPieceX < 0 && guiState.currentlyTouchedPieceY < 0 && piece > 1) {
            initiateTouch(row, column, fields, brett, guiState)
        } else if (guiState.currentlyTouchedPieceY == row && guiState.currentlyTouchedPieceX == column && piece > 1) {
            jadoube(row, column, fields, brett, guiState)
        } else if (guiState.currentlyTouchedPieceX >= 0 && guiState.currentlyTouchedPieceY >= 0) {
            brett = move(guiState.currentlyTouchedPieceY, guiState.currentlyTouchedPieceX, row, column, fields, brett, guiState, images);
        } else {
            println "Can't figure out what you want"
        }
        return brett;
    }

    public static Chessboard move(int fromRow, int fromColumn, int toRow, int toColumn, ImageView[][] fields, Chessboard brett, ChessGUIState guiState, ChessImages images) {
        if (brett.isMovePossible(fromRow, fromColumn, toRow, toColumn)) {
            fields[fromRow][fromColumn].opacity = 1.0
            println "Move"
            brett = brett.moveChessPiece(fromRow, fromColumn, toRow, toColumn)
            guiState.currentlyTouchedPieceX = -1
            guiState.currentlyTouchedPieceY = -1
            redraw(fields, brett, images)
        } else {
            println "Sorry, that's an illegal move"
        }
        return brett;
    }

    public static void redraw(ImageView[][] fields, Chessboard brett, ChessImages images) {
        8.times
                { int row ->
                    8.times
                            { int column ->
                                fields[row][column].setImage(images.getImage(brett.getChessPiece(row, column), row, column))
                            }
                }
    }

}
