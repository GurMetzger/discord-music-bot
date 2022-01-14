package core.lavaplayer;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.sedmelluq.discord.lavaplayer.player.AudioLoadResultHandler;
import com.sedmelluq.discord.lavaplayer.player.AudioPlayerManager;
import com.sedmelluq.discord.lavaplayer.player.DefaultAudioPlayerManager;
import com.sedmelluq.discord.lavaplayer.source.AudioSourceManagers;
import com.sedmelluq.discord.lavaplayer.tools.FriendlyException;
import com.sedmelluq.discord.lavaplayer.track.AudioPlaylist;
import com.sedmelluq.discord.lavaplayer.track.AudioTrack;

import core.playerActivity.AutoLeaveManager;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.TextChannel;

public class PlayerManager {
	
	private final Map<Long, GuildMusicManager> musicManagers;
	private final AudioPlayerManager audioPlayerManager;
	
	private static final PlayerManager INSTANCE = new PlayerManager();

	private PlayerManager() {
		this.musicManagers = new HashMap<>();
		this.audioPlayerManager = new DefaultAudioPlayerManager();

		AudioSourceManagers.registerRemoteSources(this.audioPlayerManager);
		AudioSourceManagers.registerLocalSource(this.audioPlayerManager);
	}

	public static PlayerManager getInstance() {
		return INSTANCE;
	}

	public GuildMusicManager getMusicManager(Guild guild) {
		return this.musicManagers.computeIfAbsent(guild.getIdLong(), (guildId) -> {
			final GuildMusicManager guildMusicManager = new GuildMusicManager(this.audioPlayerManager);
			
			guild.getAudioManager().setSendingHandler(guildMusicManager.getSendHandler());

			return guildMusicManager;
		});
	}

	public void loadAndPlay(TextChannel channel, String trackUrl) {
		final GuildMusicManager musicManager = this.getMusicManager(channel.getGuild());
		
		AutoLeaveManager.getInstance().trackPlayerActivity(channel);
		
		this.audioPlayerManager.loadItemOrdered(musicManager, trackUrl, new AudioLoadResultHandler() {

			@Override
			public void trackLoaded(AudioTrack track) {
				musicManager.scheduler.queue(track);
				
				final boolean isFirstSong = musicManager.scheduler.queue.size() < 1;
				
				channel.sendMessage("🎵 **")
					.append(isFirstSong ? "Now playing:" : "Adding to queue:")
					.append("** `")
					.append(track.getInfo().title)
					.append("` by `")
					.append(track.getInfo().author)
					.append("`")
					.queue();
			}

			@Override
			public void playlistLoaded(AudioPlaylist playlist) {
				final List<AudioTrack> tracks = playlist.getTracks();
				
				// Play ONLY the first song of the automated search result
				if (playlist.isSearchResult()) {
					trackLoaded(tracks.get(0));
					return;
				}
				
				channel.sendMessage("🎵 **Adding to queue:** `")
					.append(String.valueOf(tracks.size()))
					.append("` **tracks from playlist** `")
					.append(playlist.getName())
					.append("`")
					.queue();
				
				for (final AudioTrack track : tracks) {
					musicManager.scheduler.queue(track);
				}
			}

			@Override
			public void noMatches() {
				channel.sendMessage("No search results found for `")
					.append(trackUrl)
					.append("`.")
					.queue();
			}

			@Override
			public void loadFailed(FriendlyException exception) {
				channel.sendMessage("Failed to load track `\"")
					.append(exception.getMessage())
					.append("\"`.")
					.queue();
			}
			
		});
	}

}
