package org.firstinspires.ftc.teamcode.DriverControl.TeleOp;

import org.firstinspires.ftc.teamcode.Utils.Robot;
import com.acmerobotics.dashboard.FtcDashboard;
import org.firstinspires.ftc.teamcode.Utils.Alliance;
import com.qualcomm.robotcore.eventloop.opmode.OpMode;
import com.acmerobotics.dashboard.telemetry.MultipleTelemetry;

public abstract class TeleOp extends OpMode {

    Robot robot;

    private final Alliance alliance;

    // Manual override flag for the shooter
    private boolean shooterManualOverride = false;
    // To detect button press (rising edge)
    private boolean lastYButtonState = false;

    public TeleOp(Alliance alliance) {
        this.alliance = alliance;
    }

    public void init() {
        // link telemetry
        telemetry = new MultipleTelemetry(telemetry, FtcDashboard.getInstance().getTelemetry());

        // call the subsystems
        robot = new Robot(hardwareMap, alliance);
    }

    public void loop() {
        // run the ll loop every loop
        robot.ll.periodic();
        // Drivetrain
        robot.drivetrain.arcadeDrive(gamepad1.left_stick_y, gamepad1.right_stick_x);

        // Intake
        robot.intake.front.setPower(gamepad2.left_trigger - gamepad2.right_trigger);
        robot.intake.floop.setPosition(-gamepad2.left_stick_y * 0.75);

        // Turret Logic
        // Manual override triggers
        if (gamepad2.right_bumper) {
            robot.turretSubsystem.setPower(-0.5);
        } else if (gamepad2.left_bumper) {
            robot.turretSubsystem.setPower(0.5);
        } else {
            // Auto Turret Tracking
            // Use a tolerance of 1.5 degrees
            robot.turretAuto.faceAprilTag(1.5, alliance);
        }

        // Shooter Logic

        // Toggle Manual override with Y button on gamepad2
        if (gamepad2.y && !lastYButtonState) {
            shooterManualOverride = !shooterManualOverride;
        }
        lastYButtonState = gamepad2.y;

        if (shooterManualOverride) {
            // Manual mode:

            robot.shooter.shooter.setPower(-gamepad2.left_stick_y);
        } else {
            // Enable Automatic Mode:

            // Run the auto subsystem
            robot.shooterAutoCmd.execute();

            // Run the Periodic loop (Calculates PID + Feedforward)
            robot.shooter.periodic();
        }

        // Telemetry
        telemetry.addData("Shooter Mode", shooterManualOverride ? "MANUAL" : "AUTO");
        telemetry.addData("Shooter Target", robot.shooter.getTargetVelocity());
        telemetry.addData("Shooter Actual", robot.shooter.shooter.getVelocity());
        telemetry.update();
    }
}