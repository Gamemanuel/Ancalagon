package org.firstinspires.ftc.teamcode.Subsystems;

import com.acmerobotics.dashboard.FtcDashboard;
import com.acmerobotics.dashboard.config.Config;
import com.qualcomm.hardware.limelightvision.Limelight3A;
import com.qualcomm.hardware.limelightvision.LLResult;
import com.qualcomm.robotcore.hardware.HardwareMap;
import org.firstinspires.ftc.teamcode.Utils.Alliance;

@Config
public class LLSubsystem {

    public final Alliance alliance;
    public Limelight3A limelight;
    public LLResult result;

    // Result age tracking
    private long lastResultTimestamp = 0;
    public static long MAX_RESULT_AGE_MS = 500;  // Results older than this are considered stale

    // Camera calibration (MEASURE THESE ON YOUR ROBOT!)
    private static final double CAMERA_HEIGHT_INCHES = 16.0;
    private static final double TARGET_HEIGHT_INCHES = 29.5;
    private static final double CAMERA_PITCH_DEGREES = 5.0;

    public LLSubsystem(HardwareMap hMap, Alliance alliance) {
        this.alliance = alliance;
        limelight = hMap.get(Limelight3A.class, "limelight");
        limelight.pipelineSwitch(alliance == Alliance.RED ? 3 : 2);
        limelight.start();  // Start capturing
    }

    /**
     * MUST be called every loop to update vision data
     */
    public void periodic() {
        LLResult newResult = limelight.getLatestResult();

        // Only update if we have a fresh, valid result
        if (newResult != null && newResult.isValid()) {
            result = newResult;
            lastResultTimestamp = System.currentTimeMillis();

            // Dashboard telemetry
            FtcDashboard.getInstance().getTelemetry().addData("LL AprilTag tA", result.getTa());
            FtcDashboard.getInstance().getTelemetry().addData("LL AprilTag tX", result.getTx());
            FtcDashboard.getInstance().getTelemetry().addData("Distance (in)", getDistanceInches());
            FtcDashboard.getInstance().getTelemetry().addData("Result Age (ms)", getResultAge());
        } else {
            FtcDashboard.getInstance().getTelemetry().addData("Limelight", "No Targets");
        }
    }

    /**
     * Calculate distance to target using trigonometry
     */
    public double getDistanceInches() {
        if (result == null || !result.isValid()) {
            return 1000.0;  // Invalid/far distance
        }

        double ty = result.getTy();
        double angleToTarget = CAMERA_PITCH_DEGREES + ty;
        double heightDifference = TARGET_HEIGHT_INCHES - CAMERA_HEIGHT_INCHES;

        return heightDifference / Math.tan(Math.toRadians(angleToTarget));
    }

    /**
     * Get alliance-corrected TX value
     */
    public double getAllianceTX() {
        if (result == null || !result.isValid()) {
            return 0.0;
        }
        return result.getTx();
    }

    /**
     * Get alliance-corrected TA value (target area)
     */
    public Double getAllianceTA() {
        if (result == null || !result.isValid()) {
            return 0.0;
        }
        return result.getTa();
    }

    /**
     * Check if a valid target is currently detected
     */
    public boolean isTargetFound() {
        return result != null && result.isValid() && !isResultStale();
    }

    /**
     * Check if the current result is too old
     */
    public boolean isResultStale() {
        return getResultAge() > MAX_RESULT_AGE_MS;
    }

    /**
     * Get age of current result in milliseconds
     */
    public long getResultAge() {
        if (lastResultTimestamp == 0) return Long.MAX_VALUE;
        return System.currentTimeMillis() - lastResultTimestamp;
    }
}