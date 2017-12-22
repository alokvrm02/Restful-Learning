package com.restful.model.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;

import com.restful.model.Employee;
import com.restful.service.EmployeeService;

@Service
public class EmployeeServiceImpl implements EmployeeService{
	
	private final JdbcTemplate jdbcTemplate;
	
	@Autowired
    public EmployeeServiceImpl(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }
	public JdbcTemplate getJdbcTemplate() {
		return jdbcTemplate;
	}

	@Override
	public int addEmployee(Employee employee) {
		// TODO Auto-generated method stub
		int count = jdbcTemplate.update(
			    "INSERT INTO employee(employee_id,first_name, last_name, age)VALUES(?,?,?,?)", new Object[] {
			      employee.getEmployeeId(), employee.getFirstName(), employee.getLastName(), employee.getAge() });
			  return count;
	}

	@Override
	public void getEmployeeById(Long employeeId) {
		// TODO Auto-generated method stub
		
	}

}
