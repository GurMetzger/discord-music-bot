package core.commands;

import java.util.List;

import com.sedmelluq.discord.lavaplayer.player.AudioPlayer;

import core.lavaplayer.GuildMusicManager;
import core.lavaplayer.PlayerManager;
import core.utils.Command;
import core.utils.CommandContext;
import net.dv8tion.jda.api.entities.GuildVoiceState;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.TextChannel;

public class ResumeCommand implements Command {

	@Override
	public void handle(CommandContext ctx) {
		final TextChannel channel = ctx.getChannel();
		final Member self = ctx.getSelfMember();
		final GuildVoiceState selfVoiceState = self.getVoiceState();

		if (!selfVoiceState.inVoiceChannel()) {
			channel.sendMessage("I need to be in a voice channel for this to work").queue();
			return;
		}

		final Member member = ctx.getMember();
		final GuildVoiceState memberVoiceState = member.getVoiceState();

		if (!memberVoiceState.inVoiceChannel()) {
			channel.sendMessage("You need to be in a voice channel for this command to work").queue();
			return;
		}

		if (!memberVoiceState.getChannel().equals(selfVoiceState.getChannel())) {
			channel.sendMessage("You need to be in the same voice channel as me for this to work").queue();
			return;
		}
		
		final GuildMusicManager musicManager = PlayerManager.getInstance().getMusicManager(ctx.getGuild());
		final AudioPlayer audioPlayer = musicManager.audioPlayer;
		
		if (audioPlayer.getPlayingTrack() == null) {
			channel.sendMessage("*No tracks are currently playing.*").queue();
			return;
		}
		
		if (!audioPlayer.isPaused()) {
			channel.sendMessage("❌ **The player is not paused**").queue();
			return;
		}
		
		musicManager.scheduler.resumeTrack();
		channel.sendMessage("⏯ **Resuming** 👍").queue();
	}

	@Override
	public String getName() {
		return "resume";
	}

	@Override
	public String getHelp() {
		return "Resumes the current song";
	}
	
	@Override
	public List<String> getAlias() {
		return List.of("continue", "unpause");
	}

}
