package net.alureon.assignment1;

public class Joke {

    private String clue;
    private String punchline;


    public Joke(String clue, String punchline) {
        this.clue = clue;
        this.punchline = punchline;
    }

    public String getClue() {
        return this.clue;
    }

    public String getPunchline() {
        return this.punchline;
    }
}
