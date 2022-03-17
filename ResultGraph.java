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
public class ResultGraph {
    public ArrayList<ArrayList<Integer>> nodes;
    
    public ResultGraph(){
        nodes = new ArrayList<>();
    }
    
    public void addNode(ArrayList<Integer> neighbours){
        this.nodes.add(neighbours);
    }
    
    public void printGraph(){
        for(int i = 0; i<nodes.size(); i++){
            System.out.println("ID" + i);
            for (Integer x : nodes.get(i)){
                System.out.print(x + " ");
            }
            System.out.println();
        }
        System.out.println("-----------------------------------------");
    }
    
}
