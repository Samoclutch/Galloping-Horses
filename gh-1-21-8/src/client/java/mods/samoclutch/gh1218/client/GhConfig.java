package mods.samoclutch.gh1218.client;

import com.terraformersmc.modmenu.api.ConfigScreenFactory;
import com.terraformersmc.modmenu.api.ModMenuApi;
import me.shedaniel.clothconfig2.api.ConfigBuilder;
import me.shedaniel.clothconfig2.api.ConfigCategory;
import me.shedaniel.clothconfig2.api.ConfigEntryBuilder;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.text.Text;

import java.io.*;

public class GhConfig implements ModMenuApi{
    private static final File configLocation = new File("./config/gallopinghorses");

    private static final double DEFAULT_THROW = 3.0/4;
    private static final double MAX_THROW = DEFAULT_THROW*2;
    private static final double DEFAULT_THROW_ZOOMED = 3.0/4;
    private static final double MAX_THROW_ZOOMED = DEFAULT_THROW_ZOOMED*2;
    private static final double DEFAULT_LOFT = 1.0/5;
    private static final double MAX_LOFT = DEFAULT_LOFT*2;
    private static final double DEFAULT_LOFT_ZOOMED = 1.0/6;
    private static final double MAX_LOFT_ZOOMED = DEFAULT_LOFT_ZOOMED*6;
    private static final double DEFAULT_ZOOM = 5.0;
    private static final double MAX_ZOOM = DEFAULT_ZOOM*2;
    private static final double DEFAULT_P_O_A = 3.0;
    private static final double MAX_P_O_A = DEFAULT_P_O_A*5;
    private static final double DEFAULT_P_O_A_ZOOMED = 3.0;
    private static final double MAX_P_O_A_ZOOMED = DEFAULT_P_O_A_ZOOMED*5;

    public static double cameraLoftFactor = DEFAULT_LOFT;
    public static double cameraLoftZoomedFactor = DEFAULT_LOFT_ZOOMED;
    public static double cameraThrowFactor = DEFAULT_THROW;
    public static double cameraThrowZoomedFactor = DEFAULT_THROW_ZOOMED;
    public static double cameraZoomFactor = 1/DEFAULT_ZOOM;
    public static double cameraPointOfAimDistance = DEFAULT_P_O_A;
    public static double cameraPointOfAimDistanceZoomed = DEFAULT_P_O_A_ZOOMED;

    public static void init() {
        try (BufferedReader reader = new BufferedReader(new FileReader(configLocation))) {
            cameraPointOfAimDistance = (Double.parseDouble(
                    reader.readLine().substring(23)));
            cameraPointOfAimDistanceZoomed = (Double.parseDouble(
                    reader.readLine().substring(30)));
            cameraZoomFactor = (1/Double.parseDouble(
                    reader.readLine().substring(13)));
            cameraLoftFactor = (Double.parseDouble(
                    reader.readLine().substring(13)));
            cameraLoftZoomedFactor = (Double.parseDouble(
                    reader.readLine().substring(20)));
            cameraThrowFactor = (Double.parseDouble(
                    reader.readLine().substring(14)));
            cameraThrowZoomedFactor = (Double.parseDouble(
                    reader.readLine().substring(21)));
        } catch (Exception e) {
            try (PrintWriter writer = new PrintWriter(new BufferedWriter(new FileWriter(configLocation)))){
                configLocation.delete();
                configLocation.createNewFile();
                writer.println("Point of Aim Distance: " + DEFAULT_P_O_A);
                writer.println("Zoomed Point of Aim Distance: " + DEFAULT_P_O_A_ZOOMED);
                writer.println("Zoom Factor: " + DEFAULT_ZOOM);
                writer.println("Loft Factor: " + DEFAULT_LOFT);
                writer.println("Zoomed Loft Factor: " + DEFAULT_LOFT_ZOOMED);
                writer.println("Throw Factor: " + DEFAULT_THROW);
                writer.println("Zoomed Throw Factor: " + DEFAULT_THROW_ZOOMED);
                cameraPointOfAimDistance = (DEFAULT_P_O_A);
                cameraPointOfAimDistanceZoomed = (DEFAULT_P_O_A_ZOOMED);
                cameraZoomFactor = (1/DEFAULT_ZOOM);
                cameraLoftFactor = (DEFAULT_LOFT);
                cameraLoftZoomedFactor = (DEFAULT_LOFT_ZOOMED);
                cameraThrowFactor = (DEFAULT_THROW);
                cameraThrowZoomedFactor = (DEFAULT_THROW_ZOOMED);
            } catch (IOException ex) {
                throw new RuntimeException(ex);
            }
        }
    }
    @Override
    public ConfigScreenFactory<?> getModConfigScreenFactory() {
        return new GhConfigScreenFactory();
    }

