package core.commands;

import java.util.List;

import core.utils.Command;
import core.utils.CommandContext;
import net.dv8tion.jda.api.entities.GuildVoiceState;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.TextChannel;
import net.dv8tion.jda.api.entities.VoiceChannel;
import net.dv8tion.jda.api.managers.AudioManager;

public class JoinCommand implements Command {

	@Override
	public void handle(CommandContext ctx) {
		final TextChannel channel = ctx.getChannel();
		final Member self = ctx.getSelfMember();
		final GuildVoiceState selfVoiceState = self.getVoiceState();
		
		if (selfVoiceState.inVoiceChannel()) {
			channel.sendMessage("I'm already in a voice channel").queue();
			return;
		}
		
		final Member member = ctx.getMember();
		final GuildVoiceState memberVoiceState = member.getVoiceState();
		
		if (!memberVoiceState.inVoiceChannel()) {
			channel.sendMessage("You need to be in a voice channel for this command to work").queue();
			return;
		}
		
		final AudioManager audioManager = ctx.getGuild().getAudioManager();
		final VoiceChannel memberChannel = memberVoiceState.getChannel();
		
		audioManager.openAudioConnection(memberChannel);
		channel.sendMessageFormat("👍 Connecting to `\uD83D\uDD0A %s`", memberChannel.getName()).queue();
	}

	@Override
	public String getName() {
		return "join";
	}

	@Override
	public String getHelp() {
		return "Makes the bot join your voice channel";
	}
	
	@Override
	public List<String> getAlias() {
		return List.of("j");
	}

}
