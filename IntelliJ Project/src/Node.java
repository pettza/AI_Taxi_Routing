import java.lang.Math;

public class Node
{
    private double x;
    private double y;
    private long id;

    public Node(double x_v, double y_v, long id_v)
    {
        x = x_v;
        y = y_v;
        id = id_v;
    }

    public Node(final Node n)
    {
        x = n.x;
        y = n.y;
        id = n.id;
    }

    public double getX()
    {
        return x;
    }

    public double getY()
    {
        return y;
    }

    public long getId()
    {
        return id;
    }

    @Override
    public boolean equals(final Object obj)
    {
        if (obj == null)
        {
            return false;
        }

        if (!Node.class.isAssignableFrom(obj.getClass()))
        {
            return false;
        }

        final Node n = (Node) obj;
        return id == n.id;
    }

    public double distanceFrom(final Node n)
    {
        double dx = x - n.x;
        double dy = y - n.y;
        return Math.sqrt(dx * dx + dy * dy);
    }

    @Override
    public int hashCode()
    {
        return (int) id;
    }
}
