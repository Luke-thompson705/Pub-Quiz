package datebase;

public class Team  {

    private final String id;
    private final String name;
    private int score;

    public Team(String id, String name, int score) {
        this.id = id;
        this.name = name;
        this.score = score;
    }

    public int getScore() {
        return score;
    }

    public void setScore(int score) {
        this.score = score;
    }

    public String getName() {
        return name;
    }

    public String getId() {
        return id;
    }
}
