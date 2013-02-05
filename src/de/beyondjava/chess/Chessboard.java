package de.beyondjava.chess;

import java.util.ArrayList;
import java.util.List;

/**
 * Represents the chess board and provide a couple of methods on possible moves.
 * User: SoyYo
 * Date: 02.02.13
 * Time: 19:17
 * To change this template use File | Settings | File Templates.
 */
public class Chessboard implements ChessConstants {
    int[][] board;
    boolean activePlayerIsWhite;

    public Chessboard() {
        startGame();
    }

    public void startGame() {
        activePlayerIsWhite = true;
        board = ChessConstants.initialBoard;
    }

    public int getChessPiece(int row, int column) {
        return board[row][column];
    }

    public void moveChessPiece(int fromRow, int fromColumn, int toRow, int toColumn) {
        if (board[toRow][toColumn]==-1)
        {
            // en passant
            if (activePlayerIsWhite)
            {
                board[fromRow][toColumn]=0;
            }
            else
            {
                board[fromRow][toColumn]=0;
            }
        }
        forgetLastMovesEnPassant();
        int piece = board[fromRow][fromColumn];
        if (piece == w_bauer)
            if (fromRow - toRow == 2)
                board[fromRow - 1][toColumn] = -1; // en passant
        if (piece == s_bauer)
            if (fromRow - toRow == -2)
                board[fromRow + 1][toColumn] = -1; // en passant
        board[toRow][toColumn] = piece;
        board[fromRow][fromColumn] = 0;
         activePlayerIsWhite = !activePlayerIsWhite;
    }

    private void forgetLastMovesEnPassant() {
        for (int i = 0; i < 8; i++) {
            if (board[2][i] == -1) board[2][i] = 0;
            if (board[5][i] == -1) board[5][i] = 0;
        }
    }

    public boolean isMovePossible(int fromRow, int fromColumn, int toRow, int toColumn) {
        int piece = board[fromRow][fromColumn];
        if (piece < s_bauer) {
            return false;
        }
        if (!isActivePlayersPiece(piece)) {
            return false;
        }
        int targetPiece = board[toRow][toColumn];
        if (targetPiece >= s_bauer && isActivePlayersPiece(targetPiece)) {
            return false;
        }
        List<Position> legalTargets = getLegalMovesForAPiece(fromRow, fromColumn, activePlayerIsWhite);
        for (Position p : legalTargets) {
            if (p.equals(toRow, toColumn)) {
                return true;
            }
        }
        return false;
    }

    public boolean isWhitePiece(int piece) {
        if (piece < 2) {
            return false;
        }
        return (((piece / 2) % 2) == 0);
    }

    public boolean isBlackPiece(int piece) {
        if (piece < 2) {
            return false;
        }
        return (((piece / 2) % 2) == 1);
    }

    public boolean isActivePlayersPiece(int piece) {
        return activePlayerIsWhite == isWhitePiece(piece);
    }

    private boolean isInsideBoard(int row, int column) {
        if (row < 0 || row > 7) return false;
        if (column < 0 || column > 7) return false;
        return true;
    }

    private boolean isEmptyField(int row, int column) {
        if (!isInsideBoard(row, column)) return false;
        if (board[row][column] <= 0) return true;
        return false;
    }

    private boolean isOpponentsPiece(int row, int column) {
        if (!isInsideBoard(row, column)) return false;
        if (board[row][column] <= 0) return false;
        if (activePlayerIsWhite) {
            return (isBlackPiece(board[row][column]));
        } else {
            return (isWhitePiece(board[row][column]));
        }
    }

    private boolean isEmptyOrCanBeCaptured(int row, int column) {
        if (!isInsideBoard(row, column)) return false;
        int piece = board[row][column];
        if (piece <= 0) return true;
        if (((piece + 2) >> 2) == 6) // King
        {
            return false;
        }
        if (activePlayerIsWhite) {
            return (isBlackPiece(piece));
        } else {
            return (isWhitePiece(piece));
        }
    }

    public List<Position> getLegalMovesForAPiece(int row, int column, boolean activePlayerIsWhite) {
        int piece = board[row][column];
        if (isActivePlayersPiece(piece)) {
            switch ((piece + 2) >> 2) {
                case 1: /* Pawn */
                    return getLegalMovesForAPawn(row, column, activePlayerIsWhite);
                case 2: /* Rook */
                    return getLegalMovesForARook(row, column, activePlayerIsWhite);
                case 3: /* knight (Springer) */
                    return getLegalMovesForAKnight(row, column, activePlayerIsWhite);
                case 4: /* bishop (Laeufer) */
                    return getLegalMovesForABishop(row, column, activePlayerIsWhite);
                case 5: /* Queen */
                    return getLegalMovesForAQueen(row, column, activePlayerIsWhite);
                case 6: /* King */
                    return getLegalMovesForAKing(row, column, activePlayerIsWhite);
            }
        }
        return new ArrayList<Position>();
    }

