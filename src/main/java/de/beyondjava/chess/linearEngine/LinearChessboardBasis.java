package de.beyondjava.chess.linearEngine;

import de.beyondjava.chess.Exceptions.BlackIsCheckMateException;
import de.beyondjava.chess.Exceptions.EndOfGameException;
import de.beyondjava.chess.Exceptions.WhiteIsCheckMateException;
import de.beyondjava.chess.common.ChessConstants;
import de.beyondjava.chess.common.Move;
import de.beyondjava.chess.common.Piece;


/**
 * Represents the chess board and provide a couple of methods on possible moves.
 * Date: 02.02.13
 * Time: 19:17
 */
public class LinearChessboardBasis implements ChessConstants {
    public boolean blackCastlingLeftImpossible;
    public boolean blackCastlingRightImpossible;
    public boolean whiteCastlingLeftImpossible;
    public boolean whiteCastlingRightImpossible;
    public boolean blackHasCastled=false;
    public boolean whiteHasCastled=false;
    public int moveCount=0;
    public static int evaluatedPositions = 0; // DEBUG
    public static long totalTime = 0; // DEBUG
    public static long totalTimeGetNewBoard = 0; // DEBUG
    public final int[] board;
    public final boolean activePlayerIsWhite;
    public int[] canBeReachedByTheseWhitePieces = new int[64];
    public int[] canBeReachedByTheseBlackPieces = new int[64];
    public int whiteMaterialValue=0;      // effectively final
    public int blackMaterialValue=0;      // effectively final
    public int whiteExpectedLoss=0;      // effectively final
    public int blackExpectedLoss=0;      // effectively final
    /**
     * Value of black pieces threatened by white pieces
     */
    public int whitePotentialMaterialValue;      // effectively final
    /**
     * Value of white pieces threatened by black pieces
     */
    public int blackPotentialMaterialValue;      // effectively final
    public int whiteFieldPositionValue; // effectively final
    public int blackFieldPositionValue; // effectively final
    public int whiteMoveValue;
    public int blackMoveValue;
    public int whiteCoverageValue;
    public int blackCoverageValue;
    public boolean isWhiteKingThreatened;
    public boolean isBlackKingThreatened;
    public int whiteTotalValue;             // effectively final
    public int blackTotalValue;             // effectively final
    public int[] whiteMoves = new int[100]; // effectively final
    public int numberOfWhiteMoves = 0;      // effectively final
    public int[] blackMoves = new int[100]; // effectively final
    public int numberOfBlackMoves = 0;      // effectively final
    public boolean stalemate = false;
    public boolean checkmate = false;
    public int epColumn=-1;
    public int epRow=-1;

    private LinearChessboardBasis(boolean activePlayerIsWhite, int[] board, int oldMoveCount) {
        this.activePlayerIsWhite = activePlayerIsWhite;
        this.board = board;
        if (activePlayerIsWhite)
        {
            moveCount=oldMoveCount+1;
        }
        else
        {
            moveCount=oldMoveCount;
        }
        if (board[0]!= B_ROOK) blackCastlingLeftImpossible=true;
        if (board[7]!= B_ROOK) blackCastlingRightImpossible=true;
        if (board[56]!= W_ROOK) whiteCastlingRightImpossible=true;
        if (board[63]!= W_ROOK) whiteCastlingRightImpossible=true;
        if (board[4]!= B_KING) {
            blackCastlingLeftImpossible=true;
            blackCastlingRightImpossible=true;
        }
        if (board[60]!= W_KING) {
            whiteCastlingLeftImpossible=true;
            whiteCastlingRightImpossible=true;
        }
        evaluateBoard();
    }

    public LinearChessboardBasis() {
        this(true, ChessConstants.INITIAL_LINEAR_BOARD, 0);
    }

    public LinearChessboardBasis(boolean activePlayerIsWhite, LinearChessboardBasis board) {
        this(activePlayerIsWhite, board.board, board.moveCount);
    }

