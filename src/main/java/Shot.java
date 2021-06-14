/** Klasa, która ma za zadanie określenie pozycji oddanego strzału, prędkości i sprawdzenia trafienia */
public class Shot extends GameObject{
    float speed;
    boolean hit = false;
    /** konstruktor, który tworzy strzał w danej pozycji o określonej prędkości */
    public Shot(double posX, double posY, float speed) {
        super(posX, posY);
        this.speed = speed;
    }
}
