package core.commands;

import java.util.List;

import core.utils.Command;
import core.utils.CommandContext;
import core.utils.Config;
import net.dv8tion.jda.api.entities.TextChannel;

public class HelpCommand implements Command {

	private final CommandManager manager;
	
	public HelpCommand(CommandManager manager) {
		this.manager = manager;
	}
	
	@Override
	public void handle(CommandContext ctx) {
		List<String> args = ctx.getArgs();
		TextChannel channel = ctx.getChannel();
		
		if (args.isEmpty()) {
			StringBuilder builder = new StringBuilder();
			
			builder.append("List of commands:\n");
			
			manager.getCommands().stream().map(Command::getName).forEach(
					(it) -> builder.append("`").append(Config.get("PREFIX")).append(it).append("`\n"));
			
			builder.append("\nFor details on a specific command:\n")
					.append("`").append(Config.get("PREFIX")).append(getName()).append(" <command name>`");
			
			channel.sendMessage(builder.toString()).queue();
			
			return;
		}
		
		String search = args.get(0);
		Command command = manager.getCommand(search);
		
		if (command == null) {
			channel.sendMessage("Nothing found for " + search).queue();
			return;
		}
		
		channel.sendMessage(command.getHelp()).queue();
	}

	@Override
	public String getName() {
		return "help";
	}

	@Override
	public String getHelp() {
		return "Shows the list with commands in the bot\n" + 
				"Usage: `" + Config.get("PREFIX") + getName() + " [command name]`";
	}
	
	@Override
	public List<String> getAlias() {
		return List.of("commands", "cmds", "commandlist", "h");
	}

}
