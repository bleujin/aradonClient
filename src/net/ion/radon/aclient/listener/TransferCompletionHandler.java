package net.ion.radon.aclient.listener;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.List;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.atomic.AtomicLong;

import net.ion.framework.util.Debug;
import net.ion.radon.aclient.AsyncCompletionHandlerBase;
import net.ion.radon.aclient.FluentCaseInsensitiveStringsMap;
import net.ion.radon.aclient.HttpResponseBodyPart;
import net.ion.radon.aclient.HttpResponseHeaders;
import net.ion.radon.aclient.Response;

public class TransferCompletionHandler extends AsyncCompletionHandlerBase {
	private final ConcurrentLinkedQueue<TransferListener> listeners = new ConcurrentLinkedQueue<TransferListener>();
	private final boolean accumulateResponseBytes;
	private TransferAdapter transferAdapter;
	private AtomicLong bytesTransferred = new AtomicLong();
	private AtomicLong totalBytesToTransfer = new AtomicLong(0);

	public TransferCompletionHandler() {
		this(false);
	}

	public TransferCompletionHandler(boolean accumulateResponseBytes) {
		this.accumulateResponseBytes = accumulateResponseBytes;
	}

	public TransferCompletionHandler addTransferListener(TransferListener t) {
		listeners.offer(t);
		return this;
	}

	public TransferCompletionHandler removeTransferListener(TransferListener t) {
		listeners.remove(t);
		return this;
	}

	public void transferAdapter(TransferAdapter transferAdapter) {
		this.transferAdapter = transferAdapter;
	}

	public STATE onHeadersReceived(final HttpResponseHeaders headers) throws Exception {
		fireOnHeaderReceived(headers.getHeaders());
		return super.onHeadersReceived(headers);
	}

	public STATE onBodyPartReceived(final HttpResponseBodyPart content) throws Exception {
		STATE s = STATE.CONTINUE;
		if (accumulateResponseBytes) {
			s = super.onBodyPartReceived(content);
		}
		fireOnBytesReceived(content.getBodyPartBytes());
		return s;
	}

	@Override
	public Response onCompleted(Response response) throws Exception {
		fireOnEnd();
		return response;
	}

	public STATE onHeaderWriteCompleted() {
		List<String> list = transferAdapter.getHeaders().get("Content-Length");
		if (list != null && list.size() > 0 && list.get(0) != "") {
			totalBytesToTransfer.set(Long.valueOf(list.get(0)));
		}

		fireOnHeadersSent(transferAdapter.getHeaders());
		return STATE.CONTINUE;
	}

	public STATE onContentWriteCompleted() {
		return STATE.CONTINUE;
	}

	public STATE onContentWriteProgress(long amount, long current, long total) {
		if (bytesTransferred.get() == -1) {
			return STATE.CONTINUE;
		}

		if (totalBytesToTransfer.get() == 0) {
			totalBytesToTransfer.set(total);
		}

		// We need to track the count because all is asynchronous and Netty may not invoke us on time.
		bytesTransferred.addAndGet(amount);

		if (transferAdapter != null) {
			byte[] bytes = new byte[(int) (amount)];
			transferAdapter.getBytes(bytes);
			fireOnBytesSent(bytes);
		}
		return STATE.CONTINUE;
	}

	@Override
	public void onThrowable(Throwable t) {
		fireOnThrowable(t);
	}

	private void fireOnHeadersSent(FluentCaseInsensitiveStringsMap headers) {
		for (TransferListener l : listeners) {
			try {
				l.onRequestHeadersSent(headers);
			} catch (Throwable t) {
				l.onThrowable(t);
			}
		}
	}

	private void fireOnHeaderReceived(FluentCaseInsensitiveStringsMap headers) {
		for (TransferListener l : listeners) {
			try {
				l.onResponseHeadersReceived(headers);
			} catch (Throwable t) {
				l.onThrowable(t);
			}
		}
	}

	private void fireOnEnd() {
		// There is a probability that the asynchronous listener never gets called, so we fake it at the end once
		// we are 100% sure the response has been received.
		long count = bytesTransferred.getAndSet(-1);
		if (count != totalBytesToTransfer.get()) {
			if (transferAdapter != null) {
				byte[] bytes = new byte[8192];
				int leftBytes = (int) (totalBytesToTransfer.get() - count);
				int length = 8192;
				while (leftBytes > 0) {
					if (leftBytes > 8192) {
						leftBytes -= 8192;
					} else {
						length = leftBytes;
						leftBytes = 0;
					}

					if (length < 8192) {
						bytes = new byte[length];
					}

					transferAdapter.getBytes(bytes);
					fireOnBytesSent(bytes);
				}
			}
		}

		for (TransferListener l : listeners) {
			try {
				l.onRequestResponseCompleted();
			} catch (Throwable t) {
				l.onThrowable(t);
			}
		}
	}

	private void fireOnBytesReceived(byte[] b) {
		for (TransferListener l : listeners) {
			try {
				l.onBytesReceived(ByteBuffer.wrap(b));
			} catch (Throwable t) {
				l.onThrowable(t);
			}
		}
	}

	private void fireOnBytesSent(byte[] b) {
		for (TransferListener l : listeners) {
			try {
				l.onBytesSent(ByteBuffer.wrap(b));
			} catch (Throwable t) {
				l.onThrowable(t);
			}
		}
	}

	private void fireOnThrowable(Throwable t) {
		for (TransferListener l : listeners) {
			try {
				l.onThrowable(t);
			} catch (Throwable t2) {
				Debug.warn("onThrowable", t2);
			}
		}
	}

	public abstract static class TransferAdapter {
		private final FluentCaseInsensitiveStringsMap headers;

		public TransferAdapter(FluentCaseInsensitiveStringsMap headers) throws IOException {
			this.headers = headers;
		}

		public FluentCaseInsensitiveStringsMap getHeaders() {
			return headers;
		}

		public abstract void getBytes(byte[] bytes);
	}
}
