/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ObliqueImproved;

/**
 *
 * @author ASUS
 */
    public class OpenEdge{
        public LFace inFace;
        public Node startNode;
        public Node endNode;
        
        public OpenEdge(LFace inFace, Node startNode, Node endNode){
            this.inFace = inFace;
            this.startNode = startNode;
            this.endNode = endNode;
        }
        
        public boolean compareTo(OpenEdge o){
            if (this.inFace.compareTo(o.inFace) != 0) return false;
            if (this.startNode.compareTo(o.startNode) != 0) return false;
            if (this.endNode.compareTo(o.endNode) != 0) return false;
            return true;
        }
        
    }
