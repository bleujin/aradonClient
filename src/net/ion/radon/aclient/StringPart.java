package net.ion.radon.aclient;

public class StringPart implements Part {
	private final String name;
	private final String value;
	private final String charset;

	public StringPart(String name, String value, String charset) {
		this.name = name;
		this.value = value;
		this.charset = charset;
	}

	public StringPart(String name, String value) {
		this.name = name;
		this.value = value;
		this.charset = "UTF-8";
	}

	public String getName() {
		return name;
	}

	public String getValue() {
		return value;
	}

	public String getCharset() {
		return charset;
	}

}