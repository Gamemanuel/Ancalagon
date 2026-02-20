package org.firstinspires.ftc.teamcode.Commands.Turret;

import org.firstinspires.ftc.teamcode.Utils.Alliance;
import com.qualcomm.hardware.limelightvision.LLResult;
import org.firstinspires.ftc.teamcode.Subsystems.LLSubsystem;
import org.firstinspires.ftc.teamcode.Subsystems.TurretSubsystem;
import com.acmerobotics.dashboard.config.Config;

@Config
public class TurretAutoLLCMD {

    TurretSubsystem turret;
    LLSubsystem ll;

    // PID Values (Now tunable via dashboard!)
    public static double kP = 0.025;      // Proportional: How aggressive to correct error
    public static double kD = 0.008;      // Derivative: Dampens oscillation, smooths motion
    public static double kStatic = 0.15;  // Static friction overcome
    public static double DEADZONE = 0.5;  // Degrees - stops micro-adjustments

    // Last Known Position Tracking
    private double lastKnownTx = 0.0;
    private long lastTargetFoundTime = 0;
    private double previousError = 0.0;
    private long previousTime = 0;

    // Timeout for last known position (milliseconds)
    public static long PREDICTION_TIMEOUT_MS = 1500;

    public double offset = 0.0;

    public TurretAutoLLCMD(TurretSubsystem turret, LLSubsystem ll) {
        this.turret = turret;
        this.ll = ll;
        this.previousTime = System.currentTimeMillis();
    }

    public void faceAprilTag(double tolerance, Alliance alliance) {
        // Switch pipeline based on alliance
        if (alliance == Alliance.RED) {
            ll.limelight.pipelineSwitch(3);
        } else {
            ll.limelight.pipelineSwitch(2);
        }

        LLResult result = ll.limelight.getLatestResult();
        long currentTime = System.currentTimeMillis();
        double tx = 0.0;
        boolean usingPrediction = false;

        // --- STEP 1: Determine tracking error (TX) ---
        if (result != null && result.isValid()) {
            // Fresh target data!
            tx = result.getTx() + offset;
            lastKnownTx = tx;
            lastTargetFoundTime = currentTime;
        } else {
            // Target lost! Use last known position if recent enough
            long timeSinceLastSeen = currentTime - lastTargetFoundTime;

            if (timeSinceLastSeen < PREDICTION_TIMEOUT_MS && lastKnownTx != 0.0) {
                // Use the last known TX to continue tracking
                tx = lastKnownTx;
                usingPrediction = true;
            } else {
                // Target lost for too long - stop
                turret.setPower(0);
                return;
            }
        }

        // --- STEP 2: Check if within tolerance + deadzone ---
        if (Math.abs(tx) <= Math.max(tolerance, DEADZONE)) {
            turret.setPower(0);
            return;
        }

        // --- STEP 3: Calculate PID Control ---
        // P Term
        double pTerm = tx * kP;

        // D Term (rate of change of error)
        double dt = (currentTime - previousTime) / 1000.0; // seconds
        double dTerm = 0.0;
        if (dt > 0.001) { // Avoid division by very small numbers
            double errorRate = (tx - previousError) / dt;
            dTerm = errorRate * kD;
        }

        // Feedforward (overcome static friction)
        double ffTerm = Math.signum(tx) * kStatic;

        // Combine
        double power = pTerm + dTerm + ffTerm;

        // Clamp power to safe range
        power = Math.max(-0.8, Math.min(0.8, power));

        // --- STEP 4: Apply Power ---
        turret.setPower(-power); // Negative sign may depend on your motor direction

        // Update state for next loop
        previousError = tx;
        previousTime = currentTime;
    }

    /**
     * Resets the last known position tracking.
     * Call this when starting a new auto routine or when you want to clear history.
     */
    public void reset() {
        lastKnownTx = 0.0;
        lastTargetFoundTime = 0;
        previousError = 0.0;
        previousTime = System.currentTimeMillis();
    }
}