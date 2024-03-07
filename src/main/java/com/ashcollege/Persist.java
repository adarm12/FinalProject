package com.ashcollege;
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

    public boolean usernameExists(String username) {
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

    public User userExists(String email, String password) {
        User user;
        user = (User) this.sessionFactory.getCurrentSession().createQuery(
                        "From User WHERE email = :email AND password =: password ")
                .setParameter("email", email)
                .setParameter("password", password)
                .setMaxResults(1)
                .uniqueResult();
        return user;
    }

    public User getClientByCreds(String email, String password) {
        User user;
        user = (User) this.sessionFactory.getCurrentSession().createQuery(
                        "From User WHERE email = :email AND password = :password")
                .setParameter("email", email)
                .setParameter("password", password)
                .setMaxResults(1)
                .uniqueResult();
        return user;
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

    public LoginResponse login(String username, String email, String password) {
        LoginResponse loginResponse = new LoginResponse(false, null);
        if (usernameExists(username) && username != null)  {
            if (username != null && username.length() > 0) {
                if (email != null && password.length() > 0) {
                    if (emailExists(email)) {
                        if (password != null && password.length() > 0) {
                            if (userExists(email, password) != null) {
                                User user;
                                user = userExists(email, password);
                                loginResponse.setSuccess(true);
                                loginResponse.setErrorCode(0);
                                loginResponse.setId(user.getId());
                                loginResponse.setSecret(user.getSecret());
                            } else {
                                loginResponse.setErrorCode(ERROR_INCORRECT_PASSWORD);
                            }
                        } else {
                            loginResponse.setErrorCode(ERROR_NO_PASSWORD);
                        }
                    } else {
                        loginResponse.setErrorCode(ERROR_NO_SUCH_EMAIL);
                    }
                } else {
                    loginResponse.setErrorCode(ERROR_NO_EMAIL);
                }
            } else {
                loginResponse.setErrorCode(ERROR_NO_USERNAME);
            }
        } else {
            loginResponse.setErrorCode(ERROR_NO_SUCH_USERNAME);
        }
        return loginResponse;
    }

    public BasicResponse editUser(String email, String newUsername, String password, String newPassword) {
        BasicResponse basicResponse = new BasicResponse(false, null);
        if (!usernameExists(newUsername)) {
            if (newUsername != null && newUsername.length() > 0) {
                if (newPassword != null && newPassword.length() > 0) {
                    User user = getClientByCreds(email, password);
                    user.setUsername(newUsername);
                    user.setPassword(newPassword);
                    save(user);
                    basicResponse.setSuccess(true);
                    basicResponse.setErrorCode(0);
                } else {
                    basicResponse.setErrorCode(ERROR_NO_PASSWORD);
                }
            } else {
                basicResponse.setErrorCode(ERROR_NO_USERNAME);
            }
        } else {
            basicResponse.setErrorCode(ERROR_USERNAME_TAKEN);
        }
        return basicResponse;
    }
}