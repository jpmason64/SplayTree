/****************
 * Splay Tree Tester
 * Author: Christian Duncan
 * Spring 2024
 * 
 * This tests the performance of the splay tree augmented with a range count
 * There is a basic testing feature that inserts entries, 
 * deletes some entries, then performs a range count.
 * 
 * Then there are some larger queries with simple to compute ranges (with the set known)
 * The larger ones can be used to test efficiency.
 * The 1_000_000 entry ones will take a little time but if done efficiently should still
 * finish in about a minute if not sooner.
 */
import java.util.Random;
import java.util.Collections;
import java.util.ArrayList;

public class SplayTreeTester {
    static Random rand;

    public static void main(String[] args) {
        rand = new Random();

        testBasic();

        testInsertOnly(100, 10, true);
        testInsertOnly(1_000, 1_000, true);

        if (false) {
            // Only test these if you are ready to test performance as well
            testInsertOnly(1_000_000, 1_000, false);
            testInsertOnly(1_000_000, 1_000, true);
            testInsertOnly(1_000_000, 1_000_000, false);
            testInsertOnly(1_000_000, 1_000_000, true);
        }

        testInsertDelete(100, 10, true);
        testInsertDelete(1_000, 1_000, true);

        if (false) {
            // Only test these if you are ready to test performance as well
            testInsertDelete(1_000_000, 1_000, false);
            testInsertDelete(1_000_000, 1_000, true);
            testInsertDelete(1_000_000, 1_000_000, false);
            testInsertDelete(1_000_000, 1_000_000, true);
        }
    }

    public static void testInsertOnly(int numElements, int numRanges, boolean randomOrder) {
        System.out.println("Testing insert only (n=" + numElements +") and (m=" + numRanges +")" + (randomOrder ? " [Random Order]" : ""));
        System.out.println("   Creating elements");
        ArrayList<Integer> list = new ArrayList<>(numElements);
        // Pick a random skip value K
        int k = rand.nextInt(10)+1;
        for (int i = 0; i < numElements; i++) {
            list.add(i*k);
        }

        // Shuffle the list (if using random order)
        if (randomOrder) Collections.shuffle(list, rand);

        System.out.println("   Inserting elements" + (randomOrder ? " (in random order)" : ""));
        SplayTree<Integer> tree = new SplayTree<>();
        for (Integer e: list) {
            tree.insert(e);
        }

        System.out.println("   Testing range count");
        // Now pick a few ranges
        for (int i = 0; i < numRanges; i++) {
            int a = rand.nextInt(numElements);
            int b = rand.nextInt(numElements);
            if (b < a) {
                // Swap them.
                int t = b;
                b = a;
                a = t;
            }
            int count = tree.rangeCount(a*k, b*k);
            int expected = (b-a+1);
            if (count != expected) {
                System.out.println("Error: Tree returned " + count + " but expected " + expected + ". Aborting.");
                System.exit(1);
            }
        }
    }

    public static void testInsertDelete(int numElements, int numRanges, boolean randomOrder) {
        System.out.println("Testing insert and delete (n=" + numElements + ") and (m=" + numRanges + ")" + (randomOrder ? " [Random Order]" : ""));
        System.out.println("   Creating elements");
        ArrayList<Integer> insertList = new ArrayList<>(numElements);
        ArrayList<Integer> deleteList = new ArrayList<>(numElements);
        // Pick a random skip value K
        int k = rand.nextInt(10) + 1;
        for (int i = 0; i < numElements; i++) {
            insertList.add(i * k);
            if (i % 2 == 0) deleteList.add(i * k);  // Delete every other element
        }

        // Shuffle the list (if using random order)
        if (randomOrder) {
            Collections.shuffle(insertList, rand);
            Collections.shuffle(deleteList, rand);
        }

        System.out.println("   Inserting elements" + (randomOrder ? " (in random order)" : ""));
        SplayTree<Integer> tree = new SplayTree<>();
        for (Integer e : insertList) {
            tree.insert(e);
        }

        System.out.println("   Deleting some elements" + (randomOrder ? " (in random order)" : ""));
        for (Integer e : deleteList) {
            tree.delete(e);
        }

        System.out.println("   Testing range count");
        // Now pick a few ranges
        for (int i = 0; i < numRanges; i++) {
            int a = rand.nextInt(numElements);
            int b = rand.nextInt(numElements);
            while (b == a) b = rand.nextInt(numElements); // Rare, but avoid it
            if (b < a) {
                // Swap them.
                int t = b;
                b = a;
                a = t;
            }
            int count = tree.rangeCount(a * k, b * k);
            int expected = (b - a + 1);
            if (a %2 == 0) expected--; // a was deleted
            if (b %2 == 0) expected--; // b was deleted
            expected = expected/2 + 1; // Every other one

            if (count != expected) {
                System.out.println("Error: Tree returned " + count + " but expected " + expected + ". Aborting.");
                System.exit(1);
            }
        }
    }

    static void testBasic() {
        System.out.println("Testing basic simple functionality.");
        SplayTree<Integer> tree = new SplayTree<>();
        tree.insert(1);
        tree.insert(10);
        tree.insert(3);
        tree.insert(5);
        tree.insert(8);
        tree.insert(12);
        tree.delete(5);
        tree.delete(3);
        tree.insert(4);
        tree.printTree(System.out);  // Uncomment for some debugging if needed
        int res = tree.rangeCount(2,10); // Should return 3
        if (res != 3) {
            System.out.println("Error: Tree returned " + res + " but 3 was expected. Aborting!");
            System.exit(1);
        }
        // tree.printTree(System.out); // Uncomment for some debugging if needed
    }    
}
