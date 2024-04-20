package com.ashcollege.utils;

public class Errors {
    public static final int NO_ERRORS = -1;
    public static final int ERROR_MISSING_FIELDS = 0; // כל השדות ריקים
    public static final int ERROR_USERNAME_TAKEN = 1;
    public static final int ERROR_NO_SUCH_USERNAME = 2;
    public static final int ERROR_NO_USERNAME = 3;
    public static final int ERROR_NO_PASSWORD = 4;
    public static final int ERROR_INCORRECT_PASSWORD = 11;
    public static final int ERROR_SHORT_PASSWORD = 10;
    public static final int ERROR_PASSWORD_DOES_NOT_CONTAIN_SYMBOLS = 14;
    public static final int ERROR_PASSWORD_NOT_MATCH = 12; // אימות סיסמא לא זהה
    public static final int ERROR_SIGN_UP_PASSWORDS_DONT_MATCH = 5;
    public static final int ERROR_EMAIL_IS_NOT_VALID = 6;
    public static final int ERROR_NO_EMAIL = 7;
    public static final int ERROR_NO_SUCH_EMAIL = 13;
    public static final int ERROR_LOW_BALANCE = 15;
    public static final int ERROR_LOW_BET = 16;
    public static final int ERROR_HIGH_BET = 17;
    public static final int NO_RESULT = 18;
    public static final int NO_MATCHUP = 19;
    public static final int ROUND_OVER = 20;
    public static final int ERROR_LOGIN_WRONG_CREDS = 8;
    public static final int ERROR_SECRET_WAS_NOT_SENT = 9;
}
