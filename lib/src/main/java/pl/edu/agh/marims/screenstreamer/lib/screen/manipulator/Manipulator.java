package pl.edu.agh.marims.screenstreamer.lib.screen.manipulator;

import pl.edu.agh.marims.screenstreamer.lib.screen.manipulator.events.KeyboardEvent;
import pl.edu.agh.marims.screenstreamer.lib.screen.manipulator.events.MouseEvent;
import pl.edu.agh.marims.screenstreamer.lib.screen.manipulator.events.SpecialKeyEvent;

public interface Manipulator {
    public void manipulate(MouseEvent mouseEvent);

    public void manipulate(KeyboardEvent keyboardEvent);

    public void manipulate(SpecialKeyEvent specialKeyEvent);
}
