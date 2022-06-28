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
import site.stellarburgers.data.UserCredentialsForUpdate;
import static org.apache.http.HttpStatus.*;

public class UpdateUserCredentialsTest {

        private User user;
        private UserClient userClient;
        private String token;
        private String authorization;

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
        @DisplayName("User can be updated with authorization")
        @Description("Проверка изменения данных пользователя с авторизацией")
        public void userCanUpdateCredentialsWithAuthorization()  {

            ValidatableResponse response = userClient.createUser(user);

            authorization = response.extract().path("accessToken").toString().substring(7);
            token = response.extract().path("refreshToken");
            String name =  response.extract().jsonPath().getJsonObject("user.name");
            String email = response.extract().jsonPath().getJsonObject("user.email");
            String password = user.password;

            ValidatableResponse responseUpdate = userClient.
                    updateUserDataWithAuthorization(UserCredentialsForUpdate.
                            getUserCredentialsWithNewCredentials(user,authorization));

            int statusCode = responseUpdate.extract().statusCode();
            boolean isSuccess = responseUpdate.extract().path("success");
            String nameBeforeUpdate =  responseUpdate.extract().jsonPath().getJsonObject("user.name");
            String emailBeforeUpdate = responseUpdate.extract().jsonPath().getJsonObject("user.email");
            String passwordBeforeUpdate = user.password;

            Assert.assertEquals(statusCode, SC_OK);
            Assert.assertTrue(isSuccess);
            Assert.assertNotEquals(name, nameBeforeUpdate);
            Assert.assertNotEquals(email, emailBeforeUpdate);
            Assert.assertNotEquals(password, passwordBeforeUpdate);
        }

        @Test
        @DisplayName("User can not be updated without authorization")
        @Description("Проверка изменения данных пользователя без авторизации")
        public void userCanNotUpdateCredentialsWithoutAuthorization()  {

            ValidatableResponse response = userClient.createUser(user);

            authorization = response.extract().path("accessToken").toString().substring(7);
            token = response.extract().path("refreshToken");

            ValidatableResponse responseUpdate = userClient.
                    updateUserDataWithoutAuthorization(UserCredentialsForUpdate.
                            getUserCredentialsWithoutAuthorization(user));

            int statusCode = responseUpdate.extract().statusCode();
            String messageError = responseUpdate.extract().path("message");

            Assert.assertEquals(statusCode, SC_UNAUTHORIZED);
            Assert.assertEquals(messageError, "You should be authorised");

}
}
