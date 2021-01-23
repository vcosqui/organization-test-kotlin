package com.company.organization.rest

import com.company.organization.domain.Employee
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test

internal class RepresentationKtTest {

    @Test
    fun `When topDown no employees should represent correctly`() {
        val map = topDown(listOf())
        val expected = mapOf<String, Any>()
        assertThat(map).isEqualTo(expected)
    }

    @Test
    fun `When topDown a single employee should represent correctly`() {
        val employee1 = Employee(1, "employee 1", null)
        val map = topDown(listOf(employee1))
        val expected = mapOf<String, Any>("employee 1" to mapOf<String, Any>())
        assertThat(map).isEqualTo(expected)
    }

    @Test
    fun `When topDown two employees should represent correctly`() {
        val employee1 = Employee(1, "employee 1", null)
        val employee2 = Employee(2, "employee 2", employee1)
        employee1.addManaged(employee2)
        val map = topDown(listOf(employee1))
        val expected = mapOf<String, Any>("employee 1" to mapOf<String, Any>("employee 2" to mapOf<String, Any>()))
        assertThat(map).isEqualTo(expected)
    }
}