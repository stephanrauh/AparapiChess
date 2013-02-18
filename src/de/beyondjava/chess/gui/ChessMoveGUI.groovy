package de.beyondjava.chess.gui

import de.beyondjava.chess.common.ChessConstants
import de.beyondjava.chess.common.Move
import de.beyondjava.chess.objectOrientedEngine.BlackIsCheckMateException
import de.beyondjava.chess.objectOrientedEngine.Chessboard
import de.beyondjava.chess.objectOrientedEngine.WhiteIsCheckMateException
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
    static List<Chessboard> history = [new Chessboard()]
    static List<String> whiteMoveHistory = []
    static List<String> blackMoveHistory = []

    public static void initiateTouch(int row, int column, ImageView[][] fields, Chessboard board, ChessGUIState guiState, Text checkmate) {
        int piece = board.getChessPiece(row, column)
        if (board.isActivePlayersPiece(piece)) {
            fields[row][column].opacity = 0.7
            guiState.currentlyTouchedPieceY = row
            guiState.currentlyTouchedPieceX = column
        } else {
            checkmate.text = "opponent's piece"
        }
    }

    public static void jadoube(int row, int column, ImageView[][] fields, Chessboard board, ChessGUIState guiState, Text checkmate) {
        checkmate.text = "J'a doube"
        fields[row][column].opacity = 1.0
        guiState.currentlyTouchedPieceY = -1
        guiState.currentlyTouchedPieceX = -1
    }

    public static Chessboard onClick(int row, int column, ImageView[][] fields, Chessboard board, ChessGUIState guiState, ChessImages images, Text checkmate, Text whiteMoves, Text blackMoves) {
        if (board.checkmate || board.stalemate)
        {
            return board;
        }
        int piece = board.getChessPiece(row, column)

        if (guiState.currentlyTouchedPieceX < 0 && guiState.currentlyTouchedPieceY < 0 && piece > 1) {
            initiateTouch(row, column, fields, board, guiState, checkmate)
        } else if (guiState.currentlyTouchedPieceY == row && guiState.currentlyTouchedPieceX == column && piece > 1) {
            jadoube(row, column, fields, board, guiState, checkmate)
        } else if (guiState.currentlyTouchedPieceX >= 0 && guiState.currentlyTouchedPieceY >= 0) {
            board = move(guiState.currentlyTouchedPieceY, guiState.currentlyTouchedPieceX, row, column, fields, board, guiState, images, checkmate, whiteMoves, blackMoves);
        } else {
            checkmate.text = "Can't figure out what you want"
        }
        return board;
    }

    public static Chessboard move(int fromRow, int fromColumn, int toRow, int toColumn, ImageView[][] fields, Chessboard board, ChessGUIState guiState, ChessImages images, Text checkmate, Text whiteMoves, Text blackMoves) {
        if (board.isMovePossible(fromRow, fromColumn, toRow, toColumn)) {
            fields[fromRow][fromColumn].opacity = 1.0
            addMoveNotation(board, toRow, toColumn, fromRow, fromColumn, whiteMoves, blackMoves)
            board = board.moveChessPiece(fromRow, fromColumn, toRow, toColumn, board.activePlayerIsWhite?ChessConstants.w_dame:ChessConstants.s_dame)
            guiState.currentlyTouchedPieceX = -1
            guiState.currentlyTouchedPieceY = -1
            redraw(fields, board, images, checkmate, whiteMoves, blackMoves)
            history += board
            whiteMoveHistory += whiteMoves.text
            blackMoveHistory += blackMoves.text
//            PrimitiveMoveGenerator generator = new PrimitiveMoveGenerator(board.board, board.activePlayerIsWhite);
//            def move = generator.findBestMove()
            board = opponentsMove(board, whiteMoves, blackMoves, checkmate, fields, images)
        } else {
            checkmate.text = "illegal move"
        }
        return board;
    }

    private static Chessboard opponentsMove(Chessboard board, Text whiteMoves, Text blackMoves, Text checkmate, ImageView[][] fields, ChessImages images) {
        Timeline time = new Timeline();
        time.setCycleCount(1);
        KeyFrame keyFrame = new KeyFrame(Duration.millis(47), new EventHandler<ActionEvent>() {
            public void handle(ActionEvent event) {
                try {
                    Move move = board.findBestMove();
//                if ((!board.stalemate) && (!board.checkmate)) {
                    if (null != move) {
                        addMoveNotation(board, move.toRow, move.toColumn, move.fromRow, move.fromColumn, whiteMoves, blackMoves)
                        board = board.moveChessPiece(move)
                        ChessUIComponents.chessboard = board
                        history += board
                        whiteMoveHistory += whiteMoves.text
                        blackMoveHistory += blackMoves.text
                    } else
                        checkmate.text = "(error)"
//                }
                }
                catch (WhiteIsCheckMateException p_win) {
                    checkmate.text = "Checkmate!\nBlack wins!"
                    board.checkmate=true
                }
                catch (BlackIsCheckMateException p_win) {
                    checkmate.text = "Checkmate!\nWhite wins!"
                    board.checkmate=true
                }
                catch (EndOfGameException) {
                    checkmate.text = "Stalemate!"
                    board.stalemate=true
                }
                redraw(fields, board, images, checkmate, whiteMoves, blackMoves)
            }
        });

        time.getKeyFrames().add(keyFrame);
        time.playFromStart()
        return board

    }

    private static void addMoveNotation(Chessboard board, int toRow, int toColumn, int fromRow, int fromColumn, Text whiteMoves, Text blackMoves) {
        String text = getNotation(board, toRow, toColumn, fromRow, fromColumn)
        if (board.activePlayerIsWhite) whiteMoves.text += text + "\n" else blackMoves.text += text + "\n"
    }

    private static String getNotation(Chessboard board, int toRow, int toColumn, int fromRow, int fromColumn) {
        int capturedPiece = board.getChessPiece(toRow, toColumn)
        if (capturedPiece == -1) {
            if (board.activePlayerIsWhite) {
                capturedPiece = ChessConstants.w_bauer
            } else {
                capturedPiece = ChessConstants.s_bauer
            }
        }
        Move m = new Move(board.getChessPiece(fromRow, fromColumn), fromRow, fromColumn, toRow, toColumn, 0, false, 0 != capturedPiece, capturedPiece)
        Chessboard newBoard = board.moveChessPiece(m)
        boolean check = board.activePlayerIsWhite ? newBoard.isBlackKingThreatened : newBoard.isWhiteKingThreatened
        m.opponentInCheck = check
        return m.getNotation()
    }

    public static void redraw(ImageView[][] fields, Chessboard board, ChessImages images, Text checkmate, Text whiteMoves, Text blackMoves) {
        8.times
                { int row ->
                    8.times
                            { int column ->
                                fields[row][column].setImage(images.getImage(board.getChessPiece(row, column), row, column))
                            }
                }
        if (board.stalemate) {
//            checkmate.text = "Stalemate!"
        } else if (board.checkmate) {
//            checkmate.text = "Checkmate!"
        } else
        if (board.activePlayerIsWhite) {
            checkmate.text = "white move"
        } else {
            checkmate.text = "black move"
        }
    }

    public static Chessboard lastMove(Chessboard board, Text whiteMoves, Text blackMoves) {
        if (history.size() > 0) {
            Chessboard last = history[-2]
            history = history[0..-2]
            if (whiteMoveHistory.size() > 1) {
                whiteMoveHistory = whiteMoveHistory[0..-2]
                whiteMoves.text = whiteMoveHistory[-1]
            } else {
                whiteMoveHistory = []
                whiteMoves.text = ""
            }
            if (blackMoveHistory.size() > 1) {
                blackMoveHistory = blackMoveHistory[0..-2]
                blackMoves.text = blackMoveHistory[-1]
            } else {
                blackMoveHistory = []
                blackMoves.text = ""
            }
            return last;
        } else return board
    }

}
