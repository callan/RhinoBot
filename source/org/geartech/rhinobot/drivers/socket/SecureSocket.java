/**
 * RhinoBot
 * Modular IRC Bot
 * By Christopher Allan <chris@geartech.org>
 *
 * If you like this software, please e-mail me with suggestions/comments/etc..
 *
 * Copyright (c) 2009, Christopher Allan
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 *     * Redistributions of source code must retain the above copyright
 *       notice, this list of conditions and the following disclaimer.
 *     * Redistributions in binary form must reproduce the above copyright
 *       notice, this list of conditions and the following disclaimer in the
 *       documentation and/or other materials provided with the distribution.
 *     * Neither the name of the product nor the
 *       names of its contributors may be used to endorse or promote products
 *       derived from this software without specific prior written permission.
 *
 * THIS SOFTWARE IS PROVIDED BY Christopher Allan ''AS IS'' AND ANY
 * EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED
 * WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
 * DISCLAIMED. IN NO EVENT SHALL Christopher Allan BE LIABLE FOR ANY
 * DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES
 * (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES;
 * LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND
 * ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT
 * (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS
 * SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */
package org.geartech.rhinobot.drivers.socket;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;

import java.security.KeyStore;
import java.security.SecureRandom;
import java.security.Security;
import java.security.cert.X509Certificate;

import javax.net.ssl.KeyManagerFactory;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSocket;
import javax.net.ssl.SSLSocketFactory;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;

/**
 * 
 */
public class SecureSocket implements SocketDriver
{
	private final class MyTrustManager implements X509TrustManager
	{
		/**
		 * @see javax.net.ssl.X509TrustManager#checkClientTrusted(java.security.cert.X509Certificate[],
		 *      java.lang.String)
		 */
		public void checkClientTrusted (X509Certificate[] arg0, String arg1)
		{
			for (X509Certificate arg : arg0) 
			{
				try { arg.checkValidity(); }
				catch (Exception e) { }
			}
		}

		/**
		 * @see javax.net.ssl.X509TrustManager#checkServerTrusted(java.security.cert.X509Certificate[],
		 *      java.lang.String)
		 */
		public void checkServerTrusted (X509Certificate[] arg0, String arg1)
		{
			for (X509Certificate arg : arg0) 
			{
				try { arg.checkValidity(); }
				catch (Exception e) { }
			}
		}

		/**
		 * @see javax.net.ssl.X509TrustManager#getAcceptedIssuers()
		 */
		public X509Certificate[] getAcceptedIssuers ()
		{
			return (new X509Certificate[0]);
		}
	}

	private PrintWriter		writer;
	private BufferedReader	reader;
	private SSLSocket		socket;

	
	/* (non-Javadoc)
	 * @see org.geartech.rhinobot.drivers.SocketDriver#close()
	 */
	@Override
	public void close () throws Exception
	{
		if (socket != null)
			socket.close();
	}
	
	/* (non-Javadoc)
	 * @see org.geartech.rhinobot.drivers.SocketDriver#connected()
	 */
	@Override
	public boolean connected ()
	{
		return (socket != null && socket.isConnected());
	}
	
	/* (non-Javadoc)
	 * @see org.geartech.rhinobot.drivers.SocketDriver#open(java.lang.String, int, java.lang.String)
	 */
	@Override
	public void open (String address, int port, String charset) throws Exception
	{
		String certFile = "rhinobot.crt";
		String certPass = "serverkspw";
		String certPass2 = "serverpw";
		String sslMode  = "TLS";
		
		Security.addProvider(new com.sun.net.ssl.internal.ssl.Provider());
		
		SSLContext			context = null;
		SSLSocketFactory	factory = null;
		KeyStore			store   = null;
		KeyManagerFactory   manager = null;
		
		store   = KeyStore.getInstance("JKS");
		manager = KeyManagerFactory.getInstance("SunX509");
		
		store.load(new FileInputStream(certFile), (certPass).toCharArray());
		manager.init(store, (certPass2).toCharArray());
		
		context = SSLContext.getInstance(sslMode);
		
		context.init(manager.getKeyManagers(), new TrustManager[] { new MyTrustManager() }, new SecureRandom());
		factory = context.getSocketFactory();

		socket = (SSLSocket) factory.createSocket(address, port);
		socket.setSoTimeout(0);
		socket.setKeepAlive(true);
		
		String[] ciphers = new String[] {
			"TLS_RSA_WITH_AES_128_CBC_SHA",
			"TLS_DHE_RSA_WITH_AES_128_CBC_SHA",
			"TLS_DHE_DSS_WITH_AES_128_CBC_SHA",
			"SSL_RSA_WITH_RC4_128_MD5",
			"SSL_RSA_WITH_RC4_128_SHA",
			"SSL_RSA_WITH_3DES_EDE_CBC_SHA",
			"SSL_DHE_RSA_WITH_3DES_EDE_CBC_SHA",
			"SSL_DHE_DSS_WITH_3DES_EDE_CBC_SHA",
			"SSL_RSA_WITH_DES_CBC_SHA",
			"SSL_DHE_RSA_WITH_DES_CBC_SHA",
			"SSL_DHE_DSS_WITH_DES_CBC_SHA",
			"SSL_RSA_EXPORT_WITH_RC4_40_MD5",
			"SSL_RSA_EXPORT_WITH_DES40_CBC_SHA",
			"SSL_DHE_RSA_EXPORT_WITH_DES40_CBC_SHA",
			"SSL_DHE_DSS_EXPORT_WITH_DES40_CBC_SHA"
		};

		socket.setEnabledCipherSuites(ciphers);
		socket.setWantClientAuth(true);
		socket.setEnableSessionCreation(true);
		socket.startHandshake();
		
		 reader = new BufferedReader(new InputStreamReader(socket.getInputStream(), charset));
		 writer = new PrintWriter(new OutputStreamWriter(socket.getOutputStream(), charset), true);
	}
	
	/* (non-Javadoc)
	 * @see org.geartech.rhinobot.drivers.SocketDriver#readLine()
	 */
	@Override
	public String readLine () throws Exception
	{
		if (!connected())
			return null;
		
		return reader.readLine();
	}
	
	/* (non-Javadoc)
	 * @see org.geartech.rhinobot.drivers.SocketDriver#writeLine(java.lang.String)
	 */
	@Override
	public void writeLine (String line)
	{
		if (!connected())
			return;
		
		writer.println(line);
	}
}
