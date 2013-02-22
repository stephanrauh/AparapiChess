package de.beyondjava.chess.common
public class Piece implements ChessConstants {
    public int row;
    public int column;
    public int piece;

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

    public int getRow ( ) {
    return row ;
    }}
