package net.ion.radon.aclient.resumable;

import net.ion.radon.aclient.Request;
import net.ion.radon.aclient.filter.FilterContext;
import net.ion.radon.aclient.filter.FilterException;
import net.ion.radon.aclient.filter.IOExceptionFilter;

public class ResumableIOExceptionFilter implements IOExceptionFilter {
	public FilterContext filter(FilterContext ctx) throws FilterException {
		if (ctx.getIOException() != null && ResumableAsyncHandler.class.isAssignableFrom(ctx.getAsyncHandler().getClass())) {
			Request request = ResumableAsyncHandler.class.cast(ctx.getAsyncHandler()).adjustRequestRange(ctx.getRequest());
			return new FilterContext.FilterContextBuilder(ctx).request(request).replayRequest(true).build();
		}
		return ctx;
	}
}
