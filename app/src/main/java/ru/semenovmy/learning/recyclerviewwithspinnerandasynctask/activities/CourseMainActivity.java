package ru.semenovmy.learning.recyclerviewwithspinnerandasynctask.activities;

import androidx.fragment.app.Fragment;

import ru.semenovmy.learning.recyclerviewwithspinnerandasynctask.CourseListFragment;

public class CourseMainActivity extends SingleFragmentActivity {

    @Override
    protected Fragment getFragment() {
        return new CourseListFragment();
    }
}
