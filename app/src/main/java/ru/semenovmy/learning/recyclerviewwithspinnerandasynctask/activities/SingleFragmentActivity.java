package ru.semenovmy.learning.recyclerviewwithspinnerandasynctask.activities;

import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import ru.semenovmy.learning.recyclerviewwithspinnerandasynctask.R;

public abstract class SingleFragmentActivity extends AppCompatActivity {

    protected abstract Fragment getFragment();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.container);

        if (savedInstanceState == null) {
            getSupportFragmentManager()
                    .beginTransaction()
                    .replace(R.id.fragment_container, getFragment())
                    .commit();
        }
    }
}
