import io.qameta.allure.junit4.DisplayName;
import io.restassured.response.ValidatableResponse;
import jdk.jfr.Description;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import site.stellarburgers.data.User;
import site.stellarburgers.requests.UserClient;
import static org.apache.http.HttpStatus.*;


@RunWith(Parameterized.class)
public class CreateUserWithInvalidDataParameterizedTest {

    private final User user;
    private final int expectedCodeResult;
    private final String expectedMessage;

    public CreateUserWithInvalidDataParameterizedTest(User user, int expectedCodeResult, String expectedMessage) {
        this.user = user;
        this.expectedCodeResult = expectedCodeResult;
        this.expectedMessage = expectedMessage;
    }

    @Parameterized.Parameters(name = "Тестовые данные: {0} {1}")
    public static Object[] getTestData() {
        return new Object[][] {
                {User.getUserWithoutEmail(), SC_FORBIDDEN, "Email, password and name are required fields"},
                {User.getUserWithoutPassword(), SC_FORBIDDEN, "Email, password and name are required fields"},
                {User.getUserWithoutName(), SC_FORBIDDEN, "Email, password and name are required fields"},
        };
    }

    @Test
    @DisplayName("User can not be created with invalid data")
    @Description("Проверка регистрации пользователя с неполными данными")
    public void createCourierWithInvalidData() {
        ValidatableResponse response = new UserClient().createUser(user);
        int actualCodeResult = response.extract().statusCode();
        String actualMessage = response.extract().path("message");

        Assert.assertEquals(expectedCodeResult, actualCodeResult);
        Assert.assertEquals(expectedMessage, actualMessage);

    }

}
