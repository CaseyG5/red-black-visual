package redblackst;

import edu.princeton.cs.algs4.StdDraw;

class RBST<Key extends Comparable, Value> {
    
    private static final boolean RED = true;
    private static final boolean BLACK = false;
    
    private static final double DRAW_WIDTH = 1000.0;
    private static final double DRAW_HEIGHT = 400.0;
    private static final double C = 50.0;       // spacial constant for drawing
    
    private static double deltaX;
    private static double deltaY;
    
    private static double x0, y0, x1, y1;
    
    private Node root;
    
    private class Node<Key, Value> {
        private Key key;
        private Value val;
        private Node left, right;
        private int N;
        private boolean color;
        
        public Node(Key k, Value v, boolean c) {
            key = k;
            val = v;
            N = 1;
            color = c;
        }
    }
    
    private boolean isRed(Node h) {
        if(h == null) return false;
        return h.color == RED;
    }
    
    private Node rotateLeft(Node h) {
        Node x = h.right;
        h.right = x.left;
        x.left = h;
        x.color = h.color;
        h.color = RED;
        x.N = h.N;
        h.N = size(h.left) + size(h.right) + 1;
        return x;
    }
    
    private Node rotateRight(Node h) {
        Node x = h.left;
        h.left = x.right;
        x.right = h;
        x.color = h.color;
        h.color = RED;
        x.N = h.N;
        h.N = size(h.left) + size(h.right) + 1;
        return x;
    }
    
    private void flipColors(Node h) {
        h.color = !h.color;
        h.left.color = !h.left.color;
        h.right.color = !h.right.color;
    }
    
    public int size() {
        return size(root);
    }
    private int size(Node h) {
        if(h == null) return 0;
        return h.N;
    }
    
    private boolean isEmpty() {
        return size() == 0;
    }
    
    public boolean contains(Key k) {
        return get(k) != null;
    }
    
    // insert and search methods
    public void put(Key k, Value v) {
        root = put(root, k, v);
        root.color = BLACK;
    }
    private Node put(Node h, Key k, Value v) {
        if(h == null) return new Node(k, v, RED);
        int cmp = k.compareTo(h.key);
        if(cmp < 0) h.left = put(h.left, k, v);
        else if(cmp > 0) h.right = put(h.right, k, v);
        else h.val = v;
        
        if(isRed(h.right) && !isRed(h.left)) h = rotateLeft(h);
        if(isRed(h.left) && isRed(h.left.left)) h = rotateRight(h);
        if(isRed(h.left) && isRed(h.right)) flipColors(h);
        
        h.N = size(h.left) + size(h.right) + 1;
        return h;
    }
    
    public Value get(Key k) {
        return get(root, k);
    }
    private Value get(Node h, Key k) {
        if(h == null) return null;
        int cmp = k.compareTo(h.key);
        if(cmp < 0) return get(h.left, k);
        else if(cmp > 0) return get(h.right, k);
        else return (Value) h.val;                      
    }
    
    public void printKeys() {
        prepDrawing();
        // starting point (root)
        x1 = 0.505 * DRAW_WIDTH;            // begin x coordinate slightly to 
        y1 = DRAW_HEIGHT - deltaY;          // the right because RB leans left
        
        printKeys(root);
        //System.out.println();
    }
    private void printKeys(Node h) {        // in-order traversal
        if(h == null) return;
        
        deltaX /= 2.0;                              // this allows the graph to curve
        x1 -= deltaX; y1 -= deltaY;                 // move "cursor" left & down
        x0 = x1; y0 = y1;
        printKeys(h.left);                          // recursive call to the left
        
        x1 += deltaX; y1 += deltaY;                 // move cursor right & up
                                                    
        if(isRed(h.left))                           // red link?
            StdDraw.setPenColor(StdDraw.RED);
        StdDraw.line(x0, y0, x1, y1);               // draw incline TO current node
        
        x0 = x1; y0 = y1;
        x1 += deltaX; y1 -= deltaY;                 // move cursor right & down
        StdDraw.setPenColor(StdDraw.BLACK);         // reset pen color 
        StdDraw.line(x0, y0, x1, y1);               // draw decline FROM current node
        
        StdDraw.setPenRadius(0.02);                 // enlarge pen radius
        if(isRed(h))                                // red node?
            StdDraw.setPenColor(StdDraw.RED);       
        StdDraw.point(x0, y0);                      // draw node
        
        StdDraw.setPenRadius(0.005);                // reset pen radius
        StdDraw.setPenColor(StdDraw.BLACK);         // and color
        
        printKeys(h.right);                         // recursive call to the right
        x1 -= deltaX; y1 += deltaY;                 // move cursor left and up
        x0 = x1; y0 = y1;
        deltaX *= 2.0;
    }
    
