package org.team5940.robot_codebase_2017.modules.auto_procedures;

import org.team5940.robot_codebase_2017.modules.ArmModule;
import org.team5940.robot_core.modules.Module;
import org.team5940.robot_core.modules.ModuleHashtable;
import org.team5940.robot_core.modules.aggregates.drivetrains.TankDrivetrainModule;
import org.team5940.robot_core.modules.control.procedures.AbstractProcedureModule;
import org.team5940.robot_core.modules.logging.LoggerModule;

//TODO docs
public final class BadCenterGearAutoProcedureModule extends AbstractProcedureModule {

	private final TankDrivetrainModule drivetrain;
	private final ArmModule arm;
	private long start = 0;
	private static final long armUp = 1000;
	private static final long forward = armUp + 1900;
	private static final long drop = forward + 1000;
	private static final long backward = drop + 2000;
	
	public BadCenterGearAutoProcedureModule(LoggerModule logger, TankDrivetrainModule drivetrain, ArmModule arm)
			throws IllegalArgumentException {
		super("forward_auto_procedure", new ModuleHashtable<Module>(drivetrain).chainPut(arm), logger);
		this.logger.checkInitializationArgs(this, BadCenterGearAutoProcedureModule.class, new Object[]{drivetrain, arm});
		this.drivetrain = drivetrain;
		this.arm = arm;
		this.logger.logInitialization(this, BadCenterGearAutoProcedureModule.class, new Object[]{drivetrain, arm});
	}

	@Override
	protected void doProcedureStart() throws Exception {
		start = System.currentTimeMillis();
		drivetrain.setShifterGear(0);
	}

	@Override
	protected boolean doProcedureUpdate() throws Exception {
//		if(System.currentTimeMillis() > start + 2000) {
//			this.drivetrain.updateTank(0, 0);
//			return true;
//		}
//		return false;
		long time = System.currentTimeMillis();
		
		if(time < this.start + armUp) {//Driving
			arm.setArmRaised(true);
		}else if(time < this.start + forward) {
			arm.setArmRaised(true);
			drivetrain.updateTank(0.5, 0.5);
		}else if(time < this.start + drop) {
			drivetrain.updateTank(0, 0);
			this.arm.setPistonState(true);
		}else if(time < this.start + backward) {
			this.drivetrain.updateTank(-0.25, -0.25);
		}else {
			this.drivetrain.updateTank(0, 0);
			this.arm.setArmRaised(false);
			return true;
		}
		return false;
	}

	@Override
	protected void doProcedureClean() throws Exception {
		this.drivetrain.updateTank(0, 0);
		this.arm.setArmRaised(false);
	}

}
