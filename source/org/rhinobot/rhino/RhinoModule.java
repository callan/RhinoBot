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
package org.rhinobot.rhino;

import org.kernel.Kernel;
import org.kernel.Module;

public final class RhinoModule implements Module
{
	/**
	 * Overidden
	 * @see org.kernel.Module#init()
	 */
	public final void init ()
	{
		Rhino.enableRhino();
		Kernel.getInstance().<String>sendData(this, "BotModule", "HELLO");
	}

	/**
	 * Overidden
	 * @see org.kernel.Module#panic()
	 */
	public final void panic ()
	{
		quit();
	}

	/**
	 * Overidden
	 * @see org.kernel.Module#quit()
	 */
	public final void quit ()
	{
		Kernel.getInstance().<String>sendData(this, "BotModule", "STOPPING");
	}

	/**
	 * Overidden
	 * @see org.kernel.Module#getModuleName()
	 */
	public final String getModuleName () throws NullPointerException
	{
		return "RhinoModule";
	}

	/**
	 * Overidden
	 * @see org.kernel.Module#getVersion()
	 */
	public final String getVersion () throws NullPointerException
	{
		return "1.0.3";
	}

	/**
	 * Overidden
	 * @see org.kernel.Module#getDependencies()
	 */
	public final String[] getDependencies ()
	{
		return new String[] { "org.rhinobot.bot.BotModule" };
	}

	/**
	 * Overidden
	 * @see org.kernel.Module#dataTransferEvent(org.kernel.Kernel, java.lang.String, F)
	 */
	public final <F> void dataTransferEvent (final Kernel kernel, final String from, final F data)
	{
		if (from.equals("BotModule"))
		{
			String sData = (String) data;
			
			if (sData.equalsIgnoreCase("OK! ENABLING"))
			{
				if (!Rhino.isEnabled())
				{
					Rhino.enableRhino();
				}
			}
			else if (sData.equals("OK! DISABLING"))
			{
				Rhino.disableRhino();
			}
			else if (sData.equals("SHUTDOWN"))
			{
				Rhino.disableRhino();
				kernel.unloadModule(this);
			}
		}
		else if (from.equals("CronModule"))
		{
			String sData = (String) data;
			
			if (sData.equals("HELLO"))
			{
				if (Rhino.isEnabled())
				{
					kernel.<String>sendData(this, from, "ENABLED");
				}
				else
				{
					kernel.<String>sendData(this, from, "DISABLED");
				}
			}
		}
	}
}
