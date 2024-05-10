
/****************
 * Splay Tree
 * Author: Christian Duncan
 * Authors: Jonathan Mason
 * Spring 2024
 * 
 * This is a basic implementation of a Splay Tree which is augmented with a
 * size field. It supports inserting, deleting, and range count.
 * 
 * NOTE:
 * Currently, the size field is not being updated correctly and the range count 
 * functionality (namely the helper methods) is not fully working.
 * The methods that need repair are higlighted with "NOTE: REPAIR PLEASE"
 * 
 * The methods to repair are:
 *      insert: The sizes of all the ancestors of newest node must increase by one
 *      delete: The sizes of all the ancestors of deleted node must decrease by one
 *      rotateLeft: The sizes of the two changed nodes - u and v - must be updated
 *      rotateRight: The sizes of the two changed nodes - u and v - must be updated
 *      countLessThan: The sizes need to be used to determine the count
 */
import java.io.PrintStream;

public class SplayTree<E> {
    // A single node of the splay tree
    class TreeNode<E> {
        TreeNode<E> left;    // Left child
        TreeNode<E> right;   // Right child
        TreeNode<E> parent;  // Parent (null if node is the root)
        int size;            // Number of elements in this node's subtree
        E element;

        TreeNode(E element) {
            this.element = element;
            size = 1;  // Every node has at least their node in their subtree!
            left = right = parent = null;
        }
    }

    TreeNode<E> root;

    public SplayTree() {
        root = null;
    }

    // Insert the given element into the Splay Tree
    // This is a set so if the element is already present, then it will not
    // be inserted.
    // NOTE: REPAIR PLEASE (see note in method)
    public void insert(E element) {
        if (root == null) {
            root = new TreeNode<>(element);
        } else {
            TreeNode<E> location = find(element, false);
            int comp = compare(location.element, element);
            if (comp != 0) {
                // A new node needs to be created and added
                TreeNode<E> newNode = new TreeNode<>(element);
                newNode.parent = location; // Update the parent of this node
                if (comp < 0) {
                    // Element will go to the right
                    assert (location.right == null);
                    location.right = newNode;
                } else {
                    // Element will go to the left
                    assert (location.left == null);
                    location.left = newNode;
                }

                // ********************
                // NOTE: REPAIR PLEASE
                // 
                // Update the sizes for all ancestors of location node (starting with location)
                //       You need to update the size of each node going up the parent
                //       until the root. Each node along the way should increase their
                //       size by 1 (to reflect the newly inserted node)
                
                TreeNode<E> updateNode = location;
                // Insert code here!
                // ********************
                while(updateNode != null){
                    updateNode.size++;
                    updateNode = updateNode.parent;
                }


                // Identify where to do the splaying
                location = newNode;
            }

            // Splay from the location node
            splay(location);
        }
    }

    // Delete the element from the tree
    // Returns true if the element was found and deleted
    // Returns false if the element was not found in the Tree
    // NOTE: REPAIR PLEASE (see note in method)
    public boolean delete(E element) {
        if (root == null) return false; // Nothing to delete
        TreeNode<E> location = find(element, false);
        int comp = compare(location.element, element);
        if (comp == 0) {
            // Found the node to delete
            if (location.left != null && location.right != null) {
                // Both children are not null. Move right most node on left to here
                TreeNode<E> sub = location.left;
                while (sub.right != null) sub = sub.right;
                location.element = sub.element; // Move its content over
                location = sub;  // This is the one to delete
            }

            // Delete the node at location (at most one child node)
            TreeNode<E> parent = location.parent;
            TreeNode<E> child = location.left == null ? location.right : location.left;
            // Make the parent point to the child (special case for root)
            if (location == root) {
                root = child;
            } else {
                if (parent.left == location) parent.left = child;
                else parent.right = child;                    
            }

            // Update the child's parent
            if (child != null) child.parent = parent;

            // ********************
            // NOTE: REPAIR PLEASE
            //
            // Update the sizes for all ancestors of parent node (starting with parent)
            // You need to update the size of each node going up the parent
            // until the root. Each node along the way should decrease their
            // size by 1 (to reflect the newly inserted node)

            TreeNode<E> updateNode = parent;
            // Insert code here!
            // ********************
            while(updateNode != null){
                updateNode.size--;
                updateNode = updateNode.parent;
            }

            
            // Identify where to do the splaying
            location = parent;
        }

        // Splay the location node to the root
        splay(location);

        return comp == 0;  // True = deleted node, False = we didn't
    }

