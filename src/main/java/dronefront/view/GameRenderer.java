package dronefront.view;

import dronefront.GameConfig;
import dronefront.enemy.BomberDrone;
import dronefront.enemy.Enemy;
import dronefront.enemy.ScoutDrone;
import dronefront.enemy.TankDrone;
import dronefront.map.GridMap;
import dronefront.map.Ponto;
import dronefront.map.TileMap;
import dronefront.projectile.Bullet;
import dronefront.projectile.FireProjectile;
import dronefront.projectile.PEMProjectile;
import dronefront.projectile.Projectile;
import dronefront.tower.FireTower;
import dronefront.tower.GunTower;
import dronefront.tower.PEMTower;
import dronefront.tower.Tower;
import dronefront.wave.WaveManager;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.effect.DropShadow;
import javafx.scene.effect.GaussianBlur;
import javafx.scene.image.Image;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.scene.text.TextAlignment;

import java.util.List;

public class GameRenderer {

    private final Canvas canvas;
    private final GraphicsContext gc;
    private final int tileSize;

    private Image imgGrass, imgPath, imgFoundation, imgBase;
    private Image imgScoutNorm, imgScoutDmg, imgScoutWreck;
    private Image imgBombNorm, imgBombDmg, imgBombWreck;
    private Image imgTankNorm, imgTankDmg, imgTankWreck;
    private Image imgGun1, imgGun2, imgGun3;
    private Image imgPem1, imgPem2, imgPem3;
    private Image imgFire1, imgFire2, imgFire3;
    private Image imgProjGun, imgProjPem, imgProjFire;
    
    private Image imgGunPreview, imgPemPreview, imgFirePreview;
    private Image imgTablet; 

    private static final Color NEON_GREEN = Color.web("#39FF14");
    private final DropShadow neonGlow = new DropShadow();
    private final GaussianBlur shadowBlur = new GaussianBlur(8);

    private String feedbackText = "";
    private double feedbackTimer = 0;
    private Color feedbackColor = Color.WHITE;
    private double feedbackYOffset = 0;

    public GameRenderer(Canvas canvas, int tileSize) {
        this.canvas = canvas;
        this.gc = canvas.getGraphicsContext2D();
        this.tileSize = tileSize;
        
        neonGlow.setRadius(10); 
        neonGlow.setSpread(0.3); 
        neonGlow.setColor(NEON_GREEN);

        loadAssets();
    }

    private void loadAssets() {
        imgGrass = load("map/tile_grass.png");
        imgPath = load("map/tile_path.png");
        imgFoundation = load("map/tile_foundation.png");
        imgBase = load("map/base.png"); 
        imgScoutNorm = load("enemies/scout_normal.png");
        imgScoutDmg = load("enemies/scout_damaged.png");
        imgScoutWreck = load("enemies/scout_wreck.png");
        imgBombNorm = load("enemies/bomber_normal.png");
        imgBombDmg = load("enemies/bomber_damaged.png");
        imgBombWreck = load("enemies/bomber_wreck.png");
        imgTankNorm = load("enemies/tank_normal.png");
        imgTankDmg = load("enemies/tank_damaged.png");
        imgTankWreck = load("enemies/tank_wreck.png");
        
        imgGun1 = load("towers/gun_1.png");
        imgGun2 = load("towers/gun_2.png");
        imgGun3 = load("towers/gun_3.png");
        
        imgPem1 = load("towers/pem_1.png");
        imgPem2 = load("towers/pem_2.png");
        imgPem3 = load("towers/pem_3.png");
        
        imgFire1 = load("towers/fire_1.png");
        imgFire2 = load("towers/fire_2.png");
        imgFire3 = load("towers/fire_3.png");
        
        imgProjGun = load("projectiles/proj_gun.png");
        imgProjPem = load("projectiles/proj_pem.png");
        imgProjFire = load("projectiles/proj_fire.png");

        imgGunPreview = load("towers/gun_preview.png");
        imgPemPreview = load("towers/pem_preview.png");
        imgFirePreview = load("towers/fire_preview.png");
        
        imgTablet = load("hud/tablet.png"); 
    }

    private Image load(String path) {
        try {
            String fullPath = "/assets/" + path;
            var stream = getClass().getResourceAsStream(fullPath);
            return (stream != null) ? new Image(stream) : null;
        } catch (Exception e) { return null; }
    }

