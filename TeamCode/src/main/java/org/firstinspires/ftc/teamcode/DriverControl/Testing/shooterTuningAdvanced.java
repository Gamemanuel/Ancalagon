package org.firstinspires.ftc.teamcode.DriverControl.Testing;

import com.acmerobotics.dashboard.FtcDashboard;
import com.acmerobotics.dashboard.config.Config;
import com.acmerobotics.dashboard.telemetry.MultipleTelemetry;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import org.firstinspires.ftc.teamcode.Utils.Alliance;
import org.firstinspires.ftc.teamcode.Utils.Robot;

@Config
@TeleOp(name = "⚙️ Shooter Tuning ADVANCED", group = "Tuning")
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

            // ===== TELEMETRY FOR DASHBOARD GRAPHING =====
            // These values will appear as live graphs in FTC Dashboard
            telemetry.addData("🎯 Target Velocity", TESTING_TARGET_RPM);
            telemetry.addData("📈 Actual Velocity (Raw)", robot.shooter.shooter.getVelocity());
            telemetry.addData("📉 Actual Velocity (Filtered)", robot.shooter.getFilteredVelocity());
            telemetry.addData("❌ Error", robot.shooter.getVelocityError());
            telemetry.addData("✅ At Speed?", robot.shooter.isAtSpeed() ? "YES" : "NO");

            telemetry.addData("---", "---");
            telemetry.addData("🔋 Battery Voltage", hardwareMap.voltageSensor.iterator().next().getVoltage());
            telemetry.addData("⚡ Motor Power", robot.shooter.shooter.getPower());

            // Current PID values
            telemetry.addData("---", "PID VALUES (tune in dashboard)");
            telemetry.addData("kP", robot.shooter.SCoeffs.p);
            telemetry.addData("kI", robot.shooter.SCoeffs.i);
            telemetry.addData("kD", robot.shooter.SCoeffs.d);
            telemetry.addData("kV (Feedforward)", robot.shooter.kV);

            telemetry.update();
        }
    }

    private void displayTuningInstructions() {
        telemetry.addLine("╔════════════════════════════════════════╗");
        telemetry.addLine("║  SHOOTER TUNING GUIDE - FOLLOW STEPS  ║");
        telemetry.addLine("╚════════════════════════════════════════╝");
        telemetry.addLine();

        telemetry.addLine("📱 OPEN FTC DASHBOARD on your laptop:");
        telemetry.addLine("   → http://192.168.43.1:8080/dash");
        telemetry.addLine();

        telemetry.addLine("STEP 1️⃣: TUNE kV (Feedforward)");
        telemetry.addLine("  • Set kP = 0, kI = 0, kD = 0");
        telemetry.addLine("  • Adjust kV until velocity reaches ~90% of target");
        telemetry.addLine("  • It will be close but not perfect - that's OK!");
        telemetry.addLine();

        telemetry.addLine("STEP 2️⃣: TUNE kP (Proportional)");
        telemetry.addLine("  • Start with kP = -0.0001");
        telemetry.addLine("  • Increase (more negative) until it oscillates");
        telemetry.addLine("  • Then reduce kP by 30%");
        telemetry.addLine("  • Graph should show quick approach without overshoot");
        telemetry.addLine();

        telemetry.addLine("STEP 3️⃣: TUNE kD (Derivative)");
        telemetry.addLine("  • Start with kD = kP * 0.1");
        telemetry.addLine("  • Increase if you see oscillation");
        telemetry.addLine("  • Decrease if response is sluggish");
        telemetry.addLine("  • Smooths out the motion");
        telemetry.addLine();

        telemetry.addLine("STEP 4️⃣: TUNE kI (Integral) - OPTIONAL");
        telemetry.addLine("  • Only if there's steady-state error");
        telemetry.addLine("  • Start VERY small: kI = kP * 0.01");
        telemetry.addLine("  • Increase slowly");
        telemetry.addLine("  • ⚠️ Too much kI causes instability!");
        telemetry.addLine();

        telemetry.addLine("🎯 GOAL:");
        telemetry.addLine("  • Reach target in < 0.5 seconds");
        telemetry.addLine("  • Error < ±50 ticks/sec");
        telemetry.addLine("  • No oscillation");
        telemetry.addLine();

        telemetry.addLine("Press START when ready");
    }
}