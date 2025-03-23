package org.itmo.testing.lab2.integration;

import io.javalin.Javalin;
import io.restassured.RestAssured;
import org.itmo.testing.lab2.controller.UserAnalyticsController;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.TestMethodOrder;

import java.time.LocalDateTime;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.equalTo;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class IntegrationRobustTest {

    private static final int port = 7000;
    private Javalin app;

    @BeforeAll
    void setUp() {
        app = UserAnalyticsController.createApp();
        app.start(port);
        RestAssured.baseURI = "http://localhost";
        RestAssured.port = port;
    }

    @AfterAll
    void tearDown() {
        app.stop();
    }

    /*
    Testing /register endpoint
     */
    @Test
    @Disabled
    @DisplayName("Тест регистрации пользователя (повторная регистрация)")
    void testDoubleRegister() {
        given()
                .queryParam("userId", "user1")
                .queryParam("userName", "Alice")
                .when()
                .post("/register")
                .then()
                .statusCode(200)
                .body(equalTo("User registered: true"));

        given()
                .queryParam("userId", "user1")
                .queryParam("userName", "Alice")
                .when()
                .post("/register")
                .then()
                .statusCode(406)
                .body(equalTo("User registered: true"));
    }

    @Test
    @DisplayName("Тест регистрации пользователя (пустое имя)")
    void testUserRegistrationNameMissing() {
        given()
                .queryParam("userId", "user1")
                .when()
                .post("/register")
                .then()
                .statusCode(400)
                .body(equalTo("Missing parameters"));
    }

    @Test
    @DisplayName("Тест регистрации пользователя (пустой идентификатор)")
    void testUserRegistration() {
        given()
                .queryParam("userName", "Alice")
                .when()
                .post("/register")
                .then()
                .statusCode(400)
                .body(equalTo("Missing parameters"));
    }
    /*
    Testing /recordSession endpoint
     */

    @Test
    @DisplayName("Тест записи сессии (пустой идентификатор)")
    void testRecordSessionMissingId() {
        LocalDateTime now = LocalDateTime.now();
        given()
                .queryParam("loginTime", now.minusHours(1).toString())
                .queryParam("logoutTime", now.toString())
                .when()
                .post("/recordSession")
                .then()
                .statusCode(400)
                .body(equalTo("Missing parameters"));
    }

    @Test
    @DisplayName("Тест записи сессии (пустое время входа)")
    void testRecordSessionMissingDateFrom() {
        LocalDateTime now = LocalDateTime.now();
        given()
                .queryParam("userId", "user1")
                .queryParam("logoutTime", now.toString())
                .when()
                .post("/recordSession")
                .then()
                .statusCode(400)
                .body(equalTo("Missing parameters"));
    }

    @Test
    @DisplayName("Тест записи сессии (пустое время выхода)")
    void testRecordSession() {
        LocalDateTime now = LocalDateTime.now();
        given()
                .queryParam("userId", "user1")
                .queryParam("loginTime", now.minusHours(1).toString())
                .when()
                .post("/recordSession")
                .then()
                .statusCode(400)
                .body(equalTo("Missing parameters"));
    }

    @Test
    @DisplayName("Тест записи сессии (пустое время выхода)")
    void testRecordSessionBadDate() {
        given()
                .queryParam("userId", "user1")
                .queryParam("loginTime", "2023.11/12")
                .when()
                .post("/recordSession")
                .then()
                .statusCode(400)
                .body(equalTo("Missing parameters"));
    }

    /*
    Testing /totalActivity endpoint
     */
    @Test
    @Disabled
    @DisplayName("Тест получения общего времени активности (нет пользователя)")
    void testGetTotalActivity() {
        given()
                .queryParam("userId", "fake")
                .when()
                .get("/totalActivity")
                .then()
                .statusCode(400)
                .body(containsString("No sessions found for user"));
    }

    /*
Testing /monthlyActivity endpoint
 */
    @Test
    @DisplayName("Тест получения неактивных пользователей (нет параметра)")
    void testGetMonthlyActivityNoDateParameter() {
        given()
                .when()
                .get("/monthlyActivity")
                .then()
                .statusCode(400)
                .body(containsString("Missing parameters"));
    }

    /*
Testing /inactiveUsers endpoint
*/
    @Test
    @DisplayName("Тест получения активности пользователей за месяц (нет параметра)")
    void testGetMonthlyActivityNoIdParameter() {
        given()
                .when()
                .get("/monthlyActivity")
                .then()
                .statusCode(400)
                .body(containsString("Missing parameters"));
    }

    @Test
    @DisplayName("Тест получения активности пользователей за месяц (нет параметра)")
    void testGetMonthlyActivityNoUser() {
        given()
                .queryParam("userId", "fake")
                .queryParam("month", "2021-01")
                .when()
                .get("/monthlyActivity")
                .then()
                .statusCode(400)
                .body(containsString("No sessions found for user"));
    }

    @Test
    @DisplayName("Тест получения активности пользователей за месяц (неверный формат даты)")
    void testGetMonthlyActivityBadDate() {
        given()
                .queryParam("userId", "fake")
                .queryParam("month", "2021.01")
                .when()
                .get("/monthlyActivity")
                .then()
                .statusCode(400)
                .body(containsString("Invalid data"));
    }
}
