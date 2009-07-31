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
package org.rhinobot.bot;

import java.io.IOException;

public interface BotSocket
{
	/**
	 * Gets the classname for the botsocket, cannot be null.
	 * @return
	 */
	String getClassName ();
	
	/**
	 * The config the socket may require, e.g. ssl config settings, proxy settings, etc.
	 * @return NULL or '' if no config.
	 */
	String getRequiredConfig ();
	
	/**
	 * Opens the socket
	 * @param address
	 * @param port
	 * @param charset
	 * @throws Exception
	 */
	void open (final String address, final int port, final String charset) throws Exception;
	
	/**
	 * Checks if the socket is connected
	 * @return
	 */
	boolean connected ();
	
	/**
	 * Reads a line from the socket
	 * @return
	 * @throws IOException
	 */
	String readLine () throws IOException;
	
	/**
	 * Writes a line to the socket.
	 * @param line
	 */
	void writeLine (final String line);
	
	/**
	 * Closes the socket
	 * @throws IOException
	 */
	void close () throws IOException;
}
