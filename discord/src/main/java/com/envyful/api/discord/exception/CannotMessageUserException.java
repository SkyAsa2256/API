package com.envyful.api.discord.exception;

/**
 *
 * Exception thrown when a user cannot be messaged
 *
 */
public class CannotMessageUserException extends Exception {

    private String user;
    private String reason;

    public CannotMessageUserException(String user, String reason) {
        super("Cannot message user " + user + ": " + reason);
        this.user = user;
        this.reason = reason;
    }

    public String getUser() {
        return user;
    }

    public String getReason() {
        return reason;
    }
}
