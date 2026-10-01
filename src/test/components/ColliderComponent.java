package test.components;

import ecs.Component;

public class ColliderComponent extends Component {
    
    public float origin;
    public float height;
    public float width;

    public ColliderComponent(float origin, float height, float width) {
        this.origin = origin;
        this.height = height;
        this.width = width;
    }

    
}
