package com.example.mediaplayer;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.Slider;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import javafx.scene.media.MediaView;
import javafx.scene.shape.SVGPath;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import javafx.util.Duration;


public class App extends Application {

    // MEDIA VARIABLES
    private MediaPlayer mediaPlayer;
    private MediaView mediaView;
     // Stores all media files in the playlist
    private final List<File> playlist =
            new ArrayList<>();

    private int currentIndex = -1;

    private double currentVolume = 0.5;

    private boolean muted = false;

    private boolean userSeeking = false;

    // UI VARIABLES
    private Label nowPlayingLabel;

    private Label statusLabel;

    private Label currentTimeLabel;

    private Label totalTimeLabel;

    private Slider progressSlider;

    private Slider volumeSlider;

    private ListView<String> playlistView;

    private Button muteButton;

    private SVGPath createIcon(String svgPathData) {
    SVGPath icon = new SVGPath();
    icon.setContent(svgPathData);
    icon.setStyle("-fx-fill: white;");
    return icon;
    }


    // START APPLICATION
    @Override
    public void start(Stage stage) {

        // TITLE
        Label title =
                new Label("P MEDIA PLAYER");

        title.setStyle(
                "-fx-font-size: 22px;" +
                "-fx-font-weight: bold;" +
                "-fx-text-fill: white;"
        );

        Label subtitle =
                new Label(
                        "Audio & Video Player"
                );

        subtitle.setStyle(
                "-fx-font-size: 12px;" +
                "-fx-text-fill: #aaaaaa;"
        );

        // MEDIA / VIDEO AREA
        mediaView =
                new MediaView();

        mediaView.setFitWidth(600);

        mediaView.setFitHeight(330);

        mediaView.setPreserveRatio(true);


        StackPane mediaContainer =
                new StackPane(mediaView);

        mediaContainer.setPrefHeight(330);

        mediaContainer.setPrefWidth(600);

        mediaContainer.setStyle(
                "-fx-background-color: black;" +
                "-fx-border-color: #555555;" +
                "-fx-border-width: 2px;"
        );

        // NOW PLAYING
        nowPlayingLabel =
                new Label(
                        "No media selected"
                );

        nowPlayingLabel.setStyle(
                "-fx-font-size: 14px;" +
                "-fx-font-weight: bold;" +
                "-fx-text-fill: white;"
        );

        statusLabel =
                new Label("Ready");

        statusLabel.setStyle(
                "-fx-font-size: 12px;" +
                "-fx-text-fill: #aaaaaa;"
        );

        // TIME LABELS
        currentTimeLabel =
                new Label("00:00");

        totalTimeLabel =
                new Label("00:00");

        currentTimeLabel.setStyle(
                "-fx-text-fill: white;" +
                "-fx-font-size: 11px;"
        );

        totalTimeLabel.setStyle(
                "-fx-text-fill: white;" +
                "-fx-font-size: 11px;"
        );

        // PROGRESS SLIDER
        progressSlider =
                new Slider(
                        0,
                        100,
                        0
                );

        progressSlider.setMaxWidth(
                Double.MAX_VALUE
        );

        HBox.setHgrow(
                progressSlider,
                Priority.ALWAYS
        );

        // User starts dragging
        progressSlider.setOnMousePressed(
                event -> {
                    userSeeking = true;
                }
        );

        // User releases slider
        progressSlider.setOnMouseReleased(
                event -> {
                    userSeeking = false;
                    seekMedia();
                }
        );

        HBox progressBox =
                new HBox(
                        6,
                        currentTimeLabel,
                        progressSlider,
                        totalTimeLabel
                );

        progressBox.setAlignment(
                Pos.CENTER
        );

        // CONTROL BUTTONS (SVG ICONS)
        // SVG Path Data
       String prevPath  = "M6 6h2v12H6zm3.5 6l8.5 6V6z";
       String playPath  = "M8 5v14l11-7z";
       String pausePath = "M6 19h4V5H6v14zm8-14v14h4V5h-4z";
       String stopPath  = "M6 6h12v12H6z";
       String nextPath  = "M6 18l8.5-6L6 6v12zM16 6v12h2V6h-2z";

       Button previousButton = new Button();
       previousButton.setGraphic(createIcon(prevPath));

       Button playButton = new Button();
       playButton.setGraphic(createIcon(playPath));

       Button pauseButton = new Button();
       pauseButton.setGraphic(createIcon(pausePath));

       Button stopButton = new Button();
       stopButton.setGraphic(createIcon(stopPath));

       Button nextButton = new Button();
       nextButton.setGraphic(createIcon(nextPath));

       // Button sizes
       previousButton.setPrefWidth(65);
       playButton.setPrefWidth(65);
       pauseButton.setPrefWidth(65);
       stopButton.setPrefWidth(65);
       nextButton.setPrefWidth(65);

       // Button actions
       previousButton.setOnAction(event -> playPrevious());
       playButton.setOnAction(event -> playCurrent());
       pauseButton.setOnAction(event -> pauseCurrent());
       stopButton.setOnAction(event -> stopCurrent());
       nextButton.setOnAction(event -> playNext());

       HBox controls = new HBox(6, previousButton, playButton, pauseButton, stopButton, nextButton);
       controls.setAlignment(Pos.CENTER);

        // VOLUME CONTROLS
        Label volumeLabel =
                new Label("Volume");

        volumeLabel.setStyle(
                "-fx-text-fill: white;" +
                "-fx-font-size: 12px;"
        );

        volumeSlider =
                new Slider(
                        0,
                        100,
                        50
                );

        volumeSlider.setPrefWidth(130);

        volumeSlider.valueProperty()
                .addListener(
                        (observable,
                         oldValue,
                         newValue) -> {

                            currentVolume =
                                    newValue.doubleValue()
                                            / 100.0;

                            if (mediaPlayer != null &&
                                    !muted) {

                                mediaPlayer.setVolume(
                                        currentVolume
                                );
                            }
                        }
                );

        // MUTE BUTTON
        muteButton =
                new Button("🔊");

        muteButton.setPrefWidth(50);

        muteButton.setOnAction(
                event -> toggleMute()
        );

        HBox volumeBox =
                new HBox(
                        8,
                        volumeLabel,
                        volumeSlider,
                        muteButton
                );

        volumeBox.setAlignment(
                Pos.CENTER
        );

        // KEYBOARD HELP
        Label keyboardHelp =
                new Label(
                        "Keys: SPACE Play/Pause | " +
                        "S Stop | N Next | P Previous | " +
                        "↑↓ Volume | M Mute"
                );

        keyboardHelp.setWrapText(true);

        keyboardHelp.setStyle(
                "-fx-font-size: 10px;" +
                "-fx-text-fill: #888888;"
        );

        // PLAYLIST TITLE
        Label playlistTitle =
                new Label("PLAYLIST");

        playlistTitle.setStyle(
                "-fx-font-size: 17px;" +
                "-fx-font-weight: bold;" +
                "-fx-text-fill: white;"
        );

        // PLAYLIST LIST
        playlistView =
                new ListView<>();

        playlistView.setPrefHeight(330);

        playlistView.setPrefWidth(250);

        playlistView.setPlaceholder(
                new Label(
                        "No media files added"
                )
        );

        // PLAYLIST SELECTION
        playlistView
                .getSelectionModel()
                .selectedIndexProperty()
                .addListener(
                        (observable,
                         oldValue,
                         newValue) -> {

                            int selectedIndex =
                                    newValue.intValue();

                            if (selectedIndex >= 0 &&
                                    selectedIndex <
                                            playlist.size()) {
                                currentIndex =
                                        selectedIndex;
                                loadMedia(
                                        playlist.get(
                                                currentIndex
                                        ),
                                        true
                                );
                            }
                        }
                );

        // ADD MEDIA BUTTON
        Button addButton =
                new Button(
                        "＋ Add Media"
                );

        addButton.setPrefWidth(110);

        addButton.setPrefHeight(32);

        addButton.setStyle(
                "-fx-font-size: 12px;" +
                "-fx-font-weight: bold;"
        );

        addButton.setOnAction(
                event -> addMedia(stage)
        );

        // REMOVE MEDIA BUTTON
        Button removeButton =
                new Button(
                        "－ Remove"
                );

        removeButton.setPrefWidth(110);

        removeButton.setPrefHeight(32);

        removeButton.setStyle(
                "-fx-font-size: 12px;" +
                "-fx-font-weight: bold;"
        );

        removeButton.setOnAction(
                event -> removeSelectedMedia()
        );

        // PLAYLIST BUTTONS
        HBox playlistButtons =
                new HBox(
                        8,
                        addButton,
                        removeButton
                );

        playlistButtons.setAlignment(
                Pos.CENTER
        );

        // PLAYLIST AREA
        VBox playlistArea =
                new VBox(
                        8,
                        playlistTitle,
                        playlistView,
                        playlistButtons
                );

        playlistArea.setPadding(
                new Insets(8)
        );

        playlistArea.setPrefWidth(270);

        VBox.setVgrow(
                playlistView,
                Priority.ALWAYS
        );

        // PLAYER AREA
        VBox playerArea =
                new VBox(
                        8,
                        mediaContainer,
                        nowPlayingLabel,
                        statusLabel,
                        progressBox,
                        controls,
                        volumeBox,
                        keyboardHelp
                );

        playerArea.setAlignment(
                Pos.CENTER
        );

        playerArea.setPadding(
                new Insets(8)
        );

        HBox.setHgrow(
                playerArea,
                Priority.ALWAYS
        );

        // MAIN CONTENT
        HBox mainContent =
                new HBox(
                        8,
                        playerArea,
                        playlistArea
                );

        // ROOT
        BorderPane root =
                new BorderPane();

        root.setTop(
                createHeader(
                        title,
                        subtitle
                )
        );

        root.setCenter(
                mainContent
        );

        root.setStyle(
                "-fx-background-color: #181818;"
        );

        // SCENE
        Scene scene =
                new Scene(
                        root,
                        900,
                        650
                );

        // KEYBOARD CONTROLS
        scene.addEventFilter(
                KeyEvent.KEY_PRESSED,
                event -> {
                    KeyCode key =
                            event.getCode();

                    // SPACE
                    if (key == KeyCode.SPACE) {
                        togglePlayPause();
                        event.consume();
                    }
                    // S
                    else if (key == KeyCode.S) {
                        stopCurrent();
                        event.consume();
                    }
                    // N
                    else if (key == KeyCode.N) {
                        playNext();
                        event.consume();
                    }
                    // P
                    else if (key == KeyCode.P) {
                        playPrevious();
                        event.consume();
                    }
                    // UP
                    else if (key == KeyCode.UP) {
                        changeVolume(0.10);
                        event.consume();
                    }
                    // DOWN
                    else if (key == KeyCode.DOWN) {
                        changeVolume(-0.10);
                        event.consume();
                    }
                    // M
                    else if (key == KeyCode.M) {
                        toggleMute();
                        event.consume();
                    }
                }
        );

        // STAGE
        stage.setTitle(
                "JavaFX Media Player"
        );

        stage.setScene(scene);
        // Smaller minimum size
        stage.setMinWidth(850);
        stage.setMinHeight(600);

        // CLOSE APPLICATION
        stage.setOnCloseRequest(
                event -> {

                    if (mediaPlayer != null) {
                        mediaPlayer.stop();
                        mediaPlayer.dispose();
                    }
                }
        );
        stage.show();
    }

