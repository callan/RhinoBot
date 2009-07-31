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
package org.geartech.rhinobot.rhino;


/**
 * This is the script class which represents each script to be ran
 * as a cron job. This allows the CronThread to have a much easier
 * time delivering quick results to a cron.
 */
public final class CronScript extends RhinoScript
{
	/**
	 * Serial ID
	 */
	private static final long	serialVersionUID	= 4930556549116004841L;

	/**
	 * Time to start
	 */
	private long 	time;
	
	/**
	 * Inteveral between running. This is used for scheduled crons
	 */
	private long 	interval = -1;
	
	/**
	 * The actual script
	 */
	private RhinoScript script;
	
	/**
	 * Tells whether this is scheduled or not
	 */
	private boolean	scheduled = false;
	
	/**
	 * Constructor for when a script is called without a
	 * RhinoScript instance. This will make the RhinoScript
	 * instance of it and pass it on to the other constructor
	 * 
	 * @param time
	 * @param script
	 */
	public CronScript (final long time, final String script)
	{
		this(time, new RhinoScript(script, "Cron-" + Long.toString(time), Rhino.compileScript(script)));
	}
	
	/**
	 * Constructor for when a script is called without a
	 * RhinoScript instance. This will make the RhinoScript
	 * instance of it and pass it on to the other constructor
	 * 
	 * @param time
	 * @param script
	 * @param interval
	 */
	public CronScript (final long time, final String script, final long interval)
	{
		this(time, new RhinoScript(script, "Cron-" + Long.toString(time), Rhino.compileScript(script)), interval);
	}
	
	/**
	 * Constructor for a runOnce cron. This uses the RhinoScript as the script
	 * @param time
	 * @param script
	 */
	public CronScript (final long time, final RhinoScript script)
	{
		this(time, script, -1);
	}
	
	/**
	 * Constructor for a scheduled cron
	 * @param time
	 * @param script
	 * @param interval
	 */
	public CronScript (final long time, final RhinoScript script, final long interval)
	{
		super(script.getEventName(), script.getSource(), script.getScript());
		
		this.time      = time;
		this.script    = script;
		
		if (interval != -1)
		{		
			this.interval  = interval;
			this.scheduled = true;
		}
	}
	
	/**
	 * Checks to see if this cron is ready to run, within a 4 second difference
	 * of the actual cron job
	 * 
	 * @param currentTime
	 * @return
	 */
	public final boolean isReady (final long currentTime)
	{
		return ( (currentTime + 2 > time) && (currentTime - 2 < time) );
	}
	
	/**
	 * Executes this cron
	 *
	 */
	final void execute ()
	{
		Rhino.getInstance().runCronScript(this);
	}
	
	/**
	 * Stops this cron
	 *
	 */
	final void stop ()
	{
		time   = -1;
		script = null;
	}
	
	/**
	 * Reschedules this cron
	 * @param newTime
	 */
	final void reschedule (long newTime)
	{
		time = (newTime + interval);
	}
	
	/**
	 * Checks to see if this cron is scheduled
	 * @return
	 */
	public final boolean isScheduled ()
	{
		return scheduled;
	}
	
	/**
	 * Gets the interval
	 * @return
	 */
	public final long getInterval ()
	{
		return interval;
	}
	
	/**
	 * Gets the time where this script should run (exactly)
	 * @return
	 */
	public final long getRunTime ()
	{
		return time;
	}
	
	/**
	 * Returns the script
	 * @return
	 */
	public final RhinoScript getRhinoScript ()
	{
		return script;
	}
}
