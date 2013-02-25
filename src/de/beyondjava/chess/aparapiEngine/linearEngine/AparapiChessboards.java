package de.beyondjava.chess.aparapiEngine.linearEngine;

import com.amd.aparapi.Range;


/**
 * Represents the chess board and provide a couple of methods on possible moves.
 * Date: 02.02.13
 * Time: 19:17
 */
public class AparapiChessboards extends AparapiChessboardsEvaluator {

    public static void main(String... args) {
        AparapiChessboards a = new AparapiChessboards();
        for (int i = 0; i < MAXBOARDS; i++)
        {
            a.startGame(i);
        }
        for (int i = 0; i < 1000; i++) {
            evaluateParallel(a);
            a.jEvaluate();
        }
    }

    public void jEvaluate()
    {
        long start = System.nanoTime();
        for(int i = 0; i < MAXBOARDS; i++)
        {
            evaluateBoard(i);
        }
        long end = System.nanoTime();
        System.out.println(((end - start) / 1000) / 1000.0d + " ms");

    }

    public void run() {

        evaluateBoard(getGlobalId());
    }


    private static void evaluateParallel(AparapiChessboards a) {
        long start = System.nanoTime();
        Range range = Range.create(MAXBOARDS);
        a.execute(range);
        long end = System.nanoTime();
        System.out.print(((end - start) / 1000) / 1000.0d + " ms  ");
    }

 }
