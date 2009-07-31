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
package org.rhinobot.test;

import org.rhinobot.lib.StringUtils;

public class WildcardTester
{

	/**
	 * @param args
	 */
	public static void main (String[] args)
	{
		String[] wildcards = new String[] {
				"*",
				"t*",
				"*t",
				"ban*",
				"*o*e***h*!*.\\*net",
				"ban\\?",
				"*Mage*!*@Marshmellow",
				"*!*@DBD8169F.1F61C1EF.C11D7E5.IP",
				"*!*m@*.D5DFF4DA.1379AF57.IP",
				"*!*@ofd00m.net",
				"*!*@ZiRC-47D3AA04.users.cubicnet.net",
				"*!*goodmaneu@*.C05FD972.20429195.IP",
		};
		
		String[] strings = new String[] {
				"something",
				"something",
				"something",
				"banana",
				"something!anything@something.*net",
				"bant",
				"Mage!Mage@Marshmellow",
				"Bob!Selam@DBD8169F.1F61C1EF.C11D7E5.IP",
				"LOL!mm@asdf8.D5DFF4DA.1379AF57.IP",
				"*!*@ofd00m.net",
				"asdf!%521?*@ZiRC-47D3AA04.users.cubicnet.net",
				"2348a9ds87!asd98f7a90s8dc7a098sd7c8s8a9x98goodmaneu@asdca79s0d8c78ds89.C05FD972.20429195.IP",
		};
		
		boolean found = false;
		
		for (int i = 0; i < wildcards.length; i++)
		{
			System.out.println("Test Number " + i);
			System.out.println("Comparing   " + wildcards[i] + " to " + strings[i]);
			found = StringUtils.wildCardMatch(wildcards[i], strings[i]);
			if (found)
				System.out.println("Match!");
			else
				System.out.println("Failure!");
		}
	}

}
