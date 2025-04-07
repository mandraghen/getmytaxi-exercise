package org.smorabito.getmytaxy.search.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.smorabito.getmytaxy.export.domain.TaxiRoute;
import org.smorabito.getmytaxy.load.domain.Coordinates;
import org.smorabito.getmytaxy.load.domain.Taxi;
import org.smorabito.getmytaxy.search.domain.Node;
import org.springframework.stereotype.Service;

@Service
public class TaxiRouteBuilder {
    private static final Logger LOG = LoggerFactory.getLogger(TaxiRouteBuilder.class);

    public static final int TAXI_SPEED = 50;

    public TaxiRoute buildTaxiRoute(Taxi taxi, Node<Coordinates> taxiNode, Node<Coordinates> destinationNode) {
        var taxiRoute = new TaxiRoute();
        taxiRoute.setTaxi(taxi.getId());
        taxiRoute.setDistance(taxiNode.getSourceDistance().getDistance() +
                destinationNode.getSourceDistance().getDistance());

        LOG.info("Setting for taxi {} distance: taxi -> customer={}, customer -> destination={}", taxi.getId(),
                taxiNode.getSourceDistance().getDistance(), destinationNode.getSourceDistance().getDistance());

        taxiRoute.setCost(taxiNode.getSourceDistance().getPrice() + destinationNode.getSourceDistance().getPrice());

        LOG.info("Setting for taxi {} price: taxi -> customer={}, customer -> destination={}", taxi.getId(),
                taxiNode.getSourceDistance().getPrice(), destinationNode.getSourceDistance().getPrice());

        taxiRoute.setWaitTime(calculateTime(taxiNode));
        taxiRoute.setTravelTime(calculateTime(destinationNode) + taxiRoute.getWaitTime());
        taxiRoute.setRoute(destinationNode.getShortestPath().stream()
                .map(Node::getId)
                .toList());
        return taxiRoute;
    }

    private float calculateTime(Node<Coordinates> taxiNode) {
        return (float) taxiNode.getSourceDistance().getDistance() / TAXI_SPEED;
    }
}
