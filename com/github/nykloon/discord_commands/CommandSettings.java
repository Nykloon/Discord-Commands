package com.github.nykloon.discord_commands;

import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.MessageBuilder;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.sharding.ShardManager;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.logging.Logger;

public class CommandSettings {

    private static final String VALID_LABEL = "[^\\s]+";
    public static final Logger LOGGER = Logger.getLogger("CommandHandler");
    private static final String INVALID_PREFIX = "[CommandHandler] Invalid Prefix ] Prefix cannot be empty!";

    private final Set<Long> blacklistedChannels; // Set of ChannelIDs - Commands won't execute if channel is contained.
    private final Set<Long> blacklistedGuilds; // Set of GuildIDs - Commands won't execute if guild is contained.

    private Message channelBlacklistedMessage;
    private Message guildBlacklistedMessage;
    private Message cooldownMessage;

    private Message unknownCommand;
    private String defaultPrefix;
    private long commandCooldown;

    private boolean ignoreLabelCase;

    private final CommandListener commandListener;

    private Map<Long, String> customPrefixMap; // Custom Prefix - [Long] GuildID and [String] Prefix
    private Map<String, ICommand> commandMap; // Command and name, description

    private Object jda;

    private boolean useSharding;
    private boolean activated;

    /**
     * Constructor used to create the settings.
     * @param defaultPrefix The default prefix of the bot. Commands will be executed with this prefix by default.
     * @param jda The JDA instance.
     * @param ignoreLabelCase Ignore the label case on commands.
     */
    public CommandSettings(@Nonnull String defaultPrefix, @Nonnull JDA jda, boolean ignoreLabelCase) {
        this(defaultPrefix, ignoreLabelCase);
        this.jda = jda;
        this.useSharding = false;
    }

    /**
     * Constructor used to create the settings.
     * @param defaultPrefix The default prefix of the bot. Commands will be executed with this prefix by default.
     * @param shardManager The ShardManager instance.
     * @param ignoreLabelCase Ignore the label case on commands.
     */
    public CommandSettings(@Nonnull String defaultPrefix, @Nonnull ShardManager shardManager, boolean ignoreLabelCase) {
        this(defaultPrefix, ignoreLabelCase);
        this.jda = shardManager;
        this.useSharding = true;
    }

    public CommandSettings(@Nonnull String defaultPrefix, boolean ignoreLabelCase) {
        this.blacklistedChannels = new HashSet<>();
        this.blacklistedGuilds = new HashSet<>();
        this.customPrefixMap = new HashMap<>();
        this.commandMap = new HashMap<>();

        this.commandListener = new CommandListener(this);

        this.ignoreLabelCase = ignoreLabelCase;
        this.defaultPrefix = defaultPrefix;

        this.commandCooldown = 0;
        this.activated = false;
    }

    /**
     * @param channelID The ID of the channel to be blacklisted.
     * @return The current object.
     */
    public CommandSettings addChannelToBlacklist(long channelID) {
        this.blacklistedChannels.add(channelID);
        return this;
    }

    /**
     * @param channelID The IDs of the channels to be blacklisted.
     * @return The current object.
     */
    public CommandSettings addChannelsToBlacklist(long... channelID) {
        for (long ids : channelID) this.blacklistedChannels.add(ids);
        return this;
    }

    /**
     * @param guildID The ID of the guild to be blacklisted.
     * @return The current object.
     */
    public CommandSettings addGuildToBlacklist(long guildID) {
        this.blacklistedChannels.add(guildID);
        return this;
    }

    /**
     * @param guildID The IDs of the guilds to be blacklisted.
     * @return The current object.
     */
    public CommandSettings addGuildsToBlacklist(long... guildID) {
        for (long ids : guildID) this.blacklistedChannels.add(ids);
        return this;
    }

    /**
     * @param channelID The ID of the channel to remove.
     * @return Successability.
     */
    public boolean removeChannelFromBlacklist(long channelID) {
        return this.blacklistedChannels.remove(channelID);
    }

    /**
     * @param channelID The IDs of the channels to remove.
     * @return Successability.
     */
    public boolean removeChannelsFromBlacklist(long... channelID) {
        boolean success = true;
        for (long ids : channelID) {
            if (!this.removeChannelFromBlacklist(ids)) success = false;
        }
        return success;
    }

    /**
     * @param guildID The ID of the guild to remove.
     * @return Successability.
     */
    public boolean removeGuildFromBlacklist(long guildID) {
        return this.blacklistedGuilds.remove(guildID);
    }

    /**
     * @param guildID The IDs of the guilds to removed.
     * @return Successability.
     */
    public boolean removeGuildssFromBlacklist(long... guildID) {
        boolean success = true;
        for (long ids : guildID) {
            if (!this.removeGuildFromBlacklist(ids)) success = false;
        }
        return success;
    }

    /**
     * Clears the channel blacklist.
     * @return The current object.
     */
    public CommandSettings clearChannelBlacklist() {
        this.blacklistedChannels.clear();
        return this;
    }

    /**
     * Clears the guild blacklist.
     * @return The current object.
     */
    public CommandSettings clearGuildBlacklist() {
        this.blacklistedGuilds.clear();
        return this;
    }

