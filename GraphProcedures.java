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
public class GraphProcedures {
    boolean vypisy = false;
    EdgeStorage edges;
    MutablePriorityQueue mpq;
    Graph g;
    
    public GraphProcedures(EdgeStorage storage, MutablePriorityQueue mpq, Graph g){
        edges = storage;
        this.mpq = mpq;
        this.g = g;
    }
    
    public void changeEdges(EdgeStorage newEdges){
        this.edges = newEdges;
    }
    
    public void decreaseAvailability(int k, int l, int m){
        edges.getEdge(k, l).decreaseAvailability();
        if (k != m){
            edges.getEdge(l, m).decreaseAvailability();
        }
        if (l != m && k != l){
            edges.getEdge(m, k).decreaseAvailability();
        }
    }
    
    public void increaseAvailability(int k, int l, int m){
        edges.getEdge(k, l).increaseAvailability();
        if (k != m){
            edges.getEdge(l, m).increaseAvailability();
        }
        if (l != m && k != l){
            edges.getEdge(m, k).increaseAvailability();
        }
    }
    
    public void addAdjecentNode(Node newNode, Node oldFirst, Node oldSecond, LFace inFace){
        if (vypisy) System.out.println("addAdjecentNode in Face "  + inFace.id + " between "+ oldFirst.id + " " + oldSecond.id + " in face: ") ;
        inFace.addAdjecentNode(oldFirst, newNode);
        oldFirst.addAdjecentNode(newNode, oldSecond, inFace);
        oldSecond.addAdjecentNode(newNode, oldFirst, inFace);
        newNode.connectTwoNodesNewNodeSettUp(oldFirst, oldSecond);
        
        decreaseAvailability(oldFirst.label, oldSecond.label, newNode.label);
        
        edges.getEdge(oldFirst.label, oldSecond.label).removeOpenEdge(inFace, oldFirst, oldSecond);
        if (edges.getEdge(oldFirst.label, oldSecond.label).openEdges.size() == 0){
            mpq.removeEdgeFromQueue(edges.getEdge(oldFirst.label, oldSecond.label));
        }
        else{
            mpq.bubbleDown(edges.getEdge(oldFirst.label, oldSecond.label));
        }
        
        edges.getEdge(oldSecond.label, newNode.label).addOpenEdge(inFace, newNode, oldSecond);
        if (edges.getEdge(oldSecond.label, newNode.label).openEdges.size() == 1){
            mpq.addEdgeToQueue(edges.getEdge(oldSecond.label, newNode.label));
        }
        else{
            mpq.bubbleUp(edges.getEdge(oldSecond.label, newNode.label));
        }

        edges.getEdge(newNode.label, oldFirst.label).addOpenEdge(inFace, oldFirst, newNode);
        if (edges.getEdge(newNode.label, oldFirst.label).openEdges.size() == 1){
            mpq.addEdgeToQueue(edges.getEdge(newNode.label, oldFirst.label));
        }
        else{
            mpq.bubbleUp(edges.getEdge(newNode.label, oldFirst.label));
        }

        
        if (oldFirst.nrOfNodesNotInFace(inFace) == 0) inFace.zeroWhiskerNodes.add(oldFirst);
        if (oldSecond.nrOfNodesNotInFace(inFace) == 0) inFace.zeroWhiskerNodes.add(oldSecond);  
        
        if (oldFirst.nrOfNodesNotInFace(inFace) == 1){
            ArrayList<Node> checks = oldFirst.neighboursInFace(inFace);
            for (Node node: checks){
                if (node.compareTo(newNode) != 0 && node.nrOfNodesNotInFace(inFace) == 1) mpq.bubbleUp(edges.getEdge(oldFirst.label, node.label));
            }
        }
        if (oldSecond.nrOfNodesNotInFace(inFace) == 1){
            ArrayList<Node> checks = oldSecond.neighboursInFace(inFace);
            for (Node node: checks){
                if (node.compareTo(newNode) != 0 && node.nrOfNodesNotInFace(inFace) == 1) mpq.bubbleUp(edges.getEdge(oldSecond.label, node.label));
            }
        }
                
    }
    
