package de.beyondjava.chess.gui

import de.beyondjava.chess.common.ChessConstants
import de.beyondjava.chess.common.Move
import de.beyondjava.chess.objectOrientedEngine.Chessboard
import javafx.animation.KeyFrame
import javafx.animation.Timeline
import javafx.event.ActionEvent
import javafx.event.EventHandler
import javafx.scene.image.ImageView
import javafx.scene.text.Text
import javafx.util.Duration

/**
 * This class tries to make sense out of the users mouse clicks.
 * User: SoyYo
 * Date: 02.02.13
 * Time: 21:40
 * To change this template use File | Settings | File Templates.
 */
class ChessMoveGUI {


    public static void initiateTouch(int row, int column, ImageView[][] fields, Chessboard brett, ChessGUIState guiState, Text checkmate) {
        int piece = brett.getChessPiece(row, column)
        if (brett.isActivePlayersPiece(piece)) {
            fields[row][column].opacity = 0.7
            guiState.currentlyTouchedPieceY = row
            guiState.currentlyTouchedPieceX = column
        } else {
            checkmate.text = "opponent's piece"
        }
    }

    public static void jadoube(int row, int column, ImageView[][] fields, Chessboard brett, ChessGUIState guiState, Text checkmate) {
        checkmate.text = "J'a doube"
        fields[row][column].opacity = 1.0
        guiState.currentlyTouchedPieceY = -1
        guiState.currentlyTouchedPieceX = -1
    }

    public static Chessboard onClick(int row, int column, ImageView[][] fields, Chessboard brett, ChessGUIState guiState, ChessImages images, Text checkmate, Text whiteMoves, Text blackMoves) {
        int piece = brett.getChessPiece(row, column)

        if (guiState.currentlyTouchedPieceX < 0 && guiState.currentlyTouchedPieceY < 0 && piece > 1) {
            initiateTouch(row, column, fields, brett, guiState, checkmate)
        } else if (guiState.currentlyTouchedPieceY == row && guiState.currentlyTouchedPieceX == column && piece > 1) {
            jadoube(row, column, fields, brett, guiState, checkmate)
        } else if (guiState.currentlyTouchedPieceX >= 0 && guiState.currentlyTouchedPieceY >= 0) {
            brett = move(guiState.currentlyTouchedPieceY, guiState.currentlyTouchedPieceX, row, column, fields, brett, guiState, images, checkmate, whiteMoves, blackMoves);
        } else {
            checkmate.text = "Can't figure out what you want"
        }
        return brett;
    }

    public static Chessboard move(int fromRow, int fromColumn, int toRow, int toColumn, ImageView[][] fields, Chessboard brett, ChessGUIState guiState, ChessImages images, Text checkmate, Text whiteMoves, Text blackMoves) {
        if (brett.isMovePossible(fromRow, fromColumn, toRow, toColumn)) {
            fields[fromRow][fromColumn].opacity = 1.0
            addMoveNotation(brett, toRow, toColumn, fromRow, fromColumn, whiteMoves, blackMoves)
            brett = brett.moveChessPiece(fromRow, fromColumn, toRow, toColumn)
            guiState.currentlyTouchedPieceX = -1
            guiState.currentlyTouchedPieceY = -1
            redraw(fields, brett, images, checkmate, whiteMoves, blackMoves)
//            PrimitiveMoveGenerator generator = new PrimitiveMoveGenerator(brett.board, brett.activePlayerIsWhite);
//            def move = generator.findBestMove()
            brett = opponentsMove(brett, whiteMoves, blackMoves, checkmate, fields, images)
        } else {
            checkmate.text = "illegal move"
        }
        return brett;
    }

    private static Chessboard opponentsMove(Chessboard brett, Text whiteMoves, Text blackMoves, Text checkmate, ImageView[][] fields, ChessImages images) {
        Timeline time = new Timeline();
        time.setCycleCount(1);
        KeyFrame keyFrame = new KeyFrame(Duration.millis(47), new EventHandler<ActionEvent>() {
            public void handle(ActionEvent event) {
                Move move = brett.findBestMove();
                if ((!brett.stalemate) && (!brett.checkmate)) {
                    if (null != move) {
                        addMoveNotation(brett, move.toRow, move.toColumn, move.fromRow, move.fromColumn, whiteMoves, blackMoves)
                        brett = brett.moveChessPiece(move)
                        ChessGUIRemoteControl.chessboard=brett
                    } else
                        checkmate.text = "(error)"
                }
                redraw(fields, brett, images, checkmate, whiteMoves, blackMoves)
            }
        });

        time.getKeyFrames().add(keyFrame);
        time.playFromStart()
        return brett

    }

    private static void addMoveNotation(Chessboard brett, int toRow, int toColumn, int fromRow, int fromColumn, Text whiteMoves, Text blackMoves) {
        String text = getNotation(brett, toRow, toColumn, fromRow, fromColumn)
        if (brett.activePlayerIsWhite) whiteMoves.text += text + "\n" else blackMoves.text += text + "\n"
    }

    private static String getNotation(Chessboard brett, int toRow, int toColumn, int fromRow, int fromColumn) {
        int capturedPiece = brett.getChessPiece(toRow, toColumn)
        if (capturedPiece == -1) {
            if (brett.activePlayerIsWhite) {
                capturedPiece = ChessConstants.w_bauer
            } else {
                capturedPiece = ChessConstants.s_bauer
            }
        }
        Move m = new Move(brett.getChessPiece(fromRow, fromColumn), fromRow, fromColumn, toRow, toColumn, 0, false, 0 != capturedPiece, capturedPiece)
        Chessboard newBoard = brett.moveChessPiece(m)
        boolean check = newBoard.isOpponentsKingThreatened(newBoard.activePlayerIsWhite)
        m.opponentInCheck = check
        return m.getNotation()
    }

    public static void redraw(ImageView[][] fields, Chessboard brett, ChessImages images, Text checkmate, Text whiteMoves, Text blackMoves) {
        8.times
                { int row ->
                    8.times
                            { int column ->
                                fields[row][column].setImage(images.getImage(brett.getChessPiece(row, column), row, column))
                            }
                }
        if (brett.isStalemate()) {
            checkmate.text = "Stalemate!"
        } else if (brett.isCheckmate()) {
            checkmate.text = "Stalemate!"
        } else if (brett.activePlayerIsWhite) {
            checkmate.text = "white move"
        } else {
            checkmate.text = "black move"
        }
    }

}
