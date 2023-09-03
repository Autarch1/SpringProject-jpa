package Registration.controller;

import Registration.dao.UserDaoImplement;
import Registration.model.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpSession;
import java.util.List;

@Component
@ComponentScan("Registration")
@Controller
public class UserController {
    @Autowired
    private UserDaoImplement userDao;


    @RequestMapping(value = "/", method = RequestMethod.GET)
    public String welcome() {
        return "Welcome";
    }

    @RequestMapping(value = "/Login", method = RequestMethod.GET)
    public ModelAndView home() {
        System.out.println("in get method()");
        return new ModelAndView("Login", "loginBean", new User());
    }

    @RequestMapping(value = "/LoginProcess", method = RequestMethod.POST)
    public String login(@ModelAttribute("loginBean") @Validated User ub,
                        BindingResult br,ModelMap m,HttpSession session) {
        if(br.hasErrors()){
            return "Login";
        }
        User user = userDao.findUserByEmail(ub.getUserEmail());
        if (user == null || !user.getUserPassword().equals(ub.getUserPassword())) {
            m.addAttribute("Wrong", "Your email or password is incorrect");
            return "Login";
        }

        session.setAttribute("isLoggedIn", true);
        session.setAttribute("currentUser", user);

        if ("2".equalsIgnoreCase(user.getUserRole())) {
            System.out.println("admin");
            session.setAttribute("isAdmin", true);
            return "redirect:/CourseRegister";
        } else {
            System.out.println("user");
            session.setAttribute("isUser", true);
            return "redirect:/";
        }
    }

    @RequestMapping(value = "/Logout", method = RequestMethod.GET)
    public String logout(HttpSession session) {
        session.removeAttribute("isLoggedIn");
        session.removeAttribute("currentUser");
        session.removeAttribute("isUser");
        session.removeAttribute("isAdmin");
        return "redirect:/Login";
    }

    @RequestMapping(value = "/UserRegister", method = RequestMethod.GET)
    public ModelAndView registerView(ModelMap m) {
        int nextUserId = userDao.getUserCount() + 1;
        m.addAttribute("nextUserId", nextUserId);
        return new ModelAndView("UserRegister", "registerBean", new User());
    }

    @RequestMapping(value = "/ProcessRegister", method = RequestMethod.POST)
    public String register(@ModelAttribute("registerBean") @Validated User ub,
                           @RequestParam("cPassword") String cPassword,
                           ModelMap m, BindingResult br, HttpSession session) {

        if(br.hasErrors()){
            System.out.println("failed");
            return "UserRegister";
        }
        boolean isSamePsw = false;
        boolean isSameEmail = false;

        int nextUserId = userDao.getUserCount() + 1;

        m.addAttribute("nextUserId", nextUserId);

        if(ub.getUserId().equals("") || ub.getUserName().equals("") || ub.getUserEmail().equals("")
        || ub.getUserPassword().equals("") || cPassword.equals("") || ub.getUserRole().equals("")){
            m.addAttribute("blank", "Field Cannot be blank");
            m.addAttribute("nextUserId", nextUserId);
            return "UserRegister";
        }
        if(ub.getUserPassword().equals(cPassword)){
            isSamePsw = true;
                System.out.println("sas"+ub.getUserId());
                int i = userDao.createUser(ub);
                System.out.println("sad"+i);
                System.out.println("ss");
                if(i == 0 ){
                    m.addAttribute("insertError", "Registration Failed");
                    return "UserRegister";
                }

            }
            if(!isSamePsw){
                m.addAttribute("password", "password and confirm password must be same");
                return "UserRegister";
            }


        return "redirect:/Login";

    }
    @RequestMapping(value="/UserProfile", method = RequestMethod.GET)
    public ModelAndView userProfile(ModelMap m,HttpSession session) {
        User currentUser = (User) session.getAttribute("currentUser");
        User user = userDao.findUserByEmail(currentUser.getUserEmail());
        return new ModelAndView("UserProfile", "updatebean", new User());
    }

    @RequestMapping(value = "/UserProfileUpdate", method = RequestMethod.POST)
    public String update(@ModelAttribute("updateBean") @Validated User updateUser,
                         @RequestParam("cPassword") String cPassword, HttpSession session, ModelMap m) {

        User currentUser = (User) session.getAttribute("currentUser");
        String userEmail = currentUser.getUserEmail();

        User existingUser = userDao.findUserByEmail(userEmail);
        if (existingUser == null) {
            return "UserProfile"; // Handle error
        }

        boolean isSamePsw = false;
        if (updateUser.getUserPassword().equals(cPassword)) {
            isSamePsw = true;
            existingUser.setUserName(updateUser.getUserName());
            existingUser.setUserPassword(updateUser.getUserPassword());

            int result = userDao.updateUser(existingUser);
            currentUser.setUserName(updateUser.getUserName());
            currentUser.setUserPassword(updateUser.getUserPassword());
            if (result == 0) {
                System.out.println("Update Error");
                return "UserProfile";
            }
        }

        if (!isSamePsw) {
            m.addAttribute("password", "Password doesn't match");
            return "UserProfile";
        }

        return "redirect:/";
    }

    @RequestMapping(value="UserList", method = RequestMethod.GET)
    public String userList(ModelMap m) {
        List<User> userList = userDao.getAllUser();
        m.addAttribute("userList",userList);

        return "UserList";
    }

    @RequestMapping(value = "/UpdateView/{userId}", method = RequestMethod.GET)
    public ModelAndView updateView(@PathVariable String userId, ModelMap m) {
        User selectedUser = userDao.getOneUser(userId);
        m.addAttribute("selectedUser", selectedUser);
        return new ModelAndView("UserUpdate", "updateBean", selectedUser); // Pass selectedUser as updateBean
    }

    @RequestMapping(value = "/UpdateProcess", method = RequestMethod.POST)
    public String updateProcess(@ModelAttribute("updateBean") @Validated User ub, BindingResult br,
                                HttpSession session, @RequestParam("cPassword") String cPassword, ModelMap m) {
        if (br.hasErrors()) {
            return "UserUpdate";
        }

        if (session.getAttribute("isLoggedIn") == null) {
            return "redirect:/Login";
        }

        if (session.getAttribute("isAdmin") == null) {
            return "redirect:/";
        }
        boolean isSamepsw = false;
        User userToUpdate = userDao.getOneUser(ub.getUserId());
        if (userToUpdate == null) {
            // Handle user not found scenario
            return "UserUpdate";
        }
        userToUpdate.setUserName(ub.getUserName());
        userToUpdate.setUserPassword(ub.getUserPassword());

        if (cPassword.equals(userToUpdate.getUserPassword())) {
            System.out.println(cPassword);
            isSamepsw = true;




            int result = userDao.updateUser(userToUpdate);

            if (result == 0) {
                m.addAttribute("insertError", "Update Failed");
                return "UserUpdate";
            }
        }
        if(!isSamepsw){
            System.out.println(cPassword);
            m.addAttribute("password", "Password doesn't match");
            return "UserUpdate";
        }

        return "redirect:/UserList"; // Redirect to user list after successful update
    }

    }
