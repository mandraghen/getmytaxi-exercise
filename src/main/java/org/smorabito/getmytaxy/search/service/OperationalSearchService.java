package org.smorabito.getmytaxy.search.service;

import org.smorabito.getmytaxy.load.domain.Weight;
import org.smorabito.getmytaxy.search.domain.Graph;
import org.smorabito.getmytaxy.search.domain.Node;
import org.springframework.stereotype.Service;

import java.util.Collection;
import java.util.Comparator;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.Optional;
import java.util.Set;
import java.util.function.Function;

@Service
public class OperationalSearchService {
    /**
     * This method is used to calculate the shortest path from a source node to all other nodes in the graph using
     * Dijkstra's algorithm.
     *
     * @param graph            the graph to be processed
     * @param source           the source node
     * @param destinationNodes the destination nodes to be reached
     * @return the graph with the shortest path from the source node to all other nodes
     */
    public <T> Graph<T> calculateShortestPathFromSource(Graph<T> graph, Node<T> source,
                                                        Function<Weight, Integer> weightProvider,
                                                        Collection<Node<T>> destinationNodes) {
        var destinationNodesSet = new HashSet<>(destinationNodes);
        //reset the graph: sourceDistance, and shortest paths
        resetGraph(graph);
        //init first node and hash sets
        initWeight(source.getSourceDistance());
        var visitedNodes = new HashSet<Node<T>>();
        var unvisitedNodes = new HashSet<Node<T>>();

        unvisitedNodes.add(source);

        //until all nodes are visited
        while (!unvisitedNodes.isEmpty()) {
            //start to process the node with the lowest distance between those that are being processed in this iteration
            Node<T> currentNode = getLowestDistanceNode(unvisitedNodes, weightProvider);
            unvisitedNodes.remove(currentNode);
            //iterate through all the possible destinations from the current node
            currentNode.getAdjacentNodes().forEach((adjacentNode, edgeWeight) -> {
                //if the node is not visited yet, update the distance and the shortest path if it's lower than the current one
                // and take it for the next iteration
                if (!visitedNodes.contains(adjacentNode)) {
                    updateMinimumDistance(adjacentNode, edgeWeight, currentNode, weightProvider);
                    unvisitedNodes.add(adjacentNode);
                }
            });
            visitedNodes.add(currentNode);

            //if the current node is one of the destination nodes, and check if all the destination nodes are visited
            if (destinationNodesSet.remove(currentNode) && destinationNodesSet.isEmpty()) {
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
            graph.getNodes().forEach((id, node) -> {
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
        return unvisitedNodes.stream()
                .min(Comparator.comparingInt(node -> weightProvider.apply(node.getSourceDistance())))
                .orElse(null);
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
        int edgeWeightValue = weightProvider.apply(edgeWeigh);
        int evaluationDistance = weightProvider.apply(evaluationNode.getSourceDistance());
        if (sourceDistance + edgeWeightValue < evaluationDistance) {
            updateAllWeights(evaluationNode, edgeWeigh, sourceNode);
            //update the shortest path of the evaluation node starting from the current node shortest path and adding it
            var shortestPath = new LinkedList<>(sourceNode.getShortestPath());
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
        return Optional.ofNullable(graph.getNodes().get(nodeId));
    }
}
