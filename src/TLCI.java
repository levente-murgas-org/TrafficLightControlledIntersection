import java.util.ArrayList;
import java.util.Scanner;
import java.util.concurrent.Semaphore;

class TrafficLight {


        // Signal maintains the road which is green at the moment
        private final Signal signal;
        private Semaphore semSignal = new Semaphore(1);

        public TrafficLight() {
            signal = new Signal();
        }

        public void carArrived(
                int carId,           // ID of the car
                int roadId,          // ID of the road the car travels on. Can be 1 (road A) or 2 (road B)
                int direction,       // Direction of the car
                Runnable turnGreen,  // Use turnGreen.run() to turn light to green on current road
                Runnable crossCar    // Use crossCar.run() to make car cross the intersection
        ) throws InterruptedException {
            semSignal.acquire();
                if (signal.greenRoadA != roadId) {
                    turnGreen.run();
                    signal.greenRoadA = roadId;
                }
                crossCar.run();
            semSignal.release();
        }

        class Signal {

            // 1 corresponds to the Road A
            int greenRoadA = 1;
        }


}

class Car extends Thread{
    private TrafficLight t;
    private int carId;
    private int direction;
    private int arrivalTime;
    private int roadId;
    private static Runnable turnGreen;
    private Runnable crossCar;


    public Car(TrafficLight t, ArrayList<String> a, ArrayList<String> c, int carId, int direction, int arrivalTime, Runnable tG){
        this.carId = carId;
        this.direction = direction;
        this.roadId = direction == 1 || direction == 2 ? 1 : 2;
        this.arrivalTime = arrivalTime;
        this.t = t;
        turnGreen = tG;
        crossCar = () -> {
            a.add("Car " + carId +" Has Passed Road " + (roadId == 1 ? 'A' : 'B') + " In Direction " + direction);
            c.add(roadId == 1 ? "A" : "B" + carId);
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

    public static void main(String[] args) throws Exception {
        TrafficLight t = new TrafficLight();
        ArrayList<String> printArray = new ArrayList<>();
        ArrayList<String> checkArray = new ArrayList<>();
        Runnable turnGreen = () -> {
            if(currentRoad == roadA){
                currentRoad = roadB;
                printArray.add("Traffic Light On Road B Is Green");
                checkArray.add("TB");
            }
            else{
                currentRoad = roadA;
                printArray.add("Traffic Light On Road A Is Green");
                checkArray.add("TA");
            }
        };

        Scanner sc = new Scanner(System.in);
        ArrayList<Integer> cars = new ArrayList<>();
        ArrayList<Integer> directions = new ArrayList<>();
        ArrayList<Integer> arrivalTimes = new ArrayList<>();
        for (int line = 0; line < 3; line++) {
            String[] string = sc.nextLine().split(",");
            for (int j = 0; j < string.length; j++) {
                if(line == 0) {
                    cars.add(Integer.valueOf(string[j]));
                }else if (line == 1) {
                    directions.add(Integer.valueOf(string[j]));
                }
                else {
                    arrivalTimes.add(Integer.valueOf(string[j]));
                }
            }
        }
        for (int i = 0; i < cars.size(); i++) {
            Car c = new Car(t,printArray,checkArray,cars.get(i),directions.get(i),arrivalTimes.get(i),turnGreen);
            c.start();
        }
        Thread.sleep(3000);

        //checking the result
        char greenRoad = 'A';
        for (String entry : checkArray) {
            if (entry.charAt(0) == 'T') {  //light switching
                if(entry.charAt(1) == 'B'){
                    greenRoad = 'B';
                }
                else {
                    greenRoad = 'A';
                }
            }
            else { //car has passed
                if((entry.charAt(0) == 'A' && greenRoad == 'B') || (entry.charAt(0) == 'B' && greenRoad == 'A')) {
                    throw new Exception("Car " + entry.charAt(1) + " must not cross on RED!");
                }
            }
        }
        for (String string:
             printArray) {
            System.out.println(string);
        }
    }


}


