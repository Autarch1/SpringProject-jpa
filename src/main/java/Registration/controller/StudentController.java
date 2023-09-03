package Registration.controller;

import Registration.dao.CourseDaoImplement;
import Registration.dao.StudentDaoImplement;
import Registration.dao.UserDaoImplement;
import Registration.dto.StudentDto;
import Registration.imagehandler.imageCrud;
import Registration.model.Course;
import Registration.model.Student;
import Registration.model.User;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.commons.CommonsMultipartFile;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@Controller
public class StudentController {
    @Autowired
    CourseDaoImplement courseDao;
    @Autowired
    StudentDaoImplement studentDao;
    @Autowired
    UserDaoImplement userDao;

    @RequestMapping(value = "/StudentRegister", method = RequestMethod.GET)
    public ModelAndView studentView(ModelMap m) {

        int nextStudentId = studentDao.getStudentCount() + 1;
        m.addAttribute("nextStudentId", nextStudentId);
        List<Course> cList = courseDao.getAllCourses();
        m.addAttribute("cList", cList);
        System.out.println("getget");
        return new ModelAndView("StudentRegister", "studentBean", new StudentDto());
    }

    @RequestMapping(value = "/StudentRegisterProcess", method = RequestMethod.POST, consumes = {"multipart/form-data"})
    public String registerProcess(@ModelAttribute("studentBean") StudentDto studentDto, BindingResult rs, ModelMap m,
                                  HttpSession session, RedirectAttributes redirect, @RequestParam("studentPhoto") CommonsMultipartFile photo, HttpServletRequest request) {

        System.out.println("postpost");
        User currentUser = (User) session.getAttribute("currentUser");
        System.out.println(currentUser.getUserId());
        String userId = String.valueOf(userDao.getUserId(currentUser.getUserId()));
        int nextStudentId = studentDao.getStudentCount() + 1;
        List<Course> cList = courseDao.getAllCourses();


        if (session.getAttribute("isLoggedIn") == null) {
            System.out.println("not logged in ");
            redirect.addFlashAttribute("loginBean", new User());
            return "redirect:/Login";
        }

        if (session.getAttribute("currentUser") == null) {
            System.out.println("ss");
            redirect.addFlashAttribute("loginBean", new User());
            return "redirect:/Login";
        }

        List<Student> resList = studentDao.getAllStudent();

        if (studentDto.getStudentId().equals("") || studentDto.getStudentName().equals("") || studentDto.getStudentDob().equals("") ||
                studentDto.getStudentPhone().equals("") || studentDto.getStudentEducation().equals("")) {
            m.addAttribute("blank", "Field Cannot be blank");
            m.addAttribute("nextStudentId", nextStudentId);
            m.addAttribute("cList", cList);

            return "StudentRegister";
        }

        boolean nameDup = false;
        boolean phoneDup = false;


        for (Student res : resList) {
            if (studentDto.getStudentName().equals(res.getStudentName()) || studentDto.getStudentPhone().equals(res.getStudentPhone())) {
                nameDup = true;
                phoneDup = true;
                m.addAttribute("Dup", "This name or phone number already exist");
                m.addAttribute("nextStudentId", nextStudentId);
                m.addAttribute("cList", cList);

                return "StudentRegister";
            }
        }
        studentDto.setUserId(userId);
        Student sb = studentDto.convertToStudent();
        if (!photo.isEmpty()) {
            try {
                byte[] studentPhoto = photo.getBytes();
                sb.setStudentPhoto(studentPhoto);
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        }
        String[] courseIds = studentDto.getStudentAttend();
        List<Course> selectedCourses = new ArrayList<>();

        for (String courId : courseIds) {
            selectedCourses.add(courseDao.getCourseById(courId));
        }




        sb.setCourses(selectedCourses);
        if (!nameDup && !phoneDup) {

            int result = studentDao.createStudent(sb);
            try {
                if (result == 0) {
                    m.addAttribute("fail", "Register Failed");
                    m.addAttribute("nextStudentId", nextStudentId);
                    m.addAttribute("cList", cList);

                    return "StudentRegister";
                }
            } catch (Exception e) {
                System.out.println(e.getMessage());
            }
        }

        return "redirect:/";
    }


    @RequestMapping(value = "/StudentList", method = RequestMethod.GET)
    public String studentsView(ModelMap m) {
        List<Student> studList = studentDao.getAllStudent();
        List<Course> courses = courseDao.getAllCourses();
        System.out.println(studList.get(0));
        m.addAttribute("courses", courses);
        m.addAttribute("studList", studList);
        System.out.println(studList);
        return "StudentList";
    }


    @RequestMapping(value = "/StudentUpdate/{studentId}", method = RequestMethod.GET)
    public ModelAndView studentUpdateView(@PathVariable String studentId, ModelMap m) {
        Student  selectedStudent = studentDao.getAStudent(studentId);
        List<Course> courseList = courseDao.getAllCourses();


        m.addAttribute("courseList", courseList);
        m.addAttribute("selectedStudent", selectedStudent);
        return new ModelAndView("StudentUpdate", "updateBean", selectedStudent);
    }


    @RequestMapping(value = "/StudentUpdateProcess", method = RequestMethod.POST)
    public String updateProcess(@ModelAttribute("studentBean") StudentDto studentDto, BindingResult rs, ModelMap m,
                                HttpSession session, RedirectAttributes redirect, @RequestParam("studentPhoto") CommonsMultipartFile photo) throws IOException {


        System.out.println("postpost");
        User currentUser = (User) session.getAttribute("currentUser");
        System.out.println(currentUser.getUserId());
        String userId = String.valueOf(userDao.getUserId(currentUser.getUserId()));
        int nextStudentId = studentDao.getStudentCount() + 1;
        List<Course> cList = courseDao.getAllCourses();


        if (session.getAttribute("isLoggedIn") == null) {
            System.out.println("not logged in ");
            redirect.addFlashAttribute("loginBean", new User());
            return "redirect:/Login";
        }

        if (session.getAttribute("currentUser") == null) {
            System.out.println("ss");
            redirect.addFlashAttribute("loginBean", new User());
            return "redirect:/Login";
        }

        List<Student> resList = studentDao.getAllStudent();

        if (studentDto.getStudentId().equals("") || studentDto.getStudentName().equals("") || studentDto.getStudentDob().equals("") ||
                studentDto.getStudentPhone().equals("") || studentDto.getStudentEducation().equals("")) {
            m.addAttribute("blank", "Field Cannot be blank");
            m.addAttribute("nextStudentId", nextStudentId);
            m.addAttribute("cList", cList);

            return "redirect:/StudentUpdate/" + studentDto.getStudentId();

        }

        boolean nameDup = false;
        boolean phoneDup = false;


//        for (Student res : resList) {
//            if (studentDto.getStudentName().equals(res.getStudentName()) || studentDto.getStudentPhone().equals(res.getStudentPhone())) {
//                nameDup = true;
//                phoneDup = true;
//                m.addAttribute("Dup", "This name or phone number already exist");
//                m.addAttribute("nextStudentId", nextStudentId);
//                m.addAttribute("cList", cList);
//
//                return "StudentRegister";
//            }
//        }


        studentDto.setUserId(userId);
        Student sb = studentDto.convertToStudent();
        Student existingStudent = studentDao.getAStudent(studentDto.getStudentId());

        if (!photo.isEmpty()) {
            try {
                byte[] studentPhoto = photo.getBytes();
                sb.setStudentPhoto(studentPhoto);
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        }else{
            if (existingStudent != null && existingStudent.getStudentPhoto() != null) {
                sb.setStudentPhoto(existingStudent.getStudentPhoto());
            }
        }
        String[] courseIds = studentDto.getStudentAttend();
        List<Course> selectedCourses = new ArrayList<>();

        for (String courId : courseIds) {
            System.out.println("course ---> " + courId);
            selectedCourses.add(courseDao.getCourseById(courId));
        }




        sb.setCourses(selectedCourses);




            int result = studentDao.updateStudent(sb);
            try {
                if (result == 0) {
                    m.addAttribute("fail", "Register Failed");
                    m.addAttribute("nextStudentId", nextStudentId);
                    m.addAttribute("cList", cList);

                    return "redirect:/StudentUpdate/" + studentDto.getStudentId();
                }
            } catch (Exception e) {
                System.out.println(e.getMessage());
            }


        return "redirect:/";
    }


    @RequestMapping(value = "/deleteStudent/{studentId}", method = RequestMethod.GET)
    public String deleteStudent(@PathVariable String studentId) {
        System.out.println(studentId);
        int result = studentDao.deleteStudent(studentId);
        if (result > 0) {
            return "redirect:/StudentList"; // Redirect to the student list page after successful deletion
        } else {
            return "StudentList"; // Handle error scenario appropriately
        }
    }

        @GetMapping("/studentPhoto")
    public void displayPhoto(@RequestParam("studentId") String studentId, HttpServletResponse response) {
        Student dto = studentDao.getAStudent(studentId);
        if (dto != null && dto.getStudentPhoto() != null) {
            response.setContentType("image/jpeg");
            response.setContentType("image/png");
            try (OutputStream outputStream = response.getOutputStream()) {
                byte[] photoData = dto.getStudentPhoto(); // Assuming getStudentPhoto() returns byte[]
                outputStream.write(photoData);
            } catch (IOException e) {
                System.out.println(e.getMessage()); // Log the exception
            }
        }
    }
}
