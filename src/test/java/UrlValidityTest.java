import me.theentropyshard.jsonviewer.utils.Utils;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class UrlValidityTest {
    @Test
    void test_url_is_not_invalid() {
        assertFalse(Utils.isUrlInvalid("https://google.com"));
        assertFalse(Utils.isUrlInvalid("https://google.com/"));
        assertFalse(Utils.isUrlInvalid("https://google.com/hello"));
        assertFalse(Utils.isUrlInvalid("https://google.com/hello/there"));
        assertFalse(Utils.isUrlInvalid("https://google.com/hello/there?ok=true"));
        assertFalse(Utils.isUrlInvalid("https://google.com/hello/there?ok=true&hello=world"));
    }

    @Test
    void test_url_is_invalid() {
        assertTrue(Utils.isUrlInvalid(""));
        assertTrue(Utils.isUrlInvalid("pajdwjjdpwpadjw"));
        assertTrue(Utils.isUrlInvalid("https"));
        assertTrue(Utils.isUrlInvalid("https://"));
        assertTrue(Utils.isUrlInvalid("https://google.com/ /hello"));
    }
}
