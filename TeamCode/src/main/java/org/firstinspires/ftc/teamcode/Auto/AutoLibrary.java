package org.firstinspires.ftc.teamcode.Auto;

import com.acmerobotics.dashboard.config.Config;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import org.firstinspires.ftc.teamcode.Utils.Robot;
import org.firstinspires.ftc.teamcode.Utils.Alliance;

/**
 * Centralized library for autonomous routines.
 * ALL autonomous movements should use these functions to reduce code duplication.
 *
 * Usage: Create an instance in your auto OpMode and call these methods.
 */
@Config
public class AutoLibrary {

    private final LinearOpMode opMode;
    private final Robot robot;
    private final Alliance alliance;

    // ===== TUNING PARAMETERS =====
    public static double DRIVE_KP = 0.03;           // Proportional gain for driving
    public static double TURN_KP = 0.025;           // Proportional gain for turning
    public static double TURN_KD = 0.008;           // Derivative gain for turning
    public static double MIN_DRIVE_POWER = 0.15;    // Minimum power to overcome friction
    public static double MIN_TURN_POWER = 0.15;
    public static double MAX_DRIVE_POWER = 0.6;     // Maximum drive power for safety
    public static double MAX_TURN_POWER = 0.5;

    // Tolerances
    public static double DISTANCE_TOLERANCE_INCHES = 2.0;
    public static double ANGLE_TOLERANCE_DEGREES = 2.0;
    public static double TURRET_TOLERANCE_DEGREES = 1.5;

    // Timeouts (milliseconds)
    public static long DRIVE_TIMEOUT_MS = 5000;
    public static long TURN_TIMEOUT_MS = 3000;
    public static long SHOOTER_SPOOL_TIMEOUT_MS = 4000;

    public AutoLibrary(LinearOpMode opMode, Robot robot, Alliance alliance) {
        this.opMode = opMode;
        this.robot = robot;
        this.alliance = alliance;
    }

    // ========================================
    // DRIVING FUNCTIONS
    // ========================================

    /**
     * Drive to a specific distance using Limelight
     *
     * @param targetDistanceInches Target distance from goal
     * @param power                Base drive power (will be modulated)
     * @return true if successful, false if timeout
     */
    public boolean driveToDistance(double targetDistanceInches, double power) {
        long startTime = System.currentTimeMillis();

        while (opMode.opModeIsActive() &&
                (System.currentTimeMillis() - startTime < DRIVE_TIMEOUT_MS)) {

            double currentDistance = robot.ll.getDistanceInches();
            double error = targetDistanceInches - currentDistance;

            // Check if within tolerance
            if (Math.abs(error) <= DISTANCE_TOLERANCE_INCHES) {
                robot.sixWheelCMD.arcadeDrive(0, 0);
                return true;
            }

            // P control with feedforward
            double drivePower = error * DRIVE_KP;
            drivePower += Math.signum(error) * MIN_DRIVE_POWER;

            // Clamp power
            drivePower = Math.max(-MAX_DRIVE_POWER, Math.min(MAX_DRIVE_POWER, drivePower));
            drivePower *= power;  // Scale by requested power

            robot.sixWheelCMD.arcadeDrive((float) drivePower, 0);
            runSubsystems();

            telemetry().addData("Phase", "Driving to Distance");
            telemetry().addData("Target", targetDistanceInches);
            telemetry().addData("Current", currentDistance);
            telemetry().addData("Error", error);
            telemetry().update();
        }

        robot.sixWheelCMD.arcadeDrive(0, 0);
        return false;  // Timeout
    }

    /**
     * Drive backwards to a specific distance using Limelight
     */
    public boolean driveBackToDistance(double targetDistanceInches, double power) {
        return driveToDistance(targetDistanceInches, -power);
    }

