package de.beyondjava.chess

import javafx.scene.image.Image

/**
 * Created with IntelliJ IDEA.
 * User: SoyYo
 * Date: 02.02.13
 * Time: 19:17
 * To change this template use File | Settings | File Templates.
 */
class ChessImages {
    String[] chessPiecesFileName = ['s_feld.png', 'w_feld.png',
            "s_s_bauer.png",
            "s_w_bauer.png",
            "w_s_bauer.png",
            "w_w_bauer.png",
            "s_s_turm.png",
            "s_w_turm.png",
            "w_s_turm.png",
            "w_w_turm.png",
            "s_s_springer.png",
            "s_w_springer.png",
            "w_s_springer.png",
            "w_w_springer.png",
            "s_s_laeufer.png",
            "s_w_laeufer.png",
            "w_s_laeufer.png",
            "w_w_laeufer.png",
            "s_s_dame.png",
            "s_w_dame.png",
            "w_s_dame.png",
            "w_w_dame.png",
            "s_s_koenig.png",
            "s_w_koenig.png",
            "w_s_koenig.png",
            "w_w_koenig.png"
    ]

    int s_empty = 0
    int w_empty = 1
    int s_bauer = 2
    int w_bauer = 4
    int s_turm = 6
    int w_turm = 8
    int s_springer = 10
    int w_springer = 12
    int s_laeufer = 14
    int w_laeufer = 16
    int s_dame = 18
    int w_dame = 20
    int s_koenig = 22
    int w_koenig = 24

    int[][] baseColor = new int[8][8]
    Image[] figureImages = new Image[chessPiecesFileName.size()]

    ChessImages() {
        boolean white = false; // note that position (0,0) is top left, in the black realm
        8.times { int row -> 8.times { int column -> baseColor[row][column] = (white ? 1 : 0); white = !white; }; white = !white; }
        chessPiecesFileName.size().times { int i -> figureImages[i] = new Image("file:E:/this/Chess/ChessGUI/wikimediaimages/${chessPiecesFileName[i]}", 90, 90, true, true) };
    }

    public Image getImage(int figure, int row, int column) {
        int base = baseColor[row][column]
        if (figure < 0)
            return figureImages[base];
        return figureImages[figure + base]
    }

}
