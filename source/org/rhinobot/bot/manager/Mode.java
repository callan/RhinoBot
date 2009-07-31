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
package org.rhinobot.bot.manager;


/**
 * This class represents one mode character in a mode string.
 */
public final class Mode
{
	private boolean plus		= true;
	private char	modeChar;
	private String	parameter	= null;
	
	/**
	 * Constructor
	 * @param mode
	 * @param param
	 * @param plusMode
	 */
	public Mode (final char mode, final String param, final boolean plusMode)
	{
		modeChar  = mode;
		parameter = param;
		plus      = plusMode;
	}

	/**
	 * Returns the mode as a string rather than a character.
	 * 
	 * @return
	 */
	public final String getModeAsString ()
	{
		return String.valueOf(modeChar);
	}
	
	/**
	 * Returns the mode as a character
	 * 
	 * @return
	 */
	public final char getMode ()
	{
		return modeChar;
	}
	
	/**
	 * Checks if the parameter is valid
	 * 
	 * @return TRUE if the parameter exists
	 */
	public final boolean hasParameter ()
	{
		return ((parameter != null) && (parameter.length() != 0));
	}
	
	/**
	 * Gets the parameter
	 * 
	 * @return
	 */
	public final String getParameter ()
	{
		return parameter;
	}
	
	/**
	 * Checks if the mode is a plus mode (+m, for example)
	 * 
	 * @return
	 */
	public final boolean isPlusMode ()
	{
		return plus;
	}
}
