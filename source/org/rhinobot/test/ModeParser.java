/**
 * ModeParser.java
 * RhinoBotv2
 * @package org.rhinobot.test
 */
package org.rhinobot.test;

import java.util.ArrayList;

/**
 * @author Chris
 *
 */
public class ModeParser
{
	private class Mode
	{
		private boolean plus = true;
		private char modeChar;
		private String parameter;
		
		public Mode (char mode, String param, boolean plusMode)
		{
			modeChar  = mode;
			parameter = param;
			plus      = plusMode;
		}
		
		public char getMode ()
		{
			return modeChar;
		}
		
		public String getParameter ()
		{
			return parameter;
		}
		
		public boolean isPlusMode ()
		{
			return plus;
		}
		
		public void setParameter (String param)
		{
			parameter = param;
		}
	}
	
	/**
	 * @param args
	 */
	public static void main (String[] args)
	{
		String[] testModes = new String[] {
			"-v Chris",
			"+v Chris",
			"+v-o+a-z Chris Chris Special",
			"+va-oz Chris Special Chris",
//			"+vztf Mage [5j#R,20m#M,6n,5t#b]:3",
//			"z",
//			"xdffffffffffffffffff",
//			"+--+_=_--"
		};
		
		ModeParser parser = new ModeParser();
		
		for (String testMode : testModes)
		{
			System.out.println("Running test on " + testMode);
			parser.debug(testMode);
		}
	}

	public ArrayList<Mode> parseModes (String rawModes)
	{
		int buffer	= 0,
			pos		= 0,
			ppos	= 1,
			length	= rawModes.length();
		
		char	chr		= rawModes.charAt(buffer);
		
		// This will be dynamic
		char[]	charsWithParams	= new char[] {
			'v', 'h', 'o',
			'a', 'q', 's',
			'r', 'b', 'e',
			'I', 'k', 'f',
			'L', 'l', 'j'
		};
		
		boolean		plus    = false;
		
		String[]	params	= null;
		
		if (rawModes.indexOf(' ') != -1)
		{
			params = rawModes.split(" ");
		}
		
		ArrayList<Mode> modes  = new ArrayList<Mode>();
		
		plus = (chr != '-');
		
		for (;;)
		{
			if ((chr == '-') || (chr == '+'))
			{
				for (int i = pos + 1; i < buffer; i++)
				{
					char x = rawModes.charAt(i);
					
					if ((inCharArray(charsWithParams, x)) && (params != null))
					{
						modes.add(new Mode(x, params[ppos++], plus));
					}
					else
					{
						modes.add(new Mode(x, "", plus));
					}
				}
				
				pos  = buffer;
				plus = (chr == '+');
			}
			
			if ((chr == ' ') || ((buffer + 1) >= length))
			{
				for (int i = pos + 1; i < buffer; i++)
				{
					char x = rawModes.charAt(i);
					
					if ((inCharArray(charsWithParams, x)) && (params != null))
					{
						modes.add(new Mode(x, params[ppos++], plus));
					}
					else
					{
						modes.add(new Mode(x, "", plus));
					}
				}
				break;
			}
			
			chr = rawModes.charAt( ++buffer );
		}
		
		return modes;
	}
	
	private void debug (String rawModes)
	{
		// This will be dynamic
		char[]	charsWithParams	= new char[] {
			'v', 'h', 'o',
			'a', 'q', 's',
			'r', 'b', 'e',
			'I', 'k', 'f',
			'L', 'l', 'j'
		};
		
		ArrayList<Mode> modes = parseModes(rawModes);
		
		for (Mode mode : modes)
		{
			if (mode.isPlusMode())
				System.out.println("Plus Mode: " + mode.getMode());
			else
				System.out.println("Minus Mode: " + mode.getMode());
			
			if (!mode.getParameter().equals(""))
			{
				System.out.println("Parameter: " + mode.getParameter());
			}
			else
			{
				if (inCharArray(charsWithParams, mode.getMode()))
				{
					System.out.println("Unable to find parameter");
				}
				else
				{
					System.out.println("No parameter");
				}
			}
		}
	}
	
	private boolean inCharArray (char[] array, char searchChar)
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
}
