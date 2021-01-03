package com.github.nykloon.discord_commands;

import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.TextChannel;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;

import javax.annotation.Nonnull;
import java.util.HashMap;
import java.util.Map;

public class CommandListener extends ListenerAdapter {

    private CommandSettings commandSettings;
    private Map<Long, Long> cooldownMap;

    public CommandListener(CommandSettings commandSettings) {
        this.commandSettings = commandSettings;
        this.cooldownMap = new HashMap<>();
    }

    @Override
    public void onGuildMessageReceived(@Nonnull GuildMessageReceivedEvent event) {
        TextChannel channel = event.getChannel();
        Guild guild = event.getGuild();
        User user = event.getAuthor();

        // Check if user is a bot.
        if (user.isBot()) return;

        // Check if guild is blacklisted.
        if (commandSettings.getBlacklistedGuilds().contains(guild.getIdLong())) {
            Message guildBlacklistedMessage = commandSettings.getGuildBlacklistedMessage();
            if (guildBlacklistedMessage  != null && event.getGuild().getSelfMember().hasPermission(channel, Permission.MESSAGE_WRITE, Permission.MESSAGE_EMBED_LINKS)) {
                channel.sendMessage(guildBlacklistedMessage).queue();
            }
            return;
        }

        // Check if channel is blacklisted.
        if (commandSettings.getBlacklistedChannels().contains(guild.getIdLong())) {
            Message channelBlacklistedMessage = commandSettings.getChannelBlacklistedMessage();
            if (channelBlacklistedMessage  != null && event.getGuild().getSelfMember().hasPermission(channel, Permission.MESSAGE_WRITE, Permission.MESSAGE_EMBED_LINKS)) {
                channel.sendMessage(channelBlacklistedMessage).queue();
            }
            return;
        }

        String content = event.getMessage().getContentRaw();
        String prefix = commandSettings.getPrefix(guild.getIdLong());

        // Check if message starts with prefix.
        if (content.startsWith(prefix)) {
            long timestamp = System.currentTimeMillis();
            long userID = user.getIdLong();

            // Check if user is on cooldown.
            if (cooldownMap.containsKey(userID) && (timestamp - cooldownMap.get(userID)) < commandSettings.getCooldown()) return;
            cooldownMap.put(userID, timestamp);

            // Execute command.
            Command command = new Command(content, prefix, commandSettings);

            try {
                command.getExecutor().onCommand(event, event.getGuild(), event.getMember(), event.getChannel(), event.getMessage(), command.getArgs());
            } catch (Throwable throwable) {
                throwable.printStackTrace();
            }

        }
    }
}
