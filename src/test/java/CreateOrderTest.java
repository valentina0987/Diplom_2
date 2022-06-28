import io.qameta.allure.junit4.DisplayName;
import io.restassured.RestAssured;
import io.restassured.response.ValidatableResponse;
import jdk.jfr.Description;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import site.stellarburgers.data.Order;
import site.stellarburgers.data.User;
import site.stellarburgers.data.UserCredentialsForLogin;
import site.stellarburgers.requests.OrderListOfClient;
import site.stellarburgers.requests.UserClient;

import java.util.List;
import static org.apache.http.HttpStatus.*;

public class CreateOrderTest {

    private OrderListOfClient orderListOfClient;
    private String authorization;

    @Before
    public void setUp() {
        RestAssured.enableLoggingOfRequestAndResponseIfValidationFails();
        orderListOfClient = new OrderListOfClient();
        UserClient userClient = new UserClient();
        User user = User.getUserRandom();
        userClient.createUser(user);
        authorization = userClient.loginUser(UserCredentialsForLogin.from(user)).extract().path("accessToken").toString().substring(7);
    }


    @Test
    @DisplayName("Order can be created with valid ingredients")
    @Description("Проверка создания заказа с ингредиентами с авторизацией")
    public void orderCanBeCreatedWithIngredientsWithAuthorization() {

        ValidatableResponse responseGetInformationAboutIngredients = orderListOfClient.getInformationAboutIngredients();
        List<String> listOfIngredients = responseGetInformationAboutIngredients.extract().jsonPath().getJsonObject("data._id");
        ValidatableResponse responseCreate = orderListOfClient.createOrderWithAuthorized(new Order(new String[]{listOfIngredients.get(0), listOfIngredients.get(1)}), authorization);

        int statusCodeCreate = responseCreate.extract().statusCode();
        String name = responseCreate.extract().path("name");

        Assert.assertEquals(statusCodeCreate, SC_OK);
        Assert.assertNotEquals("Order is null", orderListOfClient, 0);
    }

    @Test
    @DisplayName("Order can be created with valid ingredients")
    @Description("Проверка создания заказа с ингредиентами без авторизации")
    public void orderCanBeCreatedWithIngredientsWithoutAuthorization() {

        ValidatableResponse responseGetInformationAboutIngredients = orderListOfClient.getInformationAboutIngredients();
        List<String> listOfIngredients = responseGetInformationAboutIngredients.extract().jsonPath().getJsonObject("data._id");
        ValidatableResponse responseCreate = orderListOfClient.createOrderWithoutAuthorized(new Order(new String[]{listOfIngredients.get(0), listOfIngredients.get(1)}));

        int statusCodeCreate = responseCreate.extract().statusCode();
        String name = responseCreate.extract().path("name");

        Assert.assertEquals(statusCodeCreate, SC_OK);
        Assert.assertNotEquals("Order is null", orderListOfClient, 0);
    }

    @Test
    @DisplayName("Order can not be created without ingredients")
    @Description("Проверка создания заказа без ингредиентов")
    public void orderCanNotBeCreatedWithoutIngredients() {

        ValidatableResponse responseGetInformationAboutIngredients = orderListOfClient.getInformationAboutIngredients();
        List<String> listOfIngredients = responseGetInformationAboutIngredients.extract().jsonPath().getJsonObject("data._id");
        ValidatableResponse responseCreate = orderListOfClient.createOrderWithoutAuthorized(Order.getOrderWithoutIngredients());
        int statusCodeCreate = responseCreate.extract().statusCode();
        String message = responseCreate.extract().path("message");

        Assert.assertEquals(statusCodeCreate, SC_BAD_REQUEST);
        Assert.assertEquals(message, "Ingredient ids must be provided");

    }

    @Test
    @DisplayName("Order can not be created with nonexistent hash")
    @Description("Проверка создания заказа с неверным хешем ингредиентов")
    public void orderCanNotBeCreatedWithNonexistentIngredients() {

        ValidatableResponse responseGetInformationAboutIngredients = orderListOfClient.getInformationAboutIngredients();
        List<String> listOfIngredients = responseGetInformationAboutIngredients.extract().jsonPath().getJsonObject("data._id");
        ValidatableResponse responseCreate = orderListOfClient.createOrderWithoutAuthorized(Order.getOrderWithDefaultHashIngredients());
        int statusCodeCreate = responseCreate.extract().statusCode();
        Assert.assertEquals(statusCodeCreate, SC_INTERNAL_SERVER_ERROR);

    }
}

