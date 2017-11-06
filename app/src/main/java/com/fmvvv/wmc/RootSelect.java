package com.fmvvv.wmc;

import java.util.ArrayList;
import java.util.Random;

public class RootSelect {
    private int RT = 0;
    private ArrayList<Integer> tl = new ArrayList<Integer>();
    int l1[]= {0,0,0,0,0,0,0,0,0,0,0,0};

    public void RootSelect(int n,float pres){

    }

    public void SetRR(int n, float pres){
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

    public int SetRoot(float x,float tx, int root){
        if(Math.abs(tx-x) > 1){
            if((int)(x)%12 == 0){
                return 0;
            }
            else if((int)(x)%12 == 1){
                return 1;
            }
            else if((int)(x)%12 == 2){
                return 2;
            }
            else if((int)(x)%12 == 3){
                return 3;
            }
            else if((int)(x)%12 == 4){
                return 4;
            }
            else if((int)(x)%12 == 5){
                return 5;
            }
            else if((int)(x)%12 == 6){
                return 6;
            }
            else if((int)(x)%12 == 7){
                return 7;
            }
            else if((int)(x)%12 == 8){
                return 8;
            }
            else if((int)(x)%12 == 9){
                return 9;
            }
            else if((int)(x)%12 == 10){
                return 10;
            }
            else {
                return 11;
            }
        }
        else{
            return root;
        }
    }
}