    // HEADER
    private VBox createHeader(
            Label title,
            Label subtitle
    ) {
        VBox header =
                new VBox(
                        3,
                        title,
                        subtitle
                );
        header.setPadding(
                new Insets(8)
        );
        header.setAlignment(
                Pos.CENTER
        );
        return header;
    }

    // ADD MEDIA
    private void addMedia(Stage stage) {
        FileChooser fileChooser =
                new FileChooser();

        fileChooser.setTitle(
                "Select Media File"
        );

        // SUPPORTED MEDIA TYPES
        FileChooser.ExtensionFilter mediaFilter =
                new FileChooser.ExtensionFilter(
                        "Supported Media Files",
            "*.mp3", "*.wav", "*.aiff", "*.aif", "*.m4a", "*.aac",
            "*.mp4", "*.m4v"
                );

        FileChooser.ExtensionFilter audioFilter =
                new FileChooser.ExtensionFilter(
                        "Audio Files",
                        "*.mp3", "*.wav", "*.aiff", "*.aif", "*.m4a", "*.aac"
                );

        FileChooser.ExtensionFilter videoFilter =
                new FileChooser.ExtensionFilter(
                        "Video Files",
                        "*.mp4", "*.m4v"
                );

        fileChooser
                .getExtensionFilters()
                .add(mediaFilter);

        fileChooser
                .getExtensionFilters()
                .add(audioFilter);

        fileChooser
                .getExtensionFilters()
                .add(videoFilter);

        // OPEN FILE CHOOSER
        File selectedFile =
                fileChooser.showOpenDialog(
                        stage
                );

        // User cancelled
        if (selectedFile == null) {

            return;
        }
        
        // CHECK FILE
        if (!isSupportedMedia(
                selectedFile
        )) {
            showError(
                    "Unsupported File",
                    "Please select a supported " +
                    "audio or video file."
            );
            return;
        }

        // ADD TO PLAYLIST
        playlist.add(
                selectedFile
        );

        playlistView
                .getItems()
                .add(
                        selectedFile.getName()
                );

        // FIRST MEDIA FILE
        if (currentIndex == -1) {

            currentIndex = 0;

            playlistView
                    .getSelectionModel()
                    .select(0);

            loadMedia(
                    selectedFile,
                    true
            );
        }
    }

