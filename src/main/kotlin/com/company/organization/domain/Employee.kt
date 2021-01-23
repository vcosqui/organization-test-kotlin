package com.company.organization.domain

import javax.persistence.*
import javax.persistence.CascadeType.ALL

@Entity
data class Employee(@Id @GeneratedValue var id: Long?,
                    var name: String,
                    @ManyToOne(cascade = [ALL], optional = true) @JoinColumn(name = "manager") var manager: Employee?,
                    @OneToMany var managed: MutableList<Employee> = mutableListOf()) {

    fun isRoot(): Boolean {
        return manager == null;
    }

    fun addManaged(employee: Employee): Unit {
        managed.add(employee);
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is Employee) return false

        if (name != other.name) return false

        return true
    }

    override fun hashCode(): Int {
        return name.hashCode()
    }
}