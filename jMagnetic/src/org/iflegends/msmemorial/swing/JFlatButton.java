/*-----------------------------------------------------------------------------
*   JMagnetic 2 
*   An interpreter for Magnetic Scrolls games
*	
*   based on Magnetic 2.3 written by Niclas Karlsson, David Kinder,
*   Stefan Meier and Paul David Doherty 
*   
*   written by Stefan Meier
*
*   This program is free software; you can redistribute it and/or modify
*   it under the terms of the GNU General Public License as published by
*   the Free Software Foundation; either version 2 of the License, or
*    (at your option) any later version.
*
*   This program is distributed in the hope that it will be useful,
*   but WITHOUT ANY WARRANTY; without even the implied warranty of
*   MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
*   GNU General Public License for more details.

*   You should have received a copy of the GNU General Public License
*   along with this program; if not, write to the Free Software
*   Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA  02111-1307  USA
*------------------------------------------------------------------------------*/

package org.iflegends.msmemorial.swing;

import java.awt.event.*;
import javax.swing.*;


/**
 * A simple extension of javax.swing.JButton that provides an Internet Explorer
 * like button. The L&F specific border is not visible until the mouse enters
 * the component, and disappears when the mouse exits.
 */
public class JFlatButton extends JButton {
	
	/**
	 * Creates a blank JFlatButton with no label or icon.
	 */
	public JFlatButton() {
		super();
		addBorderListener();
	}
	
	/**
	 * Constructs a JFlatButton from the supplied Action.
	 */
	public JFlatButton(Action a) {
		super(a);
		addBorderListener();
	}
	
	/**
	 * Creates a JFlatButton with the given icon.
	 */
	public JFlatButton(Icon icon) {
		super(icon);
		addBorderListener();
	}
	
	/**
	 * Constructs a JFlatButton with the supplied text label.
	 */
	public JFlatButton(String text) {
		super(text);
		addBorderListener();
	}
	
	/**
	 * Constructs a JFlatButton with the given text label and icon.
	 */
	public JFlatButton(String text, Icon icon) {
		super(text, icon);
		addBorderListener();
	}
	
	private void addBorderListener() {
		setBorderPainted(false);
		addMouseListener(new MouseAdapter() {
			public void mouseEntered(MouseEvent e) {
				setBorderPainted(true);
			}
			public void mouseExited(MouseEvent e) {
				setBorderPainted(false);
			}
		});
	}
}