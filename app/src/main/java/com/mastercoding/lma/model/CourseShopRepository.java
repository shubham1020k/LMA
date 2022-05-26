package com.mastercoding.lma.model;

import android.app.Application;
import android.os.Handler;
import android.os.Looper;

import androidx.lifecycle.LiveData;

import java.util.List;
import java.util.concurrent.Executor;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import javax.net.ssl.HandshakeCompletedEvent;

public class CourseShopRepository {

    private CategoryDAO categoryDAO;
    private CourseDAO courseDAO;

    private LiveData<List<Category>> categories;
    private LiveData<List<Course>> courses;

    public CourseShopRepository(Application application) {
        CourseDatabase courseDatabase = CourseDatabase.getInstance(application);

        categoryDAO = courseDatabase.categoryDAO();
        courseDAO = courseDatabase.courseDAO();

    }

    public LiveData<List<Category>> getCategories() {
        return categoryDAO.getAllCategories();
    }

    public LiveData<List<Course>> getCourses(int categoryId) {
        return courseDAO.getCourses(categoryId);
    }
    private void insertCategory(Category category){
        // Inserting Category
        categoryDAO.insert(category);

        ExecutorService executor = Executors.newSingleThreadExecutor();
        Handler handler = new Handler(Looper.getMainLooper());

        executor.execute(new Runnable() {
            @Override
            public void run() {
                // Inserting Categories

                categoryDAO.insert(category);
            }
        });
    }

    public void insertCourse(Course course){

        ExecutorService executor = Executors.newSingleThreadExecutor();
        Handler handler = new Handler(Looper.getMainLooper());
        executor.execute(new Runnable() {
            @Override
            public void run() {
                // Inserting Courses
                courseDAO.insert(course);

                // Do after background execution is done - post execution
            }
        });
    }

    public void deleteCategory(Category category){
        ExecutorService executor = Executors.newSingleThreadExecutor();
        Handler handler = new Handler(Looper.getMainLooper());

        executor.execute(new Runnable() {
            @Override
            public void run() {
                // Deleting Categories
                categoryDAO.delete(category);
            }
        });
}

    public void deleteCourse(Course course){

        ExecutorService executor = Executors.newSingleThreadExecutor();
        Handler handler = new Handler(Looper.getMainLooper());

        executor.execute(new Runnable() {
            @Override
            public void run() {
                // Deleting Course
                courseDAO.delete(course);
            }
        });
    }

    public void updateCategory(Category category){
        ExecutorService executor = Executors.newSingleThreadExecutor();
        Handler handler = new Handler(Looper.getMainLooper());

        executor.execute(new Runnable() {
            @Override
            public void run() {
                // Updating Categories;
                categoryDAO.update(category);
            }
        });
    }

    public void updateCourse(Course course){
        ExecutorService executor = Executors.newSingleThreadExecutor();
        Handler handler = new Handler(Looper.getMainLooper());

        executor.execute(new Runnable() {
            @Override
            public void run() {
                // Updating course
                courseDAO.update(course);
            }
        });
    }
}