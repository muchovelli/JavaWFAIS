
public class ParallelSearcher implements ParallelSearcherInterface{
    double licznik;
    @Override
    public void set(HidingPlaceSupplierSupplier supplier) {
        HidingPlaceSupplier placeSupplier = supplier.get(0);
        Thread[] threads = new Thread[placeSupplier.threads()];
        do {
            int i = 0;
            if (threads.length > i) do {
                threads[i] = new Run(placeSupplier,this);
                threads[i].start();
                i++;
            } while (threads.length > i);
            for (Thread thread : threads) {
                try {
                    thread.join();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
            placeSupplier = supplier.get(licznik);
            licznik = 0;
        } while (placeSupplier != null);
    }
    class Run extends Thread {
        HidingPlaceSupplier skrytki;
        Object object;

        public Run(HidingPlaceSupplier placeSupplier,Object object_) {
            skrytki = placeSupplier;
            object=object_;

        }

        public void run(){
            HidingPlaceSupplier.HidingPlace hidingPlace = skrytki.get();
            while (hidingPlace != null) {
                synchronized (hidingPlace) {
                    if (hidingPlace.isPresent()) {
                        synchronized (object){
                            licznik = licznik + hidingPlace.openAndGetValue();
                        }
                    }
                }
                hidingPlace = skrytki.get();
            }
        }
    }
}

