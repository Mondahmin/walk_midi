package com.fmvvv.wmc;

import android.content.Context;
import android.graphics.Color;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Environment;
import android.os.Handler;
import android.os.PowerManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import android.text.SpannableStringBuilder;
import android.text.format.Time;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.TabHost;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;
import java.io.File;
import java.util.Random;
import java.util.Timer;
import java.util.TimerTask;

import jp.kshoji.javax.sound.midi.Sequence;
import jp.kshoji.javax.sound.midi.MetaMessage;
import jp.kshoji.javax.sound.midi.Track;
import jp.kshoji.javax.sound.midi.MidiEvent;
import jp.kshoji.javax.sound.midi.MidiSystem;
import jp.kshoji.javax.sound.midi.ShortMessage;

public class MainActivity extends AppCompatActivity implements View.OnClickListener,SensorEventListener {
    //Layout系
    private EditText TmpEdit;
    private Button StartButton,SaveButton,TmpButton;
    private TextView nowScale,nowRoot,nowOctave,nowTempo,TmpError,Generating;
    private ListView RootList,ScaleList,OctaveList;
    //ListView用
    private static final String[] Scales = new String[]{"メジャー","ナチュラルマイナー","メロディックマイナー","ドリアン","リディアン","フリジアン"};
    private static final String[] Roots = new String[]{"C","C#","D","D#","E","F","F#","G","G#","A","A#","B"};
    private static final String[] Octaves = new String[]{"C0 B0","C1 B1","C2 B2","C3 B3","C4 B4","C5 B5","C6 B6","C7 B7","C8 B8"};

    //MIDI用パラメータ
    private int Tempo = 120;
    private int Velocity = 100;
    private int channel = 0;
    private int Instrument = 0;
    private int Octave = 60;
    private int Root = 0;
    private boolean first1 = false;
    private boolean first2 = false;
    private float gravSum1 = 0;
    private int Scale [][] = { {-5,-3,-1,0,2,4,5,7,9,11,12,14,16},{-5,-4,-2,0,2,3,5,7,8,10,12,14,15},
                               {-5,-3,-1,0,2,3,5,7,9,11,12,14,15},{-5,-3,-2,0,2,3,5,7,9,10,12,14,15},
                               {-5,-3,-1,0,2,4,6,7,9,11,12,14,16},{-5,-4,-2,0,1,3,5,7,8,10,12,13,15}};
    private int scl = 0;
    private float gravSum2 = 0;
    private int Tone = 0;
    private boolean Rest = false;
    private boolean Ref = false;
    private Sequence sequence;
    private Track track0;
    private int Length = 0; //音長
    private int AL = 0; //全体の長さ
    private int MB = 0; //一小節

    private int RANDN = 1115;

    //乱数
    private int R1 = 9; //選音 7 + 休符の数
    private int R2 = 2; //加速度、磁気の選択
    private int R3 = 4; //音価用
    private int R4 = 5; //メロディパターン用
    private int R5 = 6; //スケール
    private int R6 = 12; //ルート音
    private int R7 = 10; //オクターブ

    private int mp = 0; //メロディパターン

//boolean
    private boolean fixScale = false;//Scale固定の有無
    private boolean fixRoot = false;//Root固定の有無
    private boolean fixOctave = false;//Octave固定の有無
    private boolean fixTempo = false;//Tempo固定の有無
    private boolean GenerateState = false;//生成状態
    //センサ
    private boolean accEnabled = false; //加速度センサの有無
    private boolean magEnabled = false; //磁気センサの有無
    private boolean presEnabled = false; //圧力センサの有無
    private boolean gravEnabled = false; //重力加速度センサの有無
    private boolean gyroEnabled = false; //ジャイロセンサの有無
    private boolean rotEnabled = false; //回転軌道センサの有無
    private boolean stepEnabled = false; //Step_Detectorの有無
//    private boolean prxEnabled = false; //近接センサの有無
//    private boolean prxWorking = false; //近接センサの動作

    //センサ
    private SensorManager SenManager;
    private List<Sensor> Senlist;
    private float[] accValues = new float[3];//加速度
    private float[] magValues = new float[3];//磁気
    private float[] presValues = new float[1];//圧力
    private float[] gravValues = new float[3];//重力加速度
    private float[] gyroValues = new float[3];//ジャイロ
    private float[] rotValues = new float[3];//回転軌道センサ
    private float[] stepdetect = new float[1];//歩数
    private int stepDcount = 0; //歩数格納
//    private float[] prxValue = new float[3];//近接センサ

    private Time time = new Time();

