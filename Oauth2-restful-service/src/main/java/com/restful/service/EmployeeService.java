package com.restful.service;

import com.restful.model.Employee;

public interface EmployeeService {
	
	public int addEmployee(Employee employee);
	public void getEmployeeById(Long employeeId);

}
