import io.qameta.allure.junit4.DisplayName;
import io.restassured.RestAssured;
import io.restassured.response.ValidatableResponse;
import jdk.jfr.Description;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import site.stellarburgers.data.User;
import site.stellarburgers.requests.UserClient;
import site.stellarburgers.data.UserCredentialsForLogin;
import static org.apache.http.HttpStatus.*;

public class CreateUserTest {

    private User user;
    private UserClient userClient;
    private String token;

    @Before
    public void setUp() {
        RestAssured.enableLoggingOfRequestAndResponseIfValidationFails();
        userClient = new UserClient();
        user = User.getUserRandom();
    }

    @After
    public void tearDown() {
        userClient.logout(token);
    }

    @Test
    @DisplayName("User can be created with valid data")
    @Description("Проверка создания нового уникального пользователя с валидными данными")
    public void uniqueUserCanBeCreatedWithValidData() {

        ValidatableResponse responseCreate = userClient.createUser(user);
        ValidatableResponse responseLogin =  userClient.loginUser(UserCredentialsForLogin.from(user));
        int statusCodeCreate = responseCreate.extract().statusCode();
        boolean isCreated = responseCreate.extract().path("success");
        token = responseLogin.extract().path("refreshToken");

        Assert.assertEquals(SC_OK, statusCodeCreate);
        Assert.assertTrue(isCreated);
    }

    @Test
    @DisplayName("User can not be created with existing credentials")
    @Description("Проверка создания пользователя, который уже зарегистрирован")
    public void userCanNotBeCreatedBecauseHeWasAlreadyExists()  {

        userClient.createUser(user);
        ValidatableResponse response = userClient.createUser(user);
        int statusCode = response.extract().statusCode();
        String messageError = response.extract().path("message");
        token = userClient.loginUser(UserCredentialsForLogin.from(user)).extract().path("refreshToken");

        Assert.assertEquals(statusCode, SC_FORBIDDEN);
        Assert.assertEquals(messageError, "User already exists");
    }

}
