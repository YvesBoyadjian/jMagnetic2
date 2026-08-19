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

import java.io.*;
import java.lang.reflect.InvocationTargetException;
import java.util.Enumeration;
import java.util.Vector;

import javax.sound.midi.InvalidMidiDataException;
import javax.sound.midi.MidiDevice;
import javax.sound.midi.MidiSystem;
import javax.sound.midi.MidiUnavailableException;
import javax.sound.midi.Receiver;
import javax.sound.midi.Sequence;
import javax.sound.midi.Sequencer;
import javax.sound.midi.Transmitter;
import javax.swing.*;

import java.awt.FontMetrics;
import java.beans.XMLEncoder;

import org.iflegends.msmemorial.swing.*;
import org.jconfig.Configuration;
import org.jconfig.ConfigurationManager;
import org.jconfig.ConfigurationManagerException;

public class JMagneticSwing extends JMagneticCore {

  private static final int buf_size = 1024;
  private char[]	buffer = new char[buf_size];
  private char[]    filename = new char[128];
  private byte      xpos=0,bufpos=0,ms_gfx_enabled,log_on=0, stat_count=0;
  private static char[]  buf = new char[256];
  private static short   pos=0;
  private RandomAccessFile log2=null;
  private RandomAccessFile log=null;
  private byte      sc_width=74;
  private PipedReader kbIn;
  private JMagnetic2 guiFrame;
  private int JMAppletMode = 0;
  private String accesspath;
  private boolean tsWait = false;
  private String presetFilename = null;
  private boolean isAnim=false;
  private boolean statusScore = false;
  private boolean autographics = true;
  private boolean saverequest = false;
  private boolean useHintWindow = true;
  private boolean lastSaveDir = false;
  private String sndDevice;
  private static final ConfigurationManager cm = ConfigurationManager.getInstance();
  private String gameTag;
  private static int dmpcount = 0;
  public Sequencer sequencer = null;
private boolean saverequest2;

  public JMagneticSwing( JMagnetic2 parent, String gameTag) throws IOException {
	  super("MagneticCore");
     this.guiFrame = parent;                        
     //this.textOut = textOutput;
     //this.statusOut = statusLine;
     this.gameTag = gameTag;
     this.kbIn = new PipedReader( parent.getKBOut());
  }
  
  public byte getGfxEnabled()
  {
     return ms_gfx_enabled;
  }
  
  public void setGfxEnabled(byte val)
  {
     ms_gfx_enabled = val;
  }
  
  public RandomAccessFile getLog()
  {
     return log;
  }
  
  public void setLog(RandomAccessFile raf)
  {
     log = raf;
  }
  
  public RandomAccessFile getLog2()
  {
     return log2;
  }
  
  public void setLog2(RandomAccessFile raf)
  {
     log2 = raf;
  }
  
  public byte getLogOn()
  {
     return log_on;
  }
  
  public void setLogOn(byte val)
  {
     log_on = val;
  }

  public void ms_fatal( String txt) {
    try
	{
		guiFrame.appendGameOutput( "[" + txt + "**]\n\n" );
	}
	catch (Exception e)
	{
	}
	  running=0;
  }  

  public void ms_showpic(int c,byte mode) {
    int  iPictureOff = 0;
	  int   i,j;

    if (mode != 0/*  && gfx_buf != null*/) {
      if ( (version == 4) || ((version < 4) && (c != guiFrame.getPicIndex()))) {
        iPictureOff = ms_extract(c);
        
        if (version == 4)
        	iPictureOff =0;
    
	    if (gfx_buf != null) 
		{
          guiFrame.stopAnimation();
          //System.out.println(guiFrame.getPicIndex());
          //System.out.println(c);
		  //System.out.println( iPictureOff);
          guiFrame.setPicHeight(CurrPHeight);
          guiFrame.setPicWidth(CurrPWidth);
		  //System.out.println( "Size: "+CurrPHeight+" "+CurrPWidth+"\n");
          guiFrame.setGraphic( c, gfx_buf,iPictureOff, CurrPalette);  
          
          // V2 stuff
          if ((version == 4) && (main_pic.isAnim))
          {
             guiFrame.playAnimation();
             // DEBUG
             if (waitForAnims)
             {
                
                while (guiFrame.is_ani_running())
                {
                   try
						{
							sleep(3);
						} catch (InterruptedException e)
						{
							e.printStackTrace();
						}
                }
                                      
             }

          }
		}
      }
    }
    //textOut.grabFocus();  
  }

