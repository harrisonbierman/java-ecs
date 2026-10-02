package test.components;

import ecs.Component;

public class PositionComponent extends Component {
    public float x;
    public float y;

    public PositionComponent(float x, float y) {
        this.x = x;
        this.y = y;
    }
}
