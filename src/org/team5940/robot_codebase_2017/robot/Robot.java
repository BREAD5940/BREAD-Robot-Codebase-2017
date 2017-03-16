package org.team5940.robot_codebase_2017.robot;

import java.io.File;

import org.team5940.robot_codebase_2017.modules.ArmModule;
import org.team5940.robot_codebase_2017.modules.DriveUpdateProcedure;
import org.team5940.robot_codebase_2017.modules.ScalerMotorSetModule;
import org.team5940.robot_core.modules.ModuleHashtable;
import org.team5940.robot_core.modules.RobotModule;
import org.team5940.robot_core.modules.actuators.motor_sets.CANTalonMotorSetModule;
import org.team5940.robot_core.modules.actuators.motor_sets.MotorSetModule;
import org.team5940.robot_core.modules.actuators.pistons.DoubleSolenoidPistonModule;
import org.team5940.robot_core.modules.actuators.pistons.PistonModule;
import org.team5940.robot_core.modules.actuators.shifters.DoubleSolenoidShifterModule;
import org.team5940.robot_core.modules.actuators.shifters.ShifterModule;
import org.team5940.robot_core.modules.aggregates.drivetrains.SimpleTankDrivetrainModule;
import org.team5940.robot_core.modules.aggregates.drivetrains.TankDrivetrainModule;
import org.team5940.robot_core.modules.camera_streaming.SelectableMJPEGStreamerModule;
import org.team5940.robot_core.modules.control.procedures.AggregateProcedureModule;
import org.team5940.robot_core.modules.control.procedures.ProcedureModule;
import org.team5940.robot_core.modules.control.procedures.SingleShotSelectableProcedureModule;
import org.team5940.robot_core.modules.control.threads.ThreadModule;
import org.team5940.robot_core.modules.logging.FileLoggerModule;
import org.team5940.robot_core.modules.logging.LoggerModule;
import org.team5940.robot_core.modules.logging.MultipleLoggerModule;
import org.team5940.robot_core.modules.logging.SystemLoggerModule;
import org.team5940.robot_core.modules.sensors.axes.AxisModule;
import org.team5940.robot_core.modules.sensors.axes.ConfigurableHIDAxisModule;
import org.team5940.robot_core.modules.sensors.binary_input.BinaryInputModule;
import org.team5940.robot_core.modules.sensors.binary_input.DigitalBinaryInputModule;
import org.team5940.robot_core.modules.sensors.binary_input.HIDButtonBinaryInputModule;
import org.team5940.robot_core.modules.sensors.binary_input.TogglingBinaryInputModule;
import org.team5940.robot_core.modules.sensors.selectors.BinarySelectorModule;
import org.team5940.robot_core.modules.sensors.selectors.SelectorModule;
import org.team5940.robot_core.modules.testing.TestableModule;
import org.team5940.robot_core.modules.testing.communication.SmartDashboardTestCommunicationModule;
import org.team5940.robot_core.modules.testing.communication.TestCommunicationModule;
import org.team5940.robot_core.modules.testing.runners.MultipleTestRunnerProcedureModule;

import com.ctre.CANTalon;

import edu.wpi.cscore.UsbCamera;
import edu.wpi.cscore.VideoSource;
import edu.wpi.first.wpilibj.DigitalInput;
import edu.wpi.first.wpilibj.DoubleSolenoid;
import edu.wpi.first.wpilibj.GenericHID;
import edu.wpi.first.wpilibj.Joystick;

public class Robot extends RobotModule {
	
