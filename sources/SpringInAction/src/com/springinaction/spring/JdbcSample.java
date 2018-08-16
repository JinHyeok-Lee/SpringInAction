package com.springinaction.spring;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

import javax.sql.DataSource;

public class JdbcSample {
	
	private DataSource datasource;
	
	public Employee getEmployee(String id) {
		Connection conn = null;
		PreparedStatement psmt = null;
		ResultSet rs = null;
		try {
			conn = datasource.getConnection();
			psmt = conn.prepareStatement("select id, firstname, lastname, salary from"
					+ " employee where id = ?");
			psmt.setString(1, id);
			rs = psmt.executeQuery();
			Employee employee = null;
			if(rs.next()) {
				employee = new Employee();
				employee.setId(rs.getString("id"));
				employee.setFirstName(rs.getString("firstName"));
				employee.setLastName(rs.getString("lastName"));
				employee.setSalary(rs.getString("salary"));
			}
			return employee;
		}catch(Exception e) {
			
		}finally {
			if(rs != null) {
				try {
					rs.close();
				}catch(Exception e) {}
			}
			if(psmt != null) {
				try {
					psmt.close();
				}catch(Exception e) {}
			}
			if(conn != null) {
				try {
					conn.close();
				}catch(Exception e) {}
			}
		}
		return null;
	}
}








class Employee{
	private String id,firstName,lastName, salary;

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getFirstName() {
		return firstName;
	}

	public void setFirstName(String firstName) {
		this.firstName = firstName;
	}

	public String getLastName() {
		return lastName;
	}

	public void setLastName(String lastName) {
		this.lastName = lastName;
	}

	public String getSalary() {
		return salary;
	}

	public void setSalary(String salary) {
		this.salary = salary;
	}
	
}
