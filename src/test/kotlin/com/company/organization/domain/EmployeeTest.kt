package com.company.organization.domain

import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test

internal class EmployeeTest {

    @Test
    fun `When isRoot then return yes`() {
        val employee = Employee(null, "employee-1", null)

        assertThat(employee.isRoot()).isTrue()
    }

    @Test
    fun `When isRoot then return no`() {
        val manager = Employee(null, "manager-1", null)
        val employee = Employee(null, "employee-1", manager)

        assertThat(employee.isRoot()).isFalse()
    }

    @Test
    fun `When getManaged then return managed employees`() {
        val manager = Employee(null, "manager-1", null)
        val employee1 = Employee(null, "employee-1", manager)
        val employee2 = Employee(null, "employee-2", manager)
        manager.addManaged(employee1)
        manager.addManaged(employee2)

        val managed = manager.managed

        assertThat(managed).hasSize(2).contains(employee1, employee2)
    }
}
