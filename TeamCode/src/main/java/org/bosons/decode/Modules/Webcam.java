package org.bosons.decode.Modules;

import com.qualcomm.robotcore.hardware.HardwareMap;

import org.firstinspires.ftc.robotcore.external.hardware.camera.CameraName;
import org.firstinspires.ftc.vision.VisionPortal;
import org.firstinspires.ftc.vision.VisionProcessor;

import org.firstinspires.ftc.vision.apriltag.AprilTagProcessor;

import java.util.List;

import java.util.concurrent.atomic.AtomicReference;

public class Webcam {
	public static class Reference {
		protected VisionPortal visionPortal;

		public Reference(CameraName camera, List<VisionProcessor> processorList) {
			VisionPortal.Builder builder = new VisionPortal.Builder();
			builder.setCamera(camera);

			for (VisionProcessor processor : processorList) {
				builder.addProcessor(processor);
			};

			this.visionPortal = builder.build();
		};

		public VisionPortal getVisionPortal() {
			return this.visionPortal;
		};

		public void release() {
			this.visionPortal.close();
			this.visionPortal = null;
		};
	};

	public static final AprilTagProcessor aprilTagProcessor = new AprilTagProcessor.Builder()
			.build();

	private final AtomicReference<Webcam.Reference> reference = new AtomicReference<>();

	protected final String name;

	public Webcam(String name) {
		this.name = name;

		this.reference.set(null);
	};

	public Webcam.Reference request(HardwareMap hardwareMap, List<VisionProcessor> processorList) {
		if (this.reference.get() != null) {
			return this.reference.get();
		};

		CameraName camera = hardwareMap.get(CameraName.class, this.name);
		Webcam.Reference reference = new Webcam.Reference(camera, processorList);
		this.reference.set(reference);

		return reference;
	};

	public void free() {
		this.reference.get().release();
		this.reference.set(null);
	};
};
