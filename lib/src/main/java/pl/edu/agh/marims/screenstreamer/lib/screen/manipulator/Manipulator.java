package pl.edu.agh.marims.screenstreamer.lib.screen.manipulator;

public interface Manipulator {
    public void manipulate(MouseEvent mouseEvent);

    public void manipulate(KeyboardEvent keyboardEvent);

    public void manipulate(SpecialKeyEvent specialKeyEvent);
}
