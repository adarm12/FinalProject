package com.ashcollege.controllers;

import com.ashcollege.Persist;
import com.ashcollege.entities.*;
import com.ashcollege.responses.BasicResponse;
import com.ashcollege.responses.LoginResponse;
import com.ashcollege.utils.DbUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import javax.annotation.PostConstruct;
import java.util.ArrayList;
import java.util.List;

import static com.ashcollege.utils.Constants.GAME_LENGTH;
import static com.ashcollege.utils.Constants.TRY_GOAL;
import static com.ashcollege.utils.Errors.*;


@RestController
public class GeneralController {

    @Autowired
    private DbUtils dbUtils;

    @Autowired
    private Persist persist;


    @RequestMapping(value = "/", method = {RequestMethod.GET, RequestMethod.POST})
    public Object test() {
        return "Hello From Server";
    }

    @RequestMapping(value = "sign-up", method = {RequestMethod.GET, RequestMethod.POST})
    public BasicResponse signUp(String username, String email, String password, String repeatPassword, double balance) {
        return persist.signup(username, email, password, repeatPassword, balance);
    }

    @RequestMapping(value = "login", method = {RequestMethod.GET, RequestMethod.POST})
    public LoginResponse login(String email, String password) {
        return persist.login(email, password);
    }

    @RequestMapping(value = "edit-user", method = {RequestMethod.GET, RequestMethod.POST})
    public BasicResponse editUser(String email, String newUsername, String password, String newPassword, String repeatNewPassword) {
        return persist.editUser(email, newUsername, password, newPassword, repeatNewPassword);
    }

//    @RequestMapping(value = "start-league", method = {RequestMethod.GET, RequestMethod.POST})
//    public void startLeague() {
//        new Thread( () -> {
//            persist.startLeague();
//        }).start();
//
//    }

    @RequestMapping(value = "get-teams", method = {RequestMethod.GET, RequestMethod.POST})
    public List<Team> teams() {
        return persist.getTeams();
    }

    // run only one time to initiate the league
    // @RequestMapping(value = "insert-teams", method = {RequestMethod.GET, RequestMethod.POST})
    @PostConstruct
    public void insertTeams() {
        persist.insertTeamsToTable();
        new Thread(() -> {
            startLeague();
        }).start();
    }