    private List<Position> getLegalMovesForAPawn(int row, int column, boolean activePlayerIsWhite) {
        List result = new ArrayList<Position>();
        int nr;
        int nc;
        int direction = activePlayerIsWhite ? -1 : 1;
        nr = row + direction;
        nc = column;
        if (board[nr][nc] == 0) {
            result.add(new Position(nr, nc));
            if ((row == 6 && activePlayerIsWhite) || (row == 1 && (!activePlayerIsWhite))) {
                // first move
                nr = row + 2 * direction;
                nc = column;
                if (board[nr][nc] == 0) {
                    result.add(new Position(nr, nc));
                }
            }
        }
        nr = row + direction;
        nc = column - 1;
        if (isOpponentsPiece(nr, nc)) {
            result.add(new Position(nr, nc));
        }
        nr = row + direction;
        nc = column - 1;
        if (isOpponentsPiece(nr, nc)) {
            result.add(new Position(nr, nc));
        } else if (canBeCapturedEnPassant(nr, nc)) {
            result.add(new Position(nr, nc));
        }
        nr = row + direction;
        nc = column + 1;
        if (isOpponentsPiece(nr, nc)) {
            result.add(new Position(nr, nc));
        } else if (canBeCapturedEnPassant(nr, nc)) {
            result.add(new Position(nr, nc));
        }
        return result;
    }

    private boolean canBeCapturedEnPassant(int nr, int nc) {
        return isInsideBoard(nr, nc) && (board[nr][nc] == -1);
    }

    private List<Position> getLegalMovesForAQueen(int row, int column, boolean activePlayerIsWhite) {
        List result = getLegalMovesForARook(row, column, activePlayerIsWhite);
        result.addAll(getLegalMovesForABishop(row, column, activePlayerIsWhite));
        return result;
    }

    private List<Position> getLegalMovesForARook(int row, int column, boolean activePlayerIsWhite) {
        List result = new ArrayList<Position>();
        int nr;
        int nc;

        nr = row;
        nc = column;
        while (true) {
            nr++;
            if (isEmptyOrCanBeCaptured(nr, nc)) {
                result.add(new Position(nr, nc));
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
            if (isEmptyOrCanBeCaptured(nr, nc)) {
                result.add(new Position(nr, nc));
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
            if (isEmptyOrCanBeCaptured(nr, nc)) {
                result.add(new Position(nr, nc));
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
            if (isEmptyOrCanBeCaptured(nr, nc)) {
                result.add(new Position(nr, nc));
                if (!isEmptyField(nr, nc)) {
                    break;
                }
            } else {
                break;
            }
        }
        return result;
    }

    private List<Position> getLegalMovesForABishop(int row, int column, boolean activePlayerIsWhite) {
        List result = new ArrayList<Position>();
        int nr;
        int nc;

        nr = row;
        nc = column;
        while (true) {
            nr++;
            nc++;
            if (isEmptyOrCanBeCaptured(nr, nc)) {
                result.add(new Position(nr, nc));
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
            if (isEmptyOrCanBeCaptured(nr, nc)) {
                result.add(new Position(nr, nc));
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
            if (isEmptyOrCanBeCaptured(nr, nc)) {
                result.add(new Position(nr, nc));
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
            if (isEmptyOrCanBeCaptured(nr, nc)) {
                result.add(new Position(nr, nc));
                if (!isEmptyField(nr, nc)) {
                    break;
                }
            } else {
                break;
            }
        }
        return result;
    }

    private List<Position> getLegalMovesForAKing(int row, int column, boolean activePlayerIsWhite) {
        List result = new ArrayList<Position>();
        int nr;
        int nc;

        nr = row;
        nc = column;
        nr++;
        if (isEmptyOrCanBeCaptured(nr, nc)) {
            result.add(new Position(nr, nc));
        }
        nr = row;
        nc = column;
        nr--;
        if (isEmptyOrCanBeCaptured(nr, nc)) {
            result.add(new Position(nr, nc));
        }
        nr = row;
        nc = column;
        nc++;
        if (isEmptyOrCanBeCaptured(nr, nc)) {
            result.add(new Position(nr, nc));
        }
        nr = row;
        nc = column;
        nc--;
        if (isEmptyOrCanBeCaptured(nr, nc)) {
            result.add(new Position(nr, nc));
        }
        nr = row;
        nc = column;
        nr++;
        nc++;
        if (isEmptyOrCanBeCaptured(nr, nc)) {
            result.add(new Position(nr, nc));
        }
        nr = row;
        nc = column;
        nr--;
        nc++;
        if (isEmptyOrCanBeCaptured(nr, nc)) {
            result.add(new Position(nr, nc));
        }
        nr = row;
        nc = column;
        nr++;
        nc--;
        if (isEmptyOrCanBeCaptured(nr, nc)) {
            result.add(new Position(nr, nc));
        }
        nr = row;
        nc = column;
        nr--;
        nc--;
        if (isEmptyOrCanBeCaptured(nr, nc)) {
            result.add(new Position(nr, nc));
        }
        return result;

    }

    private List<Position> getLegalMovesForAKnight(int row, int column, boolean activePlayerIsWhite) {
        List result = new ArrayList<Position>();
        int nr;
        int nc;
        int moveRow[] = {1, 2, 2, 1, -1, -2, -2, -1};
        int moveCol[] = {-2, -1, 1, 2, -2, -1, 1, 2};

        for (int i = 0; i < moveCol.length; i++) {
            nr = row + moveRow[i];
            nc = column + moveCol[i];
            if (isEmptyOrCanBeCaptured(nr, nc)) {
                result.add(new Position(nr, nc));
            }
        }
        return result;
    }
}
