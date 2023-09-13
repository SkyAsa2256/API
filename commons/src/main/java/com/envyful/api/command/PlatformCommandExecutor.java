package com.envyful.api.command;

public interface PlatformCommandExecutor<C> {

    void execute(C sender, String[] args);

}
