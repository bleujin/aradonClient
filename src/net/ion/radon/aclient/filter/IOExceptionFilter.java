package net.ion.radon.aclient.filter;

public interface IOExceptionFilter {

	public <T> FilterContext<T> filter(FilterContext<T> ctx) throws FilterException;
}
