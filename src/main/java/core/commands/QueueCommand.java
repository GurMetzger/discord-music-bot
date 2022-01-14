package core.commands;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.TimeUnit;

import com.sedmelluq.discord.lavaplayer.track.AudioTrack;
import com.sedmelluq.discord.lavaplayer.track.AudioTrackInfo;

import core.lavaplayer.GuildMusicManager;
import core.lavaplayer.PlayerManager;
import core.utils.Command;
import core.utils.CommandContext;
import net.dv8tion.jda.api.entities.TextChannel;
import net.dv8tion.jda.api.requests.restaction.MessageAction;

public class QueueCommand implements Command {

	@Override
	public void handle(CommandContext ctx) {
		final TextChannel channel = ctx.getChannel();
		final GuildMusicManager musicManager = PlayerManager.getInstance().getMusicManager(ctx.getGuild());
		final BlockingQueue<AudioTrack> queue = musicManager.scheduler.queue;
		
		final MessageAction messageAction = channel.sendMessage("\n");
		
		final AudioTrack currentTrack = musicManager.audioPlayer.getPlayingTrack();
		
		if (currentTrack != null) {
			final AudioTrackInfo info = currentTrack.getInfo();
			
			messageAction.append("🎵 **Now playing:**\n")
					.append("`")
					.append(info.title)
					.append("` by `")
					.append(info.author)
					.append("` [`")
					.append(formatTime(currentTrack.getDuration() - currentTrack.getPosition()))
					.append(" left`]\n");
		}
		
		messageAction.append("⏫ **Current queue:**\n");
		
		if (queue.isEmpty()) {
			messageAction.append("*The queue is empty.*").queue();
			return;
		}
		
		final List<AudioTrack> trackList = new ArrayList<>(queue);
		final int trackCount = Math.min(queue.size(), 15);
		
		for (int i = 0; i < trackCount; i++) {
			final AudioTrack track = trackList.get(i);
			final AudioTrackInfo info = track.getInfo();
			
			messageAction.append("#")
					.append(String.valueOf(i + 1))
					.append(" `")
					.append(info.title)
					.append("` by `")
					.append(info.author)
					.append("` [`")
					.append(formatTime(track.getDuration()))
					.append("`]\n");
		}
		
		if (trackList.size() > trackCount) {
			messageAction.append("And `")
					.append(String.valueOf(trackList.size() - trackCount))
					.append("` more...");
		}
		
		messageAction.queue();
	}

	@Override
	public String getName() {
		return "queue";
	}

	@Override
	public String getHelp() {
		return "shows the queued up songs";
	}
	
	@Override
	public List<String> getAlias() {
		return List.of("q", "list");
	}

	private String formatTime(long timeInMillis) {
		final long hours = timeInMillis / TimeUnit.HOURS.toMillis(1);
		final long minutes = timeInMillis % TimeUnit.HOURS.toMillis(1) / TimeUnit.MINUTES.toMillis(1);
		final long seconds = timeInMillis % TimeUnit.MINUTES.toMillis(1) / TimeUnit.SECONDS.toMillis(1);
		
		return (hours > 0) ? String.format("%02d:%02d:%02d", hours, minutes, seconds)
				: String.format("%02d:%02d", minutes, seconds);
	}
	
}
