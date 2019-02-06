package datebase;

import java.util.Comparator;

public class Teambyscore implements Comparator<Team> {
    @Override
    public int compare(Team o1, Team o2) {
        return String.valueOf(o2.getScore()).compareTo(String.valueOf(o1.getScore()));
    }
}
