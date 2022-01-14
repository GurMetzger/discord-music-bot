package core.commands;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.regex.Pattern;

import javax.annotation.Nullable;

import core.utils.Command;
import core.utils.CommandContext;
import core.utils.Config;
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;

public class CommandManager {
	
	private final List<Command> commands = new ArrayList<>();
	
	public CommandManager() {
		addCommand(new HelpCommand(this));
		
		addCommand(new PingCommand());
		addCommand(new JoinCommand());
		addCommand(new PlayCommand());
		addCommand(new StopCommand());
		addCommand(new SkipCommand());
		addCommand(new NowPlayingCommand());
		addCommand(new QueueCommand());
		addCommand(new RepeatCommand());
		addCommand(new LeaveCommand());
		addCommand(new PauseCommand());
		addCommand(new ResumeCommand());
		addCommand(new ReplayCommand());
		addCommand(new ShuffleCommand());
	}
	
	private void addCommand(Command cmd) {
		boolean nameFound = this.commands.stream().anyMatch(
				(it) -> it.getName().equalsIgnoreCase(cmd.getName()) || cmd.getAlias().contains(it.getName()));
		
		if (nameFound) {
			throw new IllegalArgumentException("A command with this name already present");
		}
		
		commands.add(cmd);
	}
	
	public List<Command> getCommands() {
		return commands;
	}
	
	@Nullable
	public Command getCommand(String search) {
		String searchLower = search.toLowerCase();
		
		for (Command cmd : this.commands) {
			if (cmd.getName().equalsIgnoreCase(searchLower) || cmd.getAlias().contains(searchLower)) {
				return cmd;
			}
		}
		return null;
	}
	
	public void handle(GuildMessageReceivedEvent event) {
		String[] split = event.getMessage().getContentRaw()
				.replaceFirst("(?i)" + Pattern.quote(Config.get("PREFIX")), "")
				.split("\\s+");
		
		String invoke = split[0].toLowerCase();
		Command cmd = this.getCommand(invoke);
		
		if (cmd != null) {
			event.getChannel().sendTyping().queue();
			
			List<String> args = Arrays.asList(split).subList(1, split.length);
			CommandContext ctx = new CommandContext(event, args);
			
			cmd.handle(ctx);
		}
	}
	
}
