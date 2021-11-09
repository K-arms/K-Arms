package com.example.k_arms;

import android.content.Intent;
import android.os.Bundle;

import com.github.paolorotolo.appintro.AppIntro2;


public class CustomInstruction extends AppIntro2 {
    @Override
    public void init(Bundle savedInstanceState) {

// Здесь указываем количество слайдов, например нам нужно 3
        addSlide(SampleSlide.newInstance(R.layout.instr_1)); //
        addSlide(SampleSlide.newInstance(R.layout.instr_2));
        addSlide(SampleSlide.newInstance(R.layout.instr_3));
        addSlide(SampleSlide.newInstance(R.layout.instr_4));
        addSlide(SampleSlide.newInstance(R.layout.instr_5));
        addSlide(SampleSlide.newInstance(R.layout.instr_6));
        addSlide(SampleSlide.newInstance(R.layout.instr_11));
        addSlide(SampleSlide.newInstance(R.layout.instr_7));
        addSlide(SampleSlide.newInstance(R.layout.instr_8));
        addSlide(SampleSlide.newInstance(R.layout.instr_9));
        addSlide(SampleSlide.newInstance(R.layout.instr_12));
        addSlide(SampleSlide.newInstance(R.layout.instr_13));
        addSlide(SampleSlide.newInstance(R.layout.instr_10));



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
