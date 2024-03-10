package com.ashcollege.entities;
public class Team {
    private int id;
    private String teamName;
    private int points;

    private int goalsDifference;

    //רמה בהתקפה ערך התחלתי בין 70-90
    private int offensiveRating;
    //רמה בהתקפה ערך התחלתי בין 70-90
    private int defensiveRating;
    private int playerInjuries;


    public Team(String teamName, int points, int goalsDifference, int offensiveRating, int defensiveRating, int playerInjuries) {
        this.teamName = teamName;
        this.points = points;
        this.goalsDifference = goalsDifference;
        this.offensiveRating = offensiveRating;
        this.defensiveRating = defensiveRating;
        this.playerInjuries = playerInjuries;
    }

    public Team() {

    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getTeamName() {
        return teamName;
    }

    public void setTeamName(String teamName) {
        this.teamName = teamName;
    }

    public int getPoints() {
        return points;
    }

    public void setPoints(int score) {
        this.points = score;
    }

    public int getGoalsDifference() {
        return goalsDifference;
    }

    public int getOffensiveRating() {
        return offensiveRating;
    }

    public void setOffensiveRating(int offensiveRating) {
        this.offensiveRating = offensiveRating;
    }

    public int getDefensiveRating() {
        return defensiveRating;
    }

    public void setDefensiveRating(int defensiveRating) {
        this.defensiveRating = defensiveRating;
    }

    public void setGoalsDifference(int goalsNumber) {
        this.goalsDifference = goalsNumber;
    }

    public int getPlayerInjuries() {
        return playerInjuries;
    }

    public void setPlayerInjuries(int playerInjuries) {
        this.playerInjuries = playerInjuries;
    }
}
