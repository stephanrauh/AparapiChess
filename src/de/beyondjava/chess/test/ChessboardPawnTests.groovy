package de.beyondjava.chess.test

import de.beyondjava.chess.objectOrientedEngine.Chessboard
import de.beyondjava.chess.common.Piece

/**
 * Created with IntelliJ IDEA.
 * User: SoyYo
 * Date: 07.02.13
 * Time: 21:50
 * To change this template use File | Settings | File Templates.
 */
class ChessboardPawnTests extends GroovyTestCase {
    void testKingThreatenedByPawn1()
    {
        Piece blackKing = new Piece(row: 0, column: 4, piece: Piece.s_koenig)
        Piece whiteKing = new Piece(row: 7, column: 4, piece:  Piece.w_koenig)
        Piece whitePawn = new Piece(row: 1, column: 3, piece:  Piece.w_bauer)
        Chessboard b = new Chessboard(true, whiteKing, blackKing, whitePawn)
        assertEquals(true, b.isBlackKingThreatened())
        assertEquals(false, b.isWhiteKingThreatened())
    }

    void testKingThreatenedByPawn2()
    {
        Piece blackKing = new Piece(row: 0, column: 4, piece: Piece.s_koenig)
        Piece whiteKing = new Piece(row: 7, column: 4, piece:  Piece.w_koenig)
        Piece whitePawn = new Piece(row: 1, column: 4, piece:  Piece.w_bauer)
        Chessboard b = new Chessboard(true, whiteKing, blackKing, whitePawn)
        assertEquals(false, b.isBlackKingThreatened())
        assertEquals(false, b.isWhiteKingThreatened())
    }

    void testKingThreatenedByPawn3()
    {
        Piece blackKing = new Piece(row: 0, column: 4, piece: Piece.s_koenig)
        Piece whiteKing = new Piece(row: 7, column: 4, piece:  Piece.w_koenig)
        Piece whitePawn = new Piece(row: 1, column: 5, piece:  Piece.w_bauer)
        Chessboard b = new Chessboard(true, whiteKing, blackKing, whitePawn)
        assertEquals(true, b.isBlackKingThreatened())
        assertEquals(false, b.isWhiteKingThreatened())
    }


}
