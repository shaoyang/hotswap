package com.taobao.baoxian.service.impl;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import com.taobao.baoxian.pojo.Course;
import com.taobao.baoxian.pojo.Instructor;
import com.taobao.baoxian.service.CourseService;

public class CourseServiceImpl implements CourseService{
	public List getAllCourses(){  
	    List<Course> courseList = new ArrayList<Course>();  
	    Course course = null;  
	    Date date = null;  
	    for(int i = 0; i < 8; i++){  
	      course = new Course();  
	      course.setId("XB2006112-"+i);  
	      course.setName("Name-"+i);  
	      date = new Date();  
	      date.setYear(104-i);  
	      course.setStartDate(date);  
	      date = new Date();  
	      date.setYear(105+i);  
	        course.setEndDate(date);;  
	      Instructor instructor = new Instructor();  
	      instructor.setFirstName("firstName-"+i);  
	      instructor.setLastName("lastName-"+i);  
	      course.setInstructor(instructor);  
	      courseList.add(course);  
	    }  
	    return courseList;  
	}
}
