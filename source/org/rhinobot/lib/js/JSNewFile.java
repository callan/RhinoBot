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
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;

import org.mozilla.javascript.Context;
import org.mozilla.javascript.Function;
import org.mozilla.javascript.ScriptableObject;

public class JSNewFile extends ScriptableObject
{
	private File file;
	
	private boolean writing = false;
	private boolean reading = false;
	
	private FileWriter writer;
	
	private Function onRead;
	private Function onError;
	
	/**
	 * Overidden
	 * @see org.mozilla.javascript.ScriptableObject#getClassName()
	 */
	public final String getClassName ()
	{
		return "NFile";
	}
	
	public final void jsSet_onread (Function function)
	{
		onRead = function;
	}
	
	public final Function jsGet_onread ()
	{
		return onRead;
	}
	
	public final void jsSet_onerror (Function function)
	{
		onError = function;
	}
	
	public final Function jsGet_onerror ()
	{
		return onError;
	}
	
	/**
	 * Calls a function and sends params
	 * @param func
	 * @param params
	 */
	private final void callFunction (Function func, Object[] params)
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
	
	public final void jsConstructor (String fileName)
	{
		file = new File(fileName);
		
		if (file.isDirectory())
		{
			file = null;
		}
	}
	
	public final void jsFunction_write (String data) throws IOException
	{
		// This assumes that there was an error in the constructor and so nothing will be loadable.
		// Also, if the file is not opened for writing this will be ignored.
		if ((file == null) || (!writing)) return;
		
		if (writer == null)
		{
			writer = new FileWriter(file);
		}
		
		writer.write(data);
		writer.flush();
	}
	
	public final void jsFunction_close () throws IOException
	{
		if (writer != null)
		{
			writer.close();
		}
	}
	
	/**
	 * Reads to the int length, then calls onRead. on non-binary reads this will be approximate.
	 * @param length
	 * @throws IOException
	 */
	public final void jsFunction_read (int length) throws IOException
	{
		// This assumes that there was an error in the constructor and so nothing will be loadable.
		// Also, if the file is not opened for reading this will be ignored.
		if ((file == null) || (!reading)) return;
		
		BufferedReader reader = new BufferedReader(new FileReader(file));
		
		String strBuffer = "";
		
		while (reader.ready())
		{
			strBuffer += reader.readLine() + "\r\n";
			
			if (strBuffer.length() >= length)
			{
				callFunction(onRead, new Object[] { strBuffer });
				strBuffer = "";
			}
		}
		
		reader.close();
	}
	
	public final void jsFunction_open (String fileModes) throws IOException
	{
		// This assumes that there was an error in the constructor and so nothing will be loadable.
		if (file == null) return;
		
		fileModes = fileModes.toLowerCase();
		
		// Checks if + is in fileModes, and returns TRUE if it is.
		boolean plus = (fileModes.indexOf('+') != -1);
		
		if (fileModes.indexOf('w') != -1)
		{
			reading = plus;
			writing = true;
			
			if (!file.exists())
			{
				file.createNewFile();
			}
			
			FileWriter tmpWriter = new FileWriter(file);
			tmpWriter.write(0x00);
			tmpWriter.flush();
			tmpWriter.close();
		}
		else if (fileModes.indexOf('r') != -1)
		{
			reading = true;
			writing = plus;
			
			if (!file.exists())
			{
				file.createNewFile();
			}
		}
		else if (fileModes.indexOf('a') != -1)
		{
			writing = true;
			reading = plus;
			
			if (!file.exists())
			{
				file.createNewFile();
			}
			
			writer = new FileWriter(file, true);
		}
		else if (fileModes.indexOf('x') != -1)
		{
			if (file.exists())
			{
				throw new IOException("Warning: File already exists, unable to use open mode 'x'");
			}
			
			file.createNewFile();
			
			writing = true;
			reading = plus;
		}
	}
}
