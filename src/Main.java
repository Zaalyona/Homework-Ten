import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedList;

public class Main {
    public static void main(String[] args) {

        ArrayList<String> strings = new ArrayList<>();
        strings.add("Привет");
        strings.add("меня");
        strings.add("зовут");
        strings.add("Массив");

        strings.add(1, "не");

        /*for (String str : strings) {
            System.out.println(str);
        }*/

        CustomLinkedList<String> linkedStrings = new CustomLinkedList<>();
        linkedStrings.add("Привет");
        linkedStrings.add("меня");
        linkedStrings.add("зовут");
        linkedStrings.add("Массив");

        linkedStrings.add(1, "не");

        /*for (String str : linkedStrings) {
            System.out.println(str);
        }*/

        Iterator<String> iterator = new Iterator<String>() {
            @Override
            public boolean hasNext() {
                return false;
            }

            @Override
            public String next() {
                return null;
            }
        };

        ArrayList<Dog> dogs = new ArrayList<>();

        for (int i = 1; i < 100; i++) {
            Dog dog = new Dog();
            dog.height = i;
            dogs.add(dog);
        }

        //DogScanner dogScanner = new DogScanner();
        //dogScanner.scanDogs(dogs);

        //Поиск ArrayList
        for (Dog dog : dogs) {
            if (dog.height == 43) {
                System.out.println("Нашёл собаку с весом " + dog.height);
                break;
            }
        }

        //Поиск LinkedList
        for (String str : linkedStrings) {
            if (str.equals("меня")) {
                System.out.println("Нашёл в коллекции LinkedList слово: " + str);
            }
        }

        //Удаление в ArrayList
        try {
            dogs.remove(3);
        } catch (IndexOutOfBoundsException e) {
            System.out.println("Такой собаки нет...");
            return;
        }
        System.out.println("Массив собак после удаление одной выглядит теперь вот так:");
        DogScanner dogScanner = new DogScanner();
        dogScanner.scanDogs(dogs);

        //Удаление в LinkedList
        try {
            linkedStrings.remove(2);
        } catch (IndexOutOfBoundsException e) {
            System.out.println("Такого элемента в LinkedList нет...");
            return;
        }
        System.out.println("Коллекция LinkedList теперь выглядит вот так:");
        for (String str : linkedStrings) {
            System.out.println(str);
        }
    }
}