package com.ashcollege;

import com.ashcollege.entities.EventMatchup;
import com.ashcollege.entities.Matchup;
import com.ashcollege.entities.Team;
import com.ashcollege.entities.User;
import com.ashcollege.responses.BasicResponse;
import com.ashcollege.responses.LoginResponse;
import com.github.javafaker.Faker;
import org.hibernate.HibernateException;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import javax.annotation.PostConstruct;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static com.ashcollege.utils.Constants.*;
import static com.ashcollege.utils.Errors.*;
import static com.ashcollege.utils.Errors.ERROR_PASSWORD_NOT_MATCH;

@Transactional
@Component
@SuppressWarnings("unchecked")
public class Persist {

    private static final Logger LOGGER = LoggerFactory.getLogger(Persist.class);

    private final SessionFactory sessionFactory;

    @Autowired
    public Persist(SessionFactory sf) {
        this.sessionFactory = sf;
    }

    public Session getQuerySession() {
        return sessionFactory.getCurrentSession();
    }

    public void save(Object object) {
        this.sessionFactory.getCurrentSession().saveOrUpdate(object);
    }

    public <T> T loadObject(Class<T> clazz, int oid) {
        return this.getQuerySession().get(clazz, oid);
    }

    public BasicResponse signup(String username, String email, String password, String repeatPassword, double balance) {
        BasicResponse basicResponse = new BasicResponse(false, ERROR_MISSING_FIELDS);
        if (!username.isEmpty()) {
            if (usernameAvailable(username)) {
                if (!email.isEmpty()) {
                    if (emailIsValid(email)) {
                        if (!password.isEmpty()) {
                            if (password.equals(repeatPassword)) {
                                if (validPassword(password).isSuccess()) {
                                    if (balance > 50) {
                                        User user = new User(username, email, password, balance);
                                        Faker faker = new Faker();
                                        user.setSecret(faker.random().hex(5));
                                        save(user);
                                        basicResponse.setSuccess(true);
                                        basicResponse.setErrorCode(NO_ERRORS);
                                    } else basicResponse.setErrorCode(ERROR_LOW_BALANCE);
                                } else basicResponse = validPassword(password);
                            } else basicResponse.setErrorCode(ERROR_PASSWORD_NOT_MATCH);
                        } else basicResponse.setErrorCode(ERROR_NO_PASSWORD);
                    } else basicResponse.setErrorCode(ERROR_EMAIL_IS_NOT_VALID);
                } else basicResponse.setErrorCode(ERROR_NO_EMAIL);
            } else basicResponse.setErrorCode(ERROR_USERNAME_TAKEN);
        } else basicResponse.setErrorCode(ERROR_NO_USERNAME);
        return basicResponse;
    }

    public LoginResponse login(String email, String password) {
        LoginResponse loginResponse = new LoginResponse(false, ERROR_MISSING_FIELDS);
        if (!email.isEmpty()) {
            if (emailExists(email)) {
                if (!password.isEmpty()) {
                    if (userExists(email, password) != null) {
                        User user = userExists(email, password);
                        loginResponse.setSuccess(true);
                        loginResponse.setErrorCode(NO_ERRORS);
                        loginResponse.setId(user.getId());
                        loginResponse.setSecret(user.getSecret());
                        loginResponse.setUser(user);
                    } else loginResponse.setErrorCode(ERROR_INCORRECT_PASSWORD);
                } else loginResponse.setErrorCode(ERROR_NO_PASSWORD);
            } else loginResponse.setErrorCode(ERROR_NO_SUCH_EMAIL);
        } else loginResponse.setErrorCode(ERROR_NO_EMAIL);
        return loginResponse;
    }

