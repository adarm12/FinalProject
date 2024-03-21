package com.ashcollege.entities;

public class Matchup {

    private int round;
    private Team team1;
    private Team team2;
    private int team1Goals;
    private int team2Goals;
    private boolean rainingWeather; //raining weather is a little bit better for team1;

    public Matchup(int round, Team team1, Team team2) {
        this.round = round;
        this.team1 = team1;
        this.team2 = team2;
        this.team1Goals = 0;
        this.team2Goals = 0;
        this.rainingWeather = Math.random()>0.5;
    }

    public int calculateTeam1Odds() {

        double team1Skill = team1.getDefensiveRating()+team1.getOffensiveRating() - team1.getPlayerInjuries()*10;
        double team2Skill = team2.getDefensiveRating()+team2.getOffensiveRating()-team2.getPlayerInjuries()*10;
        if (rainingWeather) {
            team1Skill+=5;
            team2Skill-=5;
        }

        int team1Chances = (int) ((double)team1Skill/(double) (team1Skill+team2Skill)*100);

        return team1Chances;
    }

    public int getRound() {
        return round;
    }

    public void setRound(int round) {
        this.round = round;
    }

    public Team getTeam1() {
        return team1;
    }

    public void setTeam1(Team team1) {
        this.team1 = team1;
    }

    public Team getTeam2() {
        return team2;
    }

    public void setTeam2(Team team2) {
        this.team2 = team2;
    }

    public int getTeam1Goals() {
        return team1Goals;
    }



    public void setTeam1Goals(int team1Goals) {
        this.team1Goals = team1Goals;
    }

    public int getTeam2Goals() {
        return team2Goals;
    }

    public void setTeam2Goals(int team2Goals) {
        this.team2Goals = team2Goals;
    }

    public boolean isRainingWeather() {
        return rainingWeather;
    }

    public void setRainingWeather(boolean rainingWeather) {
        this.rainingWeather = rainingWeather;
    }

    public void addGoalTeam1() {
        this.team1Goals = team1Goals+1;
    }
    public void addGoalTeam2() {
        this.team2Goals = team2Goals+1;
    }
}
