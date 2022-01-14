package core;

import java.util.EnumSet;

import javax.annotation.Nonnull;
import javax.security.auth.login.LoginException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import core.commands.CommandManager;
import core.utils.Config;
import net.dv8tion.jda.api.JDABuilder;
import net.dv8tion.jda.api.entities.Activity;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.events.ReadyEvent;
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import net.dv8tion.jda.api.requests.GatewayIntent;
import net.dv8tion.jda.api.utils.cache.CacheFlag;

public class Bot {
	
	public static void main(String[] args) throws LoginException {
		JDABuilder.createDefault(
				Config.get("TOKEN"),
				GatewayIntent.GUILD_MESSAGES,
				GatewayIntent.GUILD_VOICE_STATES
				)
		.setActivity(Activity.playing("Use " + Config.get("PREFIX") + "play to listen!"))
		.disableCache(EnumSet.of(
				CacheFlag.CLIENT_STATUS,
				CacheFlag.ACTIVITY,
				CacheFlag.EMOTE
				))
		.enableCache(CacheFlag.VOICE_STATE)
		.addEventListeners(new Listener())
		.build();
	}
	
	private static class Listener extends ListenerAdapter {
		
		private static final Logger LOGGER = LoggerFactory.getLogger(Listener.class);
		private final CommandManager manager = new CommandManager();
		
		@Override
		public void onReady(ReadyEvent event) {
			LOGGER.info("{} is ready", event.getJDA().getSelfUser().getAsTag());
		}
		
		@Override
		public void onGuildMessageReceived(@Nonnull GuildMessageReceivedEvent event) {
			User user = event.getAuthor();
			if (user.isBot() || event.isWebhookMessage()) return;
			
			String raw = event.getMessage().getContentRaw();
			if (!raw.startsWith(Config.get("PREFIX"))) return;
			
			manager.handle(event);
		}
		
	}

}
