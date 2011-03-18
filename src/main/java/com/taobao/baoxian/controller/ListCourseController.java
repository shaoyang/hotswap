package com.taobao.baoxian.controller;

import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.Controller;

import com.taobao.baoxian.pojo.Course;
import com.taobao.baoxian.service.CourseService;

public class ListCourseController implements Controller{
	private CourseService courseService;
	public ModelAndView handleRequest(HttpServletRequest arg0, HttpServletResponse arg1) throws Exception {  
	    List<Course> courses = courseService.getAllCourses();  
	    return new ModelAndView("courselist","courses",courses);  
	}
	public void setCourseService(CourseService courseService) {
		this.courseService = courseService;
	}   
}
