package Registration.dao;

import Registration.model.Course;
import Registration.service.JPAUtil;
import jakarta.persistence.*;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public class CourseDaoImplement implements CourseDaoInterface {

    @Override
    public int courseAdd(Course course) {
        EntityManagerFactory emf = JPAUtil.getEntityManagerFactory(); // Assuming JPAUtil is a utility class that returns an EntityManagerFactory
        EntityManager em = emf.createEntityManager();
        int i = 0;

        try {
            // Get the max courseId to determine the next courseId
            Query query = em.createQuery("SELECT MAX(c.courseId) FROM Course c");
            String maxCourseId = (String) query.getSingleResult();
            int nextSequence = (maxCourseId != null) ? Integer.parseInt(maxCourseId.replaceAll("\\D+", "")) + 1 : 1;
            String formattedCourseId = String.format("COU%03d", nextSequence);

            course.setCourseId(formattedCourseId);

            em.getTransaction().begin();
            em.persist(course);
            em.getTransaction().commit();
            i = 1;

        } catch (Exception e) {
            // Handle exception: log it, print it, etc.
            e.printStackTrace();
        } finally {
            em.close();
        }

        return i;
    }

    @Override
    public List<Course> getAllCourses() {
        EntityManager em = null;
        List<Course> courses;

        try {
            em = JPAUtil.getEntityManagerFactory().createEntityManager();
            courses = em.createQuery("SELECT c FROM Course c ", Course.class).getResultList();
        } finally {
            em.close();
        }

        return courses;
    }

    @Override
    public int getCourseCount() {
        EntityManagerFactory emf = JPAUtil.getEntityManagerFactory();
        EntityManager em = emf.createEntityManager();
        int count = 0;
        try {
            Query query = em.createNativeQuery("SELECT MAX(courseId) FROM course");
            Object result = query.getSingleResult();
            if (result != null) {
                String maxCourseId = result.toString();
                count = Integer.parseInt(maxCourseId.replaceAll("\\D+", ""));
            }
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
        return count;
    }

    @Override
    public Course getCourseById(String courseId) {
        EntityManagerFactory emf = JPAUtil.getEntityManagerFactory();
        EntityManager em = emf.createEntityManager();

        try {
            TypedQuery<Course> query = em.createQuery("SELECT c FROM Course c WHERE c.courseId = :courseId", Course.class);
            query.setParameter("courseId", courseId);

            return query.getSingleResult();
        } catch (NoResultException e) {
            // Handle case where no course is found
            return null;
        } finally {
            if (em != null) {
                em.close();
            }
        }
    }


}
