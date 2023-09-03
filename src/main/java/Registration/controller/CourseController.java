package Registration.controller;

import Registration.dao.CourseDaoImplement;
import Registration.model.Course;
import Registration.model.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import javax.servlet.http.HttpSession;
import java.util.List;

@Controller
public class CourseController {
@Autowired
    CourseDaoImplement courseDao;

    @RequestMapping(value = "/CourseRegister", method = RequestMethod.GET)
    public ModelAndView courseView(ModelMap m) {
        int nextCourseId = courseDao.getCourseCount()+1;
        m.addAttribute("nextCourseId", nextCourseId);
        return new ModelAndView("CourseRegister", "courseBean", new Course());
    }
    @RequestMapping(value = "/ProcessCourseRegister", method = RequestMethod.POST)
    public String courseProcess(@ModelAttribute("courseBean") @Validated Course cb, ModelMap m,
                                BindingResult br, HttpSession session, RedirectAttributes redirect) {



        if (br.hasErrors()){
            return "CourseRegister";
        }
        if(session.getAttribute("isLoggedIn")==null) {
            System.out.println("not logged in ");
            redirect.addFlashAttribute("loginBean", new User());
            return "redirect:/Login";
        }

        if(session.getAttribute("isAdmin")==null) {
            System.out.println("admin only ");
            return "Login";
        }
        int nextCourseId = courseDao.getCourseCount()+1;
        boolean isDupe = false;

        if(cb.getCourseId().equals("") || cb.getCourseName().equals("")){
            m.addAttribute("blank", "Field cannot be blank");
            m.addAttribute("nextCourseId", nextCourseId);
            return "CourseRegister";
        }
        List<Course> existingCourses = courseDao.getAllCourses();
        for(Course existingCourse : existingCourses){
            if(cb.getCourseName().trim().equals(existingCourse.getCourseName().trim())) {
                    isDupe = true;
                    m.addAttribute("nextCourseId", nextCourseId);
                    m.addAttribute("sameCourse", "This Course Already Exist");
                    return "CourseRegister";
            }
        }
        System.out.println(cb.getCourseName());
        System.out.println(cb.getCourseId());
        if(!isDupe){
            System.out.println(cb.getCourseName());
            int i = courseDao.courseAdd(cb);
            System.out.println(i);
            System.out.println("sssssssss" +
                    "");

            if(i == 0 ){
                m.addAttribute("insertError", "Course Registration Failed");
                m.addAttribute("nextCourseId", nextCourseId);
                return "CourseRegister";
            }

        }
            return  "redirect:/StudentRegister";

    }
}
