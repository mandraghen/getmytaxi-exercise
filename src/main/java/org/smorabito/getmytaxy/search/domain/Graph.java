package org.smorabito.getmytaxy.search.domain;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.util.LinkedHashSet;
import java.util.Set;

@Getter
@ToString
public class Graph<T> {
    @Setter
    private boolean calculated = false;
    private final Set<Node<T>> nodes = new LinkedHashSet<>();

    public void addNode(Node<T> nodeA) {
        nodes.add(nodeA);
    }
}
