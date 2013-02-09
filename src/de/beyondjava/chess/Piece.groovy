package de.beyondjava.chess

/**
 * Created with IntelliJ IDEA.
 * User: SoyYo
 * Date: 07.02.13
 * Time: 21:55
 * To change this template use File | Settings | File Templates.
 */
class Piece implements ChessConstants {
    int row;
    int column;
    int piece;

    public Piece(String s)
    {
        char color=s.charAt(0);
        char p = s.charAt(1);
        char c = s.charAt(2);
        char r = s.charAt(3);
        row = 56  - r; // note that row 8 is chessboard[0], row 1 is chessboard[7]
        column = c - 65;
        switch (p)
        {
            case 'P': piece=s_bauer; break;
            case 'R': piece=s_turm; break;
            case 'B': piece=s_laeufer; break;
            case 'N': piece=s_springer; break;
            case 'Q': piece=s_dame; break;
            case 'K':piece = s_koenig; break;
            default: println "Unknown piece";
        }
        if (color=='W')
        {
            piece += 2;
        }
    }
}
