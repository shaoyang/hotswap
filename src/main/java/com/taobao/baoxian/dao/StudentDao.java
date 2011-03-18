package com.taobao.baoxian.dao;

import java.sql.SQLException;
import java.util.List;

import com.taobao.baoxian.pojo.Student;

public interface StudentDao {
	public List getAllStudents() throws SQLException;
	public Student getStudentById(Long studentId) throws SQLException;
}
