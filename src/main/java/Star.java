import javafx.scene.paint.Color;

import java.util.Random;
/** Klasa która odpowiada za tworzenie spadających "gwiazd" w tle o różnych kolorach i w różnych miejscach */
public class Star extends GameObject {
    private int r, g, b;
    private double opacity;
    Random RAND = new Random();
    /** konstruktor, który tworzy gwiazde o losowym kolorze, rozmiarze i umieszcza ją w określonej pozycji */
    public Star(double posX, double posY, int WIDTH) {
        super(posX, 0);

        setDimentions(RAND.nextInt(5) + 1, RAND.nextInt(5) + 1);
        r = RAND.nextInt(100) + 150;
        g = RAND.nextInt(100) + 150;
        b = RAND.nextInt(100) + 150;
        opacity = RAND.nextFloat();
        if(opacity < 0) opacity = 0.1;
        if(opacity > 0.8) opacity = 0.75;
    }
    /** metoda która ma za zadanie zwrócić kolor */
    public Color getColor() {
        return Color.rgb(r, g, b, opacity);
    }
    /** metoda, która odpowiada za ruch gwiazd */
    public void move() {
        updatePosition(posX, posY += 13);
    }
}
