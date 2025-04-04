package org.smorabito.getmytaxy.search.service;

import lombok.NonNull;
import org.smorabito.getmytaxy.export.dto.BestRoutes;
import org.smorabito.getmytaxy.export.dto.TaxiRoute;
import org.smorabito.getmytaxy.load.domain.Coordinates;
import org.smorabito.getmytaxy.load.domain.Request;
import org.smorabito.getmytaxy.load.domain.Taxi;
import org.smorabito.getmytaxy.load.domain.TaxiMap;
import org.smorabito.getmytaxy.load.domain.Weight;
import org.smorabito.getmytaxy.search.domain.Graph;
import org.smorabito.getmytaxy.search.domain.Node;

import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.Function;
import java.util.logging.Logger;

public class TaxiMapSearchService {
    private static final Logger LOG = Logger.getLogger(TaxiMapSearchService.class.getName());

    private final TaxiMapGraphBuilder taxiMapGraphBuilder = new TaxiMapGraphBuilder();
    private final OperationalSearchService operationalSearchService = new OperationalSearchService();
    private final TaxiRouteBuilder taxiRouteBuilder = new TaxiRouteBuilder();

    public Optional<BestRoutes> findBestTaxis(@NonNull TaxiMap taxiMap, @NonNull List<Taxi> taxis,
                                              @NonNull Request request) {
        //create the graph
        Graph<Coordinates> graph = taxiMapGraphBuilder.buildGraph(taxiMap);
        LOG.fine("Created Graph: " + graph);

        Optional<Node<Coordinates>> source = operationalSearchService.searchNode(graph, request.getSource());
        if (source.isEmpty()) {
            LOG.severe("Source node not found in the graph");
            return Optional.empty();
        }

        LOG.fine("Graph after operational search calculation: " + graph);
        graph = operationalSearchService.calculateShortestPathFromSource(graph, source.get(), Weight::getDistance);

        //Store results for the quickest taxi
        var result = new BestRoutes();
        extractBestTaxiRoute(taxis, request, graph, Weight::getDistance)
                .ifPresent(result::setQuickest);

        //run the operational search by price
        graph = operationalSearchService.calculateShortestPathFromSource(graph, source.get(), Weight::getPrice);
        extractBestTaxiRoute(taxis, request, graph, Weight::getPrice)
                .ifPresent(result::setCheapest);

        return Optional.of(result);
    }

    private Optional<TaxiRoute> extractBestTaxiRoute(List<Taxi> taxis, Request request, Graph<Coordinates> graph,
                                                     Function<Weight, Integer> weightProvider) {
        var taxiToNodeMap = new HashMap<Taxi, Node<Coordinates>>();
        taxis.forEach(taxi ->
                operationalSearchService.searchNode(graph, taxi)
                        .ifPresent(node -> taxiToNodeMap.put(taxi, node)));

        Optional<Map.Entry<Taxi, Node<Coordinates>>> taxiNode = taxiToNodeMap.entrySet()
                .stream().min(Comparator.comparingInt(entry ->
                        weightProvider.apply(entry.getValue().getSourceDistance())));
        Optional<Node<Coordinates>> destinationNode = operationalSearchService.searchNode(graph, request.getDestination());

        if (taxiNode.isPresent() && destinationNode.isPresent()) {
            Map.Entry<Taxi, Node<Coordinates>> taxiNodeEntry = taxiNode.get();
            return Optional.of(taxiRouteBuilder.buildTaxiRoute(taxiNodeEntry.getKey(), taxiNodeEntry.getValue(), destinationNode.get()));
        }
        return Optional.empty();
    }
}
