package com.fmvvv.wmc;

import java.util.ArrayList;
import java.util.Random;

public class MelodyPattern {
    private int MP = 0;
    private float Sum = 0;
    private int Isum = 0;
    private ArrayList<Integer> t2 = new ArrayList<Integer>();
    int l2[] = {0,0,0,0,0};
    private boolean Ref = false;

    public void MelodyPattern(){

    }

    public void SetMR(int n2, float pres){
        Random rand = new Random((int) pres);
        boolean num2[] = new boolean[n2];
        for (int i = 0; i < n2; i++) {
            num2[i] = false;
        }
        for (int i = 0; i < 50; i++) {
            int p = rand.nextInt(n2);
            if (num2[p] == false) {
                num2[p] = true;
                t2.add(p);
            }
        }
        for(int i = 0;i<n2;i++){
            l2[i] = t2.get(i).intValue();
        }
    }

    public int SetMP(float x,float y, float z){
        Sum = Math.abs(x+y+z);
        Isum = (int)Sum%5;
        if(Isum == l2[0]){
            MP = 0;
        }
        else if(Isum == l2[1]){
            MP = 1;
        }
        else if(Isum == l2[2]){
            MP = 2;
        }
        else if(Isum == l2[3]){
            MP = 3;
        }
        else if(Isum == l2[4]){
            MP = 4;
        }
        else{
            MP = 5;
            Ref = true;
        }

        return MP;
    }
    //反復チェック
    public boolean RefCheck(){
        if(Ref){
            Ref = false;
            return true;
        }else{
            return false;
        }
    }
}