    /**
     * Turn the robot chassis to center on an AprilTag (TX = 0)
     *
     * @return true if successful, false if timeout
     */
    public boolean turnToAprilTag() {
        long startTime = System.currentTimeMillis();
        double previousError = 0.0;

        while (opMode.opModeIsActive() &&
                (System.currentTimeMillis() - startTime < TURN_TIMEOUT_MS)) {

            robot.ll.periodic();
            double tx = robot.ll.getAllianceTX();

            // Check if centered
            if (Math.abs(tx) <= ANGLE_TOLERANCE_DEGREES && robot.ll.isTargetFound()) {
                robot.sixWheelCMD.arcadeDrive(0, 0);
                return true;
            }

            // PD control
            double pTerm = tx * TURN_KP;
            double dTerm = (tx - previousError) * TURN_KD;
            double turnPower = pTerm + dTerm;

            // Add feedforward
            turnPower += Math.signum(tx) * MIN_TURN_POWER;

            // Clamp
            turnPower = Math.max(-MAX_TURN_POWER, Math.min(MAX_TURN_POWER, turnPower));

            robot.sixWheelCMD.arcadeDrive(0, (float) turnPower);
            previousError = tx;

            telemetry().addData("Phase", "Turning to AprilTag");
            telemetry().addData("Error (TX)", tx);
            telemetry().addData("Power", turnPower);
            telemetry().update();
        }

        robot.sixWheelCMD.arcadeDrive(0, 0);
        return false;  // Timeout
    }

    // ========================================
    // SHOOTER FUNCTIONS
    // ========================================

    /**
     * Wait for shooter to reach target velocity
     *
     * @param targetVelocity Desired shooter velocity
     * @param toleranceTicks Acceptable error in ticks/sec
     * @return true if at speed, false if timeout
     */
    public boolean waitForShooterSpeed(double targetVelocity, double toleranceTicks) {
        long startTime = System.currentTimeMillis();
        robot.shooter.setTargetVelocity(targetVelocity);

        while (opMode.opModeIsActive() &&
                (System.currentTimeMillis() - startTime < SHOOTER_SPOOL_TIMEOUT_MS)) {

            runSubsystems();

            double error = robot.shooter.getVelocityError();

            if (error <= toleranceTicks) {
                return true;
            }

            telemetry().addData("Phase", "Spooling Shooter");
            telemetry().addData("Target", targetVelocity);
            telemetry().addData("Actual", robot.shooter.getLeaderVelocity());
            telemetry().addData("Error", error);
            telemetry().update();
        }

        return false;  // Timeout
    }

    /**
     * Shoot a single ball and wait for velocity recovery
     *
     * @param pushDurationMs  How long to run intake to push ball
     * @param recoveryDelayMs How long to wait before next ball
     */
    public void shootBall(long pushDurationMs, long recoveryDelayMs) {
        // Push ball
        robot.intake.intake.setPower(-1.0);
        safeWait(pushDurationMs);

        // Stop and wait for recovery
        robot.intake.intake.setPower(0);
        safeWait(recoveryDelayMs);

        // Wait for velocity to recover
        while (opMode.opModeIsActive() && !robot.shooter.atTargetVelocity(50)) {
            runSubsystems();
            telemetry().addData("Phase", "Recovering Shooter Speed");
            telemetry().addData("Error", robot.shooter.getVelocityError());
            telemetry().update();
        }
    }

    /**
     * Shoot multiple balls in sequence
     *
     * @param ballCount       Number of balls to shoot
     * @param pushDurationMs  Duration of each push
     * @param recoveryDelayMs Delay between shots
     */
    public void shootMultipleBalls(int ballCount, long pushDurationMs, long recoveryDelayMs) {
        robot.intake.intake.setPower(-1.0);  // Pre-spin intake

        for (int i = 1; i <= ballCount; i++) {
            if (!opMode.opModeIsActive()) break;

            telemetry().addData("Phase", "Shooting Ball " + i + "/" + ballCount);
            telemetry().update();

            shootBall(pushDurationMs, recoveryDelayMs);

            // Turn intake back on for next ball
            if (i < ballCount) {
                robot.intake.intake.setPower(-1.0);
            }
        }

        robot.intake.intake.setPower(0);  // Stop intake
    }

    // ========================================
    // UTILITY FUNCTIONS
    // ========================================

    /**
     * Safe wait that keeps subsystems running (prevents PID from stopping)
     */
    public void safeWait(long durationMs) {
        long start = System.currentTimeMillis();
        while (opMode.opModeIsActive() &&
                (System.currentTimeMillis() - start < durationMs)) {
            runSubsystems();
        }
    }

    /**
     * Run all subsystems (PID, vision, etc.)
     */
    public void runSubsystems() {
        robot.runPeriodic();
        robot.turretAuto.faceAprilTag(TURRET_TOLERANCE_DEGREES, alliance);
    }

    /**
     * Convenience method for telemetry
     */
    private org.firstinspires.ftc.robotcore.external.Telemetry telemetry() {
        return opMode.telemetry;
    }


}