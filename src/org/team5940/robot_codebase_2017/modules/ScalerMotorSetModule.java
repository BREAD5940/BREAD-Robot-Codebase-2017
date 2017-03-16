package org.team5940.robot_codebase_2017.modules;

import org.team5940.robot_core.modules.actuators.motor_sets.CANTalonMotorSetModule;
import org.team5940.robot_core.modules.actuators.motor_sets.TestableMotorSetModule;
import org.team5940.robot_core.modules.logging.LoggerModule;
import org.team5940.robot_core.modules.ownable.ThreadUnauthorizedException;
import org.team5940.robot_core.modules.testing.communication.TestCommunicationModule;

import com.ctre.CANTalon;

/**
 * A {@link CANTalonMotorSetModule} named "scaler_motor_set" that can only be set to positive values and with a scaler-specific test.
 * @author David Boles
 *
 */
public final class ScalerMotorSetModule extends CANTalonMotorSetModule implements TestableMotorSetModule {

	/**
	 * Initializes a new {@link ScalerMotorSetModule}.
	 * @param logger This' logger.
	 * @param talons This' talons. They should be configured so that positive is climbing.
	 * @throws IllegalArgumentException Thrown if any argument is null.
	 */
	public ScalerMotorSetModule(LoggerModule logger, CANTalon[] talons) throws IllegalArgumentException {
		super("scaler_motor_set", logger, talons);
	}
	
	@Override
	public synchronized void setMotorSpeed(double speed) throws IllegalArgumentException, ThreadUnauthorizedException {
		if(speed > 0) super.setMotorSpeed(speed);
		else super.setMotorSpeed(0);
	}
	
	@Override
	public TestStatus runTest(TestCommunicationModule comms) throws IllegalArgumentException {
		try {
			if(comms.promptBoolean("Are you ready to disable the robot?")) {
				//FORWARD
				this.setMotorSpeed(0.15);
				if(this.getSetMotorSpeed() != 0.15d) {
					this.setMotorSpeed(0);
					this.getModuleLogger().logTestFailed(this, "Wrong Set Scaling Speed (" + this.getSetMotorSpeed() + ")");
					return TestStatus.FAILED;
				}
				if(!comms.promptBoolean("Is " + this.getModuleName() + " scaling?")) {
					this.setMotorSpeed(0);
					this.getModuleLogger().logTestFailed(this, "Not Scaling");
					return TestStatus.FAILED;
				}
				
				//RESET
				this.setMotorSpeed(0);
				this.getModuleLogger().logTestPassed(this);
				return TestStatus.PASSED;
			}
			this.getModuleLogger().logTestFailed(this, "Not Ready To Disable");
			return TestStatus.FAILED;
		}catch(Exception e) {
			this.getModuleLogger().logTestErrored(this, e);
			return TestStatus.ERRORED;
		}
		
	}

}
