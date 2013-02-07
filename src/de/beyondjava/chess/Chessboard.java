package de.beyondjava.chess;

import java.util.ArrayList;
import java.util.List;

/**
 * Represents the chess board and provide a couple of methods on possible moves.
 * Date: 02.02.13
 * Time: 19:17
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

    public Chessboard(boolean activePlayerIsWhite, Piece... pieces)
    {
        this.activePlayerIsWhite=activePlayerIsWhite;
        board = new int[8][8];
        for (Piece p: pieces)
        {
            board[p.getRow()][p.getColumn()] = p.getPiece();
        }
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
         if (activePlayerIsWhite) {
            return (isBlackPiece(piece));
        } else {
            return (isWhitePiece(piece));
        }
    }

    public boolean isKingThreatened(boolean whiteKing) {
        Chessboard test = new Chessboard(!whiteKing, this);
//        System.out.println("--------------------------");
        List<Move> possibleMoves = test.getLegalMoves(false);
        for (Move m : possibleMoves) {
//            System.out.println("(" + m.fromColumn + m.fromRow + ") -> (" +m.toColumn + m.toRow + ")");
//            System.out.println(m.materialValueAfterMove);
            if (m.materialValueAfterMove>90000)
            {
                return true;
            }
            if (m.materialValueAfterMove<-90000)
            {
                return true;
            }
        }
        return false;
    }

    public boolean isBlackKingThreatened() {
        return isKingThreatened(false);
    }

    public boolean isWhiteKingThreatened() {
        return isKingThreatened(true);
    }

        public List<Move> getLegalMoves(boolean takeCheckIntoAccount) {
        int currentMaterialValue = evalMaterialPositionFromWhitePointOfView();
        List<Move> moves = new ArrayList<>();
        for (int fromRow = 0; fromRow < 8; fromRow++)
            for (int fromColumn = 0; fromColumn < 8; fromColumn++) {
                if (board[fromRow][fromColumn] >= 2) {

                    List<Position> targets = takeCheckIntoAccount?getLegalMovesForAPiece(fromRow, fromColumn):getLegalMovesForAPieceIgnoringCheck(fromRow, fromColumn);
                    for (Position t : targets) {
                        int capturedPiece = board[t.row][t.column];
                        int valueAfterMove = currentMaterialValue;
                        if (capturedPiece>=2)
                            valueAfterMove -= materialValue[capturedPiece];
                        if (!activePlayerIsWhite) valueAfterMove = -valueAfterMove;
                        Move m = new Move(fromRow, fromColumn, t.row, t.column, valueAfterMove);
                        moves.add(m);
                    }
                }
            }
        return moves;
    }

    public int evalMaterialPosition() {
        int value = evalMaterialPositionFromWhitePointOfView();
        if (!activePlayerIsWhite) return -value;
        else return value;
    }

    public int evalMaterialPositionFromWhitePointOfView() {
        int value = 0;
        for (int fromRow = 0; fromRow < 8; fromRow++)
            for (int toColumn = 0; toColumn < 8; toColumn++) {
                int piece = board[fromRow][toColumn];
                if (piece >= 2) {
//                    System.out.println(toColumn + "," +fromRow + ": " +piece + "=" + materialValue[piece] );
                    value += materialValue[piece];
                }
            }
        return value;
    }

    public List<Position> getLegalMovesForAPiece(int row, int column) {
        List<Position> result = getLegalMovesForAPieceIgnoringCheck(row, column);
        List<Position> r = new ArrayList<>(result.size());
        for (Position p : result) {
            Chessboard n = moveChessPiece(row, column, p.row, p.column);
            if (!n.isKingThreatened(activePlayerIsWhite))
                r.add(p);
        }
        return r;
    }

    private List<Position> getLegalMovesForAPieceIgnoringCheck(int row, int column) {
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
        return result;
    }

    private List<Position> getLegalMovesForAPawn(int row, int column) {
        List result = new ArrayList<Position>();
        int nr;
        int nc;
        int direction = activePlayerIsWhite ? -1 : 1;
        nr = row + direction;
        nc = column;
        if (nr<0 || nr>=8)
        {
            return result;
        }
        if ( isInsideBoard(nr, nc) && board[nr][nc] == 0) {
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
