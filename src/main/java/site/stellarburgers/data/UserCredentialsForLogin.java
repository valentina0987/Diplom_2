package site.stellarburgers.data;
import com.github.javafaker.Faker;

public class UserCredentialsForLogin {
    public final String email;
    public final String password;

    static Faker faker = new Faker();

    public UserCredentialsForLogin(String email, String password) {
        this.email = email;
        this.password = password;
    }

    public static UserCredentialsForLogin from(User user) {
        return new UserCredentialsForLogin(user.email, user.password);
    }


    public static UserCredentialsForLogin getUserCredentialsWithInvalidEmailAndPassword(User user){
        user.setUserEmail(faker.internet().emailAddress());
        user.setUserPassword(faker.internet().password());
        return new UserCredentialsForLogin(user.email, user.password);
    }
}
