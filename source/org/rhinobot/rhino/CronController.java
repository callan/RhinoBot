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

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * This is my implementation of a cron controller for JavaScript.
 */
public final class CronController
{
	/**
	 * This is thrown when a program or script attempts to register a 
	 * cron job on a time which is in the past.
	 * @author Chris
	 */
	public final class TimeAlreadyPassedException extends Exception
	{
		public TimeAlreadyPassedException ()
		{
			// Nothing!
		}
	}
	
	/**
	 * This is the thread to maintain crons
	 * @author Chris
	 */
	private final class CronThread extends Thread
	{
		/**
		 * Timer
		 * This is the ticker that moves up by one per second
		 */
		private long					timer;
		
		/**
		 * Scheduled crons. These crons will execute every certain time
		 */
		private List<CronScript>		scheduled = Collections.synchronizedList(new ArrayList<CronScript>());
		
		/**
		 * RunOnce Crons. These crons will only run once
		 */
		private List<CronScript>		runOnce   = Collections.synchronizedList(new ArrayList<CronScript>());
		
		/**
		 * Tells whether the thread is running or not
		 */
		private boolean					running   = true;
		
		/**
		 * Constructor.
		 *
		 */
		CronThread ()
		{
			setName("Cron Thread");
			setPriority(Thread.MIN_PRIORITY);
			start();
		}
		
		/**
		 * Cleans the cron jobs. This is only ran once and a while.
		 *
		 */
		private synchronized void cleanCronJobs ()
		{
			for (CronScript script : runOnce)
			{
				if ((script.isReady(0)) || (script.isReady(-1)))
				{
					runOnce.remove(script);
				}
			}
		}
		
		/**
		 * Registers a cron script
		 * @param script
		 * @throws TimeAlreadyPassedException
		 */
		void registerCron (CronScript script) throws TimeAlreadyPassedException
		{
			if (script.getRunTime() < timer)
			{
				throw new TimeAlreadyPassedException();
			}
			
			if (script.isScheduled())
			{
				scheduled.add(script);
			}
			else
			{
				runOnce.add(script);
			}
		}
		
		/**
		 * Quits the cron thread
		 *
		 */
		void quit ()
		{
			runOnce.clear();
			scheduled.clear();
			running = false;
		}
		
		/**
		 * Threading method
		 */
		public void run ()
		{
			int cleanup = 0;
			
			while (running)
			{
				try
				{
					sleep(1000);
				}
				catch (InterruptedException e)
				{
				}
				
				for (CronScript script : scheduled)
				{
					if (script.isReady(timer))
					{
						script.execute();
						script.stop();
					}
				}
				
				for (CronScript script2 : runOnce)
				{
					if (script2.isReady(timer))
					{
						script2.execute();
						script2.stop();
					}
				}
				
				if (cleanup == 360)
				{
					cleanCronJobs();
					cleanup = 0;
				}
				
				cleanup++;
				timer++;
			}
		}
	}
	
	/**
	 * Instance of the thread
	 */
	private CronThread				thread;
	
	/**
	 * Instance of the controller
	 */
	private static CronController	instance;
	
	/**
	 * Singleton
	 * @return
	 */
	public static final CronController getInstance ()
	{
		if (instance == null)
		{
			instance = new CronController();
		}
		return instance;
	}
	
	/**
	 * Disallow cloning.
	 */
	public final Object clone () throws CloneNotSupportedException
	{
		throw new CloneNotSupportedException();
	}
	
	/**
	 * Private Constructor
	 */
	private CronController ()
	{
	}
	
	/**
	 * Register a cron
	 * @param script
	 * @throws TimeAlreadyPassedException
	 */
	public final void registerCron (final CronScript script) throws TimeAlreadyPassedException
	{
		thread.registerCron(script);
	}
	
	/**
	 * Start the cron thread
	 *
	 */
	final void start ()
	{
		if (thread == null)
		{
			thread = new CronThread();
		}
	}
	
	/**
	 * Stop the cron thread
	 *
	 */
	final void quit ()
	{
		if (thread != null)
		{
			thread.quit();
		}
	}
}
