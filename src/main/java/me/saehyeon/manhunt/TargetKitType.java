package me.saehyeon.manhunt;

import static me.saehyeon.manhunt.Message.*;

public enum TargetKitType {
    NONE(GUI_TI_NONE),
    WOOD(GUI_TI_WOOD_SET),
    STONE(GUI_TI_STONE_SET),
    IRON(GUI_TI_IRON_SET),
    DIAMOND(GUI_TI_DIAMOND_SET),
    NETHERITE(GUI_TI_NETHERITE_SET);

    final String krName;
    TargetKitType(String krName) {
        this.krName = krName;
    }
    public String getKRName() {
        return krName;
    }
}