    public BasicResponse editUser(String email, String newUsername, String password, String newPassword, String repeatNewPassword) {
        BasicResponse basicResponse = new BasicResponse(false, ERROR_MISSING_FIELDS);
        if (!usernameExists(newUsername)) {
            if (!newUsername.isEmpty()) {
                if (!newPassword.isEmpty()) {
                    if (newPassword.equals(repeatNewPassword)) {
                        if (validPassword(newPassword).isSuccess()) {
                            User user = getUserByInfo(email, password);
                            user.setUsername(newUsername);
                            user.setPassword(newPassword);
                            save(user);
                            basicResponse.setSuccess(true);
                            basicResponse.setErrorCode(NO_ERRORS);
                        } else basicResponse = validPassword(newPassword);
                    } else basicResponse.setErrorCode(ERROR_PASSWORD_NOT_MATCH);
                } else basicResponse.setErrorCode(ERROR_NO_PASSWORD);
            } else basicResponse.setErrorCode(ERROR_NO_USERNAME);
        } else basicResponse.setErrorCode(ERROR_USERNAME_TAKEN);
        return basicResponse;
    }

    public BasicResponse validPassword(String password) {
        BasicResponse basicResponse = new BasicResponse(false, null);
        if (password.length() >= 8) {
            if (password.contains("@") || password.contains("!")) {
                basicResponse.setSuccess(true);
            } else basicResponse.setErrorCode(ERROR_PASSWORD_DOES_NOT_CONTAIN_SYMBOLS);
        } else basicResponse.setErrorCode(ERROR_SHORT_PASSWORD);

        return basicResponse;
    }

    public List<User> loadUsers() {
        List<User> allUsers = new ArrayList<>();
        User userToAdd;
        userToAdd = (User) this.sessionFactory.getCurrentSession().createQuery(
                        "From User WHERE secret IS NULL")
                .setMaxResults(1)
                .uniqueResult();
        allUsers.add(userToAdd);
        return allUsers;
    }

    private boolean usernameAvailable(String username) {
        User user;
        user = (User) this.sessionFactory.getCurrentSession().createQuery(
                        "From User WHERE username = :username ")
                .setParameter("username", username)
                .setMaxResults(1)
                .uniqueResult();
        return (user == null);
    }

    private User userBySecretAndId(String id, String secret) {
        User user;
        user = (User) this.sessionFactory.getCurrentSession().createQuery(
                        "From User WHERE id = :id AND secret =:secret")
                .setParameter("id", id)
                .setParameter("secret", secret)
                .setMaxResults(1)
                .uniqueResult();
        return user;
    }

    private boolean emailIsValid(String email) { // האם המייל תקין
        String emailRegex = "^[a-zA-Z0-9_+&*-]+(?:\\.[a-zA-Z0-9_+&*-]+)*@(?:[a-zA-Z0-9-]+\\.)+[a-zA-Z]{2,7}$";
        Pattern pattern = Pattern.compile(emailRegex);
        Matcher matcher = pattern.matcher(email);
        return matcher.matches();
    }

    private boolean usernameExists(String username) {
        boolean exists = false;
        User user;
        user = (User) this.sessionFactory.getCurrentSession().createQuery(
                        "From User WHERE username = :username")
                .setParameter("username", username)
                .setMaxResults(1)
                .uniqueResult();
        if (user != null) {
            exists = true;
        }
        return exists;
    }

    public boolean emailExists(String email) {
        boolean exists = false;
        User user;
        user = (User) this.sessionFactory.getCurrentSession().createQuery(
                        "From User WHERE email = :email")
                .setParameter("email", email)
                .setMaxResults(1)
                .uniqueResult();
        if (user != null) {
            exists = true;
        }
        return exists;
    }

    private User userExists(String email, String password) {
        User user;
        user = (User) this.sessionFactory.getCurrentSession().createQuery(
                        "From User WHERE email = :email AND password =: password ")
                .setParameter("email", email)
                .setParameter("password", password)
                .setMaxResults(1)
                .uniqueResult();
        return user;
    }

    private User getUserByInfo(String email, String password) {
        User user;
        user = (User) this.sessionFactory.getCurrentSession().createQuery(
                        "From User WHERE email = :email AND password = :password")
                .setParameter("email", email)
                .setParameter("password", password)
                .setMaxResults(1)
                .uniqueResult();
        return user;
    }