    // CHECK MEDIA TYPE
    private boolean isSupportedMedia(
            File file
    ) {
        String fileName = file.getName().toLowerCase();

        return fileName.endsWith(".mp3")
            || fileName.endsWith(".wav")
            || fileName.endsWith(".aiff")
            || fileName.endsWith(".aif")
            || fileName.endsWith(".m4a")
            || fileName.endsWith(".aac")
            || fileName.endsWith(".mp4")
            || fileName.endsWith(".m4v");
           
    }

    // LOAD MEDIA
    private void loadMedia(
            File file,
            boolean autoPlay
    ) {
        if (file == null) {
            return;
        }

        // DISPOSE OLD PLAYER
        if (mediaPlayer != null) {
            mediaPlayer.stop();
            mediaPlayer.dispose();
            mediaPlayer = null;
        }
        mediaView.setMediaPlayer(
                null
        );

        // RESET PROGRESS
        progressSlider.setValue(0);
        currentTimeLabel.setText(
                "00:00"
        );
        totalTimeLabel.setText(
                "00:00"
        );

        // UPDATE LABELS
        nowPlayingLabel.setText(
                "Now Playing: "
                        + file.getName()
        );
        statusLabel.setText(
                "Loading..."
        );

        try {
            // Convert file to URL
            String mediaURL =
                    file.toURI()
                            .toString();

            // Create Media
            Media media =
                    new Media(
                            mediaURL
                    );

            // Create MediaPlayer
            mediaPlayer =
                    new MediaPlayer(
                            media
                    );

            // Connect to MediaView
            mediaView.setMediaPlayer(
                    mediaPlayer
            );

            // Apply volume
            mediaPlayer.setVolume(
                    currentVolume
            );

            // Apply mute
            mediaPlayer.setMute(
                    muted
            );

            // MEDIA READY
            mediaPlayer.setOnReady(
                    () -> {
                        Duration totalDuration =
                                mediaPlayer
                                        .getTotalDuration();
                        if (totalDuration != null &&
                                !totalDuration.isUnknown()) {
                            totalTimeLabel.setText(
                                    formatTime(
                                            totalDuration
                                    )
                            );
                        }
                        statusLabel.setText(
                                "Ready"
                        );

                        if (autoPlay) {
                            mediaPlayer.play();
                        }
                    }
            );

            // PLAYING
            mediaPlayer.setOnPlaying(
                    () -> {
                        statusLabel.setText(
                                "Playing"
                        );
                    }
            );

            // PAUSED
            mediaPlayer.setOnPaused(
                    () -> {
                        statusLabel.setText(
                                "Paused"
                        );
                    }
            );

            // STOPPED
            mediaPlayer.setOnStopped(
                    () -> {
                        statusLabel.setText(
                                "Stopped"
                        );
                    }
            );

            // MEDIA FINISHED
            mediaPlayer.setOnEndOfMedia(
                    () -> {
                        playNext();
                    }
            );

            // PROGRESS
            mediaPlayer.currentTimeProperty()
                    .addListener(
                            (
                                    observable,
                                    oldTime,
                                    newTime
                            ) -> {
                                Duration totalDuration =
                                        mediaPlayer
                                                .getTotalDuration();

                                if (totalDuration != null &&
                                        !totalDuration.isUnknown() &&
                                        totalDuration.toMillis() > 0) {

                                    double percentage =
                                            newTime.toMillis()
                                                    /
                                            totalDuration.toMillis()
                                                    *
                                            100;

                                    if (!userSeeking) {
                                        progressSlider
                                                .setValue(
                                                        percentage
                                                );
                                    }
                                }
                                currentTimeLabel.setText(
                                        formatTime(
                                                newTime
                                        )
                                );
                            }
                    );

            // MEDIA ERROR
            mediaPlayer.setOnError(
                    () -> {
                        statusLabel.setText(
                                "Playback Error"
                        );

                        showError(
                                "Media Error",
                                "JavaFX could not play:\n\n"
                                        + file.getName()
                                        + "\n\n"
                                        + "The file may use "
                                        + "an unsupported codec."
                        );
                    }
            );

        } catch (Exception e) {

            statusLabel.setText(
                    "Could not load media"
            );
            showError(
                    "Media Error",
                    "Could not load:\n\n"
                            + file.getName()
                            + "\n\n"
                            + "The media format may "
                            + "not be supported."
            );
        }
    }

