package core.commands;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.BlockingQueue;

import com.sedmelluq.discord.lavaplayer.track.AudioTrack;

import core.lavaplayer.GuildMusicManager;
import core.lavaplayer.PlayerManager;
import core.lavaplayer.TrackScheduler;
import core.utils.Command;
import core.utils.CommandContext;
import net.dv8tion.jda.api.entities.GuildVoiceState;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.TextChannel;

public class ShuffleCommand implements Command {

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
		final TrackScheduler scheduler = musicManager.scheduler;
		
		if (scheduler.queue.isEmpty()) {
			channel.sendMessage("*No tracks are currently in queue.*").queue();
			return;
		}
		
		shuffleQueue(scheduler.queue);
		channel.sendMessage("🎵 **Queue is shuffling...** 🔀").queue();
	}

	@Override
	public String getName() {
		return "shuffle";
	}

	@Override
	public String getHelp() {
		return "shuffles the current tracks in queue";
	}
	
	private static void shuffleQueue(BlockingQueue<AudioTrack> queue) {
		final List<AudioTrack> tracksInQueue = new ArrayList<>(queue);
		
		Collections.shuffle(tracksInQueue);
		queue.clear();
		
		for (AudioTrack track : tracksInQueue) {
			queue.offer(track);
		}
	}

}
