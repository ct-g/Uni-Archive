// Conor Tumbers
// c3190729@uon.edu.au
// SENG2200, Assignment 3
// Last modified 25/06/20

public final class Singleton {
    private static final Singleton INSTANCE = new Singleton();
    private int idNum; // Number used to generate a string ID

    private Singleton() {
        idNum = 0;
    }

    public static Singleton getInstance() {
        return INSTANCE;
    }

    // Generate a string ID that is unique for this program's runtime
    public StringBuilder getID () {
        StringBuilder id = new StringBuilder(String.format("%06d", idNum));
        idNum++;
        return id;
    }
}
