/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ObliqueImproved;

import java.io.FileWriter;
import java.util.ArrayList;

/**
 *
 * @author ASUS
 */
public class main {
    
    private static FileWriter fw; 
    
    public static class TestThread extends Thread {
        
        public void run(){
            ArrayList<Integer> presetDegreeLimits = new ArrayList<>();
            presetDegreeLimits.add(0);
            presetDegreeLimits.add(0);
            presetDegreeLimits.add(0);
            presetDegreeLimits.add(0);
            /*presetDegreeLimits.add(2);
            presetDegreeLimits.add(2);
            presetDegreeLimits.add(1);*/
            
            
            presetDegreeLimits.add(3); //7
            presetDegreeLimits.add(2); //8
            presetDegreeLimits.add(1); //9
            presetDegreeLimits.add(1); //10
            presetDegreeLimits.add(1); //11
            presetDegreeLimits.add(1); //12
            
            /*Graph g = new Graph(21, 10, presetDegreeLimits);
            ArrayList<ResultGraph> result = g.obliqueSearchRestricted();*/
            Graph h = new Graph(8);
            h.obliqueSearch();
            /*for (ResultGraph rg: result){
                rg.printGraph();
            }*/
        }
    }
    
    public static void main(String[] args) {
        
        try{
            fw = new FileWriter("log.txt");
        }
        catch(Exception e){
        
        }
        
        TestThread tt = new TestThread();
        tt.run();
        try{
            Thread.sleep(20);
        }
        catch(Exception e){
        
        }
        tt.stop();
        try{
            fw.close();
        }
        catch(Exception e){}
        
        
         
    }
}
