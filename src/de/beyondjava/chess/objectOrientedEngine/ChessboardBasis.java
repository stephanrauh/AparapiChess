package de.beyondjava.chess.objectOrientedEngine;

import de.beyondjava.chess.common.ChessConstants;
import de.beyondjava.chess.common.Move;
import de.beyondjava.chess.common.Piece;

import java.util.ArrayList;
import java.util.List;


/**
 * Represents the chess board and provide a couple of methods on possible moves.
 * Date: 02.02.13
 * Time: 19:17
 */
public class ChessboardBasis implements ChessConstants {
    public static int evaluatedPositions=0;
    public final int[][] board;
    public final boolean activePlayerIsWhite;
    public byte[][] canBeReachedByWhitePiece = new byte[8][8];
    public byte[][] canBeReachedByBlackPiece = new byte[8][8];
    public int whiteMaterialValue;      // effectively final
    public int blackMaterialValue;      // effectively final
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
    public int[] whiteMoves = new int[184]; // effectively final
    public int numberOfWhiteMoves = 0;      // effectively final
    public int[] blackMoves = new int[184]; // effectively final
    public int numberOfBlackMoves = 0;      // effectively final
    public boolean stalemate = false;
    public boolean checkmate=false;

    private ChessboardBasis(boolean activePlayerIsWhite, int[][] board) {
        this.activePlayerIsWhite = activePlayerIsWhite;
        this.board = board;
        evaluateBoard();
    }

    public ChessboardBasis() {
        this(true, ChessConstants.initialBoard);
    }

    public ChessboardBasis(boolean activePlayerIsWhite, ChessboardBasis board) {
        this(activePlayerIsWhite, board.board);
    }

    public ChessboardBasis(boolean activePlayerIsWhite, Piece... pieces) {
        this(activePlayerIsWhite, getBoardFromPieces(pieces));
    }

    public ChessboardBasis(ChessboardBasis oldBoard, int fromRow, int fromColumn, int toRow, int toColumn) {
        this(!oldBoard.activePlayerIsWhite, getNewBoard(oldBoard, fromRow, fromColumn, toRow, toColumn));
    }

    private static int[][] getNewBoard(ChessboardBasis oldBoard, int fromRow, int fromColumn, int toRow, int toColumn) {
        int[][] newBoard = new int[8][8];
        for (int row = 0; row < 8; row++) {
            newBoard[row] = new int[8];
            for (int y = 0; y < 8; y++) {
                int piece = oldBoard.board[row][y];
                if (piece < 0) piece = 0; // forget that en passant once was possible
                newBoard[row][y] = piece;
            }
        }

        if (oldBoard.board[toRow][toColumn] == -1) {
            // capture en passant
            newBoard[fromRow][toColumn] = 0;
        }
        int piece = newBoard[fromRow][fromColumn];
        if (piece == w_bauer)
            if (fromRow - toRow == 2)
                newBoard[fromRow - 1][toColumn] = -1; // allow for en passant
        if (piece == s_bauer)
            if (fromRow - toRow == -2)
                newBoard[fromRow + 1][toColumn] = -1; // allow for en passant
        newBoard[toRow][toColumn] = piece;
        newBoard[fromRow][fromColumn] = 0;
        return newBoard;
    }

    private static int[][] getBoardFromPieces(Piece[] pieces) {
        int[][] board = new int[8][8];
        for (Piece p : pieces) {
            board[p.row][p.column] = p.piece;
        }
        return board;
    }

    private void evaluateBoard() {
        evaluateMaterialValue();
        evaluateFieldPositionalValue();
        findLegalMovesIgnoringCheck();
        evaluateThreats();
//        whiteTotalValue = whiteMaterialValue*10 +  whiteFieldPositionValue + whiteMoveValue + whiteCoverageValue;
//        blackTotalValue = blackMaterialValue*10 +  blackFieldPositionValue + blackMoveValue + blackCoverageValue;
        whiteTotalValue = whiteMaterialValue*10 + (whitePotentialMaterialValue>>2) + whiteFieldPositionValue + whiteMoveValue + whiteCoverageValue;
        blackTotalValue = blackMaterialValue*10 + (blackPotentialMaterialValue>>2) + blackFieldPositionValue + blackMoveValue + blackCoverageValue;
        evaluatedPositions++;
    }

    public int getChessPiece(int row, int column) {
        return board[row][column];
    }

    public Chessboard moveChessPiece(int fromRow, int fromColumn, int toRow, int toColumn) {
        return new Chessboard(this, fromRow, fromColumn, toRow, toColumn);
    }

