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
package org.geartech.rhinobot.rhino;

import java.io.Serializable;

import org.mozilla.javascript.Script;


/**
 * Each instance of this class represents a JavaScript source.
 */
public class RhinoScript implements Serializable
{
	/**
	 * MD5 Hash
	 */
	private String md5hash;
	
	/**
	 * The source
	 */
	private String source;
	
	/**
	 * The filename
	 */
	private String fileName;
	
	/**
	 * The script compiled
	 */
	private Script script;
	
	/**
	 * Constructor for auto-compiling a script
	 * @param source
	 * @param filename
	 */
	public RhinoScript (final String source, final String filename)
	{
		this(source, filename, Rhino.compileScript(filename, source));
	}
	
	/**
	 * Basic Constructor
	 * @param source
	 * @param filename
	 */
	public RhinoScript (final String source, final String filename, final Script script)
	{
		this.source = source;
		this.script = script;
		fileName    = filename;
	}
	
	/**
	 * Gets the source
	 * @return the source
	 */
	public final String getSource ()
	{
		return source;
	}
	
	/**
	 * Gets the script
	 * @return the script
	 */
	public final Script getScript ()
	{
		return script;
	}
	
	/**
	 * Gets the file name
	 * @return the filename
	 */
	public final String getEventName ()
	{
		return fileName;
	}
	
	/**
	 * Calculates the MD5 Hash of this file
	 */
	public final void calcHash ()
	{
		// TODO fix this
	}
	
	/**
	 * Returns the MD5 hash of this file.
	 * @return
	 */
	public final String getHash ()
	{
		return md5hash;
	}
}
