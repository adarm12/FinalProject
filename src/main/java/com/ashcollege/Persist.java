package com.ashcollege;

import com.ashcollege.entities.Matchup;
import com.ashcollege.entities.Team;
import com.ashcollege.entities.User;
import com.ashcollege.responses.BasicResponse;
import com.ashcollege.responses.LoginResponse;
import com.github.javafaker.Faker;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

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

    public BasicResponse signup(String username, String email, String password, String repeatPassword) {
        BasicResponse basicResponse = new BasicResponse(false, ERROR_MISSING_FIELDS);
        if (!username.isEmpty()) {
            if (usernameAvailable(username)) {
                if (!email.isEmpty()) {
                    if (emailIsValid(email)) {
                        if (!password.isEmpty()) {
                            if (password.equals(repeatPassword)) {
                                if (validPassword(password).isSuccess()) {
                                    User user = new User(username, email, password);
                                    Faker faker = new Faker();
                                    user.setSecret(faker.random().hex(5));
                                    save(user);
                                    basicResponse.setSuccess(true);
                                    basicResponse.setErrorCode(NO_ERRORS);
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

    public BasicResponse editUser(String email, String newUsername, String password, String newPassword) {
        BasicResponse basicResponse = new BasicResponse(false, ERROR_MISSING_FIELDS);
        if (!usernameExists(newUsername)) {
            if (!newUsername.isEmpty()) {
                if (!newPassword.isEmpty()) {
                    User user = getUserByInfo(email, password);
                    user.setUsername(newUsername);
                    user.setPassword(newPassword);
                    save(user);
                    basicResponse.setSuccess(true);
                    basicResponse.setErrorCode(NO_ERRORS);
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

        List<String> teamsNames = List.of(new String[]{"maccabi tel aviv", "hapoel tel aviv", "hapoel holon", "maccabi haifa",
                "hapoel eilat", "galil elyon", "maccabi ramat gan", "hapoel beersheva"});

        for (String name : teamsNames) {
            int offensiveRating = (int) (Math.random() * (MAXIMUM_OFFENSIVE_RATING - MINIMUM_OFFENSIVE_RATING + 1)) + MINIMUM_OFFENSIVE_RATING;
            int defensiveRating = (int) (Math.random() * (MAXIMUM_DEFENSIVE_RATING - MINIMUM_DEFENSIVE_RATING + 1)) + MINIMUM_DEFENSIVE_RATING;

            Team team = new Team(name, 0, 0, offensiveRating, defensiveRating, 0);
            save(team);

        }

    }

    private List<Team> getTeams() {
        List<Team> teams = (List<Team>) this.sessionFactory.getCurrentSession().createQuery(
                        "From Team")
                .list();
        return teams;
    }

    public void startLeague() {
        List<Team> teams = getTeams();
        int numTeams = teams.size();

        int totalRounds = numTeams - 1;

        for (int round = 1; round <= totalRounds; round++) {

            List<Matchup> roundMatchups = new ArrayList<>();
            System.out.println("Round " + round + " matchups:");


            for (int i = 0; i < numTeams / 2; i++) {
                int team1Index = i;
                int team2Index = numTeams - 1 - i;

                Team team1 = teams.get(team1Index);
                Team team2 = teams.get(team2Index);

                System.out.println(team1.getTeamName() + " vs " + team2.getTeamName());
                Matchup matchup = new Matchup(round,team1,team2);
                roundMatchups.add(matchup);
            }

            startRound(roundMatchups);

            //Function that will wait for all threads to be done

            rotateTeams(teams);
        }
    }

    public void startRound(List<Matchup> games) {
        for (Matchup game:games) {

            new Thread(() -> {
                int team1Chances = game.calculateTeam1Odds();
                for (int i=0;i<GAME_LENGTH/TRY_GOAL;i++) {
                    try {
                        Thread.sleep(TRY_GOAL);
                    } catch (InterruptedException e) {
                        throw new RuntimeException(e);
                    }
                    int goalHappens = (int)(Math.random()*100+1);
                    int goal = (int) (Math.random() * 100) + 1;
                    if (goalHappens>=50) {
                        if (goal <= team1Chances) {
                            game.addGoalTeam1();
                        } else {
                            game.addGoalTeam2();
                        }
                    }
                }
            }).start();
        }
    }

    private void rotateTeams(List<Team> teams) {
        // Move the last team to the second position
        Team lastTeam = teams.remove(teams.size() - 1);
        teams.add(1, lastTeam);
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