    // SEEK MEDIA
    private void seekMedia() {

        if (mediaPlayer == null) {
            return;
        }
        Duration totalDuration =
                mediaPlayer
                        .getTotalDuration();
        if (totalDuration == null ||
                totalDuration.isUnknown()) {
            return;
        }
        double percentage =
                progressSlider
                        .getValue()
                        / 100.0;

        mediaPlayer.seek(
                totalDuration.multiply(
                        percentage
                )
        );
    }

    // PLAY
    private void playCurrent() {
        if (mediaPlayer == null) {
            statusLabel.setText(
                    "No media selected"
            );
            return;
        }
        mediaPlayer.play();
    }

    // PAUSE
    private void pauseCurrent() {
        if (mediaPlayer == null) {
            return;
        }
        mediaPlayer.pause();
    }

    // PLAY / PAUSE
    private void togglePlayPause() {
        if (mediaPlayer == null) {
            statusLabel.setText(
                    "No media selected"
            );
            return;
        }

        MediaPlayer.Status status =
                mediaPlayer.getStatus();

        if (status ==
                MediaPlayer.Status.PLAYING) {
            mediaPlayer.pause();
        } else {
            mediaPlayer.play();
        }
    }

    // STOP
    private void stopCurrent() {
        if (mediaPlayer == null) {
            return;
        }
        mediaPlayer.stop();

        progressSlider.setValue(
                0
        );

        currentTimeLabel.setText(
                "00:00"
        );
    }

