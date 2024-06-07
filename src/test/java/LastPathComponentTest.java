import me.theentropyshard.jsonviewer.utils.Utils;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class LastPathComponentTest {
    @Test
    void test_components() {
        assertEquals("google.com", Utils.getLastPathComponent("https://google.com"));
        assertEquals("google.com", Utils.getLastPathComponent("https://google.com/"));
        assertEquals("search", Utils.getLastPathComponent("https://google.com/search"));
        assertEquals("search", Utils.getLastPathComponent("https://google.com/search?q=lol+kek"));
    }
}
