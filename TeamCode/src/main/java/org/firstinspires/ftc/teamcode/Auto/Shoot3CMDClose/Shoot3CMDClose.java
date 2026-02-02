package org.firstinspires.ftc.teamcode.Auto.Shoot3CMDClose;

import com.acmerobotics.dashboard.FtcDashboard;
import com.acmerobotics.dashboard.config.Config;
import com.acmerobotics.dashboard.telemetry.MultipleTelemetry;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;

import org.firstinspires.ftc.teamcode.Utils.Alliance;
import org.firstinspires.ftc.teamcode.Commands.Shooter.ShooterAutoLLCMD;
import org.firstinspires.ftc.teamcode.Commands.Turret.TurretAutoLLCMD;
import org.firstinspires.ftc.teamcode.Subsystems.Drivetrain;
import org.firstinspires.ftc.teamcode.Subsystems.Intake;
import org.firstinspires.ftc.teamcode.Subsystems.LLSubsystem;
import org.firstinspires.ftc.teamcode.Subsystems.ShooterSubsystem;
import org.firstinspires.ftc.teamcode.Subsystems.TurretSubsystem;

@Config
public abstract class Shoot3CMDClose extends LinearOpMode {

    // --- TUNING CONSTANTS ---
    public static double DRIVE_POWER = 0.34;
    public static final double TARGET_DISTANCE_INCHES = 69.0;
    public static final double SHOOTER_TOLERANCE = 2380.0;
    public static final long PUSH_DURATION_MS = 600;
    public static final long SHOT_DELAY_MS = 1000;

    // Flipper/Servo Positions
    public static final double FLIPPER_STOW_POS = 0.0;
    public static final double FLIPPER_SHOOT_POS = 0.75;

    // Turret
    public static final double TURRET_TOLERANCE = 1.5;
    public static final double TURRET_SHOOT_SPEED = 0.2;

    ShooterSubsystem shooter;
    Intake intake;
    LLSubsystem ll;
    Drivetrain drivetrain;
    TurretSubsystem turretSubsystem;
    TurretAutoLLCMD turretAuto;
    ShooterAutoLLCMD shooterAutoCmd;

    private final Alliance alliance;

    public Shoot3CMDClose(Alliance alliance) {
        this.alliance = alliance;
    }

    @Override
    public void runOpMode() {
        telemetry = new MultipleTelemetry(telemetry, FtcDashboard.getInstance().getTelemetry());

        drivetrain = new Drivetrain(hardwareMap);
        intake = new Intake(hardwareMap);
        turretSubsystem = new TurretSubsystem(hardwareMap, alliance);
        ll = new LLSubsystem(hardwareMap, alliance);
        shooter = new ShooterSubsystem(hardwareMap);

        turretAuto = new TurretAutoLLCMD(turretSubsystem, ll);
        shooterAutoCmd = new ShooterAutoLLCMD(shooter, ll);

        intake.floop.setPosition(FLIPPER_STOW_POS);

        waitForStart();

        if (isStopRequested()) return;

        // =================================================================
        // PHASE 1: Drive to Position
        // =================================================================
        while (ll.getDistanceInches() > TARGET_DISTANCE_INCHES && opModeIsActive()) {
            drivetrain.arcadeDrive(DRIVE_POWER, 0);
            runSubsystems(); // Updates PID and Vision
            telemetry.addData("Phase", "1. Driving");
            telemetry.addData("Distance", ll.getDistanceInches());
            telemetry.update();
        }
        drivetrain.arcadeDrive(0, 0);

        // =================================================================
        // PHASE 2: Wait for Speed
        // =================================================================
        long timeout = 4000;
        long startTime = System.currentTimeMillis();

        while (opModeIsActive() && System.currentTimeMillis() < startTime + timeout) {
            runSubsystems(); // Updates PID and Vision

            double target = shooter.getTargetVelocity();
            double actual = shooter.shooter.getVelocity();

            if (Math.abs(target - actual) <= SHOOTER_TOLERANCE) {
                break;
            }
            telemetry.addData("Phase", "2. Spooling Up");
            telemetry.addData("Err", target - actual);
            telemetry.update();
        }

        // =================================================================
        // PHASE 3: Shoot 3 Balls
        // =================================================================
        safeWait(8000);
        safeWait(PUSH_DURATION_MS);
        if (opModeIsActive()) {
            intake.front.setPower(-1.0);

            for (int i = 1; i <= 3; i++) {
                if (!opModeIsActive()) break;
                telemetry.addData("Phase", "3. Shooting Ball " + i);
                telemetry.update();

                // --- FIRE THE BALL ---
                if (i <= 2) {
                    // Ball 1 & 2: Push with intake roller
                    safeWait(PUSH_DURATION_MS);
                } else {
                    // Ball 3: Push with flipper
                    intake.floop.setPosition(FLIPPER_SHOOT_POS);
                    safeWait(PUSH_DURATION_MS);
                    intake.floop.setPosition(FLIPPER_STOW_POS);
                }

                // --- RECOVERY ---
                intake.front.setPower(0); // Stop feeding

                // 1. Wait for the fixed delay (keeping PID active!)
                safeWait(SHOT_DELAY_MS);

                // 2. (Optional but Recommended) Wait until velocity recovers exactly
                while (Math.abs(shooter.getTargetVelocity() - shooter.shooter.getVelocity()) > SHOOTER_TOLERANCE && opModeIsActive()) {
                    runSubsystems();
                    telemetry.addData("Phase", "Recovering Speed...");
                    telemetry.addData("Err", shooter.getTargetVelocity() - shooter.shooter.getVelocity());
                    telemetry.update();
                }

                // Turn intake back on for the next ball
                if (i < 3) {
                    intake.front.setPower(-1.0);
                }
            }
        }

        // =================================================================
        // PHASE 4: Stop
        // =================================================================
        intake.front.setPower(0);
        intake.floop.setPosition(FLIPPER_STOW_POS);
        shooter.setTargetVelocity(0);
        turretSubsystem.setPower(0);
        drivetrain.arcadeDrive(0,0);

        while (ll.getDistanceInches() < 69 && opModeIsActive() && ll.getDistanceInches() != 1000) {
            drivetrain.arcadeDrive(DRIVE_POWER, 0);
            runSubsystems(); // Updates PID and Vision
            telemetry.addData("Phase", "1. Driving");
            telemetry.addData("Distance", ll.getDistanceInches());
            telemetry.update();
        }
        drivetrain.arcadeDrive(0, 0);
    }

    /**
     * Replaces sleep(). Waits for the duration while keeping the shooter PID running.
     */
    public void safeWait(long durationMs) {
        long start = System.currentTimeMillis();
        while (opModeIsActive() && System.currentTimeMillis() < start + durationMs) {
            runSubsystems();
        }
    }

    /**
     * Helper to keep all subsystems active
     */
    public void runSubsystems() {
        ll.periodic();
        shooterAutoCmd.execute();
        shooter.periodic();
        turretAuto.faceAprilTag(TURRET_TOLERANCE, alliance);
    }
}