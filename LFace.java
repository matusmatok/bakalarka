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
public class LFace implements Comparable<LFace>{
    
    public class LinkedList{
        
        public int size;
        
        public class Element{
            public Element next;
            public Node node;
            
            public Element( Node node, Element next){
                this.next = next;
                this.node = node;
            }
        }
        
        public Element startElement = null;
        public Element lastElement = null;
        
        public LinkedList(){
            this.size = 0;
        }
        
        public LinkedList(ArrayList<Node> face){
            this.size = face.size();
            this.addFace(face);
        }
        public void addFace(ArrayList<Node> face){
            startElement = new Element(face.get(0), null);
            Element CE = startElement;
            for(int i=1; i<face.size(); i++){
                CE.next = new Element(face.get(i), null);
                CE = CE.next;
            }   
        } 
        
        public void addNodeToSequence(Node after, Node newNode){
            Element CE = startElement;
            while (CE != null){
                if (CE.node.compareTo(after) == 0){
                   
                    CE.next = new Element(newNode, CE.next);
                    size++;
                    break;
                }
                CE = CE.next;
            }
            if (CE == null) System.err.print("LFACE.LinkedList.addNodeToSequence ERROR");
        }
        
        public void takeNodeFromSequence(Node removeNode){
            if (startElement.node.compareTo(removeNode) == 0) {
                startElement = startElement.next;
                size--;
            }
            else{
                Element CE = startElement;
                while(CE != null){
                    if (CE.next.node.compareTo(removeNode) == 0){
                        CE.next = CE.next.next;
                        size--;
                        break;
                    }
                    CE = CE.next;
                }
            }
        }
        
        public ArrayList<Node> takeNodeFromSequenceWithNeighbours(Node removeNode){
            ArrayList<Node> returnList = new ArrayList<>();
            if (startElement.node.compareTo(removeNode) == 0) {
                startElement = startElement.next;
                size--;
                
                Element CE = startElement;
                while(CE != null){
                    if (CE.next == null){
                        returnList.add(CE.node);
                    }
                    CE = CE.next;
                }
                returnList.add(startElement.node);
                return returnList;
            }
            else{
                Element CE = startElement;
                while(CE != null){
                    if (CE.next.node.compareTo(removeNode) == 0){
                        CE.next = CE.next.next;
                        returnList.add(CE.node);
                        returnList.add(CE.next == null ? startElement.node : CE.next.node);
                        size--;
                        break;
                    }
                    CE = CE.next;
                }
                return returnList;
            }
        }
        
        public void append(Node newNode){
            if (lastElement == null) {
                startElement = new Element(newNode, null);
                lastElement = startElement;
            }
            else{
                lastElement.next = new Element(newNode, null);
                lastElement = lastElement.next;
            }
            size++;
        }       
        
        public boolean containsNode(Node node){
            Element CE = startElement;
            while (CE != null) {
                if (CE.node.compareTo(node) == 0) return true;
                CE = CE.next;
            }
            return false;
        }
        
        public void printFace(){
            Element CE = startElement;
            while(CE != null){
                System.out.print(CE.node.id + " ");
                CE = CE.next;
            }
            System.out.println("size: " + this.size);
        }
    }
    
    private static ArrayList<LFace> faceStack = new ArrayList<>();
    
    public static LFace getOriginFace(){
        if (faceStack.size() == 0) return new LFace();
        return faceStack.get(0);
    }
    
    public LinkedList verts;
    public ArrayList<Node> zeroWhiskerNodes = new ArrayList<>();
    
    public int id;
    private final LFace originFace;
    
    public LFace(){
        this.id = LFace.faceStack.size();
        LFace.faceStack.add(this);
        this.originFace = null;
    }
    
    public LFace(ArrayList<Node> nodes, LFace originFace){
        this.id = LFace.faceStack.size();
        LFace.faceStack.add(this);
        this.originFace = originFace;
        verts = new LinkedList(nodes);
    }
    
    public LFace(LinkedList source, LFace originFace){
        this.originFace = originFace;
        this.verts = source;
        this.id = LFace.faceStack.size();
        LFace.faceStack.add(this);
    }
    
    public void removeNodeFromZWN(Node node){
        for (int i=0; i<zeroWhiskerNodes.size(); i++){
            if (zeroWhiskerNodes.get(i).compareTo(node) == 0 ){
                zeroWhiskerNodes.remove(i);
                break;
            }
        }
    }
    
    public void addFace(ArrayList<Node> nodes){
        this.verts = new LinkedList(nodes);
    }
    
    @Override
    public int compareTo(LFace o) {
        return this.id == o.id ? 0 : 1; 
    }
    
    public int size(){
        return verts.size;
    }
    
    public boolean isClosed(){
        if (this.verts.size == 3){
            LinkedList.Element CE = verts.startElement;
            while(CE != null){
                if (CE.node.nrOfNodesNotInFace(this) != 0) return false;
                CE = CE.next;
            }
            return true;
        } 
        return false;
    }
    
    public boolean isRealisable(){
        if (verts.size > 3) return true;
        if (verts.size < 3) return false;
        int oneCounter = 0;
        int zeroCounter = 0;

        LinkedList.Element CE = this.verts.startElement;
        while(CE != null){
            if (CE.node.nrOfNodesNotInFace(this) == 0) zeroCounter++;
            if (CE.node.nrOfNodesNotInFace(this) == 1) oneCounter++;
            CE = CE.next;
        }
        if (oneCounter == 2) return false;
        if (zeroCounter > 0 && zeroCounter < 3) return false;
        
        return true;
    }
    
