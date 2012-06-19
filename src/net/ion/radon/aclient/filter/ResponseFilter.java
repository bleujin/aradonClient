package net.ion.radon.aclient.filter;

public interface ResponseFilter {

	public <T> FilterContext<T> filter(FilterContext<T> ctx) throws FilterException;
}
