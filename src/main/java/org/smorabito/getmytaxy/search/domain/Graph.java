package org.smorabito.getmytaxy.search.domain;

import lombok.Getter;

import java.util.HashSet;
import java.util.Set;

@Getter
public class Graph<T> {
    private final Set<Node<T>> nodes = new HashSet<>();

    public void addNode(Node<T> nodeA) {
        nodes.add(nodeA);
    }
}
