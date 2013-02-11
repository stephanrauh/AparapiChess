package de.beyondjava.chess.common;

/**
 * Created with IntelliJ IDEA.
 * User: SoyYo
 * Date: 02.02.13
 * Time: 22:32
 * To change this template use File | Settings | File Templates.
 */
public interface ChessConstants {
    public static final int s_empty = 0;
    public static final int w_empty = 1;
    public static final int s_bauer = 2;
    public static final int w_bauer = 4;
    public static final int s_turm = 6;
    public static final int w_turm = 8;
    public static final int s_springer = 10;
    public static final int w_springer = 12;
    public static final int s_laeufer = 14;
    public static final int w_laeufer = 16;
    public static final int s_dame = 18;
    public static final int w_dame = 20;
    public static final int s_koenig = 22;
    public static final int w_koenig = 24;
    public static final int[][] initialBoard = {{6, 10, 14, 18, 22, 14, 10, 6},
            {2, 2, 2, 2, 2, 2, 2, 2},
            {0, 0, 0, 0, 0, 0, 0, 0},
            {0, 0, 0, 0, 0, 0, 0, 0},
            {0, 0, 0, 0, 0, 0, 0, 0},
            {0, 0, 0, 0, 0, 0, 0, 0},
            {4, 4, 4, 4, 4, 4, 4, 4},
            {8, 12, 16, 20, 24, 16, 12, 8}
    };
    public static final int[] s_MATERIAL_VALUE = {0, 0, // empty fields
            1000, 1000, 1000, 1000, // pawns
            5000, 5000, 5000, 5000, // rooks,
            2750, 2750, 2750, 2750, // knights (Springer)
            3250, 3250, 3250, 3250, // bishops (Läufer)
            10000, 10000, 10000, 10000, // queens
            10000000, 1000000, 10000000, 10000000 // kings
    };
    String[] columns = {"A", "B", "C", "D", "E", "F", "G", "H"};
    String[] rows = {"8", "7", "6", "5", "4", "3", "2", "1"};


}