    public void showFeedback(String text, Color color) {
        this.feedbackText = text;
        this.feedbackColor = color;
        this.feedbackTimer = 2.0; 
        this.feedbackYOffset = 0;
    }

    public void render(GridMap map, WaveManager waveManager, List<Tower> towers, List<Projectile> projectiles, int vidaBase, int moedas, char selectedTowerType, double deltaTime) {
        gc.setEffect(null);
        gc.setFill(Color.web("#2F382E")); 
        gc.fillRect(0, 0, canvas.getWidth(), canvas.getHeight());

        drawGridAndPath(map);
        drawBase(map);
        drawTowers(towers); 
        drawEnemies(waveManager.getInimigosAtivos());
        drawProjectiles(projectiles); 
        
        drawHUD(vidaBase, moedas, waveManager.getWaveAtual(), selectedTowerType);
        
        drawFeedbackMessage(deltaTime);
    }
    
    private void drawFeedbackMessage(double deltaTime) {
        if (feedbackTimer > 0) {
            feedbackTimer -= deltaTime;
            feedbackYOffset += 20 * deltaTime; 

            double opacity = Math.min(1.0, feedbackTimer); 
            
            gc.save();
            gc.setGlobalAlpha(opacity);
            gc.setFill(feedbackColor);
            gc.setStroke(Color.BLACK);
            gc.setLineWidth(2);
            gc.setFont(Font.font("Consolas", FontWeight.BOLD, 16)); 
            gc.setTextAlign(TextAlignment.CENTER);
            
            double x = canvas.getWidth() / 2;
            double y = (canvas.getHeight() / 2 + 190) - feedbackYOffset;

            gc.strokeText(feedbackText, x, y);
            gc.fillText(feedbackText, x, y);
            
            gc.restore();
        }
    }

    private void drawRotatedImage(Image image, double angle, double screenX, double screenY, double width, double height, double pivotX, double pivotY) {
        if (image == null) return;
        gc.save();
        gc.translate(screenX, screenY);
        gc.rotate(angle);
        gc.drawImage(image, -pivotX, -pivotY, width, height);
        gc.restore();
    }
    
    private void drawEnemyShadow(double x, double y) {
        gc.save();
        gc.setEffect(shadowBlur);
        gc.setFill(Color.rgb(0, 0, 0, 0.4));
        double shadowWidth = tileSize * 0.6;
        double shadowHeight = tileSize * 0.4;
        gc.fillOval(x - shadowWidth/2, (y + 15) - shadowHeight/2, shadowWidth, shadowHeight);
        gc.restore(); 
    }

    private void drawGridAndPath(GridMap map) {
        for (int y = 0; y < map.getAltura(); y++) {
            for (int x = 0; x < map.getLargura(); x++) {
                TileMap tile = map.getTileAt(x, y);
                double drawX = x * tileSize;
                double drawY = y * tileSize;
                
                double sizeFix = tileSize + 18; 

                if (!tile.podeConstruir()) {
                    if (imgPath != null) gc.drawImage(imgPath, drawX, drawY, sizeFix, sizeFix);
                    else { gc.setFill(Color.SADDLEBROWN); gc.fillRect(drawX, drawY, tileSize, tileSize); }
                } else {
                    if (imgGrass != null) gc.drawImage(imgGrass, drawX, drawY, sizeFix, sizeFix);
                    else { gc.setFill(Color.FORESTGREEN); gc.fillRect(drawX, drawY, tileSize, tileSize); }
                }
            }
        }
    }
    
    private void drawBase(GridMap map) {
        List<Ponto> path = map.getCaminho().getPontosDoCaminho();
        if (path.isEmpty()) return;
        Ponto end = path.get(path.size() - 1);
        if (imgBase != null) {
            double baseX = end.getX() * tileSize;
            double baseY = end.getY() * tileSize;
            gc.drawImage(imgBase, baseX - tileSize/2.0, baseY - tileSize/2.0 - 27, tileSize*2, tileSize*2);
        }
    }