    // Returns how many elements are between a (inclusive) and b (inclusive)
    // Assumes a <= b.
    public int rangeCount(E a, E b) {
        // This can be done by determining how many elements are less than b
        // and how many are less than a and taking the difference!

        int lessThanA = countLessThan(a, false, true); // all < a
        // System.out.println("DEBUG: <" + a + " = " + lessThanA);
        int lessThanB = countLessThan(b, true, true);  // all <= b
        // System.out.println("DEBUG: <=" + b + " = " + lessThanB);
        return lessThanB - lessThanA;  // Number of elements between a and b.
    }

    // Display the tree to the provided output stream
    // For debuggging, reports the tree with element and size for each node
    public void printTree(PrintStream out) {
        printTree(out, root, "");
    }

    // *************************************
    // Helper functions
    // *************************************
    // Display the subtree from the given node to the provided output stream
    // For debuggging, reports the tree with element and size for each node
    void printTree(PrintStream out, TreeNode<E> node, String indent) {
        if (node == null) {
            out.println(indent + "()");
        } else {
            out.println(indent + "(" + node.element + " " + "Size: " + node.size);
            printTree(out, node.left, indent + "  ");
            printTree(out, node.right, indent + "  ");
            out.println(indent + ")");
        }
    }

    // Compare two elements using their natural ordering
    @SuppressWarnings("unchecked")
    int compare(E a, E b) {
        return ((Comparable) a).compareTo(b);
    }

    // Find the node where the element resides
    // If splay is true, it splays the last node to the root
    //    For efficiency, if this method does not splay then the calling method should
    // Returns a reference to the Node or the leaf node of this element's potential
    // parent.
    // Null is returned if the tree is empty.
    TreeNode<E> find(E e, boolean splay) {
        if (root == null)
            return null;

        TreeNode<E> last = null;
        TreeNode<E> curr = root;
        while (curr != null) {
            last = curr;
            int comp = compare(curr.element, e);
            if (comp == 0) {
                // Found the element
                break;
            } else if (comp < 0) {
                // Element e must lie to the right
                curr = curr.right;
            } else {
                // Element e must lie to the left (no change to count)
                curr = curr.left;
            }
        }

        if (splay) {
            // Splay the last node up
            splay(last);
        }

        return last;
    }

    // Return how many nodes in tree are less than (or equal to) element e
    // If inclusive: less than or equal to
    // Otherwise: strictly less than
    // If splay is true, then the last node visited is splayed to the top
    // For efficiency, this should be true (or the calling method should somehow do it)
    // NOTE: REPAIR PLEASE (see note in method)
    int countLessThan(E e, boolean inclusive, boolean splay) {
        if (root == null) return 0;

        // NOTE: REPAIR PLEASE
        //       This function does not perform as needed.
        //       It should traverse down the tree to find the node with element E
        //       (for illustration inspect how find works)
        //       Along the way, if making a right branch, then include the total of
        //       all elements in left child's size field as well as the current node
        //       since they are all less than e
        //       If the node is found, also include all the elements in that final node's
        //       left child as well, for the same reason.
        // 
        //       For efficiency, be sure to run a splay from the last (lowest) node explored!

        // Currently it just performs the find without tracking count properly!
        TreeNode<E> last = null;
        TreeNode<E> curr = root;
        int count = 0;
        while (curr != null) {
            last = curr;
            int comp = compare(curr.element, e);
            if (comp == 0) {
                // Found the element
                if (inclusive){
                    if(curr.left != null){
                        count += curr.left.size + 1;
                    }else{ 
                        count++;  // Include this node as well
                    }
                } 
                break;
            } else if (comp < 0) {
                // Element e must lie to the right
                if(curr.left != null){
                    count += curr.left.size + 1;
                }else{
                    count++;
                }
                curr = curr.right;
            } else {
                // Element e must lie to the left (no change to count)
                curr = curr.left;
            }
        }

        if (splay) {
            // Splay the last node up
            splay(last);
        }

        return count;
    }

