package org.team5940.robot_codebase_2017.modules.opcon_procedures;

import org.team5940.robot_core.modules.ModuleHashtable;
import org.team5940.robot_core.modules.actuators.motor_sets.MotorSetModule;
import org.team5940.robot_core.modules.control.procedures.AbstractProcedureModule;
import org.team5940.robot_core.modules.logging.LoggerModule;
import org.team5940.robot_core.modules.sensors.axes.AxisModule;

//TODO docs
public class RollerUpdateProcedureModule extends AbstractProcedureModule {

	private final MotorSetModule roller;
	private final AxisModule axis;
	private final boolean preventNegative;
	
	public RollerUpdateProcedureModule(String rollerName, LoggerModule logger, MotorSetModule roller, AxisModule axis, boolean preventNegative)
			throws IllegalArgumentException {
		super(rollerName + "_update_procedure", new ModuleHashtable<>(roller), logger);
		this.logger.checkInitializationArgs(this, RollerUpdateProcedureModule.class, new Object[]{roller, axis, preventNegative});
		this.roller = roller;
		this.axis = axis;
		this.preventNegative = preventNegative;
		this.logger.logInitialization(this, RollerUpdateProcedureModule.class, new Object[]{roller, axis, preventNegative});
	}

	@Override
	protected void doProcedureStart() throws Exception {
		
	}

	@Override
	protected boolean doProcedureUpdate() throws Exception {
		double axisVal = this.axis.getAxis();
		if (this.preventNegative && axisVal < 0) axisVal = 0;
		this.roller.setMotorSpeed(axisVal);
		return false;
	}

	@Override
	protected void doProcedureClean() throws Exception {
		this.roller.setMotorSpeed(0);
	}

}
