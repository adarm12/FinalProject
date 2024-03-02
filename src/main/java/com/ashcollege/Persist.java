package com.ashcollege;

import com.ashcollege.entities.Client;
import com.ashcollege.entities.User;
import com.ashcollege.responses.BasicResponse;
import com.ashcollege.responses.LoginResponse;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import java.util.IllegalFormatCodePointException;
import java.util.List;

import static com.ashcollege.utils.Errors.*;


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

    private boolean usernameAvailable(String username) {
        User user;
        user = (User) this.sessionFactory.getCurrentSession().createQuery(
                        "From User WHERE username = :username ")
                .setParameter("username", username)
                .setMaxResults(1)
                .uniqueResult();
        return (user == null);
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

    public boolean userExists(String email, String password) {
        boolean exists = false;
        User user;
        user = (User) this.sessionFactory.getCurrentSession().createQuery(
                        "From User WHERE email = :email AND password =: password ")
                .setParameter("email", email)
                .setParameter("password", password)
                .setMaxResults(1)
                .uniqueResult();
        if (user != null) {
            exists = true;
        }
        return exists;
    }

    public BasicResponse insertUser(String username, String email, String password) {
        BasicResponse basicResponse = new BasicResponse(false, ERROR_USERNAME_TAKEN);
        if (username != null && username.length() > 0) {
            if (usernameAvailable(username)) {
                if (email != null && email.length() > 0) {
                    if (email.contains("@")) {
                        if (password != null && password.length() > 0) {
                            User user = new User(username, email, password);
                            save(user);
                            basicResponse.setSuccess(true);
                            basicResponse.setErrorCode(0);
                        } else {
                            basicResponse.setErrorCode(ERROR_NO_PASSWORD);
                        }
                    } else {
                        basicResponse.setErrorCode(ERROR_EMAIL_IS_NOT_VALID);
                    }
                } else {
                    basicResponse.setErrorCode(ERROR_NO_EMAIL);
                }
            } else {
                basicResponse.setErrorCode(ERROR_NO_USERNAME);
            }
        }
        return basicResponse;
    }

    public BasicResponse login(String email, String password) {
        BasicResponse basicResponse = new BasicResponse(false, null);
        if (email != null && email.length() > 0) {
            if (emailExists(email)) {
                if (password != null && password.length() > 0) {
                    if (userExists(email, password)) {
                        basicResponse.setSuccess(true);
                        basicResponse.setErrorCode(0);
                    } else {
                        basicResponse.setErrorCode(ERROR_INCORRECT_PASSWORD);
                    }
                } else {
                    basicResponse.setErrorCode(ERROR_NO_PASSWORD);
                }
            } else {
                basicResponse.setErrorCode(ERROR_NO_SUCH_USERNAME);
            }
        } else {
            basicResponse.setErrorCode(ERROR_NO_EMAIL);
        }
        return basicResponse;
    }


//    public <T> List<T> loadList(Class<T> clazz) {
//        return  this.sessionFactory.getCurrentSession().createQuery("FROM Client").list();
//    }

//    public Client getClientByFirstName (String firstName) {
//        return (Client) this.sessionFactory.getCurrentSession().createQuery(
//                "FROM Client WHERE firstName = :firstName ")
//                .setParameter("firstName", firstName)
//                .setMaxResults(1)
//                .uniqueResult();
//    }


}