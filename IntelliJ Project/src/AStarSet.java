import java.util.*;

public class AStarSet
{
    private Map<Node, AStarNode> map;
    private SortedSet<AStarNode> set;

    public AStarSet()
    {
        map = new HashMap<>();
        set = new TreeSet<>();
    }

    public AStarNode update(AStarNode n)
    {
        if(!map.containsKey(n))
        {
            map.put(n, n);
            set.add(n);
            return n;
        }
        else
        {
            AStarNode previousInstance = map.get(n);
            if (previousInstance.getPathLength() == n.getPathLength())
            {
                previousInstance.addPrevious(n.getPreviousNodes());
                return previousInstance;
            }
            else if (previousInstance.getPathLength() > n.getPathLength())
            {
                map.replace(n, n);
                set.remove(n);
                set.add(n);
                return n;
            }

            return previousInstance;
        }
    }

    public AStarNode peekBest()
    {
        return set.first();
    }

    public void clear()
    {
        map.clear();
        set.clear();
    }

    public boolean contains(AStarNode n)
    {
        return  map.containsKey(n);
    }
    
    public AStarNode removeBest()
    {
        AStarNode best = set.first();
        set.remove(best);
        map.remove(best);
        return best;
    }

    public void addAll(List<AStarNode> nodeList)
    {
        for (AStarNode n: nodeList)
        {
            update(n);
        }
    }

    public  boolean isEmpty()
    {
        return map.isEmpty();
    }
}
