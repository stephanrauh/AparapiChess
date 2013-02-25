package de.beyondjava.chess.aparapiEngine.linearEngine;

import com.amd.aparapi.Kernel;
import com.amd.aparapi.Range;


/**
 * Represents the chess board and provide a couple of methods on possible moves.
 * Date: 02.02.13
 * Time: 19:17
 */
public class AparapiChessboardsBasis extends Kernel {
    public static final boolean s_WHITE = true;
    public static final boolean s_BLACK = false;
    public static final int B_EMPTY = 0;
    public static final int W_EMPTY = 1;
    public static final int B_PAWN = 2;
    public static final int W_PAWN = 4;
    public static final int B_ROOK = 6;
    public static final int W_ROOK = 8;
    public static final int B_KNIGHT = 10;
    public static final int W_KNIGHT = 12;
    public static final int B_BISHOP = 14;
    public static final int W_BISHOP = 16;
    public static final int B_QUEEN = 18;
    public static final int W_QUEEN = 20;
    public static final int B_KING = 22;
    public static final int W_KING = 24;
    public static final int[][] INITIAL_BOARD = {{6, 10, 14, 18, 22, 14, 10, 6},
            {2, 2, 2, 2, 2, 2, 2, 2},
            {0, 0, 0, 0, 0, 0, 0, 0},
            {0, 0, 0, 0, 0, 0, 0, 0},
            {0, 0, 0, 0, 0, 0, 0, 0},
            {0, 0, 0, 0, 0, 0, 0, 0},
            {4, 4, 4, 4, 4, 4, 4, 4},
            {8, 12, 16, 20, 24, 16, 12, 8}
    };
    public static final byte[] INITIAL_LINEAR_BOARD = {6, 10, 14, 18, 22, 14, 10, 6,
            2, 2, 2, 2, 2, 2, 2, 2,
            0, 0, 0, 0, 0, 0, 0, 0,
            0, 0, 0, 0, 0, 0, 0, 0,
            0, 0, 0, 0, 0, 0, 0, 0,
            0, 0, 0, 0, 0, 0, 0, 0,
            4, 4, 4, 4, 4, 4, 4, 4,
            8, 12, 16, 20, 24, 16, 12, 8
    };
    public static final int[] s_MATERIAL_VALUE = {0, 0, // empty fields
            100, 100, 100, 100, // pawns
            500, 500, 500, 500, // rooks,
            275, 275, 275, 275, // knights (Springer)
            325, 325, 325, 325, // bishops (Läufer)
            1000, 1000, 1000, 1000, // queens
            9999, 9999, 9999, 9999 // kings
    };
    public static final int[][] POSITIONAL_VALUES =
            {{0, 10, 20, 30, 30, 20, 10, 0},
                    {10, 20, 30, 40, 40, 30, 20, 10},
                    {15, 30, 60, 80, 80, 60, 30, 15},
                    {20, 40, 70, 100, 100, 70, 40, 20},
                    {20, 40, 70, 100, 100, 70, 40, 20},
                    {15, 30, 40, 80, 80, 40, 30, 15},
                    {10, 20, 30, 40, 40, 30, 20, 10},
                    {0, 10, 20, 30, 30, 20, 10, 0},
            };
    public static final int[][] WHITE_PAWN_POSITION_VALUES =
            {{200, 200, 200, 200, 200, 200, 200, 200},
                    {55, 70, 110, 180, 180, 110, 70, 55},
                    {45, 60, 90, 160, 160, 90, 60, 45},
                    {35, 50, 70, 130, 130, 70, 50, 35},
                    {20, 40, 50, 110, 110, 50, 40, 20},
                    {15, 30, 40, 50, 50, 40, 30, 15},
                    {20, 30, 60, 40, 40, 60, 40, 30},
                    {0, 0, 0, 0, 0, 0, 0, 0},
            };
    public static final int[] DOUBLE_PAWN_BONUS = {50 /* n/a */, 50 /* A+B */,
            60 /* B+C */,
            70 /* C+D */,
            80 /* D+E */,
            70 /* E + F */,
            60 /* F + G */,
            50 /* G + H */};
    public boolean[] blackCastlingLeftImpossible = new boolean[40000];
    public boolean[] blackCastlingRightImpossible = new boolean[40000];
    public boolean[] whiteCastlingLeftImpossible = new boolean[40000];
    public boolean[] whiteCastlingRightImpossible = new boolean[40000];
    public boolean[] blackHasCastled = new boolean[40000];
    public boolean[] whiteHasCastled = new boolean[40000];
    public short[] moveCount = new short[40000];
    public byte[][] board = new byte[40000][64];
    public boolean[] activePlayerIsWhite;
    public int[][] canBeReachedByTheseWhitePieces = new int[40000][64];
    public int[][] canBeReachedByTheseBlackPieces = new int[40000][64];
    public int[] whiteMaterialValue = new int[40000];
    public int[] blackMaterialValue = new int[40000];
    public int[] whiteExpectedLoss = new int[40000];
    public int[] blackExpectedLoss = new int[40000];
    /**
     * Value of black pieces threatened by white pieces
     */
    public int[] whitePotentialMaterialValue=new int[40000];
    /**
     * Value of white pieces threatened by black pieces
     */
    public int[] blackPotentialMaterialValue=new int[40000];
    public int[] whiteFieldPositionValue=new int[40000];
    public int[] blackFieldPositionValue=new int[40000];
    public int[] whiteMoveValue=new int[40000];
    public int[] blackMoveValue=new int[40000];
    public int[] whiteCoverageValue=new int[40000];
    public int[] blackCoverageValue=new int[40000];
    public boolean[] isWhiteKingThreatened = new boolean[40000];
    public boolean[] isBlackKingThreatened = new boolean[40000];
    public int[] whiteTotalValue=new int[40000];
    public int[] blackTotalValue=new int[40000];
    public int[][] whiteMoves = new int[40000][100];
    public byte[] numberOfWhiteMoves =new byte[40000];
    public int[][] blackMoves = new int[40000][100];
    public byte[] numberOfBlackMoves =new byte[40000];
    public boolean[] checkmate = new boolean[40000];

