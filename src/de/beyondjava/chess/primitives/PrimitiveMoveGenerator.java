package de.beyondjava.chess.primitives;

import de.beyondjava.chess.common.Move;
import de.beyondjava.chess.objectOrientedEngine.Position;

import java.util.ArrayList;
import java.util.List;

/**
 * Generates the next move, trying to use as primitive type as possible.
 */
public class PrimitiveMoveGenerator extends PrimitiveChessBoards {
    public PrimitiveMoveGenerator(int[][] board, boolean activePlayerIsWhite)
    {
        addChessboard(activePlayerIsWhite, board, 0, 0);
    }


    public Move findBestMove() {
        List<Move> moves = findEveryMove(0);
        if (moves.size()>0)
        {
            return moves.get(0);
        }
        return null;
    }

    private List<Move> findEveryMove(int board) {
        List<Move> moves = getLegalMoves(board, true);
        //   List<Move> legalMoves = origin.getLegalMoves(false);
        return moves;

    }

    public boolean isKingThreatened(int board, boolean whiteKing) {
        boolean opponentsColor = !whiteKing;
        for (int row = 0; row <= 7; row++)
            for (int col = 0; col <= 7; col++) {
                List<Position> targets = getLegalMovesForAPieceIgnoringCheck(board, row, col, opponentsColor);
                for (Position m : targets) {
                    int capturedPiece = getChessPiece(board, m.row, m.column);
                    boolean check = false;
                    check |= (capturedPiece == s_koenig && activePlayerIsWhite(board));
                    check |= (capturedPiece == w_koenig && (!activePlayerIsWhite(board)));
                    if (check)
                    {
                        return true;
                    }
                }
            }
        return false;
    }

    public boolean isBlackKingThreatened(int board) {
        return isKingThreatened(board, false);
    }

    public boolean isWhiteKingThreatened(int board) {
        return isKingThreatened(board, true);
    }

    public List<Move> getLegalMoves(int board, boolean takeCheckIntoAccount) {
        boolean activePlayerIsWhite = activePlayerIsWhite(board);
        return getLegalMoves(board, takeCheckIntoAccount, activePlayerIsWhite);
    }

    private List<Move> getLegalMoves(int board, boolean takeCheckIntoAccount, boolean activePlayerIsWhite) {
        int currentMaterialValue = getMaterialValueFromActivePlayersPointOfView(board);
        List<Move> moves = new ArrayList<>();
        for (int fromRow = 0; fromRow < 8; fromRow++)
            for (int fromColumn = 0; fromColumn < 8; fromColumn++) {
                int piece = getChessPiece(board, fromRow, fromColumn);
                if (isPlayersPiece(activePlayerIsWhite, piece))
                {
                    List<Position> targets = takeCheckIntoAccount ? getLegalMovesForAPiece(board, fromRow, fromColumn, activePlayerIsWhite)
                            : getLegalMovesForAPieceIgnoringCheck(board, fromRow, fromColumn, activePlayerIsWhite);
                    for (Position t : targets) {
                        int capturedPiece = getChessPiece(board, t.row, t.column);
                        int valueAfterMove = currentMaterialValue;
                        if (capturedPiece >= 2)
                            valueAfterMove -= s_MATERIAL_VALUE[capturedPiece];
                        boolean check = false;
                        check |= (capturedPiece == s_koenig && activePlayerIsWhite);
                        check |= (capturedPiece == w_koenig && (!activePlayerIsWhite));
                        if (!activePlayerIsWhite) valueAfterMove = -valueAfterMove;
                        Move m = new Move(getChessPiece(board, fromRow, fromColumn), fromRow, fromColumn, t.row, t.column, valueAfterMove, check, capturedPiece >= 2, capturedPiece);
                        moves.add(m);
                    }
                }
            }
        return moves;
    }

    public List<Position> getLegalMovesForAPiece(int board, int row, int column, boolean activePlayerIsWhite) {
        List<Position> result = getLegalMovesForAPieceIgnoringCheck(board, row, column, activePlayerIsWhite);
        List<Position> r = new ArrayList<>(result.size());
        for (Position p : result) {
            int newBoard = moveChessPiece(board, row, column, p.row, p.column);
            if (!isKingThreatened(newBoard, activePlayerIsWhite))
                r.add(p);
        }
        return r;
    }

    private List<Position> getLegalMovesForAPieceIgnoringCheck(int board, int row, int column, boolean activePlayerIsWhite) {
        List<Position> result = new ArrayList<Position>();
        int piece = getChessPiece(board, row, column);
        if (isPlayersPiece(activePlayerIsWhite, piece)) {
            switch ((piece + 2) >> 2) {
                case 1: /* Pawn */
                    result = getLegalMovesForAPawn(board, row, column, activePlayerIsWhite);
                    break;
                case 2: /* Rook */
                    result = getLegalMovesForARook(board, row, column);
                    break;
                case 3: /* knight (Springer) */
                    result = getLegalMovesForAKnight(board, row, column);
                    break;
                case 4: /* bishop (Laeufer) */
                    result = getLegalMovesForABishop(board, row, column);
                    break;
                case 5: /* Queen */
                    result = getLegalMovesForAQueen(board, row, column);
                    break;
                case 6: /* King */
                    result = getLegalMovesForAKing(board, row, column);
                    break;
            }
        }
        return result;
    }

