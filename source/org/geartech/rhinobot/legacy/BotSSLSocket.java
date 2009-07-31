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
package org.geartech.rhinobot.legacy;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.UnknownHostException;
import java.security.KeyStore;
import java.security.SecureRandom;
import java.security.Security;
import java.security.cert.CertificateExpiredException;
import java.security.cert.CertificateNotYetValidException;
import java.security.cert.X509Certificate;

import javax.net.ssl.KeyManagerFactory;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSocket;
import javax.net.ssl.SSLSocketFactory;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;

import org.geartech.rhinobot.drivers.socket.SocketDriver;

public final class BotSSLSocket implements SocketDriver
{
	/**
	 * This makes all the certificates friendly.
	 * 
	 * @author Chris Allan
	 * @version 1.0.0
	 */
	private final class BotTrustManager implements X509TrustManager
	{
		
		/**
		 * @see javax.net.ssl.X509TrustManager#checkClientTrusted(java.security.cert.X509Certificate[],
		 *      java.lang.String)
		 */
		public void checkClientTrusted (X509Certificate[] arg0, @SuppressWarnings("unused") String arg1)
		{
			for (X509Certificate arg : arg0) 
			{
				try
				{
					arg.checkValidity();
				}
				catch (CertificateExpiredException e)
				{
				}
				catch (CertificateNotYetValidException e)
				{
				}
			}
		}

		/**
		 * @see javax.net.ssl.X509TrustManager#checkServerTrusted(java.security.cert.X509Certificate[],
		 *      java.lang.String)
		 */
		public void checkServerTrusted (X509Certificate[] arg0, @SuppressWarnings("unused") String arg1)
		{
			for (X509Certificate arg : arg0) 
			{
				try
				{
					arg.checkValidity();
				}
				catch (CertificateExpiredException e)
				{
					
				}
				catch (CertificateNotYetValidException e)
				{
					
				}
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
	
	/**
	 * Overidden
	 * @see org.rhinobot.bot.BotSocket#getClassName()
	 */
	public final String getClassName ()
	{
		return "BotSSLSocket";
	}
	
	/**
	 * Overidden
	 * @see org.rhinobot.bot.BotSocket#getRequiredConfig()
	 */
	public final String getRequiredConfig ()
	{
		return "rhinobot.conf";
	}
	
	/**
	 * Overidden
	 * @see org.rhinobot.bot.BotSocket#getRawWriter()
	 */
	public final PrintWriter getRawWriter ()
	{
		return writer;
	}

	/**
	 * Overidden
	 * @see org.rhinobot.bot.BotSocket#getRawReader()
	 */
	public final BufferedReader getRawReader ()
	{
		return reader;
	}
	
	/**
	 * 
	 * Overidden
	 * @see org.rhinobot.bot.BotSocket#open(java.lang.String, int, java.lang.String)
	 */
	public final void open (final String address, final int port, final String charset)
				throws UnknownHostException, IOException, Exception
	{
		/*
		if (!configReader.blockExists("rhinobot>ssl"))
		{
			throw new ConfigReader.ConfigException("Expected SSL block");
		}
		
		String certFile  = configReader.getSetting("rhinobot>ssl", "cert-file");
		String certPass  = configReader.getSetting("rhinobot>ssl", "cert-pass");
		String certPass2 = configReader.getSetting("rhinobot>ssl", "cert-pass2");
		String sslMode   = configReader.getSetting("rhinobot>ssl", "mode");
		
		Security.addProvider(new com.sun.net.ssl.internal.ssl.Provider());

		SSLContext			context		= null;
		SSLSocketFactory	factory		= null;

		KeyStore			keystore	= null;
		KeyManagerFactory	key			= null;

		keystore	= KeyStore.getInstance("JKS");
		key			= KeyManagerFactory.getInstance("SunX509");

		keystore.load(new FileInputStream(certFile), (certPass).toCharArray());
		
		key.init(keystore, (certPass2).toCharArray());
		
		context = SSLContext.getInstance(sslMode);
		
		context.init(key.getKeyManagers(), new TrustManager[] { new BotTrustManager() }, new SecureRandom());
		
		factory = context.getSocketFactory();

		socket = (SSLSocket) factory.createSocket(address, port);
		socket.setSoTimeout(0x0);
		socket.setKeepAlive(true);

		String[] ciphers = new String[]
		{
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

		if (socket == null)
		{
			throw new IOException("Socket is null");
		}

		reader = new BufferedReader(new InputStreamReader(socket.getInputStream(), charset));
		writer = new PrintWriter(new OutputStreamWriter(socket.getOutputStream(), charset), true);
		*/
	}

	/**
	 * Overidden
	 * @see org.rhinobot.bot.BotSocket#connected()
	 */
	public final boolean connected ()
	{
		return (socket != null) ? socket.isConnected() : false;
	}
	
	/**
	 * Overidden
	 * @see org.rhinobot.bot.BotSocket#readLine()
	 */
	public final String readLine () throws IOException
	{
		if (reader != null)
		{
			return reader.readLine();
		}
		else
		{
			return null;
		}
	}
	
	/**
	 * Overidden
	 * @see org.rhinobot.bot.BotSocket#writeLine(String)
	 */
	public final void writeLine (final String line)
	{
		if (writer != null)
		{
			writer.println(line);
		}
	}

	/**
	 * Overidden
	 * @see org.rhinobot.bot.BotSocket#close()
	 */
	public final void close () throws IOException
	{
		if (socket != null)
		{
			socket.close();
		}
	}

}
