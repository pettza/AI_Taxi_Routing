import java.util.HashSet;
import java.util.Set;

//Extension of Node class for use in the A* algorithm

public class AStarNode extends Node implements Comparable<AStarNode>
{
    //List of predecessors in different paths of equal optimal length
    private Set<AStarNode> previousNodes;
    private double pathLength, heuristic;

    public AStarNode(Node n, double pl_v, double h_v, Set<AStarNode> previous)
    {
        super(n);
        pathLength = pl_v;
        heuristic = h_v;
        previousNodes = new HashSet<>(previous);
    }

    public AStarNode(Node n, double pl_v, double h_v, AStarNode previous)
    {
        super(n);
        pathLength = pl_v;
        heuristic = h_v;
        previousNodes = new HashSet<>();
        previousNodes.add(previous);
    }

    @Override
    public int compareTo(final AStarNode n)
    {
        int cmp = Double.compare(F(), n.F());
        if (cmp != 0) return cmp;

        return Long.compare(getId(), n.getId());
    }

    public double F()
    {
        return pathLength + heuristic;
    }

    public double getPathLength()
    {
        return pathLength;
    }

    public void addPrevious(Set<AStarNode> previous)
    {
        previousNodes.addAll(previous);
    }

    public final Set<AStarNode> getPreviousNodes()
    {
        return previousNodes;
    }
}
