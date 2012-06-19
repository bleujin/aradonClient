package net.ion.radon.aclient.filter;

public interface RequestFilter {

	public <T> FilterContext<T> filter(FilterContext<T> ctx) throws FilterException;
}
