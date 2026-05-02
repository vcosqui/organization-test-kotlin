package com.company.organization.rest

import com.company.organization.domain.Employee
import java.util.stream.Collectors.toMap

fun topDown(employees: List<Employee>): Map<String, Any> {
    return employees.stream().collect(toMap(Employee::name) { (_, _, _, managed) -> topDown(managed) })
            .ifEmpty { return mapOf() }
}

fun upstreamHierarchy(employee: Employee): Map<String, Any> =
    mapOf(employee.name to (employee.manager?.let { upstreamHierarchy(it) } ?: emptyMap<String, Any>()))
