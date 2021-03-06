/*
 * Copyright 2011 The Netty Project
 *
 * The Netty Project licenses this file to you under the Apache License,
 * version 2.0 (the "License"); you may not use this file except in compliance
 * with the License. You may obtain a copy of the License at:
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations
 * under the License.
 */
package net.ion.radon.aclient.providers.netty.codec.http.websocketx;

import net.ion.radon.aclient.providers.netty.codec.http.ExtendHttpHeaders.Names;

import org.jboss.netty.channel.Channel;
import org.jboss.netty.handler.codec.http.DefaultHttpResponse;
import org.jboss.netty.handler.codec.http.HttpRequest;
import org.jboss.netty.handler.codec.http.HttpResponse;
import org.jboss.netty.handler.codec.http.HttpResponseStatus;
import org.jboss.netty.handler.codec.http.HttpVersion;

/**
 * Instances the appropriate handshake class to use for servers
 */
public class WebSocketServerHandshakerFactory {

    private final String webSocketURL;

    private final String subprotocols;

    private final boolean allowExtensions;

    /**
     * Constructor specifying the destination web socket location
     * 
     * @param webSocketURL
     *            URL for web socket communications. e.g "ws://myhost.com/mypath". Subsequent web socket frames will be
     *            sent to this URL.
     * @param subprotocols
     *            CSV of supported protocols. Null if sub protocols not supported.
     * @param allowExtensions
     *            Allow extensions to be used in the reserved bits of the web socket frame
     */
    public WebSocketServerHandshakerFactory(String webSocketURL, String subprotocols, boolean allowExtensions) {
        this.webSocketURL = webSocketURL;
        this.subprotocols = subprotocols;
        this.allowExtensions = allowExtensions;
    }

    /**
     * Instances a new handshaker
     * 
     * @return A new WebSocketServerHandshaker for the requested web socket version. Null if web socket version is not
     *         supported.
     */
    public WebSocketServerHandshaker newHandshaker(HttpRequest req) {

        String version = req.getHeader(Names.SEC_WEBSOCKET_VERSION);
        if (version != null) {
            if (version.equals(WebSocketVersion.V13.toHttpHeaderValue())) {
                // Version 13 of the wire protocol - RFC 6455 (version 17 of the draft hybi specification).
                return new WebSocketServerHandshaker13(webSocketURL, subprotocols, allowExtensions);
            } else if (version.equals(WebSocketVersion.V08.toHttpHeaderValue())) {
                // Version 8 of the wire protocol - version 10 of the draft hybi specification.
                return new WebSocketServerHandshaker08(webSocketURL, subprotocols, allowExtensions);
            } else {
                return null;
            }
        } else {
            // Assume version 00 where version header was not specified
            return new WebSocketServerHandshaker00(webSocketURL, subprotocols);
        }
    }

    /**
     * Return that we need cannot not support the web socket version
     * 
     * @param channel
     *            Channel
     */
    public void sendUnsupportedWebSocketVersionResponse(Channel channel) {
        HttpResponse res = new DefaultHttpResponse(
                HttpVersion.HTTP_1_1,
                HttpResponseStatus.SWITCHING_PROTOCOLS);
        res.setStatus(HttpResponseStatus.UPGRADE_REQUIRED);
        res.setHeader(Names.SEC_WEBSOCKET_VERSION, WebSocketVersion.V13.toHttpHeaderValue());
        channel.write(res);
    }

}
