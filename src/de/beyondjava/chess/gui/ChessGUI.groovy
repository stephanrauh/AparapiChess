package de.beyondjava.chess.gui
import de.beyondjava.chess.objectOrientedEngine.Chessboard
import javafx.scene.paint.Color

import static groovyx.javafx.GroovyFX.start

ChessUIComponents uiElements = new ChessUIComponents()
uiElements.with {
    start {
        stage(title: 'Beyond Java Chess GUI using GroovyFX', show: true) {
            scene {
                rectangle(width: 1100, height: 880, fill: BLACK)
                rectangle(x: 20, y: 60, width: 800, height: 800, fill: Color.SANDYBROWN)
                rectangle(x: 60, y: 100, width: 720, height: 720, fill: Color.BLACK)

                toolBar {
                    button(text: "flip board", onAction: { chessboard = ChessMoveGUI.opponentsMove(chessboard, whiteMoves, blackMoves, checkmate, fields, images) })
                    button(text: "new game", onAction: { chessboard = new Chessboard(); ChessMoveGUI.redraw(fields, chessboard, images, checkmate, whiteMoves, blackMoves) })
                    button(text: "back", onAction: { chessboard = ChessMoveGUI.lastMove(chessboard, whiteMoves, blackMoves); ChessMoveGUI.redraw(fields, chessboard, images, checkmate, whiteMoves, blackMoves) })
                }

                group {
                    8.times { int column ->
                        8.times { int row ->
                            fields[row][column] = imageView(x: 60 + 90 * column, y: 100 + 90 * row,
                                    onMouseClicked: { chessboard = ChessMoveGUI.onClick(row, column, fields, chessboard, state, images, checkmate, whiteMoves, blackMoves) })
                        }
                    }
                    8.times {
                        text(
                                layoutX: 50 + 45 + 90 * it,
                                layoutY: 90, text: columnNames[it], font: '30pt sanserif') {
                            fill linearGradient(endX: 0, stops: [BLACK, BROWN])
                        }
                        text(
                                layoutX: 50 + 45 + 90 * it,
                                layoutY: 850, text: columnNames[it], font: '30pt sanserif') {
                            fill linearGradient(endX: 0, stops: [BLACK, BROWN])
                        }
                        text(
                                layoutX: 30,
                                layoutY: 160 + 90 * it, text: rowNames[it], font: '30pt sanserif') {
                            fill linearGradient(endX: 0, stops: [BLACK, BROWN])
                        }
                        text(
                                layoutX: 790,
                                layoutY: 160 + 90 * it, text: rowNames[it], font: '30pt sanserif') {
                            fill linearGradient(endX: 0, stops: [BLACK, BROWN])
                        }
                    }

                }
                whiteMoves = text(layoutX: 856, layoutY: 100, text: "", font: '18pt sanserif', fill: Color.WHEAT)
                blackMoves = text(layoutX: 970, layoutY: 100, text: "", font: '18pt sanserif', fill: Color.WHEAT)
                checkmate = text(layoutX: 856, layoutY: 840, text: "Checkmate!", font: '30pt sanserif', fill: Color.GOLD)

                text(layoutX: 0, layoutY: 896, font: '10pt sanserif', text: 'Legal notes:', fill: Color.BLACK)
                text(layoutX: 0, layoutY: 908, font: '10pt sanserif',
                        text: 'Source code by Stephan Rauh (www.beyondJava.net), published under GPL license. Use at your own risk.', fill: Color.BLACK)
                text(layoutX: 0, layoutY: 920, font: '10pt sanserif',
                        text: 'Images licenced under GPL licence (taken from http://commons.wikimedia.org/wiki/Category:SVG_chess_pieces/Standard)', fill: Color.BLACK)

            }
        }
        ChessMoveGUI.redraw(fields, chessboard, images, checkmate, whiteMoves, blackMoves)
        update()
    }
}