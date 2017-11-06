package com.fmvvv.wmc;

import java.util.ArrayList;
import java.util.Random;

public class ScaleSelect {
    private int RT = 0;
    private ArrayList<Integer> tl = new ArrayList<Integer>();
    int l1[]= {0,0,0,0,0,0,0,0,0,0,0,0};

    public void ScaleSelect(int n,float pres){

    }

    public void SetSR(int n, float pres){
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

    public int SetScale(float x, float tx,int scl){
        if(Math.abs(tx-x) > 1 ){
            if((int)(x)%6 == 0){
                return 0;
            }
            else if((int)(x)%6 == 1){
                return 1;
            }
            else if((int)(x)%6 == 2){
                return 2;
            }
            else if((int)(x)%6 == 3){
                return 3;
            }
            else if((int)(x)%6 == 4){
                return 4;
            }
            else {
                return 5;
            }
        }
        else{
            return scl;
        }
    }
}
