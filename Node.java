/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ObliqueImproved;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

/**
 *
 * @author ASUS
 */
public class Node implements Comparable<Node>{
    
    public int id;
    public int label;
    ArrayList<Node> neighbours;
    ArrayList<LFace> adjFaces;
    ArrayList<Integer> startBound;
    ArrayList<Integer> endBound;
    HashMap<Integer,Boolean> neighboursMap;

    public Node(int label, int id, LFace formerFace){
        this.id = id;
        this.label = label;
        this.neighbours = new ArrayList<>(label);
        this.adjFaces = new ArrayList<>();
        this.startBound = new ArrayList<>();
        this.endBound = new ArrayList<>();
        this.adjFaces.add(formerFace);
        for (int i=0; i<label; i++){
            neighbours.add(null);
        }
        this.startBound.add(0);
        this.endBound.add(0);
        neighboursMap = new HashMap<>();
    }
    
    @Override
    public int compareTo(Node o) {
        return this.id == o.id ? 0 : 1;
    }
    
    public void exchangeFaces(LFace oldFace, LFace newFace){
        for(int i = 0; i< adjFaces.size(); i++){
            if (adjFaces.get(i).compareTo(oldFace) == 0){
                adjFaces.set(i, newFace);
            }
        }
    }
    
    public void addFormerNodesAsNeighbours(Node first, Node second){
        this.neighboursMap.put(first.id, true);
        this.neighboursMap.put(second.id, true);
        this.neighbours.set(0, first);
        this.neighbours.set(this.label - 1, second);
        this.startBound.set(0,1);
        this.endBound.set(0, this.label -1);
    }
    
    public void connectTwoNodesNewNodeSettUp(Node firstNode, Node secondNode){
        this.neighboursMap.put(firstNode.id, true);
        this.neighboursMap.put(secondNode.id, true);
        this.neighbours.set(0, firstNode);
        this.neighbours.set(1, secondNode);
        this.startBound.set(0,2);
        this.endBound.set(0,0);
    }
    
    public int nrOfNodesNotInFace(LFace face){
        for (int i = 0; i <adjFaces.size(); i++){
            if (adjFaces.get(i).compareTo(face) == 0){
                return ((endBound.get(i) + label) - startBound.get(i))%label;                  
            }
        }
        System.out.println("PFace ID: " + face.id + " Node ID: " + this.id);
        System.out.println("Node.nrOfNodesNotInFace -- forcykle failed. AdjFaces.size: " + adjFaces.size() + " FACE O ID: " + adjFaces.get(0).id);
        return -1;
    }
    
    public void addAdjecentNode(Node newNeighbour, Node ofNeighbour, LFace fromFace){
        this.neighboursMap.put(newNeighbour.id, true);
        for(int i = 0; i<adjFaces.size(); i++){
            if (adjFaces.get(i).compareTo(fromFace) == 0){
                if (this.neighbours.get((this.startBound.get(i) + label - 1)%label).compareTo(ofNeighbour) == 0){
                    this.neighbours.set(this.startBound.get(i), newNeighbour);
                    this.startBound.set(i,(this.startBound.get(i) + 1)%this.label);
                }
                if (this.neighbours.get(this.endBound.get(i)).compareTo(ofNeighbour) == 0){
                    this.neighbours.set((this.endBound.get(i) + label - 1)%this.label, newNeighbour);
                    this.endBound.set(i,(this.endBound.get(i) + label - 1)%this.label);
                }
                //System.err.println("UNSUCCESFULL Node.addAdjecentNode");
                break;
            }
        } 
    }
    
