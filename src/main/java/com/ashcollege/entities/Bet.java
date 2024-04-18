package com.ashcollege.entities;

import com.ashcollege.Persist;

public class Bet {
    private int id;
    private User user;
    private int amount;
    private Matchup matchup;
    private int result;

    public Bet(int id, User user, int amount, Matchup matchup, int result) {
        this.id = id;
        this.user = user;
        this.amount = amount;
        this.matchup = matchup;
        this.result = result;
    }

    public Bet(User user, int amount, Matchup matchup, int result) {
        this.user = user;
        this.amount = amount;
        this.matchup = matchup;
        this.result = result;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public int getAmount() {
        return amount;
    }

    public void setAmount(int amount) {
        this.amount = amount;
    }

    public Matchup getMatchup() {
        return matchup;
    }

    public void setMatchup(Matchup matchup) {
        this.matchup = matchup;
    }

    public int getResult() {
        return result;
    }

    public void setResult(int result) {
        this.result = result;
    }

    public void checkBet () {
        //1 -> Home Bet
        //2 -> Away Bet
        //0 -> Draw
        double balanceToAdd = -amount;
        if (this.result == 0) {
            if (this.matchup.getTeam1Goals() == this.matchup.getTeam2Goals()) {
                balanceToAdd += amount*matchup.getDrawRatio();
            }
        } else if (this.result == 1) {
            if (this.matchup.getTeam1Goals() > this.matchup.getTeam2Goals()) {
                balanceToAdd += amount*matchup.getTeam1WinRatio();
            }
        } else if (this.result == 2) {
            if (this.matchup.getTeam1Goals() < this.matchup.getTeam2Goals()) {
                balanceToAdd += amount*matchup.getTeam2WinRatio();
            }
        }
        this.user.setBalance(this.user.getBalance() + balanceToAdd);
    }


}
