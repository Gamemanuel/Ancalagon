package org.firstinspires.ftc.teamcode.DriverControl.Testing;

import com.acmerobotics.dashboard.FtcDashboard;
import com.acmerobotics.dashboard.config.Config;
import com.acmerobotics.dashboard.telemetry.MultipleTelemetry;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;

import org.firstinspires.ftc.teamcode.Alliance;
import org.firstinspires.ftc.teamcode.commands.turret.TurretAutoLLCMD;
import org.firstinspires.ftc.teamcode.subsystems.Intake;
import org.firstinspires.ftc.teamcode.subsystems.LLSubsystem;
import org.firstinspires.ftc.teamcode.subsystems.ShooterSubsystem;
import org.firstinspires.ftc.teamcode.subsystems.TurretSubsystem;

@Config
@TeleOp(name = "Shooter Tuning", group = "Tuning")
public class ShooterTuning extends LinearOpMode {

    ShooterSubsystem shooter;
    Intake intake;
    LLSubsystem llSubsystem;
    TurretSubsystem turretSubsystem;
    TurretAutoLLCMD turretAuto;

    // Tuning variable visible in Dashboard
    public static double TESTING_TARGET_RPM = 1500;

    @Override
    public void runOpMode() {
        // Connect to Dashboard Telemetry
        telemetry = new MultipleTelemetry(telemetry, FtcDashboard.getInstance().getTelemetry());

        shooter = new ShooterSubsystem(hardwareMap);
        intake = new Intake(hardwareMap);
        llSubsystem = new LLSubsystem(hardwareMap, Alliance.BLUE);
        turretSubsystem = new TurretSubsystem(hardwareMap, Alliance.BLUE);
        turretAuto = new TurretAutoLLCMD(turretSubsystem, llSubsystem);

        telemetry.addLine("Ready to Tune. Open FTC Dashboard (192.168.43.1:8080)");
        telemetry.update();

        waitForStart();

        while (opModeIsActive()) {
            // run the ll loop every loop
            llSubsystem.periodic();

            telemetry = new MultipleTelemetry(telemetry, FtcDashboard.getInstance().getTelemetry());

            // 1. Set the target velocity from the Dashboard variable
            shooter.setTargetVelocity(TESTING_TARGET_RPM);
            intake.front.setPower(-1);

            turretAuto.faceAprilTag(1.5, Alliance.BLUE);

            // 2. Run the Periodic loop (Calculates PID + Feedforward)
            shooter.periodic();

            // 3. Telemetry for Graphing
            telemetry.addData("Target Velocity", TESTING_TARGET_RPM);
            telemetry.addData("Actual Velocity", shooter.shooter.getVelocity());

            // Show the values we are tuning
            telemetry.addData("Current kV", ShooterSubsystem.kV);
            telemetry.addData("Current kP", ShooterSubsystem.SCoeffs.p);

            telemetry.update();
        }
    }
}