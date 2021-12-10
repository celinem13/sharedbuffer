import java.util.concurrent.Semaphore;
import java.util.Random;

public class Main extends Thread{
    static final int SIZE = 10;
    static final int LOOP = 20;
    static int [] buffer = new int[SIZE];
    static int max = LOOP;
    static int in = 0 , out = 0;
    static Semaphore emptyB = new Semaphore(buffer.length);
    static Semaphore occupied = new Semaphore(0);
    static boolean finished = false;
    static Random r = new Random();

    public static void main(String[] args) throws InterruptedException{
        Thread thread1 = new Thread(() -> {
            try{
                consumer_thread();
            }catch(InterruptedException e){
                e.printStackTrace();
            }
        });
        Thread thread2 = new Thread(() -> {
            try {
                producer_thread();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        });
        thread1.start();
        thread2.start();
        thread1.join();
        thread2.join();
    }

    public static void producer_thread() throws InterruptedException{
        while(!finished){
            int k1 = r.nextInt(SIZE/2)+1;
            for(int i = 0; i < k1-1; i++){
                if(emptyB.availablePermits()>0){
                    emptyB.acquire();
                    buffer[i] = 1;
                    occupied.release();
                }
                else{
                    break;
                }
            }
            in = (in + k1) % buffer.length;
            System.out.println("So far producer thread OK. next_in = " + in);
            max--;
            if(max <= 0){
                System.out.println("Producer exits system without any race problem.");
                finished = true;
            }
            if(finished){
                System.out.println("Exiting producer thread loop.");
            }
            Thread.sleep((int) (Math.random()*900 + 100));
        }
    }

    public static void consumer_thread() throws InterruptedException {
        while (!finished){
            Thread.sleep((int) (Math.random()*900 + 100));
            int k2 = r.nextInt(SIZE/2) + 1;
            int data;
            for(int i = 0; i < k2-1; i++){
                occupied.acquire();
                data=buffer[i];
                if(data != 1){
                    System.out.println("No value available in the buffer.");
                    occupied.release();
                }
            }
            out = (out + k2) % buffer.length;
            System.out.println("So far consumer thread OK. next_out = " + out);
            max--;
            if(max <= 0) {
                System.out.println("Consumer exits system without any race problem.");
                finished = true;
            }
            if(finished){
                System.out.println("Exiting consumer thread loop.");
            }
            Thread.sleep((int) (Math.random()*900 + 100));
        }
    }
}