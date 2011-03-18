package com.taobao.baoxian.controller;

import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.Controller;

import com.taobao.baoxian.pojo.Student;
import com.taobao.baoxian.service.StudentService;

public class ListStudentController implements Controller{
	private StudentService studentService;
	public ModelAndView handleRequest(HttpServletRequest arg0, HttpServletResponse arg1) throws Exception {  
	    List<Student> students = studentService.getAllStudents();  
	    return new ModelAndView("studentlist","students",students);  
	}
	public void setStudentService(StudentService studentService) {
		this.studentService = studentService;
	}   
}
