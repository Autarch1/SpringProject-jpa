package Registration.dao;

import Registration.model.Student;

import java.util.List;

public interface StudentDaoInterface {

    public int createStudent(Student student);

    public List<Student> getAllStudent();

    public int getStudentCount();

    public Student getAStudent(String studentId);

    public int updateStudent(Student student);

    public int deleteStudent(String studentId);

}
