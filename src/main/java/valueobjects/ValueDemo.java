package valueobjects;

import java.util.Objects;

public class ValueDemo {

    static value class Point {
        int x;
        int y;

        public Point(int x, int y) {
            this.x = x;
            this.y = y;
        }
    }

    static void main() {
        var point1 = new Point(1, 2);
        System.out.println(Objects.hasIdentity(point1));

        var point2 = new Point(1, 2);
        System.out.println(point1 == point2);

        var point3 = new Point(1, 3);
        System.out.println(point1 == point3);

    }

}
