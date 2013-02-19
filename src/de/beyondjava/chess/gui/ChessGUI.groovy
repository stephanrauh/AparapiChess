package de.beyondjava.chess.gui
import de.beyondjava.chess.objectOrientedEngine.Chessboard
import javafx.scene.paint.Color

import static groovyx.javafx.GroovyFX.start

ChessMoveGUI uiElements = new ChessMoveGUI()
uiElements.with {
    start {
        stage(title: 'Beyond Java Chess GUI using GroovyFX', show: true) {
            chessScene = scene {
                rectangle(width: 1100, height: 880, fill: BLACK)
                rectangle(x: 20, y: 60, width: 800, height: 800, fill: Color.SANDYBROWN)
                rectangle(x: 60, y: 100, width: 720, height: 720, fill: Color.BLACK)

                toolBar {
                    button(text: "flip board", onAction: { chessboard = opponentsMove(chessboard, whiteMoves, blackMoves, checkmate, fields, images) })
                    button(text: "new game", onAction: { chessboard = new Chessboard(); redraw(fields, chessboard, images, checkmate, whiteMoves, blackMoves) })
                    button(text: "back", onAction: { chessboard = lastMove(chessboard, whiteMoves, blackMoves); redraw(fields, chessboard, images, checkmate, whiteMoves, blackMoves) })
                }

                group {
                    8.times { int column ->
                        8.times { int row ->
                            fields[row][column] = imageView(x: 60 + 90 * column, y: 100 + 90 * row,
                                    onMouseClicked: { chessboard = onClick(row, column, fields, chessboard, state, images, checkmate, whiteMoves, blackMoves) })
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
                checkmate = text(layoutX: 856, layoutY: 816, text: "Checkmate!", font: '30pt sanserif', fill: Color.GOLD)

                text(layoutX: 0, layoutY: 896, font: '10pt sanserif', text: 'Legal notes:', fill: Color.BLACK)
                text(layoutX: 0, layoutY: 908, font: '10pt sanserif',
                        text: 'Source code by Stephan Rauh (www.beyondJava.net), published under GPL license. Use at your own risk.', fill: Color.BLACK)
                text(layoutX: 0, layoutY: 920, font: '10pt sanserif',
                        text: 'Images licenced under GPL licence (taken from http://commons.wikimedia.org/wiki/Category:SVG_chess_pieces/Standard)', fill: Color.BLACK)

            }
        }
        redraw(fields, chessboard, images, checkmate, whiteMoves, blackMoves)
        update()
    }
}