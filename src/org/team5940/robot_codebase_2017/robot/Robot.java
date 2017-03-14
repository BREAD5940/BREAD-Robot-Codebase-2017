package org.team5940.robot_codebase_2017.robot;

import java.io.File;

import org.team5940.robot_core.modules.ModuleHashtable;
import org.team5940.robot_core.modules.RobotModule;
import org.team5940.robot_core.modules.actuators.motor_sets.CANTalonMotorSetModule;
import org.team5940.robot_core.modules.actuators.motor_sets.MotorSetModule;
import org.team5940.robot_core.modules.actuators.shifters.DoubleSolenoidShifterModule;
import org.team5940.robot_core.modules.actuators.shifters.ShifterModule;
import org.team5940.robot_core.modules.logging.FileLoggerModule;
import org.team5940.robot_core.modules.logging.LoggerModule;
import org.team5940.robot_core.modules.logging.MultipleLoggerModule;
import org.team5940.robot_core.modules.logging.SystemLoggerModule;
import org.team5940.robot_core.modules.testing.TestableModule;

import com.ctre.CANTalon;

import edu.wpi.first.wpilibj.DoubleSolenoid;

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

		//SCALING
		logger.log(this, "Initializing Scaler");
		MotorSetModule scalerMotors = MotorSetModule.INERT_MOTOR_SET;
		if(RobotConfig.enableScaler) {
			CANTalon leftScalerTalon = new CANTalon(7);
			leftScalerTalon.setInverted(true);
			CANTalon rightScalerTalon = new CANTalon(8);
			scalerMotors = new CANTalonMotorSetModule("scaler_motors", logger, new CANTalon[]{leftScalerTalon, rightScalerTalon});
			//testable.put(scalerMotors); TODO Replace /\ with custom Ciabatta version
		}
	}

}
