package com.example.maya.rivalnewfinal;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.util.Xml;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserFactory;

import java.io.StringReader;
import java.util.Timer;
import java.util.TimerTask;

public class TimeCompetition extends AppCompatActivity {

    ClientClass mClientClass = ClientClass.getRefrence();
    String userLevel, UserPoints, UserCountry, RivalLevel, RivalPoints, RivalCountry;
    TextView textResponse, timerView, user, rival, distanceView, answerFromServer, stam;
    SharedPreferences.Editor sp;
    SharedPreferences shp;
    Button menu;
    final Timer timerDistance = new Timer();

    GpsTracker gpsTracker;
    DistanceTracker dst;
    int x = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_time_competition);

        shp = getSharedPreferences("UserData", 0);
        sp = shp.edit();


        new connectTaskS().execute("");

        menu = (Button) findViewById(R.id.menuB);
        timerView = (TextView) findViewById(R.id.textView11);
        user = (TextView) findViewById(R.id.UserInfo);
        rival = (TextView) findViewById(R.id.RivalInfo);
        distanceView = (TextView) findViewById(R.id.Distance);
        stam = (TextView) findViewById(R.id.textView10);
        gpsTracker = new GpsTracker(TimeCompetition.this);
        dst = new DistanceTracker(TimeCompetition.this, gpsTracker);
        if (gpsTracker.isAvilable()) {
            user.setText(shp.getString("UserName", "") + "\n" + shp.getString("UserLevel", ""));
            rival.setText(shp.getString("RivalName", "") + "\n" + shp.getString("RivalLevel", ""));

            menu.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(TimeCompetition.this, MainPage.class);
                    startActivity(intent);
                }
            });


            timerDistance.schedule(new TimerTask() {
                @Override
                public void run() {
                    runOnUiThread(new Runnable() {

                        @Override
                        public void run() {
                            timerView.setVisibility(View.VISIBLE);
                            if (x <= 5) {

                                user.setVisibility(View.INVISIBLE);
                                rival.setVisibility(View.INVISIBLE);
                                distanceView.setVisibility(View.INVISIBLE);
                                stam.setVisibility(View.INVISIBLE);
                                timerView.setTextSize(30);
                                timerView.setText(String.valueOf(5 - x));
                            } else {
                                if (x <= 50) {
                                    user.setVisibility(View.VISIBLE);
                                    rival.setVisibility(View.VISIBLE);
                                    distanceView.setVisibility(View.VISIBLE);
                                    stam.setVisibility(View.VISIBLE);
                                    distanceView.setTextSize(20);
                                    if (x % 10 == 0 || x == 50) distanceView.setText(dst.updateDistance()[1]);
                                    timerView.setText(String.valueOf(50 - x));

                                } else {
                                    String xmlanswer = "<Message type = 'CompetitionEnd'><UserName>" + shp.getString("UserName", "") + "</UserName><Distance>" + dst.getDistanceSum() + "</Distance></Message>";
                                    mClientClass.sendMessage(xmlanswer);
                                    timerDistance.cancel();
                                }
                            }
                            x++;


                        }
                    });
                }
            }, 0, 1000);
            timerView.setVisibility(View.INVISIBLE);
            distanceView.setText("You ran " + dst.getDistanceSum() + " m");

        }
    }

    @Override
    protected void onStop() {
        super.onStop();
        String xmlAnswer = "<Message type='QuitCompetition'><UserName>" + shp.getString("UserName", "") + "</UserName></Message>";
        mClientClass.sendMessage(xmlAnswer);
        Intent intent = new Intent(TimeCompetition.this, MainPage.class);
        startActivity(intent);
    }
    @Override
    public void onBackPressed() {
    }

    protected class connectTaskS extends AsyncTask<String, String, ClientClass> {
        boolean isRun = true;

        public connectTaskS() {
            ClientClass.setListner(new ClientClass.OnMessageReceived() {
                @Override
                //here the messageReceived method is implemented
                public void messageReceived(String message) {
                    publishProgress(message);
                    //answerFromServer(message);
                }
            });
        }

        @Override
        protected ClientClass doInBackground(String... message) {

            //we create a TCPClient object and

            // mClientClass.run();

            return null;
        }

        @Override
        protected void onProgressUpdate(String... values) {
            super.onProgressUpdate(values);
            try {
                XmlPullParserFactory factory = XmlPullParserFactory.newInstance();
                factory.setNamespaceAware(true);
                XmlPullParser xpp = factory.newPullParser();

                xpp.setInput(new StringReader(values[0]));
                int eventType = xpp.getEventType();
                while (eventType != XmlPullParser.END_DOCUMENT) {
                    if (eventType == XmlPullParser.TEXT) {
                        if (xpp.getText().matches(shp.getString("UserName", ""))) {
                            distanceView.setVisibility(View.VISIBLE);
                            menu.setVisibility(View.VISIBLE);
                            distanceView.setTextSize(25);

                            distanceView.setText("YOU WON");
                            xpp.next();
                            xpp.next();
                            xpp.next();
                            xpp.next();
                            xpp.next();
                            xpp.next();
                            double loser = Double.parseDouble(xpp.getText());
                            int distance = (int) Math.round(dst.getDistanceSum() - loser);
                            timerView.setTextSize(25);
                            timerView.setText("You did " + distance + "m more");
                        } else {
                            if (xpp.getText().matches(shp.getString("RivalName", ""))) {
                                menu.setVisibility(View.VISIBLE);
                                distanceView.setVisibility(View.VISIBLE);
                                distanceView.setTextSize(25);
                                distanceView.setText("YOU LOST");
                                xpp.next();
                                xpp.next();
                                xpp.next();

                                double winner = Double.parseDouble(xpp.getText());
                                int distance = (int) Math.round(winner - dst.getDistanceSum());
                                timerView.setTextSize(25);
                                timerView.setText("You did " + distance + "m less");

                            } else {
                                if (xpp.getText().matches("Tie")) {
                                    menu.setVisibility(View.VISIBLE);
                                    distanceView.setVisibility(View.VISIBLE);
                                    distanceView.setTextSize(25);

                                    distanceView.setText("IT IS A TIE !!");
                                } else if (xpp.getText().matches("TheUserQuit")) {
                                    timerDistance.cancel();
                                    menu.setVisibility(View.VISIBLE);
                                    distanceView.setVisibility(View.VISIBLE);
                                    distanceView.setTextSize(10);
                                    distanceView.setText("your rival quit the game. sorry :(");


                                }

                            }


                        }
                    }

                    // String userInfo= shp.getString("UserName","")+"\n";
                    eventType = xpp.next();
                    // userInfo += "Level:"+xpp.getText();
                    //Intent intent = new Intent(TimeCompetition.this, MainPage.class);
                    // startActivity(intent);


                }


            } catch (Exception e) {
                Log.e("Message:", e.getMessage().toString());
                textResponse.setText("NOT WORKING");
            }
            if (isRun == false) {
                //mClientClass.stopClient();
                //textResponse.setText("CLOSED");
            }
        }

    }

}