    public void insertTeamsToTable() {
        List<Team> teams = getTeams();
        if (teams.isEmpty()) {
            List<String> teamsNames = List.of(new String[]{"maccabi tel aviv", "hapoel tel aviv", "hapoel holon", "maccabi haifa",
                    "hapoel eilat", "galil elyon", "maccabi ramat gan", "hapoel beersheva"});

            for (String name : teamsNames) {
                int offensiveRating = (int) (Math.random() * (MAXIMUM_OFFENSIVE_RATING - MINIMUM_OFFENSIVE_RATING + 1)) + MINIMUM_OFFENSIVE_RATING;
                int defensiveRating = (int) (Math.random() * (MAXIMUM_DEFENSIVE_RATING - MINIMUM_DEFENSIVE_RATING + 1)) + MINIMUM_DEFENSIVE_RATING;

                Team team = new Team(name, 0, 0, offensiveRating, defensiveRating, 0);
                if (!teamsExists(name)) {
                    save(team);
                }
            }
        }
    }

    public boolean teamsExists(String teamName) {
        boolean exists = false;
        Team team;
        team = (Team) this.sessionFactory.getCurrentSession().createQuery(
                        "From Team WHERE teamName = :teamName")
                .setParameter("teamName", teamName)
                .setMaxResults(1)
                .uniqueResult();
        if (team != null) {
            exists = true;
        }
        return exists;
    }

    public List<Team> getTeams() {
        List<Team> teams = (List<Team>) this.sessionFactory.getCurrentSession().createQuery(
                        "From Team")
                .list();
        return teams;
    }

    private static List<List<Matchup>> matchupsList = new ArrayList<>();
    private static List<Matchup> roundMatchups = new ArrayList<>();
    public List<List<Matchup>> startLeague() {


        List<Team> teams = getTeams();
        int numTeams = teams.size();

        int totalRounds = numTeams - 1;


        for (int round = 1; round <= totalRounds; round++) {

            roundMatchups = new ArrayList<>();
            System.out.println("Round " + round + " matchups:");


            for (int i = 0; i < numTeams / 2; i++) {
                int team1Index = i;
                int team2Index = numTeams - 1 - i;

                Team team1 = teams.get(team1Index);
                Team team2 = teams.get(team2Index);

                System.out.println(team1.getTeamName() + " vs " + team2.getTeamName());
                Matchup matchup = new Matchup(round, team1, team2);
                roundMatchups.add(matchup);



            }

            try {
                stream(matchupsList, roundMatchups);
                System.out.println("streamed success");
            } catch (Exception e) {
                System.out.println("wasnt able to stream");
                throw new RuntimeException(e);
            }

            try {
                Thread.sleep(5000);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }

            try {
                stream(matchupsList, roundMatchups);
                System.out.println("streamed success");
            } catch (Exception e) {
                System.out.println("wasnt able to stream");
                throw new RuntimeException(e);
            }

            List<Thread> games = startRound(roundMatchups);
            //Function that will wait for all threads to be done:
            for (Thread gameThread : games) {
                try {
                    gameThread.join();
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }
            }

            System.out.println("111111111111111111");

            for (Matchup matchup : roundMatchups) {
                updateSkillsAndInjuries(matchup);
            }


            System.out.println("-----------------");
            System.out.println("round over");
            System.out.println("-----------------");


            matchupsList.add(roundMatchups);
            try {
                stream(matchupsList, roundMatchups);
                System.out.println("streamed success");
            } catch (Exception e) {
                System.out.println("wasnt able to stream");
                throw new RuntimeException(e);
            }
            rotateTeams(teams);
        }
        return matchupsList;
    }

    //function that checks who is the current winning team in the game
    private Team winningTeam(Matchup matchup) {
        Team winner = null;
        if (matchup.getTeam1Goals() > matchup.getTeam2Goals()) {
            winner = matchup.getTeam1();
        } else if (matchup.getTeam1Goals() < matchup.getTeam2Goals()) {
            winner = matchup.getTeam2();
        }
        return winner;
    }

