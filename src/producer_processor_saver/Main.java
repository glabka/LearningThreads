package producer_processor_saver;

public class Main {

    public static void main(String[] args) {
        Logic logic = new Logic(100, 5, 5, 1000, 5000, 1000);

        Thread loader = new Thread(() -> logic.loadAll());
        Thread processor = new Thread(() -> logic.processAll());
        Thread storage = new Thread(() -> logic.storeAll());

        loader.start();
        processor.start();
        storage.start();

        try {
            loader.join();
            processor.join();
            storage.join();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        System.out.println("program ended.");
    }
}