    public static void main(String... args) {
        AparapiChessboardsBasis a = new AparapiChessboardsBasis();
        a.run();
        for (int i = 0; i < 1000; i++) {
            evaluateParallel(a);
        }

    }
    public void run() {
        evaluateBoard();
    }


    private static void evaluateParallel(AparapiChessboardsBasis a) {
        long start = System.nanoTime();
        Range range = Range.create(1000000);
        a.execute(range);
        long end = System.nanoTime();
        System.out.println(((end - start) / 1000) / 1000.0d + " ms");
    }

    public void startGame()
    {
        board[0]=INITIAL_LINEAR_BOARD;
    }


    private void evaluateBoard() {
//            evaluateFieldPositionalValue();
//            findLegalMovesIgnoringCheck();
//            evaluateMobility();
//            evaluateThreats();
//            whiteTotalValue = (whiteMaterialValue-whiteExpectedLoss) * 10 + (whitePotentialMaterialValue >> 2) + whiteFieldPositionValue + whiteMoveValue + whiteCoverageValue;
//            blackTotalValue = (blackMaterialValue-blackExpectedLoss) * 10 + (blackPotentialMaterialValue >> 2) + blackFieldPositionValue + blackMoveValue + blackCoverageValue;
    }


    public int getChessPiece(int currentBoard, int row, int column) {
        return board[currentBoard][(row<<3)+column];
    }

    protected boolean isWhitePiece(int piece) {
        if (piece < 2) {
            return false;
        }
        return (((piece / 2) % 2) == 0);
    }

    protected boolean isBlackPiece(int piece) {
        if (piece < 2) {
            return false;
        }
        return (((piece / 2) % 2) == 1);
    }

    protected boolean isActivePlayersPiece(int currentBoard, int piece) {
        return activePlayerIsWhite[currentBoard] == isWhitePiece(piece);
    }

    protected boolean isInsideBoard(int row, int column) {
        if (row < 0 || row > 7) return false;
        if (column < 0 || column > 7) return false;
        return true;
    }

    protected boolean isEmptyField(int currentBoard, int row, int column) {
        if (!isInsideBoard(row, column)) return false;
        if (getChessPiece(currentBoard, row, column) <= 0) return true;
        return false;
    }

    protected boolean isOpponentsPiece(int currentBoard, int row, int column, boolean p_activePlayerIsWhite) {
        if (!isInsideBoard(row, column)) return false;
        if (getChessPiece(currentBoard, row, column) <= 0) return false;
        if (p_activePlayerIsWhite) {
            return (isBlackPiece(getChessPiece(currentBoard, row, column)));
        } else {
            return (isWhitePiece(getChessPiece(currentBoard, row, column)));
        }
    }

    protected boolean isEmptyOrCanBeCaptured(int currentBoard, int row, int column, boolean p_activePlayerIsWhite) {
        if (!isInsideBoard(row, column)) return false;
        int piece = getChessPiece(currentBoard, row, column);
        if (piece <= 0) return true;
        if (p_activePlayerIsWhite) {
            return (isBlackPiece(piece));
        } else {
            return (isWhitePiece(piece));
        }
    }


}
