# Discord CommandHandler API
Discord Command Handler, written in [Java](https://www.java.com/) - using [Discord JDA](https://github.com/DV8FromTheWorld/JDA). This simple library allows you to create and modify
your commands - you can set a few messages, custom prefixes, and more. Make sure to check it out!
<br />
<br />
### Setup
1. Parameter: **Prefix** | This is your default prefix, all commands will be performed by using this prefix.
2. Parameter: **Instance** | Put your JDA or ShardManager instance here.
3. Parameter: **IgnoreCase** | If this is on TRUE, the commands will be case insensitive.
```java
CommandSettings commandSettings = new CommandSettings("!!!", jda, true);
commandSettings.activate(); // This is very important.
```

### Adding a Command
1. Parameter: **Command Class** | This is the class of your command implementing ICommand.
2. Parameter: **Name** | Name of the command, can't be null.
3. Parameter: **Description** | @Nullable Description of the command.
```java
commandSettings.add(new HelloCommand(), "hello", "say hello!"); 
```

### Extras
You can set extra settings as mentioned above. Make sure to set it before activating CommandSettings.
```java
commandSettings.setCooldown(10); // Members will get a 10 seconds cooldown once they perform a command.
commandSettings.setCustomPrefix(guildId, prefix); // Set the new prefix in a specific guild.
commandSettings.addChannelToBlacklist(channelID); // Add channels to the blacklist - commands can't be executed.
commandSettings.addGuildToBlacklist(guildID); // Add a guild to the blacklist - commands can't be executed.
```

### Messages
Sometimes, you want the member to know that something went wrong. You can easily do that with messages (lol).
As above, this has to be set before activating. You can add **Guild Blacklisted**, **Channel Blacklisted** and **Unknown Command** messages atm.
```java
Message unknownCommandMessage = new MessageBuilder("This command doesn't exist").build();
commandSettings.setUnknownCommandMessage(unknownCommandMessage);```
