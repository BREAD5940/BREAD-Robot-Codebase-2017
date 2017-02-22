package org.team5940.robot_codebase_2017.robot;

import org.team5940.robot_core.modules.actuators.motorsets.CANTalonMotorSetModule;
import org.team5940.robot_core.modules.actuators.motorsets.MotorSetModule;
import org.team5940.robot_core.modules.actuators.pistons.DoubleSolenoidPistonModule;
import org.team5940.robot_core.modules.actuators.pistons.PistonModule;
import org.team5940.robot_core.modules.actuators.shifters.DoubleSolenoidShifterModule;
import org.team5940.robot_core.modules.actuators.shifters.ShifterModule;
import org.team5940.robot_core.modules.aggregates.drivetrains.SimpleTankDrivetrainModule;
import org.team5940.robot_core.modules.aggregates.drivetrains.TankDrivetrainModule;
import org.team5940.robot_core.modules.logging.LoggerModule;
import org.team5940.robot_core.modules.logging.SystemLoggerModule;
import org.team5940.robot_core.modules.robot.SimpleRobotModule;
import org.team5940.robot_core.modules.sensors.binary_input.BinaryInputModule;
import org.team5940.robot_core.modules.sensors.binary_input.DigitalBinaryInputModule;
import org.team5940.robot_core.modules.testing.TestableModule;
import org.team5940.robot_core.modules.testing.communication.SmartDashboardTestCommunicationModule;

import com.ctre.CANTalon;

import edu.wpi.first.wpilibj.DigitalInput;
import edu.wpi.first.wpilibj.DoubleSolenoid;

public class Robot extends SimpleRobotModule {
	
	@Override
	protected void initializeModules() throws Exception {
		System.out.println("STARTING!! :)");
		LoggerModule robot_logger = new SystemLoggerModule("robot_logger", true);
		
		//SHIFTER
		ShifterModule shifter = new DoubleSolenoidShifterModule("shifter", robot_logger, new DoubleSolenoid(9, 6, 7));
		
		//LEFT MOTORS
		CANTalon frontLeftTalon = new CANTalon(3);
		frontLeftTalon.setInverted(true);
		CANTalon backLeftTalon = new CANTalon(4);
		backLeftTalon.setInverted(true);//TODO Encoder object needs to invert.
		MotorSetModule leftMotorSet = new CANTalonMotorSetModule("left_motors", robot_logger, new CANTalon[]{frontLeftTalon, backLeftTalon});
		
		//RIGHT MOTORS
		CANTalon frontRightTalon = new CANTalon(1);
		CANTalon backRightTalon = new CANTalon(2);
		MotorSetModule rightMotorSet = new CANTalonMotorSetModule("right_motors", robot_logger, new CANTalon[]{frontRightTalon, backRightTalon});
		
		//DRIVETRAIN
		//TODO wasn't working
//		TankDrivetrainModule drivetrain = new SimpleTankDrivetrainModule("drivetrain", robot_logger, leftMotorSet, rightMotorSet, shifter);
		
		//INTAKE
		CANTalon intakeTalon = new CANTalon(6);
		intakeTalon.setInverted(true);
		MotorSetModule intakeMotorSet = new CANTalonMotorSetModule("intake_motor_set", robot_logger, intakeTalon);
		
		//ARM
		CANTalon armTalon = new CANTalon(5);
		armTalon.setInverted(true);
		//TODO should be replaced for safe test
		MotorSetModule armMotorSet = new CANTalonMotorSetModule("arm_motor_set", robot_logger, armTalon);
		
		//CUP
		PistonModule cupPiston = new DoubleSolenoidPistonModule("cup_piston", robot_logger, new DoubleSolenoid(9, 4, 5));
		
		//LIMIT SWITCHES
		DigitalInput upDigitalInput = new DigitalInput(0);
		BinaryInputModule upLimitSwitch = new DigitalBinaryInputModule("up_limit_switch", robot_logger, upDigitalInput, true);
		
		DigitalInput downDigitalInput = new DigitalInput(1);
		BinaryInputModule downLimitSwitch = new DigitalBinaryInputModule("down_limit_switch", robot_logger, downDigitalInput, true);
		
		//CLIMBING
		CANTalon leftClimberTalon = new CANTalon(7);
		leftClimberTalon.setInverted(true);
		CANTalon rightClimberTalon = new CANTalon(8);
		//TODO replace with better test
		MotorSetModule climberMotorSet = new CANTalonMotorSetModule("climber_motor_set", robot_logger, new CANTalon[]{leftClimberTalon, rightClimberTalon});
	}
	
	@Override
	public void operatorControl() {
		super.operatorControl();
//		
	}

}
