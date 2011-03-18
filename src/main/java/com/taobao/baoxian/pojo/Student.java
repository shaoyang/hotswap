package com.taobao.baoxian.pojo;

public class Student implements java.io.Serializable {
	private static final long serialVersionUID = 1L;

	private Integer id;

	private String firstname;

	private String lastname;

	public String getFirstname() {
	return firstname;
	}

	public void setFirstname(String firstname) {
	this.firstname = firstname;
	}

	public Integer getId() {
	return id;
	}

	public void setId(Integer id) {
	this.id = id;
	}

	public String getLastname() {
	return lastname;
	}

	public void setLastname(String lastname) {
	this.lastname = lastname;
	}
}