    public Chessboard moveChessPiece(Move move) {
        return new Chessboard(this, move.fromRow, move.fromColumn, move.toRow, move.toColumn);
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
        if (board[row][column] <= 0) return true;
        return false;
    }

    protected boolean isOpponentsPiece(int row, int column, boolean p_activePlayerIsWhite) {
        if (!isInsideBoard(row, column)) return false;
        if (board[row][column] <= 0) return false;
        if (p_activePlayerIsWhite) {
            return (isBlackPiece(board[row][column]));
        } else {
            return (isWhitePiece(board[row][column]));
        }
    }

    protected boolean isEmptyOrCanBeCaptured(int row, int column, boolean p_activePlayerIsWhite) {
        if (!isInsideBoard(row, column)) return false;
        int piece = board[row][column];
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
        for (int fromRow = 0; fromRow < 8; fromRow++)
            for (int toColumn = 0; toColumn < 8; toColumn++) {
                int piece = board[fromRow][toColumn];
                if (isWhitePiece(piece)) {
                    whiteValue += s_MATERIAL_VALUE[piece];
                }
                if (isBlackPiece(piece)) {
                    blackValue += s_MATERIAL_VALUE[piece];
                }
            }
        whiteMaterialValue = whiteValue - 13999; // normalization (13999=initial sum)
        blackMaterialValue = blackValue - 13999; // normalization (13999=initial sum)
    }

    public void evaluateValueOfLegalMoves() {
        int whiteValue = numberOfWhiteMoves;
        for (int i = 0; i < whiteValue; i++) {
            int gain = whiteMoves[i] >> 24;
            whiteValue += gain / 10;
        }
        whiteMoveValue = whiteValue;
        int blackValue = numberOfBlackMoves;
        for (int i = 0; i < blackValue; i++) {
            int gain = blackMoves[i] >> 24;
            blackValue += gain / 10;
        }
        blackMoveValue = blackValue;
    }

    private void evaluateFieldPositionalValue() {
        int whiteValue = 0;
        int blackValue = 0;
        for (int row = 0; row < 8; row++)
            for (int col = 0; col < 8; col++) {
                int piece = board[row][col];
                if (piece == w_bauer) {
                    whiteValue += whitePawnPositions[row][col];
                } else if (piece == s_bauer) {
                    blackValue += whitePawnPositions[7 - row][col];
                } else if (isWhitePiece(piece)) {
                    whiteValue += positions[row][col];
                } else if (isBlackPiece(piece)) {
                    blackValue += positions[row][col];
                }
            }
        whiteFieldPositionValue = whiteValue;
        blackFieldPositionValue = blackValue;
    }

    private void evaluateThreats() {
        int whiteValue = 0;
        int blackValue = 0;
        int whiteAdvantage = activePlayerIsWhite ? 1 : -1;
        for (int row = 0; row < 8; row++)
            for (int col = 0; col < 8; col++) {
                whiteCoverageValue += canBeReachedByWhitePiece[row][col];
                blackCoverageValue += canBeReachedByBlackPiece[row][col];
                /*
                int piece = board[row][col];
                if (piece != 0) {
                    if (piece < 0) {
                        if (activePlayerIsWhite) piece = s_bauer;
                        else piece = w_bauer;
                    }
                    if (isWhitePiece(piece)) {
                        if (canBeReachedByBlackPiece[row][col] > 0) {
                            if (canBeReachedByBlackPiece[row][col] > canBeReachedByWhitePiece[row][col] + whiteAdvantage) {
                                // piece is very likely to be captured
                                blackPotentialMaterialValue += (s_MATERIAL_VALUE[piece]) >> 1;
                            } else if (canBeReachedByBlackPiece[row][col] >= canBeReachedByWhitePiece[row][col] + whiteAdvantage) {
                                // it's likely to became an equal exchange
                                blackPotentialMaterialValue += (s_MATERIAL_VALUE[piece]) >> 2;
                            } else {
                                // piece might get lost if something goes wrong
                                blackPotentialMaterialValue += (s_MATERIAL_VALUE[piece]) >> 3;
                            }
                        }
                    } else {
                        if (canBeReachedByWhitePiece[row][col] > 0) {
                            if (canBeReachedByBlackPiece[row][col] < canBeReachedByWhitePiece[row][col] - whiteAdvantage) {
                                // piece is very likely to be captured
                                whitePotentialMaterialValue += (s_MATERIAL_VALUE[piece]) >> 1;
                            } else if (canBeReachedByBlackPiece[row][col] <= canBeReachedByWhitePiece[row][col] - whiteAdvantage) {
                                // it's likely to became an equal exchange
                                whitePotentialMaterialValue += (s_MATERIAL_VALUE[piece]) >> 2;
                            } else {
                                // piece might get lost if something goes wrong
                                whitePotentialMaterialValue += (s_MATERIAL_VALUE[piece]) >> 3;
                            }
                        }
                    }
                }
                    */
            }
    }

