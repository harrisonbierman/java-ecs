package ecs.storage;

import ecs.Component;

public record ComponentBatch(int[] ids, Component[] components) {
}
