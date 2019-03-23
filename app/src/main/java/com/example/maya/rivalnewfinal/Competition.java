package com.example.maya.rivalnewfinal;


import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserFactory;

import java.io.StringReader;

public class Competition extends AppCompatActivity {
    com.example.maya.rivalnewfinal.ClientClass mClientClass = ClientClass.getRefrence();
    String messegeOnCompetitionType = "<Message type='Competition'><Type>";
    String competitionTypeReal;
    TextView textResponse;
    Button timeC, distanceC, backTMenu;
    SharedPreferences.Editor sp;
    SharedPreferences shp;
    GpsTracker gp;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_competition);
        gp = new GpsTracker(this);

        if (!gp.canGetLocation()) {
            Toast.makeText(getApplicationContext(), "There has been a problem. check if your GPS is on",
                    Toast.LENGTH_SHORT).show();
            Intent intent = new Intent(Competition.this, MainPage.class);
            startActivity(intent);

        } else {
            shp = getSharedPreferences("UserData", 0);
            sp = shp.edit();

            new connectTaskS().execute("");

            textResponse = (TextView) findViewById(R.id.textView20);
            timeC = (Button) findViewById(R.id.timeComp);
            distanceC = (Button) findViewById(R.id.disComp);
            backTMenu = (Button) findViewById(R.id.back);

            backTMenu.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    String xml = "<Message><Type>"+competitionTypeReal+"</Type><UserName>"+shp.getString("UserName", "")+"</UserName></Message>";
                    mClientClass.sendMessage(xml);
                    Intent intent = new Intent(Competition.this, MainPage.class);
                    startActivity(intent);

                }
            });

            timeC.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    messegeOnCompetitionType += "Time</Type><UserName>" + shp.getString("UserName", "") + "</UserName></Message>";
                    mClientClass.sendMessage(messegeOnCompetitionType);
                    competitionTypeReal = "Time";
                    textResponse.setText("Request for a rival has been sent");
                }
            });
            distanceC.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    competitionTypeReal = "Distance";

                    Intent intent = new Intent(Competition.this, DistanceCompetition.class);
                    startActivity(intent);
                }
            });
            }
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
                textResponse.setText("Searching for rival");
                XmlPullParserFactory factory = XmlPullParserFactory.newInstance();
                factory.setNamespaceAware(true);
                XmlPullParser xpp = factory.newPullParser();

                xpp.setInput(new StringReader(values[0]));
              //  textResponse.setText(values[0]);
                int eventType = xpp.getEventType();

                while (eventType != XmlPullParser.END_DOCUMENT) {
                    if (eventType == XmlPullParser.TEXT) {
                        String answer = xpp.getText();
                        if (answer.matches("AnswerFound")) {
                            textResponse.setText("RivalFound");
                            xpp.next();
                            xpp.next();
                            xpp.next();
                            String rivalName = (String) xpp.getText();

                            sp.putString("RivalName", rivalName);
                            sp.commit();

                            xpp.next();
                            xpp.next();
                            xpp.next();
                            xpp.next();
                            String rivalLevel = (String) xpp.getText();
                            xpp.next();
                            xpp.next();
                            xpp.next();

                            String userLevel = (String) xpp.getText();

                            sp.putString("RivalLevel", rivalLevel);
                            sp.commit();
                            sp.putString("UserLevel", userLevel);
                            sp.commit();
                            textResponse.setText(rivalName + "  " + rivalLevel);
                            Intent intent = new Intent(Competition.this, TimeCompetition.class);
                            startActivity(intent);
                        } else {
                            if (answer.matches("AnswerNotFound")) {
                                textResponse.setText("We coulnd't fing a rival\n if you want to try again, press the competition type again");

                            }
                            else textResponse.setText(answer);


                        }
                    }
                    eventType = xpp.next();
                }

            } catch (Exception e) {

                textResponse.setText(e.getMessage().toString() + "\n" + values[0]);
            }
            if (isRun == false) {
                //mClientClass.stopClient();
                //textResponse.setText("CLOSED");
            }
        }

    }

}
