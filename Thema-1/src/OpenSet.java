import java.util.Map;
import java.util.HashMap;
import java.util.List;
import java.util.PriorityQueue;

public class OpenSet
{
    private Map<AStarNode, AStarNode> map;
    private PriorityQueue<AStarNode> queue;

    public OpenSet()
    {
        map = new HashMap<>();
        queue = new PriorityQueue<>();
    }

    public void add(AStarNode n)
    {
        if(!map.containsKey(n))
        {
            map.put(n, n);
            queue.add(n);
        }
        else
        {
            AStarNode previousInstance = map.get(n);
            if (previousInstance.getPathLength() == n.getPathLength())
            {
                previousInstance.addPrevious(n.getPreviousNodes());
            }
        }
    }

}
