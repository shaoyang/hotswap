package com.taobao.baoxian.dao.impl;

import java.sql.SQLException;
import java.util.List;

import com.taobao.baoxian.dao.StudentDao;
import com.taobao.baoxian.dao.support.DaoSupport;
import com.taobao.baoxian.pojo.Student;


public class StudentDaoImpl  extends DaoSupport implements StudentDao {
	
	public List getAllStudents() throws SQLException{
		return (List<Student>)mysqlClient.queryForList("student.getAllStudents");
	}
	
	public Student getStudentById(Long studentId) throws SQLException{
		
		return (Student)mysqlClient.queryForObject("student.getStudentById",studentId);
	}
}