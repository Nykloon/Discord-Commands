package com.github.nykloon.discord_commands;

import java.util.Arrays;

public class Command {

    private final ICommand command;
    private final String name;
    private final String[] args;

    public Command(String content, String prefix, CommandSettings commandSettings) {
        String[] argsWithoutPrefix = content.replaceFirst(prefix, "").split("\\s+");
        this.name = commandSettings.isIgnoreLabelCase() ? argsWithoutPrefix[0].toLowerCase() : argsWithoutPrefix[0];
        if (!commandSettings.getCommands().containsKey(name)) {
            this.command = null;
            this.args = null;
        } else {
            this.command = commandSettings.getCommands().get(this.name);
            this.args = Arrays.copyOfRange(argsWithoutPrefix, 1, argsWithoutPrefix.length);
        }
    }

    public String getName() {
        return name;
    }

    public ICommand getExecutor() {
        return command;
    }

    public String[] getArgs() {
        return args;
    }
}
