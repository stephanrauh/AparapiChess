package de.beyondjava.chess.aparapiEngine.linearEngine;

import com.amd.aparapi.Kernel;


/**
 * Represents the chess board and provide a couple of methods on possible moves.
 * Date: 02.02.13
 * Time: 19:17
 */
public abstract class AparapiChessboardsBoard extends Kernel {
    public static final int MAXBOARDS = 10000;
    public  final boolean s_WHITE = true;
    public  final boolean s_BLACK = false;
    public  final int B_EMPTY = 0;
    public  final int W_EMPTY = 1;
    public  final int B_PAWN = 2;
    public  final int W_PAWN = 4;
    public  final int B_ROOK = 6;
    public  final int W_ROOK = 8;
    public  final int B_KNIGHT = 10;
    public  final int W_KNIGHT = 12;
    public  final int B_BISHOP = 14;
    public  final int W_BISHOP = 16;
    public  final int B_QUEEN = 18;
    public  final int W_QUEEN = 20;
    public  final int B_KING = 22;
    public  final int W_KING = 24;
    public  final byte[] INITIAL_BOARD = {6, 10, 14, 18, 22, 14, 10, 6,
            2, 2, 2, 2, 2, 2, 2, 2,
            0, 0, 0, 0, 0, 0, 0, 0,
            0, 0, 0, 0, 0, 0, 0, 0,
            0, 0, 0, 0, 0, 0, 0, 0,
            0, 0, 0, 0, 0, 0, 0, 0,
            4, 4, 4, 4, 4, 4, 4, 4,
            8, 12, 16, 20, 24, 16, 12, 8
    };
    public  final int[] s_MATERIAL_VALUE = {0, 0, // empty fields
            100, 100, 100, 100, // pawns
            500, 500, 500, 500, // rooks,
            275, 275, 275, 275, // knights (Springer)
            325, 325, 325, 325, // bishops (Läufer)
            1000, 1000, 1000, 1000, // queens
            9999, 9999, 9999, 9999 // kings
    };
    public  final int[] POSITIONAL_VALUES =
            {0, 10, 20, 30, 30, 20, 10, 0,
                    10, 20, 30, 40, 40, 30, 20, 10,
                    15, 30, 60, 80, 80, 60, 30, 15,
                    20, 40, 70, 100, 100, 70, 40, 20,
                    20, 40, 70, 100, 100, 70, 40, 20,
                    15, 30, 40, 80, 80, 40, 30, 15,
                    10, 20, 30, 40, 40, 30, 20, 10,
                    0, 10, 20, 30, 30, 20, 10, 0
            };
    public  final int[][] WHITE_PAWN_POSITION_VALUES =
            {{200, 200, 200, 200, 200, 200, 200, 200},
                    {55, 70, 110, 180, 180, 110, 70, 55},
                    {45, 60, 90, 160, 160, 90, 60, 45},
                    {35, 50, 70, 130, 130, 70, 50, 35},
                    {20, 40, 50, 110, 110, 50, 40, 20},
                    {15, 30, 40, 50, 50, 40, 30, 15},
                    {20, 30, 60, 40, 40, 60, 40, 30},
                    {0, 0, 0, 0, 0, 0, 0, 0},
            };
    public  final int[] DOUBLE_PAWN_BONUS = {50 /* n/a */, 50 /* A+B */,
            60 /* B+C */,
            70 /* C+D */,
            80 /* D+E */,
            70 /* E + F */,
            60 /* F + G */,
            50 /* G + H */};
    public boolean[] blackCastlingLeftImpossible = new boolean[MAXBOARDS];
    public boolean[] blackCastlingRightImpossible = new boolean[MAXBOARDS];
    public boolean[] whiteCastlingLeftImpossible = new boolean[MAXBOARDS];
    public boolean[] whiteCastlingRightImpossible = new boolean[MAXBOARDS];
    public boolean[] blackHasCastled = new boolean[MAXBOARDS];
    public boolean[] whiteHasCastled = new boolean[MAXBOARDS];
    public short[] moveCount = new short[MAXBOARDS];
    public byte[] board = new byte[MAXBOARDS *64];
    public boolean[] activePlayerIsWhite;
    public int[] canBeReachedByTheseWhitePieces = new int[MAXBOARDS *64];
    public int[] canBeReachedByTheseBlackPieces = new int[MAXBOARDS *64];
    public int[] whiteMaterialValue = new int[MAXBOARDS];
    public int[] blackMaterialValue = new int[MAXBOARDS];
    public int[] whiteExpectedLoss = new int[MAXBOARDS];
    public int[] blackExpectedLoss = new int[MAXBOARDS];
    /**
     * Value of black pieces threatened by white pieces
     */
    public int[] whitePotentialMaterialValue=new int[MAXBOARDS];
    /**
     * Value of white pieces threatened by black pieces
     */
    public int[] blackPotentialMaterialValue=new int[MAXBOARDS];
    public int[] whiteFieldPositionValue=new int[MAXBOARDS];
    public int[] blackFieldPositionValue=new int[MAXBOARDS];
    public int[] whiteMoveValue=new int[MAXBOARDS];
    public int[] blackMoveValue=new int[MAXBOARDS];
    public int[] whiteCoverageValue=new int[MAXBOARDS];
    public int[] blackCoverageValue=new int[MAXBOARDS];
    public boolean[] isWhiteKingThreatened = new boolean[MAXBOARDS];
    public boolean[] isBlackKingThreatened = new boolean[MAXBOARDS];
    public int[] whiteTotalValue=new int[MAXBOARDS];
    public int[] blackTotalValue=new int[MAXBOARDS];
    public int[] whiteMoves = new int[MAXBOARDS *128];
    public byte[] numberOfWhiteMoves =new byte[MAXBOARDS];
    public int[] blackMoves = new int[MAXBOARDS *128];
    public byte[] numberOfBlackMoves =new byte[MAXBOARDS];
    public boolean[] checkmate = new boolean[MAXBOARDS];


    protected void startGame(int currentBoard)
    {
        System.arraycopy(INITIAL_BOARD, 0, board,currentBoard<<6, 64);
    }

    protected int getChessPiece(int currentBoard, int row, int column) {
        return board[(currentBoard<<6)+(row<<3)+column];
    }
    protected int readChessCell(int currentBoard, int cell) {
        return board[(currentBoard<<6)+cell];
    }

    protected boolean whitePiece(int piece) {
        if (piece < 2) {
            return false;
        }
        return (((piece / 2) % 2) == 0);
    }

    protected boolean blackPiece(int piece) {
        if (piece < 2) {
            return false;
        }
        return (((piece / 2) % 2) == 1);
    }

    protected boolean isActivePlayersPiece(int currentBoard, int piece) {
        return activePlayerIsWhite[currentBoard] == whitePiece(piece);
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
            return (blackPiece(getChessPiece(currentBoard, row, column)));
        } else {
            return (whitePiece(getChessPiece(currentBoard, row, column)));
        }
    }

    protected boolean isEmptyOrCanBeCaptured(int currentBoard, int row, int column, boolean p_activePlayerIsWhite) {
        if (!isInsideBoard(row, column)) return false;
        int piece = getChessPiece(currentBoard, row, column);
        if (piece <= 0) return true;
        if (p_activePlayerIsWhite) {
            return (blackPiece(piece));
        } else {
            return (whitePiece(piece));
        }
    }


}
