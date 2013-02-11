package de.beyondjava.chess.gui

import de.beyondjava.chess.common.Move
import de.beyondjava.chess.objectOrientedEngine.Chessboard
import javafx.scene.image.ImageView

/**
 * Use this class to draw arbitrary chess boards
 */
class ChessGUIRemoteControl {
    static ChessImages images=new ChessImages()
    static Chessboard chessboard = new Chessboard()
    static ImageView[][] fields = new ImageView[8][8];
    static ChessGUIState state = new ChessGUIState();
    static List<Move> showMoves = null;

    public void draw(Chessboard c)
    {
        chessboard=c;
        new ChessGUI().run()
    }
    public void draw(Chessboard c, List<Move> moves)
    {
        chessboard=c;
        showMoves=moves;
        new ChessGUI().run()
    }

    public static void update()
    {
        if (showMoves!=null)
        {
            double op = 0.8
            showMoves.each{Move m -> fields[m.toRow][m.toColumn].opacity=op; op -= 0.1}
        }

    }

}
