package dronefront;

import dronefront.view.GameRenderer;
import dronefront.wave.WaveManager;
import javafx.animation.AnimationTimer;
import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.canvas.Canvas;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.input.KeyCode;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontPosture;
import javafx.scene.text.FontWeight;
import javafx.scene.text.TextAlignment;
import javafx.stage.Stage;
import javafx.scene.image.ImageView;
import dronefront.enemy.BomberDrone;
import dronefront.enemy.Enemy;
import dronefront.enemy.ScoutDrone;
import dronefront.enemy.TankDrone;
import dronefront.map.GridMap;
import dronefront.map.Ponto;
import dronefront.map.TileMap;
import dronefront.map.MapLayouts;
import dronefront.projectile.Projectile;
import dronefront.tower.*;
import dronefront.audio.SoundManager;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class DroneFrontApp extends Application {

    private enum GameState { MENU, RUNNING, PAUSED, GAME_OVER, VICTORY }
    private GameState currentState = GameState.MENU;

    private StackPane root;
    private Canvas canvas;
    private GameRenderer renderer;

    private GridMap mapa;
    private WaveManager waveManager;
    private List<Tower> towers;
    private List<Projectile> projectiles;
    private int vidaBase;
    private int moedas;
    private char selectedTowerType = '1';
    
    private boolean ignoreNextClick = false;
    
    private final SoundManager soundManager = SoundManager.getInstance();

    @Override
    public void start(Stage primaryStage) {
        canvas = new Canvas(GameConfig.WINDOW_WIDTH, GameConfig.WINDOW_HEIGHT + GameConfig.UI_HEIGHT);
        renderer = new GameRenderer(canvas, GameConfig.TILE_SIZE);

        root = new StackPane();
        root.getChildren().add(canvas);
        root.getStyleClass().add("root");

        soundManager.playMenuBGM();

        showMenu();

        Scene scene = new Scene(root, GameConfig.WINDOW_WIDTH, GameConfig.WINDOW_HEIGHT + GameConfig.UI_HEIGHT);
        
        try {
            String cssPath = getClass().getResource("/style.css").toExternalForm();
            scene.getStylesheets().add(cssPath);
        } catch (Exception e) {
            System.err.println("Erro ao carregar style.css: " + e.getMessage());
        }

        scene.setOnMouseClicked(this::handleMouseClick);
        scene.setOnKeyPressed(e -> {
            if (e.getCode() == KeyCode.ESCAPE) {
                if (currentState == GameState.RUNNING || currentState == GameState.PAUSED) {
                    togglePause();
                }
            }

            if (currentState == GameState.RUNNING) {
                if (e.getCode() == KeyCode.DIGIT1) { selectedTowerType = '1'; renderer.showFeedback("Selecionado: Gun Tower", Color.CYAN); }
                if (e.getCode() == KeyCode.DIGIT2) { selectedTowerType = '2'; renderer.showFeedback("Selecionado: PEM Tower", Color.CYAN); }
                if (e.getCode() == KeyCode.DIGIT3) { selectedTowerType = '3'; renderer.showFeedback("Selecionado: Fire Tower", Color.CYAN); }
                if (e.getCode() == KeyCode.DIGIT4) { selectedTowerType = '4'; renderer.showFeedback("MODO VENDA ATIVADO", Color.ORANGE); }
            }
        });

        try {
            var iconStream = getClass().getResourceAsStream("/assets/icon.png");
            if (iconStream != null) {
                primaryStage.getIcons().add(new Image(iconStream));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        primaryStage.setTitle("DroneFront");
        primaryStage.setScene(scene);
        primaryStage.setResizable(false);
        primaryStage.show();

        new AnimationTimer() {
            long lastTime = System.nanoTime();

            @Override
            public void handle(long now) {
                double deltaTime = (now - lastTime) / 1_000_000_000.0;
                lastTime = now;

                if (currentState == GameState.RUNNING) {
                    updateGame(deltaTime);
                    renderer.render(mapa, waveManager, towers, projectiles, vidaBase, moedas, selectedTowerType, deltaTime);
                }
            }
        }.start();
    }

    private void togglePause() {
        if (currentState == GameState.RUNNING) {
            soundManager.play("ui_pause_toggle.mp3");
            soundManager.stopGameLoop();
            currentState = GameState.PAUSED;
            showPauseMenu();
        } else if (currentState == GameState.PAUSED) {
            soundManager.play("ui_pause_toggle.mp3");
            root.getChildren().removeIf(node -> node instanceof VBox && "pauseMenu".equals(node.getId()));
            currentState = GameState.RUNNING;
            if (soundManager.isSoundEnabled()) soundManager.playGameLoop();
            this.ignoreNextClick = true; 
        }
    }

    private void showPauseMenu() {
        root.getChildren().removeIf(node -> node instanceof VBox);
        
        VBox overlay = new VBox();
        overlay.setAlignment(Pos.CENTER);
        overlay.getStyleClass().add("pause-overlay");
        overlay.setPrefSize(GameConfig.WINDOW_WIDTH, GameConfig.WINDOW_HEIGHT + GameConfig.UI_HEIGHT);
        overlay.setId("pauseMenu"); 
        
        Label title = new Label("JOGO PAUSADO");
        styleLabel(title, 48, Color.YELLOW);
        title.setPadding(new Insets(0, 0, 40, 0));

        Button btnContinue = createMenuButton("CONTINUAR MISSAO", Color.LIGHTGREEN, this::togglePause);
        Button btnRestart = createMenuButton("REINICIAR MISSAO", Color.WHITE, this::startGame); 
        Button btnMenu = createMenuButton("MENU PRINCIPAL", Color.RED, this::showMenu); 

        btnContinue.setAlignment(Pos.CENTER);
        btnRestart.setAlignment(Pos.CENTER);
        btnMenu.setAlignment(Pos.CENTER);
        
        VBox menuBox = new VBox(20);
        menuBox.setAlignment(Pos.CENTER);
        menuBox.getChildren().addAll(title, btnContinue, btnRestart, btnMenu);
        
        overlay.getChildren().add(menuBox);

        root.getChildren().add(overlay);
    }

    private void changeBackground(String imageName) {
        root.setStyle(""); 

        try {
            String path = "/assets/" + imageName;
            var stream = getClass().getResourceAsStream(path);
            
            if (stream != null) {
                Image img = new Image(stream);
                BackgroundImage bgImg = new BackgroundImage(
                    img, 
                    BackgroundRepeat.NO_REPEAT, 
                    BackgroundRepeat.NO_REPEAT, 
                    BackgroundPosition.CENTER, 
                    new BackgroundSize(1.0, 1.0, true, true, false, false)
                );
                root.setBackground(new Background(bgImg));
            } else {
                System.err.println("ERRO: Imagem nao encontrada: " + path);
                root.setBackground(new Background(new BackgroundFill(Color.web("#2F382E"), CornerRadii.EMPTY, Insets.EMPTY)));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        
        canvas.getGraphicsContext2D().clearRect(0, 0, canvas.getWidth(), canvas.getHeight());
    }

    private void showMenu() {
        currentState = GameState.MENU;
        root.getChildren().removeIf(node -> node instanceof VBox);
        root.getChildren().removeIf(node -> node != canvas);
        
        if (soundManager.isSoundEnabled()) soundManager.playMenuBGM();
        soundManager.stopGameLoop();

        changeBackground("mainpage.png");

        VBox menuBox = new VBox(10); 
        menuBox.setAlignment(Pos.BOTTOM_LEFT);
        menuBox.setPadding(new Insets(0, 0, 50, 50));
        StackPane.setAlignment(menuBox, Pos.BOTTOM_LEFT); 

        Label titleLabel = new Label("DRONEFRONT"); 
        styleLabel(titleLabel, 60, Color.WHITE);
        titleLabel.setTextAlignment(TextAlignment.LEFT);
        
        Label subtitleLabel = new Label("TOWER DEFENSE - LPOO");
        subtitleLabel.setFont(Font.font("Consolas", 14));
        subtitleLabel.setTextFill(Color.LIGHTGRAY);
        subtitleLabel.setPadding(new Insets(0, 0, 20, 0));
        subtitleLabel.getStyleClass().add("menu-subtitle");

        Button btnStart = createMenuButton("INICIAR", Color.LIGHTGREEN, this::startGame);
        Button btnTutorial = createMenuButton("TUTORIAL", Color.CYAN, this::showTutorial);
        Button btnAbout = createMenuButton("SOBRE", Color.GOLD, this::showAbout);
        Button btnSettings = createMenuButton("CONFIGURAÇÕES", Color.LIGHTGRAY, this::showSettings);

        menuBox.getChildren().addAll(titleLabel, subtitleLabel, btnStart, btnTutorial, btnAbout, btnSettings);
        root.getChildren().add(menuBox);
    }

    private void showTutorial() {
        root.getChildren().removeIf(node -> node instanceof VBox || node instanceof javafx.scene.control.ScrollPane);
        changeBackground("mainpage.png");

        VBox leftBox = new VBox(15);
        leftBox.setAlignment(Pos.BOTTOM_LEFT);
        StackPane.setAlignment(leftBox, Pos.BOTTOM_LEFT);

        leftBox.getStyleClass().add("tutorial-box-left");
        leftBox.setMaxWidth(450);

        Label titleLeft = new Label("TUTORIAL");
        styleLabel(titleLeft, 32, Color.CYAN);

        String texto = 
            "1. COMANDOS:\n" +
            "   [1] Gun Tower\n" +
            "   [2] PEM Tower\n" +
            "   [3] Fire Tower\n" +
            "   [4] Vender Torre\n" +
            "   [ESC] Pausar Jogo\n\n" + 
            "2. OBJETIVO:\n" +
            "   Impeça que os drones inimigos\n" +
            "   cheguem à sua base.\n\n" +
            "3. ESTRATÉGIA:\n" +
            "   Clique em torres prontas\n" +
            "   para APRIMORAR.\n" +
            "   OBS: Se estiver com VENDA selecionada,\n" +
            String.format("   você vai vende-lá e recuperar\n" + 
            "   %d%% do valor inicial.", GameConfig.SELL_REFUND_PERCENTAGE);

        Label contentLeft = new Label(texto);
        contentLeft.setFont(Font.font("Consolas", FontWeight.BOLD, 14));
        contentLeft.setTextFill(Color.WHITE);
        contentLeft.setWrapText(true);
        contentLeft.setMaxWidth(320);

        Button btnBack = createMenuButton("<< VOLTAR", Color.WHITE, this::showMenu);
        leftBox.getChildren().addAll(titleLeft, contentLeft, btnBack);
        
        VBox rightBox = new VBox();
        StackPane.setAlignment(rightBox, Pos.BOTTOM_RIGHT);
        
        rightBox.getStyleClass().add("tutorial-box-right");
        rightBox.setMaxWidth(600); 

        VBox cardsContainer = new VBox(15);
        cardsContainer.setPadding(new Insets(20));
        cardsContainer.getStyleClass().add("cards-container");

        Ponto pZero = new Ponto(0, 0);
        
        GunTower refGun = new GunTower(pZero);
        PEMTower refPem = new PEMTower(pZero);
        FireTower refFire = new FireTower(pZero);
        
        ScoutDrone refScout = new ScoutDrone(pZero);
        BomberDrone refBomber = new BomberDrone(pZero);
        TankDrone refTank = new TankDrone(pZero);

        cardsContainer.getChildren().add(createSectionHeader("DEFESAS (TORRES)"));
        
        cardsContainer.getChildren().add(createUnitCard("GUN TOWER", "towers/gun_preview.png", 
            String.format("Dano: %d | Range: %.1f | Custo: $%d", refGun.getDamage(), refGun.getRange(), refGun.getCost()), 
            "Disparos rápidos. Ideal para eliminar alvos fracos individualmente.", Color.CYAN));
            
        cardsContainer.getChildren().add(createUnitCard("PEM TOWER", "towers/pem_preview.png", 
            String.format("Slow: %.0f%% | Range: %.1f | Custo: $%d", (1.0 - refPem.getSlowFactor()) * 100, refPem.getRange(), refPem.getCost()), 
            "Gera pulso eletromagnético que reduz a velocidade dos inimigos.", Color.CYAN));
            
        cardsContainer.getChildren().add(createUnitCard("FIRE TOWER", "towers/fire_preview.png", 
            String.format("DPS: %.0f | Burn: %.1fs | Custo: $%d", refFire.getDamagePerSecond(), refFire.getBurnDuration(), refFire.getCost()), 
            "Lança chamas que causam dano contínuo em área.", Color.CYAN));

        cardsContainer.getChildren().add(createSectionHeader("AMEAÇAS (DRONES)"));
        
        cardsContainer.getChildren().add(createUnitCard("SCOUT DRONE", "enemies/scout_preview.png", 
            String.format("HP: %d | Speed: %.1f | Dano Base: %d", refScout.getHp(), refScout.getSpeedAtual(), refScout.getDano()), 
            "Muito rápido e leve. Difícil de acertar, mas morre fácil.", Color.RED));
            
        cardsContainer.getChildren().add(createUnitCard("BOMBER DRONE", "enemies/bomber_preview.png", 
            String.format("HP: %d | Speed: %.1f | Dano Base: %d", refBomber.getHp(), refBomber.getSpeedAtual(), refBomber.getDano()), 
            "Equilibrado. Causa dano moderado à base se colidir.", Color.RED));
            
        cardsContainer.getChildren().add(createUnitCard("TANK DRONE", "enemies/tank_preview.png", 
            String.format("HP: %d | Speed: %.1f | Dano Base: %d", refTank.getHp(), refTank.getSpeedAtual(), refTank.getDano()), 
            "Blindagem pesada e lento. Absorve muito dano antes de cair.", Color.RED));

        javafx.scene.control.ScrollPane scrollPane = new javafx.scene.control.ScrollPane(cardsContainer);
        
        scrollPane.setHbarPolicy(javafx.scene.control.ScrollPane.ScrollBarPolicy.NEVER);
        scrollPane.setVbarPolicy(javafx.scene.control.ScrollPane.ScrollBarPolicy.AS_NEEDED);
        scrollPane.setMaxWidth(420);
        scrollPane.setMaxHeight(GameConfig.WINDOW_HEIGHT + GameConfig.UI_HEIGHT - 40);

        StackPane.setAlignment(scrollPane, Pos.CENTER_RIGHT);
        StackPane.setMargin(scrollPane, new Insets(20, 20, 20, 0));

        root.getChildren().addAll(leftBox, rightBox, scrollPane);
    }

    private Label createSectionHeader(String text) {
        Label lbl = new Label(text);
        lbl.setFont(Font.font("Stencil", FontWeight.BOLD, 18));
        lbl.setTextFill(Color.GOLD);
        lbl.setPadding(new Insets(10, 0, 5, 0));
        lbl.getStyleClass().add("section-header");
        lbl.setMaxWidth(Double.MAX_VALUE);
        return lbl;
    }

    private HBox createUnitCard(String title, String imagePath, String stats, String description, Color themeColor) {
        HBox card = new HBox(15);
        card.setAlignment(Pos.CENTER_LEFT);
        card.setPadding(new Insets(10));
        
        String hexColor = "#" + themeColor.toString().substring(2, 8);
        
        card.getStyleClass().add("unit-card");
        card.setStyle("-fx-border-color: " + hexColor + ";");

        ImageView imageView = new ImageView();
        try {
            String fullPath = "/assets/" + imagePath;
            var stream = getClass().getResourceAsStream(fullPath);
            if (stream == null && imagePath.contains("preview")) {
                fullPath = fullPath.replace("_preview", "_normal").replace("towers/", "enemies/"); 
                stream = getClass().getResourceAsStream(fullPath);
            }
            
            if (stream != null) {
                Image img = new Image(stream);
                imageView.setImage(img);
                imageView.setFitWidth(60);
                imageView.setFitHeight(60);
                imageView.setPreserveRatio(true);
            }
        } catch (Exception e) { /* Ignora erro de imagem */ }

        VBox infoBox = new VBox(5);
        
        Label lblTitle = new Label(title);
        lblTitle.setFont(Font.font("Consolas", FontWeight.BOLD, 16));
        lblTitle.setTextFill(themeColor);
        
        Label lblStats = new Label(stats);
        lblStats.setFont(Font.font("Consolas", FontWeight.BOLD, 11));
        lblStats.setTextFill(Color.LIGHTGRAY);
        lblStats.setWrapText(true);
        
        Label lblDesc = new Label(description);
        lblDesc.setFont(Font.font("Consolas", FontPosture.ITALIC, 11));
        lblDesc.setTextFill(Color.GRAY);
        lblDesc.setWrapText(true);
        lblDesc.setMaxWidth(280); 

        infoBox.getChildren().addAll(lblTitle, lblStats, lblDesc);
        card.getChildren().addAll(imageView, infoBox);

        return card;
    }

    private void showAbout() {
        root.getChildren().removeIf(node -> node instanceof VBox);
        root.getChildren().removeIf(node -> node != canvas);
        changeBackground("mainpage.png");
        
        VBox box = new VBox(15);
        box.setAlignment(Pos.BOTTOM_LEFT);
        StackPane.setAlignment(box, Pos.BOTTOM_LEFT);
        
        box.getStyleClass().add("about-box");
        box.setMaxWidth(600);

        Label title = new Label("SOBRE O JOGO");
        styleLabel(title, 32, Color.GOLD);

        VBox devBox = new VBox(2);
        Label lblDevHeader = new Label("DESENVOLVIDO POR:");
        lblDevHeader.setFont(Font.font("Consolas", 12));
        lblDevHeader.setTextFill(Color.GRAY);
        
        Label lblDevName = new Label("Eliton Sena de Souza");
        lblDevName.setFont(Font.font("Consolas", FontWeight.BOLD, 20));
        lblDevName.setTextFill(Color.WHITE);
        lblDevName.getStyleClass().add("dev-name");

        Label lblContext = new Label("Disciplina LPOO - 2025.2");
        lblContext.setFont(Font.font("Consolas", 14));
        lblContext.setTextFill(Color.LIGHTGRAY);
        
        devBox.getChildren().addAll(lblDevHeader, lblDevName, lblContext);

        VBox profBox = new VBox(2);
        Label lblProfHeader = new Label("ORIENTAÇÃO E SUPORTE:");
        lblProfHeader.setFont(Font.font("Consolas", 12));
        lblProfHeader.setTextFill(Color.GRAY);
        
        Label lblProfName = new Label("Prof. Leandro Honorato de Souza Silva");
        lblProfName.setFont(Font.font("Consolas", FontWeight.BOLD, 14));
        lblProfName.setTextFill(Color.LIGHTGREEN); 
        
        profBox.getChildren().addAll(lblProfHeader, lblProfName);

        Button btnGithub = createMenuButton("ACESSAR GITHUB", Color.CYAN, () -> {
            getHostServices().showDocument("https://github.com/ElitonSena/DroneFront");
        });

        Button btnBack = createMenuButton("<< VOLTAR", Color.WHITE, this::showMenu);

        Label spacer = new Label("");
        spacer.setMinHeight(10);

        box.getChildren().addAll(title, devBox, profBox, spacer, btnGithub, btnBack);
        root.getChildren().add(box);
    }
    
    private void showSettings() {
        root.getChildren().removeIf(node -> node instanceof VBox);
        root.getChildren().removeIf(node -> node != canvas);
        
        changeBackground("mainpage.png");
        
        VBox box = new VBox(15);
        box.setAlignment(Pos.BOTTOM_LEFT);
        
        StackPane.setAlignment(box, Pos.BOTTOM_LEFT);
        
        box.getStyleClass().add("settings-box");
        box.setMaxWidth(600);

        Label title = new Label("CONFIGURAÇÕES");
        styleLabel(title, 32, Color.LIGHTGRAY);

        String soundText = soundManager.isSoundEnabled() ? "AUDIO: ATIVADO" : "AUDIO: DESATIVADO";
        Color soundColor = soundManager.isSoundEnabled() ? Color.LIGHTGREEN : Color.RED;
        
        String baseSoundStyle = "-fx-border-width: 0 0 0 3px; -fx-padding: 5 20 5 20; -fx-background-color: transparent;";

        Button btnSound = createMenuButton(soundText, soundColor, null);
        btnSound.setStyle(baseSoundStyle + " -fx-border-color: " + (soundManager.isSoundEnabled() ? "lightgreen" : "red") + ";");
        
        btnSound.setOnAction(e -> {
            boolean isEnabled = soundManager.isSoundEnabled();
            if (isEnabled) {
                btnSound.setText("AUDIO: DESATIVADO");
                btnSound.setTextFill(Color.RED);
                btnSound.setStyle(baseSoundStyle + " -fx-border-color: red;");
                soundManager.setSoundEnabled(false);
            } else {
                btnSound.setText("AUDIO: ATIVADO");
                btnSound.setTextFill(Color.LIGHTGREEN);
                btnSound.setStyle(baseSoundStyle + " -fx-border-color: lightgreen;");
                soundManager.setSoundEnabled(true);
            }
        });

        Button btnBack = createMenuButton("<< VOLTAR", Color.WHITE, this::showMenu);

        box.getChildren().addAll(title, btnSound, btnBack);
        root.getChildren().add(box);
    }

    private void showGameOver(boolean victory) {
        soundManager.stopGameLoop();
        if (soundManager.isSoundEnabled()) soundManager.playMenuBGM();
        currentState = victory ? GameState.VICTORY : GameState.GAME_OVER;
        
        root.getChildren().removeIf(node -> node instanceof VBox);
        
        VBox endScreenBox = new VBox(20);
        endScreenBox.setAlignment(Pos.CENTER);
        endScreenBox.getStyleClass().add("game-over-box");
        endScreenBox.setPrefSize(GameConfig.WINDOW_WIDTH, GameConfig.WINDOW_HEIGHT + GameConfig.UI_HEIGHT);
        
        Label titleLabel = new Label(victory ? "VOCÊ VENCEU" : "VOCÊ PERDEU");
        styleLabel(titleLabel, 60, victory ? Color.LIGHTGREEN : Color.RED);
        titleLabel.setPadding(new Insets(0, 0, 40, 0));
        
        Button menuButton = createMenuButton("MENU PRINCIPAL", Color.WHITE, this::showMenu);
        menuButton.setAlignment(Pos.CENTER);
        
        endScreenBox.getChildren().addAll(titleLabel, menuButton);
        root.getChildren().add(endScreenBox);
    }

    private void startGame() {
        int[][] selectedLayout = MapLayouts.getRandomLayout();
        mapa = new GridMap(selectedLayout);
        Ponto pontoInicial = mapa.getCaminho().getPontosDoCaminho().get(0);
        waveManager = new WaveManager(pontoInicial);
        towers = new ArrayList<>();
        projectiles = new ArrayList<>();
        vidaBase = GameConfig.INITIAL_BASE_LIVES;
        moedas = GameConfig.INITIAL_MONEY;
        selectedTowerType = '1';
        
        root.getChildren().removeIf(node -> node instanceof VBox);
        root.getChildren().removeIf(node -> node != canvas);
        root.setBackground(new Background(new BackgroundFill(Color.web("#2F382E"), CornerRadii.EMPTY, Insets.EMPTY)));
        
        currentState = GameState.RUNNING;
        if (soundManager.isSoundEnabled()) soundManager.stopMenuBGM();
        this.ignoreNextClick = true; 
        if (soundManager.isSoundEnabled()) soundManager.playGameLoop();
    }

    private void updateGame(double deltaTime) {
        if (this.ignoreNextClick) {
            this.ignoreNextClick = false;
        }

        if (vidaBase <= 0) { showGameOver(false); return; }
        if (waveManager.isFinished()) { showGameOver(true); return; }

        waveManager.update(deltaTime);
        List<Enemy> enemies = waveManager.getInimigosAtivos();
        for (Tower tower : towers) {
            tower.update(deltaTime, enemies, projectiles);
        }
        projectiles.forEach(p -> p.update(deltaTime));
        projectiles.removeIf(Projectile::hasHitTarget);

        for (int i = enemies.size() - 1; i >= 0; i--) {
            Enemy enemy = enemies.get(i);
            boolean deveRemoverTotalmente = enemy.update(deltaTime, mapa.getCaminho());
            if (!enemy.isWreckage() && enemy.chegouNaBase()) {
                vidaBase -= (enemy.getDano()); 
                soundManager.play("base_hit.mp3");
                enemies.remove(i); 
            } else if (enemy.isWreckage() && deveRemoverTotalmente) {
                moedas += enemy.getMoeda();
                enemies.remove(i);
                soundManager.play("ui_coin_gain.mp3");
            }
        }
    }

    private void handleMouseClick(MouseEvent e) {
        if (currentState != GameState.RUNNING) return;
        
        if (this.ignoreNextClick) {
            return;
        }
        
        double mouseX = e.getX();
        double mouseY = e.getY();
        
        if (mouseY < GameConfig.WINDOW_HEIGHT) {
            int gridX = (int) (mouseX / GameConfig.TILE_SIZE);
            int gridY = (int) (mouseY / GameConfig.TILE_SIZE);

            if (gridX >= 0 && gridX < mapa.getLargura() && gridY >= 0 && gridY < mapa.getAltura()) {
                Ponto clickPos = new Ponto(gridX + 0.5, gridY + 0.5);
                Optional<Tower> clickedTower = towers.stream().filter(t -> 
                    Math.abs(t.getPosition().getX() - clickPos.getX()) < 0.1 && 
                    Math.abs(t.getPosition().getY() - clickPos.getY()) < 0.1
                ).findFirst();

                if (clickedTower.isPresent()) {
                    if (selectedTowerType == '4') trySellTower(clickedTower.get());
                    else tryUpgradeTower(clickedTower.get());
                } else {
                    if (selectedTowerType == '4') {
                        renderer.showFeedback("SELECIONE UMA TORRE PARA VENDER", Color.ORANGE);
                        soundManager.play("ui_error.mp3");
                    }
                    else {
                        TileMap tile = mapa.getTileAt(gridX, gridY);
                        if (tile != null && tile.podeConstruir()) tryBuildTower(clickPos);
                        else renderer.showFeedback("LOCAL INVALIDO", Color.RED);
                        soundManager.play("ui_error.mp3");
                    }
                }
            }
            
        }
        else {
            handleUIClick(mouseX, mouseY);
        }
    }

    private void handleUIClick(double mouseX, double mouseY) {
        double width = GameConfig.WINDOW_WIDTH;
        double cardWidth = 85;
        double cardHeight = 85;
        double gap = 10;
        double totalWidth = (cardWidth * 4) + (gap * 3);
        double startX = width - totalWidth - 30;
        double cardY = GameConfig.WINDOW_HEIGHT + 7.5;

        if (isInside(mouseX, mouseY, startX, cardY, cardWidth, cardHeight)) {
            selectedTowerType = '1';
            renderer.showFeedback("Selecionado: Gun Tower", Color.CYAN);
        }
        else if (isInside(mouseX, mouseY, startX + (cardWidth + gap), cardY, cardWidth, cardHeight)) {
            selectedTowerType = '2';
            renderer.showFeedback("Selecionado: PEM Tower", Color.CYAN);
        }
        else if (isInside(mouseX, mouseY, startX + (cardWidth + gap) * 2, cardY, cardWidth, cardHeight)) {
            selectedTowerType = '3';
            renderer.showFeedback("Selecionado: Fire Tower", Color.CYAN);
        }
        else if (isInside(mouseX, mouseY, startX + (cardWidth + gap) * 3, cardY, cardWidth, cardHeight)) {
            selectedTowerType = '4';
            renderer.showFeedback("MODO VENDA ATIVADO", Color.ORANGE);
        }
    }

    private boolean isInside(double x, double y, double rectX, double rectY, double rectW, double rectH) {
        return x >= rectX && x <= rectX + rectW && y >= rectY && y <= rectY + rectH;
    }

    private void trySellTower(Tower tower) {
        int refund = tower.getCost() * GameConfig.SELL_REFUND_PERCENTAGE / 100;
        moedas += refund;
        towers.remove(tower);
        renderer.showFeedback("VENDIDA! +$" + refund, Color.GOLD);
        soundManager.play("ui_sell.mp3");
    }

    private void tryUpgradeTower(Tower tower) {
        if (tower.isMaxLevel()) {
            renderer.showFeedback("NIVEL MAXIMO!", Color.YELLOW);
            soundManager.play("ui_error.mp3");
            return;
        }
        int cost = tower.getUpgradeCost();
        if (moedas >= cost) {
            moedas -= cost;
            tower.upgrade();
            renderer.showFeedback("UPGRADE (LVL " + tower.getLevel() + ")", Color.LIGHTGREEN);
            soundManager.play("ui_upgrade.mp3");
        } else {
            renderer.showFeedback("FALTA MOEDAS ($" + cost + ")", Color.RED);
            soundManager.play("ui_error.mp3");
        }
    }

    private void tryBuildTower(Ponto pos) {
        boolean temTorre = towers.stream().anyMatch(t -> 
            Math.abs(t.getPosition().getX() - pos.getX()) < 0.1 && 
            Math.abs(t.getPosition().getY() - pos.getY()) < 0.1
        );
        if (temTorre) return;

        Tower newTower = null;
        if (selectedTowerType == '1') newTower = new GunTower(pos);
        else if (selectedTowerType == '2') newTower = new PEMTower(pos);
        else if (selectedTowerType == '3') newTower = new FireTower(pos);

        if (newTower != null) {
            if (moedas >= newTower.getCost()) {
                moedas -= newTower.getCost();
                towers.add(newTower);
                renderer.showFeedback("CONSTRUIDA!", Color.CYAN);
                soundManager.play("ui_build.mp3");
            } else {
                renderer.showFeedback("FALTA MOEDAS ($" + newTower.getCost() + ")", Color.RED);
                soundManager.play("ui_error.mp3");
            }
        }
    }

    private Button createMenuButton(String text, Color color, Runnable action) {
        Button btn = new Button(text);
        btn.setFont(Font.font("Stencil", FontWeight.BOLD, 22));
        btn.setTextFill(color);
        btn.setAlignment(Pos.CENTER_LEFT);
        btn.setPrefWidth(300);
        btn.getStyleClass().add("menu-button");
        String hexColor = "#" + color.toString().substring(2, 8);
        String normalStyle = "-fx-border-color: " + hexColor + ";";
        String hoverStyle = "-fx-background-color: rgba(255,255,255,0.1); -fx-border-color: white; -fx-border-width: 0 0 0 5px; -fx-text-fill: white; -fx-padding: 5 20 5 25;";

        btn.setStyle(normalStyle);
        
        btn.setOnMouseEntered(e -> btn.setStyle(hoverStyle));
        btn.setOnMouseExited(e -> btn.setStyle(normalStyle));
        
        if (action != null) {
            btn.setOnAction(e -> action.run());
        }
        return btn;
    }

    private void styleLabel(Label lbl, int size, Color color) {
        lbl.setFont(Font.font("Stencil", FontWeight.BOLD, size));
        lbl.setTextFill(color);
        lbl.setEffect(new javafx.scene.effect.DropShadow(10, color));
    }

    public static void main(String[] args) {
        launch(args);
    }
}