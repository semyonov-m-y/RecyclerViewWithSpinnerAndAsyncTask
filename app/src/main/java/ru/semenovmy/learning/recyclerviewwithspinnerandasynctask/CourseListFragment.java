package ru.semenovmy.learning.recyclerviewwithspinnerandasynctask;

import android.os.AsyncTask;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import ru.semenovmy.learning.recyclerviewwithspinnerandasynctask.adapters.CourseAdapter;
import ru.semenovmy.learning.recyclerviewwithspinnerandasynctask.adapters.LectorSpinnerAdapter;
import ru.semenovmy.learning.recyclerviewwithspinnerandasynctask.model.Lecture;

public class CourseListFragment extends Fragment {

    public static final int POSITION_ALL = 0;
    private CourseAdapter mCourseAdapter;
    private RecyclerView mRecyclerView;
    private Spinner mLectorSpinner;
    private Spinner mWeekSpinner;
    private List<Lecture> mLectures = new ArrayList<>();
    private List<String> mLectors;
    private int mLectorPosition;
    private int mWeekGroupStatus;
    private LecturesTask mLecturesTask;
    private View mLoadingView;
    private CourseListProvider mCourseListProvider = new CourseListProvider();

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_course_list, container, false);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        mLoadingView = view.findViewById(R.id.loading_view);
        mRecyclerView = view.findViewById(R.id.recycler);
        mLectorSpinner = view.findViewById(R.id.lectors_spinner);
        mWeekSpinner = view.findViewById(R.id.weeks_spinner);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        List<Lecture> lectures = mCourseListProvider.getLectures();
        if (lectures == null) {
            mLecturesTask = new LecturesTask(this, savedInstanceState == null);
            mLecturesTask.execute();
        } else {
            initRecyclerView(savedInstanceState == null, lectures);
            initLectorsSpinner();
            initWeekSpinner();
        }
    }

     private void initRecyclerView(boolean isFirstCreate, List<Lecture> lectures) {
        LinearLayoutManager layoutManager = new LinearLayoutManager(requireContext(), RecyclerView.VERTICAL, false);
        mRecyclerView.setLayoutManager(layoutManager);
        mCourseAdapter = new CourseAdapter(getResources());
        mCourseAdapter.setLectures(lectures);
        DividerItemDecoration dividerItemDecoration = new DividerItemDecoration(getContext(), DividerItemDecoration.VERTICAL);
        mRecyclerView.addItemDecoration(dividerItemDecoration);
        mRecyclerView.setAdapter(mCourseAdapter);

        if (isFirstCreate) {
            Date date = new Date();
            Lecture nextLecture = mCourseListProvider.getLectureByDate(date);
            int nextLecturePosition = mCourseAdapter.getLecturePosition(nextLecture);
            if (nextLecturePosition != -1) {
                mRecyclerView.scrollToPosition(nextLecturePosition);
            }
        }
    }

    private void initLectorsSpinner() {
        mLectors = mCourseListProvider.provideLectors();
        Collections.sort(mLectors);
        mLectors.add(POSITION_ALL, getResources().getString(R.string.all));
        mLectorSpinner.setAdapter(new LectorSpinnerAdapter(mLectors));

        mLectorSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int position, long l) {
                mLectorPosition = position;
                setGroupStatus();
                setLectures(mLectorPosition);
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });
    }

    private void initWeekSpinner() {
        ArrayAdapter<?> adapter = ArrayAdapter.createFromResource(getContext(), R.array.group_status, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        mWeekSpinner.setAdapter(adapter);

        mWeekSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int position, long l) {
                mWeekGroupStatus = position;

                setGroupStatus();
                setLectures(mLectorPosition);
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });
    }

    private void setGroupStatus() {
        if (mWeekGroupStatus == 0) {
            mCourseAdapter.setGroupStatus(0);
        } else {
            mCourseAdapter.setGroupStatus(1);
        }
    }

    public void setLectures(int lectorPosition) {
        if (lectorPosition == POSITION_ALL) {
            mLectures = mCourseListProvider.getLectures();
        } else {
            mLectures = mCourseListProvider.lectureFilterBy(mLectors.get(mLectorPosition));
        }
        mCourseAdapter.setLectures(mLectures);
    }

    private static class LecturesTask extends AsyncTask<Void, Void, List<Lecture>> {

        private WeakReference<CourseListFragment> mFragmentWeakReference;
        CourseListProvider mProvider;
        private final boolean mIsFirstCreate;

        private LecturesTask(CourseListFragment fragment, boolean isFirstCreate) {
            mFragmentWeakReference = new WeakReference<>(fragment);
            mProvider = fragment.mCourseListProvider;
            mIsFirstCreate = isFirstCreate;
        }

        @Override
        protected void onPreExecute() {
            CourseListFragment fragment = mFragmentWeakReference.get();
            if (fragment != null) {
                fragment.mLoadingView.setVisibility(View.VISIBLE);
            }
        }

        @Override
        protected List<Lecture> doInBackground(Void... voids) {
            return mProvider.getLecturesFromWeb();
        }

        @Override
        protected void onPostExecute(List<Lecture> lectures) {
            CourseListFragment fragment = mFragmentWeakReference.get();
            if (fragment == null) {
                return;
            }
            fragment.mLoadingView.setVisibility(View.GONE);
            if (lectures == null) {

            } else {
                fragment.initRecyclerView(mIsFirstCreate,lectures);
                fragment.initLectorsSpinner();
                fragment.initWeekSpinner();
            }
        }
    }
}
