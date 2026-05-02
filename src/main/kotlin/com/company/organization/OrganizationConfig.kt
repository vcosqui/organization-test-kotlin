package com.company.organization

import com.company.organization.application.OrganizationApplicationService
import com.company.organization.domain.port.OrganizationRepositoryPort
import com.company.organization.domain.port.OrganizationUseCase
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration

@Configuration
class OrganizationConfig {

    @Bean
    fun organizationUseCase(repo: OrganizationRepositoryPort): OrganizationUseCase =
        OrganizationApplicationService(repo)
}
