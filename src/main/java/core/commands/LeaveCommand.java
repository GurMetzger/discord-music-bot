package core.commands;

import java.util.List;

import core.lavaplayer.GuildMusicManager;
import core.lavaplayer.PlayerManager;
import core.utils.Command;
import core.utils.CommandContext;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.GuildVoiceState;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.TextChannel;
import net.dv8tion.jda.api.managers.AudioManager;

public class LeaveCommand implements Command {

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
		
		final Guild guild = ctx.getGuild();
		final GuildMusicManager musicManager = PlayerManager.getInstance().getMusicManager(guild);
		
		musicManager.audioPlayer.stopTrack();
		musicManager.scheduler.queue.clear();
		musicManager.scheduler.isRepeating = false;
		
		final AudioManager audioManager = guild.getAudioManager();
		
		audioManager.closeAudioConnection();
		channel.sendMessage("*I've left the voice channel.*").queue();
	}

	@Override
	public String getName() {
		return "leave";
	}

	@Override
	public String getHelp() {
		return "Makes the bot leave the voice channel it is currently in";
	}

	@Override
	public List<String> getAlias() {
		return List.of("disconnect", "dis");
	}

}
