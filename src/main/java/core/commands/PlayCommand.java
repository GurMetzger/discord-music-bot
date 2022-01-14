package core.commands;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.List;

import core.lavaplayer.PlayerManager;
import core.utils.Command;
import core.utils.CommandContext;
import core.utils.Config;
import net.dv8tion.jda.api.entities.GuildVoiceState;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.TextChannel;
import net.dv8tion.jda.api.entities.VoiceChannel;
import net.dv8tion.jda.api.managers.AudioManager;

public class PlayCommand implements Command {
	
	@Override
	public void handle(CommandContext ctx) {
		final TextChannel channel = ctx.getChannel();
		
		if (ctx.getArgs().isEmpty()) {
			channel.sendMessage("Correct usage is `" + Config.get("PREFIX") + getName() + " <youtube link>`").queue();
			return;
		}
		
		final Member self = ctx.getSelfMember(), member = ctx.getMember();
		final GuildVoiceState selfVoiceState = self.getVoiceState(), memberVoiceState = member.getVoiceState();
		
		if (!memberVoiceState.inVoiceChannel()) {
			channel.sendMessage("You need to be in a voice channel for this command to work").queue();
			return;
		}
		
		if (selfVoiceState.inVoiceChannel() && !memberVoiceState.getChannel().equals(selfVoiceState.getChannel())) {
			channel.sendMessage("You need to be in the same voice channel as me for this to work").queue();
			return;
		}
		
		// Automatically join's user VC if Bot is not currently in one
		if (!selfVoiceState.inVoiceChannel()) {
			final AudioManager audioManager = ctx.getGuild().getAudioManager();
			final VoiceChannel memberChannel = memberVoiceState.getChannel();
			
			audioManager.openAudioConnection(memberChannel);
			channel.sendMessageFormat("👍 Connecting to `\uD83D\uDD0A %s`", memberChannel.getName()).queue();
		}
		
		String link = String.join(" ", ctx.getArgs());
		
		if (!isUrl(link)) {
			link = "ytsearch:" + link;
		}
		
		PlayerManager.getInstance().loadAndPlay(channel, link);
	}
	
	@Override
	public String getName() {
		return "play";
	}
	
	@Override
	public String getHelp() {
		return "Plays a song\n" + 
				"Usage: `" + Config.get("PREFIX") + getName() + " [link]`";
	}
	
	@Override
	public List<String> getAlias() {
		return List.of("p");
	}
	
	private boolean isUrl(String url) {
		try {
			new URL(url);
			return true;
		} catch (MalformedURLException e) {
			return false;
		}
	}
	
}
