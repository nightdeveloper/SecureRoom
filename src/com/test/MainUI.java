package com.test;

import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.logging.Logger;

import javax.swing.*; 

public class MainUI  implements ActionListener{

	private static Logger logger = ConsoleLogger.getLogger(MainUI.class.getName());
	
	private static String ACTION_EXIT = "exit";
	
	private static int MIN_WINDOW_WIDTH  = 400;
	private static int MIN_WINDOW_HEIGHT = 500;
	
	private JFrame mainFrame;
	
	MainUI() {
		javax.swing.SwingUtilities.invokeLater(new Runnable() {
			public void run() {
				createAndShowGUI();
			}
		});			
	}
	
    private void createAndShowGUI() {
    	
        mainFrame = new JFrame("MainFrame");
        mainFrame.setMinimumSize(new Dimension(
        		MIN_WINDOW_WIDTH, MIN_WINDOW_HEIGHT));
        mainFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        
        // menu 
        JMenuBar menuBar = new JMenuBar();
        
        JMenu fileMenu = new JMenu("File");
        JMenuItem exitMenuItem = new JMenuItem("Exit");
        exitMenuItem.setActionCommand(ACTION_EXIT);
        exitMenuItem.addActionListener(this);
        fileMenu.add(exitMenuItem);        
        menuBar.add(fileMenu);
        
        menuBar.add(new JMenu("Triggers"));
        menuBar.add(new JMenu("Help"));
        
        mainFrame.setJMenuBar(menuBar);
        
        // console panel
        JPanel consolePanel = new JPanel();
        JTextArea logTextArea = new JTextArea();
        ConsoleLogger.setLogAreal(logTextArea);
        consolePanel.add(logTextArea);
                        
        // video panel
        JPanel videoPanel = new VideoPanel();
        
        // settings panel
        JPanel settingsPanel = new JPanel();
        
        // tab pane
        JTabbedPane pane = new JTabbedPane();
        pane.addTab("Console", consolePanel);
        pane.addTab("Video", videoPanel);
        pane.addTab("Settings", settingsPanel);
        mainFrame.getContentPane().add(pane);
 
        mainFrame.pack();
        mainFrame.setVisible(true);		
    }
	

	@Override
	public void actionPerformed(ActionEvent e) {
		if (ACTION_EXIT.equals(e.getActionCommand())) {
			logger.info("user closed application");
			mainFrame.dispose();
		}		
	}    
}
