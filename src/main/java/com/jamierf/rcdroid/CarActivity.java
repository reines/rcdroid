package com.jamierf.rcdroid;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;

public class CarActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Set the layout
        this.setContentView(R.layout.main);

        // Start the car service
        this.startService(new Intent(this, CarService.class));
    }
}
