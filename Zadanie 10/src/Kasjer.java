import java.util.List;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.function.Supplier;

public class Kasjer implements KasjerInterface {
    List<Pieniadz> pieniadze = new ArrayList<>();
    List<Pieniadz> result = new ArrayList<>();
    List<Pieniadz> listOfPieniadze;
    int toChange, cantChange, i1, givenChange;
    RozmieniaczInterface rozmieniaczInterface;

    public List<Pieniadz> rozlicz(int cena, List<Pieniadz> pieniadze) {
        listOfPieniadze = new ArrayList<>(pieniadze);
        toChange = 0;
        cantChange = 0;
        for (int i = 0; i < pieniadze.size(); i++) {
            Pieniadz pieniadz = pieniadze.get(i);
            if (!pieniadz.czyMozeBycRozmieniony()) {
                cantChange = cantChange + pieniadz.wartosc();
            } else {
                toChange = toChange + pieniadz.wartosc();
            }
        }
        i1 = toChange + cantChange;
        givenChange = i1 - cena;
        if (i1 != cena) {
            if (cantChange > cena) {
                int i2 = 0;
                for (int i = 0; i < listOfPieniadze.size(); i++) {
                    Pieniadz pieniadz = listOfPieniadze.get(i);
                    if (!pieniadz.czyMozeBycRozmieniony()) {
                        i2 = pieniadz.wartosc();
                    } else if (!pieniadz.czyMozeBycRozmieniony())
                        if (i2 > pieniadz.wartosc()) {
                            i2 = pieniadz.wartosc();
                        }
                }
                for (int i = 0; i < listOfPieniadze.size(); i++) {
                    Pieniadz pieniadz = listOfPieniadze.get(i);
                    if (!pieniadz.czyMozeBycRozmieniony()) {
                        if (pieniadz.wartosc() == i2) {
                            result.add(pieniadz);
                        } else {
                            this.pieniadze.add(pieniadz);
                        }
                    }
                }
                listOfPieniadze.addAll(this.pieniadze);
                boolean isBigger = true;
                if (isBigger) {
                    do {
                        isBigger = false;
                        List<Pieniadz> changed = new ArrayList<>();
                        Iterator<Pieniadz> pieniadzIterator = this.pieniadze.iterator();
                        if (pieniadzIterator.hasNext()) {
                            do {
                                Pieniadz next = pieniadzIterator.next();
                                if (next.czyMozeBycRozmieniony())
                                    if (next.wartosc() != 1) {
                                        changed = rozmieniaczInterface.rozmien(next);
                                        pieniadzIterator.remove();
                                        isBigger = true;
                                        break;
                                    }
                            } while (pieniadzIterator.hasNext());
                        }
                        this.pieniadze.addAll(changed);
                    } while (isBigger);
                }
                int i = 0;
                while (i < givenChange) {
                    Iterator<Pieniadz> pieniadzIterator = this.pieniadze.iterator();
                    if (pieniadzIterator.hasNext()) {
                        do {
                            Pieniadz next = pieniadzIterator.next();
                            if (next.czyMozeBycRozmieniony()) {
                                result.add(next);
                                pieniadzIterator.remove();
                                break;
                            }
                        } while (pieniadzIterator.hasNext());
                    }
                    i++;
                }
                return result;
            } else {
                boolean toBig = true;
                if (toBig) {
                    do {
                        List<Pieniadz> toChange = new ArrayList<>();
                        toBig = false;
                        Iterator<Pieniadz> pieniadzIterator = listOfPieniadze.iterator();
                        if (pieniadzIterator.hasNext()) {
                            do {
                                Pieniadz next = pieniadzIterator.next();
                                if (next.czyMozeBycRozmieniony())
                                    if (next.wartosc() != 1) {
                                        toChange = rozmieniaczInterface.rozmien(next);
                                        pieniadzIterator.remove();
                                        toBig = true;
                                        break;
                                    }
                            } while (pieniadzIterator.hasNext());
                        }
                        listOfPieniadze.addAll(toChange);
                    } while (toBig);
                }
                int i = 0;
                while (i < givenChange) {
                    Iterator<Pieniadz> pieniadzIterator = listOfPieniadze.iterator();
                    if (pieniadzIterator.hasNext()) {
                        do {
                            Pieniadz next = pieniadzIterator.next();
                            if (next.czyMozeBycRozmieniony()) {
                                result.add(next);
                                pieniadzIterator.remove();
                                break;
                            }
                        } while (pieniadzIterator.hasNext());
                    }
                    i++;
                }
                this.pieniadze.addAll(listOfPieniadze);
            }
            return result;
        } else {
            this.pieniadze.addAll(listOfPieniadze);
            return result;
        }
    }

    public List<Pieniadz> stanKasy() {
        return pieniadze;
    }

    public void dostępDoRozmieniacza(RozmieniaczInterface rozmieniacz) {
        this.rozmieniaczInterface = rozmieniacz;
    }

    public void dostępDoPoczątkowegoStanuKasy(Supplier<Pieniadz> dostawca) {
        Pieniadz pieniadz = dostawca.get();
        while (pieniadz != null) {
            pieniadze.add(pieniadz);
            pieniadz = dostawca.get();
        }
    }
}