package net.ion.radon.aclient.multipart;

import java.util.Timer;
import java.util.TimerTask;

/**
 * @author Gail Hernandez
 */
public class FilePartStallHandler extends TimerTask {
	public FilePartStallHandler(long waitTime, FilePart filePart) {
		_waitTime = waitTime;
		_failed = false;
		_written = false;
	}

	public void completed() {
		if (_waitTime > 0) {
			_timer.cancel();
		}
	}

	public boolean isFailed() {
		return _failed;
	}

	public void run() {
		if (!_written) {
			_failed = true;
			_timer.cancel();
		}
		_written = false;
	}

	public void start() {
		if (_waitTime > 0) {
			_timer = new Timer();
			_timer.scheduleAtFixedRate(this, _waitTime, _waitTime);
		}
	}

	public void writeHappened() {
		_written = true;
	}

	private long _waitTime;
	private Timer _timer;
	private boolean _failed;
	private boolean _written;
}
