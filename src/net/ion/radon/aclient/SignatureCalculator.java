package net.ion.radon.aclient;

public interface SignatureCalculator {

	public void calculateAndAddSignature(String url, Request request, RequestBuilderBase<?> requestBuilder);
}
