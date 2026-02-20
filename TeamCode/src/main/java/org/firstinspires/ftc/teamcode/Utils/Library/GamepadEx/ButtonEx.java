package org.firstinspires.ftc.teamcode.Utils.Library.GamepadEx;

import java.util.function.BooleanSupplier;

public class ButtonEx {
    private final BooleanSupplier inputSupplier;  // Function that reads the button
    private boolean lastInput = false;

    /**
     * Creates a button that has extra functionality.
     * NOW PROPERLY READS BUTTON STATE EVERY LOOP!
     *
     * @param inputSupplier A function that returns the current button state
     * @author Will
     * @version 0.2 (Fixed by Copilot)
     */
    public ButtonEx(BooleanSupplier inputSupplier) {
        this.inputSupplier = inputSupplier;
    }

    /**
     * Get the current button state (reads fresh every time)
     */
    private boolean getCurrentInput() {
        return inputSupplier.getAsBoolean();
    }

    /**
     * Checks if the button is currently pressed.
     */
    public boolean pressed() {
        return getCurrentInput();
    }

    /**
     * Checks if the button is currently released.
     */
    public boolean released() {
        return !getCurrentInput();
    }

    /**
     * Only returns true the first time that you pressed the button.
     * IMPORTANT: Call periodic() or GamepadEx.update() every loop!
     */
    public boolean wasJustPressed() {
        boolean current = getCurrentInput();
        boolean check = (!lastInput && current);
        return check;
    }

    /**
     * Only returns true the first time you released the button.
     * IMPORTANT: Call periodic() or GamepadEx.update() every loop!
     */
    public boolean wasJustReleased() {
        boolean current = getCurrentInput();
        boolean check = (lastInput && !current);
        return check;
    }

    /**
     * Checks if the last check was different from the current one.
     * IMPORTANT: Call periodic() or GamepadEx.update() every loop!
     */
    public boolean stateJustChanged() {
        boolean current = getCurrentInput();
        boolean check = (lastInput != current);
        return check;
    }

    /**
     * Updates the internal state. MUST be called every loop!
     */
    public void periodic() {
        lastInput = getCurrentInput();
    }
}