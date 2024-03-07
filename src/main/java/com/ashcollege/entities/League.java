package com.ashcollege.entities;

public class League {
    private int leagueId;
    private String leagueName;

    public League(int leagueId, String leagueName) {
        this.leagueId = leagueId;
        this.leagueName = leagueName;
    }

    public League() {

    }

    public int getLeagueId() {
        return leagueId;
    }

    public void setLeagueId(int leagueId) {
        this.leagueId = leagueId;
    }

    public String getLeagueName() {
        return leagueName;
    }

    public void setLeagueName(String leagueName) {
        this.leagueName = leagueName;
    }
}
