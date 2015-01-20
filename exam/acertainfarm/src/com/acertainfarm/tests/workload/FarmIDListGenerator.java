package com.acertainfarm.tests.workload;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/**
 * Created by tudorgk on 20/1/15.
 */
public class FarmIDListGenerator {

    public List<Integer> sampleIDList (List<Integer> fieldIdList, int numberOfSubsetElements){
        if(numberOfSubsetElements>fieldIdList.size()){
            //if the list number is bigger than the list size return the entire list
            return fieldIdList;
        }

        if (numberOfSubsetElements< 0){
            //if the list number is smaller than 0, return an empty list
            return new ArrayList<Integer>();
        }

        ArrayList<Integer> originalArray = new ArrayList<Integer> (fieldIdList);
        ArrayList<Integer> subsetArray = new ArrayList<Integer>(numberOfSubsetElements);

        /* Fill in subset array with first part of original array */
        for(int i=0;i<numberOfSubsetElements;i++){
            subsetArray.add(i,originalArray.get(i));
        }

		/*Go through the rest of the array*/
        for(int i=numberOfSubsetElements;i<originalArray.size();i++){
            int k = new Random().nextInt(i+1);
            if(k<numberOfSubsetElements){
                subsetArray.set(k, originalArray.get(i));
            }
        }

        return subsetArray;

    }
}