    public void undoAddAdjecentNode(Node eraseNode, LFace fromFace){
        this.neighboursMap.remove(eraseNode.id);
        for (int i = 0; i< adjFaces.size(); i++){
            if (adjFaces.get(i).compareTo(fromFace) == 0){
                if (this.neighbours.get((this.startBound.get(i) + label -1)%label).compareTo(eraseNode) == 0){
                    this.neighbours.set(((this.startBound.get(i) + label -1)%label), null);
                    this.startBound.set(i,(this.startBound.get(i) + label -1)%label);
                }
                if (this.neighbours.get(this.endBound.get(i)).compareTo(eraseNode) == 0){
                    this.neighbours.set(this.endBound.get(i), null);
                    this.endBound.set(i,(this.endBound.get(i) + 1)%label);
                }
                //System.err.println("UNSUCCESFULL Node.undoAddAdjecentNode");
                break;
            }
        }
    }
    
    public void connectTwoNodes(Node with, LFace inFace, int offset, LFace firstNew, LFace secondNew){
        this.neighboursMap.put(with.id, true);
        for(int i = 0; i < adjFaces.size(); i++){
            if (adjFaces.get(i).compareTo(inFace) == 0){
                neighbours.set((startBound.get(i) + offset)%label, with);

                startBound.add(startBound.get(i));
                endBound.add((startBound.get(i) + offset)%label);

                startBound.add((startBound.get(i) + offset + 1)%label);
                endBound.add(endBound.get(i));

                if(firstNew.containsNode(this.neighbours.get(this.endBound.get(endBound.size() - 1)))){
                    this.adjFaces.add(secondNew);
                    this.adjFaces.add(firstNew);
                }
                else{
                    this.adjFaces.add(firstNew);
                    this.adjFaces.add(secondNew);
                }
                break;
                //System.out.println("SUCCESFULL CONNECTION: SB.size: " + startBound.size() + " EB.size: " + endBound.size());
            }
        }
    }
    
    public void undoConnectTwoNodes(LFace formerFace){
        
        for (int i = 0; i<adjFaces.size(); i++){
            if (adjFaces.get(i).compareTo(formerFace) == 0){
                for (int j = startBound.get(i); j < endBound.get(i) + (endBound.get(i) < startBound.get(i) ? neighbours.size() : 0); j++){
                    if (this.neighbours.get(j) != null) neighboursMap.remove(neighbours.get(j).id);
                    this.neighbours.set(j%neighbours.size(), null);
                }
                break;
            }
        }
        
        for (int i = 0; i<2; i++){        
            this.adjFaces.remove(this.adjFaces.size() - 1);
            this.startBound.remove(this.startBound.size() - 1);
            this.endBound.remove(this.endBound.size() - 1);
        }
    }
    
    public void connectTwoNodesDet(Node to, Node neighbour, LFace inFace){
        this.addAdjecentNode(to,neighbour,inFace);  
    }
    
    public void undoConnectTwoNodesDet(Node eraseNeighbour, LFace inFace){
        this.undoAddAdjecentNode(eraseNeighbour, inFace);
    }
    
    public void printFaces(ArrayList<LFace> openFaces){
        /*for(int i = 0; i<adjFaces.size(); i++){
            for(Face x: openFaces){
                if (x.compareTo(adjFaces.get(i)) == 0){
                    System.out.println((adjFaces.get(i) != null ? "F"+adjFaces.get(i).id : "null") + " " + startBound.get(i) + " " + endBound.get(i));
                    break;
                }
            }
        }  */
        
        for(int i = 0; i<adjFaces.size(); i++){
            System.out.println((adjFaces.get(i) != null ? "F"+adjFaces.get(i).id : "null") + " " + startBound.get(i) + " " + endBound.get(i));
        }
    }
        
    public void printNeighbours(){
        for(Node x : this.neighbours){
            System.out.print(x == null ? "Null " : Integer.toString(x.id)+" ");
        }
        System.out.println();
    }
    
    public ArrayList<Node> neighboursInFace(LFace face){
        ArrayList<Node> res = new ArrayList<>();
        for (int i=0; i<adjFaces.size(); i++){
            if (adjFaces.get(i).compareTo(face) == 0){
                res.add(neighbours.get((startBound.get(i) + neighbours.size() - 1)%neighbours.size()));
                res.add(neighbours.get(endBound.get(i)));
                break;
            }
        }
        return res;
        
    }
    
    
}
