import java.util.ArrayList;
import java.util.function.Function;

class Traffic_Light_Controlled_Intersection {


        // Signal maintains the road which is green at the moment
        private final Signal signal;

        public Traffic_Light_Controlled_Intersection() {
            signal = new Signal();
        }

        public void carArrived(
                int carId,           // ID of the car
                int roadId,          // ID of the road the car travels on. Can be 1 (road A) or 2 (road B)
                int direction,       // Direction of the car
                Runnable turnGreen,  // Use turnGreen.run() to turn light to green on current road
                Runnable crossCar    // Use crossCar.run() to make car cross the intersection
        ) {
            synchronized (signal) {
                if (signal.greenRoadA != roadId) {
                    turnGreen.run();
                    signal.greenRoadA = roadId;
                }
                crossCar.run();
            }
        }

        class Signal {

            // 1 corresponds to the Road A
            int greenRoadA = 1;
        }


}

class Car extends Thread{
    private Traffic_Light_Controlled_Intersection t;
    private int carId;
    private int direction;
    private int arrivalTime;
    private int roadId;
    private static Runnable turnGreen;
    private Runnable crossCar;
    private ArrayList<String> array;

    public Car(Traffic_Light_Controlled_Intersection t,ArrayList<String> a, int carId, int direction, int arrivalTime, Runnable tG){
        this.carId = carId;
        this.direction = direction;
        this.roadId = direction == 1 || direction == 2 ? 1 : 2;
        this.arrivalTime = arrivalTime;
        this.t = t;
        array = a;
        turnGreen = tG;
        crossCar = () -> {
            a.add("Car " + carId +" Has Passed Road " + (roadId == 1 ? 'A' : 'B') + " In Direction " + direction);
        };
    }

    @Override
    public void run() {
        try {
            Thread.sleep(arrivalTime);
            t.carArrived(carId,roadId,direction,turnGreen,crossCar);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}


public class TLCI {
    public static final int roadA = 1;
    public static final int roadB = 2;
    private static int currentRoad = roadA;

    public static void main(String[] args) throws InterruptedException {
        Traffic_Light_Controlled_Intersection t = new Traffic_Light_Controlled_Intersection();
        ArrayList<String> array = new ArrayList<>();
        Runnable turnGreen = () -> {
            if(currentRoad == roadA){
                currentRoad = roadB;
                array.add("Traffic Light On Road B Is Green");
            }
            else{
                currentRoad = roadA;
                array.add("Traffic Light On Road A Is Green");
            }
        };

        int[] cars = {1,3,5,2,4}, directions = {2,1,2,4,3}, arrivalTimes = {10,20,30,40,50};
        for (int i = 0; i < cars.length; i++) {
            Car c = new Car(t,array,cars[i],directions[i],arrivalTimes[i],turnGreen);
            c.start();
        }
        Thread.sleep(5000);
        System.out.println(array);
    }


}


