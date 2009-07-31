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
package org.geartech.rhinobot.manager;

import java.util.ArrayList;

import org.geartech.rhinobot.support.StringUtils;


/**
 * Since many IRCD's implement different list types for modes (almost all has the +b list, unreal has +e,
 * and InspIRCd is infinitely customizable for these), I decided rather than hard-coding lists in Channel
 * to implement a special list which handles their char mode as well as what's inside them using a new Mask
 * class.
 */
public final class ModeList
{
	private ArrayList<Mask>	list		= new ArrayList<Mask>();
	private char 			modeChar;
	
	/**
	 * Constructor with no start entry
	 * @param mode
	 */
	public ModeList (char mode)
	{
		modeChar = mode;
	}
	
	/**
	 * Constructor with a Mask start entry
	 * @param mode
	 * @param startList
	 */
	public ModeList (char mode, Mask startList)
	{
		list.add(startList);
		modeChar = mode;
	}
	
	/**
	 * Constructor with a Mask start entry
	 * @param mode
	 * @param startList
	 */
	public ModeList (char mode, String startList)
	{
		list.add(new Mask(startList));
		modeChar = mode;
	}
	
	/**
	 * Constructor with an ArrayList&lt;Mask&gt; start Entry
	 * @param mode
	 * @param startList
	 */
	public ModeList (char mode, ArrayList<Mask> startList)
	{
		if (startList.size() == 0)
			return;

		list = startList;
		modeChar = mode;
	}
	
	/**
	 * Adds an entry to the list
	 * @param entry
	 */
	public final void addToList (Mask entry)
	{
		list.add(entry);
	}
	
	/**
	 * Adds an entry to the list
	 * @param entry
	 */
	public final void addToList (String entry)
	{
		list.add(new Mask(entry));
	}
	
	/**
	 * Removes an entry from the list
	 * @param entry
	 * @return
	 */
	public final boolean removeFromList (Mask entry)
	{
		return list.remove(entry);
	}
	
	/**
	 * Removes an entry from the list
	 * @param entry
	 * @return
	 */
	public final boolean removeFromList (String entry)
	{
		return list.remove(new Mask(entry));
	}
	
	/**
	 * Gets the mode character
	 * @return
	 */
	public final char getModeChar ()
	{
		return modeChar;
	}
	
	/**
	 * Gets the mode character as a string
	 * @return
	 */
	public final String getModeCharAsString ()
	{
		return String.valueOf(modeChar);
	}
	
	/**
	 * Gets the list
	 * @return
	 */
	public final String[] getList ()
	{
		String[] strList = new String[list.size()];
		
		for (int i = 0; i < strList.length; i++)
		{
			Mask mask = list.get(i);
			
			strList[i] = mask.toString();
		}
		
		return strList;
	}
	
	/**
	 * Clears the lsit
	 */
	public final void clear ()
	{
		list.clear();
	}
	
	/**
	 * Attempts to find an entry within the list using wildcards
	 * @param needle
	 * @return
	 */
	public final boolean find (String needle)
	{
		// If needle doesn't look like a mask, assume it's just the nick :>
		if ( (needle.indexOf('!') == -1) && (needle.indexOf('@') == -1) )
		{
			needle += "!*@*";
		}
		
		for (Mask entry : list)
		{
			if (StringUtils.wildCardMatch(needle, entry.toString()))
			{
				return true;
			}
		}
		
		return false;
	}
}
