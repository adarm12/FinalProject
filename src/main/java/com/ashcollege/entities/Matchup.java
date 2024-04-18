package com.ashcollege.entities;

public class Matchup {
    private int id;
    private int round;
    private Team team1;
    private Team team2;
    private int team1Goals;
    private int team2Goals;
    private boolean rainingWeather; //raining weather is a little bit better for team1;

    private double team1WinRatio;
    private double team2WinRatio;
    private double drawRatio;

    public Matchup(int id,int round, Team team1, Team team2) {
        this.id = id;
        this.round = round;
        this.team1 = team1;
        this.team2 = team2;
        this.team1Goals = 0;
        this.team2Goals = 0;
        this.rainingWeather = Math.random()>0.5;
        calculateBettingRatios();
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

    public void calculateBettingRatios() {
        double team1Odds = 100.0 / (double) calculateTeam1Odds();
        double team2Odds = 100.0 / (double) (100 - calculateTeam1Odds());
        double drawOdds = (team1Odds+team2Odds) / 2.0;

        this.team1WinRatio = Double.parseDouble(String.format("%.1f", team1Odds));
        this.team2WinRatio = Double.parseDouble(String.format("%.1f", team2Odds));
        this.drawRatio = Double.parseDouble(String.format("%.1f", drawOdds));

        System.out.println("team1 ratio: "+ team1WinRatio);
        System.out.println("team2 ratio: "+ team2WinRatio);
        System.out.println("draw ratio: "+ drawRatio);

    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public void printMatchup() {
        System.out.println(team1.getTeamName()+" "+team1Goals+" : "+team2Goals+" "+team2.getTeamName());
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

    public double getTeam1WinRatio() {
        return team1WinRatio;
    }

    public void setTeam1WinRatio(double team1WinRatio) {
        this.team1WinRatio = team1WinRatio;
    }

    public double getTeam2WinRatio() {
        return team2WinRatio;
    }

    public void setTeam2WinRatio(double team2WinRatio) {
        this.team2WinRatio = team2WinRatio;
    }

    public double getDrawRatio() {
        return drawRatio;
    }

    public void setDrawRatio(double drawRatio) {
        this.drawRatio = drawRatio;
    }
}
