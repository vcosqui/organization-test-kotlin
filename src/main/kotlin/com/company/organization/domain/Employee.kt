package com.company.organization.domain

data class Employee(
    val id: Long?,
    val name: String,
    var manager: Employee?,
    val managed: MutableList<Employee> = mutableListOf()
) {
    fun isRoot() = manager == null

    fun addManaged(employee: Employee) {
        managed.add(employee)
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is Employee) return false
        return name == other.name
    }

    override fun hashCode() = name.hashCode()
}
