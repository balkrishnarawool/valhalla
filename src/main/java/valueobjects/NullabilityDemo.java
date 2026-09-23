package valueobjects;

import jdk.internal.vm.annotation.NullRestricted;
import valueobjects.ValueDemo.Point;

public class NullabilityDemo {

    static class Circle {
        @NullRestricted
        final Point center;
        final int radius;

        public Circle(Point center, int radius) {
            this.center = center;
            this.radius = radius;
            super();
        }
    }

    static void main() {
        var circle = new Circle(new Point(0, 0), 4);
    }
}
