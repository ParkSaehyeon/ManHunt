package me.saehyeon.manhunt;

public class Message {
    public static final String STATE_ENABLED            = "§f§l예";
    public static final String STATE_DISABLED           = "§7아니요";
    public static final String GUI_ENABLED              = "§f》 현재 §a켜져있습니다.";
    public static final String GUI_DISABLED             = "§f》 현재 §c꺼져있습니다.";

    /*
     *
     *       게임 설정 GUI
     *
     */
    public static final String GUI_GS_TITLE             = "게임 설정";
    public static final String GUI_GS_DISTANCE          = "§f§l거리 보이기";

    // 포만감 모드 ------------------------------
    public static final String GUI_GS_ANTI_HUNGER       = "§f§l포만감";
    public static final String GUI_GS_ANTI_HUNGER_INFO  = "§f허기가 닳지 않습니다.";

    // 밸런스 모드 ------------------------------
    public static final String GUI_GS_BALANCE           = "§f§l밸런스";
    public static final String GUI_GS_BALANCE_INFO_L1   = "§f게임의 밸런스가 플러그인에서 추전하는 대로 맞춰집니다.";
    public static final String GUI_GS_BALANCE_INFO_L2   = "§f모두에게 §u재생 1이 지급되며";
    public static final String GUI_GS_BALANCE_INFO_L3   = "§f허기가 19로 고정(허기로 인한 체력 자동재생 차단)됩니다.";

    // 집결 모드 ------------------------------
    public static final String GUI_GS_UNITE             = "§f§l집결";
    public static final String GUI_GS_UNITE_INFO        = "§f쫓는 사람들은 뭉쳐서 나아가야 합니다.";

    /*
    *
    *       타깃 설정 GUI
    *
    */
    public static final String GUI_TS_TITLE             = "타깃 수동 설정";

    // 타깃 설정 안함 ------------------------------
    public static final String GUI_TS_NONE_SELECT       = "§c§결정하지 않음";
    public static final String GUI_TS_NONE_SELECT_INFO  = "§f게임 시작 시 타깃이 랜덤으로 결정됩니다.";

    // 타깃 수동 설정 관련 ------------------------------
    public static final String GUI_TS_SELECT_TO_TARGET  = "§f클릭하여 타깃으로 결정합니다.";

    /*
     *
     *       타깃 기본 아이템 설정 GUI
     *
     */
    public static final String GUI_TI_TITLE             = "타깃 기본템 설정";
    public static final String GUI_TI_NONE_INFO         = "§f타깃은 시작 시 아이템을 지급받지 않습니다.";
    public static final String GUI_TI_NONE              = "§c§l없음";
    public static final String GUI_TI_WOOD_SET          = "§6§l나무 세트";
    public static final String GUI_TI_WOOD_SET_INFO     = "§f§f타깃은 시작 시 §6§l나무 세트§f를 지급받습니다.";
    public static final String GUI_TI_STONE_SET         = "§7§l돌 세트";
    public static final String GUI_TI_STONE_SET_INFO    = "§f§f타깃은 시작 시 §7§l돌 세트§f를 지급받습니다.";
    public static final String GUI_TI_IRON_SET          = "§f§l철 세트";
    public static final String GUI_TI_IRON_SET_INFO     = "§f§f타깃은 시작 시 §f§l철 세트§f를 지급받습니다.";
    public static final String GUI_TI_DIAMOND_SET       = "§b§l다이아몬드 세트";
    public static final String GUI_TI_DIAMOND_SET_INFO  = "§f§f타깃은 시작 시 §b§l다이아몬드 세트§f를 지급받습니다.";
    public static final String GUI_TI_NETHERITE_SET     = "§d§l네더라이트 세트";
    public static final String GUI_TI_NETHERITE_SET_INFO  = "§f§f타깃은 시작 시 §d§l네더라이트 세트§f를 지급받습니다.";
    public static final String GUI_TI_SELECTED          = "§f§l현재 선택되어 있습니다.";

    /*
     *
     *       시스템 메세지
     *
     */
    // 모드 관련 ------------------------------
    public static final String MODE_ENABLED          = "§a활성화했습니다.";
    public static final String MODE_DISABLED         = "§c비활성화했습니다.";
    public static final String BALANCE_INFO          = "§b§l밸런스! §f모든 사람에게 재생 1이 지급되었으며 허기로 인한 체력 재생이 비활성화됩니다.";
    public static final String ANTI_HUNGER_INFO      = "§6§l포만감! §f허기가 닳지 않습니다.";

    // 타깃 대기 타이머 관련 ------------------------------
    public static final String WAIT_TIMER_START      = "§c§l타깃을 찾을 수 없어요. §f타깃인 플레이어가 다시 접속할때까지 기다릴게요.";
    public static final String WAIT_TIMER_TITLE      = "§f§l타깃 대기 중";
    public static final String WAIT_TIMER_SUBTITLE   = "타깃이 다시 접속할때까지 기다리고 있어요.";
}
