public class NodoAStar implements Comparable<NodoAStar> {
    public int x, y;
    public int momevent_cost, heuristic_cost, total_cost;
    public NodoAStar parent;

    public NodoAStar(int x, int y, int momevent_cost, int heuristic_cost, NodoAStar parent) {
        this.x = x; // Node X coordinate
        this.y = y; // Node Y coordinate
        this.momevent_cost = momevent_cost;
        this.heuristic_cost = heuristic_cost;
        this.total_cost = momevent_cost + heuristic_cost;
        this.parent = parent;
    }

    @Override
    public int compareTo(NodoAStar other) {
        return Integer.compare(this.total_cost, other.total_cost);
    }

    @Override
    public int hashCode() {
        return x + y * 1000;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj instanceof NodoAStar) {
            NodoAStar other = (NodoAStar) obj;
            return this.x == other.x && this.y == other.y;
        }
        return false;
    }
}
