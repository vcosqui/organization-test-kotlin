package com.company.organization.infrastructure

import jakarta.persistence.*
import jakarta.persistence.CascadeType.MERGE
import jakarta.persistence.CascadeType.PERSIST

@Entity
@Table(name = "employee")
class EmployeeJpaEntity(
    @Id @GeneratedValue var id: Long? = null,
    var name: String = "",
    @ManyToOne(cascade = [PERSIST, MERGE], optional = true) @JoinColumn(name = "manager") var manager: EmployeeJpaEntity? = null,
    @OneToMany(mappedBy = "manager") var managed: MutableList<EmployeeJpaEntity> = mutableListOf()
) {
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is EmployeeJpaEntity) return false
        return name == other.name
    }

    override fun hashCode() = name.hashCode()
}
