package org.firstinspires.ftc.teamcode.Subsystems;

import com.qualcomm.robotcore.hardware.DcMotorEx;
import com.qualcomm.robotcore.hardware.HardwareMap;
import com.qualcomm.robotcore.hardware.DcMotorSimple;
import org.firstinspires.ftc.teamcode.Commands.SixWheelCMD;
import org.firstinspires.ftc.teamcode.Utils.Library.Motor.MotorGroup;

public class Drivetrain {
    public DcMotorEx frontLeft, backLeft, frontRight, backRight;
    public MotorGroup leftSide, rightSide;
    public Imu imu;

    SixWheelCMD sixWheelCMD;

    // CPR (counts per motor revolution) calculations
    static final double COUNTS_PER_MOTOR_REV = 28.0;
    static final double DRIVE_GEAR_REDUCTION = 29.7;
    static final double COUNTS_PER_WHEEL_REV = COUNTS_PER_MOTOR_REV * DRIVE_GEAR_REDUCTION;

    // RPM calculations
    static final double MOTOR_FREE_SPEED = 6000;
    static final double THEORETICAL_RPM = MOTOR_FREE_SPEED / DRIVE_GEAR_REDUCTION;

    public Drivetrain(HardwareMap hMap) {
        // We are defining the motors here:
        // Left side of the robot
        frontLeft = hMap.get(DcMotorEx.class, "frontLeft");
        backLeft = hMap.get(DcMotorEx.class, "backLeft");
        leftSide = new MotorGroup(
                frontLeft,
                backLeft,
                DcMotorSimple.Direction.REVERSE,
                DcMotorSimple.Direction.REVERSE
        );

        // Right side of the robot
        frontRight = hMap.get(DcMotorEx.class, "frontRight");
        backRight = hMap.get(DcMotorEx.class, "backRight");
        rightSide = new MotorGroup(
                frontRight,
                backRight,
                DcMotorSimple.Direction.FORWARD,
                DcMotorSimple.Direction.FORWARD
        );

        sixWheelCMD = new SixWheelCMD();
    }

    // --- Arcade Drive Constructors ---
    public void arcadeDrive(float forward, float turn) {
        sixWheelCMD.setMotors(leftSide, rightSide, forward - turn, forward + turn);
    }

    public void arcadeDrive(DcMotorEx leftSide, DcMotorEx rightSide, float forward, float turn) {
        sixWheelCMD.setMotors(leftSide, rightSide, forward - turn, forward + turn);
    }

    public void arcadeDrive(MotorGroup leftSide, MotorGroup rightSide, float forward, float turn) {
        sixWheelCMD.setMotors(leftSide, rightSide, forward - turn, forward + turn);
    }

    // --- Tank Drive Constructors ---
    public void tankDrive(DcMotorEx leftSide, DcMotorEx rightSide,float left, float right) {
        sixWheelCMD.setMotors(leftSide, rightSide, left, right);
    }

    public void tankDrive(MotorGroup leftSide, MotorGroup rightSide,float left, float right) {
        sixWheelCMD.setMotors(leftSide, rightSide, left, right);
    }

    public void mecanumDrive(double x, double y, double turn) {
        double robotHeading = imu.getRobotHeading();

        //Rotate the movement direction counter to the bot's rotation
        double driveRotation = (x) * Math.sin(-robotHeading) + (y) * Math.cos(-robotHeading);
        double strafeRotation = (x) * Math.cos(-robotHeading) - (y) * Math.sin(-robotHeading);

        double maxMotorPower = Math.abs(driveRotation) + Math.abs(strafeRotation) + Math.abs(x);
        double denominator = Math.max(maxMotorPower, 1);
        // If this wasn't here the robot would not stay straight, it actually has some weird
        // behavior where it tries to point at the center of the field. Comment out to see.

        frontLeft.setPower((driveRotation + strafeRotation + turn) / denominator);
        frontRight.setPower((driveRotation - strafeRotation - turn) / denominator);
        backLeft.setPower((driveRotation - strafeRotation + turn) / denominator);
        backRight.setPower((driveRotation + strafeRotation - turn) / denominator);
        // Don't use motor groups when dealing with mecanum. It literally defeats the purpose of
        // mecanum (strafing)
    }

}