    // The following methods are to support deletion
    
    private Node moveRedLeft(Node h) {
        flipColors(h);
        if(isRed(h.right.left)) {
            h.right = rotateRight(h.right);
            h = rotateLeft(h);
        }
        return h;
    }
    
    private Node moveRedRight(Node h) {
        flipColors(h);
        if(isRed(h.left.left)) h = rotateRight(h);
        return h;
    }
    
    private Node balance(Node h) {
        if(isRed(h.right)) h = rotateLeft(h);
        if(isRed(h.left) && isRed(h.left.left)) h = rotateRight(h);
        if(isRed(h.left) && isRed(h.right)) flipColors(h);
        h.N = size(h.left) + size(h.right) + 1;
        return h;
    }
    
    private Node min(Node h) {
        if(h.left == null) return h;
        return min(h.left);
    }
    
    public void deleteMin() {
        if(!isRed(root.left) && !isRed(root.right))
            root.color = RED;
        root = deleteMin(root);
        if(!isEmpty()) root.color = BLACK;
    }
    private Node deleteMin(Node h) {
        if(h.left == null) return null;             // would not work with a BST
        if(!isRed(h.left) && !isRed(h.left.left))   
            h = moveRedLeft(h);                     // if 2-node, borrow a sibling
        h.left = deleteMin(h.left);
        return balance(h);
    }
    
    public void delete(Key k) {
        if(!isRed(root.left) && !isRed(root.right))
            root.color = RED;
        root = delete(root, k);
        if(!isEmpty()) root.color = BLACK;
    }
    private Node delete(Node h, Key k) {
        if(k.compareTo(h.key) < 0) {
            if(!isRed(h.left) && !isRed(h.left.left))
                h = moveRedLeft(h);
            h.left = delete(h.left, k);
        }
        else {
            if (isRed(h.left)) 
                h = rotateRight(h);
            if (k.compareTo(h.key) == 0 && h.right == null)
                return null;
            if (!isRed(h.right) && !isRed(h.right.left))
                h = moveRedRight(h);
            
            if(k.compareTo(h.key) == 0) {
                Node x = min(h.right);
                h.key = x.key;
                h.val = x.val;
                h.right = deleteMin(h.right);
            }
            else h.right = delete(h.right, k);
        }
        
        return balance(h);
    }
    
    // Initialize draw window/properties
    private void prepDrawing() { 
        StdDraw.setCanvasSize(1000, 400);           // Setup calls for StdDraw
        StdDraw.setXscale(0.1, DRAW_WIDTH);          
        StdDraw.setYscale(0.1, DRAW_HEIGHT);         
        StdDraw.setPenRadius(0.005);
        StdDraw.setPenColor(StdDraw.BLACK);
        
        // vertical unit is height / 2 lg C
        deltaY = DRAW_HEIGHT / (2 * (Math.log10(C) / Math.log10(2.0)) );
    
        // slope begins as 1/7
        deltaX = 14.0 * deltaY;
        //System.out.println("deltaX is " + deltaX);
    }
    
    // for testing random inserts
    
    private static void exch(Comparable a[], int i, int j)
    {   Comparable temp = a[i];  
        a[i] = a[j];  
        a[j] = temp;   
    }
    
    public static void shuffle(Comparable a[]) {
        for(int i=0; i<a.length; i++) {      // shuffle array
            int randindex;
            randindex = (int) (Math.random() * a.length);
            exch(a, i, randindex);
        }
    }
}

public class RedBlackST {
    public static void main(String[] args) {
        
        RBST<Integer, Integer> st = new RBST<>();
        
        System.out.println("Adding keys 1-60...");
        for(int i=1; i<61; i++)
            st.put(i,0);
        
        st.printKeys();
        
        try { Thread.sleep(5000); } catch(InterruptedException exc) {}  // pause
        
        System.out.println("Deleting 1-20");
        for(int i=1; i<21; i++)
            st.delete(i);
        
        st.printKeys();
    }
}
