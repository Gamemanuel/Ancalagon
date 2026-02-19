package org.firstinspires.ftc.teamcode.DriverControl.Testing;

import com.acmerobotics.dashboard.FtcDashboard;
import com.acmerobotics.dashboard.config.Config;
import org.firstinspires.ftc.teamcode.Utils.Robot;
import org.firstinspires.ftc.teamcode.Utils.Alliance;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.acmerobotics.dashboard.telemetry.MultipleTelemetry;
import org.firstinspires.ftc.teamcode.Subsystems.ShooterSubsystem;

@Config
@TeleOp(name = "Shooter Tuning", group = "Tuning")
public class ShooterTuning extends LinearOpMode {

    Robot robot;

    // Tuning variable visible in Dashboard
    public static double TESTING_TARGET_RPM = 1500;
    public static Alliance TEST_ALLIANCE = Alliance.RED;

    @Override
    public void runOpMode() {
        // Connect to Dashboard Telemetry
        telemetry = new MultipleTelemetry(telemetry, FtcDashboard.getInstance().getTelemetry());

        // Init Subsystems
        robot = new Robot(hardwareMap, TEST_ALLIANCE);

        waitForStart();

        while (opModeIsActive()) {
            // run the ll loop every loop
            robot.ll.periodic();

            // 1. Set the target velocity from the Dashboard variable
            robot.shooter.setTargetVelocity(TESTING_TARGET_RPM);
            robot.intake.intake.setPower(-1);

            robot.turretAuto.faceAprilTag(.5, TEST_ALLIANCE);

            // 2. Run the Periodic loop (Calculates PID + Feedforward)
            robot.shooter.periodic();

            // 3. Telemetry for Graphing and Tuning
            telemetry.addData("Target Velocity", TESTING_TARGET_RPM);
//            telemetry.addData("Average Velocity", robot.shooter.getCurrentVelocity());
//            telemetry.addData("Leader Velocity", robot.shooter.getLeaderVelocity());
//            telemetry.addData("Follower Velocity", robot.shooter.getFollowerVelocity());
            telemetry.addData("Velocity Error", robot.shooter.getVelocityError());

            // Show the tuning parameters
            telemetry.addData("kV", ShooterSubsystem.kV);
//            telemetry.addData("kP", ShooterSubsystem.kP);
//            telemetry.addData("kI", ShooterSubsystem.kI);
//            telemetry.addData("kD", ShooterSubsystem.kD);



            telemetry.update();
        }
    }
}