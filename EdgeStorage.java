/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ObliqueImproved;

import java.util.ArrayList;

/**
 *
 * @author ASUS
 */
public class EdgeStorage {
    ArrayList<ArrayList<Edge>> edges = new ArrayList<>();
    int maxLabel;
    
    public EdgeStorage(int maxLabel){
        this.maxLabel = maxLabel;
        for (int i = 3; i<= maxLabel; i++){
            edges.add(new ArrayList<>());
        }
        for (int i=3; i<=maxLabel; i++){
            for (int j=3; j<i; j++){
                edges.get(i - 3).add(edges.get(j - 3).get(i - 3));
            }
            for (int j=i; j<=maxLabel; j++){
                edges.get(i - 3).add(new Edge(maxLabel - 2, maxLabel));
            }
        }
    }
    
    public Edge getEdge(int firstLabel, int secondLabel){
        return edges.get(firstLabel - 3).get(secondLabel - 3);
    }
}