  public byte ms_load_file( char[] name, int offset, int size) throws IOException{
	FileInputStream fh;
	final StringBuffer realname = new StringBuffer("");
	final StringBuffer initialDir = new StringBuffer();
	
	boolean hasPrivilege = false;
	if (lastSaveDir)
	{	
    Configuration  configuration =  ConfigurationManager.getConfiguration("jmagnetic2");
    String lastDir = configuration.getProperty(gameTag+".lastsave");
    if (!lastDir.equals(""))
    {
    	initialDir.append(lastDir);
	}
	}
	if ((name == null) || ((name != null) && (name[0] == '*') && (presetFilename == null))){
   		try {
			SwingUtilities.invokeAndWait(new Runnable() {
			    public void run() {
			   	  JFileChooser fc = new JFileChooser(initialDir.toString());
			      int returnVal = fc.showOpenDialog(guiFrame);
			      if (returnVal == JFileChooser.APPROVE_OPTION) {
			         File file = fc.getSelectedFile();
			         try {
						realname.append(file.getCanonicalPath() );
					} catch (IOException e) {
						e.printStackTrace();
					}
			      }
			}});
		} catch (InterruptedException e2) {
			e2.printStackTrace();
		} catch (InvocationTargetException e2) {
			e2.printStackTrace();
		}
	} else if ((name != null) && (name[0] == '*') && (presetFilename != null)) {
		realname.append(presetFilename);
	} else {
	  realname.append(name);
	}	
	
	if (realname.toString().equals(""))
		return 0;
	
	if ((fh= new FileInputStream(realname.toString())) == null) return 1;
  try {
      int i;
	    for (i=0;i<size;i++) code[offset+i] = (char)fh.read();
  }
	catch (IOException e) {
    try
	{
		guiFrame.appendEngineErrorText("\n[Loading game FAILED]\n");
	}
	catch (Exception e1)
	{
	}
	  return 1;
  }
	finally {
    fh.close();
  }      
	return 0;
  }

  
  
  public byte ms_save_file( char[] name, int offset, int size) throws IOException{
		FileOutputStream fh;
		final StringBuffer realname = new StringBuffer("");
		final StringBuffer initialDir = new StringBuffer("");
		boolean hasPrivilege = false;
	    Configuration  configuration =  ConfigurationManager.getConfiguration("jmagnetic2");
		
		if (lastSaveDir)
		{	
        String lastDir = configuration.getProperty(gameTag+".lastsave");
        if (!lastDir.equals(""))
        {
        	initialDir.append(lastDir);
		}
		}
		if ((name == null) || ((name != null) && (name[0] == '*') && (presetFilename == null))){
	   		try {
				SwingUtilities.invokeAndWait(new Runnable() {
				    public void run() {
				   	  JFileChooser fc = new JFileChooser(initialDir.toString());
				      int returnVal = fc.showSaveDialog(guiFrame);
				      if (returnVal == JFileChooser.APPROVE_OPTION) {
				         File file = fc.getSelectedFile();
				         try {
							realname.append(file.getCanonicalPath() );
						} catch (IOException e) {
							e.printStackTrace();
						}
				      }
				}});
			} catch (InterruptedException e2) {
				e2.printStackTrace();
			} catch (InvocationTargetException e2) {
				e2.printStackTrace();
			}
		} else if ((name != null) && (name[0] == '*') && (presetFilename != null)) {
			realname.append(presetFilename);
		} else {
		  realname.append(name);
		}	
		
		if (realname.toString().equals(""))
			return 0;
	if ((fh= new FileOutputStream(realname.toString())) == null) return 1;
  try {
     int i;
     final JMagnetic2 owner = this.guiFrame;
    for (i=0;i<size;i++) fh.write(code[offset+i]);
    if (lastSaveDir)
    {
    	String path;
    	File f2 = new File(realname.toString());
    	if (f2.isAbsolute())
    		path = f2.getParent();
    	else
    		path = "";
        configuration.setProperty(gameTag+".lastsave", path);
        try {
			cm.save("jmagnetic2");
		} catch (ConfigurationManagerException e) {
			e.printStackTrace();
		}
    }
    if (saverequest)
    {
	SwingUtilities.invokeLater(new Runnable() {
	    public void run() {
	       owner.shutdownMagnetic(true);   	
	}});
    }
    else if (saverequest2)
    {
		JMagneticGameStarter gs = new JMagneticGameStarter(owner);
		gs.start();
    }
  }
  catch (IOException e) {
    try
	{
		guiFrame.appendEngineErrorText("\n[Saving game FAILED]\n");
	}
	catch (Exception e1)
	{
	}
    return 1;
  }
  finally {
    fh.close();
  }
	return 0;
  }


