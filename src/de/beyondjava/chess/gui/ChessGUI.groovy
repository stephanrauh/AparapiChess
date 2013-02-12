package de.beyondjava.chess.gui
import de.beyondjava.chess.objectOrientedEngine.Chessboard
import javafx.scene.image.ImageView
import javafx.scene.paint.Color
import javafx.scene.text.Text

import static groovyx.javafx.GroovyFX.start

def columnNames = ['A', 'B', 'C', 'D', 'E', 'F', 'G', 'H']
def rowNames = ['8', '7', '6', '5', '4', '3', '2', '1']

ChessImages images = ChessGUIRemoteControl.images
Chessboard chessboard = ChessGUIRemoteControl.chessboard
ImageView[][] fields = ChessGUIRemoteControl.fields
ChessGUIState state = ChessGUIRemoteControl.state
Text checkmate = null
Text whiteMoves = null
Text blackMoves = null

start {
    stage(title: 'Beyond Java Chess GUI using GroovyFX', show: true) {
        scene {
            rectangle(width: 1100, height: 840, fill: BLACK)
            rectangle(x: 20, y: 20, width: 800, height: 800, fill: Color.SANDYBROWN)
            rectangle(x: 60, y: 60, width: 720, height: 720, fill: Color.BLACK)
            group {
                8.times { int column ->
                    8.times { int row ->
                        fields[row][column] = imageView(x: 60 + 90 * column, y: 60 + 90 * row,
                                onMouseClicked: { chessboard = ChessMoveGUI.onClick(row, column, fields, ChessGUIRemoteControl.chessboard, state, images, checkmate, whiteMoves, blackMoves) })
                    }
                }
                8.times {
                    text(
                            layoutX: 60 + 45 + 90 * it,
                            layoutY: 50, text: columnNames[it], font: '30pt sanserif') {
                        fill linearGradient(endX: 0, stops: [BLACK, BROWN])
                    }
                    text(
                            layoutX: 60 + 45 + 90 * it,
                            layoutY: 810, text: columnNames[it], font: '30pt sanserif') {
                        fill linearGradient(endX: 0, stops: [BLACK, BROWN])
                    }
                    text(
                            layoutX: 30,
                            layoutY: 120 + 90 * it, text: rowNames[it], font: '30pt sanserif') {
                        fill linearGradient(endX: 0, stops: [BLACK, BROWN])
                    }
                    text(
                            layoutX: 790,
                            layoutY: 120 + 90 * it, text: rowNames[it], font: '30pt sanserif') {
                        fill linearGradient(endX: 0, stops: [BLACK, BROWN])
                    }
                }

            }
            whiteMoves = text(layoutX: 856, layoutY: 40, text: "", font: '18pt sanserif', fill: Color.WHEAT)
            blackMoves = text(layoutX: 970, layoutY: 40, text: "", font: '18pt sanserif', fill: Color.WHEAT)
            checkmate = text(layoutX: 856, layoutY: 800, text: "Checkmate!", font: '30pt sanserif', fill: Color.GOLD)

            text(layoutX: 0, layoutY: 856, font: '10pt sanserif', text: 'Legal notes:', fill: Color.BLACK)
            text(layoutX: 0, layoutY: 868, font: '10pt sanserif',
                    text: 'Source code by Stephan Rauh (www.beyondjava.net), published under GPL license. Use at your own risk.', fill: Color.BLACK)
            text(layoutX: 0, layoutY: 880, font: '10pt sanserif',
                    text: 'Images licenced under GPL licence (taken from http://commons.wikimedia.org/wiki/Category:SVG_chess_pieces/Standard)', fill: Color.BLACK)

        }
    }
    ChessMoveGUI.redraw(fields, chessboard, images, checkmate, whiteMoves, blackMoves)
    ChessGUIRemoteControl.update()
}