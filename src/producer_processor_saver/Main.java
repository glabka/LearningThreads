package producer_processor_saver;

public class Main {

    public static void main(String[] args) {
        Logic logic = new Logic(100, 5, 5, 1000, 5000, 1000);

        Thread loader = new Thread(new Runnable() {
            @Override
            public void run() {
                logic.loadAll();
            }
        });

        Thread processor = new Thread(new Runnable() {
            @Override
            public void run() {
                logic.processAll();
            }
        });

        Thread storage = new Thread(new Runnable() {
            @Override
            public void run() {
                logic.storeAll();
            }
        });

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
