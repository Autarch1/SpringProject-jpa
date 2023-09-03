package Registration.dao;

import Registration.model.User;
import Registration.service.JPAUtil;
import jakarta.persistence.*;
import org.springframework.stereotype.Repository;

import java.util.List;
@Repository
public class UserDaoImplement implements UserDaoInterface {

    public int createUser(User user) {
        EntityManagerFactory emf = JPAUtil.getEntityManagerFactory();
        EntityManager em = emf.createEntityManager();
        int i = 0;
        try  {
            // Get the max userId to determine the next userId
            Query query = em.createQuery("SELECT MAX(u.userId) FROM User u");
            String maxUserId = (String) query.getSingleResult();
            int nextSequence = (maxUserId != null) ? Integer.parseInt(maxUserId.replaceAll("\\D+", "")) + 1 : 1;
            String formattedUserId = String.format("USR%03d", nextSequence);

            // Set the new formatted userId
            user.setUserId(formattedUserId);

            em.getTransaction().begin();
            em.persist(user);
            em.getTransaction().commit();
           i = 1;
        } catch (Exception e) {
            // Handle exception: log it, print it, etc.
            e.printStackTrace();

        }finally {
            em.close();
        }
        return i;
    }



    @Override
    public List<User> getAllUser() {
        EntityManager em = null;
        List<User> allUser;
        try {
            em = JPAUtil.getEntityManagerFactory().createEntityManager();
            allUser = em.createQuery("SELECT u FROM User u WHERE u.userRole = '1'", User.class).getResultList();
        } finally {
            em.close();
        }
        return allUser;
    }

    @Override
    public int getUserCount() {
        EntityManagerFactory emf = JPAUtil.getEntityManagerFactory();
        EntityManager em = emf.createEntityManager();
        int count = 0;
        try {
            Query query = em.createNativeQuery("SELECT MAX(userId) FROM user");
            Object result = query.getSingleResult();
            if(result != null) {
                String maxUserId = result.toString();
                count = Integer.parseInt(maxUserId.replaceAll("\\D+", ""));
            }
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
        return count;
    }




    @Override
    public User getOneUser(String userId) {
        EntityManager em = null;
        try {
            em = JPAUtil.getEntityManagerFactory().createEntityManager();
            return em.find(User.class, userId); // Find the user by primary key (userId)
        } finally {
            if (em != null) {
                em.close();
            }
        }
    }


    @Override
    public List<User> getAdmin() {
        EntityManager em = null;
        try {
            em = JPAUtil.getEntityManagerFactory().createEntityManager();

            TypedQuery<User> query = em.createQuery("SELECT u FROM User u WHERE u.userRole = '2'", User.class);
            return query.getResultList();
        } finally {
            if (em != null) {
                em.close();
            }
        }
    }

    @Override
    public User getUserId(String userId) {

        EntityManagerFactory emf = JPAUtil.getEntityManagerFactory();
        EntityManager em = emf.createEntityManager();

        User user = null;

        try {
            user = em.find(User.class, userId);
        } finally {
            em.close();
        }

        return user;
    }


    @Override
    public int updateUser(User user) {
        EntityManager em = null;
        int i = 0;
        try {
            em = JPAUtil.getEntityManagerFactory().createEntityManager();
            em.getTransaction().begin();
            Query query = em.createQuery("UPDATE User u SET u.userName = :userName, u.userPassword = :userPassword WHERE u.userId = :userId");
            query.setParameter("userName", user.getUserName());
            query.setParameter("userPassword", user.getUserPassword());
            query.setParameter("userId", user.getUserId()); // Set the correct parameter here
            i = query.executeUpdate();
            em.getTransaction().commit(); // Commit the transaction
        } finally {
            em.close();
        }
        return i;
    }


    @Override
    public int deleteUser(int userId) {
        EntityManager em = null;
        int i = 0;
        try {
            em = JPAUtil.getEntityManagerFactory().createEntityManager();
            em.getTransaction().begin();
            Query query = em.createQuery("DELETE FROM User u WHERE u.userId=:userId");
            query.setParameter("bookCode", userId);
            i = query.executeUpdate();
            i = 1;
        } finally {
            em.close();
        }
        return i;
    }

    @Override
    public User findUserByEmail(String userEmail) {
        EntityManager em = null;
        try {
            em = JPAUtil.getEntityManagerFactory().createEntityManager();
            TypedQuery<User> query = em.createQuery("SELECT u FROM User u WHERE u.userEmail = :userEmail", User.class);
            query.setParameter("userEmail", userEmail);  // corrected the parameter name
            return query.getSingleResult();
        } catch (NoResultException e) {
            return null;
        } finally {
            if (em != null) {
                em.close();
            }
        }
    }

//    @Override
//    public boolean isSameEmailExist(String userEmail) {
//        EntityManager em = JPAUtil.getEntityManagerFactory().createEntityManager();
//        try {
//            Query query = em.createNativeQuery("SELECT COUNT(*) FROM user WHERE userEmail = :userEmail");
//            query.setParameter("email", userEmail);
//            BigInteger count = (BigInteger) query.getSingleResult();
//            return count.compareTo(BigInteger.ZERO) > 0;
//        } finally {
//            if (em != null) {
//                em.close();
//            }
//        }
//    }


}