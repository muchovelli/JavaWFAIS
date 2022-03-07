import java.util.*;
import java.util.stream.IntStream;

import static java.util.Comparator.*;

public class Compression implements CompressionInterface {
    Kompresja kompresja;
    ArrayList<Kompresja> kompresjaArrayList = new ArrayList<>();
    ArrayList<Entity> entities = new ArrayList<>();
    ArrayList<String> stringArrayList = new ArrayList<>();
    int sizeOfWord;
    int counter;
    @Override
    public void addWord(String word) {
        if (entities.size() > 0 && word.length() != sizeOfWord) return;
        if (word.length() < 9) {
            Entity entity = null;
            for (int i = 0; i < entities.size(); i++) {
                Entity list = entities.get(i);
                if (word.equals(list.string)) {
                    entity = list;
                    break;
                }
            }

            if (entity == null) {
                Entity en = new Entity();
                en.i = 1;
                en.string = word;
                entities.add(en);
                if (entities.size() == 1) {
                    sizeOfWord = word.length();
                }
            } else {
                entity.i++;
            }
            stringArrayList.add(word);
        } else {
            return;
        }
    }

    @Override
    public Map<String, String> getHeader() {
        return kompresja.naglowek;
    }

    @Override
    public String getWord() {
        if (kompresja.ListOfWords.size() > 0) {
            counter++;
            return kompresja.ListOfWords.get(counter - 1);
        } else {
            return "";
        }
    }

    @Override
    public void compress() {
        Kompresja kompresja = new Kompresja();
        kompresja.naglowek = new HashMap<>();
        kompresja.ListOfWords = stringArrayList;
        kompresja.sizeOfKompresion = getSize( kompresja.ListOfWords);
        ArrayList<Kompresja> ArrayListSorted;
        List<Entity> collect = new ArrayList<>();
        ArrayList<ArrayList<Entity> > lists = new ArrayList<>();
        ArrayList<Entity> tempList = new ArrayList<>();
        ArrayList<String> listOfB = new ArrayList<>();
        List<Entity> list = new ArrayList<>();
        String s1,s2;
        int i1, i2;
        kompresjaArrayList.add( kompresja );
        for (Entity entity1 : entities) {
            list.add(entity1);
        }
        list.sort(comparing((Entity entity1) -> entity1.getI())
                .reversed());
        entities = (ArrayList<Entity>) list;
        for (int i = 0; i < entities.size(); i++) {
            Entity x = entities.get(i);
            if (x.getI() > 1) collect.add(x);
        }
        {
            int i = (int) Math.pow(2, collect.size()) - 1;
            while (i >= 0) {

                s1 = Integer.toBinaryString( (int) Math.pow( 2, collect.size()) | i ).substring(1);

                int j = 0;
                while (j < collect.size()) {
                    if ( s1.charAt(j) == '1' ) {
                        tempList.add( collect.get(j) );
                    }
                    j++;
                }

                lists.add( tempList );
                tempList = new ArrayList<>();
                i--;
            }
        }

        for (Iterator<ArrayList<Entity>> iterator = lists.iterator(); iterator.hasNext(); ) {
            ArrayList<Entity> entity = iterator.next();
            i2 = (int) (Math.log(entity.size()) / Math.log(2) + 1);
            listOfB.clear();
            i1 = entity.size();
            if (i1 > 0) {
                {
                    int i = i1 - 1;
                    while (i >= 0) {
                        StringBuilder s = new StringBuilder();
                        String toBinaryString = Integer.toBinaryString(i);
                        switch (i2) {
                            case 1:
                                s2 = "0";
                                break;
                            default:
                                s.append("0".repeat(Math.max(0, i2 - toBinaryString.length())));
                                s2 = s + toBinaryString;
                                String buffor = Integer.toBinaryString(i1 - 1);
                                if (buffor.charAt(0) == '1' && i2 <= buffor.length()) {
                                    s2 = new StringBuilder().append("0").append(s).append(toBinaryString).toString();
                                }
                                break;
                        }
                        listOfB.add(s2);
                        i--;
                    }
                }
                Kompresja kompresja1 = new Kompresja();
                kompresja1.naglowek = new HashMap<>();
                kompresja1.ListOfWords = new ArrayList<>();
                IntStream.range(0, entity.size()).forEachOrdered(i -> kompresja1.naglowek.put(listOfB.get(i), entity.get(i).string));
                int i = 0;
                while (i < stringArrayList.size()) {
                    String s = stringArrayList.get(i);
                    String o = kompresja1.naglowek.entrySet().stream().filter(in -> in.getValue().equals(s)).findFirst().map(Map.Entry::getKey).orElse(null);
                    if (o == null) {
                        o = "1" + s;
                    }
                    kompresja1.ListOfWords.add(o);
                    i++;
                }
                kompresja1.sizeOfKompresion = getSizeOfHeader(kompresja1.naglowek) + getSize(kompresja1.ListOfWords);

                if (getSize(stringArrayList) >= kompresja1.sizeOfKompresion) {
                    kompresjaArrayList.add(kompresja1);
                }
            }
        }

        if( kompresjaArrayList.size() > 0 ) {
            List<Kompresja> result = new ArrayList<>();
            for (int i = 0; i < kompresjaArrayList.size(); i++) {
                Kompresja kompresja1 = kompresjaArrayList.get(i);
                result.add(kompresja1);
            }
            result.sort(comparing(Kompresja::getSizeOfKompresion));
            ArrayListSorted = (ArrayList<Kompresja>) result;

            this.kompresja = ArrayListSorted.get(0);
        }
    }

    static class Kompresja {
        public ArrayList<String> ListOfWords;
        public Map<String, String> naglowek = new HashMap<>();
        private int sizeOfKompresion;
        public int getSizeOfKompresion() { return sizeOfKompresion; }
    }

    static class Entity {
        public String string;
        private int i;
        public int getI() { return i; }
    }
    private int getSize(ArrayList<String> list ) {
        int i = 0;
        for( String s : list )
            i += s.length();
        return i;
    }
    private int getSizeOfHeader(Map<String,String> map ) {
        int i = 0;
        int i1 = 0;
        for ( Map.Entry<String, String> in : map.entrySet() ) {
            i += in.getValue().length();
            i1   += in.getKey().length();
        }
        return (i + i1);
    }
}