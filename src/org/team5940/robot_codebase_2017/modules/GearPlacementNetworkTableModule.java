package org.team5940.robot_codebase_2017.modules;

import org.team5940.robot_core.modules.AbstractModule;
import org.team5940.robot_core.modules.Module;
import org.team5940.robot_core.modules.ModuleHashtable;
import org.team5940.robot_core.modules.logging.LoggerModule;
import org.team5940.robot_core.modules.network_tables.DoubleNetworkTableModule;

import edu.wpi.first.wpilibj.networktables.NetworkTable;

public class GearPlacementNetworkTableModule extends AbstractModule implements DoubleNetworkTableModule {
	
	private final NetworkTable table;
	
	public GearPlacementNetworkTableModule(String name, LoggerModule logger)
			throws IllegalArgumentException {
		super(name, new ModuleHashtable<Module>(), logger);
		table = NetworkTable.getTable("Vision");
		this.logger.logInitialization(this, GearPlacementNetworkTableModule.class);
	}
	
	
	/**
	 * Gives values in order of timestamp, angle, distance then peg angle. 
	 * @return
	 */
	@Override
	public double[] getDoubles() {
		double[] values = new double[4];
		values[0] = table.getNumber("timestamp", 0);
		values[1] = table.getNumber("angle", 0);
		values[2] = table.getNumber("distance", 0);
		values[3] = table.getNumber("peg angle", 0);
		return values;
	}

}
