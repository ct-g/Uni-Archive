// Conor Tumbers
// c3190729@uon.edu.au
// COMP2240, Assignment 2, Problem C
// Last modified 11/10/19
// A coffee machine that can serve up to 2 clients simultaneously

class A2CCoffeeMachine {
    private static A2CTemp state; // All dispensers must be serving the same temperature
    boolean h1Available;
    boolean h2Available;

    A2CCoffeeMachine() {
        state = A2CTemp.HOT;
        h1Available = true;
        h2Available = true;
    }

    synchronized static void invertTemp() {
        if (state == A2CTemp.HOT) {
            state = A2CTemp.COLD;
        } else {
            state = A2CTemp.HOT;
        }
    }

    synchronized static A2CTemp getTemp() {
        return state;
    }
}
