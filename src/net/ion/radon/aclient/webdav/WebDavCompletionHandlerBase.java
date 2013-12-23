package net.ion.radon.aclient.webdav;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import net.ion.radon.aclient.AsyncHandler;
import net.ion.radon.aclient.HttpResponseBodyPart;
import net.ion.radon.aclient.HttpResponseHeaders;
import net.ion.radon.aclient.HttpResponseStatus;
import net.ion.radon.aclient.Response;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

public abstract class WebDavCompletionHandlerBase<T> implements AsyncHandler<T> {

	private final List<HttpResponseBodyPart> bodies = Collections.synchronizedList(new ArrayList<HttpResponseBodyPart>());
	private HttpResponseStatus status;
	private HttpResponseHeaders headers;

	public final STATE onBodyPartReceived(final HttpResponseBodyPart content) throws Exception {
		bodies.add(content);
		return STATE.CONTINUE;
	}

	public final STATE onStatusReceived(final HttpResponseStatus status) throws Exception {
		this.status = status;
		return STATE.CONTINUE;
	}

	public final STATE onHeadersReceived(final HttpResponseHeaders headers) throws Exception {
		this.headers = headers;
		return STATE.CONTINUE;
	}

	public final T onCompleted() throws Exception {
		if (status != null) {
			Response response = status.provider().prepareResponse(status, headers, bodies);
			Document document = null;
			if (status.getStatusCode() == 207) {
				document = readXMLResponse(response.getBodyAsStream());
			}
			return onCompleted(new WebDavResponse(status.provider().prepareResponse(status, headers, bodies), document));
		} else {
			throw new IllegalStateException("Status is null");
		}
	}

	public void onThrowable(Throwable t) {
		t.printStackTrace() ;
	}

	abstract public T onCompleted(WebDavResponse response) throws Exception;

	private class HttpStatusWrapper extends HttpResponseStatus {

		private final HttpResponseStatus wrapper;

		private final String statusText;

		private final int statusCode;

		public HttpStatusWrapper(HttpResponseStatus wrapper, String statusText, int statusCode) {
			super(wrapper.getUrl(), wrapper.provider());
			this.wrapper = wrapper;
			this.statusText = statusText;
			this.statusCode = statusCode;
		}

		@Override
		public int getStatusCode() {
			return (statusText == null ? wrapper.getStatusCode() : statusCode);
		}

		@Override
		public String getStatusText() {
			return (statusText == null ? wrapper.getStatusText() : statusText);
		}

		@Override
		public String getProtocolName() {
			return wrapper.getProtocolName();
		}

		@Override
		public int getProtocolMajorVersion() {
			return wrapper.getProtocolMajorVersion();
		}

		@Override
		public int getProtocolMinorVersion() {
			return wrapper.getProtocolMinorVersion();
		}

		@Override
		public String getProtocolText() {
			return wrapper.getStatusText();
		}
	}

	private Document readXMLResponse(InputStream stream) {
		DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
		Document document = null;
		try {
			document = factory.newDocumentBuilder().parse(stream);
			parse(document);
		} catch (SAXException e) {
			throw new RuntimeException(e);
		} catch (IOException e) {
			throw new RuntimeException(e);
		} catch (ParserConfigurationException e) {
			throw new RuntimeException(e);
		}
		return document;
	}

	private void parse(Document document) {
		Element element = document.getDocumentElement();
		NodeList statusNode = element.getElementsByTagName("status");
		for (int i = 0; i < statusNode.getLength(); i++) {
			Node node = statusNode.item(i);

			String value = node.getFirstChild().getNodeValue();
			int statusCode = Integer.valueOf(value.substring(value.indexOf(" "), value.lastIndexOf(" ")).trim());
			String statusText = value.substring(value.lastIndexOf(" "));
			status = new HttpStatusWrapper(status, statusText, statusCode);
		}
	}
}
