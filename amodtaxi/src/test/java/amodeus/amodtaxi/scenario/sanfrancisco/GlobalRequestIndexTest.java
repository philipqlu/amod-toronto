package amodeus.amodtaxi.scenario.sanfrancisco;

import java.util.Iterator;

import junit.framework.TestCase;

public class GlobalRequestIndexTest extends TestCase {
    public void test() {
        GlobalRequestIndex index = new GlobalRequestIndex();
        int first = index.add(1, 1);
        int second = index.add(1, 2);
        int third = index.add(2, 1);
        int fourth = index.add(2, 2);

        assertEquals(1, first);
        assertEquals(2, second);
        assertEquals(3, third);
        assertEquals(4, fourth);

        assertEquals(second, (int) index.add(1, 2));

        Iterator<Integer> actual = index.getGlobalIDs().iterator();
        for (int i = 1; i < 5; i++)
            assertEquals(i, (int) actual.next());
        assertTrue(!actual.hasNext());
    }
}
