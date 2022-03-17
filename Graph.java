/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ObliqueImproved;

import java.util.ArrayList;
import java.io.FileWriter;
import java.io.IOException;

/**
 *
 * @author ASUS
 */
public class Graph {
    
    enum NextAction{
        detConnect,
        branching,
        claimSuccess,
        error
    }
    
    private class Action{
        public NextAction action;
        public Node node;
        public LFace face;
        public OpenEdge edge;
        public int side;
        
        public Action(NextAction action){
            this.action = action;
            
        }
    }
    
    private ArrayList<LFace> openFaces;
    private ArrayList<Node> nodes;
    private int maxLabel;
    private int maxAddLabel;
    private EdgeStorage es;
    private GraphProcedures gp;
    private boolean vypisyBool = false;
    private StructuredFaceStorage storage;
    private int maxEulerLeft;
    private int currentEulerRight;
    private ArrayList<Integer> maxN;
    private ArrayList<Integer> currentN;
    private FileWriter fw;
    private MutablePriorityQueue mpq = new MutablePriorityQueue();
    
    public Graph(int maxLabel){
        
        es = new EdgeStorage(maxLabel);
        gp = new GraphProcedures(es, mpq, this);
        
        maxN = new ArrayList<>();
        maxN.add((maxLabel - 3)*(maxLabel - 4)/6);
        currentN = new ArrayList<>();
        currentN.add(0);
        
        maxN.add((maxLabel -3)*(maxLabel + 2)/8);
        maxN.add((maxLabel -3)*(maxLabel + 2)/10);
        for(int i=4; i<= maxLabel; i++){
            maxN.add((maxLabel -3)*(maxLabel + 2)/(2*i));
            currentN.add(0);
        }
                
        maxEulerLeft = 3*maxN.get(0) + 2*maxN.get(1) + maxN.get(2);
        currentEulerRight = 12;
        
        storage = new StructuredFaceStorage(maxLabel);
        setUpStorage();
        this.maxLabel = maxLabel;
        this.maxAddLabel = maxLabel;
        openFaces = new ArrayList<>();
        nodes = new ArrayList<>(maxLabel*maxLabel*maxLabel);          
    }
    
    public Graph(int maxLabel, FileWriter fw){
        this(maxLabel);
        this.fw = fw;
    }
    
    public Graph(int maxLabel, int maxAddLabel){
        this(maxLabel);
        this.maxAddLabel = maxAddLabel;
    }
    
    public Graph(int maxLabel, int maxAddLabel, ArrayList<Integer> pmaxN){
        this(maxLabel, maxAddLabel);
        this.maxN = maxN;
        int sum = 12;
        sum += maxLabel - 6;
        for(int i = 4; i<= maxAddLabel-3; i++) maxN.set(i,pmaxN.get(i));
    }
    
    private void setUpStorage(){
        for (int i=3; i<=maxLabel; i++){
            this.es.getEdge(i, 3).decreaseAvailability();
            this.storage.useFace(3, 3, i);
            
            this.storage.useFace(3, i, i);
            this.es.getEdge(i,3).decreaseAvailability();
            this.es.getEdge(i,i).decreaseAvailability();
        }
    }
    private void addNodeEuler(int label){
        if (label > 6){
            currentEulerRight += label-6;
        }
        currentN.set(label -3, currentN.get(label - 3) + 1);
       
    }
    
    private void undoAddNodeEuler(int label){
        if (label > 6){
            currentEulerRight -= label-6;
        }
        currentN.set(label - 3, currentN.get(label - 3) -1);
    }
    
    private boolean evalEuler(int label){
        if (currentN.get(label - 3) > maxN.get(label - 3)) return false;
        return maxEulerLeft >= currentEulerRight ? true : false;
    }
    
