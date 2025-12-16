package org.bosons.decode.Modules;

import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.DcMotorSimple;
import com.qualcomm.robotcore.hardware.HardwareMap;

public class Drive {
    // Constants based on the 5203 Series Yellow Jacket Planetary Gear Motor (5.2:1)
    public static final double COUNTS_PER_MOTOR_REV = 537.7; // 145.6
    public static final double WHEEL_DIAMETER_MM = 96.0;
    public static final double COUNTS_PER_MM = COUNTS_PER_MOTOR_REV / (WHEEL_DIAMETER_MM * Math.PI);
    // Distance between the center of the wheels
    public static final double TRACK_WIDTH_MM = 270.0;

    public static class Movement {
        public double magnitude;
        public double distance;
        public double direction;
        public double strafe;
    };

    public DcMotor leftFrontMotor = null;
    public DcMotor leftBackMotor = null;

    public DcMotor rightFrontMotor = null;
    public DcMotor rightBackMotor = null;


    public Drive(HardwareMap hardwareMap) {
        this.leftFrontMotor  = hardwareMap.get(DcMotor.class, "left_front_motor");
        this.leftBackMotor  = hardwareMap.get(DcMotor.class, "left_back_motor");
        this.rightFrontMotor = hardwareMap.get(DcMotor.class, "right_front_motor");
        this.rightBackMotor = hardwareMap.get(DcMotor.class, "right_back_motor");

        this.leftFrontMotor.setDirection(DcMotorSimple.Direction.FORWARD);
        this.leftBackMotor.setDirection(DcMotorSimple.Direction.FORWARD);
        this.rightFrontMotor.setDirection(DcMotorSimple.Direction.REVERSE);
        this.rightBackMotor.setDirection(DcMotorSimple.Direction.REVERSE);

        this.leftFrontMotor.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
        this.leftBackMotor.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
        this.rightFrontMotor.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
        this.rightBackMotor.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);

        this.leftFrontMotor.setMode(DcMotor.RunMode.RUN_USING_ENCODER);
        this.leftBackMotor.setMode(DcMotor.RunMode.RUN_USING_ENCODER);
        this.rightFrontMotor.setMode(DcMotor.RunMode.RUN_USING_ENCODER);
        this.rightBackMotor.setMode(DcMotor.RunMode.RUN_USING_ENCODER);
    };

    /**
     * Drives the robot using arcade-style controls for mecanum wheels.
     * @param movement An object containing magnitude (forward/backward), strafe (left/right), and direction (turning).
     */
    public void arcadeDrive(Drive.Movement movement) {
        double y = movement.magnitude; // Forward/Backward.
        double x = movement.strafe;    // Left/Right strafe.
        double rx = movement.direction;  // Rotation.

        // The following formulas are derived based on the motor directions set in the constructor.
        // With the right-side motors reversed, their power calculations must be adjusted.
        double leftFrontPower = y + x + rx;
        double leftBackPower = y - x + rx;
        double rightFrontPower = -y + x + rx;
        double rightBackPower = -y - x + rx;

        // Find the maximum absolute power to use for normalization.
        double maxPower = Math.abs(leftFrontPower);
        maxPower = Math.max(maxPower, Math.abs(leftBackPower));
        maxPower = Math.max(maxPower, Math.abs(rightFrontPower));
        maxPower = Math.max(maxPower, Math.abs(rightBackPower));

        // Normalize all motor powers if any of them exceeds 1.0, preserving the input proportions.
        if (maxPower > 1.0) {
            leftFrontPower /= maxPower;
            leftBackPower /= maxPower;
            rightFrontPower /= maxPower;
            rightBackPower /= maxPower;
        };

        this.leftFrontMotor.setPower(leftFrontPower);
        this.leftBackMotor.setPower(leftBackPower);
        this.rightFrontMotor.setPower(rightFrontPower);
        this.rightBackMotor.setPower(rightBackPower);
    };
};