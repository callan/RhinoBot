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
package org.kernel;


/**
 * @author Chris
 *
 */
public final class Panic extends Exception
{
	private Throwable e    = null;
	private Module module  = null;
	private String libName = null;
	
	public Panic (final Module module, final String errorDescription)
	{
		super("Module " + module.getModuleName() + " has caused a kernel panic: " + 
				errorDescription);
		
		this.module = module;
	}
	
	public Panic (final Module module, final String errorDescription, final Throwable e)
	{
		super("Module " + module.getModuleName() + " has caused a kernel panic: " + 
				errorDescription);
		
		this.e = e;
		this.module = module;
	}
	
	public Panic (final String libName, final String errorDescription)
	{
		super("Library " + libName + " has caused a kernel panic: " + 
				errorDescription);
		
		this.libName = libName;
	}
	
	public Panic (final String libName, final String errorDescription, final Throwable e)
	{
		super("Library " + libName + " has caused a kernel panic: " + 
				errorDescription);
		
		this.e = e;
		this.libName = libName;
	}
	
	public final boolean isModule ()
	{
		return (module != null);
	}
	
	public final String getLibraryName ()
	{
		return libName;
	}
	
	public final Module getModule ()
	{
		return module;
	}
	
	public final Throwable getException ()
	{
		return e;
	}
}
