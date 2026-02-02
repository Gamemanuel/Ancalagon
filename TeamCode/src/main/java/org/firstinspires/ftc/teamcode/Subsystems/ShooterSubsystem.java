package org.firstinspires.ftc.teamcode.Subsystems;

import com.acmerobotics.dashboard.config.Config;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.DcMotorEx;
import com.qualcomm.robotcore.hardware.HardwareMap;
import com.qualcomm.robotcore.hardware.PIDFCoefficients;
import com.qualcomm.robotcore.hardware.VoltageSensor;
import org.firstinspires.ftc.teamcode.Utils.SolversLib.PIDFController.PIDFController;

@Config
public class ShooterSubsystem {

    public DcMotorEx shooter;
    public PIDFController shooterPIDF;
    private VoltageSensor batteryVoltageSensor; // To read battery voltage

    // PID Coefficients - Tune these in Dashboard!
    public static PIDFCoefficients SCoeffs = new PIDFCoefficients(-0.00055, 0, -0.00004, 0);
    public static double kV = 0.000633; // Base feedforward

    private double TargetVelocity = 0;

    public ShooterSubsystem(HardwareMap hMap){
        shooter = hMap.get(DcMotorEx.class,"shooter");
        shooter.setMode(DcMotor.RunMode.RUN_WITHOUT_ENCODER);

//        shooter.setDirection(DcMotorSimple.Direction.REVERSE);

        // Initialize voltage sensor (usually named "Control Hub")
        batteryVoltageSensor = hMap.voltageSensor.iterator().next();

        shooterPIDF = new PIDFController(SCoeffs);
    }

    public void periodic(){
        // 1. Update Coefficients
        shooterPIDF.setCoefficients(SCoeffs);

        // 2. Get Current State
        double currentVelocity = shooter.getVelocity(); // Likely returns negative (e.g., -1500)
        double currentVoltage = batteryVoltageSensor.getVoltage();

        // 3. PID Calculation
        // If Target is -1500 and Current is -1200 (slowed down):
        // Error = -1500 - (-1200) = -300.
        // PID Output = kP * -300 = Negative Power (Correct!)
        double pidOutput = shooterPIDF.calculateOutput(currentVelocity);

        // 4. Feedforward
        double feedforward = kV * TargetVelocity; // Positive kV * Negative Target = Negative Power

        // 5. Voltage Compensation
        double targetPower = (feedforward + pidOutput) * (12.0 / currentVoltage);

        // 6. RECOVERY CHECK (Bang-Bang Hybrid)
        // If we are spinning significantly slower than target (e.g. dropped 300 RPM)
        // Remember: -1200 is "greater" than -1500
        if (TargetVelocity < -500 && currentVelocity > TargetVelocity + 200) {
            // We lost speed! Full throttle to recover.
            shooter.setPower(-1.0);
        } else {
            // Normal PID control
            shooter.setPower(targetPower);
        }
    }

    public void setTargetVelocity(double target){
        TargetVelocity = -target;
        // Update the PID setpoint immediately
        shooterPIDF.setSetPoint(-target);
    }

    public double getTargetVelocity(){
        return TargetVelocity;
    }
}