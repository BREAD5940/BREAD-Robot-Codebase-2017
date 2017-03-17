package org.team5940.robot_codebase_2017.modules.opcon_procedures;

import org.team5940.robot_codebase_2017.modules.ArmModule;
import org.team5940.robot_core.modules.Module;
import org.team5940.robot_core.modules.ModuleHashtable;
import org.team5940.robot_core.modules.control.procedures.AbstractProcedureModule;
import org.team5940.robot_core.modules.logging.LoggerModule;
import org.team5940.robot_core.modules.sensors.binary_input.BinaryInputModule;

//TODO docs
public class ArmUpdateProcedureModule extends AbstractProcedureModule {

	private final ArmModule arm;
	private final BinaryInputModule armUp;
	private final BinaryInputModule armDown;
	private final BinaryInputModule cupExtended;
	private final BinaryInputModule cupContracted;
	
	public ArmUpdateProcedureModule(LoggerModule logger, ArmModule arm, BinaryInputModule armUp, BinaryInputModule armDown, BinaryInputModule cupExtended, BinaryInputModule cupContracted)
			throws IllegalArgumentException {
		super("arm_update_procedure", new ModuleHashtable<Module>().chainPut(arm).chainPut(armUp).chainPut(armDown).chainPut(cupExtended).chainPut(cupContracted), logger);
		this.logger.checkInitializationArgs(this, ArmUpdateProcedureModule.class, new Object[]{arm, armUp, armDown, cupExtended, cupContracted});
		this.arm = arm;
		this.armUp = armUp;
		this.armDown = armDown;
		this.cupExtended = cupExtended;
		this.cupContracted = cupContracted;
		this.logger.logInitialization(this, ArmUpdateProcedureModule.class, new Object[]{arm, armUp, armDown, cupExtended, cupContracted});
	}

	@Override
	protected void doProcedureStart() throws Exception { }

	@Override
	protected boolean doProcedureUpdate() throws Exception {
		boolean armUp = this.armUp.getBinaryInput();
		boolean armDown = this.armDown.getBinaryInput();
		boolean cupExtended = this.cupExtended.getBinaryInput();
		boolean cupContracted = this.cupContracted.getBinaryInput();
		
		if(armUp && !armDown) this.arm.setArmRaised(true);
		if(armDown && !armUp) this.arm.setArmRaised(false);
		
		if(cupExtended && !cupContracted) this.arm.setPistonState(true);
		if(cupContracted && !cupExtended) this.arm.setPistonState(false);
		
		return false;
	}

	@Override
	protected void doProcedureClean() throws Exception {
		arm.setArmRaised(false);//TODO should it?
	}
	
	
}