    /*
    //ProximityWakeLock
    private PowerManager pm;
    private PowerManager.WakeLock wl;
    */
    //class
    ToneLength TL = new ToneLength();
    SelectNote SN = new SelectNote();
    MelodyPattern MP = new MelodyPattern();
    RootSelect RT = new RootSelect();
    ScaleSelect SL = new ScaleSelect();
    OctaveChange OT = new OctaveChange();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
     //Layout
        setContentView(R.layout.activity_main);
        //textView
        nowScale = (TextView) findViewById(R.id.scale) ;
        nowRoot = (TextView) findViewById(R.id.root) ;
        nowOctave = (TextView) findViewById(R.id.octave) ;
        nowTempo = (TextView) findViewById(R.id.tempo) ;
        TmpError = (TextView) findViewById(R.id.tempoError);
        TmpError.setVisibility(View.INVISIBLE);
        Generating = (TextView) findViewById(R.id.Generating);
        Generating.setVisibility(View.INVISIBLE);
        nowScale.setOnClickListener(this);
        nowRoot.setOnClickListener(this);
        nowOctave.setOnClickListener(this);
        nowTempo.setOnClickListener(this);
        //EditText
        TmpEdit = (EditText)findViewById(R.id.TempoEdit);
        //ListView
        ScaleList =(ListView) findViewById(R.id.ScaleList);
        RootList =(ListView) findViewById(R.id.RootList);
        OctaveList =(ListView) findViewById(R.id.OctaveList);
        //Button
        TmpButton = (Button) findViewById(R.id.tempoSet);
        StartButton = (Button) findViewById(R.id.StartButton);
        SaveButton = (Button) findViewById(R.id.SaveButton);
        TmpButton.setOnClickListener(this);
        StartButton.setOnClickListener(this);
        SaveButton.setOnClickListener(this);

/*
        //Proximity.WakeLock
        pm = (PowerManager) getSystemService(Context.POWER_SERVICE);
        wl = pm.newWakeLock(PowerManager.PROXIMITY_SCREEN_OFF_WAKE_LOCK, "tag");
*/
        //センサマネージャ
        SenManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
        Senlist = SenManager.getSensorList(Sensor.TYPE_ACCELEROMETER);
        SenManager.registerListener(this,Senlist.get(0),(int) 1e10);

        try{
            sequence = new Sequence(Sequence.PPQ,24);
            track0 = sequence.createTrack();
        }catch(Exception e) {
        }


        TabHost mTabHost =  (TabHost) findViewById(R.id.tabhost);
        mTabHost.setup();

        TabHost.TabSpec  Spec = mTabHost.newTabSpec("tab1");
        Spec.setIndicator("Root");
        Spec.setContent(R.id.RootTab);
        mTabHost.addTab(Spec);

        Spec = mTabHost.newTabSpec("tab2");
        Spec.setIndicator("Scale");
        Spec.setContent(R.id.ScaleTab);
        mTabHost.addTab(Spec);

        Spec = mTabHost.newTabSpec("tab3");
        Spec.setIndicator("Octave");
        Spec.setContent(R.id.OctaveTab);
        mTabHost.addTab(Spec);

        Spec = mTabHost.newTabSpec("tab4");
        Spec.setIndicator("Tempo");
        Spec.setContent(R.id.TempoTab);
        mTabHost.addTab(Spec);


        //Adapterの作成
        ListAdapter ScaleAdapter = new ArrayAdapter<String>(this,android.R.layout.simple_list_item_1,Scales);
        ListAdapter RootAdapter = new ArrayAdapter<String>(this,android.R.layout.simple_list_item_1,Roots);
        ListAdapter OctaveAdapter = new ArrayAdapter<String>(this,android.R.layout.simple_list_item_1,Octaves);

        //Adapterの設定
        ScaleList.setAdapter(ScaleAdapter);
        RootList.setAdapter(RootAdapter);
        OctaveList.setAdapter(OctaveAdapter);

