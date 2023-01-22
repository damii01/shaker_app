package com.kraljic.MMOS;

import static java.lang.Thread.sleep;

import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.os.ProcessCompat;

import android.content.SharedPreferences;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity {

    private double prethodnaJacina = 0;
    private Integer brojKoraka = 0;
    private TextView timerTextView;
    private ProgressBar timerProgressBar;
    private CountDownTimer countDownTimer;
    private ConstraintLayout c;
    private TextView textView;
    private Button buttonStartTime;
    private Button buttonStopTime;
    private ProgressBar mProgressBar1;
    private ProgressBar mProgressBar;
    private TextView textViewShowTime;
    private TextView textForce;
    private EditText edtTimerValue;

    private long secLeft = 1;
    private Boolean pobjeda = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        textView = findViewById(R.id.textView);
        textView.setTextColor(getResources().getColor(R.color.cardview_dark_background));
        c = findViewById(R.id.constraint);
        c.setBackgroundColor(getResources().getColor(R.color.cardview_light_background));
        buttonStartTime = (Button) findViewById(R.id.button_timerview_start);
        buttonStopTime = (Button) findViewById(R.id.button_timerview_stop);
        textViewShowTime = (TextView)
                findViewById(R.id.textView_timerview_time);
        textForce = (TextView)
                findViewById(R.id.textJace);
        textForce.setVisibility(View.GONE);
        edtTimerValue = (EditText) findViewById(R.id.textview_timerview_back);
        edtTimerValue.setVisibility(View.INVISIBLE);
        mProgressBar = (ProgressBar) findViewById(R.id.progressbar_timerview);
        mProgressBar1 = (ProgressBar) findViewById(R.id.progressbar1_timerview);
    }

    public void startT(View v) {
        c.setBackgroundColor(getResources().getColor(R.color.cardview_light_background));
        pobjeda = false;
        brojKoraka = 0;
        textView = findViewById(R.id.textView);
        if(secLeft >= 0 || !pobjeda) {
            SensorManager sm = (SensorManager) getSystemService(SENSOR_SERVICE);
            Sensor s = sm.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
            SensorEventListener korak = new SensorEventListener() {
                @Override
                public void onSensorChanged(SensorEvent sensorEvent) {
                    if (sensorEvent != null && !pobjeda) {
                        float x_acc = sensorEvent.values[0];
                        float y_acc = sensorEvent.values[1];
                        float z_acc = sensorEvent.values[2];

                        double jacina = Math.sqrt(x_acc * x_acc + y_acc * y_acc + z_acc * z_acc);
                        double razlikaJacine = jacina - prethodnaJacina;
                        prethodnaJacina = jacina;
                        if (brojKoraka >= 10 || (secLeft <= 0 && brojKoraka >= 10)) {
                            textView.setText("Pobjeda!");
                            pobjeda = true;
                            buttonStartTime.setVisibility(View.INVISIBLE);
                            c.setBackgroundColor(getResources().getColor(R.color.pobjeda));
                            buttonStartTime.setVisibility(View.VISIBLE);
                        } else {
                            if (razlikaJacine > 40) {
                                brojKoraka ++;
                            }

                            textView.setText(brojKoraka.toString());
                        }

                    }
                }

                @Override
                public void onAccuracyChanged(Sensor sensor, int i) {

                }
            };
            sm.registerListener(korak, s, SensorManager.SENSOR_DELAY_GAME);
            if(!pobjeda) {
                setTimer();
                buttonStartTime.setVisibility(View.INVISIBLE);
                mProgressBar.setVisibility(View.INVISIBLE);

                startTimer();
                mProgressBar1.setVisibility(View.VISIBLE);
            }

        }
    }
    private int totalTimeCountInMilliseconds = 0;
    private void setTimer(){
        int time = 10;
        totalTimeCountInMilliseconds =  time * 1000;
        mProgressBar1.setMax( time * 1000);
    }
    protected void startTimer() {
        countDownTimer = new CountDownTimer(totalTimeCountInMilliseconds, 1) {

            @Override
            public void onTick(long leftTimeInMilliseconds) {
                int seconds = (int)leftTimeInMilliseconds / 1000;
                int hundredth = (int) (leftTimeInMilliseconds / 10) % 10;
                int tenth = (int)((leftTimeInMilliseconds / 100) % 10);
                secLeft = leftTimeInMilliseconds;
                mProgressBar1.setProgress((int) (leftTimeInMilliseconds));
                textViewShowTime.setText(String.format("%2d", seconds % 60)
                        + ":" + String.format("%d", tenth)+""+ String.format("%d", hundredth));
                Log.d("LeftMil",  String.format("%d", leftTimeInMilliseconds));
                if (brojKoraka >= 10 || (secLeft <= 0 && brojKoraka >= 10)) {
                    onFinish();
                }
               /* if(secLeft <= 20 && brojKoraka < 10)
                    textViewMessage.setText("Jače shajkajte mobitel, dosegnite 10 za pobjedu");*/
            }
            @Override
            public void onFinish() {
                textViewShowTime.setText("10:00");
                textViewShowTime.setVisibility(View.VISIBLE);
                buttonStartTime.setVisibility(View.VISIBLE);
                buttonStopTime.setVisibility(View.INVISIBLE);
                mProgressBar.setVisibility(View.VISIBLE);
                mProgressBar1.setVisibility(View.GONE);
                countDownTimer.cancel();
            }
        }.start();
    }

    protected void onStop() {

        super.onStop();
        SharedPreferences sp = getPreferences(MODE_PRIVATE);
        SharedPreferences.Editor e = sp.edit();
        e.clear();
        //e.putInt("brojKoraka", brojKoraka);
        e.apply();
    }

    protected void onPause() {

        super.onPause();
        SharedPreferences sp = getPreferences(MODE_PRIVATE);
        SharedPreferences.Editor e = sp.edit();
        e.clear();
        //e.putInt("brojKoraka", brojKoraka);
        e.apply();
    }

    protected  void onResume() {

        super.onResume();
        SharedPreferences sp = getPreferences(MODE_PRIVATE);
        //brojKoraka = sp.getInt("brojKoraka", 0);
    }

}