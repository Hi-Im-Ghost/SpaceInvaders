import javafx.scene.image.Image;
/** Klasa odpowiadająca za przycisk, tworzy go w określonym miejscu i pozycji oraz sprawdza czy został kliknięty */
public class Button extends GameObject {

    public boolean isHoveringOver = false;
    Image hoveringImg;
    /** konstruktor który tworzy przycisk o określonym obrazku i na określonej pozycji */
    public Button(double posX, double posY, Image img, Image hoveringImg) {
        super(posX, posY, img);
        this.hoveringImg = hoveringImg;
    }
    /** metoda sprawdzająca czy przycisk został wciśnięty */
    public boolean buttonColliding(double posX, double posY) {
        return super.isColliding(new GameObject(posX, posY));
    }
    /** metoda pozwalająca zasygnalizować wciśnięcie */
    public void acion() {
        System.out.println("Click!");
    }
}
