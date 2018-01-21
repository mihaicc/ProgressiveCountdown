package com.hootsuite.mihaic.progressivecountdown;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.Button;
import android.view.View;
import android.util.Log;
import android.widget.EditText;
import android.widget.TextView;
import android.os.SystemClock;
import android.text.format.DateUtils;

public class ProgressiveCountdown extends AppCompatActivity {

    static Integer SECONDS_IN_A_MIN = 1;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_progressive_countdown);

        final Button button = findViewById(R.id.btn_start);

        button.setOnClickListener(new View.OnClickListener() {

            public Long now(){
                return  System.currentTimeMillis()/1000;
            }
            public String timeLeft(Long endTime){
                Long seconds_left = endTime - this.now();
                return DateUtils.formatElapsedTime(seconds_left);
            }

            public void onClick(View v) {
                Integer startMinutes = Integer.parseInt(((EditText)findViewById(R.id.edt_Minutes)).getText().toString());
                Integer startSeconds = startMinutes * SECONDS_IN_A_MIN;
                Log.v("Test", startMinutes.toString());


                Long startTime = this.now();
                Long endTime = startTime + startSeconds;
                TextView timer = (TextView)findViewById(R.id.timeRemaining);
                while (endTime > this.now()){
                    SystemClock.sleep(1000);
                    timer.setText(this.timeLeft(endTime));
                };


                EditText rem = (EditText)findViewById(R.id.edt_Remaining);
                rem.setText(String.valueOf(startMinutes * 60));
            }
        });
    }
}
