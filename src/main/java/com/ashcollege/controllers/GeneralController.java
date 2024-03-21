package com.ashcollege.controllers;

import com.ashcollege.Persist;
import com.ashcollege.entities.User;
import com.ashcollege.responses.BasicResponse;
import com.ashcollege.responses.LoginResponse;
import com.ashcollege.utils.DbUtils;
import com.mysql.cj.x.protobuf.MysqlxDatatypes;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.PostConstruct;


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
    public BasicResponse signUp(String username, String email, String password, String repeatPassword) {
        return persist.signup(username, email, password, repeatPassword);
    }

    @RequestMapping(value = "login", method = {RequestMethod.GET, RequestMethod.POST})
    public LoginResponse login(String email, String password) {
        return persist.login(email, password);
    }

    @RequestMapping(value = "edit-user", method = {RequestMethod.GET, RequestMethod.POST})
    public BasicResponse editUser(String email, String newUsername, String password, String newPassword, String repeatNewPassword) {
        return persist.editUser(email, newUsername, password, newPassword, repeatNewPassword);
    }

    @RequestMapping(value = "start-league", method = {RequestMethod.GET, RequestMethod.POST})
    public void startLeague() {
        persist.startLeague();
    }


    // run only one time to initiate the league
//    @RequestMapping(value = "insert-teams", method = {RequestMethod.GET, RequestMethod.POST})
//    public void insertTeams() {
//        persist.insertTeamsToTable();
//    }

//    @RequestMapping (value = "load-users", method = {RequestMethod.GET, RequestMethod.POST})
//    public List<User> users() {
//        return persist.loadUsers();
//    }


}
