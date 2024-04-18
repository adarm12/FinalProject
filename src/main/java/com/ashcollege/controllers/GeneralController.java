package com.ashcollege.controllers;

import com.ashcollege.Persist;
import com.ashcollege.entities.*;
import com.ashcollege.responses.BasicResponse;
import com.ashcollege.responses.LoginResponse;
import com.ashcollege.utils.DbUtils;
import com.mysql.cj.x.protobuf.MysqlxDatatypes;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import javax.annotation.PostConstruct;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;


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
        new Thread( () -> {
            persist.startLeague();
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
    public void placeBet(String user, int betSum, int matchupId, int result) {
        persist.addBet(user,betSum,matchupId,result);

    }



}