    public ArrayList<ResultGraph> obliqueSearchByFace(){
        storage = new StructuredFaceStorage(maxLabel); 
        this.setUpStorage();
        ArrayList<ResultGraph> returnObj = new ArrayList<>();
        
        int i=3;
        int j=4;
        
        currentEulerRight = 12;
        addNodeEuler(i);
        addNodeEuler(j);
        addNodeEuler(maxLabel);

        if (true) System.out.println(i + " " + j + " " + maxLabel);

        storage.useFace(i, j, maxLabel);
        generateEnbeding(i,j,maxLabel);

        returnObj.addAll(graphRecursion());
        
        return returnObj;
    }
    
    public ArrayList<ResultGraph> obliqueSearch(){
               
        storage = new StructuredFaceStorage(maxLabel); 
        this.setUpStorage();
        ArrayList<ResultGraph> returnObj = new ArrayList<>();
        
        try{
            fw = new FileWriter("result.txt");
        }
        catch (IOException e) {
            System.out.println("Error opening a file");
            e.printStackTrace();
        }
        
        for(int i=3; i<= maxAddLabel; i++){
            for (int j=i; j<= maxAddLabel; j++){
                if (!storage.isFaceUsed(i, j, maxLabel)){
                    
                    currentEulerRight = 12;
                    addNodeEuler(i);
                    addNodeEuler(j);
                    addNodeEuler(maxLabel);
                    
                    if (false) System.out.println(i + " " + j + " " + maxLabel);
                    this.writeToFW("INIT WITH FACE <" + i +"," + j + "," + maxLabel + ">\n");
                    
                    storage.useFace(i, j, maxLabel);
                    generateEnbeding(i,j,maxLabel);
                    
                    returnObj.addAll(graphRecursion());
                }  
            }
        }
       
        try{
            fw.close();
        }
        catch (IOException e) {
            System.out.println("Error closing a file");
            e.printStackTrace();
        }
        return returnObj;
    }
    
    public ArrayList<ResultGraph> obliqueSearchRestricted(){
        ArrayList<ResultGraph> result = new ArrayList<>();
        
        storage = new StructuredFaceStorage(maxLabel); 
        this.setUpStorage();
        
        for(int i=3; i<= maxAddLabel; i++){
            for (int j=i; j<= maxAddLabel; j++){
                if (!storage.isFaceUsed(i, j, maxLabel)){
                    
                    currentEulerRight = 12;
                    addNodeEuler(i);
                    addNodeEuler(j);
                    addNodeEuler(maxLabel);
                    
                    storage.useFace(i, j, maxLabel);
                    generateEnbeding(i,j,maxLabel);
                    
                    result.addAll(graphRecursion());
                }  
            }
        }
        
        return result;
    }
    
    private void testSequence(){
       generateEnbeding(3,4,8);
       this.printGraph();
       nodes.add(new Node(5,3,openFaces.get(0)));
       gp.addAdjecentNode(nodes.get(3), nodes.get(0), nodes.get(1), openFaces.get(0));
       this.printGraph();
       /*gp.undoAddAdjecentNode(nodes.get(3), nodes.get(0), nodes.get(1), openFaces.get(0));
       nodes.remove(nodes.size() - 1);
       this.printGraph();*/
       gp.connectTwoNodesDet(nodes.get(0), openFaces.get(0));
       this.printGraph();
       gp.undoConnectTwoNodesDet(nodes.get(2), nodes.get(0), nodes.get(3), openFaces.get(0));
       this.printGraph();
       ArrayList<LFace> result = gp.connectTwoNodes(openFaces.get(0), nodes.get(2), 2, nodes.get(3), 1, openFaces);
       this.printGraph();
       gp.undoConnectTwoNodes(result, nodes.get(2), nodes.get(3), openFaces);
       this.printGraph(); 
    }
    
    private void writeToFW(String text){
        if (fw != null){
            try{
                fw.write(text);
            }
            catch (IOException e) {
                System.out.println("Error writing to ze file");
                e.printStackTrace();
            }
        }
    }
        
