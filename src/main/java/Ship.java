import javafx.scene.image.Image;

/** Klasa która odpowiada za tworzenie statku gracza o danym obrazku na wskazanej pozycji oraz umożliwienie poruszania się i strzelania */
public class Ship extends GameObject {

    /** konstruktor, który tworzy statek o wskazanym obrakzu na podanej pozycji */
    public Ship(double posX, double posY, Image image) {
        /** Wykonanie metody konstruktora GameObject, który przypisuje posX i poxY, oraz wymiary */
        super(posX, posY, image);
    }

    /** metoda, która ma za zadanie aktualizacji naszego statku gdy się ruszamy */
    @Override
    public void updatePosition(double posX, double posY) {
        /** Jeśli nie wybucha, wykonaj ruch za pomocą funkcji z GameObject */
        if (!dying)
            super.updatePosition(posX, posY);
    }
    /** metoda, która umożliwinia strzelnie */
    public Shot Shoot() {
        return new Shot(posX, posY, 10);
    }
}
