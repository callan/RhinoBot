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
 * This interface is meant for modules.
 * 
 * @author Chris
 * @version 1.0.3
 */
public interface Module
{
	/**
	 * This is the preferred parameter
	 * @param kernelInstance
	 */
	void init ();
	
	/**
	 * Tells the module to start a shutdown process of itself immediately
	 */
	void panic ();
	
	/**
	 * Tells the module to gracefully shut down
	 */
	void quit ();
	
	/**
	 * Gets the class name of the module.
	 * @return module name
	 */
	String getModuleName () throws NullPointerException;
	
	/**
	 * Returns the module version.
	 * @return module version
	 */
	String getVersion () throws NullPointerException;
	
	/**
	 * Returns the dependencies of this module (java or otherwise)
	 * @return
	 */
	String[] getDependencies ();
	
	/**
	 * This is what replaces kernel-driven event system that I
	 * originally decided to put in place. This allows the transfer
	 * of data (being anything but an instance of Module) to and from
	 * the two applications.
	 * 
	 * Modules use Kernel.sendData to send data to other modules, and
	 * those modules get it picked up with this method.
	 * 
	 * Since this allows you to generate your own event system, and
	 * much more it is definately the good choice to use for
	 * multiple modules.
	 */
	<F> void dataTransferEvent (final Kernel kernel, final String from, final F data);
}