    private ArrayList<ResultGraph> graphRecursion(){
        if (false && nodes.size() == maxLabel ){
            printGraph();
        }
        ArrayList<ResultGraph> returnObj = new ArrayList<ResultGraph>();
        Action nextAction = nextAction();

        if (nextAction.action == NextAction.claimSuccess){
            ResultGraph result = new ResultGraph();
            
            printGraph();
            
            for(Node x: nodes){
                ArrayList<Integer> neighbours = new ArrayList<>();
                
                for (Node n: x.neighbours){
                    neighbours.add(n.id);
                }

                result.addNode(neighbours);
            }

            returnObj.add(result);
        } 

        if (nextAction.action == NextAction.error){
            System.err.println("STH WENT WRONG");
        }

        if (nextAction.action == NextAction.detConnect){    
            ArrayList<Node> neighbours = gp.connectTwoNodesDet(nextAction.node, nextAction.face);
            if (vypisyBool) printGraph();
            if (neighbours != null){
                if (!storage.isFaceUsed(neighbours.get(0).label, nextAction.node.label, neighbours.get(1).label)){
                    if (nextAction.face.isRealisable()){
                        storage.useFace(neighbours.get(0).label, nextAction.node.label, neighbours.get(1).label);

                        if (nextAction.face.isClosed()){

                            ArrayList<Node> faceNodes = nextAction.face.getNodes();
                            if (!storage.isFaceUsed(faceNodes.get(0).label, faceNodes.get(1).label, faceNodes.get(2).label)){
                                storage.useFace(faceNodes.get(0).label, faceNodes.get(1).label, faceNodes.get(2).label);
                                for (int i = 0; i< openFaces.size(); i++){
                                    if (nextAction.face.compareTo(openFaces.get(i)) == 0){
                                        openFaces.remove(i);
                                    }
                                }
                                
                                openFaces.remove(nextAction.face); 
                                gp.decreaseAvailability(faceNodes.get(0).label, faceNodes.get(1).label, faceNodes.get(2).label);
                                removeEdgesOfAFace(faceNodes, nextAction.face);

                                returnObj.addAll(graphRecursion());
                                
                                addEdgesOfAFace(faceNodes, nextAction.face);
                                gp.increaseAvailability(faceNodes.get(0).label, faceNodes.get(1).label, faceNodes.get(2).label);
                                openFaces.add(nextAction.face);
                                storage.unuseFace(faceNodes.get(0).label, faceNodes.get(1).label, faceNodes.get(2).label);
                            }

                        }
                        else{

                            returnObj.addAll(graphRecursion());

                        }
                        storage.unuseFace(neighbours.get(0).label, nextAction.node.label, neighbours.get(1).label);
                    }
                }
                
                gp.undoConnectTwoNodesDet(neighbours.get(0), nextAction.node , neighbours.get(1), nextAction.face);
                
                if (vypisyBool) printGraph();
            }
        }

        if (nextAction.action == NextAction.branching){
            Node firstNode = nextAction.edge.startNode;
            Node secondNode = nextAction.edge.endNode;

            for (int i = 3; i<=maxAddLabel; i++){
                if (!storage.isFaceUsed(i, firstNode.label, secondNode.label)){
                    Node newNode = new Node(i, nodes.size(), nextAction.face);
                    nodes.add(newNode);
                    gp.addAdjecentNode(newNode, firstNode, secondNode, nextAction.face);
                    if (vypisyBool) printGraph();
                    storage.useFace(i, firstNode.label, secondNode.label);
                    addNodeEuler(i);

                    if (evalEuler(i)) returnObj.addAll(graphRecursion());

                    undoAddNodeEuler(i);
                    storage.unuseFace(i, firstNode.label, secondNode.label);
                    gp.undoAddAdjecentNode(newNode, firstNode, secondNode, nextAction.face);
                    nodes.remove(nodes.size() - 1);
                    if (vypisyBool) printGraph();
                }
            }

            if (true && nextAction.face.size() > 4){
                
                ArrayList<Node> targets = nextAction.face.getConnectableNodes(nextAction.node);
                /*nextAction.face.printFace();
                System.out.print("[ " + nextAction.node.id + " ] ");
                for (Node node : targets){
                    System.out.print(node.id + " ");
                }
                System.out.println();*/
                ArrayList<LFace> resultFaces;
                for (Node node : targets){
                    if (!nextAction.node.neighboursMap.containsKey(node.id)){
                        for (int i=0; i<node.nrOfNodesNotInFace(nextAction.face); i++){
                            //System.out.println("BEFORE: ");
                            //printGraph();
                            resultFaces = gp.connectTwoNodes(nextAction.face, nextAction.node, nextAction.side == 1 ? 0 : nextAction.node.nrOfNodesNotInFace(nextAction.face) - 1, node, i, openFaces);

                            //printGraph();
                            if (resultFaces.get(0).isRealisable() && resultFaces.get(1).isRealisable()){

                                returnObj.addAll(graphRecursion());

                            }
                            //System.out.println("BEFORE: ");
                            //printGraph();
                            gp.undoConnectTwoNodes(resultFaces, nextAction.node, node, openFaces);
                            //printGraph();

                        }
                    }
                }
            }
        }
        
        return returnObj;
    }
    
