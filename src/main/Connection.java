import java.util.ArrayList;

class Connection {
    private ArrayList<Transmission> transmissions;
    private int cost;

    Connection(int cost) {
        transmissions = new ArrayList<>();
        this.cost = cost;
    }

    int getAvailableTime() {
        if (transmissions.isEmpty())
            return 0;
        else
            return transmissions.get(transmissions.size() - 1).finishTime;
    }

    void assignTransmission(Transmission t) {
        transmissions.add(t);
    }

    int getCost() {
        return cost;
    }

    public ArrayList<Transmission> getTransmissions() {
        return transmissions;
    }
}
