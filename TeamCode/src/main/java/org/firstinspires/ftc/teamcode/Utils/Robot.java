package org.firstinspires.ftc.teamcode.Utils;

import org.firstinspires.ftc.teamcode.Subsystems.*;
import com.qualcomm.robotcore.hardware.HardwareMap;
import org.firstinspires.ftc.teamcode.Commands.Drivetrain.SixWheelCMD;
import org.firstinspires.ftc.teamcode.Commands.Turret.TurretAutoLLCMD;
import org.firstinspires.ftc.teamcode.Commands.Shooter.ShooterAutoLLCMD;

public class Robot {

    // Subsystems
    public Drivetrain drivetrain;
    public Intake intake;
    public TurretSubsystem turretSubsystem;
    public LLSubsystem ll;
    public ShooterSubsystem shooter;

    // Commands / Logic
    public TurretAutoLLCMD turretAuto;
    public ShooterAutoLLCMD shooterAutoCmd;
    public SixWheelCMD sixWheelCMD;

    public Alliance alliance;

    public Robot(HardwareMap hardwareMap, Alliance alliance) {
        this.alliance = alliance;

        // 2. INITIALIZE SUBSYSTEMS
        drivetrain = new Drivetrain(hardwareMap);
        intake = new Intake(hardwareMap);
        turretSubsystem = new TurretSubsystem(hardwareMap);
        ll = new LLSubsystem(hardwareMap, alliance);
        shooter = new ShooterSubsystem(hardwareMap);

        // 3. INITIALIZE COMMANDS
        turretAuto = new TurretAutoLLCMD(turretSubsystem, ll);
        shooterAutoCmd = new ShooterAutoLLCMD(shooter, ll);
        sixWheelCMD = new SixWheelCMD(drivetrain);
    }

    /**
     * This method MUST be called at the very start of every loop().
     */
    public void runPeriodic() {
        // Run the subsystems so the values don't get stale
        ll.periodic();
        shooterAutoCmd.execute();
        shooter.periodic();
    }
}