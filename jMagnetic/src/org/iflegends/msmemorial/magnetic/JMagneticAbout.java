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
package org.iflegends.msmemorial.magnetic;

import java.awt.*;
import java.awt.event.*;
import java.beans.ExceptionListener;
 
import javax.swing.*;
import javax.swing.event.*;
import javax.swing.text.html.*;


/**
* This code was edited or generated using CloudGarden's Jigloo
* SWT/Swing GUI Builder, which is free for non-commercial
* use. If Jigloo is being used commercially (ie, by a corporation,
* company or business for any purpose whatever) then you
* should purchase a license for each developer using Jigloo.
* Please visit www.cloudgarden.com for details.
* Use of Jigloo implies acceptance of these licensing terms.
* A COMMERCIAL LICENSE HAS NOT BEEN PURCHASED FOR
* THIS MACHINE, SO JIGLOO OR THIS CODE CANNOT BE USED
* LEGALLY FOR ANY CORPORATE OR COMMERCIAL PURPOSE.
*/
public class JMagneticAbout  {
	  private JEditorPane jep;
	  private JScrollPane jScrollPane_IL;
	  private ExceptionListener listener;
	  private JFrame dialog;
	  public JMagneticAbout(ExceptionListener listener, Dimension d)
	  {
	    dialog=new JFrame();
	    this.listener=listener;
	    jep=new JEditorPane();
	    jep.setSize(d);
		Toolkit toolkit = Toolkit.getDefaultToolkit();
		Dimension scrnsize = toolkit.getScreenSize();
		int locX = (((int)scrnsize.getWidth())-d.width)/2;
		int locY = (((int)scrnsize.getHeight())-d.height)/2;
		dialog.setLocation(locX,locY);
		dialog.setTitle("About JMagnetic 2");
	    
	    jep.setEditable(false);
	    
	    dialog.setSize(d);
	    dialog.getContentPane().setLayout(new BorderLayout());
	    dialog.setResizable(false);
		{
			jScrollPane_IL = new JScrollPane(jep);
			dialog.getContentPane().add(jScrollPane_IL, BorderLayout.CENTER);
			jScrollPane_IL.setAutoscrolls(true);
		}
	    jep.addHyperlinkListener(new Hyperactive());
	  }
	  public void showInfo()
	  {
	  try{
	  	jep.setPage(getClass()
				.getClassLoader().getResource(
						"Ressources/jmagneticinfo.html"));
	    dialog.show();
	  }
	  catch (Exception e)
	  {
	  	if (listener!=null)
	  		listener.exceptionThrown(e);
	  	else{
	  		e.printStackTrace();
	  	}	    
	  }
	  }
	  
    class Hyperactive implements HyperlinkListener {
       public void hyperlinkUpdate(HyperlinkEvent e) {
          if (e.getEventType() == HyperlinkEvent.EventType.ACTIVATED) {
		      JEditorPane pane = (JEditorPane) e.getSource();
	      if (e instanceof HTMLFrameHyperlinkEvent) {
		          HTMLFrameHyperlinkEvent  evt = (HTMLFrameHyperlinkEvent)e;
		          HTMLDocument doc = (HTMLDocument)pane.getDocument();
		          doc.processHTMLFrameHyperlinkEvent(evt);
		      } else {
		          try {
		          		pane.setPage(e.getURL());
		          } catch (Exception t) {
		          		listener.exceptionThrown(t);
		          }
		      }
          }
	      }
    }
 
}