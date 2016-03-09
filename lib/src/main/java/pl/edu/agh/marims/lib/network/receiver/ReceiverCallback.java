package pl.edu.agh.marims.lib.network.receiver;

import pl.edu.agh.marims.lib.screen.manipulator.events.KeyboardEvent;
import pl.edu.agh.marims.lib.screen.manipulator.events.MouseEvent;
import pl.edu.agh.marims.lib.screen.manipulator.events.SpecialKeyEvent;

public interface ReceiverCallback {
    void onMouseEvent(MouseEvent event);

    void onKeyboardEvent(KeyboardEvent event);

    void onSpecialKeyEvent(SpecialKeyEvent event);
}