  public void ms_statuschar(char c) {
  
   	if (c==0x09) {
          statusScore = true;
		  return;
	  }
	  if (c==0x0a) {
          stat_count=0;
		  statusScore = false;
		  return;
	  }
	  if (stat_count == 0) guiFrame.clearStatusLine();
	  if ((char)c=='@')
		  c = ' ';
	     guiFrame.statusLineAppend( statusScore, (char)c);
	  stat_count++;
      //textOut.grabFocus();
	  
  }

  public void ms_putchar(char c) {
  	buffer[bufpos++]=(char)c;
  	//if (c == 0x08) bufpos--;
	if ((c==0x20) || (c==0x0a) || (bufpos>=buf_size)) ms_flush();
  }

  public void ms_flush() {
	byte   j;
	StringBuffer SecondLevel = new StringBuffer(); 

	if (bufpos == 0) return;
	if (xpos+bufpos > sc_width) {
		transcript_write((char)0x0a);
		xpos=0;
	}
	for (j=0;j<bufpos;j++) {
		if (buffer[j]==0x0a) xpos=0;
		if (buffer[j]==0x08) {
			xpos-=2;
	    try
		{
			guiFrame.appendGameOutput( SecondLevel.subSequence(0, SecondLevel.length()-2).toString());
         //String s = textOut.getText().getText();  
            //textOut.removeFromTail(1);
            //textOut.removeFromTail(1);
            //textOut.removeTrail(1);
		}
		catch (Exception e)
		{
		}
		} else {
			SecondLevel.append(buffer[j]);
    }
		transcript_write(buffer[j]);
		xpos++;
	}
	try
	{
		guiFrame.appendGameOutput( SecondLevel.toString());
	}
	catch (Exception e)
	{
	}
	bufpos=0;
   //textOut.grabFocus();
	
	
  }

/*
  char ms_getchar() throws IOException{
  int c;
  byte  i;
  boolean clearc = false;

  System.out.println("Entering ms_getchar\n");
  ms_flush();
  running=0;
  return 32;
  }
*/

