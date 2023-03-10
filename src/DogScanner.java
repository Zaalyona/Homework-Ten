import java.util.ArrayList;

public class DogScanner {
    public void scanDogs(ArrayList<Dog> dogs) {
        for (Dog dog : dogs) {
            System.out.println(dog.height);
        }
    }
}
