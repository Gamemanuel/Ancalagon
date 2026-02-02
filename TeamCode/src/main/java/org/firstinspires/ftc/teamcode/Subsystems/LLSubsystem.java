package org.firstinspires.ftc.teamcode.Subsystems;

import com.acmerobotics.dashboard.FtcDashboard;
import com.qualcomm.hardware.limelightvision.LLResult;
import com.qualcomm.hardware.limelightvision.Limelight3A;
import com.qualcomm.robotcore.hardware.HardwareMap;

import org.firstinspires.ftc.teamcode.Utils.Alliance;

public class LLSubsystem {

    public final Alliance alliance;
    public Limelight3A limelight;
    public LLResult result;

    // MEASURE THESE VALUES ON YOUR ROBOT
    private static final double CAMERA_HEIGHT_INCHES = 16.0; // Height of the Limelight lens from the floor
    private static final double TARGET_HEIGHT_INCHES = 29.5; // Height of the Goal center from the floor
    private static final double CAMERA_PITCH_DEGREES = 5.0; // Angle of Limelight (0 is straight forward, + is looking up)

    public double getDistanceInches() {
        if (result == null || !result.isValid()) {
            return 1000.0;
        }

        double ty = result.getTy(); // Vertical offset from the Limelight (degrees)

        // Calculate the angle of the target relative to the ground
        double angleToGoalRadians = Math.toRadians(CAMERA_PITCH_DEGREES + ty);

        // Calculate distance: (Goal Height - Camera Height) / tan(total angle)
        double heightDifference = TARGET_HEIGHT_INCHES - CAMERA_HEIGHT_INCHES;

        return heightDifference / Math.tan(angleToGoalRadians);
    }

    public LLSubsystem(HardwareMap hMap, Alliance alliance) {
        limelight = hMap.get(Limelight3A.class, "limelight");
        this.alliance = alliance;

        limelight.setPollRateHz(100);
        limelight.start();
        limelight.pipelineSwitch(0); // apriltags
    }

    public void periodic() {
        result = limelight.getLatestResult();

        if (result != null && result.isValid()) {
            FtcDashboard.getInstance().getTelemetry().addData("LL AprilTag tA", result.getTa());
            FtcDashboard.getInstance().getTelemetry().addData("LL AprilTag tX", result.getTx());
            FtcDashboard.getInstance().getTelemetry().addData("Distance in", getDistanceInches());
        } else {
            FtcDashboard.getInstance().getTelemetry().addData("Limelight", "No Targets");
        }
    }

    public Double getAllianceTA() {
        if (result != null && result.isValid() && !result.getFiducialResults().isEmpty()) {
            int id = result.getFiducialResults().get(0).getFiducialId();

            if (alliance == Alliance.ANY || (alliance == Alliance.RED && id == 24) || (alliance == Alliance.BLUE && id == 20)) {
                return result.getTa();
            }
        }

        return null;
    }


    public double getAllianceTX() {

        if (result != null && result.isValid() && !result.getFiducialResults().isEmpty()) {
            int id = result.getFiducialResults().get(0).getFiducialId();

            if (alliance == Alliance.ANY || (alliance == Alliance.RED && id == 24) || (alliance == Alliance.BLUE && id == 20)) {
                return result.getTx();
            }
        }
        return 0;
    }

    public boolean isTargetFound() {
        return Math.abs(getAllianceTX()) > 0.0;
    }


}