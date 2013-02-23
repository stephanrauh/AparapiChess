package de.beyondjava.chess.gui
import de.beyondjava.chess.Exceptions.BlackIsCheckMateException
import de.beyondjava.chess.Exceptions.EndOfGameException
import de.beyondjava.chess.Exceptions.WhiteIsCheckMateException
import de.beyondjava.chess.common.ChessConstants
import de.beyondjava.chess.common.Move
import de.beyondjava.chess.linearEngine.LinearChessboard
import javafx.animation.KeyFrame
import javafx.animation.Timeline
import javafx.concurrent.Task
import javafx.event.ActionEvent
import javafx.event.EventHandler
import javafx.scene.Cursor
import javafx.scene.Scene
import javafx.scene.control.CheckBox
import javafx.scene.control.ProgressIndicator
import javafx.scene.control.Slider
import javafx.scene.image.ImageView
import javafx.scene.text.Text
import javafx.util.Duration

import java.text.NumberFormat
/**
 * This class tries to make sense out of the users mouse clicks.
 */
class ChessMoveGUI {
    def columnNames = ['A', 'B', 'C', 'D', 'E', 'F', 'G', 'H']
    def rowNames = ['8', '7', '6', '5', '4', '3', '2', '1']

    ChessImages images = new ChessImages()
    ImageView[][] fields = new ImageView[8][8];
    ChessGUIState state = new ChessGUIState();
    Text checkmate = null
    Text whiteMoves = null
    Text blackMoves = null
    Text statistics = null
    Text statistics2 = null
    Scene chessScene;
    ProgressIndicator progress;
    Slider depthSlider;
    Slider widthSlider;
    CheckBox multithreadingCheckbox;

    static LinearChessboard chessboard = new LinearChessboard()
    static List<Move> showMoves = null;

    public void draw(LinearChessboard c) {
        chessboard = c;
        new ChessGUI().run()
    }

    public void draw(LinearChessboard c, List<Move> moves) {
        chessboard = c;
        showMoves = moves;
        new ChessGUI().run()
    }

    public static void update() {
        if (showMoves != null) {
            double op = 0.8
            showMoves.each { Move m -> fields[m.toRow][m.toColumn].opacity = op; op -= 0.1 }
        }
    }

    List<LinearChessboard> history = [new LinearChessboard()]
    List<String> whiteMoveHistory = []
    List<String> blackMoveHistory = []

    public void initiateTouch(int row, int column, ImageView[][] fields, LinearChessboard board, ChessGUIState guiState, Text checkmate) {
        int piece = board.getChessPiece(row, column)
        if (board.isActivePlayersPiece(piece)) {
            fields[row][column].opacity = 0.7
            guiState.currentlyTouchedPieceY = row
            guiState.currentlyTouchedPieceX = column
        } else {
            checkmate.text = "\nopponent's piece"
        }
    }

    public void jadoube(int row, int column, ImageView[][] fields, LinearChessboard board, ChessGUIState guiState, Text checkmate) {
        checkmate.text = "\nJ'a doube!"
        fields[row][column].opacity = 1.0
        guiState.currentlyTouchedPieceY = -1
        guiState.currentlyTouchedPieceX = -1
    }

    public LinearChessboard onClick(int row, int column, ImageView[][] fields, LinearChessboard board, ChessGUIState guiState, ChessImages images, Text checkmate, Text whiteMoves, Text blackMoves) {
        if (board.checkmate || board.stalemate) {
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
            checkmate.text = "\nCan't figure out what you want"
        }
        return board;
    }

    public LinearChessboard move(int fromRow, int fromColumn, int toRow, int toColumn, ImageView[][] fields, LinearChessboard board, ChessGUIState guiState, ChessImages images, Text checkmate, Text whiteMoves, Text blackMoves) {
        if (board.isMovePossible(fromRow, fromColumn, toRow, toColumn)) {
            fields[fromRow][fromColumn].opacity = 1.0
            addMoveNotation(board, toRow, toColumn, fromRow, fromColumn, whiteMoves, blackMoves)
            board = board.moveChessPiece(fromRow, fromColumn, toRow, toColumn, 0)
            guiState.currentlyTouchedPieceX = -1
            guiState.currentlyTouchedPieceY = -1
            redraw(fields, board, images, checkmate, whiteMoves, blackMoves)
            history += board
            whiteMoveHistory += whiteMoves.text
            blackMoveHistory += blackMoves.text
            board = opponentsMove(board, whiteMoves, blackMoves, checkmate, fields, images)
        } else {
            checkmate.text = "\nillegal move"
        }
        return board;
    }

