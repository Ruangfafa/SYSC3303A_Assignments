import service.LoggerService;
import java.util.logging.Logger;

public class Application {
    public static void main(String[] args) {
        Logger logger = LoggerService.getLogger(true);
    }
}