    //function that updates the points in the league table
    private void updatePoints(Matchup matchup, int pointsTeam1, int pointsTeam2, int differenceTeam1, int differenceTeam2, Session session) {
        Team winningTeam = winningTeam(matchup);
        if (winningTeam == null) {
            matchup.getTeam1().setPoints(pointsTeam1 + 1);
            matchup.getTeam2().setPoints(pointsTeam2 + 1);
        } else if (winningTeam == matchup.getTeam1()) {
            matchup.getTeam1().setPoints(pointsTeam1 + 3);
            matchup.getTeam2().setPoints(pointsTeam2);
        } else {
            matchup.getTeam1().setPoints(pointsTeam1);
            matchup.getTeam2().setPoints(pointsTeam2 + 3);
        }

        matchup.getTeam1().setGoalsDifference(differenceTeam1 + matchup.getTeam1Goals() - matchup.getTeam2Goals());
        matchup.getTeam2().setGoalsDifference(differenceTeam2 + matchup.getTeam2Goals() - matchup.getTeam1Goals());

        session.update(matchup.getTeam1());
        session.update(matchup.getTeam2());
    }

    private void updateSkillsAndInjuries(Matchup matchup) {
        Team winningTeam = winningTeam(matchup);
        if (winningTeam != null) {
            Team losingTeam = (matchup.getTeam1() == winningTeam ? matchup.getTeam2() : matchup.getTeam1());
            winningTeam.setOffensiveRating((int) (winningTeam.getOffensiveRating() + (100 - winningTeam.getOffensiveRating()) * WINNING_SKILL_BONUS));
            winningTeam.setDefensiveRating((int) (winningTeam.getDefensiveRating() + (100 - winningTeam.getDefensiveRating()) * WINNING_SKILL_BONUS));

            losingTeam.setOffensiveRating((int) (losingTeam.getOffensiveRating() * LOSING_SKILL_MINUS));
            losingTeam.setDefensiveRating((int) (losingTeam.getDefensiveRating() * LOSING_SKILL_MINUS));
            if (Math.abs(matchup.getTeam1Goals() - matchup.getTeam2Goals()) >= BLOWOUT) {
                winningTeam.setOffensiveRating((int) (winningTeam.getOffensiveRating() + (100 - winningTeam.getOffensiveRating()) * WINNING_SKILL_BONUS));
            }
        }
        int injuriesTeam1 = setInjuries();
        int injuriesTeam2 = setInjuries();
        matchup.getTeam1().setPlayerInjuries(injuriesTeam1);
        matchup.getTeam2().setPlayerInjuries(injuriesTeam2);

    }

    private int setInjuries() {
        int injuries = 0;
        for (int i = 0; i < PLAYERS_ON_TEAM; i++) {
            int injuryRandomNum = (int) (Math.random() * 100 + 1);
            if (injuryRandomNum < INJURY_RISK) {
                injuries++;
            }
        }
        return injuries;
    }