    private static class GhConfigScreenFactory implements ConfigScreenFactory<Screen> {
        public Screen create(Screen parent) {
            ConfigBuilder configBuilder = ConfigBuilder.create()
                    .setParentScreen(parent)
                    .setTitle(Text.translatable("config.gallopinghorses.title"))
                    .setSavingRunnable(() -> {
                        try (PrintWriter writer = new PrintWriter(new BufferedWriter(new FileWriter(configLocation)))){
                            configLocation.delete();
                            configLocation.createNewFile();
                            writer.println("Point of Aim Distance: " + cameraPointOfAimDistance);
                            writer.println("Zoomed Point of Aim Distance: " + cameraPointOfAimDistanceZoomed);
                            writer.println("Zoom Factor: " + 1/cameraZoomFactor);
                            writer.println("Loft Factor: " + cameraLoftFactor);
                            writer.println("Zoomed Loft Factor: " + cameraLoftZoomedFactor);
                            writer.println("Throw Factor: " + cameraThrowFactor);
                            writer.println("Zoomed Throw Factor: " + cameraThrowZoomedFactor);
                        } catch (IOException ex) {
                            throw new RuntimeException(ex);
                        }
                    });
            ConfigEntryBuilder entryBuilder = configBuilder.entryBuilder();
            ConfigCategory camera = configBuilder.getOrCreateCategory(Text.translatable("category.gallopinghorses.camera"));
            camera.addEntry(entryBuilder.startIntSlider(
                    Text.translatable("config.gallopinghorses.loft"),
                    (int)(cameraLoftFactor*(128/MAX_LOFT)), 0, 128)
                    .setDefaultValue((int) (DEFAULT_LOFT * 128/MAX_LOFT))
                    .setTooltip(Text.translatable("tooltip.gallopinghorses.loft"))
                    .setSaveConsumer(val -> cameraLoftFactor = val*(MAX_LOFT/128))
                    .setTextGetter(val -> Text.literal(String.format("%.2f", val*(MAX_LOFT/128))))
                    .build()
            );
            camera.addEntry(entryBuilder.startIntSlider(
                            Text.translatable("config.gallopinghorses.loftzoomed"),
                            (int)(cameraLoftZoomedFactor*(128/MAX_LOFT_ZOOMED)), 0, 128)
                    .setDefaultValue((int) (DEFAULT_LOFT_ZOOMED * 128/MAX_LOFT_ZOOMED))
                    .setTooltip(Text.translatable("tooltip.gallopinghorses.loftzoomed"))
                    .setSaveConsumer(val -> cameraLoftZoomedFactor = val*(MAX_LOFT_ZOOMED/128))
                    .setTextGetter(val -> Text.literal(String.format("%.2f", val*(MAX_LOFT_ZOOMED/128))))
                    .build()
            );
            camera.addEntry(entryBuilder.startIntSlider(
                            Text.translatable("config.gallopinghorses.throw"),
                            (int)(cameraThrowFactor*(128/MAX_THROW)), 0, 128)
                    .setDefaultValue((int) (DEFAULT_THROW * 128/MAX_THROW))
                    .setTooltip(Text.translatable("tooltip.gallopinghorses.throw"))
                    .setSaveConsumer(val -> cameraThrowFactor = val*(MAX_THROW/128))
                    .setTextGetter(val -> Text.literal(String.format("%.2f", val*(MAX_THROW/128))))
                    .build()
            );
            camera.addEntry(entryBuilder.startIntSlider(
                            Text.translatable("config.gallopinghorses.throwzoomed"),
                            (int)(cameraThrowZoomedFactor*(128/MAX_THROW_ZOOMED)), 0, 128)
                    .setDefaultValue((int) (DEFAULT_THROW_ZOOMED * 128/MAX_THROW_ZOOMED))
                    .setTooltip(Text.translatable("tooltip.gallopinghorses.throwzoomed"))
                    .setSaveConsumer(val -> cameraThrowZoomedFactor = val*(MAX_THROW_ZOOMED/128))
                    .setTextGetter(val -> Text.literal(String.format("%.2f", val*(MAX_THROW_ZOOMED/128))))
                    .build()
            );
            camera.addEntry(entryBuilder.startIntSlider(
                            Text.translatable("config.gallopinghorses.zoom"),
                            (int)((1/cameraZoomFactor) * (128/MAX_ZOOM)), 16, 128)
                    .setDefaultValue((int) (DEFAULT_ZOOM * 128/MAX_ZOOM))
                    .setTooltip(Text.translatable("tooltip.gallopinghorses.zoom"))
                    .setSaveConsumer(val -> cameraZoomFactor = 1.0/(val*(MAX_ZOOM/128)))
                    .setTextGetter(val -> Text.literal(String.format("%.2f", (val*(MAX_ZOOM/128)))))
                    .build()
            );
            camera.addEntry(entryBuilder.startIntSlider(
                            Text.translatable("config.gallopinghorses.poa"),
                            (int)(cameraPointOfAimDistance*(128/MAX_P_O_A)), 16, 128)
                    .setDefaultValue((int) (DEFAULT_P_O_A * 128/MAX_P_O_A))
                    .setTooltip(Text.translatable("tooltip.gallopinghorses.poa"))
                    .setSaveConsumer(val -> cameraPointOfAimDistance = val*(MAX_P_O_A/128))
                    .setTextGetter(val -> Text.literal(String.format("%.2f", val*(MAX_P_O_A/128))))
                    .build()
            );
            camera.addEntry(entryBuilder.startIntSlider(
                            Text.translatable("config.gallopinghorses.poazoomed"),
                            (int)(cameraPointOfAimDistanceZoomed*(128/MAX_P_O_A_ZOOMED)), 16, 128)
                    .setDefaultValue((int) (DEFAULT_P_O_A_ZOOMED * 128/MAX_P_O_A_ZOOMED))
                    .setTooltip(Text.translatable("tooltip.gallopinghorses.poazoomed"))
                    .setSaveConsumer(val -> cameraPointOfAimDistanceZoomed = val*(MAX_P_O_A_ZOOMED/128))
                    .setTextGetter(val -> Text.literal(String.format("%.2f", val*(MAX_P_O_A_ZOOMED/128))))
                    .build()
            );
            return configBuilder.build();
        }
    }
}
