//
//   ID3 editing tools for use with iTunes Libraries.
//
//   https://github.com/Apophenic
//   
//   Copyright (c) 2015 Justin Dayer (jdayer9@gmail.com)
//   
//   Permission is hereby granted, free of charge, to any person obtaining a copy
//   of this software and associated documentation files (the "Software"), to deal
//   in the Software without restriction, including without limitation the rights
//   to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
//   copies of the Software, and to permit persons to whom the Software is
//   furnished to do so, subject to the following conditions:
//   
//   The above copyright notice and this permission notice shall be included in
//   all copies or substantial portions of the Software.
//   
//   THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
//   IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
//   FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
//   AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
//   LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
//   OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
//   THE SOFTWARE.

package id3.main;

import java.awt.EventQueue;
import java.util.logging.Level;
import java.util.logging.Logger;

/** iD3 Application entry point.
 * <p>
 * Recommended reading:
 * <li>{@link FunctionPanel}
 * <li>{@link Library}
 * <li>{@link GUI}
 * <p>
 * See the ReadMe.txt for more information.
 */
public final class Program
{
	/** This logger instance is used by all classes */
	public static final Logger LOG = Logger.getGlobal();
	
	protected Program() {}
	
	public static void main(String[] args)
	{
		LOG.setLevel(Level.OFF); // See also: Settings.isDebugMode
		
		init();
		
		Settings.load();
	}
	
	public static void init()
	{
		EventQueue.invokeLater(new Runnable()
		{
			public void run()
			{
				try
				{
					GUI window = new GUI();
					window.frame.setVisible(true);
				}
				catch (Exception e)
				{
					LOG.log(Level.SEVERE, "Failed to initialize iD3");
				}
			}
		});
	}
}
