package nl.vaneijndhoven.objects;

import nl.vaneijndhoven.geometry.Line;

import java.util.Optional;

public class StoppingZone {

    private Optional<Line> entrance;
    private Optional<Line> exit;

    public StoppingZone(Optional<Line> entrance, Optional<Line> exit) {
        this.entrance = entrance;
        this.exit = exit;
    }

    public Optional<Line> getEntrance() {
        return entrance;
    }

    public Optional<Line> getExit() {
        return exit;
    }
}