  public char ms_getchar(int trans) throws IOException{
  int c = 0;
  byte  i;
  boolean clearc = false;
  
  // DEBUG make DUMP
  /*
  FileOutputStream fos = new FileOutputStream(new File("c:/temp/memory.dmp"));
  fos.write((new String(code)).getBytes());
  fos.flush();
  fos.close();
  fos = new FileOutputStream(new File("c:/temp/strings.dmp"));
  fos.write((new String(tstring)).getBytes());
  fos.flush();
  fos.close();
  fos = new FileOutputStream(new File("c:/temp/strings2.dmp"));
  fos.write((new String(tstring2)).getBytes());
  fos.flush();
  fos.close();
  fos = new FileOutputStream(new File("c:/temp/dict.dmp"));
  fos.write((new String(dict)).getBytes());
  fos.flush();
  fos.close();
  */
  //
  
//  Runnable waitForPipe = new Runnable() {
//      public void run() {
//          try {
//             while (!kbIn.ready())
//             {try {sleep(100);} catch (Exception waite){}}
//          } catch (Exception ex) {
//              System.out.println(ex);
//          }
//      }
//  };
  
  // Test
  if (version==4 && autographics)
  {
	  guiFrame.appendUserInput("graphics\n");
	   try {
		guiFrame.getKBOut().write("graphics\n");
	} catch (IOException e) {
		e.printStackTrace();
	}

  }

  
	autographics = false;
  try {
  if (pos == 0) {	
    i=0;
    //textOut.grabFocus();
    //textOut.setEditMode( true );
    while ( true ) {
      if (log_on == 1) {
        try {
          c=log.readByte();
		    	guiFrame.appendUserInput(""+(char)c);
	        if (c == 13) log.readByte();
        }
	      catch ( EOFException e )
        {
          log_on = 0;
	        log.close();
			    //SwingUtilities.invokeLater(waitForPipe);
	    	//System.out.println("1");	
	            while (!kbIn.ready()) {sleep(10);};
	        	//System.out.println("2");	
			    c=kbIn.read();
		    	//System.out.println("3");	
                            //KeyBIn.receive(c);
        } 
      } else {
        //textOut.requestFocus();
        //SwingUtilities.invokeLater(waitForPipe);
    	//System.out.println("4 >>"+javax.swing.SwingUtilities.isEventDispatchThread());	
        while (!kbIn.ready()) {this.sleep(10);};
      	//System.out.println("5");	
        c=kbIn.read();
    	//System.out.println("6");	
        //KeyBIn.receive(c);
      }
      if (c == '#' && i == 0) {
        if (log_on == 1) {
          while (((c=log.readByte()) != '\n') && (c > 0) && (i < 255)) {
            buf[i++]=(char)c;
            guiFrame.appendUserInput(""+(char)c);
          }
	        if (c == 13) log.readByte();
	        guiFrame.appendUserInput(""+(char)c);
        } else {
    	  //SwingUtilities.invokeLater(waitForPipe);	
    		
          //KeyBIn.receive(c);  
            while (!kbIn.ready()) {wait();};
          while (((c = kbIn.read()) != '\n') && (i < 255)) {
              buf[i++]=(char)c;
              //KeyBIn.receive(c);  
          }    
        }
	      buf[i]=0;
	      c = 13;
	      String s;
	      if (log_on == 1) {
	        s = new String(buf,0,i-1);
	      } else {
          s = new String(buf,0,i);
        }  
	      i=0;
	      if (s.compareTo("logoff") == 0) {
          guiFrame.appendEngineStatusText("\n[Closing script file]");
	        if (log_on == 1) log.close();
          log_on = 0;
	      } else if (s.compareTo("sound") == 0) {
	    	  sndenabled = !sndenabled;
	    	  guiFrame.appendEngineStatusText("\n[Sound is "+(sndenabled?"enabled":"disabled")+"]\n>");
	      } else if (s.compareTo("memdump") == 0) {
	    	  FileOutputStream fos = new FileOutputStream(new File("c:/temp/memory-"+dmpcount+".dmp"));
	    	  fos.write((new String(code)).getBytes());
	    	  fos.flush();
	    	  fos.close();
//	    	  fos = new FileOutputStream(new File("c:/temp/strings-"+dmpcount+".dmp"));
//	    	  fos.write((new String(tstring)).getBytes());
//	    	  fos.flush();
//	    	  fos.close();
//	    	  fos = new FileOutputStream(new File("c:/temp/strings2-"+dmpcount+".dmp"));
//	    	  fos.write((new String(tstring2)).getBytes());
//	    	  fos.flush();
//	    	  fos.close();
//	    	  fos = new FileOutputStream(new File("c:/temp/dict-"+dmpcount+".dmp"));
//	    	  fos.write((new String(dict)).getBytes());
//	    	  fos.flush();
//	    	  fos.close();	      
	    	  guiFrame.appendEngineStatusText("\n[All dumps created, stage: "+dmpcount+".]\n>");
	    	  dmpcount++;
	    } else if (s.compareTo("seed") == 0) {
          StringBuffer tmparr = new StringBuffer();
          int cNew = -1; 
          guiFrame.appendEngineStatusText("\nNew random seed:");
          if (log_on == 1) {
            while ( ((cNew = log.readByte()) != 13)) {
              tmparr.append((char)cNew);
              guiFrame.appendUserInput(""+(char)cNew);
            }
            if (cNew == 13) log.readByte();
            guiFrame.appendUserInput("\n");
          } else {
          	//textOut.setEditMode(true);
      		//SwingUtilities.invokeLater(waitForPipe);

            //KeyBIn.receive(cNew);    
	            while (!kbIn.ready()) {this.sleep(2);};
            while ( ((cNew = kbIn.read()) != '\n')) {
                tmparr.append((char)cNew);
                //KeyBIn.receive(cNew);
            }
          }
          String sNew = new String(tmparr);
          int iRand = Integer.parseInt(sNew);
          ms_seed(iRand);
          guiFrame.appendEngineStatusText("\n[Setting random seed to "+iRand+"]\n");
        } else if (s.compareTo("undo") == 0) {
        	ms_undo();
        	//guiFrame.appendEngineStatusText("\n[UNDO is disabled!]");
          //clearc = true;
					//c = 0;
	      } else {
	    	  guiFrame.appendEngineStatusText("\n[Nothing done]");
	      }
		  }
		if (c != 0 && (c != 10 || (c == 10 && i != 0))) script_write((char)c);
		if (c != 0 && (c != 10 || (c == 10 && i != 0))) transcript_write((char)c);
		if (c == 10 && i != 0) break;
  	    if (c == 13 || c==-1 || i==255) break;
	    if ( c != 10 ) buf[i++]=(char)c;
	    if (c == 0) break;
	    if (c == '\n') break;
	  }
	  buf[i] = '\n';
	}
     } catch (Exception ie) {
        //System.out.println(ie);
     }
	if (!clearc) {
	  c=buf[pos++];
	  if ((c=='\n' || c == 0)) pos=0;
  	return (char)c;
	}
	else {
		pos = 0;
		return 0;
	}
  }

