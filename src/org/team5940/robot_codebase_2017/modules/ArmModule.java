package org.team5940.robot_codebase_2017.modules;

import org.team5940.robot_core.modules.Module;
import org.team5940.robot_core.modules.ModuleHashtable;
import org.team5940.robot_core.modules.actuators.motor_sets.MotorSetModule;
import org.team5940.robot_core.modules.actuators.pistons.PistonModule;
import org.team5940.robot_core.modules.logging.LoggerModule;
import org.team5940.robot_core.modules.ownable.AbstractOwnableModule;
import org.team5940.robot_core.modules.ownable.ThreadUnauthorizedException;
import org.team5940.robot_core.modules.sensors.binary_input.BinaryInputModule;
import org.team5940.robot_core.modules.sensors.selectors.SelectorModule;
import org.team5940.robot_core.modules.testing.communication.TestCommunicationModule;

/**
 * A module representing the arm of Ciabatta. It's states are -1 for transitioning, 0 for down, and 1 for up (or false and true respectively if you're setting it). Any {@link PistonModule} calls are passed to the cupPiston however it is forced contracted if the arm is lowered.
 * @author David Boles
 *
 */
public final class ArmModule extends AbstractOwnableModule implements SelectorModule, PistonModule {

	//TODO document
	protected final MotorSetModule armMotorSet;
	protected final PistonModule cupPiston;
	protected final BinaryInputModule downSwitch;
	protected final BinaryInputModule upSwitch;
	
	/**
	 * Stores the state the arm is moving to. False for down, true for up.
	 */
	protected boolean setState = false;
	
	/**
	 * The current state of the arm. -1 for moving, 0 for down, 1 for up.
	 */
	protected int state = 0;
	
	public ArmModule(LoggerModule logger, MotorSetModule armMotorSet, PistonModule cupPiston, BinaryInputModule downSwitch, BinaryInputModule upSwitch)
			throws IllegalArgumentException {
		super("arm", new ModuleHashtable<Module>(cupPiston), logger);
		this.logger.checkInitializationArgs(this, ArmModule.class, new Object[]{armMotorSet, cupPiston, downSwitch, upSwitch});
		this.armMotorSet = armMotorSet;
		this.cupPiston = cupPiston;
		this.downSwitch = downSwitch;
		this.upSwitch = upSwitch;
		new ArmThread(this, logger).start();
		this.logger.logInitialization(this, ArmModule.class, new Object[]{armMotorSet, cupPiston, downSwitch, upSwitch});
	}

	@Override
	public synchronized void setPistonState(boolean state) throws ThreadUnauthorizedException {
		if(setState) cupPiston.setPistonState(state);
		else this.logger.vLog(this, "Not Setting Cup Piston, Set To Down");
	}

	@Override
	public synchronized boolean getPistonState() {
		return this.cupPiston.getPistonState();
	}

	@Override
	public synchronized int getNumberOfStates() {
		this.logger.logGot(this, "Number Of States", 2);
		return 2;
	}

	@Override
	public synchronized int getCurrentState() {
		this.logger.logGot(this, "Current State", this.state);
		return this.state;
	}
	
	/**
	 * Sets whether the arm is raised or lowered. If lowering, contracts the cup.
	 * @param raised Whether to raise the arm (true) or lower it (false).
	 * @throws ThreadUnauthorizedException Thrown if this or the cup (when lowering) aren't accessible to the current thread.
	 */
	public synchronized void setArmRaised(boolean raised) throws ThreadUnauthorizedException {
		this.doAccessibilityCheck();
		this.logger.logSettingPrimitiveArgs(this, "Arm Raised", raised);
		if(!raised) this.cupPiston.setPistonState(false);
		this.setState = raised;
	}

	@Override
	public TestStatus runTest(TestCommunicationModule comms) throws IllegalArgumentException {
		try {
			//TODO
			
			//RESET
			//TODO
			this.getModuleLogger().logTestPassed(this);
			return TestStatus.PASSED;
			
		}catch(Exception e) {
			this.getModuleLogger().logTestErrored(this, e);
			return TestStatus.ERRORED;
		}
		
	}

	
	/**
	 * The thread used to update the arm motors and arm state.
	 * @author David Boles
	 *
	 */
	private class ArmThread extends Thread {
		
		//TODO document
		private final ArmModule arm;
		private final LoggerModule logger;
		
		public ArmThread(ArmModule arm, LoggerModule logger) {
			this.arm = arm;
			this.logger = logger;
		}
		
		@Override
		public void run() {
			this.arm.armMotorSet.acquireOwnershipForCurrent(true);
			
			try{
				while(!this.isInterrupted()) {
					//Arm state detection
					boolean downIn = this.arm.downSwitch.getBinaryInput();
					boolean upIn = this.arm.upSwitch.getBinaryInput();
					int state = -1;
					if(downIn) state = 0;
					else if(upIn) state = 1;
					if(state != this.arm.state) {
						this.logger.vLog(this.arm, "Setting Arm State", new Object[]{state, downIn, upIn});
						this.arm.state = state;
					}
					this.logger.log(this.arm, "State", state);
					
					if(this.arm.state == -1 || (state == 0 && arm.setState == true) || (state == 1 && arm.setState == false)) {
						//Computing arm speed
						double setSpeed = this.arm.setState ? 0.3 : -0.1;
						this.logger.log(this.arm, "Speed", setSpeed);
						
						//Setting arm speed
						this.logger.vLog(this.arm, "Setting Arm Motor Speed", setSpeed);
						try {
							this.arm.armMotorSet.setMotorSpeed(setSpeed);
						} catch (ThreadUnauthorizedException e) {
							synchronized(this.arm.armMotorSet) {//Force arm acquisition
								this.arm.armMotorSet.acquireOwnershipForCurrent(true);
								this.arm.armMotorSet.setMotorSpeed(setSpeed);
							}
						}
					}else {
						try {
							this.logger.log(this.arm, "SETTING TO 0");
							this.arm.armMotorSet.setMotorSpeed(0);
						} catch (ThreadUnauthorizedException e) {
							synchronized(this.arm.armMotorSet) {//Force arm acquisition
								this.arm.armMotorSet.acquireOwnershipForCurrent(true);
								this.arm.armMotorSet.setMotorSpeed(0);
							}
						}
					}
					Thread.sleep(10);
				}
			}catch (Exception e) {
				this.logger.error(this.arm, "Arm Update Thread Failed", e);
			}
		}
	}
}