package com.ludwici.cri.config;

import java.util.function.IntSupplier;
import java.util.function.Supplier;

public final class CriConfig {
    private static IntSupplier iconSize = () -> ConfigConstants.DEFAULT_ICON_SIZE;
    private static IntSupplier teraIconSize = () -> ConfigConstants.DEFAULT_TERA_ICON_SIZE;
    private static Supplier<MapIconStyle> dioramaIconStyle = () -> MapIconStyle.DEFAULT;
    private static Supplier<MapIconStyle> ftbIconStyle = () -> MapIconStyle.DEFAULT;

    private CriConfig() {
    }

    public static void setIconSizeSupplier(IntSupplier supplier) {
        iconSize = supplier;
    }

    public static int iconSize() {
        return iconSize.getAsInt();
    }

    public static void setTeraIconSizeSupplier(IntSupplier supplier) {
        teraIconSize = supplier;
    }

    public static int teraIconSize() {
        return teraIconSize.getAsInt();
    }

    public static void setMapIconStyleSupplier(Supplier<MapIconStyle> supplier) {
        dioramaIconStyle = supplier;
    }

    public static MapIconStyle dioramaIconStyle() {
        MapIconStyle style = dioramaIconStyle.get();
        return style == null ? MapIconStyle.DEFAULT : style;
    }

    public static void setFtbIconStyleSupplier(Supplier<MapIconStyle> supplier) {
        ftbIconStyle = supplier;
    }

    public static MapIconStyle ftbIconStyle() {
        MapIconStyle style = ftbIconStyle.get();
        return style == null ? MapIconStyle.DEFAULT : style;
    }
}
