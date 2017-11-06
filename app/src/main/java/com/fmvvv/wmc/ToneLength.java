package com.fmvvv.wmc;

import java.util.ArrayList;
import java.util.Random;

public class ToneLength {
    private int TL = 0;     //音長
    private float Sum = 0; //センサの値の和
    private int Isum = 0;
    private ArrayList<Integer> tl = new ArrayList<Integer>();
    int l1[]= {0,0,0,0};
    boolean MBOver = false;

    public ToneLength(){

    }

    public void SetLR(int n,float pres){
        Random rand = new Random((int)pres);
        boolean num[] = new boolean[n];

        for(int i = 0; i<n;i++){
            num[i] = false;
        }
        for(int i = 0; i<50; i++){
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

    public int SetLength(int l, float x, float y, float z){
        Sum = Math.abs(x+y+z);
        Isum = (int)Sum%4;
        if(Isum == l1[0]){
            TL = 48;
        }
        else if(Isum == l1[1]){
            TL = 24;
        }
        else if(Isum == l1[2]){
            TL = 12;
        }
        else{
            TL = 8;
        }
        if(l + TL > 96){
            TL = 96 - l;
            MBOver = true;
        }
        return TL;
    }

    public int CheckOver(int a){
        if(MBOver){
            MBOver = false;
            return 0;
        }
        else {
            return a;
        }
    }
}
