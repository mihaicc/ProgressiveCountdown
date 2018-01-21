package com.hootsuite.mihaic.progressivecountdown;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.widget.Button;
import android.view.View;
import android.util.Log;
import android.widget.EditText;
import android.widget.TextView;
import android.text.format.DateUtils;
import android.speech.tts.TextToSpeech;
import java.util.LinkedList;
import java.util.Timer;
import java.util.Locale;
import java.util.TimerTask;

public class ProgressiveCountdown extends AppCompatActivity {

    static Integer SECONDS_IN_A_MIN = 60;
    TextView timeRem;
    Handler handler;
    Handler mainHandler;
    Timer timer=new Timer();
    Runnable r;
    Long startTime, endTime;
    LinkedList alerts;
    TextToSpeech tts;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_progressive_countdown);
        tts=new TextToSpeech(this, new TextToSpeech.OnInitListener() {

            @Override
            public void onInit(int status) {
                // TODO Auto-generated method stub
                if(status == TextToSpeech.SUCCESS){
                    int result=tts.setLanguage(Locale.US);
                    if(result==TextToSpeech.LANG_MISSING_DATA ||
                            result==TextToSpeech.LANG_NOT_SUPPORTED){
                        Log.e("error", "This Language is not supported");
                    }
                }
                else
                    Log.e("error", "Initilization Failed!");
            }
        });
        tts.setLanguage(Locale.US);
        handler = new Handler(callback);
        timeRem = (TextView) findViewById(R.id.timeRemaining);
        final Button button = findViewById(R.id.btn_start);
        mainHandler = new Handler();

        r = new Runnable() {
            @Override
            public void run() {
                Integer startMinutes = Integer.parseInt(((EditText)findViewById(R.id.edt_Minutes)).getText().toString());
                Integer startSeconds = startMinutes * SECONDS_IN_A_MIN;

                endTime = startTime + startSeconds;
                timer.schedule(new SmallDelay(this.timeLeft(endTime)), 100);
                Long now = this.now();

                //Continue counting
                if (endTime >= now+1) {
                    handler.postDelayed(r, 1000);
                }
            }

            public Long now(){
                return  System.currentTimeMillis()/1000;
            }
            public Long timeLeft(Long endTime){
                Long secondsLeft = endTime - this.now();

                Log.v("Test", secondsLeft.toString());
                return secondsLeft;
            }

        };

        button.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                // Define the moment the user started the timer;
                startTime = System.currentTimeMillis()/1000;
                // Define the alert levels
                alerts = new LinkedList<Float>();
                alerts.add(Float.parseFloat(((EditText)findViewById(R.id.edt_Alert1)).getText().toString()));
                alerts.add(Float.parseFloat(((EditText)findViewById(R.id.edt_Alert2)).getText().toString()));
                alerts.add(Float.parseFloat(((EditText)findViewById(R.id.edt_Alert3)).getText().toString()));

                //TODO: A reset starts another handler
                mainHandler.postDelayed(r, 100);
            }
        });
    }

    public Handler.Callback callback = new Handler.Callback() {
        public boolean handleMessage(Message msg) {
            Long timeLeft = msg.getData().getLong("msg");
            timeRem.setText(DateUtils.formatElapsedTime(timeLeft));
            sendNotificationIfNeeded(timeLeft);
            return true;
        }
        public void sendNotificationIfNeeded(Long timeLeft){
            if (alerts.isEmpty() || !(timeLeft>0)){
                return;
            }
            Float alertThreshold = (Float)alerts.peek();
            Long totalTime = endTime-startTime;
            if (timeLeft.floatValue()/totalTime*100 < alertThreshold) {
                alerts.pop();
                String timeForNotif;
                if (timeLeft > 60){
                    timeForNotif = timeLeft/60 + " min";
                } else {
                    timeForNotif = timeLeft + " sec";
                }
                tts.speak(timeForNotif, TextToSpeech.QUEUE_ADD, null, "1234");
            }
        }
    };
    class SmallDelay extends TimerTask {
        Long msg;
        public SmallDelay(Long s) {
            msg = s;
        }

        public void run() {
            Message m = Message.obtain();
            Bundle b = new Bundle();
            b.putLong("msg", msg);
            m.setData(b);
            handler.sendMessage(m);
        }
    }
}