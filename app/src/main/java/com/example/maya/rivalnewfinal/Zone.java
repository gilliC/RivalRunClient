package com.example.maya.rivalnewfinal;

import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserFactory;

import java.io.StringReader;

public class Zone extends AppCompatActivity {
    SharedPreferences shp;
    SharedPreferences.Editor sp;
    Button friends, practice, details,home;
    TextView textResponse, welcome;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_zone);

        shp = getSharedPreferences("UserData", 0);
        sp = shp.edit();

        String userName = shp.getString("UserName", "");
        if (!userName.matches("")) {

            welcome = (TextView) findViewById(R.id.thewelcome);
            textResponse = (TextView) findViewById(R.id.textView12);
            friends = (Button) findViewById(R.id.fr);
            practice = (Button) findViewById(R.id.pr);
            details = (Button) findViewById(R.id.det);
            home = (Button) findViewById(R.id.back);


            welcome.setText("Welcome " + userName);

            home.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(Zone.this, MainPage.class);
                    startActivity(intent);
                }
            });
            friends.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(Zone.this, Friends.class);
                    startActivity(intent);

                }
            });
            practice.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(Zone.this, Practice.class);
                    startActivity(intent);

                }
            });
            details.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent=new Intent(Zone.this,Details.class);
                    startActivity(intent);
                }
            });
            try {
                XmlPullParserFactory factory = XmlPullParserFactory.newInstance();
                factory.setNamespaceAware(true);
                XmlPullParser xpp = factory.newPullParser();
                String x = "<Message><Type>AnswerFound</Type></Message>";
                textResponse.setText(x);
                xpp.setInput(new StringReader(x));
                int eventType = xpp.getEventType();


                while (eventType != XmlPullParser.END_DOCUMENT) {
                    String answer = xpp.getText();
                    if (eventType == XmlPullParser.TEXT) {
                        if (answer.matches("AnswerFound")) {
                            textResponse.setText("RivalFound");
                            String rivalName = (String) xpp.getProperty("RivalUserName");
                            String rivalLevel = (String) xpp.getProperty("RivalLevel");
                            textResponse.setText(rivalName + "  " + rivalLevel);
                        }
                    }
                    eventType = xpp.next();
                }
            } catch (Exception e) {
            }
        } else {

            Toast.makeText(getApplicationContext(), "You need to sign in", Toast.LENGTH_SHORT).show();
            Intent intent = new Intent(Zone.this, MainPage.class);
            startActivity(intent);

        }
    }
    @Override
    public void onBackPressed() {
    }


}
