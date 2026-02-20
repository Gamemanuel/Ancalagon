package org.firstinspires.ftc.teamcode.Auto.Shoot3CMD;

import com.acmerobotics.dashboard.FtcDashboard;
import com.acmerobotics.dashboard.config.Config;
import com.acmerobotics.dashboard.telemetry.MultipleTelemetry;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import org.firstinspires.ftc.teamcode.Auto.AutoLibrary;
import org.firstinspires.ftc.teamcode.Utils.Alliance;
import org.firstinspires.ftc.teamcode.Utils.Robot;

@Config
public abstract class Shoot3CMD extends LinearOpMode {

    // ===== TUNING CONSTANTS =====
    public static final double TARGET_DISTANCE_INCHES = 50.0;
    public static final double SHOOTER_TARGET_VELOCITY = 130.0;
    public static final long PUSH_DURATION_MS = 600;
    public static final long SHOT_DELAY_MS = 1000;

    private final Alliance alliance;

    public Shoot3CMD(Alliance alliance) {
        this.alliance = alliance;
    }

    @Override
    public void runOpMode() {
        // Setup
        telemetry = new MultipleTelemetry(telemetry, FtcDashboard.getInstance().getTelemetry());
        Robot robot = new Robot(hardwareMap, alliance);
        AutoLibrary auto = new AutoLibrary(this, robot, alliance);

        waitForStart();
        if (isStopRequested()) return;

        // ===== PHASE 1: Drive to Shooting Position =====
        telemetry.addData("Phase", "Driving to Position");
        telemetry.update();
        auto.driveToDistance(TARGET_DISTANCE_INCHES, 0.5);

//        // ===== PHASE 2: Spool Up Shooter =====
//        telemetry.addData("Phase", "Spooling Shooter");
//        telemetry.update();
//        auto.waitForShooterSpeed(SHOOTER_TARGET_VELOCITY, -2200.0);

        // ===== PHASE 3: Shoot 3 Balls =====
        telemetry.addData("Phase", "Shooting");
        telemetry.update();
        auto.shootMultipleBalls(3, PUSH_DURATION_MS, SHOT_DELAY_MS);

        // ===== PHASE 4: Drive Back =====
        telemetry.addData("Phase", "Backing Up");
        telemetry.update();
        auto.driveBackToDistance(90.0, 0.5);

        // ===== DONE =====
        robot.shooter.setTargetVelocity(0);
        robot.turretSubsystem.setPower(0);
        robot.sixWheelCMD.arcadeDrive(0, 0);

        telemetry.addData("Status", "Auto Complete!");
        telemetry.update();
    }
}