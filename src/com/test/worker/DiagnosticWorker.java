package com.test.worker;

import javax.swing.SwingWorker;

import org.hyperic.sigar.ProcCpu;
import org.hyperic.sigar.Sigar;


public class DiagnosticWorker extends SwingWorker<Integer, String> {

	public static final String CPU_LOAD = "cpu_load";
	
	@Override
	protected Integer doInBackground() throws Exception {
		
	    Sigar sigar = new Sigar();
        long pid = sigar.getPid();
        long cpuCount = sigar.getCpuList().length;
        ProcCpu prevPc = sigar.getProcCpu(pid);        
		
		while(!isCancelled()) {
			
            ProcCpu curPc = sigar.getProcCpu(pid);        
            long totalDelta = curPc.getTotal() - prevPc.getTotal();
            long timeDelta = curPc.getLastTime() - prevPc.getLastTime();                
            double load = 100. * totalDelta / timeDelta / cpuCount;;
            if (load > 100)
            	load = 100;
            if (load < 0)
            	load = 0;
            
            firePropertyChange(CPU_LOAD, "", Integer.valueOf((int)load).toString());
            
            prevPc = curPc;
            
            Thread.sleep(1000);
		}
		
		return null;
	}
}
