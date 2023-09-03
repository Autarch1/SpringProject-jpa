package Registration.dao;

import Registration.model.User;

import java.util.List;

public interface UserDaoInterface {

        public int createUser(User user);

    List<User> getAllUser();

    public int getUserCount();

    public User getOneUser(String userId);


    public User getUserId(String userEmail);


    public int updateUser(User user);

    public int deleteUser(int userId);

    public List<User> getAdmin();

    public User findUserByEmail(String userEmail);

//    public boolean isSameEmailExist(String userEmail);

}
