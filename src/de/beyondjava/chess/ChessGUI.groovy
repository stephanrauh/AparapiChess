package de.beyondjava.chess

import javafx.scene.image.ImageView
import javafx.scene.paint.Color

import static groovyx.javafx.GroovyFX.start

def rowNames = ['1', '2', '3', '4', '5', '6', '7', '8']
def columnNames = ['A', 'B', 'C', 'D', 'E', 'F', 'G', 'H']

ChessImages images=new ChessImages()
Chessboard brett = new Chessboard()
ImageView[][] fields = new ImageView[8][8];
ChessGUIState state = new ChessGUIState();


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
                                              onMouseClicked: {ChessMoveGUI.onClick(row, column, fields, brett, state, images)})
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
        }
    }
    ChessMoveGUI.redraw(fields, brett, images)
}