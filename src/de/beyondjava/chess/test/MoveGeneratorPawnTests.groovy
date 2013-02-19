package de.beyondjava.chess.test

import de.beyondjava.chess.gui.ChessMoveGUI
import de.beyondjava.chess.objectOrientedEngine.Chessboard
import de.beyondjava.chess.common.Move
import de.beyondjava.chess.common.Piece
/**
 * Tests checking the evaluator methods.
 */
class MoveGeneratorPawnTests extends GroovyTestCase {
    public final static boolean WHITE_MOVE = true;

    public void testPawn1()
    {
        Piece blackPawn = new Piece('BPD5');
        Piece whitePawn = new Piece('WPE4');
        Chessboard b = new Chessboard(WHITE_MOVE, whitePawn, blackPawn)
        Move m = b.findBestMove(1);
        assertEquals("E4xD5", m.getNotation())
        b = b.findBestMove();
        assertEquals(100, b.evalMaterialPositionFromWhitePointOfView())
        //new ChessGUIRemoteControl().draw(b)
    }

    public void testPawn2()
    {
        Piece blackPawn = new Piece('BPD5');
        Piece whitePawn = new Piece('WPE4');
        Piece blackPawn2 = new Piece('BPE5');
        Chessboard b = new Chessboard(WHITE_MOVE, whitePawn, blackPawn, blackPawn2)
        Move m = b.findBestMove(1);
        assertEquals("E4xD5", m.getNotation())
        b = b.findBestMove();
        assertEquals(0, b.evalMaterialPositionFromWhitePointOfView())
        //new ChessGUIRemoteControl().draw(b)
    }

    public void testPawn3()
    {
        Piece blackPawn = new Piece('BPD5');
        Piece whitePawn = new Piece('WPE4');
        Piece blackPawn2 = new Piece('BPF5');
        Chessboard b = new Chessboard(WHITE_MOVE, whitePawn, blackPawn, blackPawn2)
        Move m = b.findBestMove(1);
        assertEquals("E4xD5", m.getNotation())
        b = b.findBestMove();
        assertEquals(0, b.evalMaterialPositionFromWhitePointOfView())
        new ChessMoveGUI().draw(b)
    }

}