    public void printFace(){
        System.out.print("F" + this.id + ": ");
        this.verts.printFace();
        for(Node x: zeroWhiskerNodes){
            System.out.println(x.id + " " + x.label);
        }
    }
    
    public ArrayList<Integer> getNodeLabels(){
        ArrayList<Integer> returnList = new ArrayList<>();
        
        LinkedList.Element CE = verts.startElement;
        
        while (CE != null){
            returnList.add(CE.node.label);
            CE = CE.next;
        }
        
        return returnList;
    
    }
    
    public ArrayList<Node> getNodes(){
        ArrayList<Node> returnList = new ArrayList<>();
        
        LinkedList.Element CE = verts.startElement;
        
        while (CE != null){
            returnList.add(CE.node);
            CE = CE.next;
        }
        
        return returnList;
        
    }
    
    public ArrayList<Node> getConnectableNodes(Node node){
        //System.out.println(node == null ? "null" : node.id);
        ArrayList<Node> returnList = new ArrayList<>();
        
        LinkedList.Element CE = verts.startElement;
        int index = 0;
        int searchedIndex = 0;
        boolean found = false;
        
        while (CE != null){
            if (CE.node.compareTo(node) == 0){
                searchedIndex = index;
                found = true;
            }
            
            if (found && index > searchedIndex + 1){
                returnList.add(CE.node);
            }
            if (found && (verts.size + searchedIndex -2) <= index) break;
            
            index++;
            CE = CE.next == null ? verts.startElement : CE.next;
        }
        
        return returnList;
    }
    
    public void addAdjecentNode(Node after, Node newNode){
        verts.addNodeToSequence(after, newNode);
    }
    
    public void undoAddAdjecentNode(Node removeNode){
        verts.takeNodeFromSequence(removeNode);
    }
    
    public boolean containsNode(Node which){
        LinkedList.Element CE = this.verts.startElement;
        
        while (CE != null) {
            if (CE.node.compareTo(which) == 0) return true;
            CE = CE.next;
        }
        return false;
    }
    
    public ArrayList<Node> connectTwoNodesDet(Node removeNode){
        return verts.takeNodeFromSequenceWithNeighbours(removeNode);
    }
    
    public void undoConnectTwoNodesDet(Node after, Node returnNode){
        verts.addNodeToSequence(after, returnNode);
    }
    
    public ArrayList<LFace> connectTwoNodes(Node firstNode, Node secondNode){
        int which = 0;
        LinkedList firstList = new LinkedList();
        LinkedList.Element CF = null;
        LinkedList secondList = new LinkedList();
        LinkedList.Element CS = null;
        
        
        LinkedList.Element CE = verts.startElement;
        
        while (CE != null){
            if (CE.node.compareTo(firstNode) == 0 || CE.node.compareTo(secondNode) == 0){
                if (which == 0){
                    firstList.append(CE.node);
                    which = 1;
                }
                else{
                    secondList.append(CE.node);
                    which = 0;
                }
                
            }
            if (which == 0){
                firstList.append(CE.node);
            }
            else{
                secondList.append(CE.node);
            }
            CE = CE.next;
        }
        
        LFace firstNew = new LFace(firstList, this);
        LFace secondNew = new LFace (secondList, this);
        ArrayList<LFace> result = new ArrayList<>();
        result.add(firstNew);
        result.add(secondNew);
        
        return result;   
    }
    
    public LFace undoConnectTwoNodes(){
        LFace.faceStack.remove(LFace.faceStack.size() - 1);
        LFace.faceStack.remove(LFace.faceStack.size() - 1);
        
        return this.originFace;
    }
    
    public void changeFaceInEdges(LFace oldFace, EdgeStorage storage){
        LinkedList.Element CE = this.verts.startElement;
        
        while (CE != null) {
            if (CE.next == null){
                storage.getEdge(CE.node.label, this.verts.startElement.node.label).exchangeFace(oldFace, CE.node, this.verts.startElement.node, this);
            }
            else{
                storage.getEdge(CE.node.label,CE.next.node.label).exchangeFace(oldFace, CE.node, CE.next.node, this);
            }
            CE = CE.next;
        }
    }
    
    public void changeFaceToNew(LFace newFace, EdgeStorage storage){
        LinkedList.Element CE = this.verts.startElement;
        
        while (CE != null){
            if (CE.next == null){
                storage.getEdge(CE.node.label, verts.startElement.node.label).exchangeFace(this, CE.node, verts.startElement.node, newFace);
            }
            else{
                storage.getEdge(CE.node.label, CE.next.node.label).exchangeFace(this, CE.node, CE.next.node, newFace);
            }
            CE = CE.next;
        }
    }
    
    public void changeFaceInNodes(LFace oldFace, Node expectOne, Node exceptTwo){
        LinkedList.Element CE = this.verts.startElement;
        
        while (CE != null) {
            if (CE.node.compareTo(expectOne) != 0 && CE.node.compareTo(exceptTwo) != 0){
                CE.node.exchangeFaces(oldFace,this);
            }
            CE = CE.next;
        }
    }
    
}
