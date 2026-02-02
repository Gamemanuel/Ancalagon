package org.firstinspires.ftc.teamcode.DriverControl.Testing;

import com.acmerobotics.dashboard.FtcDashboard;
import com.acmerobotics.dashboard.config.Config;
import com.acmerobotics.dashboard.telemetry.MultipleTelemetry;
import com.qualcomm.robotcore.eventloop.opmode.OpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import org.firstinspires.ftc.teamcode.subsystems.Drivetrain;
import org.firstinspires.ftc.teamcode.subsystems.LLSubsystem;
import org.firstinspires.ftc.teamcode.Alliance; // Assuming you have an Alliance enum

@Config // Allows real-time tuning via FTC Dashboard
@TeleOp(name = "Limelight Turn Tuner", group = "Tuning")
public class LimelightTurnTuner extends OpMode {

    // --- TUNING CONSTANTS (Adjusted via Dashboard) ---
    // Start small and increase until oscillation, then back off.
    public static double TURN_KP = 0.03;

    // Minimum power to overcome friction.
    public static double MIN_POWER = 0.15;

    // Allowed error in degrees (when to stop).
    public static double TOLERANCE = 1.5;

    // Subsystems
    private Drivetrain drivetrain;
    private LLSubsystem ll;

    // Control Variables
    private boolean isTurning = false;
    private long turnStartTime;
    private static final long TIMEOUT_MS = 3000; // 3 second safety timeout

    @Override
    public void init() {
        // Initialize Telemetry for Dashboard
        telemetry = new MultipleTelemetry(telemetry, FtcDashboard.getInstance().getTelemetry());

        // Initialize Subsystems
        drivetrain = new Drivetrain(hardwareMap);
        // Note: I'm assuming Alliance.RED here, but you may need to pass this
        // to your LLSubsystem if it relies on it.
        ll = new LLSubsystem(hardwareMap, Alliance.RED);

        telemetry.addData("Status", "Ready. Press A to turn.");
    }

    @Override
    public void loop() {
        // Always run Limelight periodic to get fresh data
        ll.periodic();

        // -------------------------------------------------------------
        // I. START TURNING COMMAND
        // -------------------------------------------------------------
        if (gamepad1.a && !isTurning) {
            isTurning = true;
            turnStartTime = System.currentTimeMillis();
        }

        // -------------------------------------------------------------
        // II. TURNING LOGIC
        // -------------------------------------------------------------
        if (isTurning) {
            double tx = ll.getAllianceTX();

            // 1. Check Exit Conditions
            boolean isCentered = (Math.abs(tx) < TOLERANCE && ll.isTargetFound());
            boolean isTimedOut = (System.currentTimeMillis() - turnStartTime > TIMEOUT_MS);

            if (isCentered || isTimedOut) {
                // STOP
                drivetrain.Drive(0, 0);
                isTurning = false;
                telemetry.addData("Turn Result", isCentered ? "SUCCESS" : "TIMEOUT");
            } else {
                // 2. Calculate Power
                double turnPower = tx * -TURN_KP; // Note the negative sign may be needed to correct direction

                // 3. Apply Minimum Feedforward
                if (Math.abs(turnPower) < MIN_POWER) {
                    // This applies MIN_POWER in the correct direction (signum)
                    turnPower = MIN_POWER * Math.signum(turnPower);
                }

                // 4. Execute Turn
                drivetrain.Drive(0, turnPower);
            }
        } else {
            // Manual control when not turning
            drivetrain.Drive(gamepad1.left_stick_y, gamepad1.right_stick_x);
        }

        // -------------------------------------------------------------
        // III. REAL-TIME TUNING CONTROLS
        // -------------------------------------------------------------
        double KP_STEP = 0.001;
        double MIN_POWER_STEP = 0.01;

        // D-PAD UP/DOWN tunes KP
        if (gamepad1.dpad_up) {
            TURN_KP += KP_STEP;
        } else if (gamepad1.dpad_down) {
            TURN_KP = Math.max(0, TURN_KP - KP_STEP); // Ensure KP doesn't go negative
        }

        // D-PAD LEFT/RIGHT tunes MinPower
        if (gamepad1.dpad_right) {
            MIN_POWER += MIN_POWER_STEP;
        } else if (gamepad1.dpad_left) {
            MIN_POWER = Math.max(0, MIN_POWER - MIN_POWER_STEP); // Ensure power doesn't go negative
        }


        // -------------------------------------------------------------
        // IV. TELEMETRY DISPLAY
        // -------------------------------------------------------------
        telemetry.addData("Mode", isTurning ? "--- TURNING ---" : "Idle (Manual Control)");
        telemetry.addData("Instructions", "DPAD Up/Down: Tune KP | DPAD Left/Right: Tune MinPower | A: START TURN");

//        telemetry.addData("Limelight Target Found", ll.isTargetFound());
        telemetry.addData("Limelight TX (Error)", ll.getAllianceTX());
//
//        telemetry.addData("Current Turn Power", drivetrain.getLastTurnPower()); // Assuming you have a getter for power

        telemetry.addData("TUNING VALUES", "");
        telemetry.addData("1. TURN_KP", String.format("%.4f (Adjust: DPAD Up/Down)", TURN_KP));
        telemetry.addData("2. MIN_POWER", String.format("%.2f (Adjust: DPAD Left/Right)", MIN_POWER));
        telemetry.addData("3. TOLERANCE", String.format("%.1f", TOLERANCE));
        telemetry.update();
    }
}