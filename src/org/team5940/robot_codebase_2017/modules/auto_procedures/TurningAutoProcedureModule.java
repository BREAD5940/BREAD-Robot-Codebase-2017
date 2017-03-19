package org.team5940.robot_codebase_2017.modules.auto_procedures;

import org.team5940.robot_codebase_2017.modules.ArmModule;
import org.team5940.robot_core.modules.Module;
import org.team5940.robot_core.modules.ModuleHashtable;
import org.team5940.robot_core.modules.aggregates.drivetrains.TankDrivetrainModule;
import org.team5940.robot_core.modules.control.procedures.AbstractProcedureModule;
import org.team5940.robot_core.modules.logging.LoggerModule;

//TODO docs
public final class TurningAutoProcedureModule extends AbstractProcedureModule {

	private final TankDrivetrainModule drivetrain;
	private final ArmModule arm;
	private final boolean direction;
	private long start = 0;
	private static final long armUp = 0;
	private static final long forward = armUp + 1900;
	private static final long turn = forward + 1500;
	
	
	public TurningAutoProcedureModule(LoggerModule logger, TankDrivetrainModule drivetrain, ArmModule arm, boolean direction)
			throws IllegalArgumentException {
		super("forward_auto_procedure", new ModuleHashtable<Module>(drivetrain).chainPut(arm), logger);
		this.logger.checkInitializationArgs(this, TurningAutoProcedureModule.class, new Object[]{drivetrain, arm, direction});
		this.drivetrain = drivetrain;
		this.arm = arm;
		this.direction = direction;
		this.logger.logInitialization(this, TurningAutoProcedureModule.class, new Object[]{drivetrain, arm, direction});
	}

	@Override
	protected void doProcedureStart() throws Exception {
		start = System.currentTimeMillis();
		drivetrain.setShifterGear(0);
	}

	@Override
	protected boolean doProcedureUpdate() throws Exception {
		long time = System.currentTimeMillis();
		
		if(time < this.start + armUp) {//Driving
			arm.setPistonState(false);
			arm.setArmRaised(true);
		}else if(time < this.start + forward) {
			arm.setPistonState(false);
			arm.setArmRaised(true);
			drivetrain.updateTank(0.5, 0.5);
		}else if(time < this.start + turn) {
			drivetrain.updateTank(0, 0);
			this.drivetrain.updateArcade(0, this.direction ? 0.25 : -0.25);
		}else {
			drivetrain.updateTank(0, 0);
			return true;
		}
		return false;
	}

	@Override
	protected void doProcedureClean() throws Exception {
		this.drivetrain.updateTank(0, 0);
	}

}