    private LinearChessboard opponentsMove(LinearChessboard board, Text whiteMoves, Text blackMoves, Text checkmate, ImageView[][] fields, ChessImages images) {
        board.multithreading=multithreadingCheckbox.selected
        board.width=widthSlider.value
        board.depth=depthSlider.value
        final long start = System.nanoTime()

        Timeline time = new Timeline();
        time.setCycleCount(-1);
        KeyFrame keyFrame = new KeyFrame(Duration.millis(200), new EventHandler<ActionEvent>() {
            public void handle(ActionEvent event) {
                updateStatistics(start)
            }
        });

        time.getKeyFrames().add(keyFrame);
        time.playFromStart()
        Task<Void> task = new Task<Void>() {
            public Void call() {
                try {
                    chessScene.setCursor(Cursor.WAIT)
                    if (board.activePlayerIsWhite) {
                        checkmate.text = "\nwhite's thinking..."
                    } else {
                        checkmate.text = "\nblack's thinking..."
                    }
                    Move move
                    try {
                        move = board.findBestMove()
                    }
                    finally {
                        time.stop()
                        updateStatistics(start)
                    }
                    if (null != move) {
                        addMoveNotation(board, move.toRow, move.toColumn, move.fromRow, move.fromColumn, whiteMoves, blackMoves)
                        board = board.moveChessPiece(move)
                        redraw(fields, board, images, checkmate, whiteMoves, blackMoves)
                        chessboard = board
                        history += board
                        whiteMoveHistory += whiteMoves.text
                        blackMoveHistory += blackMoves.text
                    } else
                        checkmate.text = "(error)"
                }
                catch (WhiteIsCheckMateException p_win) {
                    blackMoves.text = blackMoves.text.trim() + "+";
                    checkmate.text = "Checkmate!\nBlack wins!"
                    board.checkmate = true
                }
                catch (BlackIsCheckMateException p_win) {
                    whiteMoves.text = whiteMoves.text.trim() + "+";
                    checkmate.text = "Checkmate!\nWhite wins!"
                    board.checkmate = true
                }
                catch (EndOfGameException p_draw) {
                    checkmate.text = "\nStalemate!"
                    board.stalemate = true
                }
                catch (Exception p_error) {
                    checkmate.text = "technical error\n" + p_error.getClass().getSimpleName()
                    p_error.printStackTrace()
                }
                chessScene.setCursor(Cursor.HAND)
            }

        };
        progress.visibleProperty().bind(task.runningProperty());
        new Thread(task).start()
        return board

    }
    private void updateStatistics(long start) {

        long totalTime = System.nanoTime() - start;
        String s = "";
        def evalPos = NumberFormat.getInstance().format(LinearChessboard.evaluatedPositions)
        def calc = NumberFormat.getInstance().format(((int)(totalTime / 100000000)) / 10.0d)
        def eval = NumberFormat.getInstance().format((((int) (LinearChessboard.totalTime / 100000000)) / 10.0d))
        def avg = NumberFormat.getInstance().format(((int) (LinearChessboard.totalTime / LinearChessboard.evaluatedPositions)) / 1000.0d)
        def cores = Runtime.getRuntime().availableProcessors()
        s += "Calculation took $calc s (* $cores cores)\n"
        s += "evaluation took  $eval s\n";
        s += "Average evaluation: $avg µs\n";
        statistics.text = s;
        s = "positions evalutated:" + evalPos + "\n";
        statistics2.text = s
    }


    private void addMoveNotation(LinearChessboard board, int toRow, int toColumn, int fromRow, int fromColumn, Text whiteMoves, Text blackMoves) {
        String text = getNotation(board, toRow, toColumn, fromRow, fromColumn)
        if (board.activePlayerIsWhite) whiteMoves.text += "${board.moveCount}. $text\n" else blackMoves.text += "$text\n"
    }

    private String getNotation(LinearChessboard board, int toRow, int toColumn, int fromRow, int fromColumn) {
        boolean enPassant=false;
        int capturedPiece = board.getChessPiece(toRow, toColumn)
        if (capturedPiece == -1) {
            enPassant=true;
            if (board.activePlayerIsWhite) {
                capturedPiece = ChessConstants.W_PAWN
            } else {
                capturedPiece = ChessConstants.B_PAWN
            }
        }
        Move m = new Move(board.getChessPiece(fromRow, fromColumn), fromRow, fromColumn, toRow, toColumn, 0, false, 0 != capturedPiece, capturedPiece)
        LinearChessboard newBoard = board.moveChessPiece(m)
        boolean check = board.activePlayerIsWhite ? newBoard.isBlackKingThreatened : newBoard.isWhiteKingThreatened
        m.opponentInCheck = check
        return m.getNotation(enPassant)
    }

    public void redraw(ImageView[][] fields, LinearChessboard board, ChessImages images, Text checkmate, Text whiteMoves, Text blackMoves) {
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
        } else if (board.activePlayerIsWhite) {
            checkmate.text = "\nwhite move"
        } else {
            checkmate.text = "\nblack move"
        }
    }

    public LinearChessboard lastMove(LinearChessboard board, Text whiteMoves, Text blackMoves) {
        if (history.size() > 0) {
            LinearChessboard last = history[-2]
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