    public LinearChessboardBasis(LinearChessboardBasis oldBoard, int fromRow, int fromColumn, int toRow, int toColumn, int promotedPiece) {
        this(!oldBoard.activePlayerIsWhite, getNewBoard(oldBoard, fromRow, fromColumn, toRow, toColumn, promotedPiece), oldBoard.moveCount);
        whiteMaterialValue=oldBoard.whiteMaterialValue;
        blackMaterialValue=oldBoard.blackMaterialValue;
        int movedPiece = oldBoard.getChessPiece(fromRow, fromColumn);
        int arrivingPiece = getChessPiece(toRow, toColumn);
        int capturedPiece = oldBoard.getChessPiece(toRow, toColumn);
        if (capturedPiece != 0) {
            if (oldBoard.activePlayerIsWhite) {
                if (capturedPiece < 0){
                    if (movedPiece== W_PAWN)
                        capturedPiece = B_PAWN;
                    else
                        capturedPiece=0;
                }
                blackMaterialValue -= s_MATERIAL_VALUE[capturedPiece];
            } else {
                if (capturedPiece < 0){
                if (movedPiece== B_PAWN)
                    capturedPiece = W_PAWN;
                else
                    capturedPiece=0;
                }
                whiteMaterialValue -= s_MATERIAL_VALUE[capturedPiece];
            }
        }
        if (movedPiece!=arrivingPiece)
        {
            if (oldBoard.activePlayerIsWhite) {
                whiteMaterialValue += s_MATERIAL_VALUE[arrivingPiece]-s_MATERIAL_VALUE[movedPiece];
            } else {
                blackMaterialValue += s_MATERIAL_VALUE[arrivingPiece]-s_MATERIAL_VALUE[movedPiece];
            }
        }
//        evaluateMaterialValue();  // DEBUG
        whiteTotalValue = (whiteMaterialValue-whiteExpectedLoss) * 10 + (whitePotentialMaterialValue >> 2) + whiteFieldPositionValue + whiteMoveValue + whiteCoverageValue;
        blackTotalValue = (blackMaterialValue-blackExpectedLoss) * 10 + (blackPotentialMaterialValue >> 2) + blackFieldPositionValue + blackMoveValue + blackCoverageValue;
        if (movedPiece == W_PAWN) {
            if (fromRow - toRow == 2)
            {
                epColumn=toColumn;
                epRow=fromRow-1;
            }
        }
        else if (movedPiece==B_PAWN)
        {
            if (fromRow - toRow == -2)
            {
                epColumn=toColumn;
                epRow=fromRow+1;
            }
        }

    }

    private static int[] getNewBoard(LinearChessboardBasis oldBoard, int fromRow, int fromColumn, int toRow, int toColumn, int promotedPiece) {
        int[] newBoard = new int[64];
        System.arraycopy(oldBoard.board, 0, newBoard, 0, 64);
        if (oldBoard.epColumn>0)
        {
            newBoard[(oldBoard.epRow<<3)+oldBoard.epColumn]=0;
        }

        int piece = newBoard[(fromRow<<3)+fromColumn];
        if (piece== W_PAWN || piece== B_PAWN)
        {
            if (oldBoard.getChessPiece(toRow,toColumn) == -1) {
                // capture en passant
                int cap = newBoard[(fromRow<<3)+toColumn];
                int matCap=s_MATERIAL_VALUE[cap];
                newBoard[(fromRow<<3)+toColumn] = 0;
            }
        }
        if (piece == W_PAWN) {
            if (fromRow - toRow == 2)
            {
                newBoard[((fromRow - 1)<<3)+toColumn] = -1; // allow for en passant
            }
            if (toRow == 0) {
                // promotion
                if (promotedPiece > 0) {
                    piece = promotedPiece;
                } else
                    piece = W_QUEEN;
            }
        }
        if (piece == B_PAWN) {
            if (fromRow - toRow == -2)
                newBoard[((fromRow + 1)<<3)+toColumn] = -1; // allow for en passant
            if (toRow == 7) {
                // promotion
                if (promotedPiece > 0) {
                    piece = promotedPiece;
                } else
                    piece = B_QUEEN;
            }
        }
        if ((piece == W_KING && fromRow == 7) || (piece == B_KING && fromRow == 0)) {
            if (fromColumn == 4 && toColumn == 6) {
                // castling right hand side
                newBoard[(fromRow<<3)+5] = newBoard[(fromRow<<3)+7];
                newBoard[(fromRow<<3)+7] = 0;
            }
            if (fromColumn == 4 && toColumn == 2) {
                // castling left hand side
                newBoard[(fromRow<<3)+3] = newBoard[(fromRow<<3)+0];
                newBoard[(fromRow<<3)+0] = 0;
            }
        }
        newBoard[(toRow<<3)+toColumn] = piece;
        newBoard[(fromRow<<3)+fromColumn] = 0;
        return newBoard;
    }

