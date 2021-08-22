package com.example.wificrack;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

public class InputActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_input);
        Button button=(Button) findViewById(R.id.button2);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String input_message=((TextView) findViewById(R.id.input)).getText().toString();
                Intent intent=new Intent(InputActivity.this,CrackActivity.class);
                Bundle bundle=new Bundle();
                bundle.putCharSequence("input_message",input_message);
                intent.putExtras(bundle);
                startActivity(intent);
            }
        });
    }
}