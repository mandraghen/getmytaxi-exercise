package org.smorabito.getmytaxy;

import org.smorabito.getmytaxy.load.domain.Coordinates;
import org.smorabito.getmytaxy.load.domain.Request;
import org.smorabito.getmytaxy.load.domain.Taxi;
import org.smorabito.getmytaxy.load.domain.TaxiMap;
import org.smorabito.getmytaxy.search.domain.Graph;
import org.smorabito.getmytaxy.search.domain.Node;

import java.util.List;
import java.util.stream.Stream;

public class GetMyTaxiExecutor {
    public void findTaxis(TaxiMap taxiMap, List<Taxi> taxis, Request request) {
        //create the graph
        //run the oprational search
        //find the tax that minimizes the time
        //fine the taxi that minimizes the distance
    }

    private Graph<Coordinates> createGraph(TaxiMap taxiMap) {
        Graph<Coordinates> graph = new Graph<>();
        //create the nodes
        for (int x = 0; x < taxiMap.getWidth(); x++) {
            for (int y = 0; y < taxiMap.getHeight(); y++) {
                Coordinates coordinates = new Coordinates();
                coordinates.setX(x);
                coordinates.setY(y);
                graph.addNode(new Node<>(coordinates));
            }
        }

//        taxiMap.getCheckpoints().forEach(checkpoint -> {
//            Coordinates coordinates = new Coordinates();
//            coordinates.setX(checkpoint.getX());
//            coordinates.setY(checkpoint.getY());
//            graph.addNode(new Node<>(coordinates));
//        });
        return graph;
    }
}