    private List<Position> getLegalMovesForAPawn(int board, int row, int column, boolean activePlayerIsWhite) {
        List result = new ArrayList<Position>();
        int nr;
        int nc;
        int direction = activePlayerIsWhite(board) ? -1 : 1;

        // simple move forward
        nr = row + direction;
        nc = column;
        if (nr < 0 || nr >= 8) {
            return result;
        }
        if (isInsideBoard(board, nr, nc) && isEmptyField(board, nr, nc)) {
            result.add(new Position(nr, nc));
            // double move forward
            if ((row == 6 && activePlayerIsWhite) || (row == 1 && (!activePlayerIsWhite))) {
                // first move
                nr = row + 2 * direction;
                nc = column;
                if (isEmptyField(board, nr, nc)) {
                    result.add(new Position(nr, nc));
                }
            }
        }

        // capture piece at left hand side
        nr = row + direction;
        nc = column - 1;
        if (isOpponentsPiece(board, nr, nc)) {
            result.add(new Position(nr, nc));
        } else if (canBeCapturedEnPassant(board, nr, nc)) {
            result.add(new Position(nr, nc));
        }

        // capture piece at right hand side
        nr = row + direction;
        nc = column + 1;
        if (isOpponentsPiece(board, nr, nc)) {
            result.add(new Position(nr, nc));
        } else if (canBeCapturedEnPassant(board, nr, nc)) {
            result.add(new Position(nr, nc));
        }
        return result;
    }

    private boolean canBeCapturedEnPassant(int board, int nr, int nc) {
        return isInsideBoard(board, nr, nc) && (getChessPiece(board, nr, nc) == -1);
    }

    private List<Position> getLegalMovesForAQueen(int board, int row, int column) {
        List result = getLegalMovesForARook(board, row, column);
        result.addAll(getLegalMovesForABishop(board, row, column));
        return result;
    }

    private List<Position> getLegalMovesForARook(int board, int row, int column) {
        List result = new ArrayList<Position>();
        int nr;
        int nc;

        nr = row;
        nc = column;
        while (true) {
            nr++;
            if (isEmptyOrCanBeCaptured(board, nr, nc)) {
                result.add(new Position(nr, nc));
                if (!isEmptyField(board, nr, nc)) {
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
            if (isEmptyOrCanBeCaptured(board, nr, nc)) {
                result.add(new Position(nr, nc));
                if (!isEmptyField(board, nr, nc)) {
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
            if (isEmptyOrCanBeCaptured(board, nr, nc)) {
                result.add(new Position(nr, nc));
                if (!isEmptyField(board, nr, nc)) {
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
            if (isEmptyOrCanBeCaptured(board, nr, nc)) {
                result.add(new Position(nr, nc));
                if (!isEmptyField(board, nr, nc)) {
                    break;
                }
            } else {
                break;
            }
        }
        return result;
    }

    private List<Position> getLegalMovesForABishop(int board, int row, int column) {
        List result = new ArrayList<Position>();
        int nr;
        int nc;

        nr = row;
        nc = column;
        while (true) {
            nr++;
            nc++;
            if (isEmptyOrCanBeCaptured(board, nr, nc)) {
                result.add(new Position(nr, nc));
                if (!isEmptyField(board, nr, nc)) {
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
            if (isEmptyOrCanBeCaptured(board, nr, nc)) {
                result.add(new Position(nr, nc));
                if (!isEmptyField(board, nr, nc)) {
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
            if (isEmptyOrCanBeCaptured(board, nr, nc)) {
                result.add(new Position(nr, nc));
                if (!isEmptyField(board, nr, nc)) {
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
            if (isEmptyOrCanBeCaptured(board, nr, nc)) {
                result.add(new Position(nr, nc));
                if (!isEmptyField(board, nr, nc)) {
                    break;
                }
            } else {
                break;
            }
        }
        return result;
    }

    private List<Position> getLegalMovesForAKing(int board, int row, int column) {
        List result = new ArrayList<Position>();
        int nr;
        int nc;

        nr = row;
        nc = column;
        nr++;
        if (isEmptyOrCanBeCaptured(board, nr, nc)) {
            result.add(new Position(nr, nc));
        }
        nr = row;
        nc = column;
        nr--;
        if (isEmptyOrCanBeCaptured(board, nr, nc)) {
            result.add(new Position(nr, nc));
        }
        nr = row;
        nc = column;
        nc++;
        if (isEmptyOrCanBeCaptured(board, nr, nc)) {
            result.add(new Position(nr, nc));
        }
        nr = row;
        nc = column;
        nc--;
        if (isEmptyOrCanBeCaptured(board, nr, nc)) {
            result.add(new Position(nr, nc));
        }
        nr = row;
        nc = column;
        nr++;
        nc++;
        if (isEmptyOrCanBeCaptured(board, nr, nc)) {
            result.add(new Position(nr, nc));
        }
        nr = row;
        nc = column;
        nr--;
        nc++;
        if (isEmptyOrCanBeCaptured(board, nr, nc)) {
            result.add(new Position(nr, nc));
        }
        nr = row;
        nc = column;
        nr++;
        nc--;
        if (isEmptyOrCanBeCaptured(board, nr, nc)) {
            result.add(new Position(nr, nc));
        }
        nr = row;
        nc = column;
        nr--;
        nc--;
        if (isEmptyOrCanBeCaptured(board, nr, nc)) {
            result.add(new Position(nr, nc));
        }
        return result;

    }

    private List<Position> getLegalMovesForAKnight(int board, int row, int column) {
        List result = new ArrayList<Position>();
        int nr;
        int nc;
        int moveRow[] = {1, 2, 2, 1, -1, -2, -2, -1};
        int moveCol[] = {-2, -1, 1, 2, -2, -1, 1, 2};

        for (int i = 0; i < moveCol.length; i++) {
            nr = row + moveRow[i];
            nc = column + moveCol[i];
            if (isEmptyOrCanBeCaptured(board, nr, nc)) {
                result.add(new Position(nr, nc));
            }
        }
        return result;
    }


}
