package de.beyondjava.chess.test

import de.beyondjava.chess.*

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
        int v1= b.evalPositionalValueFromWhitePointOfView();

        blackPawn2 = new Piece('BPE5');
        b = new Chessboard(WHITE_MOVE, whitePawn, blackPawn, blackPawn2)
        int v2 = b.evalPositionalValueFromWhitePointOfView()
        assertTrue("Position is worse due to blocked pawn", v2<v1)
    }

}
