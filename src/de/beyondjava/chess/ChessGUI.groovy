package de.beyondjava.chess

import javafx.scene.image.ImageView
import javafx.scene.paint.Color

import static groovyx.javafx.GroovyFX.start

def rowNames =    ['A', 'B', 'C', 'D', 'E', 'F', 'G', 'H']
def columnNames = ['1', '2', '3', '4', '5', '6', '7', '8']

ChessImages images=ChessGUIRemoteControl.images
Chessboard chessboard =ChessGUIRemoteControl.chessboard
ImageView[][] fields = ChessGUIRemoteControl.fields
ChessGUIState state = ChessGUIRemoteControl.state


start {
    stage(title: 'Beyond Java Chess GUI using GroovyFX', show: true) {
        scene {
            rectangle(width: 840, height: 840, fill: BLACK)
            rectangle(x: 20, y: 20, width: 800, height: 800, fill: Color.SANDYBROWN)
            rectangle(x: 60, y: 60, width: 720, height: 720, fill: Color.BLACK)
            group {
                8.times { int column ->
                    8.times { int row ->
                        fields[row][column] = imageView(x: 60 + 90 * column, y: 60 + 90 * row,
                                              onMouseClicked: {chessboard=ChessMoveGUI.onClick(row, column, fields, chessboard, state, images)})
                    }
                }
                8.times {
                    text(
                            layoutX: 60 + 45 + 90 * it,
                            layoutY: 50, text: rowNames[it], font: '30pt sanserif') {
                        fill linearGradient(endX: 0, stops: [BLACK, BROWN])
                    }
                    text(
                            layoutX: 60 + 45 + 90 * it,
                            layoutY: 810, text: rowNames[it], font: '30pt sanserif') {
                        fill linearGradient(endX: 0, stops: [BLACK, BROWN])
                    }
                    text(
                            layoutX: 30,
                            layoutY: 120 + 90 * it, text: columnNames[it], font: '30pt sanserif') {
                        fill linearGradient(endX: 0, stops: [BLACK, BROWN])
                    }
                    text(
                            layoutX: 790,
                            layoutY: 120 + 90 * it, text: columnNames[it], font: '30pt sanserif') {
                        fill linearGradient(endX: 0, stops: [BLACK, BROWN])
                    }
                }

            }
            text(layoutX:0, layoutY:856, font:'10pt sanserif',
                    text:'Legal notes:')
            text(layoutX:0, layoutY:868, font:'10pt sanserif',
                    text:'Source code by Stephan Rauh (www.beyondjava.net), published under GPL license. Use at your own risk.')
            text(layoutX:0, layoutY:880, font:'10pt sanserif',
                    text:'Images licenced under GPL licence (taken from http://commons.wikimedia.org/wiki/Category:SVG_chess_pieces/Standard)')
            fill linearGradient(endX: 0, stops: [WHITE, WHITE])
        }
    }
    ChessMoveGUI.redraw(fields, chessboard, images)
    ChessGUIRemoteControl.update()
}