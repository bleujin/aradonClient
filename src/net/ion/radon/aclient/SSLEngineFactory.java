package net.ion.radon.aclient;

import java.security.GeneralSecurityException;

import javax.net.ssl.SSLEngine;

public interface SSLEngineFactory {

	SSLEngine newSSLEngine() throws GeneralSecurityException;
}
