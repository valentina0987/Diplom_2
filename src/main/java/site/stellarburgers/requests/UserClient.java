package site.stellarburgers.requests;
import com.fasterxml.jackson.annotation.JsonInclude;
import io.qameta.allure.Step;
import io.restassured.response.ValidatableResponse;
import site.stellarburgers.data.UserCredentialsForLogin;
import site.stellarburgers.data.UserCredentialsForUpdate;
import site.stellarburgers.data.User;

import static io.restassured.RestAssured.given;
import static org.apache.http.HttpStatus.*;


@JsonInclude(JsonInclude.Include.NON_NULL)
public class UserClient extends RestAssuredClient {
    private static final String USER_PATH = "api/auth/";

    @Step("Создание уникального пользователя")
    public ValidatableResponse createUser(User user) {

        return given()
                .spec(getBaseSpec())
                .body(user)
                .when()
                .post(USER_PATH + "register")
                .then();
    }

    @Step("Авторизация пользователя")
    public ValidatableResponse loginUser(UserCredentialsForLogin credentials) {
        return given()
                .spec(getBaseSpec())
                .body(credentials)
                .when()
                .post(USER_PATH + "login")
                .then();
    }

    @Step("Изменение данных пользователя после авторизации")
    public ValidatableResponse updateUserDataWithAuthorization(UserCredentialsForUpdate credentials) {
        return given()
                .spec(getBaseSpec()).auth().oauth2(credentials.authorization)
                .body(credentials)
                .when()
                .patch(USER_PATH + "user")
                .then();
    }

    @Step("Изменение данных пользователя без авторизации")
    public ValidatableResponse updateUserDataWithoutAuthorization(UserCredentialsForUpdate credentials) {
        return given()
                .spec(getBaseSpec())
                .body(credentials)
                .when()
                .patch(USER_PATH + "user")
                .then();
    }

    @Step("Выход из учетной записи")
    public ValidatableResponse logout(String token) {
        String json = "{\"token\":" + "\"" + token + "\"}";
        return given()
                .spec(getBaseSpec())
                .body(json)
                .when()
                .post(USER_PATH + "logout")
                .then().assertThat().statusCode(SC_OK);
    }

}
