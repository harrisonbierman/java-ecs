package test.components;

import ecs.Component;

public class VelocityComponent extends Component {
    public float x;
    public float y;

    public VelocityComponent(float x, float y) {
        this.x = x;
        this.y = y;
    }
}
