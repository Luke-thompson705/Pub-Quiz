package menuapp;

public class Question {

    private int id;
    private String question;
    private String answer;
    private int round;
    private int time;

    public Question(int id, String question, String answer, int round, int time) {
        this.id = id;
        this.question = question;
        this.answer = answer;
        this.round = round;
        this.time = time;
    }


    public int getId() {
        return id;
    }

    public String getQuestion() {
        return question;
    }

    public String getAnswer() {
        return answer;
    }

    public int getRound() {
        return round;
    }

    public int getTime() {
        return time;
    }
}
