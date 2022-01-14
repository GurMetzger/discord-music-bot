package core.commands;

import java.util.List;
import java.util.concurrent.TimeUnit;

import com.sedmelluq.discord.lavaplayer.player.AudioPlayer;
import com.sedmelluq.discord.lavaplayer.track.AudioTrack;
import com.sedmelluq.discord.lavaplayer.track.AudioTrackInfo;

import core.lavaplayer.GuildMusicManager;
import core.lavaplayer.PlayerManager;
import core.utils.Command;
import core.utils.CommandContext;
import net.dv8tion.jda.api.entities.GuildVoiceState;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.TextChannel;

public class NowPlayingCommand implements Command {

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
		final AudioTrack track = audioPlayer.getPlayingTrack();

		if (audioPlayer.getPlayingTrack() == null) {
			channel.sendMessage("*There is no track currently playing.*").queue();
			return;
		}

		AudioTrackInfo info = track.getInfo();
		channel.sendMessageFormat("🎵 **Now playing:** `%s` by `%s` [`%s left`] (Link: <%s>)", 
				info.title, info.author, formatTime(track.getDuration() - track.getPosition()), info.uri)
			.queue();
	}

	@Override
	public String getName() {
		return "nowplaying";
	}

	@Override
	public String getHelp() {
		return "Shows the currently playing song";
	}

	@Override
	public List<String> getAlias() {
		return List.of("current", "curr");
	}

	private String formatTime(long timeInMillis) {
		final long hours = timeInMillis / TimeUnit.HOURS.toMillis(1);
		final long minutes = timeInMillis % TimeUnit.HOURS.toMillis(1) / TimeUnit.MINUTES.toMillis(1);
		final long seconds = timeInMillis % TimeUnit.MINUTES.toMillis(1) / TimeUnit.SECONDS.toMillis(1);

		return (hours > 0) ? String.format("%02d:%02d:%02d", hours, minutes, seconds)
				: String.format("%02d:%02d", minutes, seconds);
	}

}
