package com.test;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.logging.Logger;

import javax.swing.*; 

import com.test.worker.InitializeWorker;
import com.test.worker.VideoWorker;

public class MainUI  implements ActionListener{

	private static Logger logger = ConsoleLogger.getLogger(MainUI.class.getName());
	
	private static String ACTION_EXIT = "exit";
	
	private static int MIN_WINDOW_WIDTH  = 400;
	private static int MIN_WINDOW_HEIGHT = 500;
	
	private JFrame mainFrame;
	private VideoPanel videoPanel;
	
	MainUI() {
		javax.swing.SwingUtilities.invokeLater(new Runnable() {
			public void run() {
				createAndShowGUI();
				initialize(); 
			}
		});			
	}
	
	private void initialize() {
		InitializeWorker worker = new InitializeWorker();
		worker.addPropertyChangeListener(new PropertyChangeListener() {
			@Override
			public void propertyChange(PropertyChangeEvent event) {
				logger.info("property changed - " + event.getPropertyName() + " - " + event.getNewValue() );
				switch (event.getPropertyName()) {
					case InitializeWorker.RESULT:
						if (InitializeWorker.OK.equals( event.getNewValue() ))
							startVideo();
						break;
					case "progress":
					case "state":						
				}
			}
		    });
		worker.execute();
	}
	
	private void startVideo() {
		VideoWorker worker = new VideoWorker(videoPanel);
		worker.addPropertyChangeListener(new PropertyChangeListener() {
			@Override
			public void propertyChange(PropertyChangeEvent event) {
				logger.info("property changed - " + event.getPropertyName() + " - " + event.getNewValue() );
				switch (event.getPropertyName()) {
					case "progress":
					case "state":						
				}
			}
		    });
		worker.execute();
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
        JPanel consolePanel = new JPanel(new BorderLayout());
        JTextArea logTextArea = new JTextArea();
        ConsoleLogger.setLogAreal(logTextArea);
        consolePanel.add(logTextArea, BorderLayout.CENTER);
        
        // video panel
        videoPanel = new VideoPanel();
        
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
