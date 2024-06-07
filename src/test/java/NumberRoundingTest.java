import me.theentropyshard.jsonviewer.utils.Utils;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class NumberRoundingTest {
    @Test
    void test_throws_on_negative() {
        assertThrows(IllegalArgumentException.class, () -> Utils.round(14124.2451, -1));
        assertThrows(IllegalArgumentException.class, () -> Utils.round(14124.2451, -2));
        assertThrows(IllegalArgumentException.class, () -> Utils.round(14124.2451, -3));
    }

    @Test
    void test_rounding() {
        assertEquals(14124.2, Utils.round(14124.2451, 1));
        assertEquals(14124.25, Utils.round(14124.2451, 2));
        assertEquals(14124.245, Utils.round(14124.2451, 3));
        assertEquals(14124.2451, Utils.round(14124.2451, 4));

        assertEquals(134.5, Utils.round(134.45, 1));
        assertEquals(134.4, Utils.round(134.44, 1));
        assertEquals(134.5, Utils.round(134.47, 1));
    }
}
