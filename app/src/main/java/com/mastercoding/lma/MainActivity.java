package com.mastercoding.lma;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.mastercoding.lma.databinding.ActivityMainBinding;
import com.mastercoding.lma.model.Category;
import com.mastercoding.lma.model.Course;
import com.mastercoding.lma.viewModel.MainActivityViewModel;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {
    private MainActivityViewModel mainActivityViewModel;
    private ArrayList<Category> categoryList;
    private ActivityMainBinding activityMainBinding;
    private MainActivityClickHandler handlers;
    private Category selectedCategory;

    // Recycler view
    private RecyclerView courseRecyclerView;
    private CourseAdapter courseAdapter;
    private  ArrayList<Course> coursesList;
    private static final int ADD_COURSE_REQUEST_CODE=1;
    private static final int EDIT_COURSE_REQUEST_CODE=2;

    public int selectedCourseId;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mainActivityViewModel = new ViewModelProvider(this).get(MainActivityViewModel.class);

        activityMainBinding = DataBindingUtil.setContentView(this,R.layout.activity_main);
        handlers = new MainActivityClickHandler();
        activityMainBinding.setClickHandler(handlers);



        mainActivityViewModel.getAllCategories().observe(this, new Observer<List<Category>>() {
            @Override
            public void onChanged(List<Category> categories) {

                categoryList = (ArrayList<Category>) categories;
                for(Category c: categories){
                    Log.i("TAG",c.getCategoryName());
                }
                showOnSpinner();
            }
        });
            mainActivityViewModel.getCoursesOfSelectedCategory(1).
        observe(this, new Observer<List<Course>>() {
    @Override
    public void onChanged(List<Course> courses) {
        for(Course c : courses){
            Log.v("TAG",c.getCourseName());
        }
    }
        });
    }

    private void showOnSpinner() {

        ArrayAdapter<Category> categoryArrayAdapter = new ArrayAdapter<>(this,
                R.layout.spinner_item,categoryList);
        categoryArrayAdapter.setDropDownViewResource(R.layout.spinner_item);
        activityMainBinding.setSpinnerAdapter(categoryArrayAdapter);
    }

    public void LoadCoursesArrayList(int categoryID){
        mainActivityViewModel.getCoursesOfSelectedCategory(categoryID).observe(
                this, new Observer<List<Course>>() {
                    @Override
                    public void onChanged(List<Course> courses) {
                        coursesList = (ArrayList<Course>) courses;
                        LoadRecyclerView();
                        
                    }
                }
        );
    }

    private void LoadRecyclerView() {
        courseRecyclerView = activityMainBinding.secondaryLayout.recyclerView;
        courseRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        courseRecyclerView.setHasFixedSize(true);

        courseAdapter = new CourseAdapter();
        courseRecyclerView.setAdapter(courseAdapter);

        courseAdapter.setCourses(coursesList);

        //EDIT THE COURSE
        courseAdapter.setListener(new CourseAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(Course course) {

                selectedCourseId = course.getCourseId();

                Intent i = new Intent(MainActivity.this, AddEditActivity.class);
                i.putExtra(AddEditActivity.COURSE_ID, selectedCourseId);
                i.putExtra(AddEditActivity.COURSE_NAME, course.getCourseName());
                i.putExtra(AddEditActivity.UNIT_PRICE, course.getUnitPrice());
                startActivityForResult(i,EDIT_COURSE_REQUEST_CODE);


            }
        });

        //Delete A course
        new ItemTouchHelper(new ItemTouchHelper.SimpleCallback(0,ItemTouchHelper.LEFT) {
            @Override
            public boolean onMove(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder, @NonNull RecyclerView.ViewHolder target) {
                return false;
            }

            @Override
            public void onSwiped(@NonNull RecyclerView.ViewHolder viewHolder, int direction) {

                    Course courseToDelete = coursesList.get(viewHolder.getAdapterPosition());
                    mainActivityViewModel.deleteCourse(courseToDelete);

            }
        }).attachToRecyclerView(courseRecyclerView);
    }


    public class MainActivityClickHandler{
        public void onFABclick(View view){
        //CREATE THE COURSE

            Intent i = new Intent(MainActivity.this, AddEditActivity.class);
            startActivityForResult(i, ADD_COURSE_REQUEST_CODE);



        }
        public void onSelectItem(AdapterView<?> parent,View view,int pos, long id){
            selectedCategory = (Category) parent.getItemAtPosition(pos);
            String message = "id is: " +selectedCategory.getId() +
                    " \n name is" + selectedCategory.getCategoryName();

            Toast.makeText(parent.getContext(), " "+message, Toast.LENGTH_SHORT).show();

            LoadCoursesArrayList(selectedCategory.getId());
        }
            }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        int selectedCategoryId = selectedCategory.getId();
        if(requestCode == ADD_COURSE_REQUEST_CODE && resultCode == RESULT_OK){
            Course course = new Course();
            course.setCategoryId(selectedCategoryId);
            course.setCourseName(data.getStringExtra(AddEditActivity.COURSE_NAME));
            course.setUnitPrice(data.getStringExtra(AddEditActivity.UNIT_PRICE));
            mainActivityViewModel.addNewCourse(course);

        }
        else if(requestCode == EDIT_COURSE_REQUEST_CODE && resultCode == RESULT_OK){

            Course course = new Course();
            course.setCategoryId(selectedCategoryId);
            course.setCourseName(data.getStringExtra(AddEditActivity.COURSE_NAME));
            course.setUnitPrice(data.getStringExtra(AddEditActivity.UNIT_PRICE));

            course.setCourseId(selectedCourseId);
            mainActivityViewModel.updateCourse(course);

        }

    }
}