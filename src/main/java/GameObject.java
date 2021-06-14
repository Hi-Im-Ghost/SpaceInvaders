import javafx.scene.image.Image;
/** Klasa która ma za zadanie ustawianie obiektów gry na określonej pozycji, określania ich wymiarów oraz przypisywania wskazanych obrazków */
public class GameObject {
    public double posX, posY, width, height;
    Image img;
    boolean dying = false, died = false;
    int explosionStep = 0;
    /** konstruktor, który ustawia obiekt na odpowiedniej pozycji, przypisuje jej grafikę i ustawia wymiary na jej podstawie */
    public GameObject(double posX, double posY, Image img) {
        /** Ustaw obiekt na odpowiedniej pozycji */
        updatePosition(posX, posY);
        /** Przypisz grafikę do obiektu */
        this.img = img;
        /** Ustaw wymiary obiektu na podstawie wymiarów grafiki */
        setDimentions(img.getWidth(), img.getHeight());
    }
    /** konstruktor, który służy do aktualizacji pozycji obiektu */
    public GameObject(double posX, double posY) {             // Konstruktor wykonywany w przypadku niepodania grafiki
        updatePosition(posX, posY);
    }
    /** metoda, który służy do ustawienia wymiarów obiektu na podstawie obrazka */
    public void setDimentions(double width, double height) {    // Ustaw wymiary obiektu na podstawie wymiarów grafiki (np. dla pocisku)
        this.width = (float)width;
        this.height = (float)height;
    }
    /** metoda, która określona nową pozycje obiektu */
    public void updatePosition(double posX, double posY) {    // Ustaw nową pozycję obiektu
        this.posX = posX;
        this.posY = posY;
    }
    /** metoda, która sprawdza czy obiekt przekroczył podaną granice Y */
    public boolean killY(int heightBoundary) {      // Zwróć true, jeżeli obiekt przekroczy podaną granicę na osi y
        if (posY > heightBoundary || posY < 0)
            return true;
        return false;
    }
    /** metoda, która sprawdza czy obiekty ze sobą kolidują */
    public boolean isColliding (GameObject o) {
        if (( (o.posY < posY + height && o.posY > posY) && (o.posX > posX && o.posX < posX + width) ) ||
                ( (o.posY + o.height < posY + height && o.posY - o.height > posY) && (o.posX + o.width > posX && o.posX + o.width < posX + width)) )
            return true;    // true, jeżeli koliduje
        return false;       //false, jeśli nie koliduje
    }
/** metoda, która ma za zadanie tworzyć animacje eksplozji */
    public void updateExplosion() {
        /** Przejdź do następnego kroku eksplozji */
        if(dying) explosionStep++;
        /** Sprawdź, czy eksplozja została zakończona */
        died = explosionStep > Main.EXPLOSION_STEPS;
    }
/** metoda, która ma za zadanie poruszać obiekt w dół */
    public void move(double speed, boolean up) {
        if (up) {
            posY -= speed;
        } else {
            posY += speed;
        }
    }
}
