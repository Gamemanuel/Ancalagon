package org.firstinspires.ftc.teamcode.Utils;

import com.qualcomm.robotcore.hardware.HardwareMap;
import com.qualcomm.hardware.lynx.LynxModule; // Import for the Hubs
import org.firstinspires.ftc.teamcode.Subsystems.*;
import org.firstinspires.ftc.teamcode.Commands.Shooter.ShooterAutoLLCMD;
import org.firstinspires.ftc.teamcode.Commands.Turret.TurretAutoLLCMD;

import java.util.List; // Required for the list of hubs

public class Robot {
    // Hardware Hubs (for Bulk Reads)
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
        turretSubsystem = new TurretSubsystem(hardwareMap, alliance);
        ll = new LLSubsystem(hardwareMap, alliance);
        shooter = new ShooterSubsystem(hardwareMap);

        // 3. INITIALIZE COMMANDS
        turretAuto = new TurretAutoLLCMD(turretSubsystem, ll);
        shooterAutoCmd = new ShooterAutoLLCMD(shooter, ll);
    }

    /**
     * This method is the "heartbeat" of your robot.
     * It MUST be called at the very start of every loop().
     */
    public void runPeriodic() {
        // A. CLEAR BULK CACHE
        // This tells the Hubs: "Go get new data from the hardware right now."
        // All subsequent reads in this loop will use this fresh data instantly.
        for (LynxModule hub : allHubs) {
            hub.clearBulkCache();
        }

        // B. RUN SUBSYSTEM LOOPS
        ll.periodic();
        shooterAutoCmd.execute();
        shooter.periodic();
    }
}