	@Override
	protected void initializeRobotModules() throws Exception {
		//LOGGING
		//FILE LOGGING
		LoggerModule fileLogger = LoggerModule.INERT_LOGGER;
		if(RobotConfig.enableFileLog)
			fileLogger = new FileLoggerModule("file_logger", LoggerModule.INERT_LOGGER, RobotConfig.enableVerboseFileLog, true, new File("/home/lvuser/logs.txt"));
		//RIOLOGGING
		LoggerModule rioLogger = LoggerModule.INERT_LOGGER;
		if(RobotConfig.enableRiolog)
			rioLogger = new SystemLoggerModule("rio_logger", LoggerModule.INERT_LOGGER, RobotConfig.enableVerboseRiolog, true);
		//COMBINED LOGGER - might cause performance concerns across threads due to synchronization during module information collection
		LoggerModule logger = new MultipleLoggerModule("logger", LoggerModule.INERT_LOGGER, true, true, new ModuleHashtable<LoggerModule>().chainPut(fileLogger).chainPut(rioLogger));
		this.setRobotLogger(logger);
		
		//TESTABLE
		ModuleHashtable<TestableModule> testable = new ModuleHashtable<>();
		
		//SHIFTING
		logger.log(this, "Initializing Shifter");
		ShifterModule shifter = ShifterModule.INERT_SHIFTER;
		if(RobotConfig.enableShifter) {
			shifter = RobotConfig.isCiabatta ? new DoubleSolenoidShifterModule("shifter", logger, new DoubleSolenoid(9, 6, 7)) : new DoubleSolenoidShifterModule("shifter", logger, new DoubleSolenoid(6, 0, 1));
			testable.put(shifter);
		}
		
		//DRIVETRAIN
		TankDrivetrainModule drivetrain;
		if(RobotConfig.enableDrivetrain) {
			//LEFT MOTORS
			CANTalon frontLeftTalon = new CANTalon(3);
			frontLeftTalon.setInverted(true);
			CANTalon backLeftTalon = new CANTalon(4);
			backLeftTalon.setInverted(true);
			MotorSetModule leftMotorSet = new CANTalonMotorSetModule("left_motor_set", logger, new CANTalon[]{frontLeftTalon, backLeftTalon});
			
			//RIGHT MOTORS
			CANTalon frontRightTalon = new CANTalon(1);
			CANTalon backRightTalon = new CANTalon(2);
			MotorSetModule rightMotorSet = new CANTalonMotorSetModule("right_motor_set", logger, new CANTalon[]{frontRightTalon, backRightTalon});
			
			//DRIVETRAIN
			if(RobotConfig.enableShifter) drivetrain = new SimpleTankDrivetrainModule("drivetrain", logger, leftMotorSet, rightMotorSet, shifter);
			else drivetrain = new SimpleTankDrivetrainModule("drivetrain", logger, leftMotorSet, rightMotorSet);
			testable.put(drivetrain);
		}

		//SCALING
		logger.log(this, "Initializing Scaler");
		MotorSetModule scalerMotorSet = MotorSetModule.INERT_MOTOR_SET;
		if(RobotConfig.enableScaler) {
			CANTalon leftScalerTalon = new CANTalon(7);
			leftScalerTalon.setInverted(true);
			CANTalon rightScalerTalon = new CANTalon(8);
			scalerMotorSet = new ScalerMotorSetModule(logger, new CANTalon[]{leftScalerTalon, rightScalerTalon});
			testable.put((TestableModule) scalerMotorSet);
		}
		
		//CUP
		logger.log(this, "Initializing Cup");
		PistonModule cupPiston = PistonModule.INERT_PISTON;
		if(RobotConfig.enableCup) {
			cupPiston = new DoubleSolenoidPistonModule("cup", logger, new DoubleSolenoid(9, 4, 5));
			testable.put(cupPiston);
		}
		
		//ARM LIMIT SWITCHES
		logger.log(this, "Initializing Limit Switches");
		BinaryInputModule upLimitSwitch = new DigitalBinaryInputModule("up_limit_switch", logger, new DigitalInput(0), true);
		BinaryInputModule downLimitSwitch = new DigitalBinaryInputModule("down_limit_switch", logger, new DigitalInput(1), true);
		testable.chainPut(upLimitSwitch).put(downLimitSwitch);
		
		//ARM MOTORS
		MotorSetModule armMotorSet = MotorSetModule.INERT_MOTOR_SET;
		if(RobotConfig.enableArm) {
			CANTalon armTalon = new CANTalon(5);
			armTalon.setInverted(true);
			armMotorSet = new CANTalonMotorSetModule("arm_motor_set", logger, armTalon);
		}
		
		//ARM
		ArmModule arm = null;
		logger.log(this, "Initializing Arm");
		if(RobotConfig.enableArm) {
			arm = new ArmModule(logger, armMotorSet, cupPiston, downLimitSwitch, upLimitSwitch);
		}
		
		//HUMAN INTERFACES
		//HIDs
		GenericHID driverController = new Joystick(0);
		GenericHID mechanismController;
		if(RobotConfig.isCiabatta) mechanismController = new Joystick(1);
		//ROBOT DIRECTION
		BinaryInputModule directionSwapButton = new HIDButtonBinaryInputModule("dir_swap_button", logger, driverController, 1, false);
		BinaryInputModule robotDirection = new TogglingBinaryInputModule("robot_direction", logger, directionSwapButton, false, true, 100);
		SelectorModule robotDirectionSelector = new BinarySelectorModule("robot_direction_selector", logger, robotDirection);
		testable.chainPut(directionSwapButton).chainPut(robotDirection).chainPut(robotDirectionSelector);
		//DRIVING
		AxisModule forwardAxis;
		AxisModule yawAxis;
		if(RobotConfig.enableDrivetrain) {
			forwardAxis = new ConfigurableHIDAxisModule("forward_axis", logger, driverController, 1, true, 0.05, 2);
			yawAxis = new ConfigurableHIDAxisModule("yaw_axis", logger, driverController, 4, false, 0.05, 2);
			testable.chainPut(forwardAxis).chainPut(yawAxis);
		}
		//SHIFTING
		BinaryInputModule shiftUpButton;
		BinaryInputModule shiftDownButton;
		if(RobotConfig.enableShifter) {
			shiftUpButton = new HIDButtonBinaryInputModule("shift_up_button", logger, driverController, 6);
			shiftDownButton = new HIDButtonBinaryInputModule("shift_down_button", logger, driverController, 5);
			testable.chainPut(shiftUpButton).chainPut(shiftDownButton);
		}
		
		//CAMERAS
		if(RobotConfig.enableOpCams) {
			VideoSource frontCamera = new UsbCamera("front", 0);
			frontCamera.setResolution(320, 240);
			VideoSource backCamera = new UsbCamera("back", 1);
			frontCamera.setResolution(320, 240);
			SelectableMJPEGStreamerModule cameraStream = new SelectableMJPEGStreamerModule("camera_stream", logger, robotDirectionSelector, frontCamera, new VideoSource[]{frontCamera, backCamera});
			ThreadModule cameraThread = new ThreadModule("camera_stream_thread", logger, cameraStream);
			cameraThread.start();
		}
		
		//CONTROL//TODO add configuration checks
		//STANDARD OPCON
		ModuleHashtable<ProcedureModule> opConProcedures = new ModuleHashtable<>();
		if(RobotConfig.enableDrivetrain)
			opConProcedures.put(new DriveUpdateProcedure(logger, drivetrain, forwardAxis, yawAxis, robotDirectionSelector));
		ProcedureModule opConAggregateProcedure = new AggregateProcedureModule("opcon_aggregate_procedure", logger, opConProcedures, true);
		//TESTING
		TestCommunicationModule comms = new SmartDashboardTestCommunicationModule("test_communications", logger, "TESTING:", "RETURN:");
		ProcedureModule testingProcedure = new MultipleTestRunnerProcedureModule("testing_procedure", logger, testable, comms);
		//COMBINED OPCON
		SelectorModule testingSelector = new BinarySelectorModule("testing_selector", logger, directionSwapButton);
		ProcedureModule opConProcedure = new SingleShotSelectableProcedureModule("opcon_procedure", logger, testingSelector, opConAggregateProcedure, new ProcedureModule[]{opConAggregateProcedure, testingProcedure}, true);
		//ROBOT PROCEDURE
		this.createRobotProcedure(logger, ProcedureModule.INERT_PROCEDURE, ProcedureModule.INERT_PROCEDURE, opConProcedure, ProcedureModule.INERT_PROCEDURE);
		
	}

}
