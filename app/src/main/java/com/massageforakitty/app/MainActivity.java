package com.massageforakitty.app;

import android.app.AlertDialog;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.BitmapFactory;
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

    private final int MAX_PROGRESS_VALUE = 600;
    private int currentProgressValue = 0;

    private TextView txtAsk;
    private ProgressBar progressBar;
    private TextView txtCurrentProgress;
    private Button btnStart;
    private ImageView imgV;

    private NotificationManager nm;
    private final int NOTIFICATION_ID = 26;
    private boolean SHOW_NOTIFY;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        txtAsk = (TextView) findViewById(R.id.txtAsk);
        progressBar = (ProgressBar) findViewById(R.id.progressBar);
        txtCurrentProgress = (TextView) findViewById(R.id.txtCurrentProgress);
        btnStart = (Button) findViewById(R.id.btnStart);
        imgV = (ImageView) findViewById(R.id.imgV);

        nm = (NotificationManager) getApplicationContext().getSystemService(Context.NOTIFICATION_SERVICE);
    }

    @Override
    protected void onPostResume() {
        super.onPostResume();
        nm.cancel(NOTIFICATION_ID);
        SHOW_NOTIFY = false;
    }


    @Override
    protected void onPause() {
        super.onPause();
        showNotification(MAX_PROGRESS_VALUE, currentProgressValue);
        SHOW_NOTIFY = true;
    }

    @Override
    public void onBackPressed() {
        new AlertDialog.Builder(this)
                .setTitle("Выйти из приложения?")
                .setMessage("Вы действительно хотите выйти?")
                .setNegativeButton(android.R.string.no, null)
                .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface arg0, int arg1) {
                        System.exit(0);
                    }
                }).create().show();
    }

    public void onClickButtonReady(View v) {
        new AsyncTaskHeir().execute();
    }

    public void showNotification(int maxValue, int currentValue) {

        Notification.Builder builder = new Notification.Builder(getApplicationContext());

        final Intent intent = new Intent(getApplicationContext(), MainActivity.class);
        final PendingIntent pedingIntent = PendingIntent.getActivity(getApplicationContext(), 0, intent, PendingIntent.FLAG_CANCEL_CURRENT);

        builder
                .setContentIntent(pedingIntent)
                .setSmallIcon(R.drawable.ic_launcher)
                .setLargeIcon(BitmapFactory.decodeResource(getApplication().getResources(), R.drawable.ic_launcher))
                .setTicker("Расслабление в процессе...")
                .setContentTitle(txtCurrentProgress.getText())
                .setContentText("Нажмите, чтобы перейти на главный экран.")
                .setWhen(System.currentTimeMillis())
                .setAutoCancel(true)
                .setProgress(maxValue, currentValue, false);

        Notification notification = builder.build();
        notification.flags = Notification.FLAG_ONGOING_EVENT | Notification.FLAG_SHOW_LIGHTS;

        nm.notify(NOTIFICATION_ID, notification);
    }

    private String getTime(int milliseconds) {
        SimpleDateFormat dateFormat = new SimpleDateFormat("mm:ss");
        return dateFormat.format(new Date(milliseconds));
    }

    //...Внутрениий класс_______________________________________________________________________________________________

    public class AsyncTaskHeir extends AsyncTask<Void, Integer, Void> {

        private final int _5_MINUTE = 300;
        private final int _2_MINUTE = 120;

        MediaPlayer mPlayer;
        MediaPlayer notifyPlayer;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            Toast.makeText(getApplicationContext(), "Тогда поехали!", Toast.LENGTH_LONG).show();
            progressBar.setMax(MAX_PROGRESS_VALUE);

            txtAsk.setText("Настал твой час =)");
            btnStart.setVisibility(View.INVISIBLE);

            imgV.setImageResource(R.drawable.img_pre);

            int num = new Random().nextInt(5) + 1;

            /*
             * Исходя из того, что:
             * R.raw.track_1 = 2131034113
             * R.raw.track_5 = 2131034117
             */
            mPlayer = MediaPlayer.create(getApplicationContext(), 0x7f050002 + num);
            mPlayer.start();
        }

        @Override
        protected Void doInBackground(Void... params) {
            while (currentProgressValue < MAX_PROGRESS_VALUE - 1) {
                publishProgress(++currentProgressValue);
                SystemClock.sleep(1000);
            }

            return null;
        }

        @Override
        protected void onProgressUpdate(Integer... values) {
            super.onProgressUpdate(values);

            progressBar.setProgress(currentProgressValue);
            txtCurrentProgress.setText("Расслабление... " + getTime(currentProgressValue * 1000));

            if (SHOW_NOTIFY == true) {
                showNotification(MAX_PROGRESS_VALUE, currentProgressValue);
            }

            if (MAX_PROGRESS_VALUE - currentProgressValue == _5_MINUTE) {
                notifyPlayer = MediaPlayer.create(getApplicationContext(), R.raw.left_5_min);
                notifyPlayer.start();

            }
            if (MAX_PROGRESS_VALUE - currentProgressValue == _2_MINUTE) {
                notifyPlayer = MediaPlayer.create(getApplicationContext(), R.raw.left_2_min);
                notifyPlayer.start();
            }

        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            txtCurrentProgress.setText(getTime(++currentProgressValue * 1000));
            progressBar.setProgress(currentProgressValue);

            imgV.setImageResource(R.drawable.img_post);
            Toast.makeText(getApplicationContext(), "А вот и конец :)", Toast.LENGTH_LONG).show();

            TextView txtAsk = (TextView) findViewById(R.id.txtAsk);
            txtAsk.setText("Ну... Вот и все!");

            mPlayer.stop(); //Остановка меложии массажа
            mPlayer = MediaPlayer.create(getApplicationContext(), R.raw.fin);
            mPlayer.start();
        }

    }
    //__________________________________________________________________________________________________________________
}