package com.example.mobile.view;

import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import com.example.mobile.R;
import com.google.firebase.FirebaseApp;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        FirebaseApp.initializeApp(this);

        setContentView(R.layout.activity_main);
    }
}
