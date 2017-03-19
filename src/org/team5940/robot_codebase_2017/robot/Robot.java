package org.team5940.robot_codebase_2017.robot;

import java.io.File;

import org.team5940.robot_codebase_2017.modules.ArmModule;
import org.team5940.robot_codebase_2017.modules.ScalerMotorSetModule;
import org.team5940.robot_codebase_2017.modules.auto_procedures.BadCenterGearAutoProcedureModule;
import org.team5940.robot_codebase_2017.modules.auto_procedures.ForwardAutoProcedureModule;
import org.team5940.robot_codebase_2017.modules.auto_procedures.TurningAutoProcedureModule;
import org.team5940.robot_codebase_2017.modules.opcon_procedures.ArmUpdateProcedureModule;
import org.team5940.robot_codebase_2017.modules.opcon_procedures.DriveUpdateProcedureModule;
import org.team5940.robot_codebase_2017.modules.opcon_procedures.RollerUpdateProcedureModule;
import org.team5940.robot_codebase_2017.modules.opcon_procedures.ShifterUpdateProcedureModule;
import org.team5940.robot_core.modules.ModuleHashtable;
import org.team5940.robot_core.modules.RobotModule;
import org.team5940.robot_core.modules.actuators.motor_sets.CANTalonMotorSetModule;
import org.team5940.robot_core.modules.actuators.motor_sets.MotorSetModule;
import org.team5940.robot_core.modules.actuators.motor_sets.TestableCANTalonMotorSetModule;
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
import org.team5940.robot_core.modules.sensors.axes.HIDAxisModule;
import org.team5940.robot_core.modules.sensors.binary_input.BinaryInputModule;
import org.team5940.robot_core.modules.sensors.binary_input.DigitalBinaryInputModule;
import org.team5940.robot_core.modules.sensors.binary_input.HIDButtonBinaryInputModule;
import org.team5940.robot_core.modules.sensors.binary_input.TogglingBinaryInputModule;
import org.team5940.robot_core.modules.sensors.selectors.BinarySelectorModule;
import org.team5940.robot_core.modules.sensors.selectors.SelectorModule;
import org.team5940.robot_core.modules.sensors.selectors.SmartDashboardSelectorModule;
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
			fileLogger = new FileLoggerModule("file_logger", LoggerModule.INERT_LOGGER, RobotConfig.enableVerboseFileLog, true, new File("/media/sda1/log.txt"));
		//RIOLOGGING
		LoggerModule rioLogger = LoggerModule.INERT_LOGGER;
		if(RobotConfig.enableRiolog)
			rioLogger = new SystemLoggerModule("rio_logger", LoggerModule.INERT_LOGGER, RobotConfig.enableVerboseRiolog, true);
		//COMBINED LOGGER - might cause performance concerns across threads due to synchronization during module information collection
		LoggerModule logger = new MultipleLoggerModule("logger", LoggerModule.INERT_LOGGER, true, true, new ModuleHashtable<LoggerModule>().chainPut(fileLogger).chainPut(rioLogger));
		this.setRobotLogger(logger);
		
		//TESTABLE
		ModuleHashtable<TestableModule> testable = new ModuleHashtable<>();
		
		//SHIFTER
		System.out.println("SHIFTER");
		ShifterModule shifter;
		if(RobotConfig.enableShifter) {
			shifter = RobotConfig.isCiabatta ? new DoubleSolenoidShifterModule("shifter", logger, new DoubleSolenoid(9, 6, 7)) : new DoubleSolenoidShifterModule("shifter", logger, new DoubleSolenoid(6, 0, 1));
			testable.put(shifter);
		}
		
		//DRIVETRAIN
		System.out.println("DRIVETRAIN");
		TankDrivetrainModule drivetrain;
		if(RobotConfig.enableDrivetrain) {
			//LEFT MOTORS
			System.out.println("LEFT MOTORS");
			CANTalon frontLeftTalon = new CANTalon(3);
			frontLeftTalon.setInverted(true);
			CANTalon backLeftTalon = new CANTalon(4);
			backLeftTalon.setInverted(true);
			MotorSetModule leftMotorSet = new CANTalonMotorSetModule("left_motor_set", logger, new CANTalon[]{frontLeftTalon, backLeftTalon});
			
			//RIGHT MOTORS
			System.out.println("RIGHT MOTORS");
			CANTalon frontRightTalon = new CANTalon(1);
			CANTalon backRightTalon = new CANTalon(2);
			MotorSetModule rightMotorSet = new CANTalonMotorSetModule("right_motor_set", logger, new CANTalon[]{frontRightTalon, backRightTalon});
			
			//DRIVETRAIN
			System.out.println("DRIVETRAIN");
			if(RobotConfig.enableShifter) drivetrain = new SimpleTankDrivetrainModule("drivetrain", logger, leftMotorSet, rightMotorSet, shifter);
			else drivetrain = new SimpleTankDrivetrainModule("drivetrain", logger, leftMotorSet, rightMotorSet);
			testable.put(drivetrain);
		}

		//SCALER
		System.out.println("SCALER");
		MotorSetModule scalerMotorSet;
		if(RobotConfig.enableScaler) {
			CANTalon leftScalerTalon = new CANTalon(7);
			leftScalerTalon.setInverted(true);
			CANTalon rightScalerTalon = new CANTalon(8);
			scalerMotorSet = new ScalerMotorSetModule(logger, new CANTalon[]{leftScalerTalon, rightScalerTalon});
			testable.put((TestableModule) scalerMotorSet);
		}
		
		//INTAKE
		System.out.println("INTAKE");
		MotorSetModule intakeMotorSetModule;
		if(RobotConfig.enableIntake) {
			CANTalon intakeTalon = new CANTalon(6);
			intakeTalon.setInverted(true);
			intakeMotorSetModule = new TestableCANTalonMotorSetModule("intake_motor_set", logger, intakeTalon);
			testable.chainPut((TestableModule) intakeMotorSetModule);
		}
		
		//CUP
		System.out.println("CUP");
		PistonModule cupPiston;
		if(RobotConfig.enableCup) {
			cupPiston = new DoubleSolenoidPistonModule("cup", logger, new DoubleSolenoid(9, 4, 5));
			testable.put(cupPiston);
		}
		
		//ARM LIMIT SWITCHES
		System.out.println("ARM LIMIT SWITCHES");
		BinaryInputModule upLimitSwitch = new DigitalBinaryInputModule("up_limit_switch", logger, new DigitalInput(0), true);
		BinaryInputModule downLimitSwitch = new DigitalBinaryInputModule("down_limit_switch", logger, new DigitalInput(1), true);
		testable.chainPut(upLimitSwitch).put(downLimitSwitch);
		
		//ARM MOTORS
		System.out.println("ARM MOTORS");
		MotorSetModule armMotorSet;
		if(RobotConfig.enableArm) {
			CANTalon armTalon = new CANTalon(5);
			armTalon.setInverted(true);
			armMotorSet = new CANTalonMotorSetModule("arm_motor_set", logger, armTalon);
		}
		
		//ARM
		System.out.println("ARM");
		ArmModule arm;
		logger.log(this, "Initializing Arm");
		if(RobotConfig.enableArm) {
			arm = new ArmModule(logger, armMotorSet, cupPiston, downLimitSwitch, upLimitSwitch);
			testable.put(arm);
		}
		
		//HUMAN INTERFACES
		System.out.println("HUMAN INTERFACES");
		//HIDs
		System.out.println("HIDs");
		GenericHID driverController = new Joystick(0);
		GenericHID mechanismController;
		if(RobotConfig.isCiabatta) mechanismController = new Joystick(1);
		//ROBOT DIRECTION
		System.out.println("ROBOT DIRECTION");
		BinaryInputModule directionSwapButton = new HIDButtonBinaryInputModule("dir_swap_button", logger, driverController, 1, false);
		BinaryInputModule robotDirection = new TogglingBinaryInputModule("robot_direction", logger, directionSwapButton, false, true, 100);
		SelectorModule robotDirectionSelector = new BinarySelectorModule("robot_direction_selector", logger, robotDirection);
		testable.chainPut(directionSwapButton).chainPut(robotDirection).chainPut(robotDirectionSelector);
		//DRIVING
		System.out.println("DRIVING");
		AxisModule forwardAxis;
		AxisModule yawAxis;
		if(RobotConfig.enableDrivetrain) {
			forwardAxis = new ConfigurableHIDAxisModule("forward_axis", logger, driverController, 1, true, 0.05, 2);
			yawAxis = new ConfigurableHIDAxisModule("yaw_axis", logger, driverController, 4, false, 0.05, 2);
			testable.chainPut(forwardAxis).chainPut(yawAxis);
		}
		//SHIFTING
		System.out.println("SHIFTING");
		BinaryInputModule shiftUpButton;
		BinaryInputModule shiftDownButton;
		if(RobotConfig.enableShifter) {
			shiftUpButton = new HIDButtonBinaryInputModule("shift_up_button", logger, driverController, 6);
			shiftDownButton = new HIDButtonBinaryInputModule("shift_down_button", logger, driverController, 5);
			testable.chainPut(shiftUpButton).chainPut(shiftDownButton);
		}
		//INTAKE
		System.out.println("INTAKE");
		AxisModule intakeAxis;
		if(RobotConfig.enableIntake) {
			intakeAxis = new ConfigurableHIDAxisModule("intake_axis", logger, mechanismController, 1, true, 0.1, 1);
			testable.put(intakeAxis);
		}
		//SCALER
		System.out.println("SCALER");
		AxisModule scalerAxis;
		if(RobotConfig.enableScaler) {
			scalerAxis = new HIDAxisModule("scaler_axis", logger, mechanismController, 3, false);
			testable.put(scalerAxis);
		}
		//ARM
		System.out.println("ARM");
		BinaryInputModule armUpButton;
		BinaryInputModule armDownButton;
		if(RobotConfig.enableArm) {
			armUpButton = new HIDButtonBinaryInputModule("arm_up_button", logger, mechanismController, 4);
			armDownButton = new HIDButtonBinaryInputModule("arm_down_button", logger, mechanismController, 1);
			testable.chainPut(armUpButton).chainPut(armDownButton);
		}
		//CUP
		System.out.println("CUP");
		BinaryInputModule cupExtendedButton;
		BinaryInputModule cupContractedButton;
		if(RobotConfig.enableCup) {
			cupExtendedButton = new HIDButtonBinaryInputModule("cup_extended_button", logger, mechanismController, 6);
			cupContractedButton = new HIDButtonBinaryInputModule("cup_contracted_button", logger, mechanismController, 5);
			testable.chainPut(cupExtendedButton).chainPut(cupContractedButton);
		}
		
		//AUTO SELECTOR
		System.out.println("AUTO SELECTOR");
		SelectorModule autoSelector;
		if(RobotConfig.enableAuto) {
			autoSelector = new SmartDashboardSelectorModule("auto_selector", logger, new String[]{"None", "Forward", "Bad Center", "Turn Right", "Turn Left"}, 1);
			testable.put(autoSelector);
		}
		
		//OPERATOR CAMERAS
		System.out.println("OPERATOR CAMERAS");
		if(RobotConfig.enableOpCams) {
			VideoSource frontCamera = new UsbCamera("front", 1);
			frontCamera.setResolution(320, 240);
			frontCamera.setFPS(15);
			VideoSource backCamera = new UsbCamera("back", 0);
			backCamera.setResolution(320, 240);
			backCamera.setFPS(15);
			SelectableMJPEGStreamerModule cameraStream = new SelectableMJPEGStreamerModule("camera_stream", logger, robotDirectionSelector, frontCamera, new VideoSource[]{frontCamera, backCamera});
			ThreadModule cameraThread = new ThreadModule("camera_stream_thread", logger, cameraStream);
			cameraThread.start();
		}
		
		//CONTROL
		System.out.println("CONTROL");
		//STANDARD OPCON
		System.out.println("STANDARD OPCON");
		ModuleHashtable<ProcedureModule> opConProcedures = new ModuleHashtable<>();
		if(RobotConfig.enableDrivetrain)
			opConProcedures.put(new DriveUpdateProcedureModule(logger, drivetrain, forwardAxis, yawAxis, robotDirectionSelector));
		if(RobotConfig.enableShifter)
			opConProcedures.put(new ShifterUpdateProcedureModule(logger, shifter, shiftUpButton, shiftDownButton));
		if(RobotConfig.enableIntake)
			opConProcedures.put(new RollerUpdateProcedureModule("intake", logger, intakeMotorSetModule, intakeAxis, false));
		if(RobotConfig.enableScaler)
			opConProcedures.put(new RollerUpdateProcedureModule("scaler", logger, scalerMotorSet, scalerAxis, true));
		if(RobotConfig.enableArm) {
			if(RobotConfig.enableCup)
				opConProcedures.put(new ArmUpdateProcedureModule(logger, arm, armUpButton, armDownButton, cupExtendedButton, cupContractedButton));
			else
				opConProcedures.put(new ArmUpdateProcedureModule(logger, arm, armUpButton, armDownButton, BinaryInputModule.INERT_BINARY_INPUT, BinaryInputModule.INERT_BINARY_INPUT));
		}
		ProcedureModule opConAggregateProcedure = new AggregateProcedureModule("opcon_aggregate_procedure", logger, opConProcedures, true);
		//TESTING
		System.out.println("TESTING");
		TestCommunicationModule comms;
		ProcedureModule testingProcedure;
		SelectorModule testingSelector;
		if(RobotConfig.enableTesting) {
			comms = new SmartDashboardTestCommunicationModule("test_communications", logger, "TESTING:", "RETURN:");
			testingProcedure = new MultipleTestRunnerProcedureModule("testing_procedure", logger, testable, comms);
			testingSelector = new BinarySelectorModule("testing_selector", logger, directionSwapButton);
			testable.put(testingSelector);
		}
		System.out.println("COMBINED OPCON");
		//COMBINED OPCON
		ProcedureModule opConProcedure;
		if(RobotConfig.enableTesting)
			opConProcedure = new SingleShotSelectableProcedureModule("opcon_procedure", logger, testingSelector, opConAggregateProcedure, new ProcedureModule[]{opConAggregateProcedure, testingProcedure}, true);
		else
			opConProcedure = opConAggregateProcedure;
		//AUTO
		ProcedureModule autoProcedure = ProcedureModule.INERT_PROCEDURE;
		if(RobotConfig.enableAuto) {
			ProcedureModule forwardAutoProcedure = new ForwardAutoProcedureModule(logger, drivetrain);
			ProcedureModule badCenter = new BadCenterGearAutoProcedureModule(logger, drivetrain, arm);
			ProcedureModule rightAutoProcedure = new TurningAutoProcedureModule(logger, drivetrain, arm, true);
			ProcedureModule leftAutoProcedure = new TurningAutoProcedureModule(logger, drivetrain, arm, false);
			autoProcedure = new SingleShotSelectableProcedureModule("auto_procedure", logger, autoSelector, ProcedureModule.INERT_PROCEDURE, new ProcedureModule[]{ProcedureModule.INERT_PROCEDURE, forwardAutoProcedure, badCenter, rightAutoProcedure, leftAutoProcedure}, true);
		}
		//ROBOT PROCEDURE
		System.out.println("ROBOT PROCEDURE");
		this.createRobotProcedure(logger, ProcedureModule.INERT_PROCEDURE, autoProcedure, opConProcedure, ProcedureModule.INERT_PROCEDURE);
//		this.setRobotProcedure(new AbstractProcedureModule("arm_tester", new ModuleHashtable<>(), logger) {
//			
//			@Override
//			protected boolean doProcedureUpdate() throws Exception {
//				this.logger.log(this, "Arm State", arm.getCurrentState());
//				return false;
//			}
//			
//			@Override
//			protected void doProcedureStart() throws Exception {
//				// TODO Auto-generated method stub
//				
//			}
//			
//			@Override
//			protected void doProcedureClean() throws Exception {
//				// TODO Auto-generated method stub
//				
//			}
//		});
	}

}