    public void findLegalMovesIgnoringCheck() {
        for (int fromRow = 0; fromRow < 8; fromRow++)
            for (int fromColumn = 0; fromColumn < 8; fromColumn++) {
                int piece = board[fromRow][fromColumn];
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
                addPossibleMove(row, column, nr, nc, pieceIsWhite, s_springer);
                addPossibleMove(row, column, nr, nc, pieceIsWhite, s_laeufer);
                addPossibleMove(row, column, nr, nc, pieceIsWhite, s_turm);
                addPossibleMove(row, column, nr, nc, pieceIsWhite, s_dame);
            } else if (nr == 7 && (!pieceIsWhite)) {
                addPossibleMove(row, column, nr, nc, pieceIsWhite, w_springer);
                addPossibleMove(row, column, nr, nc, pieceIsWhite, w_laeufer);
                addPossibleMove(row, column, nr, nc, pieceIsWhite, w_turm);
                addPossibleMove(row, column, nr, nc, pieceIsWhite, w_dame);
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
        return isInsideBoard(nr, nc) && (board[nr][nc] == -1);
    }

    private void getLegalMovesForAQueen(int row, int column, boolean pieceIsWhite) {
        getLegalMovesForARook(row, column, pieceIsWhite);
        getLegalMovesForABishop(row, column, pieceIsWhite);
    }

    private void getLegalMovesForARook(int row, int column, boolean pieceIsWhite) {
        List result = new ArrayList<Position>();
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
            addPossibleMove(row, column, nr, nc, pieceIsWhite);
        }
        nr = row;
        nc = column;
        nr--;
        if (isEmptyOrCanBeCaptured(nr, nc, pieceIsWhite)) {
            addPossibleMove(row, column, nr, nc, pieceIsWhite);
        }
        nr = row;
        nc = column;
        nc++;
        if (isEmptyOrCanBeCaptured(nr, nc, pieceIsWhite)) {
            addPossibleMove(row, column, nr, nc, pieceIsWhite);
        }
        nr = row;
        nc = column;
        nc--;
        if (isEmptyOrCanBeCaptured(nr, nc, pieceIsWhite)) {
            addPossibleMove(row, column, nr, nc, pieceIsWhite);
        }
        nr = row;
        nc = column;
        nr++;
        nc++;
        if (isEmptyOrCanBeCaptured(nr, nc, pieceIsWhite)) {
            addPossibleMove(row, column, nr, nc, pieceIsWhite);
        }
        nr = row;
        nc = column;
        nr--;
        nc++;
        if (isEmptyOrCanBeCaptured(nr, nc, pieceIsWhite)) {
            addPossibleMove(row, column, nr, nc, pieceIsWhite);
        }
        nr = row;
        nc = column;
        nr++;
        nc--;
        if (isEmptyOrCanBeCaptured(nr, nc, pieceIsWhite)) {
            addPossibleMove(row, column, nr, nc, pieceIsWhite);
        }
        nr = row;
        nc = column;
        nr--;
        nc--;
        if (isEmptyOrCanBeCaptured(nr, nc, pieceIsWhite)) {
            addPossibleMove(row, column, nr, nc, pieceIsWhite);
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
        int capturedPiece = board[toRow][toColumn];
        if (capturedPiece < 0) {
            capturedPiece = pieceIsWhite ? s_bauer : w_bauer;
        } else if (capturedPiece == w_koenig) {
            isWhiteKingThreatened = true;
        } else if (capturedPiece == s_koenig) {
            isBlackKingThreatened = true;
        }
        int m = move(fromRow, fromColumn, toRow, toColumn, capturedPiece, promotedPiece);
        if (pieceIsWhite) {
            whiteMoves[numberOfWhiteMoves++] = m;
            canBeReachedByWhitePiece[toRow][toColumn]++;
        } else {
            blackMoves[numberOfBlackMoves++] = m;
            canBeReachedByBlackPiece[toRow][toColumn]++;
        }
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
                Chessboard b = moveChessPiece(new Move(0, fromRow, fromColumn, toRow, toColumn, 0, false, false, 0));
                try {
                    if (b.activePlayerIsWhite) {
                        b.findBestWhiteMoves(0, 1);
                    } else {
                        b.findBestBlackMoves(0, 1);
                    }
                    return true;
                } catch (CheckMateException p_impossibleMove) {
                    return false;
                } catch (EndOfGameException e) {
                    return true;
                }
            }
        }
        return false;
    }

}
