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

/**
 * 
 */
public class SecureSocket implements SocketDriver
{
	
	/* (non-Javadoc)
	 * @see org.geartech.rhinobot.drivers.SocketDriver#close()
	 */
	@Override
	public void close () throws Exception
	{
		// TODO Auto-generated method stub
		
	}
	
	/* (non-Javadoc)
	 * @see org.geartech.rhinobot.drivers.SocketDriver#connected()
	 */
	@Override
	public boolean connected ()
	{
		// TODO Auto-generated method stub
		return false;
	}
	
	/* (non-Javadoc)
	 * @see org.geartech.rhinobot.drivers.SocketDriver#open(java.lang.String, int, java.lang.String)
	 */
	@Override
	public void open (String address, int port, String charset) throws Exception
	{
		// TODO Auto-generated method stub
		
	}
	
	/* (non-Javadoc)
	 * @see org.geartech.rhinobot.drivers.SocketDriver#readLine()
	 */
	@Override
	public String readLine () throws Exception
	{
		// TODO Auto-generated method stub
		return null;
	}
	
	/* (non-Javadoc)
	 * @see org.geartech.rhinobot.drivers.SocketDriver#writeLine(java.lang.String)
	 */
	@Override
	public void writeLine (String line)
	{
		// TODO Auto-generated method stub
		
	}
	
}
