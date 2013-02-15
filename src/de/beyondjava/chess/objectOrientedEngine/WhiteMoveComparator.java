package de.beyondjava.chess.objectOrientedEngine;

import java.util.Comparator;

public class WhiteMoveComparator implements Comparator<XMove>
{
    @Override
    public int compare(XMove o1, XMove o2) {
        return -o1.compareTo(o2);
    }
}