    public List<Thread> startRound(List<Matchup> games) {
        List<Thread> threads = new ArrayList<>();
        for (Matchup game : games) {

            Thread thread = new Thread(() -> {
                try (Session session = sessionFactory.openSession()) {
                    Transaction transaction = session.beginTransaction();

                    int team1Chances = game.calculateTeam1Odds();
                    System.out.println("22222222222222 " + team1Chances);

                    int pointsTeam1 = game.getTeam1().getPoints();
                    int pointsTeam2 = game.getTeam2().getPoints();

                    int differenceTeam1 = game.getTeam1().getGoalsDifference();
                    int differenceTeam2 = game.getTeam2().getGoalsDifference();

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
                            updatePoints(game, pointsTeam1, pointsTeam2, differenceTeam1, differenceTeam2, session);

                            game.printMatchup();
                            try {
                                stream(matchupsList, roundMatchups);
                                System.out.println("streamed success");
                            } catch (Exception e) {
                                System.out.println("wasnt able to stream");
                                throw new RuntimeException(e);
                            }
                            // commit the transaction after each goal
                            transaction.commit();
                            // begin a new transaction
                            transaction = session.beginTransaction();
                        }
                    }

                    transaction.commit();
                } catch (HibernateException e) {
                    throw new RuntimeException(e);
                }
            });
            thread.start();
            threads.add(thread);

        }
        return threads;
    }


    private void rotateTeams(List<Team> teams) {
        // Move the last team to the second position
        Team lastTeam = teams.remove(teams.size() - 1);
        teams.add(1, lastTeam);
    }

    private List<EventMatchup> matchups = new ArrayList<>();

    public void stream(List<List<Matchup>> matchupsList, List<Matchup> currentMatchups) {
        try {
            for (EventMatchup matchup : matchups) {
                JSONObject jsonObject = new JSONObject();
                jsonObject.put("list", matchupsList);
                jsonObject.put("current", currentMatchups);
                matchup.getSseEmitter().send(jsonObject.toString());
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

    }

    public SseEmitter createStreamingSession() {
        try {
            SseEmitter sseEmitter = new SseEmitter((long) (10 * 60 * 1000));
            matchups.add(new EventMatchup(sseEmitter));
            return sseEmitter;
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

//
//   public BasicResponse innerUser (String username, String email, String password) {
//        BasicResponse basicResponse = new BasicResponse(false, ERROR_USERNAME_TAKEN);
//        if (username != null) {
//            if (usernameAvailable(username)) {
//                if (email != null && email.length() > 0) {
//                    if (email.contains("@")) {
//                        if (password != null && password.length() > 0) {
//                            User user = new User(username, email, password);
//                            save(user);
//                            basicResponse.setSuccess(true);
//                            basicResponse.setErrorCode(0);
//                        } else {
//                            basicResponse.setErrorCode(ERROR_NO_PASSWORD);
//                        }
//                    } else {
//                        basicResponse.setErrorCode(ERROR_EMAIL_IS_NOT_VALID);
//                    }
//                } else {
//                    basicResponse.setErrorCode(ERROR_NO_EMAIL);
//                }
//            } else {
//                basicResponse.setErrorCode(ERROR_NO_USERNAME);
//            }
//        }
//        return basicResponse;
//    }

    //    public LoginResponse login(String username, String email, String password) {
//        LoginResponse loginResponse = new LoginResponse(false, null);
//        if (usernameExists(username) && username != null)  {
//            if (username != null && username.length() > 0) {
//                if (email != null && password.length() > 0) {
//                    if (emailExists(email)) {
//                        if (password != null && password.length() > 0) {
//                            if (userExists(email, password) != null) {
//                                User user;
//                                user = userExists(email, password);
//                                loginResponse.setSuccess(true);
//                                loginResponse.setErrorCode(0);
//                                loginResponse.setId(user.getId());
//                                loginResponse.setSecret(user.getSecret());
//                            } else {
//                                loginResponse.setErrorCode(ERROR_INCORRECT_PASSWORD);
//                            }
//                        } else {
//                            loginResponse.setErrorCode(ERROR_NO_PASSWORD);
//                        }
//                    } else {
//                        loginResponse.setErrorCode(ERROR_NO_SUCH_EMAIL);
//                    }
//                } else {
//                    loginResponse.setErrorCode(ERROR_NO_EMAIL);
//                }
//            } else {
//                loginResponse.setErrorCode(ERROR_NO_USERNAME);
//            }
//        } else {
//            loginResponse.setErrorCode(ERROR_NO_SUCH_USERNAME);
//        }
//        return loginResponse;
//    }


//    public BasicResponse editUser(String email, String newUsername, String password, String newPassword) {
//        BasicResponse basicResponse = new BasicResponse(false, null);
//        if (!usernameExists(newUsername)) {
//            if (newUsername != null && newUsername.length() > 0) {
//                if (newPassword != null && newPassword.length() > 0) {
//                    User user = getClientByCreds(email, password);
//                    user.setUsername(newUsername);
//                    user.setPassword(newPassword);
//                    save(user);
//                    basicResponse.setSuccess(true);
//                    basicResponse.setErrorCode(0);
//                } else {
//                    basicResponse.setErrorCode(ERROR_NO_PASSWORD);
//                }
//            } else {
//                basicResponse.setErrorCode(ERROR_NO_USERNAME);
//            }
//        } else {
//            basicResponse.setErrorCode(ERROR_USERNAME_TAKEN);
//        }
//        return basicResponse;
//    }
}