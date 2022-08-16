public class MainTMP {

    public static void main(String[] args) throws InterruptedException {
        MainTMP tmp = new MainTMP();
        Thread t1 = new Thread(() -> tmp.firstMethod());
        Thread t2 = new Thread(() -> tmp.secondMethod());

        t1.start();
        Thread.sleep(1000);
        t2.start();

        t1.join();
        t2.join();
    }

    public synchronized void firstMethod() {
        System.out.println("1. something.");
        try {
            wait();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        System.out.println("2. something.");
    }

    public synchronized void secondMethod() {
        System.out.println("3. something.");
        notify();
        System.out.println("4. something.");
    }
}
