package Registration.dao;

import Registration.model.Course;

import java.util.List;

public interface CourseDaoInterface {

    public int courseAdd(Course course);

    List<Course> getAllCourses();

    public int getCourseCount();

    public Course getCourseById(String courseId);

    }