  void script_write(char c){
    try {
	  if (log_on==2) {
		log.write((byte)c);
		if (c == 0x0d) log.write((byte)0x0a);
	  }
    } 
	catch ( IOException e ) {
	  try {
		  guiFrame.appendEngineErrorText("[Problem with script file - closing]\n");
 	    log_on=0;
	    log.close();
	    guiFrame.appendEngineErrorText("[Fatal error on closing file]\n");
      }
      catch ( IOException e2 ){
      } catch (Exception ble)
		{
			//e.printStackTrace();
		}
    }
  }

  void transcript_write(char c) {
    try {
 	  if (log2 != null && c==0x08 && log2.getFilePointer()>0) log2.seek(log2.getFilePointer()-1);
	  else if (log2 != null) log2.write((byte)c);
    } 
	catch ( IOException e ) {
	  try {
		  guiFrame.appendEngineErrorText("[Problem with transcript file - closing]\n");
 	    log_on=0;
	    log2.close();
	    guiFrame.appendEngineErrorText("[Fatal error on closing file]\n");
      }
      catch ( IOException e2 ){
      } catch (Exception ble)
		{
		}
    }
  }
	
	public void ms_waitForKey() {
      String msg = "Press any key to start game!\n";   
		if (tsWait) {
      try
		{
    	  guiFrame.appendEngineStatusText(msg);
		}
		catch (Exception e)
		{
		}
      //textOut.setSimpleKeyPress(true);
      //textOut.setEditMode(true);
      try {
        //if (kbIn.read() == -1) { try {wait();} catch (Exception waite){} }

    	  kbIn.read();
        //textOut.setSimpleKeyPress(false);
      }
      catch (IOException ioex ) {}  
      guiFrame.rollUp();
      try
      {
         //textOut.setText("");
			guiFrame.clearInterpreterOutput();
         //textOut.removeFromTail(msg.length());
      }
      catch (Exception e)
      {
      }
      //guiFrame.enableScript();
      tsWait = false;
		}
  }
  
