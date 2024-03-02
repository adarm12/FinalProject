package com.ashcollege.controllers;

import com.ashcollege.Persist;
import com.ashcollege.entities.Client;
import com.ashcollege.entities.Product;
import com.ashcollege.entities.User;
import com.ashcollege.responses.BasicResponse;
import com.ashcollege.responses.LoginResponse;
import com.ashcollege.responses.ProductsResponse;
import com.ashcollege.utils.DbUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.PostConstruct;
import java.util.List;

import static com.ashcollege.utils.Errors.*;

@RestController
public class GeneralController {

    @Autowired
    private DbUtils dbUtils;

    @Autowired
    private Persist persist;


    @RequestMapping(value = "/", method = {RequestMethod.GET, RequestMethod.POST})
    public Object test () {
        return "Hello From Server";
    }


//    @RequestMapping (value = "/login", method = {RequestMethod.GET, RequestMethod.POST})
//    public BasicResponse login (String username, String password) {
//        BasicResponse basicResponse = null;
//        boolean success = false;
//        Integer errorCode = null;
//        if (username != null && username.length() > 0) {
//            if (password != null && password.length() > 0) {
//                User user = dbUtils.login(username, password);
//                if (user != null) {
//                    basicResponse = new LoginResponse(true, errorCode, user.getId(), user.getSecret());
//                } else {
//                    errorCode = ERROR_LOGIN_WRONG_CREDS;
//                }
//            } else {
//                errorCode = ERROR_SIGN_UP_NO_PASSWORD;
//            }
//        } else {
//            errorCode = ERROR_SIGN_UP_NO_USERNAME;
//        }
//        if (errorCode != null) {
//            basicResponse = new BasicResponse(success, errorCode);
//        }
//        return basicResponse;
//    }

    @RequestMapping (value = "add-user", method = {RequestMethod.GET, RequestMethod.POST})
    public BasicResponse addUser (String username, String email, String password) {
        return persist.insertUser(username, email, password);
    }
    @RequestMapping (value = "login", method = {RequestMethod.GET, RequestMethod.POST})
    public BasicResponse login(String email, String password) {
        return persist.login(email, password);
    }

//    @RequestMapping (value = "get-users")
//    public List<User> getUsers () {
//        return dbUtils.getAllUsers();
//    }


//    @RequestMapping (value = "add-product")
//    public boolean addProduct (String description, float price, int count) {
//        Product toAdd = new Product(description, price, count);
//        dbUtils.addProduct(toAdd);
//        return true;
//    }

//    @RequestMapping(value = "get-products")
//    public BasicResponse getProducts (String secret) {
//        boolean success = false;
//        Integer errorCode = null;
//        BasicResponse basicResponse = null;
//        if (secret != null) {
//            User user = dbUtils.getUserBySecret(secret);
//            if (user != null) {
//                List<Product> products = dbUtils.getProductsByUserSecret(secret);
//                basicResponse = new ProductsResponse(true, null, products);
//            } else {
//                errorCode = ERROR_NO_SUCH_USER;
//            }
//        } else {
//            errorCode = ERROR_SECRET_WAS_NOT_SENT;
//        }
//        if (errorCode != null) {
//            basicResponse = new BasicResponse(false, errorCode);
//        }
//        return basicResponse;
//    }

//    @RequestMapping(value = "test")
//    public Client test (String firstName, String newFirstName) {
//        Client client = persist.getClientByFirstName(firstName);
//        client.setFirstName(newFirstName);
//        client.setLastName(newFirstName);
//        persist.save(client);
//        return client;
//    }

}