    private static int[] getBoardFromPieces(Piece[] pieces) {
        int[] board = new int[64];
        for (Piece p : pieces) {
            board[(p.row<<3)+p.column] = p.piece;
        }
        return board;
    }

    private void evaluateBoard() {
        long timer = System.nanoTime();
//        evaluateMaterialValue();
        evaluateFieldPositionalValue();
        findLegalMovesIgnoringCheck();
        evaluateMobility();
        evaluateThreats();
        whiteTotalValue = (whiteMaterialValue-whiteExpectedLoss) * 10 + (whitePotentialMaterialValue >> 2) + whiteFieldPositionValue + whiteMoveValue + whiteCoverageValue;
        blackTotalValue = (blackMaterialValue-blackExpectedLoss) * 10 + (blackPotentialMaterialValue >> 2) + blackFieldPositionValue + blackMoveValue + blackCoverageValue;
        long duration = System.nanoTime() - timer;
        totalTime += duration;
        evaluatedPositions++;
    }

    public int getChessPiece(int row, int column) {
        return board[(row<<3)+column];
    }

    public LinearChessboard moveChessPiece(int fromRow, int fromColumn, int toRow, int toColumn, int promotedPiece) {
        return new LinearChessboard(this, fromRow, fromColumn, toRow, toColumn, promotedPiece);
    }