    // Perform a left rotation of the given node U and its right child V
    // This makes V the parent node and U the left child of V.
    // Subtrees and sizes have to be updated appropriately.
    // NOTE: Since the two nodes are not swapped. U points to the same node which is now 
    //       the content of V.  So, conceptually, U now points to what V was.
    // NOTE: REPAIR PLEASE (see note in method)
    void rotateLeft(TreeNode<E> u) {
        TreeNode<E> v = u.right;
        assert(v != null);  // This should not be called if u has no right child
        
        // Rather than move u and v, we shall swap contents (no need to adjust u's parent!)
        E temp = u.element;
        u.element = v.element;
        v.element = temp;

        // Adjust the left/right children to perform the rotation
        u.right = v.right;
        if (u.right != null) u.right.parent = u;
        v.right = v.left;
        v.left = u.left;
        if (v.left != null) v.left.parent = v;
        u.left = v;
        v.parent = u;

        // NOTE: REPAIR PLEASE. SIZE IS NOT UPDATED
        // The only two node's whose sizes changed as a result of rotation are u and v
        // u's size remains the same
        // v's size can just be computed
        v.size = u.size - v.size;
    }

    // Perform a right rotation of the given node U and its left child V
    // This makes V the parent node and U the right child of V.
    // Subtrees and sizes have to be updated appropriately.
    // NOTE: Since the two nodes are not swapped. U points to the same node which is now
    //       the content of V. So, conceptually, U now points to what V was.
    // NOTE: REPAIR PLEASE (see note in method)
    void rotateRight(TreeNode<E> u) {
        TreeNode<E> v = u.left;
        assert (v != null); // This should not be called if u has no right child

        // Rather than move u and v, we shall swap contents (no need to adjust u's
        // parent!)
        E temp = u.element;
        u.element = v.element;
        v.element = temp;

        // Adjust the left/right children to perform the rotation
        u.left = v.left;
        if (u.left != null) u.left.parent = u;
        v.left = v.right;
        v.right = u.right;
        if (v.right != null) v.right.parent = v;
        u.right = v;
        v.parent = u;

        // NOTE: REPAIR PLEASE. SIZE IS NOT UPDATED
        // The only two node's whose sizes changed as a result of rotation are u and v
        // u's size remains the same
        // v's size can just be computed
        v.size = u.size - v.size;
    }

    // Perform a splay of the current node all the way to the root
    void splay(TreeNode<E> current) {
        while (current.parent != null) {
            // Are we a left child of parent?
            boolean leftChild = current.parent.left == current;
            if (current.parent.parent == null) {
                // Just a zig
                current = current.parent;
                if (leftChild) rotateRight(current);
                else rotateLeft(current);
            } else {
                // Is our parent a left child?
                boolean leftGrandChild = current.parent.parent.left == current.parent;
                if (leftChild && leftGrandChild) {
                    // Zig-zig (down left side)
                    rotateRight(current.parent.parent);
                    current = current.parent;
                    rotateRight(current);
                } else if (!leftChild && !leftGrandChild) {
                    // Zig-zig (down right side)
                    rotateLeft(current.parent.parent);
                    current = current.parent;
                    rotateLeft(current);
                } else if (leftChild) {
                    // Zig-zag right/left
                    current = current.parent;
                    rotateRight(current);
                    current = current.parent;
                    rotateLeft(current);  // Our grandparent is now our parent
                } else {
                    // Zig-zag left/right
                    current = current.parent;
                    rotateLeft(current);
                    current = current.parent;
                    rotateRight(current);
                }
            }
        }
    }
}
