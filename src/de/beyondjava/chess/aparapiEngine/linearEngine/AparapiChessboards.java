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
        a.run();
        for (int i = 0; i < 1000; i++) {
            evaluateParallel(a);
        }

    }
    public void run() {
        evaluateBoard();
    }


    private static void evaluateParallel(AparapiChessboards a) {
        long start = System.nanoTime();
        Range range = Range.create(1000000);
        a.execute(range);
        long end = System.nanoTime();
        System.out.println(((end - start) / 1000) / 1000.0d + " ms");
    }

 }
