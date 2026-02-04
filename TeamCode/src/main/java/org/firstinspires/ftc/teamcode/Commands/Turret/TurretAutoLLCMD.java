package org.firstinspires.ftc.teamcode.Commands.Turret;

import org.firstinspires.ftc.teamcode.Utils.Alliance;
import com.qualcomm.hardware.limelightvision.LLResult;
import org.firstinspires.ftc.teamcode.Subsystems.LLSubsystem;
import org.firstinspires.ftc.teamcode.Subsystems.TurretSubsystem;

public class TurretAutoLLCMD {

    TurretSubsystem turret;
    LLSubsystem ll;

    // Values that we tune:
    double kP = 0.02; // kP: How fast to turn per degree of error
    double kStatic = 0.15; // kStatic: Minimum power required to get the servo moving (overcome friction)

    public double offset; // init the offset var

    public TurretAutoLLCMD(TurretSubsystem turret, LLSubsystem ll) {
        this.turret = turret;
        this.ll = ll;
    }

    public void faceAprilTag(double tolerance, Alliance alliance) {
        // TODO:// make this offset system better
        // Switch pipeline based on alliance
        if (alliance == Alliance.RED) {
            ll.limelight.pipelineSwitch(3);
            offset = 2.5;
        } else {
            ll.limelight.pipelineSwitch(2);
            offset = 1;
        }

        LLResult result = ll.limelight.getLatestResult();

        if (result != null && result.isValid()) {
            // Get error (TX)
            double tx = result.getTx() + offset;

            // If we are outside the tolerance (e.g. > 2 degrees off)
            if (Math.abs(tx) > tolerance) {

                // Calculate P (Proportional) term
                double pidTerm = tx * kP;

                // Calculate Feedforward (Static friction)
                // If tx is positive (target to right), add kStatic. If negative, subtract kStatic.
                double ffTerm = Math.signum(tx) * kStatic;

                // Combine them.
                // Since CRServo: Positive power usually turns one way.
                // You might need to flip the sign (-) depending on your servo wiring.
                double power = pidTerm + ffTerm;

                // Send to turret (Limit to max 1.0)
                // Note: I added a negative sign here assuming standard orientation, remove if it turns wrong way
                turret.setPower(-power);
            } else {
                // Inside tolerance: Stop completely
                turret.setPower(0);
            }
        } else {
            // No target found: Stop
            turret.setPower(0);
        }
    }
}