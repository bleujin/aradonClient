package net.ion.radon.aclient.resumable;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.atomic.AtomicLong;

import net.ion.radon.aclient.AsyncHandler;
import net.ion.radon.aclient.HttpResponseBodyPart;
import net.ion.radon.aclient.HttpResponseHeaders;
import net.ion.radon.aclient.HttpResponseStatus;
import net.ion.radon.aclient.Request;
import net.ion.radon.aclient.RequestBuilder;
import net.ion.radon.aclient.Response;
import net.ion.radon.aclient.Response.ResponseBuilder;

public class ResumableAsyncHandler implements AsyncHandler<Response> {
	private final AtomicLong byteTransferred;
	private Integer contentLength;
	private String url;
	private final ResumableProcessor resumableProcessor;
	private final AsyncHandler<Response> decoratedAsyncHandler;
	private static Map<String, Long> resumableIndex;
	private final static ResumableIndexThread resumeIndexThread = new ResumableIndexThread();
	private ResponseBuilder responseBuilder = new ResponseBuilder();
	private final boolean accumulateBody;
	private ResumableListener resumableListener = new NULLResumableListener();

	private ResumableAsyncHandler(long byteTransferred, ResumableProcessor resumableProcessor, AsyncHandler<Response> decoratedAsyncHandler, boolean accumulateBody) {

		this.byteTransferred = new AtomicLong(byteTransferred);

		if (resumableProcessor == null) {
			resumableProcessor = new NULLResumableHandler();
		}
		this.resumableProcessor = resumableProcessor;

		resumableIndex = resumableProcessor.load();
		resumeIndexThread.addResumableProcessor(resumableProcessor);

		this.decoratedAsyncHandler = decoratedAsyncHandler;
		this.accumulateBody = accumulateBody;
	}

	public ResumableAsyncHandler(long byteTransferred) {
		this(byteTransferred, null, null, false);
	}

	public ResumableAsyncHandler(boolean accumulateBody) {
		this(0, null, null, accumulateBody);
	}

	public ResumableAsyncHandler() {
		this(0, null, null, false);
	}

	public ResumableAsyncHandler(AsyncHandler<Response> decoratedAsyncHandler) {
		this(0, new PropertiesBasedResumableProcessor(), decoratedAsyncHandler, false);
	}

	public ResumableAsyncHandler(long byteTransferred, AsyncHandler<Response> decoratedAsyncHandler) {
		this(byteTransferred, new PropertiesBasedResumableProcessor(), decoratedAsyncHandler, false);
	}

	public ResumableAsyncHandler(ResumableProcessor resumableProcessor) {
		this(0, resumableProcessor, null, false);
	}

	public ResumableAsyncHandler(ResumableProcessor resumableProcessor, boolean accumulateBody) {
		this(0, resumableProcessor, null, accumulateBody);
	}

	public AsyncHandler.STATE onStatusReceived(final HttpResponseStatus status) throws Exception {
		responseBuilder.accumulate(status);
		if (status.getStatusCode() == 200 || status.getStatusCode() == 206) {
			url = status.getUrl().toURL().toString();
		} else {
			return AsyncHandler.STATE.ABORT;
		}

		if (decoratedAsyncHandler != null) {
			return decoratedAsyncHandler.onStatusReceived(status);
		}

		return AsyncHandler.STATE.CONTINUE;
	}

	public void onThrowable(Throwable t) {
		if (decoratedAsyncHandler != null) {
			decoratedAsyncHandler.onThrowable(t);
		} else {
			t.printStackTrace() ;
		}
	}

	public AsyncHandler.STATE onBodyPartReceived(HttpResponseBodyPart bodyPart) throws Exception {

		if (accumulateBody) {
			responseBuilder.accumulate(bodyPart);
		}

		STATE state = STATE.CONTINUE;
		try {
			resumableListener.onBytesReceived(bodyPart.getBodyByteBuffer());
		} catch (IOException ex) {
			return AsyncHandler.STATE.ABORT;
		}

		if (decoratedAsyncHandler != null) {
			state = decoratedAsyncHandler.onBodyPartReceived(bodyPart);
		}

		byteTransferred.addAndGet(bodyPart.getBodyPartBytes().length);
		resumableProcessor.put(url, byteTransferred.get());

		return state;
	}

	public Response onCompleted() throws Exception {
		resumableProcessor.remove(url);
		resumableListener.onAllBytesReceived();

		if (decoratedAsyncHandler != null) {
			decoratedAsyncHandler.onCompleted();
		}
		// Not sure
		return responseBuilder.build();
	}

	public AsyncHandler.STATE onHeadersReceived(HttpResponseHeaders headers) throws Exception {
		responseBuilder.accumulate(headers);
		if (headers.getHeaders().getFirstValue("Content-Length") != null) {
			contentLength = Integer.valueOf(headers.getHeaders().getFirstValue("Content-Length"));
			if (contentLength == null || contentLength == -1) {
				return AsyncHandler.STATE.ABORT;
			}
		}

		if (decoratedAsyncHandler != null) {
			return decoratedAsyncHandler.onHeadersReceived(headers);
		}
		return AsyncHandler.STATE.CONTINUE;
	}

	public Request adjustRequestRange(Request request) {

		if (resumableIndex.get(request.getUrl()) != null) {
			byteTransferred.set(resumableIndex.get(request.getUrl()));
		}

		// The Resumbale
		if (resumableListener != null && resumableListener.length() > 0 && byteTransferred.get() != resumableListener.length()) {
			byteTransferred.set(resumableListener.length());
		}

		RequestBuilder builder = new RequestBuilder(request);
		if (request.getHeaders().get("Range") == null && byteTransferred.get() != 0) {
			builder.setHeader("Range", "bytes=" + byteTransferred.get() + "-");
		}
		return builder.build();
	}

	public ResumableAsyncHandler setResumableListener(ResumableListener resumableListener) {
		this.resumableListener = resumableListener;
		return this;
	}

	private static class ResumableIndexThread extends Thread {

		public final ConcurrentLinkedQueue<ResumableProcessor> resumableProcessors = new ConcurrentLinkedQueue<ResumableProcessor>();

		public ResumableIndexThread() {
			Runtime.getRuntime().addShutdownHook(this);
		}

		public void addResumableProcessor(ResumableProcessor p) {
			resumableProcessors.offer(p);
		}

		public void run() {
			for (ResumableProcessor p : resumableProcessors) {
				p.save(resumableIndex);
			}
		}
	}

	public static interface ResumableProcessor {

		public void put(String key, long transferredBytes);

		public void remove(String key);

		public void save(Map<String, Long> map);

		public Map<String, Long> load();

	}

	private static class NULLResumableHandler implements ResumableProcessor {

		public void put(String url, long transferredBytes) {
		}

		public void remove(String uri) {
		}

		public void save(Map<String, Long> map) {
		}

		public Map<String, Long> load() {
			return new HashMap<String, Long>();
		}
	}

	private static class NULLResumableListener implements ResumableListener {

		private long length = 0L;

		public void onBytesReceived(ByteBuffer byteBuffer) throws IOException {
			length += byteBuffer.remaining();
		}

		public void onAllBytesReceived() {
		}

		public long length() {
			return length;
		}

	}
}
