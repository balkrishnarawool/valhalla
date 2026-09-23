package valueobjects;

public class ValueDemo {

    // value record Point(int x, int y) { }

    static value class Point {
        private int x;
        private int y;

        public Point(int x, int y) {
            this.x = x;
            this.y = y;
        }

//        @Override
//        public String toString() {
//            return "Point [x="+x+",y="+y+"]";
//        }
    }


    static void main() {
        var point = new Point(1, 2);
        IO.println(point.hashCode());
        IO.println(System.identityHashCode(point));

        var point2 = new Point(1, 2);
        IO.println("point == point2: " + (point.equals(point2)));

        var point3 = new Point(1, 3);
        IO.println("point == point3: " + (point.equals(point3)));

//        IO.println(Objects.hasIdentity(point));

//        Point[] point4 = new Point[4];
//        System.out.println(points[0]);
    }
}
