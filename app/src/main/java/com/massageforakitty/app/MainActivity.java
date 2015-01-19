package com.massageforakitty.app;

import android.media.MediaPlayer;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.SystemClock;
import android.support.v7.app.ActionBarActivity;
import android.view.View;
import android.widget.*;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Random;


public class MainActivity extends ActionBarActivity {

    private TextView txtAsk;
    private ProgressBar progressBar;
    private TextView txtCurrentProgress;
    private Button btnStart;
    private ImageView imgV;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        txtAsk = (TextView)findViewById(R.id.txtAsk);
        progressBar = (ProgressBar)findViewById(R.id.progressBar);
        txtCurrentProgress = (TextView)findViewById(R.id.txtCurrentProgress);
        btnStart = (Button)findViewById(R.id.btnStart);
        imgV = (ImageView)findViewById(R.id.imgV);
    }


    //...Внутрениий класс_______________________________________________________________________________________________

    public class AsyncTaskHeir extends AsyncTask<Void, Integer, Void> {

        private final int MAX_VALUE = 600;
        private int currentProgress = 0;

        MediaPlayer mPlayer;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            Toast.makeText(getApplicationContext(), "Тогда поехали!", Toast.LENGTH_LONG).show();
            progressBar.setMax(MAX_VALUE);

            txtAsk.setText("Настал твой час =)");
            btnStart.setVisibility(View.INVISIBLE);

            imgV.setImageResource(R.drawable.img_pre);

            int num = new Random().nextInt(5) + 1;
            //mPlayer = MediaPlayer.create(getApplicationContext(), Uri.parse("R.raw.track_" + num));
            switch (num) {
                case 1:
                    mPlayer = MediaPlayer.create(getApplicationContext(), R.raw.track_1);
                    break;
                case 2:
                    mPlayer = MediaPlayer.create(getApplicationContext(), R.raw.track_2);
                    break;
                case 3:
                    mPlayer = MediaPlayer.create(getApplicationContext(), R.raw.track_3);
                    break;
                case 4:
                    mPlayer = MediaPlayer.create(getApplicationContext(), R.raw.track_4);
                    break;
                case 5:
                    mPlayer = MediaPlayer.create(getApplicationContext(), R.raw.track_5);
                    break;
            }
            mPlayer.start();
        }

        @Override
        protected Void doInBackground(Void... params) {
            while (currentProgress < MAX_VALUE - 1) {
                publishProgress(++currentProgress);
                SystemClock.sleep(1000);
            }
            return null;
        }

        @Override
        protected void onProgressUpdate(Integer... values) {
            super.onProgressUpdate(values);

            progressBar.setProgress(currentProgress);
            txtCurrentProgress.setText("Расслабление... " + getTime(currentProgress * 1000));
        }
        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            txtCurrentProgress.setText(getTime(++currentProgress * 1000));
            progressBar.setProgress(currentProgress);

            imgV.setImageResource(R.drawable.img_post);
            Toast.makeText(getApplicationContext(), "А вот и конец :)", Toast.LENGTH_LONG).show();

            TextView txtAsk = (TextView)findViewById(R.id.txtAsk);
            txtAsk.setText("Ну... Вот и все!");

            mPlayer.stop(); //Остановка меложии массажа
            mPlayer = MediaPlayer.create(getApplicationContext(), R.raw.fin);
            mPlayer.start();
        }
    }
    //__________________________________________________________________________________________________________________


    public void onStart(View v) { new AsyncTaskHeir().execute(); }

    private String getTime(int milliseconds) {
        SimpleDateFormat dateFormat = new SimpleDateFormat("mm:ss");
        return dateFormat.format(new Date(milliseconds));
    }
}