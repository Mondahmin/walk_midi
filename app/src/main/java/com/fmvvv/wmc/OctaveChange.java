package com.fmvvv.wmc;

import java.util.ArrayList;
import java.util.Random;

public class OctaveChange {
    private int RT = 0;
    private ArrayList<Integer> tl = new ArrayList<Integer>();
    int l1[]= {0,0,0,0,0,0,0,0,0,0,0,0};

    public void OctaveChange(){

    }

    public void SetOR(int n, float pres){
        Random rand = new Random((int)pres);
        boolean num[] = new boolean[n];

        for(int i = 0; i<n;i++){
            num[i] = false;
        }
        for(int i = 0; i<200; i++){
            int p = rand.nextInt(n);
            if(num[p] == false){
                num[p] = true;
                tl.add(p);
            }
        }
        for(int i = 0;i<n;i++){
            l1[i] = tl.get(i).intValue();
        }
    }

    public int ChangeOct(float x){
        if((int)(x)%10 == l1[0]){
            return 12;
        }
        else if((int)(x)%10 == l1[1]){
            return 0;
        }
        else if((int)(x)%10 == l1[2]){
            return 0;
        }
        else if((int)(x)%10 == l1[3]){
            return 0;
        }
        else if((int)(x)%10 == l1[4]){
            return 0;
        }
        else if((int)(x)%10 == l1[5]){
            return 0;
        }
        else if((int)(x)%10 == l1[6]){
            return 0;
        }
        else if((int)(x)%10 == l1[7]){
            return 0;
        }
        else if((int)(x)%10 == l1[8]){
            return 0;
        }
        else{
            return -12;
        }
    }
}
