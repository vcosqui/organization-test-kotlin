package com.company.organization.rest

import com.company.organization.infrastructure.EmployeeCrudRepository
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.test.web.server.LocalServerPort
import org.springframework.http.HttpStatus
import org.springframework.http.MediaType
import org.springframework.web.client.RestClient
import org.springframework.web.client.toEntity

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class IntegrationTests {

    @LocalServerPort
    var port: Int = 0

    @Autowired
    lateinit var employeeCrudRepository: EmployeeCrudRepository

    val client: RestClient get() = RestClient.create("http://localhost:$port")

    @BeforeEach
    fun clearDatabase() {
        employeeCrudRepository.deleteAll()
    }

    @Test
    fun `GET organization returns 200 when empty`() {
        val response = client.get().uri("/organization").retrieve().toBodilessEntity()
        assertThat(response.statusCode).isEqualTo(HttpStatus.OK)
    }

    @Test
    fun `POST then GET returns the full hierarchy`() {
        client.post().uri("/organization")
            .contentType(MediaType.APPLICATION_JSON)
            .body(mapOf("bob" to "alice", "charlie" to "alice"))
            .retrieve().toBodilessEntity()

        val response = client.get().uri("/organization")
            .retrieve().toEntity<Map<String, Any>>().body

        assertThat(response).containsKey("alice")
    }

    @Test
    fun `GET employee management returns upstream chain`() {
        client.post().uri("/organization")
            .contentType(MediaType.APPLICATION_JSON)
            .body(mapOf("bob" to "alice"))
            .retrieve().toBodilessEntity()

        val response = client.get().uri("/organization/employee/bob/management")
            .retrieve().toEntity<Map<String, Any>>().body

        assertThat(response).containsKey("bob")
        @Suppress("UNCHECKED_CAST")
        assertThat(response?.get("bob") as Map<String, Any>).containsKey("alice")
    }

    @Test
    fun `POST with cyclic dependency returns 400 with error message`() {
        val response = client.post().uri("/organization")
            .contentType(MediaType.APPLICATION_JSON)
            .body(mapOf("alice" to "bob", "bob" to "alice"))
            .retrieve()
            .onStatus({ it.is4xxClientError }) { _, _ -> }
            .toEntity<Map<String, Any>>()

        assertThat(response.statusCode).isEqualTo(HttpStatus.BAD_REQUEST)
        assertThat(response.body).containsKey("error")
    }

    @Test
    fun `POST with multiple roots returns 400 with error message`() {
        val response = client.post().uri("/organization")
            .contentType(MediaType.APPLICATION_JSON)
            .body(mapOf("bob" to "alice", "dave" to "charlie"))
            .retrieve()
            .onStatus({ it.is4xxClientError }) { _, _ -> }
            .toEntity<Map<String, Any>>()

        assertThat(response.statusCode).isEqualTo(HttpStatus.BAD_REQUEST)
        assertThat(response.body).containsKey("error")
    }

    @Test
    fun `GET unknown employee returns 400 with error message`() {
        val response = client.get().uri("/organization/employee/ghost/management")
            .retrieve()
            .onStatus({ it.is4xxClientError }) { _, _ -> }
            .toEntity<Map<String, Any>>()

        assertThat(response.statusCode).isEqualTo(HttpStatus.BAD_REQUEST)
        assertThat(response.body).containsKey("error")
    }

    @Test
    fun `DELETE leaf employee returns 200 and employee is removed from hierarchy`() {
        client.post().uri("/organization")
            .contentType(MediaType.APPLICATION_JSON)
            .body(mapOf("bob" to "alice"))
            .retrieve().toBodilessEntity()

        val deleteResponse = client.delete().uri("/organization/employee/bob")
            .retrieve().toBodilessEntity()

        assertThat(deleteResponse.statusCode).isEqualTo(HttpStatus.OK)

        val getResponse = client.get().uri("/organization")
            .retrieve().toEntity<Map<String, Any>>().body

        assertThat(getResponse).doesNotContainKey("bob")
    }

    @Test
    fun `DELETE employee with direct reports returns 400 with error message`() {
        client.post().uri("/organization")
            .contentType(MediaType.APPLICATION_JSON)
            .body(mapOf("bob" to "alice"))
            .retrieve().toBodilessEntity()

        val response = client.delete().uri("/organization/employee/alice")
            .retrieve()
            .onStatus({ it.is4xxClientError }) { _, _ -> }
            .toEntity<Map<String, Any>>()

        assertThat(response.statusCode).isEqualTo(HttpStatus.BAD_REQUEST)
        assertThat(response.body).containsKey("error")
    }

    @Test
    fun `DELETE root employee with no direct reports returns 200 and organization is empty`() {
        client.post().uri("/organization")
            .contentType(MediaType.APPLICATION_JSON)
            .body(mapOf("bob" to "alice"))
            .retrieve().toBodilessEntity()

        client.delete().uri("/organization/employee/bob").retrieve().toBodilessEntity()

        val deleteResponse = client.delete().uri("/organization/employee/alice")
            .retrieve().toBodilessEntity()

        assertThat(deleteResponse.statusCode).isEqualTo(HttpStatus.OK)

        val getResponse = client.get().uri("/organization")
            .retrieve().toEntity<Map<String, Any>>().body

        assertThat(getResponse).isEmpty()
    }

    @Test
    fun `DELETE unknown employee returns 404 with error message`() {
        val response = client.delete().uri("/organization/employee/ghost")
            .retrieve()
            .onStatus({ it.is4xxClientError }) { _, _ -> }
            .toEntity<Map<String, Any>>()

        assertThat(response.statusCode).isEqualTo(HttpStatus.NOT_FOUND)
        assertThat(response.body).containsKey("error")
    }
}
