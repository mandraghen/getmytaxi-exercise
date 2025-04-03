package org.smorabito.getmytaxy.search.domain;

import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

@Data
@EqualsAndHashCode(of = {"id"})
public class Node<T> {
    private T id;
    private List<Node<T>> shortestPath = new LinkedList<>();
    private Integer sourceDistance = Integer.MAX_VALUE;
    private Map<Node<T>, Integer> adjacentNodes = new HashMap<>();

    public void addDestination(Node<T> destination, int distance) {
        adjacentNodes.put(destination, distance);
    }

    public Node(T id) {
        this.id = id;
    }
}