        //ScaleListのClickイベント
        ScaleList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if(GenerateState == false) {
                    ListView list = (ListView) parent;
                    String msg = (String) list.getItemAtPosition(position);
                    nowScale.setText(msg);
                    if (position == 0) {
                        scl = 0;
                    } else if (position == 1) {
                        scl = 1;
                    } else if (position == 2) {
                        scl = 2;
                    } else if (position == 3) {
                        scl = 3;
                    } else if (position == 4) {
                        scl = 4;
                    } else if (position == 5) {
                        scl = 5;
                    } else if (position == 6) {
                        scl = 6;
                    } else if (position == 7) {
                        scl = 7;
                    } else if (position == 8) {
                        scl = 8;
                    } else if (position == 9) {
                        scl = 9;
                    } else if (position == 10) {
                        scl = 10;
                    } else if (position == 11) {
                        scl = 11;
                    }
                }

            }
        });
        //RootListのClickイベント
        RootList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if(GenerateState == false) {
                    ListView list = (ListView) parent;
                    String msg = (String) list.getItemAtPosition(position);
                    nowRoot.setText(msg);
                    if (position == 0) {
                        Root = 0;
                    } else if (position == 1) {
                        Root = 1;
                    } else if (position == 2) {
                        Root = 2;
                    } else if (position == 3) {
                        Root = 3;
                    } else if (position == 4) {
                        Root = 4;
                    } else if (position == 5) {
                        Root = 5;
                    } else if (position == 6) {
                        Root = 6;
                    } else if (position == 7) {
                        Root = 7;
                    } else if (position == 8) {
                        Root = 8;
                    } else if (position == 9) {
                        Root = 9;
                    } else if (position == 10) {
                        Root = 10;
                    } else if (position == 11) {
                        Root = 11;
                    }
                }
            }
        });
        //OctaveListのClickイベント
        OctaveList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if(GenerateState == false) {
                    ListView list = (ListView) parent;
                    String msg = (String) list.getItemAtPosition(position);
                    nowOctave.setText(msg);
                    if(position == 0){
                        Octave = 12;
                    } else if (position == 1){
                        Octave = 24;
                    }else if (position == 2){
                        Octave = 36;
                    }else if (position == 3){
                        Octave = 48;
                    }else if (position == 4){
                        Octave = 60;
                    }else if (position == 5){
                        Octave = 72;
                    }else if (position == 6){
                        Octave = 84;
                    }else if (position == 7){
                        Octave = 96;
                    }else if (position == 8){
                        Octave = 108;
                    }
                }
            }
        });
        //TempoButtonのClickイベント
        TmpButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SpannableStringBuilder sp = (SpannableStringBuilder)TmpEdit.getText();
                String str = sp.toString();
                Tempo = Integer.parseInt(str);
                if(1<Tempo && Tempo<300){
                    nowTempo.setText(str);
                    TmpError.setVisibility(View.INVISIBLE);
                }
                else{
                    TmpEdit.setText("");
                    TmpError.setVisibility(View.VISIBLE);
                }
            }
        });

    }

    @Override
    public void onClick(View v) {
        if(GenerateState == false) {
            if (v.equals(nowScale)) {
                if (fixScale) {
                    fixScale = false;
                    nowScale.setTextColor(Color.BLACK);
                } else {
                    fixScale = true;
                    nowScale.setTextColor(Color.parseColor("#ffa500"));
                }
            }
            if (v.equals(nowRoot)) {
                if (fixRoot) {
                    fixRoot = false;
                    nowRoot.setTextColor(Color.BLACK);
                } else {
                    fixRoot = true;
                    nowRoot.setTextColor(Color.parseColor("#ffa500"));
                }
            }
            if (v.equals(nowOctave)) {
                if (fixOctave) {
                    fixOctave = false;
                    nowOctave.setTextColor(Color.BLACK);
                } else {
                    fixOctave = true;
                    nowOctave.setTextColor(Color.parseColor("#ffa500"));
                }
            }
            if (v.equals(nowTempo)) {
                if (fixTempo) {
                    fixTempo = false;
                    nowTempo.setTextColor(Color.BLACK);
                } else {
                    fixTempo = true;
                    nowTempo.setTextColor(Color.parseColor("#ffa500"));

                }
            }
        }
        if(v.equals(StartButton)){
            if(GenerateState){
                Log.d("hoge","Stop");
                GenerateState = false;
                StartButton.setText("START");
                Generating.setVisibility(View.INVISIBLE);
                Log.d("hoge","Stoppp");
            }
            else{
                Log.d("hoge","start");
                GenerateState = true;
                StartButton.setText("STOP");
                Generating.setVisibility(View.VISIBLE);
/*
                TL.SetLR(R3,presValues[0]);
                SN.SetNR(presValues[0]);
                MP.SetMR(R4,presValues[0]);
                RT.SetRR(R6,presValues[0]);
                SL.SetSR(R5,presValues[0]);
                OT.SetOR(R7,presValues[0]);
*/
                TL.SetLR(R3,RANDN);
                SN.SetNR(RANDN);
                MP.SetMR(R4,RANDN);
                RT.SetRR(R6,RANDN);
                SL.SetSR(R5,RANDN);
                OT.SetOR(R7,RANDN);
                first1 = true;
                first2 = true;
                Log.d("hoge","started");
                }
            }
        if(v.equals(SaveButton)){
            Log.d("hoge","save");
                //Log.d("hoge","Saving");
                time.setToNow();
                String jikan = new String(time.format2445());
                File dir = new File(Environment.getExternalStorageDirectory(),"midi");
                if (!dir.exists()) dir.mkdir();

                try {
                    Log.d("hoge", "mae");
                    MidiSystem.write(sequence, 0, new java.io.File(Environment.getExternalStorageDirectory() + "/midi/" + "Midi_" + jikan + ".mid"));
                    Log.d("hoge1", "usiro");
                    sequence.deleteTrack(track0);
                    SaveButton.setText("Saved");
                } catch (Exception e) {

                }


            Log.d("hoge","saved");
        }
    }

    @Override
    public void onSensorChanged(SensorEvent event) {
        if (event.accuracy == SensorManager.SENSOR_STATUS_UNRELIABLE) return;

        //センサーの種別の取得
        int type = event.sensor.getType();

        //加速度センサーの情報の格納
        if (type == Sensor.TYPE_ACCELEROMETER) {
            accValues = event.values.clone();
        }
        //地磁気センサーの情報の格納
        if (type == Sensor.TYPE_MAGNETIC_FIELD) {
            magValues = event.values.clone();
        }
        //回転軌道センサ
        if (type == Sensor.TYPE_ROTATION_VECTOR) {
            rotValues = event.values.clone();
        }
        //重力加速度センサーの情報の格納
        if (type == Sensor.TYPE_GRAVITY) {
            gravValues = event.values.clone();
        }
        //ジャイロセンサーの情報の格納
        if (type == Sensor.TYPE_GYROSCOPE) {
            gyroValues = event.values.clone();
        }
        //圧力センサ
        if (type == Sensor.TYPE_PRESSURE) {
            presValues = event.values.clone();
        }
        //step_detector
        else if (type == Sensor.TYPE_STEP_DETECTOR) {
            stepdetect = event.values.clone();
            stepDcount++;
            if (stepDcount > 10) {
                if(first1){
                    first1 = false;
                    gravSum1 = gravValues[0];
                    gravSum2 = gravValues[1] + gravValues[2];
                }
                if (stepDcount % 2 == 0) {
                    if (MB == 0) {
                        //メロディパターン
                        mp = MP.SetMP(gyroValues[0], gyroValues[1], gyroValues[2]);
                        if(first2) {
                            first2 = false;
                        }else{
                            //スケール
                            if (fixScale = false) {
                                scl = SL.SetScale(gravValues[1]+gravValues[2],gravSum2,scl);
                            }
                            //ルート音
                            if (fixRoot = false) {
                                Root = RT.SetRoot(gravValues[0],gravSum1,Root);
                            }
                        }
                    }
                    //選音
                    Tone = SN.SetTone(mp, MB, Tone, accValues[0], accValues[1], accValues[2]);
                    //Ref = MP.RefCheck();
                    if (Ref) {
                    } else {
                        Rest = SN.RestCheck();
                        if (Rest) {
                            Rest = false;
                            Velocity = 0;
                        }
                        //上昇，下降，ジグザグ
                        if (mp == 1 || mp == 2 || mp == 3) {
                            //オクターブ切り替え
                            if (Tone == 0) {
                                if (Octave > 23) {
                                    Tone = 7;
                                    Octave -= 12;
                                }
                            } else if (Tone == 1) {
                                if (Octave > 24) {
                                    Tone = 8;
                                    Octave -= 12;
                                }
                            } else if (Tone == 2) {
                                if (Octave > 24) {
                                    Tone = 9;
                                    Octave -= 12;
                                }
                            }
                            if (Tone == 11) {
                                if (Octave < 107) {
                                    Tone = 4;
                                    Octave += 12;
                                }
                            } else if (Tone == 12) {
                                if (Octave < 107) {
                                    Tone = 5;
                                    Octave += 12;
                                }
                            } else if (Tone == 13) {
                                if (Octave < 107) {
                                    Tone = 5;
                                    Octave += 12;
                                }
                            }
                        }
                        //オクターブ
                        if(fixOctave = false){
                            Octave += OT.ChangeOct(accValues[0]+magValues[0]);
                            }

                        //音長
                        Length = TL.SetLength(MB, magValues[0], magValues[1], magValues[2]);
                        MB += Length;
                        MB = TL.CheckOver(MB);

                        try {
                            Log.d("hoge", "add");
                            MetaMessage mmessage = new MetaMessage();
                            int l = 60 * 1000000 / Tempo;
                            mmessage.setMessage(0x51, new byte[]{(byte) (l / 65536), (byte) (l % 65536 / 256), (byte) (l % 256)}, 3);
                            track0.add(new MidiEvent(mmessage, AL));
                            //set instrument
                            ShortMessage message = new ShortMessage();
                            message.setMessage(ShortMessage.PROGRAM_CHANGE, 0, Instrument, 0);
                            track0.add(new MidiEvent(message, AL));
                            // Note on
                            message = new ShortMessage();
                            message.setMessage(ShortMessage.NOTE_ON, channel, Octave + Root + Scale[scl][Tone], Velocity);
                            track0.add(new MidiEvent(message, AL));
                            // Note off
                            message = new ShortMessage();
                            message.setMessage(ShortMessage.NOTE_OFF, channel, Octave + Root + Scale[scl][Tone], Velocity);
                            track0.add(new MidiEvent(message, AL + Length));
                            Log.d("hoge", "added");
                        } catch (Exception e) {
                        }

                        //書き込み後の処理
                        AL += Length;
                        Velocity = 100;
                        gravSum1 = gravValues[0];
                        gravSum2 = gravValues[1] + gravValues[2];

                    }
                }

            }

        }
        //近接センサ
/*
        else if (type == Sensor.TYPE_PROXIMITY){
            prxValue = event.values.clone();
            if(prxValue[0] == 0 && GenerateState){
                wl.acquire();
                prxWorking = true;
            }
            else{
                //  wl.release();
                prxWorking = false;
            }
        }
*/
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int i) {

    }

    @Override
    protected void onResume() {
        super.onResume();
        List<Sensor> sensors = SenManager.getSensorList(Sensor.TYPE_ALL);
        for (Sensor sensor : sensors) {
            int type = sensor.getType();
            //加速度センサのリスナー登録
            if(type == Sensor.TYPE_ACCELEROMETER){
                SenManager.registerListener(this,sensor,SensorManager.SENSOR_DELAY_NORMAL);
                accEnabled = true;
            }
            //地磁気センサのリスナー登録
            if(type == Sensor.TYPE_MAGNETIC_FIELD){
                SenManager.registerListener(this,sensor,SensorManager.SENSOR_DELAY_NORMAL);
                magEnabled = true;
            }
            //回転軌道センサのリスナー登録
            if(type == Sensor.TYPE_ROTATION_VECTOR){
                SenManager.registerListener(this,sensor,SensorManager.SENSOR_DELAY_NORMAL);
                rotEnabled = true;
            }
            //重力加速度センサのリスナー登録
            if(type == Sensor.TYPE_GRAVITY){
                SenManager.registerListener(this,sensor,SensorManager.SENSOR_DELAY_NORMAL);
                gravEnabled = true;
            }
            //ジャイロセンサのリスナー登録
            if(type == Sensor.TYPE_GYROSCOPE){
                SenManager.registerListener(this,sensor,SensorManager.SENSOR_DELAY_NORMAL);
                gyroEnabled = true;
            }
            //気圧センサのリスナー登録
            if(type == Sensor.TYPE_PRESSURE){
                SenManager.registerListener(this,sensor,SensorManager.SENSOR_DELAY_NORMAL);
                presEnabled = true;
            }
            //step_detectorのリスナ登録
            if (type == Sensor.TYPE_STEP_DETECTOR) {
                SenManager.registerListener(this, sensor, SensorManager.SENSOR_DELAY_NORMAL);
                stepEnabled = true;
            }
/*
            //近接センサのリスナ登録
            if (type == Sensor.TYPE_PROXIMITY) {
                SenManager.registerListener(this, sensor, SensorManager.SENSOR_DELAY_NORMAL);
                prxEnabled = true;
            }
*/
        }
    }

    protected void onPause() {
        super.onPause();
/*
        //センサのリスナー解除
        if(gravEnabled){
            SenManager.unregisterListener(this);
            gravEnabled = false;
        }
        if(accEnabled){
            SenManager.unregisterListener(this);
            accEnabled = false;
        }
        if(presEnabled){
            SenManager.unregisterListener(this);
            presEnabled = false;
        }
        if(magEnabled){
            SenManager.unregisterListener(this);
            magEnabled = false;
        }
        if(stepEnabled){
            SenManager.unregisterListener(this);
            stepEnabled = false;
        }
        if(gyroEnabled){
            SenManager.unregisterListener(this);
            gyroEnabled = false;
        }
        if(rotEnabled){
            SenManager.unregisterListener(this);
            rotEnabled = false;
        }

        if(prxEnabled){
            SenManager.unregisterListener(this);
            prxEnabled = false;
        }
*/
    }
    protected void onDestroy(){
        super.onDestroy();
    }

}
