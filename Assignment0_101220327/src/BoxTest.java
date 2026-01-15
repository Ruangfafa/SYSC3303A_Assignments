import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class BoxTest {

    @Test
    void test() {
        Box box = new Box();

        assertTrue(box.isEmpty());

        box.put("Ruangfafa.");

        assertFalse(box.isEmpty());

        Object result = box.get();
        System.out.println(result);

        assertTrue(box.isEmpty());
    }
}