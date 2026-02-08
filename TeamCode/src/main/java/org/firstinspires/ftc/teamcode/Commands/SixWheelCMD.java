package org.firstinspires.ftc.teamcode.Commands;

import static java.lang.Thread.sleep;
import org.firstinspires.ftc.teamcode.Subsystems.Drivetrain;
public class SixWheelCMD {

    Drivetrain drivetrain;

    public SixWheelCMD(Drivetrain drivetrain) {
        this.drivetrain = drivetrain;
    }
    /**
     * A basic command that runs the drivetrain for a specified amount of time and then stops.
     *
     * @Creator Will (Finch)
     * @param left left side of drivetrain
     * @param right right side of drivetrain
     * @param time how long it runs
     */
    public void driveBasic(float left, float right, long time) throws InterruptedException {
        //set all motor powers
        setMotors(left,right);
        sleep(time); //wait for however long
        //stop
        setMotors(0,0);
    }
    /**
     * A basic command that sets the motors to a specified power. Beware that it won't stop until
     * said to.
     *
     * @Creator Will (Finch)
     * @param left left side of drivetrain
     * @param right right side of drivetrain
     */
    public void setMotors(float left, float right) {
        drivetrain.leftSide.setPower((double) left);
        drivetrain.rightSide.setPower((double) right);
    }
}