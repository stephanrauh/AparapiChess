package de.beyondjava.chess.primitives;

import de.beyondjava.chess.common.Move;
import de.beyondjava.chess.objectOrientedEngine.Position;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

/**
 * Generates the next move, trying to use as primitive type as possible.
 */
public class PrimitiveMoveGenerator extends PrimitiveChessBoards {

    public static final int[][] positions =
            {{0, 10, 20, 30, 30, 20, 10, 0},
                    {10, 20, 30, 40, 40, 30, 20, 10},
                    {20, 30, 40, 50, 50, 40, 30, 20},
                    {30, 40, 50, 70, 70, 50, 40, 30},
                    {30, 40, 50, 70, 70, 50, 40, 30},
                    {20, 30, 40, 50, 50, 40, 30, 20},
                    {10, 20, 30, 40, 40, 30, 20, 10},
                    {0, 10, 20, 30, 30, 20, 10, 0},
            };
    public static final int[][] whitePawnPositions =
            {{200, 200, 200, 200, 200, 200, 200, 200},
                    {60, 70, 80, 110, 110, 80, 70, 60},
                    {50, 60, 70, 100, 100, 70, 60, 50},
                    {40, 50, 60, 90, 90, 60, 50, 40},
                    {30, 40, 50, 80, 80, 50, 40, 30},
                    {20, 30, 40, 50, 50, 40, 30, 20},
                    {10, 20, 30, 40, 40, 30, 20, 10},
                    {0, 10, 20, 30, 30, 20, 10, 0},
            };
    int anzahlZuege = 0;


    public PrimitiveMoveGenerator(int[][] board, boolean activePlayerIsWhite) {
        addChessboard(activePlayerIsWhite, board, 0, 0);
    }

    public Move findBestMove() {
        Tree<Move> moveTree = new Tree<>(null);
        findEveryMove(0, 0, moveTree);
        for (int board = 0; board < count; board++) {
            evalFieldPositionalValue(board);
        }

        Tree<Move> bestMove = findBestMove(moveTree);
        if (null != bestMove) {
            return bestMove.data;
        }
        return null;
    }

    private Tree<Move> findBestMove(Tree<Move> parent) {
        if (parent.children != null && parent.children.size() > 0) {
            // returns the best move from <code>parents</code> point of view.
            // This is why the worst value is selected - the flag <code>activePlayer</code> has already been inverted
            int bestValue = 12345678;
            int bestMaterial = 12345678;
            int bestPosition = 12345678;
            int bestMoveValue = 12345678;
            Tree<Move> bestMove = null;
            for (Tree<Move> tm : parent.children) {
                if (tm.children != null && tm.children.size() > 0) {
                    findBestMove(tm);
                }
                int board = tm.data.boardAfterMove;
                int newValue = materialValueFromActivePlayersPointOfView[board] + positionalValueFromActivePlayersPointOfView[board]
                        + moveValueFromActivePlayersPointOfView[board];
                int positionDEBUG = positionalValueFromActivePlayersPointOfView[board];
                int moveValueDEBUG = moveValueFromActivePlayersPointOfView[board];
                tm.data.moveValue = moveValueDEBUG;
                tm.data.positionalValue = positionDEBUG;

                if (newValue < bestValue) {
                    bestValue = newValue;
                    bestMove = tm;
                    bestMaterial = materialValueFromActivePlayersPointOfView[board];
                    bestPosition = positionalValueFromActivePlayersPointOfView[board];
                    bestMoveValue = moveValueFromActivePlayersPointOfView[board];
                }
            }
            Collections.sort(parent.children, new Comparator<Tree<Move>>() {
                @Override
                public int compare(Tree<Move> o1, Tree<Move> o2) {
                    int v1 = o1.data.materialValueAfterMove + o1.data.positionalValue + o1.data.moveValue;
                    int v2 = o2.data.materialValueAfterMove + o2.data.positionalValue + o2.data.moveValue;
                    return v1 - v2;
                }
            });
            for (Tree<Move> tm : parent.children) {
                anzahlZuege++;
                System.out.println(anzahlZuege + " " + tm.data);
            }

            if (parent.data != null) {
                int parentBoard = parent.data.boardAfterMove;
                materialValueFromActivePlayersPointOfView[parentBoard] = -bestMaterial;
                positionalValueFromActivePlayersPointOfView[parentBoard] = -bestPosition;
            }
            return bestMove;
        }
        return null;
    }

