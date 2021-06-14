import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import javafx.util.Duration;
import java.nio.file.Paths;

/** Klasa służąca do przechowywania muzyki, dźwięków gry, ustawiania oraz ich puszczania */
public class Music {
    private final MediaPlayer Mainmenu;
    private final MediaPlayer Background;
    private final MediaPlayer Explosion;
    private final MediaPlayer Shooting;
    private final MediaPlayer Medkit;

    /** konstruktor, który przypisuje do zmiennych ścieżki plików muzycznych, ustawia ich głośność oraz zapętlenie */
    public Music() {
        String mainmenu     = "music/menu_music.wav";
        String back_music   = "music/game_music.wav";
        String expl_sfx     = "music/explosion.wav";
        String shot_sfx     = "music/shoot.wav";
        String medkit_sfx   = "music/medkit.wav";

        Mainmenu    = new MediaPlayer(new Media(Paths.get(mainmenu).toUri().toString()));
        Background  = new MediaPlayer(new Media(Paths.get(back_music).toUri().toString()));
        Explosion   = new MediaPlayer(new Media(Paths.get(expl_sfx).toUri().toString()));
        Shooting    = new MediaPlayer(new Media(Paths.get(shot_sfx).toUri().toString()));
        Medkit      = new MediaPlayer(new Media(Paths.get(medkit_sfx).toUri().toString()));

        Mainmenu.setOnEndOfMedia(() -> Mainmenu.seek(Duration.ZERO));
        Background.setOnEndOfMedia(() -> Background.seek(Duration.ZERO));

        Mainmenu.setVolume(0.03);
        Background.setVolume(0.03);
        Explosion.setVolume(0.05);
        Medkit.setVolume(0.1);
    }

    /** metoda odpowiadająca za włączenie muzyki w menu gry */
    public void playMainmenu() {
        Mainmenu.play();
    }
    /** metoda odpowiadająca za wyłączenie muzyki w menu gry */
    public void stopMainmenu() {
        Mainmenu.stop();
    }
    /** metoda odpowiadająca za włączenie muzyki podczas gry */
    public void playBackground() {
        Background.play();
    }
    /** metoda odpowiadająca za odtworzenie dźwięku eksplozji podczas gry */
    public void playExplosion() {
        Explosion.seek(Duration.ZERO);
        Explosion.play();
    }
    /** metoda odpowiadająca za odtworzenie dźwięku strzału podczas gry */
    public void playShot() {
        Shooting.seek(Duration.ZERO);
        Shooting.play();
    }
    /** metoda odpowiadająca za odtworzenie dźwięku zebrania medkitu podczas gry */
    public void playMedkit() {
        Medkit.seek(Duration.ZERO);
        Medkit.play();
    }
}
