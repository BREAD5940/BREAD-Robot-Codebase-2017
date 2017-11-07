package org.team5940.robot_codebase_2017.robot;

/**
 * Stores configuration booleans for this year's codebase.
 * @author David Boles
 *
 */
public class RobotConfig {
	
	//ROBOT TYPE
	/**
	 * Stores whether this is Ciabatta or not, automatically disables features when testing on Focaccia.
	 */
	public static final boolean isCiabatta = true;//DEFAULT: true
	
	//LOGGING
	/**
	 * Configures whether the robot should log to the Riolog.
	 */
	public static final boolean enableRiolog = false;//DEFAULT: false
	
	/**
	 * Configures whether the robot should have a verbose Riologging.
	 */
	public static final boolean enableVerboseRiolog = true;//DEFAULT: true
	
	/**
	 * Configures whether the robot should log to files.
	 */
	public static final boolean enableFileLog = false;//DEFAULT: true
	
	/**
	 * Configures whether the robot should have a verbose file logging.
	 */
	public static final boolean enableVerboseFileLog = true;//DEFAULT: true
	
	//CONTROL
	/**
	 * Configures whether the robot has autonomous.
	 */
	public static final boolean enableAuto = true;//DEFAULT: true
	
	/**
	 * Configures whether the robot has advanced autonomous, overriden by {@link RobotConfig#enableAuto} and {@link RobotConfig#isCiabatta}.
	 */
	public static final boolean enableAdvancedAuto = enableAuto && isCiabatta && false;//DEFAULT: true

	/**
	 * Configures whether the robot has operator control.
	 */
	public static final boolean enableOpCon = true;//DEFAULT: true

	/**
	 * Configures whether the robot has testing, overridden by {@link RobotConfig#enableOpCon}.
	 */
	public static final boolean enableTesting = enableOpCon && true;//DEFAULT: true
	
	//SUBSYSTEMS
	/**
	 * Configures whether the robot should control the drivetrain.
	 */
	public static final boolean enableDrivetrain = true;//DEFAULT: true
	
	/**
	 * Configures whether the robot should control the arm, overridden by {@link RobotConfig#isCiabatta}.
	 */
	public static final boolean enableArm = isCiabatta && true;//DEFAULT: true
	
	/**
	 * Configures whether the robot should control the intake, overridden by {@link RobotConfig#isCiabatta}.
	 */
	public static final boolean enableIntake = isCiabatta && true;//DEFAULT: true
	
	/**
	 * Configures whether the robot should control the scaler, overridden by {@link RobotConfig#isCiabatta}.
	 */
	public static final boolean enableScaler = isCiabatta && true;//DEFAULT: true
	
	/**
	 * Configures whether the robot should stream operator cameras.
	 */
	public static final boolean enableOpCams = false;//DEFAULT: true
	
	//PNEUMATICS
	/**
	 * Configures whether the robot should have pneumatics.
	 */
	public static final boolean enablePneumatics = true;//DEFAULT: true
	
	/**
	 * Configures whether the robot should have shifting, overridden by {@link RobotConfig#enableDrivetrain} and {@link RobotConfig#enablePneumatics}.
	 */
	public static final boolean enableShifter = enableDrivetrain && enablePneumatics && true;//DEFAULT: true
	
	/**
	 * Configures whether the robot should control the cup, overridden by {@link RobotConfig#enablePneumatics}, and {@link RobotConfig#enableArm}.
	 */
	public static final boolean enableCup = enablePneumatics && enableArm && true;//DEFAULT: true
	
	
	//MOTORS
	/**
	 * Configures whether the robot should control the arm motor(s), overridden by {@link RobotConfig#enableArm}.
	 */
	public static final boolean enableArmMotors = enableArm && true;//DEFAULT: true
	
	
}
