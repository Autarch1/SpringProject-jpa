package Registration.dao;

import Registration.model.Course;
import Registration.model.Student;
import Registration.model.User;
import Registration.service.JPAUtil;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.Query;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import javax.swing.text.html.parser.Entity;
import java.util.ArrayList;
import java.util.List;
@Repository
public class StudentDaoImplement implements StudentDaoInterface {
    @Autowired
    CourseDaoImplement courseDao;
    @Override
    public int createStudent(Student student) {
        System.out.println("creating student .........................");
        EntityManagerFactory emf = JPAUtil.getEntityManagerFactory();
        EntityManager em = emf.createEntityManager();
        int i = 0;
        try{
            Query query = em.createQuery("SELECT MAX(s.studentId) FROM Student s");
            String maxStudentId = (String) query.getSingleResult();
            int nextSequence = (maxStudentId != null) ? Integer.parseInt(maxStudentId.replaceAll("\\D+", "")) + 1 : 1;
            String formattedUserId = String.format("STU%03d", nextSequence);

            // Set the new formatted userId
            student.setStudentId(formattedUserId);

            em.getTransaction().begin();
            System.out.println(student.getStudentPhoto().length);
            em.persist(student);

            em.getTransaction().commit();
            i = 1;
        } catch (Exception e) {
            // Handle exception: log it, print it, etc.
            e.printStackTrace();

        }finally {
            em.close();
            emf.close();
        }
        return i;
    }

    @Override
    public List<Student> getAllStudent() {
        EntityManager em = null;
        List<Student> allStudent;
        try{
            em = JPAUtil.getEntityManagerFactory().createEntityManager();
            allStudent = em.createQuery("SELECT s FROM Student s", Student.class).getResultList();
        }finally {
            em.close();
        }
        return allStudent;
    }

    @Override
    public int getStudentCount() {
        EntityManagerFactory emf = JPAUtil.getEntityManagerFactory();
        EntityManager em = emf.createEntityManager();
        int count = 0;
        try {
            Query query = em.createNativeQuery("SELECT MAX(studentId) FROM  Student");
            Object result = query.getSingleResult();
            if(result != null) {
                String maxUserId = result.toString();
                count = Integer.parseInt(maxUserId.replaceAll("\\D+", ""));
            }
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }finally {
            em.close();
            emf.close();

        }
        return count;
    }

    @Override
    public Student getAStudent(String studentId) {
        EntityManager em = null;
        try {
            em = JPAUtil.getEntityManagerFactory().createEntityManager();
            return em.find(Student.class,studentId);
        }finally {
            if(em!=null){
                em.close();

            }
        }
    }

    @Override
    public int updateStudent(Student student) {
        EntityManagerFactory emf = JPAUtil.getEntityManagerFactory();
        EntityManager em = emf.createEntityManager();
        int i = 0;
        try{
            em.getTransaction().begin();
            em.merge(student);
            em.getTransaction().commit();
            i = 1;
        }catch (Exception e){
            System.out.println(e.getMessage());
        }
            return  i;
    }

    @Override
    public int deleteStudent(String studentId) {
        EntityManagerFactory emf = JPAUtil.getEntityManagerFactory();
        EntityManager em = emf.createEntityManager();
        int i = 0;

        try {
            em.getTransaction().begin();

            Student student = em.find(Student.class, studentId);
            if (student != null) {
                student.setDeleted(!student.isDeleted());
                em.getTransaction().commit();
                i = 1;
            }
        } catch (Exception e) {
            System.out.println(e.getMessage());
        } finally {
            em.close();
            emf.close();
        }

        return i;
    }
}
