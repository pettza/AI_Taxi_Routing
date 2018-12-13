import java.util.List;
import java.util.LinkedList;

public class StreetNode extends Node {
    private List<Long> street_ids;

    public StreetNode(double x_v, double y_v, long id_v, Long street_id)
    {
        super(x_v, y_v, id_v);
        street_ids = new LinkedList<>();
        street_ids.add(street_id);
    }

    public StreetNode(double x_v, double y_v, long id_v, List<Long> street_ids_v)
    {
        super(x_v, y_v, id_v);
        street_ids = street_ids_v;
    }

    public void addStreet(long street_id)
    {
        street_ids.add(street_id);
    }
}
