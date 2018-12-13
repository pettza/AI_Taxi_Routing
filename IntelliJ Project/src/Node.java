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

    public Node(Node n)
    {
        x = n.x;
        y = n.y;
        id = n.id;
    }

    public double GetX()
    {
        return x;
    }

    public double GetY()
    {
        return y;
    }

    public long GetId()
    {
        return id;
    }

    public boolean equals(final Node n)
    {
        return id == n.id;
    }

    public double distance_from(final Node n)
    {
        double dx = x - n.x;
        double dy = y - n.y;
        return Math.sqrt(dx * dx + dy * dy);
    }

    public int hashCode()
    {
        return (int) id;
    }
}
