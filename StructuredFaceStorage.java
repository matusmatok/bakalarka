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
public class StructuredFaceStorage {    
    private ArrayList<ArrayList<ArrayList<MyBool>>> arrayStruct;
    int maxVertDegree;
    
    private class MyBool{
        public boolean val;
        public MyBool(boolean val) {
            this.val = val;
        }
        
    }

    public StructuredFaceStorage(int maxVertDegree) {
        this.maxVertDegree = maxVertDegree;
        this.arrayStruct = new ArrayList<>(maxVertDegree + 1);
        
        for(int i=0; i <= maxVertDegree; i++){
            if (i < 3){
                this.arrayStruct.add(null);
            }
            else{
                this.arrayStruct.add(new ArrayList<>(maxVertDegree + 1));
                ArrayList<ArrayList<MyBool>> tempLevelOneNode = this.arrayStruct.get(i);
                
                for(int j=0; j <= maxVertDegree; j++){
                    if (j < 3){
                        tempLevelOneNode.add(null);
                    }
                    else{
                        if (i <= j){
                            tempLevelOneNode.add(new ArrayList<>(maxVertDegree + 1));
                            ArrayList<MyBool> tempLevelTwoNode = tempLevelOneNode.get(j);
                            
                            for(int k=0; k <= maxVertDegree; k++){
                                if (k < 3){
                                    tempLevelTwoNode.add(null);
                                }
                                else{
                                    if (k >= i && k >= j){ //create
                                        tempLevelTwoNode.add(new MyBool(true));
                                    }
                                    else{
                                        // k < aspon od 1 
                                        if (i < j) {
                                            tempLevelTwoNode.add(this.arrayStruct.get(i).get(k).get(j));
                                        }
                                        else{
                                            tempLevelTwoNode.add(this.arrayStruct.get(j).get(k).get(i));
                                        }
                                        //tempLevelTwoNode.add(this.arrayStruct.get(k).get(i).get(j));
                                    }
                                }
                            }
                        }
                        else{
                            tempLevelOneNode.add(arrayStruct.get(j).get(i));
                        }
                    }
                }
            }
        }        
        
    }   
    
    public boolean useFace(int i, int j, int k){
        assert(i >= 3 && j >= 3 && k >= 3 && i <= maxVertDegree && j <= maxVertDegree && k <= maxVertDegree);
        
        if (this.arrayStruct.get(i).get(j).get(k).val == true){
            this.arrayStruct.get(i).get(j).get(k).val = false;
            return true;
        }
        return false;
    }
    
    public boolean unuseFace(int i, int j, int k){
        assert(i >= 3 && j >= 3 && k >= 3 && i <= maxVertDegree && j <= maxVertDegree && k <= maxVertDegree);
        
        if (this.arrayStruct.get(i).get(j).get(k).val == false){
            this.arrayStruct.get(i).get(j).get(k).val = true;
            return true;
        }
        return false;        
    }
    
    public boolean isFaceUsed(int i, int j, int k){
        assert(i >= 3 && j >= 3 && k >= 3 && i <= maxVertDegree && j <= maxVertDegree && k <= maxVertDegree);
        if (!(i >= 3 && j >= 3 && k >= 3 && i <= maxVertDegree && j <= maxVertDegree && k <= maxVertDegree)) return false;
        return (this.arrayStruct.get(i).get(j).get(k).val == false);
    }
    
    public int howManyAvailable(int i, int j){
        int sum = 0;
        for(MyBool face : this.arrayStruct.get(i).get(j)){
            sum += face == null ? 0 : face.val == true ? 1 : 0;
        }
        return sum;
    }
    
    // public STH getAllFacesWithDegreeOf(int degree)
}