    @RequestMapping(value = "/streaming", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    public SseEmitter createStreamingSession() {
        return persist.createStreamingSession();
    }

//    @RequestMapping (value = "load-users", method = {RequestMethod.GET, RequestMethod.POST})
//    public List<User> users() {
//        return persist.loadUsers();
//    }

    @RequestMapping(value = "place-bet", method = {RequestMethod.GET, RequestMethod.POST})
    public BasicResponse placeBet(String user, int betSum, int matchupId, int result) {
        return addBet(user, betSum, matchupId, result);
    }


    private List<List<Matchup>> matchupsList = new ArrayList<>();
    private List<Matchup> roundMatchups = new ArrayList<>();
    private List<Bet> bets = new ArrayList<>();
    private boolean betFlag;
    public List<List<Matchup>> startLeague() {
        int counterMatchupId = 1;
        List<Team> teams = persist.getTeams();
        int numTeams = teams.size();

        int totalRounds = numTeams - 1;


        for (int round = 1; round <= totalRounds; round++) {
            betFlag = true;  //bets are able now

            roundMatchups = new ArrayList<>();
            System.out.println("Round " + round + " matchups:");


            counterMatchupId = createRoundMatchups(teams,round,counterMatchupId);

            for (int i=0;i<5;i++) {
                streamAndWaitBeforeGameStarts();
            }


            List<Thread> games = startRound(roundMatchups);
            betFlag = false;  //bets are unable now
            //Function that will wait for all threads to be done:
            for (Thread gameThread : games) {
                try {
                    gameThread.join();
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }
            }

            persist.checkBets(bets);


            for (Matchup matchup : roundMatchups) {
                persist.updateSkillsAndInjuries(matchup);
            }


            System.out.println("-----------------");
            System.out.println("round over");
            System.out.println("-----------------");


            matchupsList.add(roundMatchups);
            try {
                persist.stream(matchupsList, roundMatchups);
                System.out.println("streamed success");
            } catch (Exception e) {
                System.out.println("wasnt able to stream");
                throw new RuntimeException(e);
            }
            rotateTeams(teams);
        }
        return matchupsList;
    }

    private int createRoundMatchups(List<Team> teams,int round, int counterMatchupId) {
        for (int i = 0; i < teams.size() / 2; i++) {
            int team1Index = i;
            int team2Index = teams.size() - 1 - i;

            Team team1 = teams.get(team1Index);
            Team team2 = teams.get(team2Index);

            System.out.println(team1.getTeamName() + " vs " + team2.getTeamName());
            Matchup matchup = new Matchup(counterMatchupId, round, team1, team2);
            counterMatchupId++;
            roundMatchups.add(matchup);


        }
        return counterMatchupId;
    }

    private void rotateTeams(List<Team> teams) {
        // Move the last team to the second position
        Team lastTeam = teams.remove(teams.size() - 1);
        teams.add(1, lastTeam);
    }

    public List<Thread> startRound(List<Matchup> games) {
        List<Thread> threads = new ArrayList<>();
        for (Matchup game : games) {

            Thread thread = new Thread(() -> {


                    int team1Chances = game.calculateTeam1Odds();

                    int pointsTeam1 = game.getTeam1().getPoints();
                    int pointsTeam2 = game.getTeam2().getPoints();

                    int differenceTeam1 = game.getTeam1().getGoalsDifference();
                    int differenceTeam2 = game.getTeam2().getGoalsDifference();

                    persist.updatePoints(game, pointsTeam1, pointsTeam2, differenceTeam1, differenceTeam2);
                    for (int i = 0; i < GAME_LENGTH / TRY_GOAL; i++) {
                        try {
                            Thread.sleep(TRY_GOAL);
                        } catch (InterruptedException e) {
                            throw new RuntimeException(e);
                        }
                        int goalHappens = (int) (Math.random() * 100 + 1);
                        int goal = (int) (Math.random() * 100) + 1;
                        if (goalHappens >= 50) {
                            if (goal <= team1Chances) {
                                game.addGoalTeam1();
                            } else {
                                game.addGoalTeam2();
                            }

                            //points are updated mid-game
                            persist.updatePoints(game, pointsTeam1, pointsTeam2, differenceTeam1, differenceTeam2);

                            game.printMatchup();
                            try {
                                persist.stream(matchupsList, roundMatchups);
                                System.out.println("streamed success");
                            } catch (Exception e) {
                                System.out.println("wasnt able to stream");
                                throw new RuntimeException(e);
                            }

                        }
                    }

            });
            thread.start();
            threads.add(thread);

        }
        return threads;
    }

    private void streamAndWaitBeforeGameStarts() {
        try {
            persist.stream(matchupsList, roundMatchups);
            System.out.println("streamed success");
        } catch (Exception e) {
            System.out.println("wasnt able to stream");
            throw new RuntimeException(e);
        }

        try {
            Thread.sleep(3000);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }

    public BasicResponse addBet(String user, int betSum, int matchupId, int result) {
        BasicResponse basicResponse = new BasicResponse(false, ERROR_MISSING_FIELDS);
        User user1 = persist.getUserBySecret(user);
        Matchup currentMatchup = null;
        for (
                Matchup matchup : roundMatchups) {
            if (matchup.getId() == matchupId) {
                currentMatchup = matchup;
            }
        }
        if (betSum > 0) {
            if (betSum < user1.getBalance()) {
                if (result == 0 || result == 1 || result == 2) {
                    if (currentMatchup != null) {
                        if (betFlag) {
                            basicResponse.setSuccess(true);
                            basicResponse.setErrorCode(NO_ERRORS);
                            Bet bet = new Bet(user1, betSum, currentMatchup, result);
                            bets.add(bet);
                        } else basicResponse.setErrorCode(ROUND_START);
                    } else basicResponse.setErrorCode(NO_MATCHUP);
                } else basicResponse.setErrorCode(NO_RESULT);
            } else basicResponse.setErrorCode(ERROR_HIGH_BET);
        } else basicResponse.setErrorCode(ERROR_LOW_BET);
//        if ((result == 1 || result == 0 || result == 2) && currentMatchup != null && betFlag) {
//            Bet bet = new Bet(user1, betSum, currentMatchup, result);
//            bets.add(bet);
//        }
        return basicResponse;
    }


}
