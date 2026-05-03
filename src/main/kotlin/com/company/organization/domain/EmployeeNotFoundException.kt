package com.company.organization.domain

class EmployeeNotFoundException(name: String) :
    RuntimeException("Employee '$name' not found")