    public void undoAddAdjecentNode(Node removeNode, Node oldFirst, Node oldSecond, LFace inFace){
        
        if (vypisy) System.out.println("undoAddAdjecentNode");
        inFace.undoAddAdjecentNode(removeNode);
        oldFirst.undoAddAdjecentNode(removeNode, inFace);
        oldSecond.undoAddAdjecentNode(removeNode, inFace);
        
        increaseAvailability(oldFirst.label, oldSecond.label, removeNode.label);
        
        edges.getEdge(oldFirst.label, oldSecond.label).addOpenEdge(inFace, oldFirst, oldSecond);
        if (edges.getEdge(oldFirst.label, oldSecond.label).openEdges.size() == 1){
            mpq.addEdgeToQueue(edges.getEdge(oldFirst.label, oldSecond.label));
        }
        else{
            mpq.bubbleUp(edges.getEdge(oldFirst.label, oldSecond.label));
        }
        
        edges.getEdge(oldSecond.label, removeNode.label).removeOpenEdge(inFace, removeNode, oldSecond);
        if (edges.getEdge(oldSecond.label, removeNode.label).openEdges.size() == 0){
            mpq.removeEdgeFromQueue(edges.getEdge(oldSecond.label, removeNode.label));
        }
        else{
            mpq.bubbleDown(edges.getEdge(oldSecond.label, removeNode.label));
        }
        
        edges.getEdge(removeNode.label, oldFirst.label).removeOpenEdge(inFace, oldFirst, removeNode);
        if (edges.getEdge(removeNode.label, oldFirst.label).openEdges.size() == 0){
            mpq.removeEdgeFromQueue(edges.getEdge(removeNode.label, oldFirst.label));
        }
        else{
            mpq.bubbleDown(edges.getEdge(removeNode.label, oldFirst.label));
        }
        
        if (oldFirst.nrOfNodesNotInFace(inFace) == 1) inFace.removeNodeFromZWN(oldFirst);
        if (oldSecond.nrOfNodesNotInFace(inFace) == 1) inFace.removeNodeFromZWN(oldSecond);
        
        if (oldFirst.nrOfNodesNotInFace(inFace) == 1){
            ArrayList<Node> checks = oldFirst.neighboursInFace(inFace);
            for (Node node: checks){
                if (node.compareTo(oldSecond) != 0 && node.nrOfNodesNotInFace(inFace) == 1) mpq.bubbleUp(edges.getEdge(oldFirst.label, node.label));
            }
        }
        if (oldSecond.nrOfNodesNotInFace(inFace) == 1){
            ArrayList<Node> checks = oldSecond.neighboursInFace(inFace);
            for (Node node: checks){
                if (node.compareTo(oldFirst) != 0 && node.nrOfNodesNotInFace(inFace) == 1) mpq.bubbleUp(edges.getEdge(oldSecond.label, node.label));
            }
        }
        
    }
    public ArrayList<Node> connectTwoNodesDet(Node aroundNode, LFace inFace){
        if (vypisy) System.out.println("connectTwoNodesDet in Face: " + inFace.id + " Node: " + aroundNode.id);
        ArrayList<Node> neighbours =  inFace.connectTwoNodesDet(aroundNode);
        if (vypisy) System.out.println("FN " + neighbours.get(0).id + " sN " + neighbours.get(1).id);
        Node firstNode = neighbours.get(0);
        Node secondNode = neighbours.get(1);
        
        if (firstNode.nrOfNodesNotInFace(inFace) == 0 || secondNode.nrOfNodesNotInFace(inFace) == 0 || firstNode.neighboursMap.containsKey(secondNode.id)) {
            inFace.undoConnectTwoNodesDet(firstNode, aroundNode);
            return null;
        }
        
        firstNode.connectTwoNodesDet(secondNode, aroundNode, inFace);
        secondNode.connectTwoNodesDet(firstNode, aroundNode, inFace);
        
        decreaseAvailability(firstNode.label, secondNode.label, aroundNode.label);
                
        edges.getEdge(firstNode.label, secondNode.label).addOpenEdge(inFace, firstNode, secondNode);
        if (edges.getEdge(firstNode.label, secondNode.label).openEdges.size() == 1){
            mpq.addEdgeToQueue(edges.getEdge(firstNode.label, secondNode.label));
        }
        else{
            mpq.bubbleUp(edges.getEdge(firstNode.label, secondNode.label));
        }
        
        edges.getEdge(firstNode.label, aroundNode.label).removeOpenEdge(inFace, firstNode, aroundNode);
        if (edges.getEdge(firstNode.label, aroundNode.label).openEdges.size() == 0){
            mpq.removeEdgeFromQueue(edges.getEdge(firstNode.label, aroundNode.label));
        }
        else{
            mpq.bubbleDown(edges.getEdge(firstNode.label, aroundNode.label));
        }
        
        edges.getEdge(secondNode.label, aroundNode.label).removeOpenEdge(inFace, aroundNode, secondNode);
        if (edges.getEdge(secondNode.label, aroundNode.label).openEdges.size() == 0){
            mpq.removeEdgeFromQueue(edges.getEdge(secondNode.label, aroundNode.label));
        }
        else{
            mpq.bubbleDown(edges.getEdge(secondNode.label, aroundNode.label));
        }
        
        if (firstNode.nrOfNodesNotInFace(inFace) == 0) inFace.zeroWhiskerNodes.add(firstNode);
        if (secondNode.nrOfNodesNotInFace(inFace) == 0) inFace.zeroWhiskerNodes.add(secondNode);
        
        if (firstNode.nrOfNodesNotInFace(inFace) == 1){
            ArrayList<Node> checks = firstNode.neighboursInFace(inFace);
            for (Node node: checks){
                if (node.compareTo(secondNode) != 0 && node.nrOfNodesNotInFace(inFace) == 1) mpq.bubbleUp(edges.getEdge(firstNode.label, node.label));
            }
        }
        if (secondNode.nrOfNodesNotInFace(inFace) == 1){
            ArrayList<Node> checks = secondNode.neighboursInFace(inFace);
            for (Node node: checks){
                if (node.compareTo(firstNode) != 0 && node.nrOfNodesNotInFace(inFace) == 1) mpq.bubbleUp(edges.getEdge(secondNode.label, node.label));
            }
        }
        
        inFace.removeNodeFromZWN(aroundNode);
        
        return neighbours;
    }
    public void undoConnectTwoNodesDet(Node firstNode, Node aroundNode, Node secondNode, LFace inFace){
        if (vypisy) System.out.println("undoConnectTwoNodesDet  in Face: " + inFace.id + " fN " + firstNode.id +  " Node: " + aroundNode.id + " sN " + secondNode.id);
        
        inFace.undoConnectTwoNodesDet(firstNode, aroundNode);
        firstNode.undoConnectTwoNodesDet(secondNode, inFace);
        secondNode.undoConnectTwoNodesDet(firstNode, inFace);
        
        increaseAvailability(firstNode.label, secondNode.label, aroundNode.label); 
       
        edges.getEdge(firstNode.label, secondNode.label).removeOpenEdge(inFace, firstNode, secondNode);
        if (edges.getEdge(firstNode.label, secondNode.label).openEdges.size() == 0){
            mpq.removeEdgeFromQueue(edges.getEdge(firstNode.label, secondNode.label));
        }
        else{
            mpq.bubbleDown(edges.getEdge(firstNode.label, secondNode.label));
        }
        
        edges.getEdge(firstNode.label, aroundNode.label).addOpenEdge(inFace, firstNode, aroundNode);
        if (edges.getEdge(firstNode.label, aroundNode.label).openEdges.size() == 1){
            mpq.addEdgeToQueue(edges.getEdge(firstNode.label, aroundNode.label));
        }
        else{
            mpq.bubbleUp(edges.getEdge(firstNode.label, aroundNode.label));
        }
        
        edges.getEdge(aroundNode.label, secondNode.label).addOpenEdge(inFace, aroundNode, secondNode);
        if (edges.getEdge(aroundNode.label, secondNode.label).openEdges.size() == 1){
            mpq.addEdgeToQueue(edges.getEdge(aroundNode.label, secondNode.label));
        }
        else{
            mpq.bubbleUp(edges.getEdge(aroundNode.label, secondNode.label));
        }
        
        if (firstNode.nrOfNodesNotInFace(inFace) == 1) inFace.removeNodeFromZWN(firstNode);
        if (secondNode.nrOfNodesNotInFace(inFace) == 1) inFace.removeNodeFromZWN(secondNode);
        inFace.zeroWhiskerNodes.add(aroundNode);
        
        if (firstNode.nrOfNodesNotInFace(inFace) == 1){
            ArrayList<Node> checks = firstNode.neighboursInFace(inFace);
            for (Node node: checks){
                if (node.compareTo(aroundNode) != 0 && node.nrOfNodesNotInFace(inFace) == 1) mpq.bubbleUp(edges.getEdge(firstNode.label, node.label));
            }
        }
        if (secondNode.nrOfNodesNotInFace(inFace) == 1){
            ArrayList<Node> checks = secondNode.neighboursInFace(inFace);
            for (Node node: checks){
                if (node.compareTo(aroundNode) != 0 && node.nrOfNodesNotInFace(inFace) == 1) mpq.bubbleUp(edges.getEdge(secondNode.label, node.label));
            }
        }
       
    }
    public ArrayList<LFace> connectTwoNodes(LFace inFace, Node firstNode, int firstOffset, Node secondNode, int secondOffset, ArrayList<LFace> openFaces){
        if (vypisy) System.out.println("connectTwoNodes: n1: " + firstNode.id + " o1: " + firstOffset +  " n2: " + secondNode.id + " o2: " + secondOffset + " inFace: " + inFace.id);
        ArrayList<LFace> result = inFace.connectTwoNodes(firstNode, secondNode);
        firstNode.connectTwoNodes(secondNode, inFace, firstOffset, result.get(0), result.get(1));
        secondNode.connectTwoNodes(firstNode, inFace, secondOffset, result.get(0), result.get(1));
        
        edges.getEdge(firstNode.label, secondNode.label).addOpenEdge(inFace, firstNode, secondNode);
        edges.getEdge(firstNode.label, secondNode.label).addOpenEdge(inFace, secondNode, firstNode);
        
        for(LFace x: result){
            x.changeFaceInNodes(inFace, firstNode, secondNode);
            x.changeFaceInEdges(inFace, edges);
        }
        
        if (edges.getEdge(firstNode.label, secondNode.label).openEdges.size() == 2){
            mpq.addEdgeToQueue(edges.getEdge(firstNode.label, secondNode.label));
        }
        else{
            mpq.bubbleUp(edges.getEdge(firstNode.label, secondNode.label));
        }
        
        for (LFace face: result){
            if (firstNode.nrOfNodesNotInFace(face) == 1){
                ArrayList<Node> checks = firstNode.neighboursInFace(face);
                for (Node node: checks){
                    if (node.compareTo(secondNode) != 0 && node.nrOfNodesNotInFace(face) == 1) mpq.bubbleUp(edges.getEdge(firstNode.label, node.label));
                }
            }
            if (secondNode.nrOfNodesNotInFace(face) == 1){
                ArrayList<Node> checks = secondNode.neighboursInFace(face);
                for (Node node: checks){
                    if (node.compareTo(firstNode) != 0 && node.nrOfNodesNotInFace(face) == 1) mpq.bubbleUp(edges.getEdge(secondNode.label, node.label));
                }
            }
        }
        
        for(int i=0; i<openFaces.size(); i++){
            if (openFaces.get(i).compareTo(inFace) == 0){
                openFaces.remove(i);
                break;
            }
        } 
        
        openFaces.addAll(result);
        
        if (firstNode.nrOfNodesNotInFace(result.get(0)) == 0) result.get(0).zeroWhiskerNodes.add(firstNode);
        if (secondNode.nrOfNodesNotInFace(result.get(0)) == 0) result.get(0).zeroWhiskerNodes.add(secondNode);
        if (firstNode.nrOfNodesNotInFace(result.get(1)) == 0) result.get(1).zeroWhiskerNodes.add(firstNode);
        if (secondNode.nrOfNodesNotInFace(result.get(1)) == 0) result.get(1).zeroWhiskerNodes.add(secondNode);
        
        
        for (Node x: inFace.zeroWhiskerNodes){
            if (result.get(0).containsNode(x)) result.get(0).zeroWhiskerNodes.add(x);
            else result.get(1).zeroWhiskerNodes.add(x);
        }
        
        return result;
    }
    public void undoConnectTwoNodes(ArrayList<LFace> formerFaces, Node firstNode, Node secondNode,  ArrayList<LFace> openFaces){
        if (vypisy) System.out.println("undoConnectTwoNodes n1: " + firstNode.id + " n2: " + secondNode.id);
        
        LFace result = formerFaces.get(0).undoConnectTwoNodes();
                
        formerFaces.get(0).changeFaceToNew(result, edges);
        formerFaces.get(1).changeFaceToNew(result, edges);

        edges.getEdge(firstNode.label, secondNode.label).removeOpenEdge(result, firstNode, secondNode);
        edges.getEdge(firstNode.label, secondNode.label).removeOpenEdge(result, secondNode, firstNode);
        
        firstNode.undoConnectTwoNodes(result);
        secondNode.undoConnectTwoNodes(result);
        for(LFace x: formerFaces){
            result.changeFaceInNodes(x, firstNode, secondNode);
            result.changeFaceInEdges(x, edges);
            
            for(int i=0; i<openFaces.size(); i++){
                if (openFaces.get(i).compareTo(x) == 0){
                    openFaces.remove(i);
                    break;
                }
            }
        }
        
        if (edges.getEdge(firstNode.label, secondNode.label).openEdges.size() == 0){
            mpq.removeEdgeFromQueue(edges.getEdge(firstNode.label, secondNode.label));
        }
        else{
            mpq.bubbleDown(edges.getEdge(firstNode.label, secondNode.label));
        }
        
        if (firstNode.nrOfNodesNotInFace(result) == 1){
            ArrayList<Node> checks = firstNode.neighboursInFace(result);
            for (Node node : checks){
                if (node.nrOfNodesNotInFace(result) == 1) mpq.bubbleUp(edges.getEdge(firstNode.label, node.label));
            }
        }
        if (secondNode.nrOfNodesNotInFace(result) == 1){
            ArrayList<Node> checks = secondNode.neighboursInFace(result);
            for (Node node : checks){
                if (node.nrOfNodesNotInFace(result) == 1) mpq.bubbleUp(edges.getEdge(secondNode.label, node.label));
            }
        }
        
        openFaces.add(result);
        
      
    }
    
}
