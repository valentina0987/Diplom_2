package site.stellarburgers.data;
import com.github.javafaker.Faker;

public class User {

    public String name;
    public String password;
    public String email;

    static Faker faker = new Faker();

    public User(String name, String password, String email){
        this.name = name;
        this.password = password;
        this.email = email;
    }

    public User(){}

    public User setUserName(String name){
        this.name = name;
        return this;
    }

    public User setUserPassword(String password){
        this.password = password;
        return this;
    }

    public User setUserEmail(String email){
        this.email = email;
        return this;
    }

    public static User getUserRandom(){

        final String name = faker.name().firstName();
        final String password = faker.internet().password();
        final String email = faker.internet().emailAddress();
        return new User(name, password, email);
    }

    public static User getUserWithoutEmail(){
        return new User().
                setUserName(faker.name().firstName()).
                setUserPassword(faker.internet().password());
    }

    public static User getUserWithoutName(){
        return new User().
                setUserEmail(faker.internet().emailAddress()).
                setUserPassword(faker.internet().password());
    }

    public static User getUserWithoutPassword(){
        return new User().
                setUserName(faker.name().firstName()).
                setUserEmail(faker.internet().emailAddress());
    }
}
