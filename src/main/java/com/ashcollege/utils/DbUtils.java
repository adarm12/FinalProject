package com.ashcollege.utils;


import com.ashcollege.entities.Product;
import com.ashcollege.entities.User;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

@Component
public class DbUtils {

    private Connection connection;

    @PostConstruct
    public void init() {
        createDbConnection(Constants.DB_USERNAME, Constants.DB_PASSWORD);
    }

    private void createDbConnection(String username, String password) {
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
            connection = DriverManager.getConnection("jdbc:mysql://localhost:3306/football", username, password);
            System.out.println("Connection successful!");
            System.out.println();
        } catch (Exception e) {
            System.out.println("Cannot create DB connection!");
        }
    }

//    public boolean checkIfEmailIsValid(String email) {
//        boolean valid = false;
//        try {
//            PreparedStatement preparedStatement = connection.prepareStatement("SELECT email FROM users WHERE email = ?");
//            preparedStatement.setString(1, email);
//            ResultSet resultSet = preparedStatement.executeQuery();
//            while (email.contains("@")) {
//                valid = true;
//            }
//        } catch (SQLException e) {
//            e.printStackTrace();
//        }
//        return valid;
//    }
//
//
//    public boolean checkIfUsernameAvailable(String username) {
//        boolean available = false;
//        try {
//            PreparedStatement preparedStatement = connection.prepareStatement("SELECT username FROM users WHERE username = ?");
//            preparedStatement.setString(1, username);
//            ResultSet resultSet = preparedStatement.executeQuery();
//            while (!resultSet.next()) {
//                available = true;
//            }
//        } catch (SQLException e) {
//            e.printStackTrace();
//        }
//        return available;
//    }
//
//    public boolean addUser(User user) {
//        boolean success = false;
//        try {
//            if (checkIfUsernameAvailable(user.getUsername())) {
//                PreparedStatement preparedStatement = connection.prepareStatement("INSERT INTO users (username, password) VALUES ( ? , ? )");
//                preparedStatement.setString(1, user.getUsername());
//                preparedStatement.setString(2, user.getPassword());
//                preparedStatement.executeUpdate();
//                success = true;
//            }
//        } catch (SQLException e) {
//            e.printStackTrace();
//        }
//        return success;
//    }
//
//    public List<User> getAllUsers() {
//        List<User> allUsers = new ArrayList<>();
//        try {
//            PreparedStatement preparedStatement = connection.prepareStatement("SELECT * FROM users");
//            ResultSet resultSet = preparedStatement.executeQuery();
//            while (resultSet.next()) {
//                int id = resultSet.getInt("id");
//                String username = resultSet.getString("username");
//                String email = resultSet.getString("email");
//                String password = resultSet.getString("password");
//                User user = new User(id, username, email, password);
//                allUsers.add(user);
//            }
//
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
//        return allUsers;
//    }
//
//    public void addProduct(Product product) {
//        try {
//            PreparedStatement preparedStatement = connection.prepareStatement("INSERT INTO product (description, price, count) VALUES ( ? , ? , ?)");
//            preparedStatement.setString(1, product.getDescription());
//            preparedStatement.setFloat(2, product.getPrice());
//            preparedStatement.setInt(3, product.getCount());
//            preparedStatement.executeUpdate();
//
//        } catch (SQLException e) {
//            e.printStackTrace();
//        }
//    }

    /*public boolean checkCredentials (String username, String password) {
        boolean ok = false;
        if (checkIfUsernameAvailable(username)) {
            PreparedStatement preparedStatement = connection.prepareStatement("SELECT * FROM users WHERE password = ? and username = ?");
            preparedStatement.setString(1,username);
            preparedStatement.setString(2,password);
        }
    }*/

//    public User login(String username, String password) {
//        User user = null;
//        try {
//            PreparedStatement preparedStatement = connection.prepareStatement(
//                    "SELECT id, secret FROM users WHERE username = ? AND password = ? ");
//            preparedStatement.setString(1, username);
//            preparedStatement.setString(2, password);
//            ResultSet resultSet = preparedStatement.executeQuery();
//            if (resultSet.next()) {
//                int id = resultSet.getInt("id");
//                String secret = resultSet.getString("secret");
//                user = new User();
//                user.setId(id);
//                user.setSecret(secret);
//            }
//        } catch (SQLException e) {
//            e.printStackTrace();
//        }
//        return user;
//
//    }
//
//    public List<Product> getProductsByUserSecret(String secret) {
//        List<Product> products = new ArrayList<>();
//        try {
//            PreparedStatement preparedStatement = connection.prepareStatement(
//                    "SELECT p.description, p.price " +
//                            "FROM users u INNER JOIN users_products_map upm ON u.id = upm.user_id " +
//                            "INNER JOIN products p ON upm.product_id = p.id " +
//                            "WHERE u.secret = ?"
//            );
//            preparedStatement.setString(1, secret);
//            ResultSet resultSet = preparedStatement.executeQuery();
//            while (resultSet.next()) {
//                String description = resultSet.getString(1);
//                float price = resultSet.getFloat(2);
//                Product product = new Product(description, price, 0);
//                products.add(product);
//            }
//        } catch (SQLException e) {
//            e.printStackTrace();
//        }
//        return products;
//    }

//    public User getUserBySecret(String secret) {
//        User user = null;
//        try {
//            PreparedStatement preparedStatement = connection.prepareStatement("SELECT * FROM users WHERE secret = ?");
//            preparedStatement.setString(1, secret);
//            ResultSet resultSet = preparedStatement.executeQuery();
//            if (resultSet.next()) {
//                int id = resultSet.getInt(1);
//                user = new User();
//                user.setId(id);
//            }
//        } catch (SQLException e) {
//            e.printStackTrace();
//        }
//        return user;
//
//    }


}
