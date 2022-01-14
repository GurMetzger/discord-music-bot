package core.lavaplayer;

import java.nio.ByteBuffer;

import javax.annotation.Nullable;

import com.sedmelluq.discord.lavaplayer.player.AudioPlayer;
import com.sedmelluq.discord.lavaplayer.track.playback.MutableAudioFrame;

import net.dv8tion.jda.api.audio.AudioSendHandler;

public class AudioPlayerSendHandler implements AudioSendHandler {

	private final AudioPlayer audioPlayer;
	private final ByteBuffer buffer;
	private final MutableAudioFrame frame;

	public AudioPlayerSendHandler(AudioPlayer audioPlayer) {
		this.audioPlayer = audioPlayer;
		this.buffer = ByteBuffer.allocate(1024);
		this.frame = new MutableAudioFrame();
		this.frame.setBuffer(this.buffer);
	}
	
	@Override
	public boolean canProvide() {
		return this.audioPlayer.provide(this.frame);
	}

	@Nullable
	@Override
	public ByteBuffer provide20MsAudio() {
		return this.buffer.flip();
	}

	@Override
	public boolean isOpus() {
		return true;
	}
	
}