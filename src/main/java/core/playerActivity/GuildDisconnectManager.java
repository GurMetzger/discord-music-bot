package core.playerActivity;

import core.lavaplayer.GuildMusicManager;
import core.lavaplayer.PlayerManager;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.TextChannel;

public class GuildDisconnectManager {

	private final GuildMusicManager musicManager;
	private final Guild guild;
	private TextChannel channel;
	public int notUsedCount;
	
	public GuildDisconnectManager(TextChannel channel) {
		this.channel = channel;
		this.guild = channel.getGuild();
		this.musicManager = PlayerManager.getInstance().getMusicManager(this.guild);
	}

	public GuildMusicManager getMusicManager() {
		return musicManager;
	}
	
	public Guild getGuild() {
		return guild;
	}

	public TextChannel getChannel() {
		return channel;
	}

	public void setChannel(TextChannel channel) {
		this.channel = channel;
	}
	
}
