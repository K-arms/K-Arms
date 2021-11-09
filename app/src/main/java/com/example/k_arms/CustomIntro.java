package com.example.k_arms;

import android.content.Intent;
import android.os.Bundle;

import com.github.paolorotolo.appintro.AppIntro2;

public class CustomIntro extends AppIntro2 {
    @Override
    public void init(Bundle savedInstanceState) {

// Здесь указываем количество слайдов, например нам нужно 3
        addSlide(SampleSlide.newInstance(R.layout.intro_1)); //


    }

    private void loadMainActivity(){
        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);
    }


    @Override
    public void onNextPressed() {
        // Do something here
    }

    @Override
    public void onDonePressed() {
        finish();
    }

    @Override
    public void onSlideChanged() {
        // Do something here
    }

}
