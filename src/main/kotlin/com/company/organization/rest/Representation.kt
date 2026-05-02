package com.company.organization.rest

import com.company.organization.domain.Employee

fun topDown(employees: List<Employee>): Map<String, Any> =
    employees.associate { it.name to topDown(it.managed) }

fun upstreamHierarchy(employee: Employee): Map<String, Any> =
    mapOf(employee.name to (employee.manager?.let { upstreamHierarchy(it) } ?: emptyMap<String, Any>()))
