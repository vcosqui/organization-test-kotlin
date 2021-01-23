package com.company.organization.domain

import org.assertj.core.api.Assertions.*
import org.junit.jupiter.api.Test

internal class EmployeeTest {

    @Test
    fun `When isRoot then return yes`() {
        val employee = Employee(null, "employee-1", null);

        assertThat(employee.isRoot());
    }

    @Test
    fun `When isRoot then return no`() {
        val manager = Employee(null, "manager-1", null);
        val employee = Employee(null, "employee-1", manager);

        assertThat(not(employee.isRoot()));
    }

    @Test
    fun `When getManaged then return managed employees`() {
        val manager = Employee(null, "manager-1", null);
        val employee1 = Employee(null, "employee-1", manager);
        val employee2 = Employee(null, "employee-2", manager);
        manager.addManaged(employee1);
        manager.addManaged(employee2);

        val managed = manager.managed

        assertThat(managed.size == 2);
        assertThat(managed.contains(employee1));
        assertThat(managed.contains(employee2));
    }
}