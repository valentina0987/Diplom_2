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

public class GetOrderListTest {
    private OrderListOfClient orderListOfClient;
    private UserClient userClient;
    private User user;
    private String authorization;

    @Before
    public void setUp() {
        RestAssured.enableLoggingOfRequestAndResponseIfValidationFails();

        orderListOfClient = new OrderListOfClient();
        userClient = new UserClient();
        user = User.getUserRandom();
        userClient.createUser(user);
        authorization = userClient.loginUser(UserCredentialsForLogin.from(user)).extract().path("accessToken").toString().substring(7);

        ValidatableResponse responseGetInformationAboutIngredients = orderListOfClient.getInformationAboutIngredients();
        List<String> listOfIngredients = responseGetInformationAboutIngredients.extract().jsonPath().getJsonObject("data._id");
        ValidatableResponse responseCreateOrder =  orderListOfClient.createOrderWithAuthorized(new Order(new String[]{listOfIngredients.get(0),listOfIngredients.get(1)}),authorization);
    }

    @Test
    @DisplayName("Orderlist of client can be getting after authorization")
    @Description("Проверка получения списка заказов конкретного пользователя авторизованным пользователем")
    public void orderListCanBeGetAfterAuthorization() {

        ValidatableResponse response = orderListOfClient.getAllOrdersByUserWithAuthorized(authorization);
        int statusCodeCreate = response.extract().statusCode();
        boolean isSuccess = response.extract().path("success");
        int countOrders = response.extract().path("totalToday");

        Assert.assertEquals(statusCodeCreate, SC_OK);
        Assert.assertTrue(isSuccess);
        Assert.assertNotEquals(countOrders, 0);
    }

    @Test
    @DisplayName("Orderlist of client not can be getting without authorization")
    @Description("Проверка получения списка заказов конкретного пользователя неавторизованным пользователем")
    public void orderListCanNotBeGetWithoutAuthorization() {

        ValidatableResponse response = orderListOfClient.getAllOrdersByUserWithoutAuthorized();
        int statusCodeCreate = response.extract().statusCode();
        String message = response.extract().path("message");

        Assert.assertEquals(statusCodeCreate, SC_UNAUTHORIZED);
        Assert.assertEquals(message, "You should be authorised");
    }
}
