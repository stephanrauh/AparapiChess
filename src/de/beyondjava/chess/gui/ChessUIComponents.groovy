package de.beyondjava.chess.gui

import de.beyondjava.chess.common.Move
import de.beyondjava.chess.objectOrientedEngine.Chessboard
import javafx.scene.image.ImageView
import javafx.scene.text.Text

/**
 * Created with IntelliJ IDEA.
 * User: SoyYo
 * Date: 18.02.13
 * Time: 22:47
 * To change this template use File | Settings | File Templates.
 */
class ChessUIComponents {
    def columnNames = ['A', 'B', 'C', 'D', 'E', 'F', 'G', 'H']
    def rowNames = ['8', '7', '6', '5', '4', '3', '2', '1']

    ChessImages images=new ChessImages()
    ImageView[][] fields = new ImageView[8][8];
    ChessGUIState state = new ChessGUIState();
    Text checkmate = null
    Text whiteMoves = null
    Text blackMoves = null

    static Chessboard chessboard = new Chessboard()
    static List<Move> showMoves = null;

    public void draw(Chessboard c)
    {
        chessboard=c;
        new ChessGUI().run()
    }
    public static void draw(Chessboard c, List<Move> moves)
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
