package org.firstinspires.ftc.teamcode.DriverControl.Testing;

import com.acmerobotics.dashboard.FtcDashboard;
import com.acmerobotics.dashboard.config.Config;
import com.acmerobotics.dashboard.telemetry.MultipleTelemetry;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import org.firstinspires.ftc.teamcode.Utils.Alliance;
import org.firstinspires.ftc.teamcode.Utils.Robot;

@Config
@TeleOp(name = "Shooter Tuning DUAL FLYWHEEL", group = "Tuning")
public class shooterTuningAdvanced extends LinearOpMode {

    // ===== STEP 1: Set your target velocity =====
    public static double TESTING_TARGET_RPM = 1000.0;

    // Test alliance for limelight
    public static Alliance TEST_ALLIANCE = Alliance.RED;

    Robot robot;

    @Override
    public void runOpMode() {
        // Connect to FTC Dashboard for live graphing
        telemetry = new MultipleTelemetry(telemetry, FtcDashboard.getInstance().getTelemetry());

        // Initialize robot
        robot = new Robot(hardwareMap, TEST_ALLIANCE);

        // Display tuning instructions
        displayTuningInstructions();
        telemetry.update();

        waitForStart();

        while (opModeIsActive()) {
            // Update subsystems
            robot.ll.periodic();
            robot.shooter.setTargetVelocity(TESTING_TARGET_RPM);
            robot.shooter.periodic();

            // ===== PRIMARY METRICS =====
            telemetry.addLine("╔═══════════════════════════════════╗");
            telemetry.addLine("║     DUAL FLYWHEEL TELEMETRY       ║");
            telemetry.addLine("╚═══════════════════════════════════╝");
            telemetry.addData("Target Velocity", "%.0f ticks/sec", TESTING_TARGET_RPM);
            telemetry.addData("Average Velocity", "%.0f ticks/sec", robot.shooter.getFilteredVelocity());
            telemetry.addData("Error", "%.0f ticks/sec", robot.shooter.getVelocityError());
            telemetry.addData("At Speed?", robot.shooter.isAtSpeed() ? "YES ✓" : "NO ✗");

            telemetry.addLine();
            telemetry.addLine("───────── INDIVIDUAL MOTORS ─────────");
            telemetry.addData("Left Motor", "%.0f ticks/sec", robot.shooter.getLeftVelocity());
            telemetry.addData("Right Motor", "%.0f ticks/sec", robot.shooter.getRightVelocity());
            telemetry.addData("Mismatch", "%.0f ticks/sec", robot.shooter.getVelocityMismatch());

            // Motor sync warning
            if (!robot.shooter.areMotorsSynced()) {
                telemetry.addLine();
                telemetry.addLine("WARNING: MOTORS OUT OF SYNC!");
                telemetry.addLine("Check for mechanical issues:");
                telemetry.addLine("  • Chain tension");
                telemetry.addLine("  • Motor mounting");
                telemetry.addLine("  • Flywheel balance");
            }

            telemetry.addLine();
            telemetry.addLine("───────── SYSTEM STATUS ─────────");
            telemetry.addData("Battery Voltage", "%.2fV", hardwareMap.voltageSensor.iterator().next().getVoltage());
            telemetry.addData("Left Motor Power", "%.2f", robot.shooter.shooterLeft.getPower());
            telemetry.addData("Right Motor Power", "%.2f", robot.shooter.shooterRight.getPower());

            telemetry.addLine();
            telemetry.addLine("───────── PID COEFFICIENTS ─────────");
            telemetry.addLine("(Adjust these in FTC Dashboard)");
            telemetry.addData("kP", "%.6f", robot.shooter.SCoeffs.p);
            telemetry.addData("kI", "%.6f", robot.shooter.SCoeffs.i);
            telemetry.addData("kD", "%.6f", robot.shooter.SCoeffs.d);
            telemetry.addData("kV (Feedforward)", "%.6f", robot.shooter.kV);
            telemetry.addData("Filter Gain", "%.2f", robot.shooter.VELOCITY_FILTER_GAIN);

            telemetry.update();
        }
    }

    private void displayTuningInstructions() {
        telemetry.addLine("╔════════════════════════════════════════╗");
        telemetry.addLine("║   DUAL FLYWHEEL TUNING GUIDE          ║");
        telemetry.addLine("╚════════════════════════════════════════╝");
        telemetry.addLine();

        telemetry.addLine("OPEN FTC DASHBOARD:");
        telemetry.addLine("   → http://192.168.43.1:8080/dash");
        telemetry.addLine();

        telemetry.addLine(" CRITICAL: Check Motor Sync First!");
        telemetry.addLine("   • Both motors should spin same speed");
        telemetry.addLine("   • Mismatch should be < 100 ticks/sec");
        telemetry.addLine("   • If not, fix mechanical issues first!");
        telemetry.addLine();

        telemetry.addLine("STEP 1: TUNE kV (Feedforward)");
        telemetry.addLine("  • Set kP = 0, kI = 0, kD = 0");
        telemetry.addLine("  • Adjust kV until average velocity");
        telemetry.addLine("    reaches ~90% of target");
        telemetry.addLine("  • Start with kV = 0.00169");
        telemetry.addLine();

        telemetry.addLine("STEP 2: TUNE kP (Proportional)");
        telemetry.addLine("  • Start with kP = -0.0001");
        telemetry.addLine("  • Increase (more negative) slowly");
        telemetry.addLine("  • Stop when you see oscillation");
        telemetry.addLine("  • Reduce kP by 30%");
        telemetry.addLine("  • Watch the GRAPH in dashboard!");
        telemetry.addLine();

        telemetry.addLine("STEP 2: TUNE kD (Derivative)");
        telemetry.addLine("  • Start with kD = kP * 0.1");
        telemetry.addLine("  • Increase if oscillating");
        telemetry.addLine("  • Decrease if sluggish");
        telemetry.addLine();

        telemetry.addLine("STEP 4️: TUNE kI (Integral) - OPTIONAL");
        telemetry.addLine("  • Only if steady-state error exists");
        telemetry.addLine("  • Start tiny: kI = kP * 0.01");
        telemetry.addLine("  • ⚠️ Too much = unstable!");
        telemetry.addLine();

        telemetry.addLine(" SUCCESS CRITERIA:");
        telemetry.addLine("  ✓ Reach target in < 0.5 seconds");
        telemetry.addLine("  ✓ Error < ±50 ticks/sec");
        telemetry.addLine("  ✓ No oscillation");
        telemetry.addLine("  ✓ Motors stay synced");
        telemetry.addLine();

        telemetry.addLine("Press START when ready");
    }
}