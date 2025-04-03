package org.smorabito.getmytaxy.search.utils;

import org.smorabito.getmytaxy.search.domain.Graph;
import org.smorabito.getmytaxy.search.domain.Node;

import java.util.HashSet;
import java.util.LinkedList;
import java.util.Map;
import java.util.Set;

public class OperationalSearchUtils {
    /**
     * This method is used to calculate the shortest path from a source node to all other nodes in the graph using
     * Dijkstra's algorithm.
     *
     * @param graph the graph to be processed
     * @param source the source node
     * @return the graph with the shortest path from the source node to all other nodes
     */
    public static <T> Graph<T> calculateShortestPathFromSource(Graph<T> graph, Node<T> source) {
        //init first node and hash sets
        source.setSourceDistance(0);

        Set<Node<T>> visitedNodes = new HashSet<>();
        Set<Node<T>> unvisitedNodes = new HashSet<>();

        unvisitedNodes.add(source);

        //until all nodes are visited
        while (!unvisitedNodes.isEmpty()) {
            //start to process the node with the lowest distance between those that are being processed in this iteration
            Node<T> currentNode = getLowestDistanceNode(unvisitedNodes);
            unvisitedNodes.remove(currentNode);
            //iterate through all the possible destinations from the current node
            for (Map.Entry<Node<T>, Integer> adjacencyPair : currentNode.getAdjacentNodes().entrySet()) {
                Node<T> adjacentNode = adjacencyPair.getKey();
                Integer edgeWeight = adjacencyPair.getValue();
                //if the node is not visited yet, update the distance and the shortest path if it's lower than the current one
                // and take it for the next iteration
                if (!visitedNodes.contains(adjacentNode)) {
                    updateMinimumDistance(adjacentNode, edgeWeight, currentNode);
                    unvisitedNodes.add(adjacentNode);
                }
            }
            visitedNodes.add(currentNode);
        }
        return graph;
    }

    /**
     * This method is used to find the node with the lowest distance from the source node from the set of nodes
     * that are being processed.
     *
     * @param unvisitedNodes the set of nodes that are being processed
     * @return the node with the lowest distance from the source node
     */
    private static <T> Node<T> getLowestDistanceNode(Set<Node<T>> unvisitedNodes) {
        Node<T> lowestDistanceNode = null;
        int lowestDistance = Integer.MAX_VALUE;
        //TODO improve with lambda expression
        for (Node<T> node : unvisitedNodes) {
            int nodeDistance = node.getSourceDistance();
            if (nodeDistance < lowestDistance) {
                lowestDistance = nodeDistance;
                lowestDistanceNode = node;
            }
        }
        return lowestDistanceNode;
    }

    /**
     * This method is used to update the distance of the evaluation node if the distance from the source node is lower
     * than its current distance considering the path passing from the source node.
     *
     * @param evaluationNode the node that is being evaluated
     * @param edgeWeigh      the weight of the edge between the source node and the evaluation node
     * @param sourceNode     the source node
     */
    private static <T> void updateMinimumDistance(Node<T> evaluationNode, Integer edgeWeigh, Node<T> sourceNode) {
        Integer sourceDistance = sourceNode.getSourceDistance();
        if (sourceDistance + edgeWeigh < evaluationNode.getSourceDistance()) {
            evaluationNode.setSourceDistance(sourceDistance + edgeWeigh);
            //update the shortest path of the evaluation node starting from the current node shortest path and adding it
            LinkedList<Node<T>> shortestPath = new LinkedList<>(sourceNode.getShortestPath());
            shortestPath.add(sourceNode);
            evaluationNode.setShortestPath(shortestPath);
        }
    }
}
