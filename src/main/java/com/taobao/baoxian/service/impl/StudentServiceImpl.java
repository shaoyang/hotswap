package com.taobao.baoxian.service.impl;

import java.sql.SQLException;
import java.util.List;

import com.taobao.baoxian.dao.StudentDao;
import com.taobao.baoxian.pojo.Student;
import com.taobao.baoxian.service.StudentService;

public class StudentServiceImpl implements StudentService{
	private StudentDao studentDao;
	
	public List<Student> getAllStudents() throws SQLException {
		return (List<Student>)studentDao.getAllStudents();
	}
	public void setStudentDao(StudentDao dao){
		this.studentDao = dao;
	}
	
}
