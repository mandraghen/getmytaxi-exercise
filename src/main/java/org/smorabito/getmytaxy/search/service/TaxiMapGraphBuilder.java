package org.smorabito.getmytaxy.search.service;

import lombok.RequiredArgsConstructor;
import org.smorabito.getmytaxy.load.domain.Coordinates;
import org.smorabito.getmytaxy.load.domain.TaxiMap;
import org.smorabito.getmytaxy.load.domain.Wall;
import org.smorabito.getmytaxy.load.domain.Weight;
import org.smorabito.getmytaxy.search.domain.Graph;
import org.smorabito.getmytaxy.search.domain.Node;
import org.springframework.stereotype.Service;

import java.util.Optional;
import java.util.stream.Stream;

@RequiredArgsConstructor
@Service
public class TaxiMapGraphBuilder {
    private final OperationalSearchService operationalSearchService;

    public Graph<Coordinates> buildGraph(TaxiMap taxiMap) {
        var graph = new Graph<Coordinates>();
        //create the nodes
        for (int x = 1; x <= taxiMap.getWidth(); x++) {
            for (int y = 1; y <= taxiMap.getHeight(); y++) {
                var node = new Node<>(new Coordinates(x, y));
                graph.addNode(node);
            }
        }
        //create the edges with distance = space (always 1 Km by design)
        for (int x = 1; x <= taxiMap.getWidth(); x++) {
            for (int y = 1; y <= taxiMap.getHeight(); y++) {
                searchNode(graph, x, y)
                        .ifPresent(node -> addDestinations(node, graph, taxiMap));
            }

        }

        return graph;
    }

    private void addDestinations(Node<Coordinates> node, Graph<Coordinates> graph, TaxiMap taxiMap) {
        Stream.of(
                        searchNode(graph, node.getId().getX() + 1, node.getId().getY()),
                        searchNode(graph, node.getId().getX(), node.getId().getY() + 1),
                        searchNode(graph, node.getId().getX() - 1, node.getId().getY()),
                        searchNode(graph, node.getId().getX(), node.getId().getY() - 1))
                .filter(Optional::isPresent)
                .map(Optional::get)
                .filter(destinationNode -> isValidCoordinates(destinationNode, taxiMap, node))
                .forEach(destinationNode ->
                        node.addDestination(destinationNode, calculateWeight(node.getId(),
                                destinationNode.getId(), taxiMap)));
    }

    private Weight calculateWeight(Coordinates sourceCoordinate, Coordinates destinationCoordinate, TaxiMap taxiMap) {
        int price = taxiMap.getCheckpoints().stream()
                .filter(checkpoint -> isThroughWall(checkpoint, sourceCoordinate, destinationCoordinate))
                .mapToInt(checkpoint -> checkpoint.getPrice() + 1)
                .findFirst()
                .orElse(1);
        return new Weight(1, price);
    }

    private boolean isValidCoordinates(Node<Coordinates> adjacentNode, TaxiMap taxiMap, Node<Coordinates> node) {
        Coordinates adjacentCoordinates = adjacentNode.getId();
        return taxiMap.getWalls().stream()
                .noneMatch(wall -> isThroughWall(wall, node.getId(), adjacentCoordinates));
    }

    private boolean isThroughWall(Wall wall, Coordinates sourceCoordinates, Coordinates destinationCoordinates) {
        return (wall.getX1() == destinationCoordinates.getX() && wall.getY1() == destinationCoordinates.getY()
                && wall.getX2() == sourceCoordinates.getX() && wall.getY2() == sourceCoordinates.getY()) ||
                (wall.getX1() == sourceCoordinates.getX() && wall.getY1() == sourceCoordinates.getY()
                        && wall.getX2() == destinationCoordinates.getX()
                        && wall.getY2() == destinationCoordinates.getY());
    }

    private Optional<Node<Coordinates>> searchNode(Graph<Coordinates> graph, int x, int y) {
        var coordinates = new Coordinates(x, y);
        return operationalSearchService.searchNode(graph, coordinates);
    }
}
