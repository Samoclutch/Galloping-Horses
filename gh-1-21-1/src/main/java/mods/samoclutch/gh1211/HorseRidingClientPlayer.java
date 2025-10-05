package mods.samoclutch.gh1211;

public interface HorseRidingClientPlayer {
    boolean gallopingHorses$getGallopInput();
    boolean getBrakeInput();
    void setBrakeInput(boolean brake);
    boolean getZoomInput();
    void setZoomInput(boolean zoom);
    int getCameraPositionInput();
    void setCameraPositionInput(int position);
}
