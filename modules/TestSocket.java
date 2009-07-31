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
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.InetAddress;
import java.net.Socket;

import org.rhinobot.bot.BotSocket;

public class TestSocket implements BotSocket
{
	private PrintWriter		writer;
	private BufferedReader	reader;
	private Socket			socket;
	
	/**
	 * Overidden
	 * @see org.rhinobot.bot.BotSocket#getClassName()
	 */
	public String getClassName ()
	{
		return "TestSocket";
	}
	
	/**
	 * Overidden
	 * @see org.rhinobot.bot.BotSocket#getRawWriter()
	 */
	public PrintWriter getRawWriter ()
	{
		return writer;
	}

	/**
	 * Overidden
	 * @see org.rhinobot.bot.BotSocket#getRawReader()
	 */
	public BufferedReader getRawReader ()
	{
		return reader;
	}

	/**
	 * Overidden
	 * @see org.rhinobot.bot.BotSocket#getRequiredConfig()
	 */
	public String getRequiredConfig ()
	{
		return null;
	}

	/**
	 * Overidden
	 * @see org.rhinobot.bot.BotSocket#open(java.net.InetAddress, int, java.lang.String)
	 */
	public void open (InetAddress address, int port, String charset) throws Exception
	{
		socket = new Socket(address, port);
		
		reader = new BufferedReader(new InputStreamReader(socket.getInputStream(), charset));
		writer = new PrintWriter(new OutputStreamWriter(socket.getOutputStream(), charset), true);
	}

	/**
	 * Overidden
	 * @see org.rhinobot.bot.BotSocket#open(java.lang.String, int, java.lang.String)
	 */
	public void open (String address, int port, String charset) throws Exception
	{
		open(InetAddress.getByName(address), port, charset);
	}

	/**
	 * Overidden
	 * @see org.rhinobot.bot.BotSocket#open(java.net.InetAddress, int)
	 */
	public void open (InetAddress address, int port) throws Exception
	{
		open(address, port, "ISO-8859-1");
	}

	/**
	 * Overidden
	 * @see org.rhinobot.bot.BotSocket#open(java.lang.String, int)
	 */
	public void open (String address, int port) throws Exception
	{
		open(InetAddress.getByName(address), port, "ISO-8859-1");
	}

	/**
	 * Overidden
	 * @see org.rhinobot.bot.BotSocket#connected()
	 */
	public boolean connected ()
	{
		return socket.isConnected();
	}

	/**
	 * Overidden
	 * @see org.rhinobot.bot.BotSocket#readLine()
	 */
	public String readLine () throws IOException
	{
		return reader.readLine();
	}
	
	/**
	 * Overidden
	 * @see org.rhinobot.bot.BotSocket#writeLine(String)
	 */
	public void writeLine (String line)
	{
		writer.println(line);
	}

	/**
	 * Overidden
	 * @see org.rhinobot.bot.BotSocket#close()
	 */
	public void close () throws IOException
	{
		socket.close();
	}
}