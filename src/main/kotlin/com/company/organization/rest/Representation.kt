package com.company.organization.rest

import com.company.organization.domain.Employee
import java.util.stream.Collectors.toMap

fun topDown(employees: List<Employee>): Map<String, Any> {
    return employees.stream().collect(toMap(Employee::name) { (_, _, _, managed) -> topDown(managed) })
            .ifEmpty { return mapOf() }
}