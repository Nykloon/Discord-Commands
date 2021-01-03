package com.github.nykloon.discord_commands;

import java.util.Arrays;
import java.util.regex.Pattern;

public class Command {

    private final ICommand command;
    private final String name;
    private final String[] args;

    public Command(String content, String prefix, CommandSettings commandSettings) {
        String[] argsWithoutPrefix = content.replaceFirst(Pattern.quote(prefix), "").split("\\s+");
        this.name = commandSettings.isIgnoreLabelCase() ? argsWithoutPrefix[0].toLowerCase() : argsWithoutPrefix[0];
        this.command = commandSettings.getCommands().getOrDefault(this.name, null);
        this.args = Arrays.copyOfRange(argsWithoutPrefix, 1, argsWithoutPrefix.length);
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