    private void drawTowers(List<Tower> towers) {
        for (Tower t : towers) {
            double tileCenterX = (t.getPosition().getX() * tileSize); 
            double tileCenterY = (t.getPosition().getY() * tileSize);

            if (imgFoundation != null) {
                gc.drawImage(imgFoundation, (int)t.getPosition().getX() * tileSize + 6, (int)t.getPosition().getY() * tileSize + 6, tileSize, tileSize);
            }

            Image towerSprite = null;
            int level = t.getLevel();

            if (t instanceof GunTower) {
                if (level == 1) towerSprite = imgGun1;
                else if (level == 2) towerSprite = imgGun2;
                else towerSprite = imgGun3;
            } else if (t instanceof PEMTower) {
                if (level == 1) towerSprite = imgPem1;
                else if (level == 2) towerSprite = imgPem2;
                else towerSprite = imgPem3;
            } else if (t instanceof FireTower) {
                if (level == 1) towerSprite = imgFire1;
                else if (level == 2) towerSprite = imgFire2;
                else towerSprite = imgFire3;
            }

            double spritePivotX = 15; 
            double spritePivotY = 22; 
            drawRotatedImage(towerSprite, t.getRotation(), tileCenterX+6, tileCenterY+6, tileSize, tileSize, spritePivotX, spritePivotY);
        }
    }

    private void drawEnemies(List<Enemy> enemies) {
        for (Enemy e : enemies) {
            double tileCenterX = e.getPosition().getX() * tileSize;
            double tileCenterY = e.getPosition().getY() * tileSize;

            if (!e.isWreckage()) {
                drawEnemyShadow(tileCenterX, tileCenterY);
            }

            Image spriteToDraw = null;
            double maxHp;
                if (e instanceof TankDrone) {
                    maxHp = GameConfig.TANK_HP;
                } else if (e instanceof BomberDrone) {
                    maxHp = GameConfig.BOMBER_HP;
                } else {
                    maxHp = GameConfig.SCOUT_HP;
                }
            double hpPercent = (double)e.getHp() / maxHp;

            if (e.isWreckage() || e.getHp() <= 0) {
                if (e instanceof ScoutDrone) spriteToDraw = imgScoutWreck;
                else if (e instanceof BomberDrone) spriteToDraw = imgBombWreck;
                else if (e instanceof TankDrone) spriteToDraw = imgTankWreck;
            } else if (hpPercent <= 0.5) {
                if (e instanceof ScoutDrone) spriteToDraw = imgScoutDmg;
                else if (e instanceof BomberDrone) spriteToDraw = imgBombDmg;
                else if (e instanceof TankDrone) spriteToDraw = imgTankDmg;
            } else {
                if (e instanceof ScoutDrone) spriteToDraw = imgScoutNorm;
                else if (e instanceof BomberDrone) spriteToDraw = imgBombNorm;
                else if (e instanceof TankDrone) spriteToDraw = imgTankNorm;
            }

            if (spriteToDraw != null) {
                if (e.isWreckage()) {
                    gc.drawImage(spriteToDraw, tileCenterX - tileSize/2.0, tileCenterY - tileSize/2.0, tileSize, tileSize);
                } else {
                    drawRotatedImage(spriteToDraw, e.getRotation(), tileCenterX, tileCenterY, tileSize, tileSize, tileSize/2.0, tileSize/2.0);
                }
            } else {
                gc.setFill(Color.RED);
                gc.fillOval(tileCenterX -11, tileCenterY - 12, 20, 20);
            }

            if (!e.isWreckage()) {
                drawHealthBar(e, tileCenterX, tileCenterY - tileSize/2.0);
            }
        }
    }

    private void drawProjectiles(List<Projectile> projectiles) {
        for (Projectile p : projectiles) {
            if (p.hasHitTarget()) continue;
            double screenX = p.getPosition().getX() * tileSize;
            double screenY = p.getPosition().getY() * tileSize;
            Image sprite = null;
            double rawPivotX = 0, rawPivotY = 0;

            if (p instanceof Bullet) { sprite = imgProjGun; rawPivotX = 205; rawPivotY = 129; }
            else if (p instanceof PEMProjectile) { sprite = imgProjPem; rawPivotX = 184; rawPivotY = 128; }
            else if (p instanceof FireProjectile) { sprite = imgProjFire; rawPivotX = 200; rawPivotY = 134; }

            if (sprite != null) {
                double scale = (double) tileSize / sprite.getWidth(); 
                drawRotatedImage(sprite, p.getRotation(), screenX, screenY, sprite.getWidth()*scale, sprite.getHeight()*scale, rawPivotX*scale, rawPivotY*scale);
            }
        }
    }

