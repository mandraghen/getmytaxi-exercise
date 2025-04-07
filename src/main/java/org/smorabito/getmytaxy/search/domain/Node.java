package org.smorabito.getmytaxy.search.domain;

import lombok.Data;
import lombok.EqualsAndHashCode;
import org.smorabito.getmytaxy.load.domain.Weight;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

@Data
@EqualsAndHashCode(of = {"id"})
public class Node<T> {
    private T id;
    private List<Node<T>> shortestPath = new LinkedList<>();
    private Weight sourceDistance = new Weight(Integer.MAX_VALUE, Integer.MAX_VALUE);
    private Map<Node<T>, Weight> adjacentNodes = new HashMap<>();

    public void addDestination(Node<T> destination, Weight distance) {
        adjacentNodes.put(destination, distance);
    }

    public Node(T id) {
        this.id = id;
    }

    @Override
    public String toString() {
        return "Node{" +
                "id=" + id +
                ", sourceDistance=" + sourceDistance +
                ", shortestPath=" + logShortestPath() +
                ", adjacentNodes=" + logAdjacentNodes() +
                '}';
    }

    private String logShortestPath() {
        var sb = new StringBuilder("[");
        for (Node<T> node : shortestPath) {
            sb.append(node.getId().toString()).append(" (sourceDistance=").append(node.getSourceDistance()).append("), ");
        }
        return sb.append("]").toString();
    }
    
    private String logAdjacentNodes() {
        var sb = new StringBuilder("[");
        for (Map.Entry<Node<T>, Weight> entry : adjacentNodes.entrySet()) {
            sb.append(entry.getKey().getId().toString()).append(" (distance=").append(entry.getValue()).append("), ");
        }
        return sb.append("]").toString();
    }
}