    public Action nextAction(){
        if (openFaces.size() == 0) return new Action(NextAction.claimSuccess);
        if (mpq.size() == 0) return new Action(NextAction.error);
        
        for(LFace f: openFaces){
            if (f.zeroWhiskerNodes.size() > 0){
                Action nextAction = new Action(NextAction.detConnect);
                nextAction.face = f;
                nextAction.node = f.zeroWhiskerNodes.get(0);
                return nextAction;
            }
        }
        
        Action nextAction = new Action(NextAction.branching);
        Edge edgeType = mpq.peak();   
        int leastWhiskers = Integer.MAX_VALUE;
        int order = 0;
        if (edgeType.openEdges.size() == 0) printGraph();
        OpenEdge bestEdge = edgeType.openEdges.get(0);
        
        for (OpenEdge e: edgeType.openEdges){
            if (e.startNode.nrOfNodesNotInFace(e.inFace) == 1 && e.endNode.nrOfNodesNotInFace(e.inFace) == 1){
                bestEdge = e;
                order = 1;
                break;
            }
            if (e.startNode.nrOfNodesNotInFace(e.inFace) < leastWhiskers){
                bestEdge = e;
                order = 1;
                leastWhiskers = e.startNode.nrOfNodesNotInFace(e.inFace);
            }
            if(e.endNode.nrOfNodesNotInFace(e.inFace) < leastWhiskers){
                bestEdge = e;
                order = 0;
                leastWhiskers = e.endNode.nrOfNodesNotInFace(e.inFace);
            }
        }
        nextAction.edge = bestEdge;
        nextAction.face = bestEdge.inFace;
        nextAction.side = order;
        nextAction.node = order == 1 ? bestEdge.startNode : bestEdge.endNode;
        
        return nextAction;
    }
    
    public void removeEdgesOfAFace(ArrayList<Node> faceNodes, LFace face){
        
        es.getEdge(faceNodes.get(0).label, faceNodes.get(1).label).removeOpenEdge(face, faceNodes.get(0), faceNodes.get(1));
        es.getEdge(faceNodes.get(1).label, faceNodes.get(2).label).removeOpenEdge(face, faceNodes.get(1), faceNodes.get(2));
        es.getEdge(faceNodes.get(2).label, faceNodes.get(0).label).removeOpenEdge(face, faceNodes.get(2), faceNodes.get(0));

        if (es.getEdge(faceNodes.get(0).label, faceNodes.get(1).label).queuePosition != -1 && es.getEdge(faceNodes.get(0).label, faceNodes.get(1).label).openEdges.size() == 0){
           mpq.removeEdgeFromQueue(es.getEdge(faceNodes.get(0).label, faceNodes.get(1).label));
        }
        if (es.getEdge(faceNodes.get(1).label, faceNodes.get(2).label).queuePosition != -1 && es.getEdge(faceNodes.get(1).label, faceNodes.get(2).label).openEdges.size() == 0){
           mpq.removeEdgeFromQueue(es.getEdge(faceNodes.get(1).label, faceNodes.get(2).label));
        }
        if (es.getEdge(faceNodes.get(2).label, faceNodes.get(0).label).queuePosition != -1 && es.getEdge(faceNodes.get(2).label, faceNodes.get(0).label).openEdges.size() == 0){
           mpq.removeEdgeFromQueue(es.getEdge(faceNodes.get(2).label, faceNodes.get(0).label));
        }
        
    }
    
