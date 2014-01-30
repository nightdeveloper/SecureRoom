package com.test;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.logging.Logger;

import javax.swing.*; 

import com.test.worker.DiagnosticWorker;
import com.test.worker.InitializeWorker;
import com.test.worker.VideoWorker;

public class MainUI  implements ActionListener{

	private static Logger logger = ConsoleLogger.getLogger(MainUI.class.getName());
	
	private static String ACTION_EXIT = "exit";
	
	private static int MIN_WINDOW_WIDTH  = 400;
	private static int MIN_WINDOW_HEIGHT = 500;
	
	private JFrame mainFrame;
	private VideoPanel videoPanel;
    private JTabbedPane pane = new JTabbedPane();
	
	private final ArrayList<SwingWorker<Integer, String>> workerList = 
			new ArrayList<SwingWorker<Integer, String>>();
	
	MainUI() {
		javax.swing.SwingUtilities.invokeLater(new Runnable() {
			public void run() {
				createAndShowGUI();
				initialize(); 
			}
		});			
	}	
	
	private void initialize() {
		final InitializeWorker worker = new InitializeWorker();
		worker.addPropertyChangeListener(new PropertyChangeListener() {
			@Override
			public void propertyChange(PropertyChangeEvent event) {
				logger.warning("initialize " + event.getPropertyName() + ": " + event.getNewValue());
				switch (event.getPropertyName()) {
					case InitializeWorker.RESULT:
						if (InitializeWorker.OK.equals( event.getNewValue() )) {
							startVideo();
						}
						break;
					case "done":
						workerList.remove(worker);
						break;
				}
			}
		    });
		workerList.add(worker);
		worker.execute();
	}
	
	private final JProgressBar motionBar = new JProgressBar();
	
	private void startVideo() {
		final VideoWorker worker = new VideoWorker(videoPanel);
		worker.addPropertyChangeListener(new PropertyChangeListener() {
			@Override
			public void propertyChange(PropertyChangeEvent event) {
				logger.warning("video " + event.getPropertyName() + ": " + event.getNewValue());
				switch (event.getPropertyName()) {
					case VideoWorker.VIDEO_WIDTH:
						int newWidth = (Integer)event.getNewValue()*2 + 
								(mainFrame.getSize().width - videoPanel.getSize().width);
						logger.warning("new width = " + newWidth);
						mainFrame.setSize(newWidth, mainFrame.getSize().height);
						break;
					case VideoWorker.VIDEO_HEIGHT:
						int newHeight = (Integer)event.getNewValue() + 
								(mainFrame.getSize().height - videoPanel.getSize().height); 
						logger.warning("new width = " + newHeight);
						//mainFrame.setSize(mainFrame.getSize().width, newHeight);
						pane.setSelectedIndex(1);
						break;
					case VideoWorker.VIDEO_MOTION:
						motionBar.setValue(Integer.valueOf(event.getNewValue().toString()));
						break;
					case "done":
						workerList.remove(worker);
					break;
				}
			}
		    });
		workerList.add(worker);
		worker.execute();
	}
	
	private final JProgressBar cpuProgressBar = new JProgressBar();
	
	private void startDiagnostic() {
		final DiagnosticWorker worker = new DiagnosticWorker();
		worker.addPropertyChangeListener(new PropertyChangeListener() {
			@Override
			public void propertyChange(PropertyChangeEvent event) {
				if (!DiagnosticWorker.CPU_LOAD.equals(event.getPropertyName()))
					logger.warning("diagnostic " + event.getPropertyName() + ": " + event.getNewValue());
				switch (event.getPropertyName()) {
					case "done":
						workerList.remove(worker);
						break;
					case DiagnosticWorker.CPU_LOAD:
							cpuProgressBar.setValue(Integer.valueOf(event.getNewValue().toString()));
						break;
				}
			}
		    });
		workerList.add(worker);
		worker.execute();		
	}
	
    private void createAndShowGUI() {
    	
        mainFrame = new JFrame("MainFrame");
        mainFrame.setMinimumSize(new Dimension(
        		MIN_WINDOW_WIDTH, MIN_WINDOW_HEIGHT));
        mainFrame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        mainFrame.addWindowListener(new WindowListener() {
    		public void windowActivated(WindowEvent arg0) {}
    		public void windowClosed(WindowEvent arg0) {}
    		public void windowClosing(WindowEvent arg0) {
    	        ConsoleLogger.setLogArea(null);
    	        
    			Iterator<SwingWorker<Integer,String>> it = workerList.iterator();
    			while(it.hasNext()) {
    				SwingWorker<Integer, String> sw = it.next();
    				sw.cancel(true);
    			}
    		}
    		public void windowDeactivated(WindowEvent arg0) {}
    		public void windowDeiconified(WindowEvent arg0) {}
    		public void windowIconified(WindowEvent arg0) {}
    		public void windowOpened(WindowEvent arg0) {}
        }
        );
        
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
        ConsoleLogger.setLogArea(logTextArea);
        consolePanel.add(logTextArea, BorderLayout.CENTER);
        
        // video panel
        videoPanel = new VideoPanel();
        
        // settings panel
        JPanel settingsPanel = new JPanel();
                
        // tab pane
        pane = new JTabbedPane();
        pane.addTab("Console", consolePanel);
        pane.addTab("Video", videoPanel);
        pane.addTab("Settings", settingsPanel);
        
        JPanel mainPanel = new JPanel(new BorderLayout());
        mainPanel.add(pane, BorderLayout.CENTER);
        mainPanel.add(cpuProgressBar, BorderLayout.PAGE_END);
        mainPanel.add(motionBar, BorderLayout.PAGE_START);
        
        mainFrame.getContentPane().add(mainPanel);
        
        startDiagnostic();
 
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
