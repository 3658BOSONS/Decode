package org.bosons.decode.Auto;

import com.qualcomm.robotcore.eventloop.opmode.Autonomous;
import com.qualcomm.robotcore.eventloop.opmode.OpMode;

import org.bosons.decode.Modules.Launcher;
import org.bosons.decode.Robot;
import org.bosons.decode.Season;
import org.firstinspires.ftc.vision.VisionPortal;

import java.util.List;

@Autonomous(name = "Auto [Don't move] (Temp)", group = "Decode")
public class AutoNoMove extends OpMode {
	private Robot robot = null;
	private final Launcher<Season.Ball> launcher = null;

	// private VisionPortal portal = null;

	@Override
	public void init() {
		this.robot = new Robot(this.hardwareMap, this.telemetry);

		// - this.webcam = this.robot.topWebcam.request(this.hardwareMap, List.of(Webcam.aprilTagProcessor));
		// - this.portal = this.webcam.getVisionPortal();
	};

	@Override
	public void start() {


	};

	@Override
	public void loop() {
		/*List<AprilTagDetection> currentDetections = Webcam.aprilTagProcessor.getDetections();
		this.telemetry.addData("Detected", currentDetections.size());

		for (AprilTagDetection detection : currentDetections) {
			if (detection.metadata != null) {
				telemetry.addLine(String.format("\n==== (ID %d) %s", detection.id, detection.metadata.name));
				telemetry.addLine(String.format("XYZ %6.1f %6.1f %6.1f  (inch)", detection.ftcPose.x, detection.ftcPose.y, detection.ftcPose.z));
				telemetry.addLine(String.format("PRY %6.1f %6.1f %6.1f  (deg)", detection.ftcPose.pitch, detection.ftcPose.roll, detection.ftcPose.yaw));
				telemetry.addLine(String.format("RBE %6.1f %6.1f %6.1f  (inch, deg, deg)", detection.ftcPose.range, detection.ftcPose.bearing, detection.ftcPose.elevation));
			} else {
				telemetry.addLine(String.format("\n==== (ID %d) Unknown", detection.id));
				telemetry.addLine(String.format("Center %6.0f %6.0f   (pixels)", detection.center.x, detection.center.y));
			};
		};*/
	};

	@Override
	public void stop() {
		// this.webcam.release();
	};
};