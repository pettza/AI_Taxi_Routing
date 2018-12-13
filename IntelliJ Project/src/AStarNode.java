import java.util.List;
import java.util.LinkedList;

public class AStarNode extends Node implements Comparable<AStarNode>
{
    private List<AStarNode> previousNodes;
    private double pathLength, heuristic;

    public AStarNode(Node n, double pl_v, double h_v, AStarNode previous)
    {
        super(n);
        pathLength = pl_v;
        heuristic = h_v;
        previousNodes = new LinkedList<>();
        previousNodes.add(previous);
    }

    public int compareTo(AStarNode n)
    {
        return Double.compare(F(), n.F());
    }

    public AStarNode(Node n, double pl_v, double h_v, List<AStarNode> previous)
    {
        super(n);
        pathLength = pl_v;
        heuristic = h_v;
        previousNodes = new LinkedList<>(previous);
    }

    public double F()
    {
        return pathLength + heuristic;
    }

    public double getPathLength()
    {
        return pathLength;
    }

    public void addPrevious(List<AStarNode> n)
    {
        previousNodes.addAll(n);
    }

    public List<AStarNode> getPreviousNodes()
    {
        return previousNodes;
    }
}