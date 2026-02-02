package org.firstinspires.ftc.teamcode.Commands.Shooter;

import org.firstinspires.ftc.teamcode.Utils.SolversLib.InterpLUT.InterpLUT;
import org.firstinspires.ftc.teamcode.Subsystems.LLSubsystem;
import org.firstinspires.ftc.teamcode.Subsystems.ShooterSubsystem;

public class ShooterAutoLLCMD {

    private static InterpLUT VelocityLUT = new InterpLUT();

    static {
        // Format: VelocityLUT.add(Distance_Inches, Target_Velocity);
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


        // 1. Close Range (e.g., 4 feet)
//        VelocityLUT.add(24, -1000);
//        VelocityLUT.add(36, -1015);
//        VelocityLUT.add(48, -1025);
//        VelocityLUT.add(60, -1045);
//        VelocityLUT.add(72, -1175);
//        VelocityLUT.add(84, -1200);
//        VelocityLUT.add(96, -1320);
//        VelocityLUT.add(108, -1360);
//        VelocityLUT.add(120, -1525);

        // 4. Maximum Range Cap (If we see something extremely far)
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

            // 1. Get the distance from our new math method
            double distanceInches = LL.getDistanceInches();

            // 2. Safety check: Ensure distance is positive (valid)
            if(distanceInches > 0) {

                // 3. Look up the velocity based on distance
                double targetVelocity = VelocityLUT.get(distanceInches);

                // 4. Send to motor
                // Note: Update the Range.clip numbers to match your min/max safe RPMs
                subsystem.setTargetVelocity(targetVelocity);
            }
        }
    }

}