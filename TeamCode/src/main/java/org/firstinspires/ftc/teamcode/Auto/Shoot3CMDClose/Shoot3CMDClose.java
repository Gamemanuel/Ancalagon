//package org.firstinspires.ftc.teamcode.Auto.Shoot3CMDClose;
//
//import com.acmerobotics.dashboard.FtcDashboard;
//import com.acmerobotics.dashboard.config.Config;
//import org.firstinspires.ftc.teamcode.Utils.Robot;
//import org.firstinspires.ftc.teamcode.Utils.Alliance;
//import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
//import com.acmerobotics.dashboard.telemetry.MultipleTelemetry;
//
//@Config
//public abstract class Shoot3CMDClose extends LinearOpMode {
//
//    // --- TUNING CONSTANTS ---
//    public static float DRIVE_POWER = 0.34f;
//    public static final double TARGET_DISTANCE_INCHES = 69.0;
//    public static final double SHOOTER_TOLERANCE = 2380.0;
//    public static final long PUSH_DURATION_MS = 600;
//    public static final long SHOT_DELAY_MS = 1000;
//
//    // Turret
//    public static final double TURRET_TOLERANCE = 1.5;
//    public static final double TURRET_SHOOT_SPEED = 0.2;
//
//    Robot robot;
//
//    private final Alliance alliance;
//
//    public Shoot3CMDClose(Alliance alliance) {
//        this.alliance = alliance;
//    }
//
//    @Override
//    public void runOpMode() {
//        telemetry = new MultipleTelemetry(telemetry, FtcDashboard.getInstance().getTelemetry());
//        robot = new Robot(hardwareMap, alliance);
//
//        waitForStart();
//
//        if (isStopRequested()) return;
//
//        // =================================================================
//        // PHASE 1: Drive to Position
//        // =================================================================
//        while (robot.ll.getDistanceInches() > TARGET_DISTANCE_INCHES && opModeIsActive()) {
//            robot.sixWheelCMD.arcadeDrive(DRIVE_POWER, 0);
//            runSubsystems(); // Updates PID and Vision
//            telemetry.addData("Phase", "1. Driving");
//            telemetry.addData("Distance", robot.ll.getDistanceInches());
//            telemetry.update();
//        }
//        robot.sixWheelCMD.arcadeDrive(0, 0);
//
//        // =================================================================
//        // PHASE 2: Wait for Speed
//        // =================================================================
//        long timeout = 4000;
//        long startTime = System.currentTimeMillis();
//
//        while (opModeIsActive() && System.currentTimeMillis() < startTime + timeout) {
//            runSubsystems(); // Updates PID and Vision
//
//            double target = robot.shooter.getTargetVelocity();
//            double actual = robot.shooter.shooterMotors.getVelocity();
//
//            if (Math.abs(target - actual) <= SHOOTER_TOLERANCE) {
//                break;
//            }
//            telemetry.addData("Phase", "2. Spooling Up");
//            telemetry.addData("Err", target - actual);
//            telemetry.update();
//        }
//
//        // =================================================================
//        // PHASE 3: Shoot 3 Balls
//        // =================================================================
//        safeWait(8000);
//        safeWait(PUSH_DURATION_MS);
//        if (opModeIsActive()) {
//            robot.intake.intake.setPower(-1.0);
//
//            for (int i = 1; i <= 3; i++) {
//                if (!opModeIsActive()) break;
//                telemetry.addData("Phase", "3. Shooting Ball " + i);
//                telemetry.update();
//
//                // --- FIRE THE BALL ---
//                safeWait(PUSH_DURATION_MS);
//
//                // --- RECOVERY ---
//                robot.intake.intake.setPower(0); // Stop feeding
//
//                // 1. Wait for the fixed delay (keeping PID active!)
//                safeWait(SHOT_DELAY_MS);
//
//                // 2. (Optional but Recommended) Wait until velocity recovers exactly
//                while (Math.abs(robot.shooter.getTargetVelocity() - robot.shooter.shooterMotors.getVelocity()) > SHOOTER_TOLERANCE && opModeIsActive()) {
//                    runSubsystems();
//                    telemetry.addData("Phase", "Recovering Speed...");
//                    telemetry.addData("Err", robot.shooter.getTargetVelocity() - robot.shooter.shooterMotors.getVelocity());
//                    telemetry.update();
//                }
//
//                // Turn intake back on for the next ball
//                if (i < 3) {
//                    robot.intake.intake.setPower(-1.0);
//                }
//            }
//        }
//
//        // =================================================================
//        // PHASE 4: Stop
//        // =================================================================
//        robot.intake.intake.setPower(0);
//        robot.shooter.setTargetVelocity(0);
//        robot.turretSubsystem.setPower(0);
//        robot.sixWheelCMD.arcadeDrive(0,0);
//
//        while (robot.ll.getDistanceInches() < 69 && opModeIsActive() && robot.ll.getDistanceInches() != 1000) {
//            robot.sixWheelCMD.arcadeDrive(DRIVE_POWER, 0);
//            runSubsystems(); // Updates PID and Vision
//            telemetry.addData("Phase", "1. Driving");
//            telemetry.addData("Distance", robot.ll.getDistanceInches());
//            telemetry.update();
//        }
//        robot.sixWheelCMD.arcadeDrive(0, 0);
//    }
//
//    /**
//     * Replaces sleep(). Waits for the duration while keeping the shooter PID running.
//     */
//    public void safeWait(long durationMs) {
//        long start = System.currentTimeMillis();
//        while (opModeIsActive() && System.currentTimeMillis() < start + durationMs) {
//            runSubsystems();
//        }
//    }
//
//    /**
//     * Helper to keep all subsystems active
//     */
//    public void runSubsystems() {
//        robot.runPeriodic();
//        robot.turretAuto.faceAprilTag(TURRET_TOLERANCE, alliance);
//    }
//}