    // NEXT
    private void playNext() {
        if (playlist.isEmpty()) {
            statusLabel.setText(
                    "Playlist is empty"
            );
            return;
        }
        currentIndex++;

        if (currentIndex >=
                playlist.size()) {
            currentIndex = 0;
        }
        playlistView
                .getSelectionModel()
                .select(
                        currentIndex
                );

        loadMedia(
                playlist.get(
                        currentIndex
                ),
                true
        );
    }

    // PREVIOUS
    private void playPrevious() {
        if (playlist.isEmpty()) {
            statusLabel.setText(
                    "Playlist is empty"
            );
            return;
        }

        currentIndex--;

        if (currentIndex < 0) {
            currentIndex =
                    playlist.size() - 1;
        }

        playlistView
                .getSelectionModel()
                .select(
                        currentIndex
                );

        loadMedia(
                playlist.get(
                        currentIndex
                ),
                true
        );
    }

    // REMOVE SELECTED MEDIA
    private void removeSelectedMedia() {
        int selectedIndex =
                playlistView
                        .getSelectionModel()
                        .getSelectedIndex();
        // No selection
        if (selectedIndex < 0) {
            statusLabel.setText(
                    "Select a media file first."
            );
            return;
        }


        // Is the selected item currently playing?
        boolean removingCurrent =
                selectedIndex ==
                        currentIndex;

        // REMOVE FROM PLAYLIST
        playlist.remove(
                selectedIndex
        );

        playlistView
                .getItems()
                .remove(
                        selectedIndex
                );

        // PLAYLIST EMPTY
        if (playlist.isEmpty()) {
            currentIndex = -1;
            if (mediaPlayer != null) {
                mediaPlayer.stop();
                mediaPlayer.dispose();
                mediaPlayer = null;
            }
            mediaView.setMediaPlayer(
                    null
            );
            nowPlayingLabel.setText(
                    "No media selected"
            );
            statusLabel.setText(
                    "Playlist empty"
            );
            progressSlider.setValue(
                    0
            );
            currentTimeLabel.setText(
                    "00:00"
            );


            totalTimeLabel.setText(
                    "00:00"
            );
            return;
        }

        // ADJUST INDEX
        if (selectedIndex <
                currentIndex) {
            currentIndex--;
        }

        // CURRENT MEDIA REMOVED
        if (removingCurrent) {
            if (currentIndex >=
                    playlist.size()) {
                currentIndex =
                        playlist.size() - 1;
            }

            playlistView
                    .getSelectionModel()
                    .select(
                            currentIndex
                    );


            loadMedia(
                    playlist.get(
                            currentIndex
                    ),
                    true
            );
        }
    }

