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
package org.rhinobot.lib;

public final class StringUtils
{
	private StringUtils ()
	{
		
	}
	
	/**
	 * Adds slashes to a string
	 * 
	 * @param string
	 * @return
	 */
	public static final String addSlashes(String string)
	{
		if (string != null)
		{
			string = string.replace("\\", "\\\\");
			string = string.replace("'", "\\'");
			string = string.replace("\"", "\\\"");
		}
		return string;
	}
	
	/**
	 * Strips a string of its slashes
	 * 
	 * @param string
	 * @return
	 */
	public static final String stripSlashes(String string)
	{
		if (string != null)
		{
			string = string.replace("\\\\", "\\");
			string = string.replace("\\'", "'");
			string = string.replace("\\\"", "\"");
		}
		return string;
	}
	
	/**
	 * Searches a char array for a specific char
	 * 
	 * @param array
	 * @param searchChar
	 * @return TRUE if searchChar is array, or FALSE if it isn't
	 */
	public static final boolean inCharArray (final char[] array, final char searchChar)
	{
		for (char character : array)
		{
			if (searchChar == character)
			{
				return true;
			}
		}
		return false;
	}
	
//	private static final boolean DEBUG = false;
	
	/**
	 * Checks to see if needle matches haystack, where needle uses special characters known as wildcards.
	 * Asterisks (*) and Question marks (?) have special meaning in wildcard-based strings: Asterisks are
	 * meant to represent every character for any amount of length, even zero. Question marks represent every
	 * character for only the length of one. That is to say:<br>
	 * <br>
	 * If you had <tt>*</tt> it would match everything, no matter what.<br>
	 * If you had <tt>hel??</tt> or <tt>hel*</tt> it would match "<tt>hello</tt>", as well as "<tt>help!</tt>", though the latter
	 * would match "<tt>hello there how are you doing?</tt>" and the former would not.<br>
	 * <br>
	 * @param needle
	 * @param haystack
	 * @return TRUE if the needle matches the haystack, FALSE if otherwise.
	 */
	public static final boolean wildCardMatch (final String needle, final String haystack)
	{
		char[]	wildcard = needle.toCharArray(),
				string   = haystack.toCharArray();
		
		int str = 0, wil = 0;
		
//		if (DEBUG)
//		{
//			System.out.println("\r\n");
//			System.out.println("Wildcard: " + needle);
//			System.out.println("String:   " + haystack);
//		}
		
		for (; wil < wildcard.length; wil++)
		{
			char wild = wildcard[wil];
			
//			if (DEBUG)
//			{
//				System.out.println("Wildcard char: " + wild);
//				System.out.println("String char:   " + string[str]);
//			}
			
			if (wild == '\\')
			{
//				if (DEBUG)
//					System.out.println("Encountered Escape character, skipping next char...");
				
				wil++;
				
				if (wildcard[wil] != string[str])
					return false;
				
				str++;
			}
			else if (wild == '?')
			{
				str++;
			}
			else if (wild == '*')
			{
//				if (DEBUG)
//					System.out.println("Wildcard found");
				
				for (; ((wil + 1) < wildcard.length && (wildcard[wil+1] == '*' || wildcard[wil+1] == '?')); wil++);
				
				if ( (wil + 1) < wildcard.length )
				{
					if ((str == 0) && (string[str] == wildcard[wil + 1]))
					{
						str++;
						wil++;
						continue;
					}
					
					str++;
					
//					if (DEBUG)
//						System.out.print("Cycling through characters until we find '" + wildcard[wil+1] + "'... ");
					
//					if (DEBUG)
//					{
//						while ( (string[str++] != wildcard[wil+1]) && (str < string.length) )
//						{
//							System.out.print(string[str]);
//						}
//					}
//					else
//					{
						while ( (string[str++] != wildcard[wil+1]) && (str < string.length) );
//					}

//					if (DEBUG)
//						System.out.println();
					
					if (str == string.length)
					{
//						if (DEBUG)
//							System.out.println("str == string.length, therefore breaking out to TRUE");
						break;
					}
					
					wil++;
					str--;
				}
				else
				{
					// The wildcard is at the end of the string, therefore we just have to say everything matched ;) ;)
					str = string.length;
					break;
				}
				str++;
			}
			else
			{
				if (wild != string[str])
					return false;
				
				str++;
			}
		}
		
		if (str != string.length)
			return false;
		
		return true;
	}
}
