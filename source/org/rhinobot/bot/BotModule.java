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

import org.kernel.Kernel;
import org.kernel.Logger;
import org.kernel.Module;
import org.kernel.Logger.LogLevel;
import org.rhinobot.module.DatabaseModuleController;

public final class BotModule implements Module
{
	public static final String	version = "RhinoBot (0.0.4)";
	
	private Logger				logger;
	private Instancer			instancer;
	private static BotModule	thisInstance;
	
	/**
	 * Exits this module :D!
	 *
	 */
	public static final void quitModule ()
	{
		Kernel kernel = Kernel.getInstance();
		kernel.<String>sendData(thisInstance, "RhinoModule", "SHUTDOWN");
		kernel.<String>sendData(thisInstance, "CronModule", "SHUTDOWN");
		kernel.unloadModule(thisInstance);
	}
	
	/**
	 * Somewhat of a singleton
	 *
	 */
	public BotModule ()
	{
		if (thisInstance == null)
		{
			thisInstance = this;
		}
		else
		{
			return;
		}
	}

	/**
	 * Overidden
	 * @see org.kernel.Module#init()
	 */
	public final void init ()
	{
		DatabaseModuleController.getInstance().init();
		
		try
		{
			instancer = Instancer.getInstance();
			instancer.init();
		}
		catch (BotException e)
		{
			logger = new Logger("logs", "BotModule");
			logger.write(LogLevel.MAJOR, e.getMessage());
			logger.close();
			
			instancer.quitAll("Bot Exception: " + e.getMessage());
			
			if (instancer.botCount() == 0)
			{
				quitModule();
			}
		}
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
		if (instancer != null)
		{
			instancer.quitAll("Shutdown initiated by Kernel");
		}
		
		instancer = null;
	}

	/**
	 * Overidden
	 * @see org.kernel.Module#getModuleName()
	 */
	public final String getModuleName () throws NullPointerException
	{
		return "BotModule";
	}

	/**
	 * Overidden
	 * @see org.kernel.Module#getVersion()
	 */
	public final String getVersion () throws NullPointerException
	{
		return "1.0.0";
	}

	/**
	 * Overidden
	 * @see org.kernel.Module#getDependencies()
	 */
	public final String[] getDependencies ()
	{
		return null;
	}

	/**
	 * Overidden
	 * @see org.kernel.Module#dataTransferEvent(org.kernel.Kernel, java.lang.String, F)
	 */
	public final <F> void dataTransferEvent (final Kernel kernel, final String from, final F data)
	{
		if (from.equals("RhinoModule"))
		{
			if (data instanceof String)
			{
				String sData = (String) data;
				if (sData.equals("HELLO"))
				{
					RhinoBot.enableRhino();
					kernel.<String>sendData(this, from, "OK! ENABLING");
				}
				else if (sData.equals("STOPPING"))
				{
					RhinoBot.disableRhino();
					kernel.<String>sendData(this, from, "OK! DISABLING");
				}
			}
		}
	}

}
