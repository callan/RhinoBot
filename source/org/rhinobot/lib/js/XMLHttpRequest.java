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
package org.rhinobot.lib.js;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.URL;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.mozilla.javascript.Context;
import org.mozilla.javascript.Function;
import org.mozilla.javascript.Scriptable;
import org.mozilla.javascript.ScriptableObject;

/**
 * This is the implementation of the famous XMLHttpRequest used in browsers.
 */
public final class XMLHttpRequest extends ScriptableObject
{
	private final class ContentHandler extends Thread
	{
		private String content;
		
		private ContentHandler (final String content)
		{
			this.content = content;
			setName("XMLHttpRequest");
			setPriority(Thread.NORM_PRIORITY);
			start();
		}
		
		public final void run ()
		{
			if (content != null)
			{
				try
				{
					final DataOutputStream out = new DataOutputStream(connection.getOutputStream());
					
					out.writeBytes(content);
					out.flush();
					out.close();
				}
				catch (IOException e)
				{
					statusText = e.getMessage();
					callFunction(onError, thiz);
				}
			}
			
			try
			{
				final BufferedReader in = new BufferedReader(new InputStreamReader(connection.getInputStream()));
				
				setState(3);

				String line;
				
				for (;;)
				{
					line = in.readLine();
					
					if ((line == null) || (!running))
					{
						break;
					}
					
					responseText += line + "\r\n";
				}

				in.close();
				
				if (!running)
				{
					return;
				}
			}
			catch (IOException e)
			{
				statusText = e.getMessage();
				callFunction(onError, thiz);
			}
			catch (Exception e)
			{
				statusText = e.getMessage();
				callFunction(onError, thiz);
			}
			
			if (!mimeOverridden)
			{
				mimeType = connection.getContentType();
			}
			
			if (mimeType.equalsIgnoreCase("text/xml"))
			{
				Context cx = Context.getCurrentContext();
				final Scriptable scope = onReadyStateChange.getParentScope();
				
				if (responseXMLCompiler == null)
				{
					// REDO
					final String script = "function jsXmlHttpRequestXMLCompile (xml) { return (xml != null) ? new XML(xml) : null; }";
					responseXMLCompiler = cx.compileFunction(scope, script, "jsXmlHttpRequestXMLCompile", 0, null);
				}
				
				responseXML = responseXMLCompiler.call(cx, scope, scope, new Object[] { responseText });
			}
			
			System.out.println(responseText);
			
			setState(4);
			running = false;
		}
	}
	
	static Function			responseXMLCompiler;
	
	/**
	 * 0 = uninitialized<br>
	 * 1 = loading<br>
	 * 2 = loaded<br>
	 * 3 = interactive (not done grabbing data)<br>
	 * 4 = complate<br>
	 */
	private int						readyState		= 0;
	
	boolean	running			= false;

	private	boolean					asynchronous	= false;
	
	String					statusText;
	
	String					mimeType		= "text/xml";
	boolean					mimeOverridden	= false;
	
	String					responseText	= "";
	
	Object					responseXML		= "";
	
	Function				onReadyStateChange,
							onError;
	
	private URL						url;
	
	HttpURLConnection		connection;
	
	Object[]				thiz;
	
	public XMLHttpRequest ()
	{
		ScriptableObject thiz1 = this;
		thiz = new Object[] { thiz1 };
	}
	
	/**
	 * Overidden
	 * @see org.mozilla.javascript.Scriptable#getClassName()
	 */
	public String getClassName ()
	{
		return "XMLHttpRequest";
	}
	
	/**
	 * The JS Getter for onreadystatechange.
	 * @return
	 */
	public Function jsGet_onreadystatechange ()
	{
		return onReadyStateChange;
	}
	
	/**
	 * The JS Getter for statusText
	 * @return
	 */
	public String jsGet_statusText ()
	{
		return statusText;
	}
	
	/**
	 * JS Getter for responseText
	 * @return
	 */
	public String jsGet_responseText ()
	{
		return responseText;
	}
	
	/**
	 * JS Getter for responseXML
	 * @return
	 */
	public Object jsGet_responseXML ()
	{
		return responseXML;
	}
	
	/**
	 * JS Getter for mimeType
	 * @return
	 */
	public String jsGet_mimeType ()
	{
		return mimeType;
	}
	
	/**
	 * The JS Setter for onreadystatechange
	 * @param newStateChangeFunction
	 */
	public void jsSet_onreadystatechange (Function newStateChangeFunction)
	{
		onReadyStateChange = newStateChangeFunction;
	}
	
	/**
	 * Set onError
	 * @param newOnErrorFunction
	 */
	public void jsSet_onerror (Function newOnErrorFunction)
	{
		onError = newOnErrorFunction;
	}
	
	/**
	 * Calls a function and sends params
	 * @param func
	 * @param params
	 */
	void callFunction (Function func, Object[] params)
	{
		Context cx = Context.getCurrentContext();
		
		if (cx == null) { cx = Context.enter(); }
		
		if (func == null)
		{
			return;
		}
		
		if (params == null)
		{
			params = new Object[0];
		}
		
		func.call(cx, func.getParentScope(), this, params);
	}
	
