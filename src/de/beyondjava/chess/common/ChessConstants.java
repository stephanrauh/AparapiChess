package de.beyondjava.chess.common;

/**
 * Created with IntelliJ IDEA.
 * User: SoyYo
 * Date: 02.02.13
 * Time: 22:32
 * To change this template use File | Settings | File Templates.
 */
public interface ChessConstants {
    public static final boolean s_WHITE = true;
    public static final boolean s_BLACK = false;
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
            100, 100, 100, 100, // pawns
            500, 500, 500, 500, // rooks,
            275, 275, 275, 275, // knights (Springer)
            325, 325, 325, 325, // bishops (Läufer)
            1000, 1000, 1000, 1000, // queens
            9999, 9999, 9999, 9999 // kings
    };
    String[] columns = {"A", "B", "C", "D", "E", "F", "G", "H"};
    String[] rows = {"8", "7", "6", "5", "4", "3", "2", "1"};

    public static final int[][] positions =
                    {{0, 10, 20,  30,  30, 20, 10, 0},
                    {10, 20, 30,  40,  40, 30, 20, 10},
                    {15, 30, 60,  80,  80, 60, 30, 15},
                    {20, 40, 70, 100, 100, 70, 40, 20},
                    {20, 40, 70, 100, 100, 70, 40, 20},
                    {15, 30, 40,  80,  80, 40, 30, 15},
                    {10, 20, 30,  40,  40, 30, 20, 10},
                    { 0, 10, 20,  30,  30, 20, 10, 0},
            };
    public static final int[][] whitePawnPositions =
            {{200, 200, 200, 200, 200, 200, 200, 200},
                    {55, 70, 110, 170, 170, 110, 70, 55},
                    {45, 60, 90, 140, 140, 90, 60, 45},
                    {35, 50, 70, 110, 110, 70, 50, 35},
                    {20, 40, 60, 90, 90, 60, 40, 20},
                    {15, 30, 40, 50, 50, 40, 30, 15},
                    {10, 20, 30, 40, 40, 30, 20, 10},
                    {0, 10, 20, 30, 30, 20, 10, 0},
            };




}
