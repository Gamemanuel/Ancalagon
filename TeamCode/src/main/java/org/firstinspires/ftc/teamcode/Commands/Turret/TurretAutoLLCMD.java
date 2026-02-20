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

    // ===== TUNING PARAMETERS (Adjust in FTC Dashboard) =====
    public static double kP = 0.02;              // How aggressive to turn (start small!)
    public static double MIN_POWER = 0.12;       // Minimum power to overcome friction
    public static double MAX_POWER = 0.6;        // Maximum power for safety
    public static double DEADZONE = 1.0;         // Stop moving if error < this (degrees)

    public TurretAutoLLCMD(TurretSubsystem turret, LLSubsystem ll) {
        this.turret = turret;
        this.ll = ll;
    }

    /**
     * @param tolerance How close is "good enough" (degrees)
     * @param alliance Which alliance (RED or BLUE)
     */
    public void faceAprilTag(double tolerance, Alliance alliance) {
        // 1. Switch to correct pipeline
        if (alliance == Alliance.RED) {
            ll.limelight.pipelineSwitch(3);
        } else {
            ll.limelight.pipelineSwitch(2);
        }

        // 2. Get latest limelight result
        LLResult result = ll.limelight.getLatestResult();

        // 3. Check if we have a valid target
        if (result == null || !result.isValid()) {
            // NO TARGET - stop moving
            turret.setPower(0);
            return;
        }

        // 4. Get the error (how far off we are)
        double tx = result.getTx();  // Positive = target is to the right

        // 5. If we're close enough, stop
        if (Math.abs(tx) <= Math.max(tolerance, DEADZONE)) {
            turret.setPower(0);
            return;
        }

        // 6. Calculate power using simple P control
        double power = tx * kP;

        // 7. Add minimum power to overcome friction
        //    (only if we're outside the deadzone)
        if (Math.abs(power) < MIN_POWER) {
            power = Math.signum(tx) * MIN_POWER;
        }

        // 8. Clamp to max power for safety
        power = Math.max(-MAX_POWER, Math.min(MAX_POWER, power));

        // 9. Send power to motor
        // NOTE: Negative sign might be needed depending on your motor direction
        turret.setPower(-power);
    }

    /**
     * Emergency stop
     */
    public void stop() {
        turret.setPower(0);
    }
}