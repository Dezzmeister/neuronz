package main.math.execution;

import com.aparapi.Kernel;
import com.aparapi.Range;
import com.aparapi.device.Device;
import com.aparapi.internal.kernel.KernelManager;

public final class SigmoidKernel extends Kernel {
	public static final Device BEST_DEVICE = KernelManager.instance().bestDevice();
	public static final SigmoidKernel kernel = new SigmoidKernel();
	
	private float[] out = null;
	private float[] in = null;
	
	private SigmoidKernel() {
		out = new float[1];
		in = new float[] {0};
		
		Range range = BEST_DEVICE.createRange(1);
		execute(range);
		
		System.out.println("Sigmoid Kernel initialized with " + BEST_DEVICE.getShortDescription());
	}
	
	@Override
	public void run() {
		int i = getGlobalId();
		out[i] = (1.0f/(1.0f + exp(-in[i])));
	}
	
	public void setInput(float[] _in) {
		in = _in;
	}
	
	public void setOutput(float[] _out) {
		out = _out;
	}
}
