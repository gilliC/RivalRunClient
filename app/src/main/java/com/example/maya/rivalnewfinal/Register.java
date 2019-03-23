package com.example.maya.rivalnewfinal;

import java.io.StringReader;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.app.Activity;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserFactory;


public class Register extends Activity {


    Button buttonSend, home;
    EditText userName, name, password, repassword, birthDate, country;
    String answerFromServer;
    ClientClass mClientClass = ClientClass.getRefrence();
    SharedPreferences.Editor sp;
    SharedPreferences shp;
    TextView textResponse;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        shp = getSharedPreferences("UserData", 0);
        sp = shp.edit();

        new connectTaskS().execute("");

        buttonSend = (Button) findViewById(R.id.send);
        userName = (EditText) findViewById(R.id.userName);
        textResponse = (TextView) findViewById(R.id.notes);
        name = (EditText) findViewById(R.id.name);
        password = (EditText) findViewById(R.id.password);
        repassword = (EditText) findViewById(R.id.repassword);
        birthDate = (EditText) findViewById(R.id.birthDate);
        home = (Button) findViewById(R.id.back);
        country = (EditText) findViewById(R.id.country);
        home.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Register.this, MainPage.class);
                startActivity(intent);
            }
        });
        buttonSend.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                String note = "", xmlAnswer = "<Message type='Registry'>";
                //  XmlSerializer
                if (!(userName.getText().toString().matches("")))
                    xmlAnswer += "<UserName>" + userName.getText().toString() + "</UserName>";
                else note += "You need to enter user name \n";
                if (!(name.getText().toString().matches("")))
                    xmlAnswer += "<Name>" + name.getText().toString() + "</Name>";
                else note += "You need to enter name \n";
                if (!(password.getText().toString().matches("")))
                    if (!(repassword.getText().toString().matches("")))
                        if (password.getText().toString().matches(repassword.getText().toString()))
                            xmlAnswer += "<Password>" + password.getText().toString() + "</Password>";
                        else note += "The passwords are not match \n";
                    else note += "You need to enter the password \n";
                else note += "You need to enter the password \n";
                if (!(country.getText().toString().matches("")))
                    xmlAnswer += "<Country>" + country.getText().toString() + "</Country>";
                else note += "You need to enter country \n";
                if (!(birthDate.getText().toString().matches("")))
                    xmlAnswer += "<BirthDate>" + birthDate.getText().toString() + "</BirthDate>";
                xmlAnswer += "</Message>";
                if (!(note.matches("")))
                    Toast.makeText(Register.this, note, Toast.LENGTH_LONG).show();
                else {

                    //sends the message to the server

                    if (mClientClass != null) {
                        mClientClass.sendMessage(xmlAnswer);
                    }
                }

            }
        });

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
                    //   if (eventType == XmlPullParser.START_TAG)
                    //if (eventType == XmlPullParser.START_TAG)
                    //str += xpp.getName() + " :";
                    if (eventType == XmlPullParser.TEXT) {
                        Log.e("EventType", xpp.getText());
                        // if (xpp.getText() == "CLOSING")
                        //   isRun = false;
                        answerFromServer = xpp.getText();
                    }
                    eventType = xpp.next();
                }
                textResponse.setText(answerFromServer);
                if (answerFromServer.matches("Succeeded")) {
                    sp.putString("UserName", userName.getText().toString());
                    sp.commit();
                    Intent intent = new Intent(Register.this, MainPage.class);
                    startActivity(intent);
                }
                if (answerFromServer.matches("The user name is allready exists")) {
                    Toast.makeText(getApplicationContext(), answerFromServer, Toast.LENGTH_LONG);

                    Intent intent = new Intent(Register.this, Register.class);
                    startActivity(intent);
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