import com.fasterxml.jackson.annotation.JsonInclude;
import io.qameta.allure.junit4.DisplayName;
import io.restassured.RestAssured;
import io.restassured.response.ValidatableResponse;
import jdk.jfr.Description;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import site.stellarburgers.User;
import site.stellarburgers.UserClient;
import site.stellarburgers.UserCredentialsForLogin;

//@JsonInclude(JsonInclude.Include.NON_NULL)
public class LoginUserTest {

    private User user;
    private UserClient userClient;
    private String token;

    @Before
    public void setUp() {
        RestAssured.enableLoggingOfRequestAndResponseIfValidationFails();
        userClient = new UserClient();
        user = User.getRandom();
    }

    @After
    public void tearDown() {
        userClient.logout(token);
    }

    @Test
    @DisplayName("User can be authorized with valid login and password")
    @Description("Проверка авторизации пользователя с верным логином и паролем")
    public void userCanBeAuthorizedWithValidLoginAndPassword() {

        ValidatableResponse responseCreate = userClient.create(user);
        ValidatableResponse responseLogin =  userClient.login(UserCredentialsForLogin.from(user));
        int statusCodeLogin = responseLogin.extract().statusCode();
        boolean isLogined = responseLogin.extract().path("success");
        token = responseLogin.extract().path("refreshToken");

        Assert.assertEquals(statusCodeLogin, 200);
        Assert.assertTrue(isLogined);
    }

    @Test
    @DisplayName("User can not be authorized with invalid login and password")
    @Description("Проверка авторизации пользователя с неверным логином и паролем")
    public void userCanNotBeAuthorizedWithInvalidData() {

        ValidatableResponse responseCreate = userClient.create(user);
        ValidatableResponse response = userClient.
                login(UserCredentialsForLogin.
                        getUserCredentialsWithInvalidEmailAndPassword(user));

        int statusCode = response.extract().statusCode();
        String messageError = response.extract().path("message");
        token = responseCreate.extract().path("refreshToken");

        Assert.assertEquals(statusCode, 401);
        Assert.assertEquals(messageError, "email or password are incorrect");
    }
}
