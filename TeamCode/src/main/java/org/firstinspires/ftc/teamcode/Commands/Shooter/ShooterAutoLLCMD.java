package org.firstinspires.ftc.teamcode.Commands.Shooter;

import org.firstinspires.ftc.teamcode.Subsystems.LLSubsystem;
import org.firstinspires.ftc.teamcode.Subsystems.ShooterSubsystem;
import org.firstinspires.ftc.teamcode.Utils.SolversLib.InterpLUT.InterpLUT;

public class ShooterAutoLLCMD {

    private static final InterpLUT VelocityLUT = new InterpLUT();

    static {
        // Format: VelocityLUT.add(Distance_Inches, Target_Velocity);

        // Minimum range cap (If we see something really close)
        VelocityLUT.add(0,0);

        VelocityLUT.add(34.8,-950);
        VelocityLUT.add(36.2,-960);
        VelocityLUT.add(42.8,-975);
        VelocityLUT.add(65.9,-1120);
        VelocityLUT.add(75.9,-1160);
        VelocityLUT.add(77.5,-1200);
        VelocityLUT.add(88.5,-1275);
        VelocityLUT.add(92.4, -1250);
        VelocityLUT.add(102, -1383);
        VelocityLUT.add(109,-1424);
        VelocityLUT.add(120,-1468);

        // Maximum Range Cap (If we see something extremely far)
        VelocityLUT.add(8000, -3000);

        VelocityLUT.createLUT();
    }

    private final ShooterSubsystem subsystem;
    private final LLSubsystem LL;

    public ShooterAutoLLCMD(ShooterSubsystem subsystem, LLSubsystem LL) {
        this.subsystem = subsystem;
        this.LL = LL;
    }

    public void execute(){
        // Ensure we have a valid target
        if (LL.result != null && LL.result.isValid()) {

            // Get the distance from limelight
            double distanceInches = LL.getDistanceInches();

            // Make sure that distance is positive
            if(distanceInches > 0) {

                // Calculate velocity based on distance
                double targetVelocity = VelocityLUT.get(distanceInches);

                // Send target to motor
                subsystem.setTargetVelocity(targetVelocity);
            }
        }
    }

}