    public void addEdgesOfAFace(ArrayList<Node> faceNodes, LFace face){
        es.getEdge(faceNodes.get(0).label, faceNodes.get(1).label).addOpenEdge(face, faceNodes.get(0), faceNodes.get(1));
        es.getEdge(faceNodes.get(1).label, faceNodes.get(2).label).addOpenEdge(face, faceNodes.get(1), faceNodes.get(2));
        es.getEdge(faceNodes.get(2).label, faceNodes.get(0).label).addOpenEdge(face, faceNodes.get(2), faceNodes.get(0));

        if (es.getEdge(faceNodes.get(0).label, faceNodes.get(1).label).queuePosition == -1){
            mpq.addEdgeToQueue(es.getEdge(faceNodes.get(0).label, faceNodes.get(1).label));
        }
        if (es.getEdge(faceNodes.get(1).label, faceNodes.get(2).label).queuePosition == -1){
            mpq.addEdgeToQueue(es.getEdge(faceNodes.get(1).label, faceNodes.get(2).label));
        }
        if (es.getEdge(faceNodes.get(2).label, faceNodes.get(0).label).queuePosition == -1){
            mpq.addEdgeToQueue(es.getEdge(faceNodes.get(2).label, faceNodes.get(0).label));
        }
    }
    
    public void printGraph(){
        
        System.out.println("--------");
        System.out.println("NODES:");
        for (int i = 0; i<this.nodes.size(); i++){
            System.out.println("ID " + this.nodes.get(i).id);
            //this.nodes.get(i).printFaces(openFaces);
            this.nodes.get(i).printNeighbours();
        }
        if (false){
            System.out.println("FACES:");
            for(int i = 0; i<this.openFaces.size(); i++){
                this.openFaces.get(i).printFace();
            }
        }
        if (false){ mpq.printQueue();}
        /*for (Node x: nodes){
            System.out.print(x.label + " ");
        }*/
    }
    
    private void generateEnbeding(int a, int b, int c){
        openFaces = new ArrayList<>();
        nodes = new ArrayList<>();
        mpq.clear();
        
        LFace openFace = LFace.getOriginFace();
        this.nodes.add(new Node(a, 0, openFace));
        this.nodes.add(new Node(b, 1, openFace));
        this.nodes.add(new Node(c, 2, openFace));
        
        es.getEdge(a,b).decreaseAvailability();
        es.getEdge(a,b).addOpenEdge(openFace, this.nodes.get(0), this.nodes.get(1));
        es.getEdge(b,c).addOpenEdge(openFace, this.nodes.get(1), this.nodes.get(2));
        es.getEdge(a,c).addOpenEdge(openFace, this.nodes.get(2), this.nodes.get(0));
        
        mpq.addEdgeToQueue(es.getEdge(a, b));
        if (c != a){
            es.getEdge(b,c).decreaseAvailability();
            mpq.addEdgeToQueue(es.getEdge(c, b));
        }
        
        if (a != b && c != b){
            es.getEdge(c,a).decreaseAvailability();
            mpq.addEdgeToQueue(es.getEdge(a, c));
        }
        
        
        this.nodes.get(0).addFormerNodesAsNeighbours(this.nodes.get(1), this.nodes.get(2));
        this.nodes.get(1).addFormerNodesAsNeighbours(this.nodes.get(2), this.nodes.get(0));
        this.nodes.get(2).addFormerNodesAsNeighbours(this.nodes.get(0), this.nodes.get(1));
        
        ArrayList<Node> arg = new ArrayList<>();
        arg.add(this.nodes.get(0));
        arg.add(this.nodes.get(1));
        arg.add(this.nodes.get(2));
        
        openFace.addFace(arg);
        openFaces.add(openFace);
    }
}
