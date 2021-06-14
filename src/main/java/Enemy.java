import javafx.scene.image.Image;

/** Klasa określające prędkość spadania kosmitów oraz ich pozycje */
public class Enemy extends GameObject{
    float speed;
    /** metoda służacą to przypisywania prędkości, pozycji oraz obrazka kosmitą */
    public Enemy(float posX, float posY, Image image, float speed) {
        super(posX, posY, image);
        this.speed = speed;
        img = image;
    }

    /** metoda odpowadajaca za ruch w dół o określonej prędkości */
    @Override
    public void move(double speed, boolean up) {
        if (!dying)             // Stój w miejscu, gdy wybucha
            super.move(this.speed / 10, up);
    }

    /** Sprawdź, czy obiekt nie znajduje się w strefie podanego punktu na osi y */
    public boolean isCollidingY (int y) {
        /** dodatkowe 50 służy zwiększeniu odstępu pomiędzy falami */
        if (y < posY && y > posY - height - 50)
        /** true, jeżeli koliduje */
            return true;
        /** false, jeśli nie koliduje */
        return false;
    }
}
