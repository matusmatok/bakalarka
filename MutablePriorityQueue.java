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
public class MutablePriorityQueue {
    
    private ArrayList<Edge> queue = new ArrayList<>();
   // private EdgeStorage es;
    
    public MutablePriorityQueue(){
    }
    
    public Edge peak(){
        return queue.get(0);
    }
    
    public int size(){
        return queue.size();
    }
    
    public void clear(){
        for (Edge e : queue){
            e.queuePosition = -1;
            e.openEdges = new ArrayList<>();
        }
        queue = new ArrayList<>();
    }
    
    public void addEdgeToQueue(Edge newEdge){
        queue.add(newEdge);
        queue.get(queue.size() - 1).queuePosition = queue.size() - 1;
        bubbleUp(queue.size() - 1);
    }
    
    public void removeEdgeFromQueue(Edge removeEdge){
        queue.set(removeEdge.queuePosition, queue.get(queue.size() - 1));
        queue.get(removeEdge.queuePosition).queuePosition = removeEdge.queuePosition;
        queue.remove(queue.size() - 1);
        
        //bubbleDown(removeEdge.queuePosition);
        
        removeEdge.queuePosition = -1;
    }
    
    public void bubbleUp(Edge edge){
        bubbleUp(edge.queuePosition);
    }
    
    private int bubbleUp(int position){
        if (position == 0) return 0;
        if (queue.get(position).compareTo(queue.get((position-1)/2)) == 1){
            Edge temp = queue.get(position);
            queue.set(position, queue.get((position-1)/2));
            queue.get(position).queuePosition = position;
            queue.set((position-1)/2, temp);
            temp.queuePosition = (position-1)/2;
            bubbleUp((position-1)/2);
        }
        return position;
    }
    
    public void bubbleDown(Edge edge){
        bubbleDown(edge.queuePosition);
    }
    
    private int bubbleDown(int position){
        int winner = position;
        
        if (position*2 + 1 < queue.size()){
            if (queue.get(position*2 + 1).compareTo(queue.get(position)) == 1){
                winner = position*2 + 1;
                if (position*2 + 2 < queue.size()){
                    if (queue.get(position*2 + 2).compareTo(queue.get(position*2 + 1)) == 1){
                        winner = position*2 + 2;
                    }
                }
            }
            else{
                if (position*2 + 2 < queue.size()){
                    if (queue.get(position*2 + 2).compareTo(queue.get(position)) == 1){
                        winner = position*2 + 2;
                    }
                }
            }    
        }
        
        if (winner == position){ return winner;}
        
        Edge temp = queue.get(winner);
        queue.set(winner, queue.get(position));
        queue.set(position, temp);
        queue.get(winner).queuePosition = winner;
        queue.get(position).queuePosition = position;
        
        return bubbleDown(winner);
    }
    
    public void printQueue(){
        System.out.println("QUEUE:");
        for (Edge x: this.queue){
            x.printEdge();
        }
    }
    
}
