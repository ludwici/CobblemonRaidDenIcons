package com.ludwici.cri.config;

import java.util.function.IntSupplier;
import java.util.function.Supplier;

public final class CriConfig {
    private static IntSupplier iconSize = () -> ConfigConstants.DEFAULT_ICON_SIZE;
    private static IntSupplier teraIconSize = () -> ConfigConstants.DEFAULT_TERA_ICON_SIZE;
    private static Supplier<DioramaIconStyle> dioramaIconStyle = () -> DioramaIconStyle.DEFAULT;

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

    public static void setDioramaIconStyleSupplier(Supplier<DioramaIconStyle> supplier) {
        dioramaIconStyle = supplier;
    }

    public static DioramaIconStyle dioramaIconStyle() {
        DioramaIconStyle style = dioramaIconStyle.get();
        return style == null ? DioramaIconStyle.DEFAULT : style;
    }
}
