package org.firstinspires.ftc.teamcode.DriverControl.Testing;

import com.acmerobotics.dashboard.FtcDashboard;
import com.acmerobotics.dashboard.config.Config;
import com.acmerobotics.dashboard.telemetry.MultipleTelemetry;
import com.qualcomm.robotcore.eventloop.opmode.OpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import org.firstinspires.ftc.teamcode.Commands.Turret.TurretAutoLLCMD;
import org.firstinspires.ftc.teamcode.Utils.Alliance;
import org.firstinspires.ftc.teamcode.Utils.Robot;

@Config
@TeleOp(name = "🎯 Turret Tuner SIMPLE", group = "Tuning")
public class TurretTuner extends OpMode {

    // Test alliance
    public static Alliance TEST_ALLIANCE = Alliance.RED;

    // Tolerance for auto tracking
    public static double TOLERANCE = 1.5;

    Robot robot;
    private boolean autoMode = false;

    @Override
    public void init() {
        telemetry = new MultipleTelemetry(telemetry, FtcDashboard.getInstance().getTelemetry());
        robot = new Robot(hardwareMap, TEST_ALLIANCE);

        telemetry.addLine("╔═══════════════════════════════════╗");
        telemetry.addLine("║      TURRET TUNING TOOL           ║");
        telemetry.addLine("╚═══════════════════════════════════╝");
        telemetry.addLine();
        telemetry.addLine("CONTROLS:");
        telemetry.addLine("• LEFT BUMPER: Manual turn left");
        telemetry.addLine("• RIGHT BUMPER: Manual turn right");
        telemetry.addLine("• A: Toggle AUTO tracking");
        telemetry.addLine();
        telemetry.addLine("TUNING:");
        telemetry.addLine("• Open FTC Dashboard");
        telemetry.addLine("• Adjust kP, MIN_POWER, MAX_POWER");
        telemetry.addLine("• Watch telemetry below");
        telemetry.addLine();
        telemetry.addLine("Press START when ready");
        telemetry.update();
    }

    @Override
    public void loop() {
        // Update limelight
        robot.ll.periodic();

        // Toggle auto mode
        if (gamepad1.a) {
            autoMode = !autoMode;
            while (gamepad1.a) { /* wait for release */ }
        }

        // ===== CONTROL MODE =====
        if (autoMode) {
            // AUTO MODE - Let the command control it
            robot.turretAuto.faceAprilTag(TOLERANCE, TEST_ALLIANCE);
        } else {
            // MANUAL MODE - Direct control
            if (gamepad1.left_bumper) {
                robot.turretSubsystem.setPower(0.3);
            } else if (gamepad1.right_bumper) {
                robot.turretSubsystem.setPower(-0.3);
            } else {
                robot.turretSubsystem.setPower(0);
            }
        }

        // ===== TELEMETRY =====
        telemetry.addLine("╔═══════════════════════════════════╗");
        telemetry.addLine("║      TURRET STATUS                ║");
        telemetry.addLine("╚═══════════════════════════════════╝");

        telemetry.addData("Mode", autoMode ? "AUTO" : "MANUAL 🎮");
        telemetry.addData("Motor Power", "%.2f", robot.turretSubsystem.Turret.getPower());

        telemetry.addLine();
        telemetry.addLine("───────── LIMELIGHT DATA ─────────");
        telemetry.addData("Target Found?", robot.ll.isTargetFound() ? "YES ✓" : "NO ✗");
        telemetry.addData("TX (Error)", "%.2f°", robot.ll.getAllianceTX());
        telemetry.addData("Distance", "%.1f inches", robot.ll.getDistanceInches());

        telemetry.addLine();
        telemetry.addLine("───────── TUNING VALUES ─────────");
        telemetry.addData("kP", "%.3f", TurretAutoLLCMD.kP);
        telemetry.addData("MIN_POWER", "%.2f", TurretAutoLLCMD.MIN_POWER);
        telemetry.addData("MAX_POWER", "%.2f", TurretAutoLLCMD.MAX_POWER);
        telemetry.addData("DEADZONE", "%.1f°", TurretAutoLLCMD.DEADZONE);

        telemetry.addLine();
        telemetry.addLine("───────── INSTRUCTIONS ─────────");
        if (autoMode) {
            telemetry.addLine("AUTO MODE: Point at AprilTag");
            telemetry.addLine("• Turret should smoothly center");
            telemetry.addLine("• If oscillating: DECREASE kP");
            telemetry.addLine("• If too slow: INCREASE kP");
            telemetry.addLine("• If doesn't move: INCREASE MIN_POWER");
            telemetry.addLine("• Press A to switch to MANUAL");
        } else {
            telemetry.addLine("MANUAL MODE:");
            telemetry.addLine("• LEFT BUMPER: Turn left");
            telemetry.addLine("• RIGHT BUMPER: Turn right");
            telemetry.addLine("• Check that motor spins correctly");
            telemetry.addLine("• Press A to switch to AUTO");
        }

        telemetry.update();
    }
}