    public LinearChessboard moveChessPiece(Move move) {
        return new LinearChessboard(this, move.fromRow, move.fromColumn, move.toRow, move.toColumn, 0);
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

    protected boolean isActivePlayersPiece(int piece) {
        return activePlayerIsWhite == isWhitePiece(piece);
    }

    protected boolean isInsideBoard(int row, int column) {
        if (row < 0 || row > 7) return false;
        if (column < 0 || column > 7) return false;
        return true;
    }

    protected boolean isEmptyField(int row, int column) {
        if (!isInsideBoard(row, column)) return false;
        if (getChessPiece(row, column) <= 0) return true;
        return false;
    }

    protected boolean isOpponentsPiece(int row, int column, boolean p_activePlayerIsWhite) {
        if (!isInsideBoard(row, column)) return false;
        if (getChessPiece(row, column) <= 0) return false;
        if (p_activePlayerIsWhite) {
            return (isBlackPiece(getChessPiece(row, column)));
        } else {
            return (isWhitePiece(getChessPiece(row, column)));
        }
    }

    protected boolean isEmptyOrCanBeCaptured(int row, int column, boolean p_activePlayerIsWhite) {
        if (!isInsideBoard(row, column)) return false;
        int piece = getChessPiece(row, column);
        if (piece <= 0) return true;
        if (p_activePlayerIsWhite) {
            return (isBlackPiece(piece));
        } else {
            return (isWhitePiece(piece));
        }
    }

    private void evaluateMaterialValue() {
        int whiteValue = 0;
        int blackValue = 0;
        for (int cell=0; cell<64; cell++)
        {
            int piece = board[cell];
            if (isWhitePiece(piece)) {
                whiteValue += s_MATERIAL_VALUE[piece];
            }
            if (isBlackPiece(piece)) {
                blackValue += s_MATERIAL_VALUE[piece];
            }
        }
//        if (whiteMaterialValue != whiteValue - 13999)
//        {
//            System.out.println("Error evaluating material" + whiteMaterialValue + "<>" +(whiteValue - 13999));
//        }
//        if (blackMaterialValue != blackValue - 13999)
//        {
//            System.out.println("Error evaluating material");
//        }
    }

    public String toString()
    {
        String result="";
        for (int row = 0; row < 8; row++)
        {
            for (int col = 0; col<8; col++)
            {
                int piece = getChessPiece(row, col);
                if (isWhitePiece(piece))
                {
                result += PIECE_NAME[piece+1];
                }
                else
                {
                    result += PIECE_NAME[piece+1].toLowerCase();
                }
            }
            result +="\n";
        }
        return result;
    }

    private void evaluateMobility() {
        int whiteValue = numberOfWhiteMoves;
        if ((!whiteCastlingLeftImpossible) && (!whiteCastlingRightImpossible))
        {
            whiteValue+=90;
        }
        else if ((!whiteCastlingLeftImpossible) && (!whiteCastlingRightImpossible))
        {
            whiteValue+=70;
        }
        else if (whiteHasCastled)
        {
            whiteValue+=90;
        }
//        for (int i = 0; i < whiteValue; i++) {
//            int gain = whiteMoves[i] >> 24;
//            whiteValue += gain / 10;
//        }
        whiteMoveValue = whiteValue;
        int blackValue = numberOfBlackMoves;
        if ((!blackCastlingLeftImpossible) && (!blackCastlingRightImpossible))
        {
            blackValue+=90;
        }
        else if ((!blackCastlingLeftImpossible) && (!blackCastlingRightImpossible))
        {
            blackValue+=70;
        }
        else if (blackHasCastled)
        {
            blackValue+=90;
        }

//        for (int i = 0; i < blackValue; i++) {
//            int gain = blackMoves[i] >> 24;
//            blackValue += gain / 10;
//        }
        blackMoveValue = blackValue;
    }

    private void evaluateFieldPositionalValue() {
        int whiteValue = 0;
        int blackValue = 0;
        for (int row = 0; row < 8; row++)
            for (int col = 0; col < 8; col++) {
                int piece = getChessPiece(row,col);
                if (piece == W_PAWN) {
                    whiteValue += evaluatePawnPosition(row, col, piece, -1);
                } else if (piece == B_PAWN) {
                    blackValue += evaluatePawnPosition(row, col, piece, 1);
                }
                else if (piece== W_KING)
                {
                    if (7==row) {
                        blackValue+= evaluateKingsProtection(row, col, -1, W_PAWN);
                    }
                }
                else if (piece== B_KING)
                {
                    if (0==row) {
                        blackValue+= evaluateKingsProtection(row, col, 1, B_PAWN);
                    }
                } else if (isWhitePiece(piece)) {
                    whiteValue += POSITIONAL_VALUES[row][col];
                } else if (isBlackPiece(piece)) {
                    blackValue += POSITIONAL_VALUES[row][col];
                }
            }
        whiteFieldPositionValue = whiteValue;
        blackFieldPositionValue = blackValue;
    }

    private int evaluateKingsProtection(int row, int col, int direction, int protector) {
        int protection = 0;
        if (col != 3 && col != 4)     // discourage the king from staying in the center
        {
            if (col > 0) {
                if (getChessPiece(row + direction, col - 1) == protector) {
                    protection += 40;
                } else if (getChessPiece(row + 2 * direction, col - 1) == protector) {
                    protection += 20;
                }
            }
            if (getChessPiece(row + direction, col) == protector) {
                protection += 40;
            } else if (getChessPiece(row + 2 * direction, col) == protector) {
                protection += 20;
            }
            if (col < 7) {
                if (getChessPiece(row + direction, col + 1) == protector) {
                    protection += 40;
                } else if (getChessPiece(row + 2 * direction, col + 1) == protector) {
                    protection += 20;
                }
            }
        }
        return protection;
    }

    private int evaluatePawnPosition(int row, int col, int piece, int direction) {
        int value=0;
        int normalizedRow = direction<0?row:(7-row);
        value += WHITE_PAWN_POSITION_VALUES[normalizedRow][col];
        boolean advancedRow = (normalizedRow <= 4);
        // double pawn bonus
        if ((col > 0)&&advancedRow && getChessPiece(row, col - 1) ==piece)
        {
            value += DOUBLE_PAWN_BONUS[col];
        }
        // row chains
        if ((col>0) && advancedRow && getChessPiece(row + direction, col - 1) == piece) {
            value += DOUBLE_PAWN_BONUS[col];
        }
        if ((col<7) && advancedRow && getChessPiece(row+direction, col + 1) == piece) {
            value += DOUBLE_PAWN_BONUS[col];
        }
        // blocked pawns
        if (row>1 && row<6)
        {
            int pieceInFront = getChessPiece(row+direction, col);
            if (pieceInFront>0)
            {
                if (pieceInFront==piece)
                {
                    value -= WHITE_PAWN_POSITION_VALUES[normalizedRow][col];
                }
            }
        }
        if (row==1 || row==6)
        {
            if (col==3 || col==4)
            {
                int pieceInFront = getChessPiece(row+direction, col);
                if (pieceInFront>0)
                {
                    if (isWhitePiece(pieceInFront)==isWhitePiece(piece))
                    {  // prevent computer from blocking central pawn during opening phase
                        value -= WHITE_PAWN_POSITION_VALUES[normalizedRow][col];
                    }
                }

            }
        }
        return value;
    }

    private void evaluateThreats() {
        int whiteValue = 0;
        int blackValue = 0;
        if (isBlackKingThreatened) whiteValue+=1000;
        if (isWhiteKingThreatened) blackValue+=1000;
        for (int cell = 0; cell < 64; cell++) {
            int b = canBeReachedByTheseBlackPieces[cell];
            if (b > 0) {
                blackCoverageValue ++;
            }
            int w = canBeReachedByTheseWhitePieces[cell];
            if (w > 0) {
                whiteCoverageValue ++;
            }

            int piece = board[cell];
            if (piece>0)
            {
                if (activePlayerIsWhite && (w>0) && isBlackPiece(piece))
                {
                    if (b>0)
                    {
                        System.out.println("Todo: Abtauschevaluation");
                    }
                    else
                    {
                        blackExpectedLoss+=s_MATERIAL_VALUE[1+piece];
                    }
                }
                else if ((!activePlayerIsWhite) && (b>0) && isWhitePiece(piece))
                {
                    if (w>0)
                    {
                        System.out.println("Todo: Abtauschevaluation");
                    }
                    else
                    {
                        whiteExpectedLoss+=s_MATERIAL_VALUE[1+piece];
                    }
                }
            }
        }
    }

    public void findLegalMovesIgnoringCheck() {
        for (int fromRow = 0; fromRow < 8; fromRow++)
            for (int fromColumn = 0; fromColumn < 8; fromColumn++) {
                int piece = getChessPiece(fromRow,fromColumn);
                if (piece >= 2) {
                    boolean pieceIsWhite = isWhitePiece(piece);
                    switch ((piece + 2) >> 2) {
                        case 1: /* Pawn */
                            getLegalMovesForAPawn(fromRow, fromColumn, pieceIsWhite);
                            break;
                        case 2: /* Rook */
                            getLegalMovesForARook(fromRow, fromColumn, pieceIsWhite);
                            break;
                        case 3: /* knight (Springer) */
                            getLegalMovesForAKnight(fromRow, fromColumn, pieceIsWhite);
                            break;
                        case 4: /* bishop (Laeufer) */
                            getLegalMovesForABishop(fromRow, fromColumn, pieceIsWhite);
                            break;
                        case 5: /* Queen */
                            getLegalMovesForAQueen(fromRow, fromColumn, pieceIsWhite);
                            break;
                        case 6: /* King */
                            getLegalMovesForAKing(fromRow, fromColumn, pieceIsWhite);
                            break;
                    }

                }
            }
    }

    private void getLegalMovesForAPawn(int row, int column, boolean pieceIsWhite) {
        int nr;
        int nc;
        int direction = pieceIsWhite ? -1 : 1;

        // simple move forward
        nr = row + direction;
        nc = column;
        if (nr < 0 || nr >= 8) {
            return;
        }
        if (isInsideBoard(nr, nc) && isEmptyField(nr, nc)) {
            if (nr == 0 && pieceIsWhite) {
                addPossibleMove(row, column, nr, nc, pieceIsWhite, B_KNIGHT);
                addPossibleMove(row, column, nr, nc, pieceIsWhite, B_BISHOP);
                addPossibleMove(row, column, nr, nc, pieceIsWhite, B_ROOK);
                addPossibleMove(row, column, nr, nc, pieceIsWhite, W_QUEEN);
            } else if (nr == 7 && (!pieceIsWhite)) {
                addPossibleMove(row, column, nr, nc, pieceIsWhite, W_KNIGHT);
                addPossibleMove(row, column, nr, nc, pieceIsWhite, W_BISHOP);
                addPossibleMove(row, column, nr, nc, pieceIsWhite, W_ROOK);
                addPossibleMove(row, column, nr, nc, pieceIsWhite, B_QUEEN);
            } else {
                addPossibleMove(row, column, nr, nc, pieceIsWhite);
            }
            // double move forward
            if ((row == 6 && pieceIsWhite) || (row == 1 && (!pieceIsWhite))) {
                // first move
                nr = row + 2 * direction;
                nc = column;
                if (isEmptyField(nr, nc)) {
                    addPossibleMove(row, column, nr, nc, pieceIsWhite);
                }
            }
        }

        // capture piece at left hand side
        nr = row + direction;
        nc = column - 1;
        if (isOpponentsPiece(nr, nc, pieceIsWhite)) {
            addPossibleMove(row, column, nr, nc, pieceIsWhite);
        } else if (canBeCapturedEnPassant(nr, nc)) {
            addPossibleMove(row, column, nr, nc, pieceIsWhite);
        }

        // capture piece at right hand side
        nr = row + direction;
        nc = column + 1;
        if (isOpponentsPiece(nr, nc, pieceIsWhite)) {
            addPossibleMove(row, column, nr, nc, pieceIsWhite);
        } else if (canBeCapturedEnPassant(nr, nc)) {
            addPossibleMove(row, column, nr, nc, pieceIsWhite);
        }
    }

    private boolean canBeCapturedEnPassant(int nr, int nc) {
        return isInsideBoard(nr, nc) && (getChessPiece(nr,nc) == -1);
    }

    private void getLegalMovesForAQueen(int row, int column, boolean pieceIsWhite) {
        getLegalMovesForARook(row, column, pieceIsWhite);
        getLegalMovesForABishop(row, column, pieceIsWhite);
    }

    private void getLegalMovesForARook(int row, int column, boolean pieceIsWhite) {
        int nr;
        int nc;

        nr = row;
        nc = column;
        while (true) {
            nr++;
            if (isEmptyOrCanBeCaptured(nr, nc, pieceIsWhite)) {
                addPossibleMove(row, column, nr, nc, pieceIsWhite);
                if (!isEmptyField(nr, nc)) {
                    break;
                }
            } else {
                break;
            }
        }
        nr = row;
        nc = column;
        while (true) {
            nr--;
            if (isEmptyOrCanBeCaptured(nr, nc, pieceIsWhite)) {
                addPossibleMove(row, column, nr, nc, pieceIsWhite);
                if (!isEmptyField(nr, nc)) {
                    break;
                }
            } else {
                break;
            }
        }
        nr = row;
        nc = column;
        while (true) {
            nc++;
            if (isEmptyOrCanBeCaptured(nr, nc, pieceIsWhite)) {
                addPossibleMove(row, column, nr, nc, pieceIsWhite);
                if (!isEmptyField(nr, nc)) {
                    break;
                }
            } else {
                break;
            }
        }
        nr = row;
        nc = column;
        while (true) {
            nc--;
            if (isEmptyOrCanBeCaptured(nr, nc, pieceIsWhite)) {
                addPossibleMove(row, column, nr, nc, pieceIsWhite);
                if (!isEmptyField(nr, nc)) {
                    break;
                }
            } else {
                break;
            }
        }
    }

    private void getLegalMovesForABishop(int row, int column, boolean pieceIsWhite) {
        int nr;
        int nc;

        nr = row;
        nc = column;
        while (true) {
            nr++;
            nc++;
            if (isEmptyOrCanBeCaptured(nr, nc, pieceIsWhite)) {
                addPossibleMove(row, column, nr, nc, pieceIsWhite);
                if (!isEmptyField(nr, nc)) {
                    break;
                }
            } else {
                break;
            }
        }
        nr = row;
        nc = column;
        while (true) {
            nr--;
            nc++;
            if (isEmptyOrCanBeCaptured(nr, nc, pieceIsWhite)) {
                addPossibleMove(row, column, nr, nc, pieceIsWhite);
                if (!isEmptyField(nr, nc)) {
                    break;
                }
            } else {
                break;
            }
        }
        nr = row;
        nc = column;
        while (true) {
            nr++;
            nc--;
            if (isEmptyOrCanBeCaptured(nr, nc, pieceIsWhite)) {
                addPossibleMove(row, column, nr, nc, pieceIsWhite);
                if (!isEmptyField(nr, nc)) {
                    break;
                }
            } else {
                break;
            }
        }
        nr = row;
        nc = column;
        while (true) {
            nr--;
            nc--;
            if (isEmptyOrCanBeCaptured(nr, nc, pieceIsWhite)) {
                addPossibleMove(row, column, nr, nc, pieceIsWhite);
                if (!isEmptyField(nr, nc)) {
                    break;
                }
            } else {
                break;
            }
        }
    }

    private void getLegalMovesForAKing(int row, int column, boolean pieceIsWhite) {
        int nr;
        int nc;

        nr = row;
        nc = column;
        nr++;
        if (isEmptyOrCanBeCaptured(nr, nc, pieceIsWhite)) {
            //if (!canBeReachedByOpponentsPiece(row, column, pieceIsWhite))
            addPossibleMove(row, column, nr, nc, pieceIsWhite);
        }
        nr = row;
        nc = column;
        nr--;
        if (isEmptyOrCanBeCaptured(nr, nc, pieceIsWhite)) {
            //if (!canBeReachedByOpponentsPiece(row, column, pieceIsWhite))
            addPossibleMove(row, column, nr, nc, pieceIsWhite);
        }
        nr = row;
        nc = column;
        nc++;
        if (isEmptyOrCanBeCaptured(nr, nc, pieceIsWhite)) {
            //if (!canBeReachedByOpponentsPiece(row, column, pieceIsWhite))
            addPossibleMove(row, column, nr, nc, pieceIsWhite);
        }
        nr = row;
        nc = column;
        nc--;
        if (isEmptyOrCanBeCaptured(nr, nc, pieceIsWhite)) {
            //if (!canBeReachedByOpponentsPiece(row, column, pieceIsWhite))
            addPossibleMove(row, column, nr, nc, pieceIsWhite);
        }
        nr = row;
        nc = column;
        nr++;
        nc++;
        if (isEmptyOrCanBeCaptured(nr, nc, pieceIsWhite)) {
            //if (!canBeReachedByOpponentsPiece(row, column, pieceIsWhite))
            addPossibleMove(row, column, nr, nc, pieceIsWhite);
        }
        nr = row;
        nc = column;
        nr--;
        nc++;
        if (isEmptyOrCanBeCaptured(nr, nc, pieceIsWhite)) {
            //if (!canBeReachedByOpponentsPiece(row, column, pieceIsWhite))
            addPossibleMove(row, column, nr, nc, pieceIsWhite);
        }
        nr = row;
        nc = column;
        nr++;
        nc--;
        if (isEmptyOrCanBeCaptured(nr, nc, pieceIsWhite)) {
            //if (!canBeReachedByOpponentsPiece(row, column, pieceIsWhite))
            addPossibleMove(row, column, nr, nc, pieceIsWhite);
        }
        nr = row;
        nc = column;
        nr--;
        nc--;
        if (isEmptyOrCanBeCaptured(nr, nc, pieceIsWhite)) {
            //if (!canBeReachedByOpponentsPiece(row, column, pieceIsWhite))
            addPossibleMove(row, column, nr, nc, pieceIsWhite);
        }
        // castling to the right hand side
        nr = row;
        nc = column;
        if (((nr == 0 && (!pieceIsWhite))) || ((nr == 7) && pieceIsWhite))
            if (nc == 4)
            {
                if (isEmptyField(nr, 5) && isEmptyField(nr, 6)) {
                    int piece = getChessPiece(nr, 7);
                    if (piece == B_ROOK && (!pieceIsWhite) && (!blackCastlingRightImpossible)) {
                        if (canBeReachedByWhitePiece(nr,4) == false && canBeReachedByWhitePiece(nr,5) == false && canBeReachedByWhitePiece(nr,6) == false) {
                            addPossibleMove(row, column, row, column + 2, pieceIsWhite);
                        }
                    }
                    if (piece == W_ROOK && (pieceIsWhite) && (!whiteCastlingRightImpossible)) {
                        if (canBeReachedByBlackPiece(nr,4) == false && canBeReachedByBlackPiece(nr,5) == false && canBeReachedByBlackPiece(nr,6) == false) {
                            addPossibleMove(row, column, row, column + 2, pieceIsWhite);
                        }
                    }
                }
            }
        // castling to the left hand side
        nr = row;
        nc = column;
        if (((nr == 0 && (!pieceIsWhite))) || ((nr == 7) && pieceIsWhite))
            if (nc == 4) {
                if (isEmptyField(nr, 3) && isEmptyField(nr, 2) && isEmptyField(nr, 1)) {
                    int piece = getChessPiece(nr, 0);
                    if (piece == B_ROOK && (!pieceIsWhite) && (!blackCastlingLeftImpossible)) {
                        if (canBeReachedByWhitePiece(nr,4) == false && canBeReachedByWhitePiece(nr,3) == false && canBeReachedByWhitePiece(nr,2)==false) {
                            addPossibleMove(row, column, row, column - 2, pieceIsWhite);
                        }
                    }
                    if (piece == W_ROOK && (pieceIsWhite) && (!whiteCastlingLeftImpossible)) {
                        if (canBeReachedByBlackPiece(nr,4) == false && canBeReachedByBlackPiece(nr,3) == false && canBeReachedByBlackPiece(nr,2)==false) {
                            addPossibleMove(row, column, row, column - 2, pieceIsWhite);
                        }
                    }
                }
            }


    }

    private void getLegalMovesForAKnight(int row, int column, boolean pieceIsWhite) {
        int nr;
        int nc;
        int moveRow[] = {1, 2, 2, 1, -1, -2, -2, -1};
        int moveCol[] = {-2, -1, 1, 2, -2, -1, 1, 2};

        for (int i = 0; i < moveCol.length; i++) {
            nr = row + moveRow[i];
            nc = column + moveCol[i];
            if (isEmptyOrCanBeCaptured(nr, nc, pieceIsWhite)) {
                addPossibleMove(row, column, nr, nc, pieceIsWhite);
            }
        }
    }

    public void addPossibleMove(int fromRow, int fromColumn, int toRow, int toColumn, boolean pieceIsWhite) {
        int promotedPiece = 0;
        addPossibleMove(fromRow, fromColumn, toRow, toColumn, pieceIsWhite, promotedPiece);
    }

    private void addPossibleMove(int fromRow, int fromColumn, int toRow, int toColumn, boolean pieceIsWhite, int promotedPiece) {
        int capturedPiece = getChessPiece(toRow,toColumn);
        if (capturedPiece < 0) {
            capturedPiece = pieceIsWhite ? B_PAWN : W_PAWN;
        } else if (capturedPiece == W_KING) {
            isWhiteKingThreatened = true;
        } else if (capturedPiece == B_KING) {
            isBlackKingThreatened = true;
        }
        int m = move(fromRow, fromColumn, toRow, toColumn, capturedPiece, promotedPiece);
        if (pieceIsWhite) {
            whiteMoves[numberOfWhiteMoves++] = m;
        } else {
            blackMoves[numberOfBlackMoves++] = m;
        }
        addFieldToPieceReach(toRow, toColumn, getChessPiece(fromRow, fromColumn), pieceIsWhite);
    }

    private void addFieldToPieceReach(int row, int column, int piece, boolean pieceIsWhite)
    {
        if (pieceIsWhite) {
            canBeReachedByTheseWhitePieces[(row<<3)+column]= (canBeReachedByTheseWhitePieces[(row<<3)+column])<<8+piece;
        } else {
            canBeReachedByTheseBlackPieces[(row<<3)+column]=(canBeReachedByTheseBlackPieces[(row<<3)+column])<<8+piece;
        }
    }
    private boolean canBeReachedByWhitePiece(int row, int column)
    {
        return whitePieceReach(row, column)>0;
    }
    private boolean canBeReachedByBlackPiece(int row, int column)
    {
        return blackPieceReach(row, column)>0;
    }
    private int whitePieceReach(int row, int column)
    {
        return canBeReachedByTheseWhitePieces[(row<<3)+column];
    }
    private boolean canBeReachedByOpponentsPiece(int row, int column, boolean pieceIsWhite)
    {
        if (pieceIsWhite)
            return canBeReachedByBlackPiece(row, column);
        else
            return canBeReachedByWhitePiece(row, column);
    }

    private int blackPieceReach(int row, int column)
    {
        return canBeReachedByTheseBlackPieces[(row<<3)+column];
    }


    public int move(int fromRow, int fromColumn, int toRow, int toColumn, int capturedPiece, int promotedPiece) {
        int capturedValue = s_MATERIAL_VALUE[capturedPiece];
        int compact = (capturedValue << 24) + (promotedPiece << 16) + (fromRow << 12) + (fromColumn << 8) + (toRow << 4) + toColumn;
        return compact;
    }

    public boolean isMovePossible(int fromRow, int fromColumn, int toRow, int toColumn) {
        int compact = move(fromRow, fromColumn, toRow, toColumn, 0, 0);
        int[] legalMoves;
        if (activePlayerIsWhite) {
            legalMoves = whiteMoves;
        } else {
            legalMoves = blackMoves;
        }
        for (int c : legalMoves) {
            if ((c & 0xFFFF) == compact) {
                LinearChessboard b = moveChessPiece(new Move(0, fromRow, fromColumn, toRow, toColumn, 0, false, false, 0));
                try {
                    if (b.activePlayerIsWhite) {
                        b.findBestWhiteMoves(0, 1, false);
                    } else {
                        b.findBestBlackMoves(0, 1, false);
                    }
                    return true;
                } catch (WhiteIsCheckMateException p_impossibleMove) {
                    return !activePlayerIsWhite;
                } catch (BlackIsCheckMateException p_impossibleMove) {
                    return activePlayerIsWhite;
                } catch (EndOfGameException e) {
                    return true;
                }
            }
        }
        return false;
    }
}
