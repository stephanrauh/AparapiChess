package de.beyondjava.chess.primitives;

import java.util.ArrayList;
import java.util.List;

public class Tree<T> {
    public T data;
    public Tree<T> parent;
    public List<Tree<T>> children = new ArrayList<Tree<T>>();

    public Tree(T rootData) {
        data = rootData;
    }

    public void addChild(T data) {
        Tree<T> node = new Tree(data);
        node.parent=this;
        children.add(node);
    }
}