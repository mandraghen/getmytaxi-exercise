package org.smorabito.getmytaxy.search.service;

import org.smorabito.getmytaxy.export.dto.TaxiRoute;
import org.smorabito.getmytaxy.load.domain.Coordinates;
import org.smorabito.getmytaxy.load.domain.Taxi;
import org.smorabito.getmytaxy.search.domain.Node;

import java.util.logging.Logger;

public class TaxiRouteBuilder {
    private static final Logger LOG = Logger.getLogger(TaxiRouteBuilder.class.getName());

    public static final int TAXI_SPEED = 50;

    public TaxiRoute buildTaxiRoute(Taxi taxi, Node<Coordinates> taxiNode, Node<Coordinates> destinationNode) {
        var taxiRoute = new TaxiRoute();
        taxiRoute.setTaxi(taxi.getId());
        taxiRoute.setDistance(taxiNode.getSourceDistance().getDistance() +
                destinationNode.getSourceDistance().getDistance());

        LOG.info("Setting for taxi " + taxi.getId() + " distance: taxi -> customer=" +
                taxiNode.getSourceDistance().getDistance() + ", customer -> destination=" +
                destinationNode.getSourceDistance().getDistance());

        taxiRoute.setCost(taxiNode.getSourceDistance().getPrice() + destinationNode.getSourceDistance().getPrice());

        LOG.info("Setting for taxi " + taxi.getId() + " price: taxi -> customer=" +
                taxiNode.getSourceDistance().getPrice() + ", customer -> destination=" +
                destinationNode.getSourceDistance().getPrice());

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
