package net.ion.radon.aclient.providers.netty;

import org.jboss.netty.channel.ChannelHandlerContext;
import org.jboss.netty.channel.ChannelStateEvent;
import org.jboss.netty.channel.ExceptionEvent;
import org.jboss.netty.channel.MessageEvent;

public interface Protocol {

	void handle(ChannelHandlerContext ctx, MessageEvent e) throws Exception;

	void onError(ChannelHandlerContext ctx, ExceptionEvent e);

	void onClose(ChannelHandlerContext ctx, ChannelStateEvent e);
}
