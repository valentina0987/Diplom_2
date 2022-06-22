package site.stellarburgers;
import io.qameta.allure.Step;
import io.restassured.response.ValidatableResponse;
import static io.restassured.RestAssured.given;
public class OrderListOfClient extends RestAssuredClient {

    @Step("Создание заказа без авторизации")
    public ValidatableResponse createWithoutAuthorized(Order order) {

        return given()
                .spec(getBaseSpec())
                .body(order)
                .when()
                .post("api/orders").then();
    }

    @Step("Создание заказа с авторизацией")
    public ValidatableResponse createWithAuthorized(Order order, String authorization) {

        return given()
                .spec(getBaseSpec()).auth().oauth2(authorization)
                .body(order)
                .when()
                .post("api/orders").then();
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
                .get("api/orders").then();
    }

    @Step("Получение списка всех заказов пользователя с авторизацией")
    public ValidatableResponse getAllOrdersByUserWithAuthorized(String authorization) {

        return given()
                .spec(getBaseSpec()).auth().oauth2(authorization)
                .when()
                .get("api/orders").then();
    }
}
