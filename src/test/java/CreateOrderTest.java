import io.qameta.allure.junit4.DisplayName;
import io.restassured.RestAssured;
import io.restassured.response.ValidatableResponse;
import jdk.jfr.Description;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import site.stellarburgers.*;
import java.util.List;


public class CreateOrderTest {

    private Order order;
    private OrderListOfClient orderListOfClient;
    private String authorization;

    @Before
    public void setUp() {
        RestAssured.enableLoggingOfRequestAndResponseIfValidationFails();
        orderListOfClient = new OrderListOfClient();
        UserClient userClient = new UserClient();
        User user = User.getRandom();
        userClient.create(user);
        authorization = userClient.login(UserCredentialsForLogin.from(user)).extract().path("accessToken").toString().substring(7);
    }


    @Test
    @DisplayName("Order can be created with valid ingredients")
    @Description("Проверка создания заказа с ингредиентами с авторизацией")
    public void orderCanBeCreatedWithIngredientsWithAuthorization() {

        ValidatableResponse responseGetInformationAboutIngredients = orderListOfClient.getInformationAboutIngredients();
        List<String> listOfIngredients = responseGetInformationAboutIngredients.extract().jsonPath().getJsonObject("data._id");
        ValidatableResponse responseCreate = orderListOfClient.createWithAuthorized(new Order(new String[]{listOfIngredients.get(0), listOfIngredients.get(1)}), authorization);

        int statusCodeCreate = responseCreate.extract().statusCode();
        String name = responseCreate.extract().path("name");

        Assert.assertEquals(statusCodeCreate, 200);
        Assert.assertNotEquals("Order is null", orderListOfClient, 0);
    }

    @Test
    @DisplayName("Order can be created with valid ingredients")
    @Description("Проверка создания заказа с ингредиентами без авторизации")
    public void orderCanBeCreatedWithIngredientsWithoutAuthorization() {

        ValidatableResponse responseGetInformationAboutIngredients = orderListOfClient.getInformationAboutIngredients();
        List<String> listOfIngredients = responseGetInformationAboutIngredients.extract().jsonPath().getJsonObject("data._id");
        ValidatableResponse responseCreate = orderListOfClient.createWithoutAuthorized(new Order(new String[]{listOfIngredients.get(0), listOfIngredients.get(1)}));

        int statusCodeCreate = responseCreate.extract().statusCode();
        String name = responseCreate.extract().path("name");

        Assert.assertEquals(statusCodeCreate, 200);
        Assert.assertNotEquals("Order is null", orderListOfClient, 0);
    }

    @Test
    @DisplayName("Order can not be created without ingredients")
    @Description("Проверка создания заказа без ингредиентов")
    public void orderCanNotBeCreatedWithoutIngredients() {

        ValidatableResponse responseGetInformationAboutIngredients = orderListOfClient.getInformationAboutIngredients();
        List<String> listOfIngredients = responseGetInformationAboutIngredients.extract().jsonPath().getJsonObject("data._id");
        ValidatableResponse responseCreate = orderListOfClient.createWithoutAuthorized(Order.getWithoutIngredients());
        int statusCodeCreate = responseCreate.extract().statusCode();
        String message = responseCreate.extract().path("message");

        Assert.assertEquals(statusCodeCreate, 400);
        Assert.assertEquals(message, "Ingredient ids must be provided");

    }

    @Test
    @DisplayName("Order can not be created with nonexistent hash")
    @Description("Проверка создания заказа с неверным хешем ингредиентов")
    public void orderCanNotBeCreatedWithNonexistentIngredients() {

        ValidatableResponse responseGetInformationAboutIngredients = orderListOfClient.getInformationAboutIngredients();
        List<String> listOfIngredients = responseGetInformationAboutIngredients.extract().jsonPath().getJsonObject("data._id");
        ValidatableResponse responseCreate = orderListOfClient.createWithoutAuthorized(Order.getOrderWithDefaultHashIngredients());
        int statusCodeCreate = responseCreate.extract().statusCode();
        Assert.assertEquals(statusCodeCreate, 500);

    }
}

