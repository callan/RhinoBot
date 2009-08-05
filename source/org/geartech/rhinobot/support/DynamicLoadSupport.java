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
package org.geartech.rhinobot.support;

import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLClassLoader;

/**
 * 
 */
public class DynamicLoadSupport
{	
	private URLClassLoader _loader;
	
	private File[] _directories;
	
	public DynamicLoadSupport (String[] directories) throws IOException
	{
		URL[] urls  = new URL[directories.length];
		
		_directories = new File[directories.length];
		
		for (int i = 0; i != directories.length; i++)
		{
			if (directories[i] != null)
			{			
				_directories[i] = new File(directories[i]);
				urls[i] = _directories[i].toURI().toURL();
			}
		}
		
		_loader = new URLClassLoader(urls);
	}
	
	public Object clone () throws CloneNotSupportedException
	{
		throw new CloneNotSupportedException();
	}
	
	/**
	 * Dynamically loads a Class into the runtime.
	 * 
	 * @param className name of the class
	 * @return Class
	 * @throws ClassNotFoundException
	 */
	@SuppressWarnings("unchecked")
	public Class findClass (String className) throws ClassNotFoundException
	{
		if (className.lastIndexOf('.') != -1)
		{
			return _loader.loadClass(className);
		}
		
		File   found    = null;
		String filename = className + ".class";
		
		for (File directory : _directories)
		{
			File[] files = directory.listFiles();
			
			for (File file : files)
			{
				if (!file.isDirectory() && file.getName().equals(filename))
				{
					found = file;
					break;
				}
			}
		}
		
		if (found != null)
			return _loader.loadClass(className);

		return null;
	}
}
