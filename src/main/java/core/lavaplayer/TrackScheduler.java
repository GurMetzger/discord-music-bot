package core.lavaplayer;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

import com.sedmelluq.discord.lavaplayer.player.AudioPlayer;
import com.sedmelluq.discord.lavaplayer.player.event.AudioEventAdapter;
import com.sedmelluq.discord.lavaplayer.tools.FriendlyException;
import com.sedmelluq.discord.lavaplayer.track.AudioTrack;
import com.sedmelluq.discord.lavaplayer.track.AudioTrackEndReason;

public class TrackScheduler extends AudioEventAdapter {
	
	public final AudioPlayer player;
	public final BlockingQueue<AudioTrack> queue;
	public boolean isRepeating;
	
	public TrackScheduler(AudioPlayer player) {
		this.player = player;
		this.queue = new LinkedBlockingQueue<>();
		this.isRepeating = false;
	}
	
	public void queue(AudioTrack track) {
		if (!this.player.startTrack(track, true)) {
			this.queue.offer(track);
		}
	}
	
	public void nextTrack() {
		this.player.startTrack(this.queue.poll(), false);
	}
	
	public void pauseTrack() {
		this.player.setPaused(true);
	}
	
	public void resumeTrack() {
		this.player.setPaused(false);
	}
	
	public void replayTrack() {
		if (this.player.getPlayingTrack() != null) {
			this.player.startTrack(this.player.getPlayingTrack().makeClone(), false);
		}
	}

	@Override
	public void onTrackEnd(AudioPlayer player, AudioTrack track, AudioTrackEndReason endReason) {	
		if (endReason.mayStartNext) {	
			if (this.isRepeating) {
				this.player.startTrack(track.makeClone(), false);
			} else {
				nextTrack();
			}
		}
	}
	
	@Override
	public void onPlayerPause(AudioPlayer player) {}

	@Override
	public void onPlayerResume(AudioPlayer player) {}

	@Override
	public void onTrackStart(AudioPlayer player, AudioTrack track) {}

	@Override
	public void onTrackException(AudioPlayer player, AudioTrack track, FriendlyException exception) {}

	@Override
	public void onTrackStuck(AudioPlayer player, AudioTrack track, long thresholdMs) {}
	
}
