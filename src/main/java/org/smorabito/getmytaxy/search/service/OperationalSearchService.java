package org.smorabito.getmytaxy.search.service;

import org.smorabito.getmytaxy.load.domain.Weight;
import org.smorabito.getmytaxy.search.domain.Graph;
import org.smorabito.getmytaxy.search.domain.Node;
import org.springframework.stereotype.Service;

import java.util.Collection;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.function.Function;

@Service
public class OperationalSearchService {
    /**
     * This method is used to calculate the shortest path from a source node to all other nodes in the graph using
     * Dijkstra's algorithm.
     *
     * @param graph  the graph to be processed
     * @param source the source node
     * @return the graph with the shortest path from the source node to all other nodes
     */
    //TODO Improve getting a list of destinations and stopping when all the destinations are reached
    public <T> Graph<T> calculateShortestPathFromSource(Graph<T> graph, Node<T> source,
                                                        Function<Weight, Integer> weightProvider,
                                                        Collection<Node<T>> destinationNodes) {
        var destinationNodesSet = new HashSet<>(destinationNodes);
        //reset the graph: sourceDistance, and shortest paths
        resetGraph(graph);
        //init first node and hash sets
        initWeight(source.getSourceDistance());

        Set<Node<T>> visitedNodes = new HashSet<>();
        Set<Node<T>> unvisitedNodes = new HashSet<>();

        unvisitedNodes.add(source);

        //until all nodes are visited
        while (!unvisitedNodes.isEmpty()) {
            //start to process the node with the lowest distance between those that are being processed in this iteration
            Node<T> currentNode = getLowestDistanceNode(unvisitedNodes, weightProvider);
            unvisitedNodes.remove(currentNode);
            //iterate through all the possible destinations from the current node
            for (Map.Entry<Node<T>, Weight> adjacencyPair : currentNode.getAdjacentNodes().entrySet()) {
                Node<T> adjacentNode = adjacencyPair.getKey();
                Weight edgeWeight = adjacencyPair.getValue();
                //if the node is not visited yet, update the distance and the shortest path if it's lower than the current one
                // and take it for the next iteration
                if (!visitedNodes.contains(adjacentNode)) {
                    updateMinimumDistance(adjacentNode, edgeWeight, currentNode, weightProvider);
                    unvisitedNodes.add(adjacentNode);
                }
            }
            visitedNodes.add(currentNode);

            //if the current node is one of the destination nodes, and check if all the destination nodes are visited
            destinationNodesSet.remove(currentNode);
            if (destinationNodesSet.isEmpty()) {
                //if all the destination nodes are visited, break the loop
                break;
            }
        }

        graph.setCalculated(true);
        return graph;
    }

    private void initWeight(Weight weight) {
        weight.setDistance(0);
        weight.setPrice(0);
    }

    private <T> void resetGraph(Graph<T> graph) {
        if (graph.isCalculated()) {
            graph.getNodes().forEach(node -> {
                node.setSourceDistance(new Weight(Integer.MAX_VALUE, Integer.MAX_VALUE));
                node.setShortestPath(new LinkedList<>());
            });
        }
    }

    /**
     * This method is used to find the node with the lowest distance from the source node from the set of nodes
     * that are being processed.
     *
     * @param unvisitedNodes the set of nodes that are being processed
     * @return the node with the lowest distance from the source node
     */
    private <T> Node<T> getLowestDistanceNode(Set<Node<T>> unvisitedNodes, Function<Weight, Integer> weightProvider) {
        Node<T> lowestDistanceNode = null;
        int lowestDistance = Integer.MAX_VALUE;
        for (Node<T> node : unvisitedNodes) {
            int nodeDistance = weightProvider.apply(node.getSourceDistance());
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
    private <T> void updateMinimumDistance(Node<T> evaluationNode, Weight edgeWeigh, Node<T> sourceNode,
                                           Function<Weight, Integer> weightProvider) {
        int sourceDistance = weightProvider.apply(sourceNode.getSourceDistance());
        int edgeWeighValue = weightProvider.apply(edgeWeigh);
        int evaluationDistance = weightProvider.apply(evaluationNode.getSourceDistance());
        if (sourceDistance + edgeWeighValue < evaluationDistance) {
            updateAllWeights(evaluationNode, edgeWeigh, sourceNode);
            //update the shortest path of the evaluation node starting from the current node shortest path and adding it
            LinkedList<Node<T>> shortestPath = new LinkedList<>(sourceNode.getShortestPath());
            shortestPath.add(sourceNode);
            evaluationNode.setShortestPath(shortestPath);
        }
    }

    private <T> void updateAllWeights(Node<T> evaluationNode, Weight edgeWeigh, Node<T> sourceNode) {
        int sourceDistance = sourceNode.getSourceDistance().getDistance();
        int edgeWeighDistance = edgeWeigh.getDistance();
        evaluationNode.getSourceDistance().setDistance(sourceDistance + edgeWeighDistance);

        int sourcePrice = sourceNode.getSourceDistance().getPrice();
        int edgeWeighPrice = edgeWeigh.getPrice();
        evaluationNode.getSourceDistance().setPrice(sourcePrice + edgeWeighPrice);
    }

    public <T> Optional<Node<T>> searchNode(Graph<T> graph, T nodeId) {
        var sampleNode = new Node<>(nodeId);
        //TODO consider to change node set with a map to improve performances
        if (graph.getNodes().contains(sampleNode)) {
            return graph.getNodes().stream().filter(node -> node.equals(sampleNode)).findFirst();
        }
        return Optional.empty();
    }
}
