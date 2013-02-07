package de.beyondjava.chess;

import java.util.ArrayList;
import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * User: SoyYo
 * Date: 07.02.13
 * Time: 23:59
 * To change this template use File | Settings | File Templates.
 */
public class LegalMoves extends MaterialValueEvaluator {
    public LegalMoves() {
        super();
    }

    public LegalMoves(boolean activePlayerIsWhite, ChessboardBasis board) {
        super(activePlayerIsWhite, board);
    }

    public LegalMoves(boolean activePlayerIsWhite, Piece... pieces)
    {
        super(activePlayerIsWhite, pieces);
    }


    public LegalMoves(ChessboardBasis oldBoard, int fromRow, int fromColumn, int toRow, int toColumn) {
        super(oldBoard, fromRow, fromColumn, toRow, toColumn);
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
        if ( isInsideBoard(nr, nc) && isEmptyField(nr, nc)) {
            result.add(new Position(nr, nc));
            if ((row == 6 && activePlayerIsWhite) || (row == 1 && (!activePlayerIsWhite))) {
                // first move
                nr = row + 2 * direction;
                nc = column;
                if (isEmptyField(nr, nc)) {
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