    private void findEveryMove(int board, int lookAhead, Tree<Move> parent) {
        List<Move> moves = getLegalMoves(board, true);
        for (Move m : moves) {
            int newBoard = moveChessPiece(board, m);
            m.boardAfterMove = newBoard;
            parent.addChild(m);
        }
        if (lookAhead > 0) {
            System.out.println("Next Level ----------------------------------------");
            for (Tree<Move> tm : parent.children) {
                findEveryMove(tm.data.boardAfterMove, lookAhead - 1, tm);
            }
        }
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
                    if (check) {
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
                if (isPlayersPiece(activePlayerIsWhite, piece)) {
                    List<Position> targets = takeCheckIntoAccount ? getLegalMovesForAPiece(board, fromRow, fromColumn, activePlayerIsWhite)
                            : getLegalMovesForAPieceIgnoringCheck(board, fromRow, fromColumn, activePlayerIsWhite);
                    for (Position t : targets) {
                        int capturedPiece = getChessPiece(board, t.row, t.column);
                        int valueAfterMove = currentMaterialValue;
                        if (capturedPiece >= 2)
                            valueAfterMove += s_MATERIAL_VALUE[capturedPiece];
                        boolean check = false;
                        check |= (capturedPiece == s_koenig && activePlayerIsWhite);
                        check |= (capturedPiece == w_koenig && (!activePlayerIsWhite));
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
//            int newBoard = moveChessPiece(board, row, column, p.row, p.column);
//            if (!isKingThreatened(newBoard, activePlayerIsWhite))
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
                    result = getLegalMovesForARook(board, row, column, activePlayerIsWhite);
                    break;
                case 3: /* knight (Springer) */
                    result = getLegalMovesForAKnight(board, row, column, activePlayerIsWhite);
                    break;
                case 4: /* bishop (Laeufer) */
                    result = getLegalMovesForABishop(board, row, column, activePlayerIsWhite);
                    break;
                case 5: /* Queen */
                    result = getLegalMovesForAQueen(board, row, column, activePlayerIsWhite);
                    break;
                case 6: /* King */
                    result = getLegalMovesForAKing(board, row, column, activePlayerIsWhite);
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
        if (isOpponentsPiece(board, nr, nc, activePlayerIsWhite)) {
            result.add(new Position(nr, nc));
        } else if (canBeCapturedEnPassant(board, nr, nc)) {
            result.add(new Position(nr, nc));
        }

        // capture piece at right hand side
        nr = row + direction;
        nc = column + 1;
        if (isOpponentsPiece(board, nr, nc, activePlayerIsWhite)) {
            result.add(new Position(nr, nc));
        } else if (canBeCapturedEnPassant(board, nr, nc)) {
            result.add(new Position(nr, nc));
        }
        return result;
    }

    private boolean canBeCapturedEnPassant(int board, int nr, int nc) {
        return isInsideBoard(board, nr, nc) && (getChessPiece(board, nr, nc) == -1);
    }

    private List<Position> getLegalMovesForAQueen(int board, int row, int column, boolean activePlayerIsWhite) {
        List result = getLegalMovesForARook(board, row, column, activePlayerIsWhite);
        result.addAll(getLegalMovesForABishop(board, row, column, activePlayerIsWhite));
        return result;
    }

    private List<Position> getLegalMovesForARook(int board, int row, int column, boolean activePlayerIsWhite) {
        List result = new ArrayList<Position>();
        int nr;
        int nc;

        nr = row;
        nc = column;
        while (true) {
            nr++;
            if (isEmptyOrCanBeCaptured(board, nr, nc, activePlayerIsWhite)) {
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
            if (isEmptyOrCanBeCaptured(board, nr, nc, activePlayerIsWhite)) {
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
            if (isEmptyOrCanBeCaptured(board, nr, nc, activePlayerIsWhite)) {
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
            if (isEmptyOrCanBeCaptured(board, nr, nc, activePlayerIsWhite)) {
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

    private List<Position> getLegalMovesForABishop(int board, int row, int column, boolean activePlayerIsWhite) {
        List result = new ArrayList<Position>();
        int nr;
        int nc;

        nr = row;
        nc = column;
        while (true) {
            nr++;
            nc++;
            if (isEmptyOrCanBeCaptured(board, nr, nc, activePlayerIsWhite)) {
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
            if (isEmptyOrCanBeCaptured(board, nr, nc, activePlayerIsWhite)) {
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
            if (isEmptyOrCanBeCaptured(board, nr, nc, activePlayerIsWhite)) {
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
            if (isEmptyOrCanBeCaptured(board, nr, nc, activePlayerIsWhite)) {
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

    private List<Position> getLegalMovesForAKing(int board, int row, int column, boolean activePlayerIsWhite) {
        List result = new ArrayList<Position>();
        int nr;
        int nc;

        nr = row;
        nc = column;
        nr++;
        if (isEmptyOrCanBeCaptured(board, nr, nc, activePlayerIsWhite)) {
            result.add(new Position(nr, nc));
        }
        nr = row;
        nc = column;
        nr--;
        if (isEmptyOrCanBeCaptured(board, nr, nc, activePlayerIsWhite)) {
            result.add(new Position(nr, nc));
        }
        nr = row;
        nc = column;
        nc++;
        if (isEmptyOrCanBeCaptured(board, nr, nc, activePlayerIsWhite)) {
            result.add(new Position(nr, nc));
        }
        nr = row;
        nc = column;
        nc--;
        if (isEmptyOrCanBeCaptured(board, nr, nc, activePlayerIsWhite)) {
            result.add(new Position(nr, nc));
        }
        nr = row;
        nc = column;
        nr++;
        nc++;
        if (isEmptyOrCanBeCaptured(board, nr, nc, activePlayerIsWhite)) {
            result.add(new Position(nr, nc));
        }
        nr = row;
        nc = column;
        nr--;
        nc++;
        if (isEmptyOrCanBeCaptured(board, nr, nc, activePlayerIsWhite)) {
            result.add(new Position(nr, nc));
        }
        nr = row;
        nc = column;
        nr++;
        nc--;
        if (isEmptyOrCanBeCaptured(board, nr, nc, activePlayerIsWhite)) {
            result.add(new Position(nr, nc));
        }
        nr = row;
        nc = column;
        nr--;
        nc--;
        if (isEmptyOrCanBeCaptured(board, nr, nc, activePlayerIsWhite)) {
            result.add(new Position(nr, nc));
        }
        return result;

    }

    private List<Position> getLegalMovesForAKnight(int board, int row, int column, boolean activePlayerIsWhite) {
        List result = new ArrayList<Position>();
        int nr;
        int nc;
        int moveRow[] = {1, 2, 2, 1, -1, -2, -2, -1};
        int moveCol[] = {-2, -1, 1, 2, -2, -1, 1, 2};

        for (int i = 0; i < moveCol.length; i++) {
            nr = row + moveRow[i];
            nc = column + moveCol[i];
            if (isEmptyOrCanBeCaptured(board, nr, nc, activePlayerIsWhite)) {
                result.add(new Position(nr, nc));
            }
        }
        return result;
    }

    private int evalFieldPositionalValue(int board) {
        int value = evalFieldPositionalValueFromWhitePointOfView(board);
        if (!activePlayerIsWhite(board)) {
            value = -value;
        }
        positionalValueFromActivePlayersPointOfView[board] = value;
        moveValueFromActivePlayersPointOfView[board] = evalValueOfLegalMoves(board, materialValueFromActivePlayersPointOfView[board], activePlayerIsWhite(board))
                - evalValueOfLegalMoves(board, -materialValueFromActivePlayersPointOfView[board], !activePlayerIsWhite(board));
        return value + moveValueFromActivePlayersPointOfView[board];
    }

    private int evalFieldPositionalValueFromWhitePointOfView(int board) {
        int value = 0;
        for (int row = 0; row < 8; row++)
            for (int col = 0; col < 8; col++) {
                int piece = getChessPiece(board, row, col);
                if (piece == w_bauer) {
                    value += whitePawnPositions[row][col];
                } else if (piece == s_bauer) {
                    value -= whitePawnPositions[7 - row][col];
                } else if (isWhitePiece(piece)) {
                    value += positions[row][col];
                } else if (isBlackPiece(piece)) {
                    value -= positions[row][col];
                }
            }
        return value;
    }

    public int evalValueOfLegalMoves(int board, int currentMaterial, boolean activePlayerIsWhite) {
        int value = 0;
        List<Move> moves = getLegalMoves(board, true, activePlayerIsWhite);
        for (Move m : moves) {
            if (m.opponentInCheck) {
                value += 100;
            } else {
                if (m.capturedPiece >= 2) {
                    int mvGain = s_MATERIAL_VALUE[m.capturedPiece];
                    value += mvGain / 100;
                }
            }
            value += 20;
        }
//        if (getChessPiece(board, 2, 2)==s_springer && (activePlayerIsWhite))
//        {
//            value -= 50000; // ensures knight move
//        }
        return value;
    }


}
