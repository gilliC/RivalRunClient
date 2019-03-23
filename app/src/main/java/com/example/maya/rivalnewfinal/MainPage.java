package com.example.maya.rivalnewfinal;

import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.app.AlertDialog;
import android.widget.Toast;


import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserFactory;

import java.io.StringReader;
import java.util.logging.Logger;

public class MainPage extends AppCompatActivity {
    String userNameS;
    ClientClass mClientClass;
    String answerFromServer;
    SharedPreferences shp;
    SharedPreferences.Editor sp;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_page);

        new connectTask().execute("");
        shp = getSharedPreferences("UserData", 0);
        sp = shp.edit();


        Button zone = (Button) findViewById(R.id.userZONE);
        Button competition = (Button) findViewById(R.id.competition);
        Button champions = (Button) findViewById(R.id.champions);
        Button settings = (Button) findViewById(R.id.settings);
        Button register = (Button) findViewById(R.id.reg);
        Button logOut = (Button) findViewById(R.id.logOut);
        Button sign = (Button) findViewById(R.id.sign);


        String value = shp.getString("UserName", "");

////////////////////////////////////////////// if the user already signed in
        if (!value.matches("")) {
            register.setVisibility(View.INVISIBLE);
            logOut.setVisibility(View.VISIBLE);
            sign.setVisibility(View.INVISIBLE);
            logOut.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    sp.putString("UserName", null);
                    sp.commit();
                    Intent intent = new Intent(MainPage.this, MainPage.class);
                    startActivity(intent);
                }
            });


        } /////////////////////////// listener to sign in button
        else
        {
            zone.setVisibility(View.INVISIBLE);
            competition.setVisibility(View.INVISIBLE);
            settings.setVisibility(View.INVISIBLE);
            champions.setVisibility(View.INVISIBLE);

        }
        sign.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                //alertDialog.show();
                Log.e("bla1","bla1");
                Intent intent = new Intent(MainPage.this, SignIn.class);
                Log.e("bla2","bla2");
                startActivity(intent);
                Log.e("bla3", "bla3");
            }
        });


        zone.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainPage.this, Zone.class);
                startActivity(intent);
            }
        });
        competition.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainPage.this, Competition.class);
                startActivity(intent);

            }
        });
        champions.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainPage.this, Champions.class);
                startActivity(intent);
            }
        });
        settings.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainPage.this, Settings.class);
                startActivity(intent);
            }
        });
        register.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainPage.this, Register.class);
                // mClientClass.stopClient();
                startActivity(intent);
            }
        });


    }

    @Override
    public void onBackPressed() {
    }
    @Override
    protected void onDestroy() {
        super.onDestroy();
        mClientClass.stopClient();
    }


    protected class connectTask extends AsyncTask<String, String, ClientClass> {
        boolean isRun = true;

        @Override
        protected ClientClass doInBackground(String... message) {

            //we create a TCPClient object and
            try {
                mClientClass = new ClientClass(new ClientClass.OnMessageReceived() {
                    @Override
                    //here the messageReceived method is implemented
                    public void messageReceived(String message) {
                        publishProgress(message);
                    }
                }, getApplicationContext());

                mClientClass.run();
            }
            catch (Exception e)
            {

            }

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
                        Log.e("EventType", xpp.getText());

                        answerFromServer = xpp.getText();
                    }
                    eventType = xpp.next();
                }
                //alertDialog.setMessage(answerFromServer);
                if (answerFromServer.matches("Succeeded")) {
                    sp.putString("UserName", userNameS);
                    sp.commit();
                    Intent intent = new Intent(MainPage.this, MainPage.class);
                    startActivity(intent);
                }

                Log.e("Message:", "End document");
            } catch (Exception e) {
                // textResponse.setText("NOT WORKING");
            }
            if (isRun == false) {
                //mClientClass.stopClient();
                // alertDialog.setMessage("CLOSED");
            }
        }


    }
}
