package com.flyingh.mediaplayer2;

import static com.flyingh.mediaplayer2.State.PAUSE;
import static com.flyingh.mediaplayer2.State.PLAY;
import static com.flyingh.mediaplayer2.State.RESET;
import static com.flyingh.mediaplayer2.State.STOP;

import java.io.File;

import android.app.Activity;
import android.media.MediaPlayer;
import android.media.MediaPlayer.OnPreparedListener;
import android.os.Bundle;
import android.os.Environment;
import android.telephony.PhoneStateListener;
import android.telephony.TelephonyManager;
import android.view.Menu;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

public class MainActivity extends Activity {
	private EditText musicText;
	private MediaPlayer player = new MediaPlayer();
	private State state = STOP;
	private Button playOrPauseButton;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		musicText = (EditText) findViewById(R.id.music);
		playOrPauseButton = (Button) findViewById(R.id.playOrPause);
		TelephonyManager service = (TelephonyManager) getSystemService(TELEPHONY_SERVICE);
		service.listen(new PhoneStateListener() {
			@Override
			public void onCallStateChanged(int state, String incomingNumber) {
				super.onCallStateChanged(state, incomingNumber);
				switch (state) {
				case TelephonyManager.CALL_STATE_RINGING:
					pause();
					break;
				case TelephonyManager.CALL_STATE_IDLE:
					if (MainActivity.this.state == PAUSE) {
						start();
					}
					break;

				default:
					break;
				}
			}
		}, PhoneStateListener.LISTEN_CALL_STATE);
	}

	public void playOrPause(View view) {
		if (player.isPlaying()) {
			pause();
		} else {
			play();
		}
	}

	private void play() {
		if (state == PAUSE) {
			start();
			return;
		}
		String musicName = musicText.getText().toString();
		if (musicName == null || "".equals(musicName.trim())) {
			return;
		}
		File file = new File(Environment.getExternalStorageDirectory(), musicName);
		if (file.exists()) {
			player.reset();
			try {
				player.setDataSource(file.getAbsolutePath());
				player.prepare();
				player.setOnPreparedListener(new OnPreparedListener() {

					@Override
					public void onPrepared(MediaPlayer mp) {
						start();
					}

				});
			} catch (Exception e) {
				Toast.makeText(this, R.string.error, Toast.LENGTH_LONG).show();
			}

		} else {
			Toast.makeText(this, R.string.file_not_exists_, Toast.LENGTH_LONG).show();
		}

	}

	private void start() {
		player.start();
		state = PLAY;
		playOrPauseButton.setText(R.string.pause);
	}

	private void pause() {
		player.pause();
		state = PAUSE;
		playOrPauseButton.setText(R.string.play);
	}

	public void reset(View view) {
		state = RESET;
		if (player.isPlaying()) {
			reset();
		} else {
			play();
		}
	}

	private void reset() {
		player.seekTo(0);
		state = PLAY;
		playOrPauseButton.setText(R.string.pause);
	}

	public void stop(View view) {
		if (player.isPlaying() || state == PAUSE) {
			stop();
		}
	}

	private void stop() {
		player.stop();
		state = STOP;
		playOrPauseButton.setText(R.string.play);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}

}
