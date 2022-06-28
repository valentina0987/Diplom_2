package site.stellarburgers.requests;
import io.qameta.allure.Step;
import io.restassured.response.ValidatableResponse;
import site.stellarburgers.data.Order;

import static io.restassured.RestAssured.given;

public class OrderListOfClient extends RestAssuredClient {
    final static String ORDER_URL = "api/orders";

    @Step("Создание заказа без авторизации")
    public ValidatableResponse createOrderWithoutAuthorized(Order order) {

        return given()
                .spec(getBaseSpec())
                .body(order)
                .when()
                .post(ORDER_URL).then();
    }

    @Step("Создание заказа с авторизацией")
    public ValidatableResponse createOrderWithAuthorized(Order order, String authorization) {

        return given()
                .spec(getBaseSpec()).auth().oauth2(authorization)
                .body(order)
                .when()
                .post(ORDER_URL).then();
    }


    @Step("Получение информации об ингредиентах")
    public ValidatableResponse getInformationAboutIngredients() {

        return given()
                .spec(getBaseSpec())
                .when()
                .get("api/ingredients").then();
    }

    @Step("Получение списка всех заказов пользователя без авторизации")
    public ValidatableResponse getAllOrdersByUserWithoutAuthorized() {

        return given()
                .spec(getBaseSpec())
                .when()
                .get(ORDER_URL).then();
    }

    @Step("Получение списка всех заказов пользователя с авторизацией")
    public ValidatableResponse getAllOrdersByUserWithAuthorized(String authorization) {

        return given()
                .spec(getBaseSpec()).auth().oauth2(authorization)
                .when()
                .get(ORDER_URL).then();
    }
}
