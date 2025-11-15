package org.bosons.decode.Modules;

import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.DcMotorSimple;
import com.qualcomm.robotcore.hardware.HardwareMap;

public class Launcher {
	private DcMotor lifter = null;

	public Launcher(HardwareMap hardwareMap) {
		this.lifter = hardwareMap.get(DcMotor.class, "lifter");

		this.lifter.setMode(DcMotor.RunMode.RUN_USING_ENCODER);
		this.lifter.setDirection(DcMotorSimple.Direction.FORWARD);

	};
};
