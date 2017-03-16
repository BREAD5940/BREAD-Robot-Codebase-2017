package org.team5940.robot_codebase_2017.modules;

import org.team5940.robot_core.modules.Module;
import org.team5940.robot_core.modules.ModuleHashtable;
import org.team5940.robot_core.modules.actuators.shifters.ShifterModule;
import org.team5940.robot_core.modules.control.procedures.AbstractProcedureModule;
import org.team5940.robot_core.modules.logging.LoggerModule;
import org.team5940.robot_core.modules.sensors.binary_input.BinaryInputModule;

//TODO docs
public final class ShifterUpdateProcedureModule extends AbstractProcedureModule {
	
	//TODO docs
	private final ShifterModule shifter;
	private final BinaryInputModule shiftUp;
	private final BinaryInputModule shiftDown;
	
	//TODO docs
	public ShifterUpdateProcedureModule(LoggerModule logger, ShifterModule shifter, BinaryInputModule shiftUp, BinaryInputModule shiftDown)
			throws IllegalArgumentException {
		super("shifter_update_procedure", new ModuleHashtable<Module>().chainPut(shifter).chainPut(shiftUp).chainPut(shiftDown), logger);
		this.logger.checkInitializationArgs(this, ShifterUpdateProcedureModule.class, new Object[]{shifter, shiftUp, shiftDown});
		this.shifter = shifter;
		this.shiftUp = shiftUp;
		this.shiftDown = shiftDown;
		this.logger.logInitialization(this, ShifterUpdateProcedureModule.class, new Object[]{shifter, shiftUp, shiftDown});
	}

	@Override
	protected void doProcedureStart() throws Exception {
		this.shifter.setShifterGear(0);
	}

	boolean lastUp;
	boolean lastDown;
	@Override
	protected boolean doProcedureUpdate() throws Exception {
		boolean up = this.shiftUp.getBinaryInput();
		boolean down = this.shiftDown.getBinaryInput();
		int gear = shifter.getShifterGear();
		int max = shifter.getNumberOfGears();
		
		if(!lastUp && !down && up && gear < max-1) shifter.setShifterGear(gear+1);
		else if(!lastDown && !up && down && gear > 0) shifter.setShifterGear(gear-1);
		
		lastUp = up;
		lastDown = down;
		return false;
	}

	@Override
	protected void doProcedureClean() throws Exception {
		this.shifter.setShifterGear(0);
	}

}
