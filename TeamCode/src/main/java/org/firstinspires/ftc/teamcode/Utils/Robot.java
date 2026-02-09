package org.firstinspires.ftc.teamcode.Utils;

import java.util.List;
import com.qualcomm.hardware.lynx.LynxModule;
import org.firstinspires.ftc.teamcode.Subsystems.*;
import com.qualcomm.robotcore.hardware.HardwareMap;
import org.firstinspires.ftc.teamcode.Commands.SixWheelCMD;
import org.firstinspires.ftc.teamcode.Commands.Turret.TurretAutoLLCMD;
import org.firstinspires.ftc.teamcode.Commands.Shooter.ShooterAutoLLCMD;

public class Robot {
    // Hardware Hubs
    public List<LynxModule> allHubs;

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

        // 1. SETUP BULK READS
        // Get all Hubs (Control Hub + Expansion Hub) from the hardware map
        allHubs = hardwareMap.getAll(LynxModule.class);

        // Loop through them and set them to MANUAL mode
        // In this mode, we must manually clear the cache every loop
        for (LynxModule hub : allHubs) {
            hub.setBulkCachingMode(LynxModule.BulkCachingMode.MANUAL);
        }

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
        // gets new data from the hubs
        for (LynxModule hub : allHubs) {
            hub.clearBulkCache();
        }

        // B. RUN SUBSYSTEM LOOPS
        ll.periodic();
        shooterAutoCmd.execute();
        shooter.periodic();
    }
}