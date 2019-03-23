package com.example.maya.rivalnewfinal;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserFactory;

import java.io.StringReader;

public class SignIn extends AppCompatActivity {
    EditText password,userName;
    Button signIn,home;
    String answerFromServer,xmlToServer;
    ClientClass mClientClass=ClientClass.getRefrence();
    SharedPreferences.Editor sp;
    SharedPreferences shp;
    TextView textResponse;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        shp= getSharedPreferences("UserData", 0);
        sp=  shp.edit();

        new connectTaskS().execute("");

        setContentView(R.layout.activity_sign_in);
        signIn = (Button) findViewById(R.id.sign);
        userName = (EditText) findViewById(R.id.usN);
        password = (EditText) findViewById(R.id.pas);
        textResponse = (TextView)findViewById(R.id.textView7);
        home = (Button)findViewById(R.id.back);
        home.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(SignIn.this, MainPage.class);
                startActivity(intent);
            }
        });
        signIn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String usn,pass;
                usn=userName.getText().toString();
                pass=password.getText().toString();
                if (!usn.matches("")&&!pass.matches(""))
                {
                    try {
                        xmlToServer="<Message type='SignIn'><UserName>"+usn+"</UserName><Password>"+pass+"</Password></Message>";
                        mClientClass.sendMessage(xmlToServer);
                    }
                    catch (Exception e){}
                }

            }
        });

    }

    @Override
    public void onBackPressed() {
    }

    protected class connectTaskS extends AsyncTask<String, String, ClientClass> {
        boolean isRun = true;
        public connectTaskS()
        {    ClientClass.setListner(new ClientClass.OnMessageReceived() {
            @Override
            //here the messageReceived method is implemented
            public void messageReceived(String message) {
                publishProgress(message);
                //answerFromServer(message);
            }
        });}
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
                        Log.e("EventType", xpp.getText());
                        // if (xpp.getText() == "CLOSING")
                        //   if (eventType == XmlPullParser.START_TAG)
                        //if (eventType == XmlPullParser.START_TAG)
                        //str += xpp.getName() + " :";       //   isRun = false;
                        answerFromServer = xpp.getText();
                    }
                    eventType = xpp.next();
                }
                textResponse.setText(answerFromServer);
                if(answerFromServer.matches("Succeeded")) {
                    sp.putString("UserName",userName.getText().toString());
                    sp.commit();
                    Intent intent = new Intent(SignIn.this, MainPage.class);
                    startActivity(intent);
                }

                Log.e("Message:", "End document");
            } catch (Exception e) {
                Log.e("Message:",e.getMessage().toString());
                textResponse.setText("NOT WORKING");
            }
            if (isRun == false)
            {
                //mClientClass.stopClient();
                //textResponse.setText("CLOSED");
            }
        }


    }

}

