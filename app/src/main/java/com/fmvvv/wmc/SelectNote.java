package com.fmvvv.wmc;

import java.util.ArrayList;
import java.util.Random;

public class SelectNote {
    private int Tone = 0;     //音長
    private float Sum = 0; //センサの値の和
    private int Isum1 = 0;
    private int Isum2 = 0; //上昇、下降用
    private boolean updown = false;
    boolean Rest = false;
    boolean Refrain = false;
//    private ArrayList<Integer> t1 = new ArrayList<Integer>();
    private ArrayList<Integer> t3 = new ArrayList<Integer>();
    int l1[] = {0, 0, 0, 0, 0, 0, 0, 0, 0};
    int l3[] = {0,0,0};
    int douon = 0;

    public SelectNote() {

    }

    public void SetNR(float pres) {
        Random rand = new Random((int) pres);
//        boolean num1[] = new boolean[n1];
        boolean num3[] = new boolean[3];
/*
        for (int i = 0; i < n1; i++) {
            num1[i] = false;
        }
        for (int i = 0; i < 50; i++) {
            int p = rand.nextInt(n1);
            if (num1[p] == false) {
                num1[p] = true;
                t1.add(p);
            }
        }
        l1[0] = t1.get(0).intValue();
        l1[1] = t1.get(1).intValue();
        l1[2] = t1.get(2).intValue();
        l1[3] = t1.get(3).intValue();
        l1[4] = t1.get(4).intValue();
        l1[5] = t1.get(5).intValue();
        l1[6] = t1.get(6).intValue();
        l1[7] = t1.get(7).intValue();
        l1[8] = t1.get(8).intValue();
*/


        for (int i = 0; i < 3; i++) {
            num3[i] = false;
        }
        for (int i = 0; i < 50; i++) {
            int p = rand.nextInt(3);
            if (num3[p] == false) {
                num3[p] = true;
                t3.add(p);
            }
        }
        for(int i = 0;i<3;i++){
            l3[i] = t3.get(i).intValue();
        }
    }

    public int SetTone(int mp,int mb ,int tn, float x, float y, float z) {
        //mp = メロディパターン, mb = 一小節、tn = 前のTone
        Sum = Math.abs(x + y + z);
        Isum1 = (int) Sum % 9;
        Isum2 = (int)Sum%4;

        //同音連打
        if (mp == 0) {
            if (mb == 0) {
/*
                if(Isum == l1[0]){douon = 0;}
                else if(Isum == l1[1]){douon = 1;}
                else if(Isum == l1[2]){douon = 2;}
                else if(Isum == l1[3]){douon = 3;}
                else if(Isum == l1[4]){douon = 4;}
                else if(Isum == l1[5]){douon = 5;}
                else if(Isum == l1[6]){douon = 6;}
                else if(Isum == l1[7]){douon = 7;}
                else if(Isum == l1[8]){douon = 8;}
*/
                if (Isum1 == 0) {
                    douon = 3;
                } else if (Isum1 == 1) {
                    douon = 4;
                } else if (Isum1 == 2) {
                    douon = 5;
                } else if (Isum1 == 3) {
                    douon = 6;
                } else if (Isum1 == 4) {
                    douon = 7;
                } else if (Isum1 == 5) {
                    douon = 8;
                } else if (Isum1 == 6) {
                    douon = 9;
                } else if (Isum1 == 7) {
                    douon = tn;
                    Rest = true;
                } else if (Isum1 == 8) {
                    douon = tn;
                    Rest = true;
                }
            }
            else {

            }
            return douon;
        }
        //上昇
        else if (mp == 1) {
            if (Isum2 == l3[0]) {
                Tone = 1 + tn;
            } else if (Isum2 == l3[1]) {
                Tone = 2 + tn;
            } else if (Isum2 == l3[2]) {
                Tone = 3 + tn;
            } else {
                Tone = tn;
                Rest = true;
            }
            return Tone;
        }
        //下降
        else if (mp == 2) {
            if (Isum2 == l3[0]) {
                Tone = tn - 1;
            } else if (Isum2 == l3[1]) {
                Tone = tn - 2;
            } else if (Isum2 == l3[2]) {
                Tone = tn - 3;
            } else {
                Tone = tn;
                Rest = true;
        }
            return Tone;
        }
        //ジグザグ
        else if (mp == 3) {
           if(updown) {
               updown = false;
               if (Isum2 == l3[0]) {
                   Tone = 1 + tn;
               } else if (Isum2 == l3[1]) {
                   Tone = 2 + tn;
               } else if (Isum2 == l3[2]) {
                   Tone = 3 + tn;
               } else {
                   Tone = tn;
                   Rest = true;
               }
               return Tone;
           }
            else{
               updown = true;
               if (Isum2 == l3[0]) {
                   Tone = tn - 1;
               } else if (Isum2 == l3[1]) {
                   Tone = tn - 2;
               } else if (Isum2 == l3[2]) {
                   Tone = tn - 3;
               } else {
                   Tone = tn;
                   Rest = true;
               }
               return Tone;
           }
        }
        //ランダム
        else {
            if (Isum1 == 0) {
                Tone = 3;
            } else if (Isum1 == 1) {
                Tone = 4;
            } else if (Isum1 == 2) {
                Tone = 5;
            } else if (Isum1 == 3) {
                Tone = 6;
            } else if (Isum1 == 4) {
                Tone = 7;
            } else if (Isum1 == 5) {
                Tone = 8;
            } else if (Isum1 == 6) {
                Tone = 9;
            } else if (Isum1 == 7) {
                Tone = 10;
            } else if (Isum1 == 8) {
                Tone = 11;
            }
            return Tone;
        }
    }

    //休符チェック
    public boolean RestCheck() {
        if(Rest) {
            Rest = false;
            return true;
        }
        else{
            return false;
        }
    }



}

