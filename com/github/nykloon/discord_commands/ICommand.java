package com.github.nykloon.discord_commands;

import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.TextChannel;
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;

/**
 * @author Nyk76
 * @version 1.0.0
 * @code https://www.github.com/Nykloon/Discord-Commands
 * Interface, required for any command.
 */
public interface ICommand {

    /**
     * @param event The GuildMessageReceivedEvent instance from JDA.
     * @param guild The guild the command has been called on.
     * @param member The member who used the command.
     * @param channel The channel the member used to command in.
     * @param message The message the member sent.
     * @param args The splitted arguments of it's content.
     */

    void onCommand(final GuildMessageReceivedEvent event, final Guild guild, final Member member, final TextChannel channel, final Message message, final String[] args);

}
