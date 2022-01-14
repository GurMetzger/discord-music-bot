package core.playerActivity;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import com.sedmelluq.discord.lavaplayer.player.AudioPlayer;

import core.lavaplayer.GuildMusicManager;
import net.dv8tion.jda.api.entities.TextChannel;
import net.dv8tion.jda.api.managers.AudioManager;

public class AutoLeaveManager extends Thread {
	
	private final Map<Long, GuildDisconnectManager> disconnectManagers;
	private static final int MAX_WAIT_TIME = 30;
	
	private static final AutoLeaveManager INSTANCE = new AutoLeaveManager();

	private AutoLeaveManager() {
		this.disconnectManagers = new HashMap<>();
		INSTANCE.start();
	}

	public static AutoLeaveManager getInstance() {
		return INSTANCE;
	}
	
	private GuildDisconnectManager getDisconnectManager(TextChannel channel) {
		return this.disconnectManagers.computeIfAbsent(channel.getGuild().getIdLong(), (guildId) -> {
			return new GuildDisconnectManager(channel);
		});
	}
	
	public void trackPlayerActivity(TextChannel channel) {
		final GuildDisconnectManager disconnectManager = this.getDisconnectManager(channel);
		disconnectManager.setChannel(channel);
	}
	
	@Override
	public void run() {
		while (true) {
			try {
				Thread.sleep(TimeUnit.MINUTES.toMillis(1));
				
				for (var entry : disconnectManagers.entrySet()) {
					final GuildDisconnectManager guildDisconnect = entry.getValue();
					final GuildMusicManager musicManager = guildDisconnect.getMusicManager();
					
					final AudioManager audioManager = guildDisconnect.getGuild().getAudioManager();
					final AudioPlayer audioPlayer = musicManager.audioPlayer;
					
					if (!audioManager.isConnected()) {
						guildDisconnect.notUsedCount = 0;
						continue;
					}
					
					if (audioPlayer.getPlayingTrack() == null) {
						guildDisconnect.notUsedCount++;
						
						if (guildDisconnect.notUsedCount >= MAX_WAIT_TIME) {
							final TextChannel channel = guildDisconnect.getChannel();
							
							audioManager.closeAudioConnection();
							channel.sendMessage("**I disconnected due to inactivity.**").queue();
						}
					} else {
						guildDisconnect.notUsedCount = 0;
					}
				}
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
	}
	
}
