package org.smorabito.getmytaxy.search.domain;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.util.HashMap;
import java.util.Map;

@Getter
@ToString
public class Graph<T> {
    @Setter
    private boolean calculated = false;
    private final Map<T, Node<T>> nodes = new HashMap<>();

    public void addNode(Node<T> nodeA) {
        nodes.put(nodeA.getId(), nodeA);
    }
}
