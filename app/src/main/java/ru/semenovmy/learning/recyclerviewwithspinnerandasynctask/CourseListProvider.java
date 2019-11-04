package ru.semenovmy.learning.recyclerviewwithspinnerandasynctask;

import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.net.URLConnection;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Set;
import ru.semenovmy.learning.recyclerviewwithspinnerandasynctask.model.Lecture;

public class CourseListProvider {

    public static final String DATE_FORMAT = "dd.MM.yyyy";
    public List<Lecture> mLectures;

    public List<Lecture> getLectures() {
        return mLectures == null ? null : new ArrayList<>(mLectures);
    }

    public Lecture getLectureByDate(Date date) {
        SimpleDateFormat format = new SimpleDateFormat(DATE_FORMAT, Locale.getDefault());
        for (Lecture lecture : mLectures) {
            try {
                Date lectureDate = format.parse(lecture.getDate());
                if (lectureDate != null && lectureDate.after(date)) {
                    return lecture;
                }
            } catch (ParseException e) {
                e.printStackTrace();
            }
        }
        return mLectures.get(mLectures.size() - 1);
    }

    public List<String> provideLectors() {
        Set<String> lectorSet = new HashSet<>();
        for (Lecture lecture : mLectures) {
            lectorSet.add(lecture.getLector());
        }
        return new ArrayList<>(lectorSet);
    }

    public List<Lecture> lectureFilterBy(String lectorName) {
        List<Lecture> result = new ArrayList<>();
        for (Lecture lecture : mLectures) {
            if (lecture.getLector().equals(lectorName)) {
                result.add(lecture);
            }
        }
        return new ArrayList<>(result);
    }

    public List<Lecture> getLecturesFromWeb() {
        if (mLectures != null) {
            return mLectures;
        }

        InputStream is = null;
        try {
            final URL url = new URL("http://landsovet.ru/learning_program.json");
            URLConnection connection = url.openConnection();
            is = connection.getInputStream();
            ObjectMapper mapper = new ObjectMapper();
            Lecture[] lectures = mapper.readValue(is, Lecture[].class);
            mLectures = Arrays.asList(lectures);
            return new ArrayList<>(mLectures);
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (is != null) {
                try {
                    is.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        return null;
    }
}
