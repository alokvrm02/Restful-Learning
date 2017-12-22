package com.restful.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.restful.model.Employee;
import com.restful.service.EmployeeService;

@RestController
public class EmployeeController {

	 @Autowired
	 private EmployeeService employeeService;

	 @RequestMapping(value = "/empService", method = RequestMethod.POST,produces = "application/json",consumes = MediaType.APPLICATION_JSON_VALUE)
	 public ResponseEntity<Employee> createEmployee(@RequestBody Employee employee) {
	  HttpHeaders headers = new HttpHeaders();
	  if (employee == null) {
	   return new ResponseEntity<Employee>(HttpStatus.BAD_REQUEST);
	  }
	  employeeService.addEmployee(employee);
	  headers.add("Employee Created  - ", String.valueOf(employee.getEmployeeId()));
	  return new ResponseEntity<Employee>(employee, headers, HttpStatus.CREATED);
	 }


}
