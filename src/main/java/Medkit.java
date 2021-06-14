import javafx.scene.image.Image;

/** Klasa służy do określanie pozycji medkita i jego ruch */
public class Medkit extends GameObject {

    boolean destroy = false;
    /** konstruktor, który tworzy medkit o danym obrazku na pozycji określonej przez parametr */
    public Medkit(double posX, double posY, Image img) {
        super(posX, posY, img);
    }

    /** Ruch w dół */
    public void move() {
        posY += 7;
    }
}
