package core.utils;

import java.util.List;

public interface Command {
	
	void handle(CommandContext ctx);
	
	String getName();
	
	String getHelp();
	
	default List<String> getAlias() {
		return List.of();
	}

}
