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

    public DcMotor leftDrive = null;
    public DcMotor rightDrive = null;


    public Drive(HardwareMap hardwareMap) {
        this.leftDrive  = hardwareMap.get(DcMotor.class, "left_motor");
        this.rightDrive = hardwareMap.get(DcMotor.class, "right_motor");

        this.leftDrive.setDirection(DcMotorSimple.Direction.FORWARD);
        this.rightDrive.setDirection(DcMotorSimple.Direction.REVERSE);

        this.leftDrive.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
        this.rightDrive.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);

        this.leftDrive.setMode(DcMotor.RunMode.RUN_USING_ENCODER);
        this.rightDrive.setMode(DcMotor.RunMode.RUN_USING_ENCODER);
    };

    public void setPower(double left, double right) {
        this.leftDrive.setPower(left);
        this.rightDrive.setPower(right);
    };

    public void arcadeDrive(double magnitude, double direction) {
        double leftPower = magnitude + direction;
        double rightPower = magnitude - direction;

        // Normalize the values so neither exceed +/- 1.0
        double max = Math.max(Math.abs(leftPower), Math.abs(rightPower));
        if (max > 1.0) {
            leftPower /= max;
            rightPower /= max;
        };

        this.setPower(leftPower, rightPower);
    };

    public void arcadeDrive(double magnitude, double direction, double millimeters) {
        // # Setup
        if (Math.abs(magnitude) < 0.01 && Math.abs(direction) < 0.01) {
            this.setPower(0, 0);
            return;
        };

        double distance = Math.abs(millimeters);
        int travelDirection = (int) Math.signum(magnitude);
        double speed = Math.abs(magnitude);

        // # Handle Point Turns (magnitude is negligible)
        if (speed < 0.01) {
            // 'distance' defines wheel travel for a point turn
            int turnTicks = (int) (distance * COUNTS_PER_MM);
            int leftTicks = (int) (turnTicks * Math.signum(direction));
            int rightTicks = -leftTicks;

            // Set target positions
            this.leftDrive.setTargetPosition(leftDrive.getCurrentPosition() + leftTicks);
            this.rightDrive.setTargetPosition(rightDrive.getCurrentPosition() + rightTicks);

            // Set mode
            this.leftDrive.setMode(DcMotor.RunMode.RUN_TO_POSITION);
            this.rightDrive.setMode(DcMotor.RunMode.RUN_TO_POSITION);

            // Set power for turning, proportional to 'direction'
            double turnPower = Math.min(1.0, Math.abs(direction));
            this.leftDrive.setPower(turnPower);
            this.rightDrive.setPower(turnPower);

            return;
        };

        // # Handle Arc Turns (driving forward/backward with turning)

        // 1. Calculate initial power distribution for each wheel
        double leftPower = speed + direction;
        double rightPower = speed - direction;

        // 2. Scale power values to respect 'speed' as the maximum speed.
        // The wheel with the highest power will move at 'speed', and the other is scaled proportionally.
        double maxAbsPower = Math.max(Math.abs(leftPower), Math.abs(rightPower));
        if (maxAbsPower > speed) {
            double scalingFactor = speed / maxAbsPower;
            leftPower *= scalingFactor;
            rightPower *= scalingFactor;
        };

        // 3. Calculate the distance each wheel needs to travel
        // Wheel distance is proportional to its speed relative to the center's speed.
        double leftDistance = distance * (Math.abs(leftPower) / speed);
        double rightDistance = distance * (Math.abs(rightPower) / speed);

        int leftTicks = (int) (leftDistance * COUNTS_PER_MM) * travelDirection;
        int rightTicks = (int) (rightDistance * COUNTS_PER_MM) * travelDirection;

        // Set target positions
        this.leftDrive.setTargetPosition(leftDrive.getCurrentPosition() + leftTicks);
        this.rightDrive.setTargetPosition(rightDrive.getCurrentPosition() + rightTicks);

        // Set mode
        this.leftDrive.setMode(DcMotor.RunMode.RUN_TO_POSITION);
        this.rightDrive.setMode(DcMotor.RunMode.RUN_TO_POSITION);

        // Set motor power (absolute value for RUN_TO_POSITION)
        // Power is set to the scaled values, ensuring the robot moves at the correct speed.
        this.leftDrive.setPower(Math.abs(leftPower));
        this.rightDrive.setPower(Math.abs(rightPower));
    };
};
