package fanficsBlog.security;

public class UserIsBannedException extends Exception {

    public UserIsBannedException(String message) {
        super(message);
    }

}
