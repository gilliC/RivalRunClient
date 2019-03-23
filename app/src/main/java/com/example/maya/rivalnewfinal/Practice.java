package com.example.maya.rivalnewfinal;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

public class Practice extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_practice);

        Toast.makeText(getApplicationContext(), "Coming soon....",
                Toast.LENGTH_SHORT).show();
        Intent intent = new Intent(Practice.this, Zone.class);
        startActivity(intent);
    }

    @Override
    public void onBackPressed() {
    }
}
