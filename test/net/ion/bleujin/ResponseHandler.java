package net.ion.bleujin;

import java.io.IOException;

import net.ion.radon.aclient.Response;

public interface ResponseHandler<T> {

	public T handle(Response response) throws IOException ;
}
