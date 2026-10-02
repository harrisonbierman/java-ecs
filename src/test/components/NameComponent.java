package test.components;

import ecs.Component;

public class NameComponent extends Component {

    public String name;

    public NameComponent(String name) {
        this.name = name;
    }
}
