package org.firstinspires.ftc.teamcode.Subsystems;

import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.DcMotorEx;
import com.qualcomm.robotcore.hardware.HardwareMap;
import com.qualcomm.robotcore.hardware.DcMotorSimple;
import org.firstinspires.ftc.teamcode.Utils.Library.Motor.MotorGroup;

public class Drivetrain {
    public DcMotorEx frontLeft, backLeft, frontRight, backRight;
    public MotorGroup leftSide, rightSide;

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
    }

}