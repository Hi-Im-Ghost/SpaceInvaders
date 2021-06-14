/** Space Invaders.
 * @author Adrian Chmielowiec
 * @author Mateusz Kubas
 * @author Tomasz Kubik
 * @version 1.0
 */

import java.awt.event.KeyEvent;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Random;
import java.util.stream.IntStream;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.application.Application;
import javafx.geometry.VPos;
import javafx.scene.Cursor;
import javafx.scene.Scene;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.image.Image;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.TextAlignment;
import javafx.stage.Stage;
import javafx.util.Duration;
import javafx.scene.control.Label;

/**Klasa główna, która odpowiada za przechowywanie plików graficznych, wymiarów, scen, wartości oraz odpowiada za uruchomienie gry */
public class Main extends Application {
    /** Pobierz grafikę gracza */
    static final Image PLAYER_IMG = new Image("file:grafiki/statek.png");
    /**Pobierz grafikę ekspozji */
    static final Image EXPLOSION_IMG = new Image("file:grafiki/explosion.png");
    /** Pobierz grafikę przeciwnika */
    static final Image ENEMY_IMG = new Image("file:grafiki/wrog.png");
    /** Pobierz grafikę życia */
    static final Image LIFE_IMG = new Image("file:grafiki/zycie.png");

    /** Grafika przycisku START na którego nie najechał kursor */
    static final Image START_BUTTON_0 = new Image("file:grafiki/start0.png");
    /** Grafika przycisku START, gdy gracz najechał na niego kursorem */
    static final Image START_BUTTON_1 = new Image("file:grafiki/start1.png");
    /** Grafika tytułową */
    static final Image TITLE_IMG = new Image("file:grafiki/title.png");
    /** Pobierz grafikę z wersją */
    static final Image VERSION_IMG = new Image("file:grafiki/ver.png");


    private static final Random RAND = new Random();
    /** Wysokość okna z grą */
    private static final int WIDTH = 1024;
    /** Szerokość okna z grą */
    private static final int HEIGHT = 768;

    static final int EXPLOSION_W = 128;
    static final int EXPLOSION_ROWS = 3;
    static final int EXPLOSION_COL = 3;
    static final int EXPLOSION_H = 128;
    static final int EXPLOSION_STEPS = 15;

    enum ESceneType {
        MainMenu,
        Gameplay
    }

    /**  MAIN MENU
     * Deklaracja przyciku START w menu głównym
     */
    Button startButton;

    /** GAME STATE
     * Określa, czy gra została zakończona
     */
    boolean gameOver = false;
    /** Do nich zapisywana jest pozycja myszy. Podczas gry tylko do mouseX */
    private double mouseX, mouseY;
    private GraphicsContext gc;
    /** Współrzędne granic siatki, na jaką dzielona jest plansza */
    int[] ySectors, xSectors;
    /** Ilość zdobytych punktów */
    private int score;
    /** Lista gwiazd spadających w tle podczas gry */
    List<Star> stars = new ArrayList<>();
    /** Obiekt klasy odpowiadającej za obsługę dźwięków */
    Music music = new Music();
    /** Typ wyświetlanej sceny (Menu główne / Gra) */
    ESceneType sceneType;
    /** Scena z menu głównym */
    Scene menuScene;
    /** Scena z grą */
    Scene gameScene;

    /** PLAYER
     * Postać gracza
     */
    Ship player;
    /** Życia gracz i ich maksymalna ilość */
    int lives, maxLives = 5;
    /** Lista wystrzelonych pocisków */
    List<Shot> shots = new ArrayList<>();
    /** Lista spadających apteczek */
    List<Medkit> medkits = new ArrayList<>();

