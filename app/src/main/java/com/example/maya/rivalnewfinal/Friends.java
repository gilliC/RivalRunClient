package com.example.maya.rivalnewfinal;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;
import org.xmlpull.v1.XmlPullParserFactory;

import java.io.IOException;
import java.io.StringReader;

public class Friends extends AppCompatActivity {
    TextView bla, textResponse;
    Button sendrequest,home;
    EditText nana;
    ClientClass mClientClass = ClientClass.getRefrence();
    SharedPreferences.Editor sp;
    SharedPreferences shp;
    String xmlMessage, serverList;
    ListView friendsList, friendsRequestList;
    ArrayAdapter friendsAdapter, friendsRequestAdapter;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_friends);
        ImageButton im = (ImageButton) findViewById(R.id.imageButton);
        bla = (TextView) findViewById(R.id.textView13);
        nana = (EditText) findViewById(R.id.runame);
        final AlertDialog alertDialog = new AlertDialog.Builder(this).create();
        textResponse = (TextView) findViewById(R.id.textView19);
        final TextView request = (TextView) findViewById(R.id.textView14);
        final TextView blabla = (TextView) findViewById(R.id.textView13);
        final TextView answer = (TextView) findViewById(R.id.runame);
        final ImageButton add = (ImageButton) findViewById(R.id.imageButton);
        sendrequest = (Button) findViewById(R.id.button);
        home = (Button) findViewById(R.id.back);



        final Button accept = new Button(this);
        final Button decline = new Button(this);


        shp = getSharedPreferences("UserData", 0);
        sp = shp.edit();

        new connectTaskS().execute("");
        xmlMessage = "<Message Type='FriendsList'><UserName>" + shp.getString("UserName", "") + "</UserName></Message>";
        mClientClass.sendMessage(xmlMessage);
        xmlMessage = "<Message Type='RequestFriendsList'><UserName>" + shp.getString("UserName", "") + "</UserName></Message>";
        mClientClass.sendMessage(xmlMessage);
        im.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                bla.setVisibility(View.VISIBLE);
                nana.setVisibility(View.VISIBLE);
                sendrequest.setVisibility(View.VISIBLE);
            }
        });
        add.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                blabla.setVisibility(View.VISIBLE);
                answer.setVisibility(View.VISIBLE);
                sendrequest.setVisibility(View.VISIBLE);
            }
        });
        home.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Friends.this, MainPage.class);
                startActivity(intent);            }
        });
        sendrequest.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!nana.getText().toString().matches("")) {
                    xmlMessage = "<Message Type='AddFriend'><UserName>" + shp.getString("UserName", "") + "</UserName><FriendUsername>" + nana.getText().toString() + "</FriendUsername></Message>";
                    mClientClass.sendMessage(xmlMessage);
                }
            }
        });
        friendsAdapter = new ArrayAdapter(this, android.R.layout.simple_list_item_1);
        friendsList = (ListView) findViewById(R.id.listView);
        friendsList.setAdapter(friendsAdapter);
        friendsRequestAdapter = new ArrayAdapter(this, android.R.layout.simple_list_item_1);
        friendsRequestList = (ListView) findViewById(R.id.listView2);
        friendsRequestList.setAdapter(friendsRequestAdapter);

        friendsList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                final String otherUserName = (String) friendsList.getItemAtPosition(position);
                alertDialog.setTitle("Delete "+otherUserName+" ?");


                alertDialog.setButton("Delete", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        xmlMessage = "<Message type='DeleteFriend'><UserName>" + shp.getString("UserName", "") + "</UserName><FriendUserName>" + otherUserName + "</FriendUserName></Message>";
                        mClientClass.sendMessage(xmlMessage);
                        alertDialog.cancel();
                    }
                });
                alertDialog.setButton2("Cancel", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {

                        alertDialog.cancel();
                    }
                });
                alertDialog.show();

            }
        });

                friendsRequestList.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> arg0, View arg1, int position, long arg3) {

                final String otherUserName = (String) friendsRequestList.getItemAtPosition(position);

                alertDialog.setTitle("Accept " + otherUserName + " ?");

                alertDialog.setButton("Decline", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        xmlMessage = "<Message type='UnAcceptFriend'><UserName>" + shp.getString("UserName", "") + "</UserName><FriendUserName>" + otherUserName + "</FriendUserName></Message>";
                        mClientClass.sendMessage(xmlMessage);
                        alertDialog.cancel();
                    }
                });
                alertDialog.setButton2("Accept", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        xmlMessage = "<Message type='AcceptFriend'><UserName>" + shp.getString("UserName", "") + "</UserName><FriendUserName>" + otherUserName + "</FriendUserName></Message>";
                        mClientClass.sendMessage(xmlMessage);
                        alertDialog.cancel();
                    }
                });
                alertDialog.show();
            }
        });


    }

    @Override
    public void onBackPressed() {
    }
    public void friendsTable() {
        int i;

            String[] friendsList = serverList.split(",");
            for (i = 0; i < friendsList.length; i++) {
                friendsAdapter.add(friendsList[i]);
            }

    }

    public void friendsRequestTable() {
        int i;

        String[] friendsList = serverList.split(",");
        for (i = 0; i < friendsList.length; i++) {
            friendsRequestAdapter.add(friendsList[i]);
        }
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
            String answerFromServer = "";
            try {
                XmlPullParserFactory factory = XmlPullParserFactory.newInstance();
                factory.setNamespaceAware(true);
                XmlPullParser xpp = factory.newPullParser();
                xpp.setInput(new StringReader(values[0]));
                int eventType = xpp.getEventType();
                while (eventType != XmlPullParser.END_DOCUMENT) {

                    if (eventType == XmlPullParser.TEXT) {
                        answerFromServer = xpp.getText();

                        // textResponse.setText(answerFromServer);
                        if (answerFromServer.matches("FriendsList")) {
                            xpp.next();
                            xpp.next();
                            xpp.next();
                            serverList = (String) xpp.getText();

                            friendsTable();
                        } else {
                            if (answerFromServer.matches("RequestFriendsList")) {
                                xpp.next();
                                xpp.next();
                                xpp.next();
                                serverList = (String) xpp.getText();

                                friendsRequestTable();
                            } else {
                                if (answerFromServer.matches("Succeded")) {
                                    textResponse.setText("Your friend request has been sent");
                                } else {

                                    if (answerFromServer.matches("No Friends Yet"))
                                        friendsList.setClickable(false);
                                    if (answerFromServer.matches("No Friends Requests"))
                                        friendsList.setClickable(false);
                                    textResponse.setText(answerFromServer);
                                    if (answerFromServer.matches("Accept") || answerFromServer.matches("UnAcceptFriend")) {
                                        Intent intent = new Intent(Friends.this, Friends.class);
                                        startActivity(intent);
                                    }
                                }
                            }
                        }
                    }
                    eventType = xpp.next();
                }
            } catch (XmlPullParserException e1) {
                e1.printStackTrace();
            } catch (IOException e1) {
                e1.printStackTrace();
            }
            Log.e("Message:", "End document");

        }


    }

}
