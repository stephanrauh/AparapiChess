package de.beyondjava.chess.test

import de.beyondjava.chess.common.Piece
import de.beyondjava.chess.objectOrientedEngine.Chessboard

/**
 * Tests checking the evaluator methods.
 */
class EvaluatorPawnTests extends GroovyTestCase {
    public final static boolean WHITE_MOVE = true;

    public void testPawn1()
    {
        Piece blackPawn = new Piece('BPD5');
        Piece whitePawn = new Piece('WPE4');
        Piece blackPawn2 = new Piece('BPC5');
        Chessboard b = new Chessboard(WHITE_MOVE, whitePawn, blackPawn, blackPawn2)
        assertEquals(-100, b.evalMaterialPosition())
        int v1= b.evalPositionalValue();

        blackPawn2 = new Piece('BPE5');
        b = new Chessboard(WHITE_MOVE, whitePawn, blackPawn, blackPawn2)
        int v2 = b.evalPositionalValue()
        assertTrue("Position is worse due to blocked pawn", v2<v1)
    }

    public void testPawnWhite()
    {
        Chessboard b = new Chessboard();
        Chessboard randbauer = b.moveChessPiece(6, 7, 4, 7);
        Chessboard koenigsbauer = b.moveChessPiece(6, 4, 4, 4);
        int randwert = -randbauer.evalPositionalValue();
        int koenigswert = -koenigsbauer.evalPositionalValue();
        assertTrue(koenigswert>randwert);
    }

    public void testPawnBlack()
    {
        Chessboard b = new Chessboard(false, new Chessboard());
        Chessboard randbauer = b.moveChessPiece(1, 7, 3, 7);
        Chessboard koenigsbauer = b.moveChessPiece(1, 4, 3, 4);
        int randwert = -randbauer.evalPositionalValue();
        int koenigswert = -koenigsbauer.evalPositionalValue();
        assertTrue(koenigswert>randwert);
    }


}
