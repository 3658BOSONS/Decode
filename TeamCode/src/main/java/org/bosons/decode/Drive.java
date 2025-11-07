package org.bosons.decode;

import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.DcMotorSimple;
import com.qualcomm.robotcore.hardware.HardwareMap;

public class Drive {
    public DcMotor leftDrive = null;
    public DcMotor rightDrive = null;

    public Drive(HardwareMap hardwareMap) {
        this.leftDrive  = hardwareMap.get(DcMotor.class, "left_motor");
        this.rightDrive = hardwareMap.get(DcMotor.class, "right_motor");

        this.leftDrive.setDirection(DcMotorSimple.Direction.FORWARD);
        this.rightDrive.setDirection(DcMotorSimple.Direction.REVERSE);

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

        setPower(leftPower, rightPower);
    };
};