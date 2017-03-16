package org.team5940.robot_codebase_2017.modules.auto_procedures;

import org.team5940.robot_core.modules.ModuleHashtable;
import org.team5940.robot_core.modules.aggregates.drivetrains.TankDrivetrainModule;
import org.team5940.robot_core.modules.control.procedures.AbstractProcedureModule;
import org.team5940.robot_core.modules.logging.LoggerModule;

//TODO docs
public final class ForwardAutoProcedureModule extends AbstractProcedureModule {

	private final TankDrivetrainModule drivetrain;
	private long start = 0;
	
	public ForwardAutoProcedureModule(LoggerModule logger, TankDrivetrainModule drivetrain)
			throws IllegalArgumentException {
		super("forward_auto_procedure", new ModuleHashtable<>(drivetrain), logger);
		this.logger.checkInitializationArg(this, ForwardAutoProcedureModule.class, drivetrain);
		this.drivetrain = drivetrain;
		this.logger.logInitialization(this, ForwardAutoProcedureModule.class, drivetrain);
	}

	@Override
	protected void doProcedureStart() throws Exception {
		start = System.currentTimeMillis();
		drivetrain.setShifterGear(0);
		drivetrain.updateTank(0.5, 0.5);
	}

	@Override
	protected boolean doProcedureUpdate() throws Exception {
		if(System.currentTimeMillis() > start + 2000) {
			this.drivetrain.updateTank(0, 0);
			return true;
		}
		return false;
		
	}

	@Override
	protected void doProcedureClean() throws Exception {
		drivetrain.updateTank(0, 0);
	}

}