    /** ENEMIES
     * Lista wrogów
     */
    List<Enemy> enemies = new ArrayList<>();
    /** Prędkość nowo tworzonych wrogów */
    float enemySpeed;
    /** Ilość pustych miejsc między wrogami */
    int spaces = 4;
    /**metoda start, która odpowiada za tworzenie płótn gry i menu oraz licznika dla gry i wykonywania zdarzeń */
    public void start(Stage stage) {
        /** Utworzenie "płótna" z grą */
        Canvas canvasGame = new Canvas(WIDTH, HEIGHT);
        /** Utworzenie "płótna" z menu głównym */
        Canvas canvasMenu = new Canvas(WIDTH, HEIGHT);
        /** Wyświetlanie płótna menu głównego */
        gc = canvasMenu.getGraphicsContext2D();
        /** Ustawienie stałej ilości klatek na sekundę */
        float fps = 60;
        /** Chodzący w nieskończoność licznik wykonujący się co 1000 / fps milisekund wykonyjąc przy tym funkcję run() */
        Timeline timeline = new Timeline(new KeyFrame(Duration.millis(1000 / fps), e -> run(gc)));
        timeline.setCycleCount(Timeline.INDEFINITE);
        timeline.play();

        /** Chodzący w nieskończoność licznik wykonujący się tylko co 100 milisekund i obsługujący rysowanie eksplozji
        * Licznik ten zapewnia wyświetlanie eksplozji w odpowiednim tempie */
        Timeline explosionTimeline = new Timeline(new KeyFrame(Duration.millis(100), e -> drawExplosions(true)));
        explosionTimeline.setCycleCount(Timeline.INDEFINITE);
        explosionTimeline.play();

        /** Wydarzenia wykonywane przez płótno z grą
         * Ustawienie grafiki kursora
         */
        canvasGame.setCursor(Cursor.MOVE);
        /** Gdy gracz poruszy myszką, do zmiennej mouseX zostanie zapisana jej pozycja X */
        canvasGame.setOnMouseMoved(e -> mouseX = e.getX());
        /** Gdy gracz naciśnie i poruszy stanie się to co wyżej. Jest to wymagane, by gracz nie zatrzymywał się podczas strzelania */
        canvasGame.setOnMouseDragged(e -> mouseX = e.getX());
        /** Gdy przycisk myszy zostanie naciśnięty... */
        canvasGame.setOnMousePressed(e -> {
            /** Wystrzel pocisk */
            shots.add(player.Shoot());
            /** Graj dźwięk wystrzału */
            music.playShot();
            /** Jeśli gra jest zakończona, zresetuj rozgrywkę */
            if(gameOver) {
                gameOver = false;
                setup();
            }
        });

        /** Wydarzenia wykonywane przez płótno z menu głównym
         * Gdy mysz się porusza...
         */
        canvasMenu.setOnMouseMoved(e -> {
            /** Zapisz jej pozycję X w mouseX */
            mouseX = e.getX();
            /** Zapisz jej pozycję Y w mouseY */
            mouseY = e.getY();
            /** Sprawdź, czy nie znajduje się nad przyciskiem start */
            startButton.isHoveringOver = startButton.buttonColliding(mouseX, mouseY);
        });
        /** Gdy przycisk myszy zostanie naciśnięty... */
        canvasMenu.setOnMousePressed(e -> {
            /** Sprawdź, czy kursor znajduje się nad przyciskiem start */
                if (startButton.isHoveringOver)
                /** Jeśli tak, przejdź do rozgrywki */
                    switchToGameplay(stage, canvasGame);
        });
        /** Przypisanie do sceny menuScene nową scenę na podstawie płótna menu głównego */
        menuScene = new Scene(new StackPane(canvasMenu));
        /** Przypisanie do sceny gameScene nową scenę na podstawie płótna z grą */
        gameScene = new Scene(new StackPane(canvasGame));
        /** Twworzenie przycisku START */
        startButton = new Button(WIDTH/2d - START_BUTTON_0.getWidth() / 2d, HEIGHT / 2d, START_BUTTON_0, START_BUTTON_1);
        /** Ustawienie początkowego typu sceny na menu główne */
        sceneType = ESceneType.MainMenu;
        /** ustawienie sceny menu głównego jako głównej sceny */
        stage.setScene(menuScene);
        /** Ustawienie tytułu w oknie gry */
        stage.setTitle("Space Invaders");
        /** Wyłącza możliwość zmiany rozmiaru okienka */
        stage.setResizable(false);
        /** Zmienia ikonkę gry na ikonę przeciwnika */
        stage.getIcons().add(new Image("file:grafiki/wrog.png"));
        /** Rozpoczyna pokazywanie sceny */
        stage.show();
        /** Zaczyna grać muzykę menu głównego */
        music.playMainmenu();
    }
    /**metoda, która ma za zadanie ustawić wszystko co potrzeba do rozgrywki */
    private void switchToGameplay(Stage stage, Canvas canvas) {
        /** Ustawienie wszystkiego co trzeba do rozgrywki */
        setup();
        /** Ustawienie płótna z grą jako domyślnego bufora renderowania grafiki */
        gc = canvas.getGraphicsContext2D();
        /** Ustawienie sceny domyślnej na scenę z grą */
        stage.setScene(gameScene);
        /** Ustawienie typu wyświetlanej sceny na scenę z rozgrywką */
        sceneType = ESceneType.Gameplay;
        /** Zakończ granie muzyki z menu głównego */
        music.stopMainmenu();
        /** Graj muzykę podczas rozgrywki */
        music.playBackground();
    }
    /** metoda, która ma za zadanie utworzyć gracza, przeciwników na okeślonych pozycjach */
    private void setup() {
        /** Utwórz postać gracza */
        player = new Ship(WIDTH / 2d, HEIGHT - 70d, PLAYER_IMG);

        /** Szatkowanie planszy w kratkę
         * Weź wysokość obrazka przeciwnika
         */
        int enemyH = (int)ENEMY_IMG.getHeight();
        /** Weź szerokość obrazka przeciwnika */
        int enemyW = (int)ENEMY_IMG.getWidth();
        /** Podziel okno w osi y na paski wielkości wysokości przeciwnika */
        ySectors = DivideEqual(HEIGHT, enemyH,0, 0);
        /** Podziel okno w osi x na paski wielkości szerokości przeciwnika */
        xSectors = DivideEqual(WIDTH, enemyW + 20, 50, 0);

        /** Reset zmiennych do stanu domyślnego */
        score = 0;
        enemySpeed = 3;
        lives = 3;

        /** Utworzenie początkowych przeciwników */
        spawnEnemies(3);
    }

