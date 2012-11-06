package net.ion.radon.aclient.multipart;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.charset.Charset;

public class FilePart extends PartBase {

	public static final String DEFAULT_CONTENT_TYPE = "application/octet-stream";

	public static final String DEFAULT_CHARSET = "ISO-8859-1";

	public static final String DEFAULT_TRANSFER_ENCODING = "binary";

	protected static final String FILE_NAME = "; filename=";

	private static final byte[] FILE_NAME_BYTES = MultipartEncodingUtil.getAsciiBytes(FILE_NAME);

	private PartSource source;

	public FilePart(String name, PartSource partSource, String contentType, String charsetName) {
		super(name, contentType == null ? DEFAULT_CONTENT_TYPE : contentType, (charsetName == null || (!Charset.isSupported(charsetName))) ? Charset.forName("ISO-8859-1") : Charset.forName(charsetName), DEFAULT_TRANSFER_ENCODING);

		if (partSource == null) {
			throw new IllegalArgumentException("Source may not be null");
		}
		this.source = partSource;
	}

	public FilePart(String name, PartSource partSource) {
		this(name, partSource, null, null);
	}

	public FilePart(String name, File file) throws FileNotFoundException {
		this(name, new FilePartSource(file), null, null);
	}

	public FilePart(String name, File file, String contentType, String charset) throws FileNotFoundException {
		this(name, new FilePartSource(file), contentType, charset);
	}

	public FilePart(String name, String fileName, File file) throws FileNotFoundException {
		this(name, new FilePartSource(fileName, file), null, null);
	}

	public FilePart(String name, String fileName, File file, String contentType, String charset) throws FileNotFoundException {
		this(name, new FilePartSource(fileName, file), contentType, charset);
	}

	protected void sendDispositionHeader(OutputStream out) throws IOException {
		super.sendDispositionHeader(out);
		String filename = this.source.getFileName();
		if (filename != null) {
			out.write(FILE_NAME_BYTES);
			out.write(QUOTE_BYTES);
			// out.write(MultipartEncodingUtil.getAsciiBytes(filename));
			out.write(filename.getBytes("UTF-8"));
			out.write(QUOTE_BYTES);
		}
	}

	protected void sendData(OutputStream out) throws IOException {
		if (lengthOfData() == 0) {

			// this file contains no data, so there is nothing to send.
			// we don't want to create a zero length buffer as this will
			// cause an infinite loop when reading.
			return;
		}

		byte[] tmp = new byte[4096];
		InputStream instream = source.createInputStream();
		try {
			int len;
			while ((len = instream.read(tmp)) >= 0) {
				out.write(tmp, 0, len);
			}
		} finally {
			// we're done with the stream, close it
			instream.close();
		}
	}

	public void setStalledTime(long ms) {
		_stalledTime = ms;
	}

	public long getStalledTime() {
		return _stalledTime;
	}

	protected PartSource getSource() {
		return this.source;
	}

	protected long lengthOfData() throws IOException {
		return source.getLength();
	}

	private long _stalledTime = -1;

}
