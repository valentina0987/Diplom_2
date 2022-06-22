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

@JsonInclude(JsonInclude.Include.NON_NULL)
public class CreateUserTest {

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
    @DisplayName("User can be created with valid data")
    @Description("Проверка создания нового уникального пользователя с валидными данными")
    public void uniqueUserCanBeCreatedWithValidData() {

        ValidatableResponse responseCreate = userClient.create(user);
        ValidatableResponse responseLogin =  userClient.login(UserCredentialsForLogin.from(user));
        int statusCodeCreate = responseCreate.extract().statusCode();
        boolean isCreated = responseCreate.extract().path("success");
        token = responseLogin.extract().path("refreshToken");

        Assert.assertEquals(statusCodeCreate, 200);
        Assert.assertTrue(isCreated);
    }

    @Test
    @DisplayName("User can not be created with existing credentials")
    @Description("Проверка создания пользователя, который уже зарегистрирован")
    public void userCanNotBeCreatedBecauseHeWasAlreadyExists()  {

        userClient.create(user);
        ValidatableResponse response = userClient.create(user);
        int statusCode = response.extract().statusCode();
        String messageError = response.extract().path("message");
        token = userClient.login(UserCredentialsForLogin.from(user)).extract().path("refreshToken");

        Assert.assertEquals(statusCode, 403);
        Assert.assertEquals(messageError, "User already exists");
    }

}
