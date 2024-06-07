import me.theentropyshard.jsonviewer.utils.Utils;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class StringCapitalizeTest {
    @Test
    void test_capitalize() {
        assertEquals("Hello", Utils.capitalize("hello"));
        assertEquals("Hello", Utils.capitalize("Hello"));
        assertEquals("HELLO", Utils.capitalize("HELLO"));
    }
}
