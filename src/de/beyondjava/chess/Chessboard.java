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
    final int[][] board;
    final boolean activePlayerIsWhite;

    public Chessboard() {
        activePlayerIsWhite = true;
        board = ChessConstants.initialBoard;
    }

    public Chessboard(boolean activePlayerIsWhite, Chessboard board) {
        this.board = board.board;
        this.activePlayerIsWhite = activePlayerIsWhite;
    }


    public Chessboard(Chessboard oldBoard, int fromRow, int fromColumn, int toRow, int toColumn) {
        int[][] newBoard = new int[8][8];
        for (int row = 0; row < 8; row++) {
            newBoard[row] = new int[8];
            for (int y = 0; y < 8; y++) {
                int piece = oldBoard.board[row][y];
                if (piece < 0) piece = 0; // forget en passant
                newBoard[row][y] = piece;
            }
        }

        if (oldBoard.board[toRow][toColumn] == -1) {
            // capture en passant
            if (oldBoard.activePlayerIsWhite) {
                newBoard[fromRow][toColumn] = 0;
            } else {
                newBoard[fromRow][toColumn] = 0;
            }
        }
        int piece = newBoard[fromRow][fromColumn];
        if (piece == w_bauer)
            if (fromRow - toRow == 2)
                newBoard[fromRow - 1][toColumn] = -1; // make en passant possible in the opponents move
        if (piece == s_bauer)
            if (fromRow - toRow == -2)
                newBoard[fromRow + 1][toColumn] = -1; // make en passant possible in the opponents move
        newBoard[toRow][toColumn] = piece;
        newBoard[fromRow][fromColumn] = 0;
        board = newBoard;
        activePlayerIsWhite = !oldBoard.activePlayerIsWhite;
    }

    public int getChessPiece(int row, int column) {
        return board[row][column];
    }

    public Chessboard moveChessPiece(int fromRow, int fromColumn, int toRow, int toColumn) {
        return new Chessboard(this, fromRow, fromColumn, toRow, toColumn);
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
        List<Position> legalTargets = getLegalMovesForAPiece(fromRow, fromColumn);
        for (Position p : legalTargets) {
            if (p.equals(toRow, toColumn)) {
                return true;
            }
        }
        return false;
    }

    private boolean isWhitePiece(int piece) {
        if (piece < 2) {
            return false;
        }
        return (((piece / 2) % 2) == 0);
    }

    private boolean isBlackPiece(int piece) {
        if (piece < 2) {
            return false;
        }
        return (((piece / 2) % 2) == 1);
    }

    private boolean isActivePlayersPiece(int piece) {
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

    public boolean isKingThreatened(boolean whiteKing) {
        Chessboard test = new Chessboard(!whiteKing, this);
        List<Move> possibleMoves = test.getLegalMoves();
        for (Move m : possibleMoves) {
            int piece = test.getChessPiece(m.toRow, m.toColumn);
            if (whiteKing && piece == w_koenig)
                return true;
            if ((!whiteKing) && piece == s_koenig)
                return true;
        }
        return false;
    }

    public List<Move> getLegalMoves() {
        List<Move> moves = new ArrayList<>();
        for (int fromRow = 0; fromRow < 8; fromRow++)
            for (int toColumn = 0; toColumn < 8; toColumn++) {
                if (board[fromRow][toColumn] >= 2) {
                    List<Position> targets = getLegalMovesForABishop(fromRow, toColumn);
                    for (Position t : targets) {
                        Move m = new Move(fromRow, toColumn, t.row, t.column);
                        moves.add(m);
                    }
                }
            }
        return moves;
    }

    public List<Position> getLegalMovesForAPiece(int row, int column) {
        List<Position> result = new ArrayList<Position>();
        int piece = board[row][column];
        if (isActivePlayersPiece(piece)) {
            switch ((piece + 2) >> 2) {
                case 1: /* Pawn */
                    result = getLegalMovesForAPawn(row, column);
                    break;
                case 2: /* Rook */
                    result = getLegalMovesForARook(row, column);
                    break;
                case 3: /* knight (Springer) */
                    result = getLegalMovesForAKnight(row, column);
                    break;
                case 4: /* bishop (Laeufer) */
                    result = getLegalMovesForABishop(row, column);
                    break;
                case 5: /* Queen */
                    result = getLegalMovesForAQueen(row, column);
                    break;
                case 6: /* King */
                    result = getLegalMovesForAKing(row, column);
                    break;
            }
        }
        List<Position> r = new ArrayList<>(result.size());
        for (Position p : result) {
            Chessboard n = moveChessPiece(row, column, p.row, p.column);
            if (!n.isKingThreatened(activePlayerIsWhite))
                r.add(p);
        }
        return r;
    }

    private List<Position> getLegalMovesForAPawn(int row, int column) {
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

    private List<Position> getLegalMovesForAQueen(int row, int column) {
        List result = getLegalMovesForARook(row, column);
        result.addAll(getLegalMovesForABishop(row, column));
        return result;
    }

    private List<Position> getLegalMovesForARook(int row, int column) {
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

    private List<Position> getLegalMovesForABishop(int row, int column) {
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

    private List<Position> getLegalMovesForAKing(int row, int column) {
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

    private List<Position> getLegalMovesForAKnight(int row, int column) {
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
