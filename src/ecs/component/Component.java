package ecs.component;

import java.lang.reflect.Field;

public abstract class Component {
   
    @Override
    public String toString() {
        String output = this.getClass().getSimpleName(); 

        Field[] fields = this.getClass().getFields();

        for (Field field : fields) {
            try {
                output = output + ", " + field.getName() + ": "  + field.get(this);
            } catch (IllegalAccessException e) {
                System.out.println(e.getMessage());
            }
        }

        return output;
    }
}
