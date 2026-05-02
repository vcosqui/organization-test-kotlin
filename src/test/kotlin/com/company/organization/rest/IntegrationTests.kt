package com.company.organization.rest

import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.test.web.server.LocalServerPort
import org.springframework.http.HttpStatus
import org.springframework.web.client.RestClient

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class IntegrationTests {

    @LocalServerPort
    var port: Int = 0

    @Test
    fun `Assert organization endpoint returns OK`() {
        val restClient = RestClient.create("http://localhost:$port")
        val response = restClient.get().uri("/organization").retrieve().toBodilessEntity()
        assertThat(response.statusCode).isEqualTo(HttpStatus.OK)
    }

}
