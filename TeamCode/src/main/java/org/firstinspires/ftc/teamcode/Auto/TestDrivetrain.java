package org.firstinspires.ftc.teamcode.Auto;

import com.qualcomm.robotcore.eventloop.opmode.Autonomous;
import org.firstinspires.ftc.teamcode.Subsystems.Drivetrain;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;

@Autonomous
public class TestDrivetrain extends LinearOpMode {

    Drivetrain drivetrain;

    @Override
    public void runOpMode() throws InterruptedException{
        drivetrain = new Drivetrain(hardwareMap);
        waitForStart();

        while (!gamepad1.a && !isStopRequested()) {
            // watch the motors on the drive train, and they should all spin forward, if they don't
            // then you need to reverse them if they don't spin at all then you need to fix the
            // hardware issue

            // spins the all the drivetrain motors in a positive direction
            // NOTE: they all should spin at the same power as well. If they do not check the
            // voltage on the driver station and make sure the motors are configured correctly

            drivetrain.arcadeDrive(1,1);
        }
        // if you press the controller button a then you stop all motors
        drivetrain.arcadeDrive(0,0);
    }
}