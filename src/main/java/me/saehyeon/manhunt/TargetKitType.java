package me.saehyeon.manhunt;

public enum TargetKitType {
    NONE("§c§l없음"),
    WOOD("§6§l나무 세트"),
    STONE("§7§l돌 세트"),
    IRON("§f§l철 세트"),
    DIAMOND("§b§l다이아몬드 세트"),
    NETHERITE("§d§l네더라이트 세트");

    final String krName;
    TargetKitType(String krName) {
        this.krName = krName;
    }
    public String getKRName() {
        return krName;
    }
}
