package org.firstinspires.ftc.teamcode.Utils;

import org.firstinspires.ftc.teamcode.Subsystems.*;
import com.qualcomm.robotcore.hardware.HardwareMap;
import org.firstinspires.ftc.teamcode.Commands.SixWheelCMD;
import org.firstinspires.ftc.teamcode.Commands.Turret.TurretAutoLLCMD;
import org.firstinspires.ftc.teamcode.Commands.Shooter.ShooterAutoLLCMD;

public class Robot {

    // Subsystems
    public Drivetrain drivetrain;
    public Intake intake;
    public TurretSubsystem turretSubsystem;
    public LLSubsystem ll;
    public ShooterSubsystem shooter;

    // Commands
    public TurretAutoLLCMD turretAuto;
    public ShooterAutoLLCMD shooterAutoCmd;
    public SixWheelCMD sixWheelCMD;

    public Alliance alliance;

    public Robot(HardwareMap hardwareMap, Alliance alliance) {
        this.alliance = alliance;

        // subsystems
        drivetrain = new Drivetrain(hardwareMap);
        intake = new Intake(hardwareMap);
        turretSubsystem = new TurretSubsystem(hardwareMap);
        ll = new LLSubsystem(hardwareMap, alliance);
        shooter = new ShooterSubsystem(hardwareMap);

        // commands
        turretAuto = new TurretAutoLLCMD(turretSubsystem, ll);
        shooterAutoCmd = new ShooterAutoLLCMD(shooter, ll);
        sixWheelCMD = new SixWheelCMD(drivetrain);
    }

    /**
     * This method MUST be called at the very start of every loop().
     */
    public void runPeriodic() {
        ll.periodic();
        shooterAutoCmd.execute();
        shooter.periodic();
    }
}