package com.taobao.baoxian.service;

import java.sql.SQLException;
import java.util.List;

import com.taobao.baoxian.pojo.Student;

public interface StudentService {
	public List<Student> getAllStudents() throws SQLException;
}
