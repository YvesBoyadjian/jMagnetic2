package org.iflegends.msmemorial.magnetic;

import java.io.IOException;
import java.io.PipedWriter;

public class JMagneticGameStarter extends Thread {
	
	private JMagnetic2 inst;
	
	JMagneticGameStarter(JMagnetic2 inst)
	{
		this.inst = inst;
	}
	
	
	public void run()
	{
	       // Kill interpreter instance
		try {
			inst.getKBOut().close();
			inst.killKBOut();
			inst.kbOut = new PipedWriter();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		inst.magInstance.stop();
		inst.magInstance=null;
		System.gc();
		// clean up UI
        inst.resetDividerLocation();
        inst.clearInputLine();
        inst.clearInterpreterOutput();
        inst.clearStatusLine();
		// select game
		StringBuffer gameTag = new StringBuffer();
		StringBuffer storyFile = new StringBuffer("");

        if (JMagnetic2.gameAvailable())
		{
           inst.gameSelect.setVisible(true);
           gameTag.append(inst.gameSelect.getSelectedGame());
		}
		if (gameTag.toString().equals(""))
		{		 
			storyFile.append(inst.getGameFile());
			gameTag.append(inst.getGameTag(storyFile.toString()));
		}
        if (!gameTag.toString().equals(""))
        {
			inst.startInterpreter(gameTag.toString(), storyFile.toString());
			inst.inputLine.requestFocus();
		}
		
		
	}

}
