import io.javalin.Javalin;
import org.itmo.testing.lab2.controller.UserAnalyticsController;

public class Main {
    private static final int port = 7000;

    /**
     * Test the application
     * @param args command line arguments
     */
    public static void main(String[] args) {
        Javalin app = UserAnalyticsController.createApp();
        app.start(port);
    }
}