    /** metoda, która odpowaida za rysowanie i gameplay np. sprawdzanie czy pocisk trafił */
    private void run(GraphicsContext gc) {
        /** Ustaw kolor wypełniania na szary */
        gc.setFill(Color.grayRgb(20));
        /** Wypewnij tło szarym kolorem */
        gc.fillRect(0, 0, WIDTH, HEIGHT);
        /** Ustaw centrowanie wyświetlanego tekstu */
        gc.setTextAlign(TextAlignment.CENTER);
        /** Zawsze rysuj gwiazdy */
        drawStars();
        /** Ze wględu na typ wyświetlanej sceny... */
        switch (sceneType) {
            case MainMenu -> {
                /** Jeżeli kursor jest nad przyciskiem START zastosuj drugą grafikę tego przycisku */
                if (startButton.isHoveringOver)
                    gc.drawImage(startButton.hoveringImg, startButton.posX, startButton.posY, startButton.width, startButton.height);
                else
                    gc.drawImage(startButton.img, startButton.posX, startButton.posY, startButton.width, startButton.height);
                /** Rysuj grafikę tytułową */
                gc.drawImage(TITLE_IMG, WIDTH / 2d - 300, HEIGHT / 2d - 400, 600, 400);
                /** Rysuj grafikę z wersją gry */
                gc.drawImage(VERSION_IMG, 20, HEIGHT - 38);
            }
            case Gameplay -> {

                /** Wykonuj podczas trwania gry */
                if (!gameOver) {
                    gc.setFont(Font.font(20));
                    gc.setFill(Color.WHITE);
                    gc.fillText("Wynik: " + score, WIDTH / 2f, 30);
                    /** Aktualizacja pozycji wzkaźnika myszy */
                    player.posX = (float) mouseX;
                    /** Aktualizacja pozycji gracza w stosunku do pozycji myszy */
                    player.updatePosition((float) mouseX, player.posY);

                    /** Dla każdego wroga */
                    for (Enemy enemy : enemies) {
                        /** Porusz się */
                        enemy.move(0, false);
                        for (Shot shot : shots) {
                            /** jeżeli koliduje z pociskiem i nie jest w trakcie niszczenia */
                            if (enemy.isColliding(shot) && !enemy.dying) {
                                /** Wróg rozpoczyna niszczenie */
                                enemy.dying = true;
                                /** Pocisk trafił */
                                shot.hit = true;
                                /** Dodanie pukntów */
                                score += 1;
                                if (score % 10 < 1)
                                    speedUp();

                                if (RAND.nextInt(100) < 2) {
                                    medkits.add(new Medkit(enemy.posX, enemy.posY, LIFE_IMG));
                                }
                            }
                        }
                        if (enemy.killY(HEIGHT)) {
                            lives--;
                        }
                    }

                    /** Dla każdego wystrzału */
                    for (Shot shot : shots) {
                        /** Porusz się */
                        shot.move(shot.speed, true);
                    }

                    /**Dla każdej apteczki */
                    for (Medkit medkit : medkits) {
                        /** Ruch */
                        medkit.move();
                        /** Jeżeli weszła w kolizję z graczem */
                        if (medkit.isColliding(player)) {
                            /** Sazygnalizuj chęć zniszczenia apteczki */
                            medkit.destroy = true;
                            /** Nie można dodać więcej żyć niż max */
                            if (lives < maxLives) {
                                lives++;
                                music.playMedkit();
                            }
                        }
                    }
                    /** Zniszcz, jeżeli zleci poza mapę, lub zostanie zniszczony */
                    enemies.removeIf(enemy -> enemy.killY(HEIGHT) || enemy.died);
                    /** Zniszcz, jeżeli wyleci poza mapę, lub trafi */
                    shots.removeIf(shot -> shot.killY(HEIGHT) || shot.hit);
                    /** Zniszcz, jeżeli apteczka sygnalizuje, że ma zostać zniszczona */
                    medkits.removeIf(medkit -> medkit.destroy);

                    /** Kiedy przeciwnicy przelecą pewną odległość, lub nie ma przeciwników przywołaj następną falę */
                    if (enemies.isEmpty())
                        spawnEnemies(spaces);
                    else if (!enemies.get(enemies.size() - 1).isCollidingY(ySectors[1]))
                        spawnEnemies(spaces);

                    /** Kiedy życie spadnie poniżej 1 inicjuj procedurę końca gry */
                    if (lives < 1) {
                        player.dying = true;
                        gameOver = true;
                    }

                    /** Wykonuj gdy gra się zakończy */
                } else {
                    clearAllObjects();

                    gc.setFont(Font.font(35));
                    gc.setFill(Color.RED);
                    gc.fillText("Koniec gry!", WIDTH / 2f, HEIGHT / 2f);
                    gc.setFont(Font.font(20));
                    gc.fillText("zdobyte punkty: " + score, WIDTH / 2f, HEIGHT / 2f + 30);

                }

                //************************ Rysowanie wszystkich obiektów
                /** Rysuj gracza */
                drawPlayer();
                /** Rysuj wrogów */
                drawEnemies();
                /** Rysuj pociski */
                drawShots();
                /** Rysuj życia */
                drawLives();
                /** Rysuj apteczki */
                drawMedkits();
                /** Rysuj gwiazdy */
                drawStars();

                /**Rysowanie eksplozji */
                drawExplosions(false);
            }
        }
    }
    /** metoda odpowaidająca za rysowanie gracza */
    public void drawPlayer() {
        if (!player.dying) {
            gc.drawImage(player.img, player.posX - (player.width / 2), player.posY, player.width, player.height);
        }
    }
    /** metoda odpowaidająca za rysowanie przeciwników */
    public void drawEnemies() {
        for (Enemy enemy : enemies) {
            if (!enemy.dying) {
                /** Narysuj przypisaną do wroga grafikę */
                gc.drawImage(enemy.img, enemy.posX, enemy.posY, enemy.width, enemy.height);
            }
        }
    }
    /** metoda odpowiadająca za rysowanie strzałów */
    public void drawShots() {
        /** Dla każdego strzału... */
        for (Shot shot : shots) {
            gc.setFill(Color.WHITE);
            gc.fillOval(shot.posX, shot.posY, 6, 24);
        }
    }
    /** metoda odpowiadająca za rysowanie ilości żyć */
    public void drawLives() {
        for (int i = 0; i < lives; i++)
            /** Narysuj przypisaną do życia grafikę */
            gc.drawImage(LIFE_IMG, 10 + (LIFE_IMG.getWidth() * i), 10, LIFE_IMG.getWidth(), LIFE_IMG.getHeight());
    }
    /** metoda odpowiadająca za rysowanie apteczek */
    public void drawMedkits() {
        for(Medkit medkit : medkits) {
            /** Narysuj przypisaną do apteczki grafikę */
            gc.drawImage(LIFE_IMG, medkit.posX, medkit.posY, medkit.width, medkit.height);
        }
    }
    /** metoda odpowaidająca za rysowanie eksplozji */
    public void drawExplosions(boolean update) {
        for (Enemy e : enemies)
            dex(e, update);
        dex(player, update);
    }
    /** metoda odpowaidająca za tworzenie animacji eksplozji */
    private void dex(GameObject o, boolean u) {
        if (o == null)
            return;
        if (o.dying) {
            gc.drawImage(EXPLOSION_IMG, o.explosionStep % EXPLOSION_COL * EXPLOSION_W,
                    (o.explosionStep / EXPLOSION_ROWS) * EXPLOSION_H + 1,
                    EXPLOSION_W, EXPLOSION_H,
                    o.posX, o.posY, o.width, o.height);
            /** Jeżeli update true, przejdź do następnej klatki eksplozji */
            if (u)
                o.updateExplosion();

            if (o.explosionStep < 1)
                music.playExplosion();
        }
    }
    /**metoda odpowiadajaća za rysowanie gwiazd */
    public void drawStars() {
        if (RAND.nextInt(10) > 2) {
            stars.add(new Star(RAND.nextInt(WIDTH), 0, WIDTH));
        }

        stars.removeIf(s -> s.killY(HEIGHT));
        stars.forEach(star -> {
            star.move();
            gc.setFill(star.getColor());
            gc.fillOval(star.posX, star.posY, star.width, star.height);
        });
    }
    /** metoda odpowiadająca za przyspieszenie gry */
    public void speedUp() {
        if (enemySpeed < 10)
            enemySpeed += 1;
        else
            enemySpeed += (enemySpeed / score);
    }
    /** metoda odpowiadająca za rysowanie przeciwników */
    public void spawnEnemies(int emptySpaces) {
        /** Utwórz listę indeksów tabeli xSectors nadających sie do spawnowania przeciwników */
        List<Integer> availablePlaces = new ArrayList<>();

        for(int i=0; i < xSectors.length; i++) {
            /** Wypełnij tabelę indeksami */
            availablePlaces.add(i);
        }
        /** Wykonaj spawn przeciwnika tyle razy ile jest indeksów minus przekazane puste miejsca */
        for(int i=0; i < xSectors.length - emptySpaces; i++) {
            /** Losowy indeks dla listy z indeksami */
            int r = RAND.nextInt(availablePlaces.size());
            /** Wybieranie sektora na podtawie obiektu na liście pod wylosowanym indeksem */
            int sectorToSpawn = availablePlaces.get(r);
            /** Usuwanie indeksu z listy, by nie można go było ponownie wybrać */
            availablePlaces.remove(r);
            /** Tworzenie wroga */
            Enemy enemy = new Enemy(xSectors[sectorToSpawn], ySectors[1], ENEMY_IMG, enemySpeed);
            /** Dodawanie wroga do listy wrogów */
            enemies.add(enemy);
        }

    }
    /** metoda odpowiadająca za podzielenie planszy na równe części
     *  Podziel podaną wartość planszę na równe części */
    int[] DivideEqual(int value, int by, int originOffset, int endOffset) {
        /** cała podana wartość minus offsety zostawiające puste miejsce po obu stronach */
        int sectorCount = (value - endOffset - originOffset) / by;
        /** Tworzenie tablicy przechowującej wartości granic sektorów */
        int[] sec = new int[sectorCount-1];
        /** Wykonaj tyle razy, ile ma być granic sektorów */
        for (int i=0; i < sectorCount-1; i++) {
            if (i < 1) {
                /** Dla perwszego sektora granica to 0 + offset w celu odsunięcia go od krawędzi */
                sec[0] = originOffset;
                continue;
            }
            /** Dla kolejnych, to pozycja poprzedniej granicy + wartość szerokości sektora */
            sec[i] = sec[i-1] + by;
        }
        /** Wolne miejsce między szerokością ostatniego sektora a końca planszy - offset końca, */
        float freeSpace = value - sec[sectorCount - 2] - endOffset;
        /** które powstało bo nie mógł się zmieścić tam kolejny sektor.
         * Dzielenie tego miejsca pomiędzy każdy sektor */
        float middleOffset = freeSpace / sectorCount;
        for (int i=1; i < sec.length; i++) {
            for (int j=i; j < sec.length; j++) {
                /** Do każdego sektora dodaj trochę tego miejsca, by je równo rozprowadzić */
                sec[j] += middleOffset;
            }
        }
        /** Jeżeli Ostatni sektor wychodzi poza planszę... */
        if (sec[sec.length-1] + by > value) {
            /** Utwórz tablicę sektorów o wielkości 1 mniejszej od poprzedniej */
            int[] sec2 = new int[sec.length - 1];
            for(int i=0;i<sec.length-1;i++)
            /** Skopiuj zawartość poprzedniej tablicy do nowej bez ostatniego elementu */
                sec2[i] = sec[i];
            /** Zwróć nową tablicę */
            return sec2;
        }
        /** Jeżeli wszystko pasuje, to zwróć starą tablicę */
        return sec;
    }

    /*
    /----------------------------------\
    |                                  |
    |                                  |
    |                                  |
    |                                  |
    |                                  |
    |                                  |
    \----------------------------------/

    Dzielenie na sektory:

    /--T--T--T--T--T--T--T--T--T--T--T-\
    |  |  |  |  |  |  |  |  |  |  |  | |
    |  |  |  |  |  |  |  |  |  |  |  | |
    |  |  |  |  |  |  |  |  |  |  |  | |
    |  |  |  |  |  |  |  |  |  |  |  | |
    |  |  |  |  |  |  |  |  |  |  |  | |
    |  |  |  |  |  |  |  |  |  |  |  | |
    \----------------------------------/

    Na tych granicach pojawiają się przeciwnicy
    */

    /** metoda odpowiadająca za czyszczenie przeciwników, strzałów i apteczek */
    void clearAllObjects() {
        enemies.removeIf(o -> true);
        shots.removeIf(o -> true);
        medkits.removeIf(o -> true);
    }
    /** metoda odpowiadająca za uruchomienie gry */
    public static void main(String[] args) {
        launch(args);
    }
}