    private void drawHealthBar(Enemy e, double centerX, double y) {
        double maxHp;
            if (e instanceof TankDrone) {
                maxHp = GameConfig.TANK_HP;
            } else if (e instanceof BomberDrone) {
                maxHp = GameConfig.BOMBER_HP;
            } else {
                maxHp = GameConfig.SCOUT_HP;
            }
        double hpPercent = Math.max(0, (double)e.getHp() / maxHp);
        double barWidth = tileSize * 0.8;
        double barX = centerX - (barWidth / 2.0);
        gc.setFill(Color.BLACK);
        gc.fillRect(barX, y - 5, barWidth, 5);
        gc.setFill(hpPercent > 0.5 ? Color.LIMEGREEN : Color.RED);
        gc.fillRect(barX, y - 5, barWidth * hpPercent, 5);
    }

    private void drawHUD(int vida, int moedas, int onda, char selectedTower) {
        double hudY = canvas.getHeight() - 100;
        double width = canvas.getWidth();

        gc.setEffect(null);
        gc.setFill(Color.rgb(20, 25, 20, 0.95)); 
        gc.fillRect(0, hudY, width, 100);
        gc.setStroke(Color.rgb(50, 60, 50));
        gc.setLineWidth(2);
        gc.strokeLine(0, hudY, width, hudY);

        double leftMargin = 30;
        double statsBaseY = hudY + 30;

        drawLeftStats(vida, moedas, leftMargin, statsBaseY); 
        drawCenterTablet(onda, width, hudY);
        
        double cardWidth = 85;
        double cardHeight = 85;
        double gap = 10;
        double totalWidth = (cardWidth * 4) + (gap * 3);
        double startX = width - totalWidth - 30;
        double cardY = hudY + 7.5;

        drawBuildCard(startX, cardY, cardWidth, cardHeight, 
                     '1', selectedTower, "GUN", 100, imgGunPreview);

        drawBuildCard(startX + (cardWidth + gap), cardY, cardWidth, cardHeight, 
                     '2', selectedTower, "PEM", 150, imgPemPreview);

        drawBuildCard(startX + (cardWidth + gap) * 2, cardY, cardWidth, cardHeight, 
                     '3', selectedTower, "FIRE", 125, imgFirePreview);

        drawSellCard(startX + (cardWidth + gap) * 3, cardY, cardWidth, cardHeight, 
                    'v', selectedTower); 
    }

    private void drawLeftStats(int vida, int moedas, double x, double y) {
        gc.setFill(Color.web("#889988"));
        gc.setFont(Font.font("Consolas", FontWeight.NORMAL, 12));
        gc.fillText("VIDA DA BASE", x, y);

        double barWidth = 200;
        double barHeight = 10;
        double barY = y + 5;
        
        gc.setFill(Color.rgb(40, 40, 40));
        gc.fillRect(x, barY, barWidth, barHeight);

        double maxVida = (double) GameConfig.INITIAL_BASE_LIVES;
        double hpPercent = Math.max(0, Math.min(1.0, vida / maxVida));
        Color hpColor = (hpPercent > 0.5) ? Color.web("#44FF99") : Color.web("#FF4444");
        
        if (vida > 0) {
            gc.save();
            gc.setEffect(new DropShadow(5, hpColor)); 
            gc.setFill(hpColor);
            gc.fillRect(x, barY, barWidth * hpPercent, barHeight);
            gc.restore();
        }

        gc.setFill(Color.WHITE);
        gc.setFont(Font.font("Consolas", FontWeight.BOLD, 12));
        int vidaPercent = (int)(hpPercent * 100);
        gc.fillText(vidaPercent + "%", x + barWidth + 10, barY + 9);

        double creditsY = barY + 35;
        gc.setFill(Color.GOLD);
        gc.setFont(Font.font("Consolas", FontWeight.BOLD, 18));
        gc.fillText("$ " + moedas, x, creditsY);
        gc.setFill(Color.web("#666666"));
        gc.setFont(Font.font("Consolas", FontWeight.NORMAL, 12));
        gc.fillText("MOEDAS", x, creditsY + 12);
    }