    /**
     * Add a command to the settings.
     * @param command The Command instance.
     * @param name The name of the command.
     * @return The current object.
     */
    public CommandSettings add(@Nonnull ICommand command, @Nonnull String name) {
        if (name.matches(VALID_LABEL))
            this.commandMap.put(name, command);
        else
            throw new IllegalArgumentException("Command Name \"" + name + "\" is not valid.");
        return this;
    }

    /**
     * Add a command to the settings including a description.
     * @param command The Command instance.
     * @param name The name of the command.
     * @param description Description of the command.
     * @return The current object.
     */
    public CommandSettings add(@Nonnull ICommand command, @Nonnull String name, String description) {
        if (name.matches(VALID_LABEL))
            this.commandMap.put(name, command);
        else
            throw new IllegalArgumentException("Command Name \"" + name + "\" is not valid.");
        return this;
    }

    /**
     * Set the default prefix. Commands will be executed with this prefix.
     * @param prefix The prefix you want to use.
     * @return The current object.
     */
    public CommandSettings setDefaultPrefix(@Nonnull String prefix) {
        this.defaultPrefix = prefix;
        return this;
    }

    /**
     * Set the prefix of a specific guild.
     * @param guildID The ID of the guild.
     * @param prefix Prefix you want to use.
     * @return The current object.
     */
    public CommandSettings setCustomPrefix(long guildID, @Nullable String prefix) {
        if (prefix != null)
            this.customPrefixMap.put(guildID, prefix);
        else
            this.customPrefixMap.remove(guildID);
        return this;
    }

    /**
     * Sets the cooldown of the instance.
     * @param seconds Seconds to wait before executing a command.
     * @return The current object.
     */
    public CommandSettings setCooldown(int seconds) {
        this.commandCooldown = seconds * 1000L;
        return this;
    }

    /**
     * Activate the CommandSettings.
     * If this isn't called after setup, the handler won't work.
     */
    public void activate() {
        if (!this.activated) {
            if (useSharding)
                ((ShardManager)jda).addEventListener(commandListener);
            else
                ((JDA)jda).addEventListener(commandListener);
            this.activated = true;
            LOGGER.info("CommandSettings successfully activated.");
        } else {
            throw new RuntimeException("CommandSettings already activated!");
        }
    }

    /**
     * Set the message that pops up once the member calls a command while on cooldown.
     * @param message The message you want to send.
     * @return The current object.
     */
    public CommandSettings setCooldownMessage(@Nullable Message message) {
        if (message == null) this.cooldownMessage = null;
        else this.cooldownMessage = new MessageBuilder(message).build();
        return this;
    }

    /**
     * Set the message that pops up once the member calls a command that doesn't exist.
     * @param message The message you want to send.
     * @return The current object.
     */
    public CommandSettings setUnknownCommandMessage(@Nullable Message message) {
        if (message == null) this.unknownCommand = null;
        else this.unknownCommand = new MessageBuilder(message).build();
        return this;
    }

    /**
     * Set the message that pops up once the member calls a command in a blacklisted channel.
     * @param message The message you want to send.
     * @return The current object.
     */
    public CommandSettings setChannelBlacklistedMessage(@Nullable Message message) {
        if (message == null) this.channelBlacklistedMessage = null;
        else this.channelBlacklistedMessage = new MessageBuilder(message).build();
        return this;
    }

    /**
     * Set the message that pops up once the member calls a command in a blacklisted guild.
     * @param message The message you want to send.
     * @return The current object.
     */
    public CommandSettings setGuildBlacklistedMessage(@Nullable Message message) {
        if (message == null) this.guildBlacklistedMessage = null;
        else this.guildBlacklistedMessage = new MessageBuilder(message).build();
        return this;
    }

    /**
     * Get the prefix of a specific guild.
     * @param guildID ID of the guild.
     * @return The current object.
     */
    public String getPrefix(long guildID) {
        String prefix = this.customPrefixMap.get(guildID);
        return prefix != null ? prefix : defaultPrefix;
    }

    /**
     * @return The default prefix.
     */
    public String getPrefix() {
        return this.defaultPrefix;
    }

    /**
     * @return Set of blacklisted channels.
     */
    public Set<Long> getBlacklistedChannels() {
        return this.blacklistedChannels;
    }

    /**
     * @return Set of blacklisted guilds.
     */
    public Set<Long> getBlacklistedGuilds() {
        return this.blacklistedGuilds;
    }

    /**
     * @return Message object.
     */
    public Message getChannelBlacklistedMessage() {
        return this.channelBlacklistedMessage;
    }

    /**
     * @return Message object.
     */
    public Message getGuildBlacklistedMessage() {
        return this.guildBlacklistedMessage;
    }

    /**
     * @return Message object.
     */
    public Message getUnknownCommand() {
        return this.unknownCommand;
    }

    /**
     * @return Command execution cooldown in ms.
     */
    public long getCooldown() {
        return this.commandCooldown;
    }

    /**
     * @return true, if ignore case on labels.
     */
    public boolean isIgnoreLabelCase() {
        return this.ignoreLabelCase;
    }

    /**
     * @return Map of custom prefixes.
     */
    public Map<Long, String> getCustomPrefixMap() {
        return this.customPrefixMap;
    }

    /**
     * @return Map of commands.
     */
    public Map<String, ICommand> getCommands() {
        return this.commandMap;
    }

    /**
     * @return Status of activation.
     */
    public boolean isActivated() {
        return this.activated;
    }
}
