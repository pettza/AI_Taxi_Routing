import java.util.LinkedList;

public class test
{
    public static void main(String[] args)
    {
        Graph gr = new Graph("Data\\test.txt");
        AStarifier a = new AStarifier();
        LinkedList<Node> l = new LinkedList<>();
        l.add(new Node(1, 2, 0));
        a.AStarify(gr, l, null);
    }
}