    private void drawCenterTablet(int onda, double width, double hudY) {
        double centerX = width / 2;
        double tabletScale = 0.55;
        double tabletYOffset = 25;  

        if (imgTablet != null) {
            double tabW = imgTablet.getWidth() * tabletScale;
            double tabH = imgTablet.getHeight() * tabletScale;
            double drawX = centerX - tabW / 2;
            double drawY = hudY + (100 - tabH) / 2 + tabletYOffset; 
            
            gc.save();
            gc.setEffect(null); 
            gc.drawImage(imgTablet, drawX, drawY, tabW, tabH);
            gc.setFill(Color.rgb(30, 35, 30, 1.0));
            gc.setFont(Font.font("Consolas", FontWeight.BOLD, 100 * tabletScale)); 
            gc.setTextAlign(TextAlignment.CENTER);
            double textY = drawY + (tabH / 2) + (12 * tabletScale);
            gc.fillText(String.valueOf(onda), centerX, textY - 25); 
            gc.restore();
        }
    }

    private void drawBuildCard(double x, double y, double w, double h, char type, char selectedType, String name, int cost, Image previewImg) {
        boolean isSelected = (type == selectedType);
        
        if (isSelected) {
            gc.save();
            gc.setEffect(neonGlow);
            gc.setStroke(NEON_GREEN);
            gc.setLineWidth(2);
            gc.setFill(Color.rgb(0, 50, 60, 0.5));
            gc.fillRoundRect(x, y, w, h, 10, 10);
            gc.strokeRoundRect(x, y, w, h, 10, 10);
            gc.restore();
        } else {
            gc.setEffect(null);
            gc.setFill(Color.rgb(30, 30, 30));
            gc.fillRoundRect(x, y, w, h, 10, 10);
            gc.setStroke(Color.rgb(60, 60, 60));
            gc.setLineWidth(1);
            gc.strokeRoundRect(x, y, w, h, 10, 10);
        }

        double imgSize = w * 0.5;
        double imgX = x + (w - imgSize) / 2;
        double imgY = y + 5;
        gc.drawImage(previewImg, imgX, imgY, imgSize, imgSize);

        gc.setTextAlign(TextAlignment.CENTER);
        
        gc.setFill(isSelected ? NEON_GREEN : Color.LIGHTGRAY);
        gc.setFont(Font.font("Consolas", FontWeight.BOLD, 10));
        gc.fillText(name, x + w/2, y + h - 25);

        gc.setFill(Color.GOLD);
        gc.setFont(Font.font("Consolas", FontWeight.BOLD, 12));
        gc.fillText("$" + cost, x + w/2, y + h - 12);

        gc.setTextAlign(TextAlignment.LEFT);
        gc.setFill(Color.GRAY);
        gc.setFont(Font.font("Consolas", FontWeight.NORMAL, 10));
        gc.fillText("[" + type + "]", x + 5, y + 12);
    }

    private void drawSellCard(double x, double y, double w, double h, char type, char selectedType) {
        boolean isSelected = (selectedType == type || selectedType == '4'); 
        
        Color alertColor = Color.web("#FF5500");

        if (isSelected) {
            gc.save();
            gc.setEffect(new DropShadow(10, alertColor)); 
            gc.setStroke(alertColor);
            gc.setLineWidth(2);
            gc.setFill(Color.rgb(60, 20, 0, 0.6)); 
            gc.fillRoundRect(x, y, w, h, 10, 10);
            gc.strokeRoundRect(x, y, w, h, 10, 10);
            gc.restore();
        } else {
            gc.setEffect(null);
            gc.setFill(Color.rgb(30, 20, 20)); 
            gc.fillRoundRect(x, y, w, h, 10, 10);
            gc.setStroke(Color.rgb(60, 40, 40));
            gc.setLineWidth(1);
            gc.strokeRoundRect(x, y, w, h, 10, 10);
        }

        gc.setTextAlign(TextAlignment.CENTER);
        gc.setFont(Font.font("Stencil", FontWeight.BOLD, 36));
        gc.setFill(isSelected ? alertColor : Color.rgb(100, 50, 50));
        gc.fillText("$", x + w/2, y + h/2 + 5);
        
        gc.setFont(Font.font("Consolas", FontWeight.BOLD, 12));
        gc.setFill(isSelected ? Color.WHITE : Color.GRAY);
        gc.fillText("VENDER", x + w/2, y + h - 12);

        gc.setTextAlign(TextAlignment.LEFT);
        gc.setFill(Color.GRAY);
        gc.setFont(Font.font("Consolas", FontWeight.NORMAL, 10));
        gc.fillText("[4]", x + 5, y + 12);
    }
}