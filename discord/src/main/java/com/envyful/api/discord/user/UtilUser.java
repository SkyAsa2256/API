package com.envyful.api.discord.user;

import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.PrivateChannel;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.exceptions.ErrorResponseException;

import javax.annotation.Nullable;

public class UtilUser {

    /**
     *
     * Gets the private message channel for this user
     * Returns null if they've got their DMs turned off, or the bot blocked.
     *
     * @param user The user
     * @return The private message channel
     */
    @Nullable
    public static PrivateChannel getDMs(User user) {
        try {
            return user.openPrivateChannel().complete();
        } catch (ErrorResponseException e) {
            return null;
        }
    }

    /**
     *
     * Gets the private message channel for this user
     * Returns null if they've got their DMs turned off, or the bot blocked.
     *
     * @param user The user
     * @return The private message channel
     */
    @Nullable
    public static PrivateChannel getDMs(Member user) {
        return getDMs(user.getUser());
    }
}
