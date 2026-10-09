package test.components;

import ecs.Component;

public class HealthComponent extends Component {
    public int health;

    public HealthComponent(int health) {
        this.health = health;
    }

}