    // CHANGE VOLUME
    private void changeVolume(
            double amount
    ) {
        currentVolume += amount;
        // Maximum
        if (currentVolume > 1.0) {
            currentVolume = 1.0;
        }
        // Minimum
        if (currentVolume < 0.0) {
            currentVolume = 0.0;
        }
        // Update slider
        volumeSlider.setValue(
                currentVolume * 100
        );

        // Apply to MediaPlayer
        if (mediaPlayer != null &&
                !muted) {
            mediaPlayer.setVolume(
                    currentVolume
            );
        }
    }

    // MUTE / UNMUTE
    private void toggleMute() {
        muted = !muted;
        if (mediaPlayer != null) {
            mediaPlayer.setMute(
                    muted
            );
        }
        if (muted) {
            muteButton.setText(
                    "🔇"
            );
            statusLabel.setText(
                    "Muted"
            );

        } else {
            muteButton.setText(
                    "🔊"
            );

            statusLabel.setText(
                    "Unmuted"
            );
        }
    }

    // FORMAT TIME
    private String formatTime(
            Duration duration
    ) {
        if (duration == null ||
                duration.isUnknown()) {
            return "00:00";
        }

        int totalSeconds =
                (int) Math.floor(
                        duration.toSeconds()
                );

        int minutes =
                totalSeconds / 60;
        int seconds =
                totalSeconds % 60;
        int hours =
                minutes / 60;
        minutes =
                minutes % 60;
        if (hours > 0) {
            return String.format(
                    "%02d:%02d:%02d",
                    hours,
                    minutes,
                    seconds
            );
        }

        return String.format(
                "%02d:%02d",
                minutes,
                seconds
        );
    }

    // ERROR MESSAGE
    private void showError(
            String title,
            String message
    ) {
        Alert alert =
                new Alert(
                        AlertType.ERROR
                );
        alert.setTitle(
                title
        );
        alert.setHeaderText(
                null
        );
        alert.setContentText(
                message
        );
        alert.show();
    }

    // MAIN METHOD
    public static void main(
            String[] args
    ) {

        launch(args);
    }
}