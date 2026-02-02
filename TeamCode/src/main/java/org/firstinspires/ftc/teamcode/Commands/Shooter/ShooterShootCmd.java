package org.firstinspires.ftc.teamcode.Commands.Shooter;


import org.firstinspires.ftc.teamcode.Subsystems.ShooterSubsystem;

import java.util.function.DoubleSupplier;

public class ShooterShootCmd {

    ShooterSubsystem shooterSubsystem;
    DoubleSupplier targetvelocity;

    public ShooterShootCmd(ShooterSubsystem shooterSubsystem, DoubleSupplier targetvelocity){
        this.shooterSubsystem = shooterSubsystem;
        this.targetvelocity = targetvelocity;
    }

    public void execute(){
        shooterSubsystem.setTargetVelocity(targetvelocity.getAsDouble());
    }

    public void end(boolean interrupted){
        shooterSubsystem.setTargetVelocity(0);
    }

}