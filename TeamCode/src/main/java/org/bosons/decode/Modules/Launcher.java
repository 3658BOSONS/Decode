package org.bosons.decode.Modules;

import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.HardwareMap;
import com.qualcomm.robotcore.hardware.CRServo;

import org.firstinspires.ftc.robotcore.external.Telemetry;

import java.util.List;


public class Launcher<P extends Launcher.Projectile> {
	public static abstract class Projectile {

	};

	public static enum Mode {
		TURBO_LAUNCHING,
		LAUNCHING,
		COLLECTING,
		IDLE,
	};

	public static final double LIFTER_POWER = (double) 1 / 1;

	public static final double FLYWHEEL_POWER_LAUNCHING = (double) 1 / 2;
	public static final double FLYWHEEL_POWER_TURBO_LAUNCHING = (double) 1;

	public static final double FLYWHEEL_POWER_COLLECTING = (double) 1 / 3;

	public Launcher.Mode mode = Launcher.Mode.IDLE;

	private DcMotor lifter = null;
	private DcMotor flywheels= null;
	private CRServo pusher = null;
	private Telemetry telemetry = null;

	public List<P> contents;

	public Launcher(HardwareMap hardwareMap, Telemetry telemetry) {
		this.telemetry = telemetry;

		this.lifter = hardwareMap.get(DcMotor.class, "lifter");

		this.lifter.setMode(DcMotor.RunMode.RUN_USING_ENCODER); // ! Never disable
		this.lifter.setDirection(DcMotor.Direction.FORWARD);

		this.pusher = hardwareMap.get(CRServo.class, "pusher");

		this.pusher.setDirection(CRServo.Direction.FORWARD);

		this.flywheels = hardwareMap.get(DcMotor.class, "flywheels");

		this.flywheels.setMode(DcMotor.RunMode.RUN_USING_ENCODER);
		this.flywheels.setDirection(DcMotor.Direction.FORWARD);
	};

	public void setMode(Launcher.Mode mode) {
		this.mode = mode;

		switch (this.mode) {
			case TURBO_LAUNCHING: {
				this.flywheels.setPower(Launcher.FLYWHEEL_POWER_TURBO_LAUNCHING);
				break;
			}
			case LAUNCHING: {
				this.flywheels.setPower(Launcher.FLYWHEEL_POWER_LAUNCHING);
				break;
			}
			case COLLECTING: {
				this.flywheels.setMode(DcMotor.RunMode.RUN_USING_ENCODER);
				this.flywheels.setPower(- Launcher.FLYWHEEL_POWER_COLLECTING);
				break;
			}
			case IDLE: {
				this.flywheels.setMode(DcMotor.RunMode.RUN_USING_ENCODER);
				this.flywheels.setPower(0);
				break;
			}
		};

		this.telemetry.addData("Mode", this.mode);
	};

	public void launch() {
		if (this.mode != Launcher.Mode.LAUNCHING) {
			return;
		};

		// TODO(Brendan, Edmond): Implement
	};

	public void push(double position) {
		// Push the hand.
		this.pusher.setPower(position);
	};

	public void aimPower(double power) {
		this.lifter.setPower(power * Launcher.LIFTER_POWER);
	};
};