	/**
	 * Sets the readyState and calls the js function<br>
	 * <code>
	 * 0 = uninitialized<br>
	 * 1 = loading<br>
	 * 2 = loaded<br>
	 * 3 = interactive (not done grabbing data)<br>
	 * 4 = complate<br>
	 * </code>
	 * <br>
	 * @param state
	 */
	synchronized void setState (int state)
	{
		readyState = state;
		
		callFunction(onReadyStateChange, null);
	}
	
	/**
	 * Ready state
	 * 
	 * @return
	 */
	public int jsGet_readyState ()
	{
		return readyState;
	}
	
	/**
	 * Gets the response code, or -1 if an error
	 * @return -1 on error
	 */
	public int jsGet_status ()
	{
		try
		{
			return connection.getResponseCode();
		}
		catch (IOException e)
		{
			return -1;
		}
	}
	
	/**
	 * 
	 * @param method
	 * @param url
	 */
	public void jsFunction_open (String method, String url)
	{
		jsFunction_open(method, url, false, null, null);
	}
	
	/**
	 * 
	 * @param method
	 * @param url
	 * @param asyncFlag
	 */
	public void jsFunction_open (String method, String url, boolean asyncFlag)
	{
		jsFunction_open(method, url, asyncFlag, null, null);
	}
	
	/**
	 * 
	 * @param method
	 * @param url
	 * @param asyncFlag
	 * @param username
	 */
	public void jsFunction_open (String method, String url, boolean asyncFlag, String username)
	{
		jsFunction_open(method, url, asyncFlag, username, null);
	}
	
	/**
	 * 
	 * @param method
	 * @param url1
	 * @param asyncFlag
	 * @param username
	 * @param password
	 */
	public void jsFunction_open (String method, String url1, boolean asyncFlag, String username, String password)
	{
		// Clear all the event listeners
		onReadyStateChange	= null;
		onError				= null;
		connection			= null;
		url					= null;
		running				= false;
		
		asynchronous		= asyncFlag;
		
		if ((!method.equalsIgnoreCase("POST")) && (!method.equalsIgnoreCase("GET")))
		{
			return;
		}
		
		try
		{
			url	= new URL(url1);
		}
		catch (MalformedURLException e)
		{
			statusText = e.getMessage();
			callFunction(onError, thiz);
			return;
		}
		
		try
		{
			connection = (HttpURLConnection) url.openConnection();
			setState(1);
		}
		catch (IOException e)
		{
			statusText = e.getMessage();
			callFunction(onError, thiz);
			return;
		}
		
		try
		{
			connection.setRequestMethod(method);
		}
		catch (ProtocolException e1)
		{
			return;
		}
		
		if (method.equalsIgnoreCase("POST"))
		{
			connection.setDoOutput(true);
			jsFunction_setRequestHeader("Content-Type", "application/x-www-form-urlencoded");
		}
		
		connection.setDoInput(true);		
		connection.setUseCaches(false);
		
		HttpURLConnection.setFollowRedirects(true);
		
		if ((username != null) && (password != null))
		{
//			TODO fix
//			jsFunction_setRequestHeader("Authorization", "Basic " + crypt.Base64Encode(username + ":" + password));
		}
		
		try
		{
			connection.connect();
		}
		catch (IOException e)
		{
			statusText = e.getMessage();
			callFunction(onError, thiz);
			return;
		}
		
		setState(2);
	}
	
	/**
	 * @param newMimeType
	 */
	public void jsFunction_overrideMimeType (String newMimeType)
	{
		mimeType		= newMimeType;
		mimeOverridden	= true;
	}
	
	/**
	 * JavaScript constructor
	 *
	 */
	public void jsConstructor ()
	{
		setState(0);
	}
	
	/**
	 * 
	 * @param content
	 */
	public void jsFunction_send (String content)
	{
		if (url == null)
		{
			return;
		}
		
		if (content == null)
		{
			content = "";
		}
		
		running = true;
		new ContentHandler(content);
	}
	
	/**
	 * Aborts the connection
	 *
	 */
	public void jsFunction_abort ()
	{
		if (url == null)
		{
			return;
		}
		
		if (asynchronous)
		{
			running = false;
		}
		
		connection 	= null;
		url			= null;
	}
	
	/**
	 * Sets a request header
	 * @param label
	 * @param value
	 */
	public void jsFunction_setRequestHeader (String label, String value)
	{
		if (url == null)
		{
			return;
		}
		
		if (connection.getRequestProperty(label) == null)
		{
			connection.addRequestProperty(label, value);
		}
		else
		{
			connection.setRequestProperty(label, value);
		}
	}
	
	/**
	 * Get a response header
	 * @param label
	 * @return NULL if it's not there
	 */
	public String jsFunction_getResponseHeader (String label)
	{
		if (url == null)
		{
			return null;
		}
		
		return connection.getRequestProperty(label);
	}
	
	/**
	 * Get all the response headers
	 * @return
	 */
	public String jsFunction_getAllResponseHeaders ()
	{
		if (url == null)
		{
			return null;
		}
		
		Map<String, List<String>>	responseHeaders	= connection.getRequestProperties();
		Set<String>					set				= responseHeaders.keySet();
		String						headers			= "";
		
		for (String key : set)
		{
			headers += key + ": ";
			List<String> list = responseHeaders.get(key);
			
			for (int i = 0; i < list.size(); i++)
			{
				headers += list.get(i);
				
				if (i + 1 < list.size())
				{
					headers += ",";
				}
			}
			
			headers += "\r\n";
		}
		
		return headers;
	}
}
