package com.springinaction.spring;

import java.sql.ResultSet;
import java.sql.SQLException;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;

public class SpringJdbcTemplate {
	
	private JdbcTemplate jdbctemplate;
	
	public Employee getEmployee(String id) {
		return jdbctemplate.queryForObject(
				      "select id, firstname, lastname, salary from employee where id = ?"
				, new RowMapper<Employee>() {

						@Override
						public Employee mapRow(ResultSet rs, int arg1) throws SQLException {
							Employee employee = new Employee();
							employee.setId(rs.getString("id"));
							employee.setFirstName(rs.getString("firstName"));
							employee.setLastName(rs.getString("lastName"));
							employee.setSalary(rs.getString("salary"));
							return employee;
						}
					}, id);
	}
}







