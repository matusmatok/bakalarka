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
public class Edge implements Comparable<Edge> {
    
    int maxLabel;
    int availableFaces;
    int queuePosition;
    ArrayList<OpenEdge> openEdges = new ArrayList<>();
    
    public Edge(int availableFaces, int maxLabel){
        this.availableFaces = availableFaces;
        this.maxLabel = maxLabel;
        queuePosition = -1;
    }
    
    public void increaseAvailability(){
        this.availableFaces++;
    }
    
    public void decreaseAvailability(){
        this.availableFaces--;
    }
    
    public boolean containsOneOneEdge(){
        for (OpenEdge e: openEdges){
            if (e.startNode.nrOfNodesNotInFace(e.inFace) == 1 && e.endNode.nrOfNodesNotInFace(e.inFace) == 1) return true;
        }
        return false;
    }
    
    public void addOpenEdge(LFace inFace, Node startNode, Node endNode){
        openEdges.add(new OpenEdge(inFace, startNode, endNode));
    }
    
    public void removeOpenEdge(LFace inFace, Node startNode, Node endNode){
        for (int i= 0; i<openEdges.size(); i++){
            if (openEdges.get(i).inFace.compareTo(inFace) == 0 && openEdges.get(i).startNode.compareTo(startNode) == 0 && openEdges.get(i).endNode.compareTo(endNode) == 0){
                openEdges.remove(i);
                break;
            }
        }
    }
    
    public void exchangeFace(LFace inFace, Node startNode, Node endNode, LFace newFace){
        for(OpenEdge e: openEdges){
            if (e.inFace.compareTo(inFace) == 0 && e.startNode.compareTo(startNode) == 0 && e.endNode.compareTo(endNode) == 0) e.inFace = newFace;
        }
    }

    @Override
    public int compareTo(Edge o) {
        
        if (this.openEdges.get(0).endNode.label == maxLabel || this.openEdges.get(0).startNode.label == maxLabel) return 1;
        if (o.openEdges.get(0).endNode.label == maxLabel || o.openEdges.get(0).startNode.label == maxLabel) return -1;
        if (this.containsOneOneEdge()){
            if (o.containsOneOneEdge()) return (this.availableFaces == o.availableFaces) ? 0 : ((this.availableFaces < o.availableFaces) ? 1 : -1);
            else return 1;
        }
        else{
            if (o.containsOneOneEdge()) return -1;
            else return (this.availableFaces == o.availableFaces) ? 0 : ((this.availableFaces < o.availableFaces) ? 1 : -1);
        }   
    }
    
     
    public void printEdge(){
        System.out.println(queuePosition);
        if (openEdges.size() > 0){
            System.out.println("TYPE: " + openEdges.get(0).startNode.label + " " + openEdges.get(0).endNode.label + " AV. FACES: " + availableFaces);
            for (OpenEdge x : openEdges){
                System.out.println("F" + x.inFace.id + "  S: " + x.startNode.id + " E: " + x.endNode.id);
            }
        }
        else{
            System.out.println("NO OPEN EDGES - FORGOT TO REMOVE ME ");
        }
    }
    
}
