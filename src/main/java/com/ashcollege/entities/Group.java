package com.ashcollege.entities;
public class Group {
    private int groupId;
    private String groupName;
    private int score;
    private int goalsNumber;

    public Group(int groupId, String groupName, int score, int goalsNumber) {
        this.groupId = groupId;
        this.groupName = groupName;
        this.score = score;
        this.goalsNumber = goalsNumber;
    }

    public Group() {

    }

    public int getGroupId() {
        return groupId;
    }

    public void setGroupId(int groupId) {
        this.groupId = groupId;
    }

    public String getGroupName() {
        return groupName;
    }

    public void setGroupName(String groupName) {
        this.groupName = groupName;
    }

    public int getScore() {
        return score;
    }

    public void setScore(int score) {
        this.score = score;
    }

    public int getGoalsNumber() {
        return goalsNumber;
    }

    public void setGoalsNumber(int goalsNumber) {
        this.goalsNumber = goalsNumber;
    }
}