  public int ms_showhints(Vector hints)
  {
	  if (useHintWindow)
	  {
	  final Vector hintsLocal = hints; 
			SwingUtilities.invokeLater(new Runnable() {
			    public void run() {
			    	JMagneticHintDlg hintWnd = new JMagneticHintDlg(guiFrame, hintsLocal, gameTag);
			    	hintWnd.setVisible(true);
			    	
			}});
     return 1;
	  }
	  else
	  {
		  return 0;
	  }
  }
  

  public void clearText() {
      try
		{
			guiFrame.clearInterpreterOutput();
         //textOut.setText("");
		}
		catch (Exception e)
		{
		}
  }

public boolean isAutographics() {
	return autographics;
}

public void setAutographics(boolean autographics) {
	this.autographics = autographics;
}

public boolean isWaitForAnims() {
	return waitForAnims;
}

public void setWaitForAnims(boolean waitForAnims) {
	this.waitForAnims = waitForAnims;
}

public void setSaverequest(boolean saverequest) {
	this.saverequest = saverequest;
}

public void setUseHintWindow(boolean useHintWindow) {
	this.useHintWindow = useHintWindow;
}

public void setLastSaveDir(boolean lastSaveDir) {
	this.lastSaveDir = lastSaveDir;
}

public void setSndDevice(String sndDevice) {
	this.sndDevice = sndDevice;
}

public void ms_playmusic(String name) {
	if (sndenabled)
	{
		if (name == null)
		{
			if ((sequencer != null))
			{
				
					sequencer.stop();
					sequencer.close();
					sequencer = null;
			}
		}
		else 
		{
	   try {
		   JMagneticSound snd = (JMagneticSound)sounds.get(name);
		   Sequence sequence;
		   if (snd != null)
		   {
				   MidiDevice.Info		info = getMidiDeviceInfo(sndDevice, true);
                   if (info != null)
                   {
    				   MidiDevice	midiDevice = MidiSystem.getMidiDevice(info);
				      midiDevice.open();
					   // From InputStream
					   sequence = MidiSystem.getSequence(new StringBufferInputStream(new String(snd.data)));
			    
					   // Create a sequencer for the sequence
						if ((sequencer != null))
						{
								sequencer.stop();
								sequencer.close();
								sequencer = null;
						}
				      sequencer = MidiSystem.getSequencer(false);
					      sequencer.open();
					   sequencer.setSequence(sequence);
					   sequencer.setTempoInBPM(snd.tempo);
					   
					Receiver	midiReceiver = midiDevice.getReceiver();
					Transmitter	midiTransmitter = sequencer.getTransmitter();
					midiTransmitter.setReceiver(midiReceiver);
			        // Start playing
			        sequencer.start();
                   }
                   else
                   {
           		    guiFrame.appendEngineErrorText("\n[Error on accessing sound device:"+sndDevice+"]\n");
                	   
                   }
		   }
	    } catch (IOException e) {
		    guiFrame.appendEngineErrorText("[Error on reading music data.]\n");
	    } catch (MidiUnavailableException e) {
		    guiFrame.appendEngineErrorText("[Error on accessing sound device:"+sndDevice+"]\n");
	    } catch (InvalidMidiDataException e) {
		    guiFrame.appendEngineErrorText("[Error on reading music data.]\n");
	    }
		}
	}
}
public MidiDevice.Info getMidiDeviceInfo(String strDeviceName, boolean bForOutput)
{
	MidiDevice.Info[]	aInfos = MidiSystem.getMidiDeviceInfo();
	for (int i = 0; i < aInfos.length; i++)
	{
		if (aInfos[i].getName().equals(strDeviceName))
		{
			try
			{
				MidiDevice device = MidiSystem.getMidiDevice(aInfos[i]);
				boolean	bAllowsInput = (device.getMaxTransmitters() != 0);
				boolean	bAllowsOutput = (device.getMaxReceivers() != 0);
				if ((bAllowsOutput && bForOutput) || (bAllowsInput && !bForOutput))
				{
					return aInfos[i];
				}
			}
			catch (MidiUnavailableException e)
			{
			    //guiFrame.appendEngineErrorText("[Error on accessing sound devices.]\n");
			}
		}
	}
	return null;
}

public void setSaverequest2(boolean saverequest2) {
	this.saverequest2 = saverequest2;
	
}

}