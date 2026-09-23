package valueobjects;

import jdk.internal.vm.annotation.NullRestricted;
import valueobjects.ValueDemo.Point;

public class NullabilityDemo {

    static value class Circle {
        @NullRestricted
        Point center;
        int radius;

        public Circle(Point center, int radius) {
            this.center = center;
            this.radius = radius;
        }
    }

    static void main() {
        var circle = new Circle(new Point(0, 0), 4);
        System.out.println(circle);
    }
}
