package org.firstinspires.ftc.teamcode.Subsystems;

import com.qualcomm.robotcore.hardware.DcMotorEx;
import com.qualcomm.robotcore.hardware.HardwareMap;
import com.qualcomm.robotcore.hardware.DcMotorSimple;
import org.firstinspires.ftc.teamcode.Utils.Library.Motor.MotorGroup;

public class Drivetrain {

    // Import global definitions
    public DcMotorEx frontLeft, backLeft, frontRight, backRight;
    public MotorGroup leftSide, rightSide;

    // ======================================================
    // Define the Drivetrain Motor Group
    // ======================================================
    public Drivetrain(HardwareMap hMap) {
        // ======================================================
        // Left Side of Drivetrain
        // ======================================================

        // Define the left drivetrain motors
        frontLeft = hMap.get(DcMotorEx.class, "frontLeft");
        backLeft = hMap.get(DcMotorEx.class, "backLeft");

        // Define the left side into a motor group
        leftSide = new MotorGroup(
                frontLeft,
                backLeft,
                DcMotorSimple.Direction.REVERSE,
                DcMotorSimple.Direction.REVERSE
        );

        // ======================================================
        // Right Side of Drivetrain
        // ======================================================

        // Define the right drivetrain motors
        frontRight = hMap.get(DcMotorEx.class, "frontRight");
        backRight = hMap.get(DcMotorEx.class, "backRight");

        // Define the right side into a motor group
        rightSide = new MotorGroup(
                frontRight,
                backRight,
                DcMotorSimple.Direction.FORWARD,
                DcMotorSimple.Direction.FORWARD
        );
    }
}