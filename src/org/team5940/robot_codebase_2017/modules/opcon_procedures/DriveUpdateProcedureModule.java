package org.team5940.robot_codebase_2017.modules.opcon_procedures;

import org.team5940.robot_core.modules.Module;
import org.team5940.robot_core.modules.ModuleHashtable;
import org.team5940.robot_core.modules.aggregates.drivetrains.TankDrivetrainModule;
import org.team5940.robot_core.modules.control.procedures.AbstractProcedureModule;
import org.team5940.robot_core.modules.logging.LoggerModule;
import org.team5940.robot_core.modules.sensors.axes.AxisModule;
import org.team5940.robot_core.modules.sensors.selectors.SelectorModule;

//TODO
public final class DriveUpdateProcedureModule extends AbstractProcedureModule {

	private final TankDrivetrainModule drivetrain;
	private final AxisModule forwardAxis;
	private final AxisModule yawAxis;
	private final SelectorModule direction;
	
	//TODO
	public DriveUpdateProcedureModule(LoggerModule logger, TankDrivetrainModule drivetrain, AxisModule forwardAxis, AxisModule yawAxis, SelectorModule direction)
			throws IllegalArgumentException {
		super("drive_update_procedure", new ModuleHashtable<>(new Module[]{drivetrain, forwardAxis, yawAxis, direction}), logger);
		this.logger.checkInitializationArgs(this, DriveUpdateProcedureModule.class, new Object[]{drivetrain, forwardAxis, yawAxis, direction});
		this.drivetrain = drivetrain;
		this.forwardAxis = forwardAxis;
		this.yawAxis = yawAxis;
		this.direction = direction;
		this.logger.logInitialization(this, DriveUpdateProcedureModule.class, new Object[]{drivetrain, forwardAxis, yawAxis, direction});
		
	}

	@Override
	protected void doProcedureStart() throws Exception { }

	@Override
	protected boolean doProcedureUpdate() throws Exception {
		double forward = this.forwardAxis.getAxis();
		double yaw = this.yawAxis.getAxis();
		if(direction.getCurrentState() == 1) {
			forward = -forward;
			//yaw = -yaw;
		}
		this.logger.vLog(this, "Updating Drivetrain", new Object[]{forward, yaw});
		this.drivetrain.updateArcade(forward, yaw);
		return false;
	}

	@Override
	protected void doProcedureClean() throws Exception {
		drivetrain.updateTank(0, 0);
	}

}
