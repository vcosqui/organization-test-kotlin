package com.company.organization.domain

class Organization private constructor(
    private val _employees: MutableList<Employee>
) {
    val allEmployees: List<Employee> get() = _employees

    companion object {
        fun empty(): Organization = Organization(mutableListOf())
        fun reconstitute(employees: List<Employee>): Organization = Organization(employees.toMutableList())
    }

    fun getRootEmployee(): Employee? = _employees.firstOrNull { it.isRoot() }

    fun getEmployee(name: String): Employee =
        _employees.firstOrNull { it.name == name }
            ?: throw IllegalOrganizationException("Employee '$name' not found")

    fun addEmployees(employeesMap: Map<String, String>) {
        employeesMap.forEach { (employeeName, managerName) ->
            val employee = findOrCreate(employeeName)
            val manager = findOrCreate(managerName)
            checkCyclicDep(manager, employee)
            employee.manager = manager
            if (!manager.managed.contains(employee)) manager.addManaged(employee)
        }
        verifySingleRoot()
    }

    fun removeEmployee(name: String) {
        val employee = _employees.find { it.name == name }
            ?: throw EmployeeNotFoundException(name)
        if (employee.managed.isNotEmpty())
            throw IllegalOrganizationException(
                "Cannot remove '$name': has ${employee.managed.size} direct report(s). Reassign them first.")
        employee.manager?.managed?.remove(employee)
        _employees.remove(employee)
    }

    private fun findOrCreate(name: String): Employee {
        require(name.isNotBlank()) { "Employee name cannot be blank" }
        return _employees.firstOrNull { it.name == name }
            ?: Employee(null, name, null).also { _employees.add(it) }
    }

    private fun verifySingleRoot() {
        if (_employees.count { it.isRoot() } > 1)
            throw IllegalOrganizationException("Organization must have a single root")
    }

    private tailrec fun checkCyclicDep(employee: Employee, target: Employee) {
        if (employee == target) throw IllegalOrganizationException("Cyclic dependency detected for '${target.name}'")
        checkCyclicDep(employee.manager ?: return, target)
    }
}
