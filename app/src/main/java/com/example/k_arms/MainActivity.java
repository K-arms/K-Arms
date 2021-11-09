package com.example.k_arms;

import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.View;
import android.widget.Button;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

public class MainActivity extends AppCompatActivity implements View.OnClickListener{

    private int REQUEST_CODE_PERMISSIONS = 101;
    private final String[] REQUIRED_PERMISSIONS = new String[]{"android.permission.CAMERA", "android.permission.WRITE_EXTERNAL_STORAGE"};
    static String email;

    public static final String FIRST_START = "spKeyFirstStart";
    Button instruction;
    Button set;
    Button Zatilnik;
    Button exit;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main_activity);

        if(allPermissionsGranted()){
             //start camera if permission has been granted by user
        } else{
            ActivityCompat.requestPermissions(this, REQUIRED_PERMISSIONS, REQUEST_CODE_PERMISSIONS);
        }

        // Запустим Приветствие и демонстрацию в 1 запуск приложения
        Thread t = new Thread(new Runnable() {
            @Override
            public void run() {
                SharedPreferences getPrefs = PreferenceManager
                        .getDefaultSharedPreferences(getBaseContext());

                boolean isFirstStart = getPrefs.getBoolean(FIRST_START, true);

                if (isFirstStart) {
                    Intent i = new Intent(MainActivity.this, CustomIntro.class);
                    startActivity(i);

                    SharedPreferences.Editor e = getPrefs.edit();
                    e.putBoolean(FIRST_START, false);
                    e.apply();
                }
            }
        });

        // Start the thread
        t.start();
        // Закончили Приветствие и демонстрацию в 1 запуск приложения

// взять Емайл
        Bundle arguments = getIntent().getExtras();
        email = arguments.get("email").toString();

        instruction=findViewById(R.id.bInstruction);
        set=findViewById(R.id.bSet);
        exit=findViewById(R.id.bExit);
        Zatilnik=findViewById(R.id.bZatTSK);

        instruction.setOnClickListener(this);
        Zatilnik.setOnClickListener(this);
        set.setOnClickListener(this);
        exit.setOnClickListener(this);

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.bInstruction:
                Intent intent1 = new Intent(MainActivity.this,CustomInstruction.class);
                startActivity(intent1);
                break;
            case R.id.bSet:
                Intent intent2 = new Intent(MainActivity.this,ListTasks.class);
                intent2.putExtra("email", email);
                startActivity(intent2);
                break;
            case R.id.bExit:
                Intent intent3 = new Intent(MainActivity.this,EmailPasswordActivity.class);
                startActivity(intent3);
                break;
            case R.id.bZatTSK:
                Intent intent4 = new Intent(MainActivity.this,Zatilnik.class);
                intent4.putExtra("email", email);
                startActivity(intent4);



        }
    }

    private boolean allPermissionsGranted(){

        for(String permission : REQUIRED_PERMISSIONS){
            if(ContextCompat.checkSelfPermission(this, permission) != PackageManager.PERMISSION_GRANTED){
                return false;
            }
        }
        return true;
    }
}
