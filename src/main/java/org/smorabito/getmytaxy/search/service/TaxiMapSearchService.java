package org.smorabito.getmytaxy.search.service;

import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.smorabito.getmytaxy.export.domain.BestRoutes;
import org.smorabito.getmytaxy.export.domain.TaxiRoute;
import org.smorabito.getmytaxy.load.domain.Coordinates;
import org.smorabito.getmytaxy.load.domain.Request;
import org.smorabito.getmytaxy.load.domain.Taxi;
import org.smorabito.getmytaxy.load.domain.TaxiMap;
import org.smorabito.getmytaxy.load.domain.Weight;
import org.smorabito.getmytaxy.search.domain.Graph;
import org.smorabito.getmytaxy.search.domain.Node;
import org.springframework.stereotype.Service;

import java.util.Comparator;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Optional;
import java.util.function.Function;

@RequiredArgsConstructor
@Service
public class TaxiMapSearchService {
    private static final Logger LOG = LoggerFactory.getLogger(TaxiMapSearchService.class);

    private final TaxiMapGraphBuilder taxiMapGraphBuilder;
    private final OperationalSearchService operationalSearchService;
    private final TaxiRouteBuilder taxiRouteBuilder;

    public Optional<BestRoutes> findBestTaxis(@NonNull TaxiMap taxiMap, @NonNull List<Taxi> taxis,
                                              @NonNull Request request) {
        //create the graph
        Graph<Coordinates> graph = taxiMapGraphBuilder.buildGraph(taxiMap);
        LOG.debug("Created Graph: {}", graph);

        var source = operationalSearchService.searchNode(graph, request.getSource()).orElse(null);
        if (source == null) {
            LOG.error("Source node not found in the graph");
            return Optional.empty();
        }

        var destinationNode = operationalSearchService.searchNode(graph, request.getDestination()).orElse(null);
        if (destinationNode == null) {
            LOG.error("Destination node not found in the graph");
            return Optional.empty();
        }

        HashMap<Taxi, Node<Coordinates>> taxiToNodeMap = buildTaxiToNodesMap(taxis, graph);
        var destinationNodes = new LinkedList<>(taxiToNodeMap.values());
        destinationNodes.add(destinationNode);

        graph = operationalSearchService.calculateShortestPathFromSource(graph, source, Weight::getDistance,
                destinationNodes);
        LOG.debug("Graph after operational search calculation: {}", graph);
        //Store results for the quickest taxi
        var result = new BestRoutes();
        extractBestTaxiRoute(taxiToNodeMap, destinationNode, Weight::getDistance)
                .ifPresent(result::setQuickest);

        //run the operational search by price
        graph = operationalSearchService.calculateShortestPathFromSource(graph, source, Weight::getPrice,
                destinationNodes);
        extractBestTaxiRoute(taxiToNodeMap, destinationNode, Weight::getPrice)
                .ifPresent(result::setCheapest);

        LOG.debug("Graph after operational search calculation: {}", graph);

        return Optional.of(result);
    }

    private HashMap<Taxi, Node<Coordinates>> buildTaxiToNodesMap(List<Taxi> taxis, Graph<Coordinates> graph) {
        var taxiToNodeMap = new HashMap<Taxi, Node<Coordinates>>();
        taxis.forEach(taxi ->
                operationalSearchService.searchNode(graph, taxi)
                        .ifPresent(node -> taxiToNodeMap.put(taxi, node)));
        return taxiToNodeMap;
    }

    private Optional<TaxiRoute> extractBestTaxiRoute(HashMap<Taxi, Node<Coordinates>> taxiToNodeMap,
                                                     Node<Coordinates> destinationNode,
                                                     Function<Weight, Integer> weightProvider) {
        return taxiToNodeMap.entrySet()
                .stream()
                .min(Comparator.comparingInt(entry ->
                        weightProvider.apply(entry.getValue().getSourceDistance())))
                .map(entry ->
                        taxiRouteBuilder.buildTaxiRoute(entry.getKey(), entry.getValue(), destinationNode));
    }
}
