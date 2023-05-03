package me.saehyeon.manhunt;

import com.destroystokyo.paper.profile.PlayerProfile;
import net.md_5.bungee.api.ChatMessageType;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.*;
import org.bukkit.boss.BarColor;
import org.bukkit.boss.BarStyle;
import org.bukkit.boss.BossBar;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.SkullMeta;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitTask;
import org.bukkit.scoreboard.Scoreboard;
import org.bukkit.util.Vector;

import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.logging.Level;
import java.util.stream.Collectors;

import static me.saehyeon.manhunt.Message.*;

interface Runnable {
    void run(List<Player> players);
}

enum DirectionType {
    FORWARD("↑"),
    BACKWARD("↓"),
    LEFT("←"),
    RIGHT("→"),
    FORWARD_RIGHT("↗"),
    FORWARD_LEFT("↖"),
    BACKWARD_RIGHT("↘"),
    NONE("??"),
    BACKWARD_LEFT("↙");

    final String krName;
    DirectionType(String krName) {
        this.krName = krName;
    }
}
public class ManHunt {
    public static ManHunt getInstance() {
        return new ManHunt();
    }
    static HashMap<UUID, Inventory> inv = new HashMap<>();
    public static HashMap<UUID, TaskType> tasks = new HashMap<>();
    public static boolean cantMove = false;
    public static UUID targetUUID;
    static String originName;

    static BukkitTask runAwayTimer;
    static BukkitTask updateWaitTarget;
    static BukkitTask updateActionBar;
    static BukkitTask updateMaxHealth;
    public static boolean isGaming = false;
    public static boolean debugMode = false;
    public static boolean isStartCountDowning = false;
    public static Scoreboard scoreboard;
    public static TargetKitType targetKitType = TargetKitType.NONE;
    public static HashMap<World, Location> targetLastLoc = new HashMap<>();
    public static GameSettings gameSettings = new GameSettings();
    static BossBar runAwayTimerBossBar;

    /**
     * 타깃 설정 GUI
     * @param player 띄울 플레이어
     */
    public void openTargetSettingGUI(Player player) {
        inv.put(player.getUniqueId(), Bukkit.createInventory(null, 54, GUI_TS_TITLE));
        player.openInventory(inv.get(player.getUniqueId()));
        tasks.put(player.getUniqueId(), TaskType.TARGET_SETTING);

        // 랜덤 타깃 설정 아이템 배치
        ItemStack random = new ItemStack(Material.BARRIER);
        ItemMeta meta = random.getItemMeta();
        meta.setDisplayName(GUI_TS_NONE_SELECT);
        meta.setLore(Arrays.asList(GUI_TS_NONE_SELECT_INFO));
        random.setItemMeta(meta);

        player.getOpenInventory().setItem(0, random);

        // 서버에 있는 플레이어들의 머리를 아이템으로 넣기
        int index = 1;
        for(Player p : Bukkit.getOnlinePlayers()) {
            ItemStack head = new ItemStack(Material.PLAYER_HEAD);
            SkullMeta skullMeta = (SkullMeta) head.getItemMeta();
            skullMeta.setOwningPlayer(p);
            skullMeta.setDisplayName("§f§l"+p.getName());
            skullMeta.setLore(Arrays.asList(GUI_TS_SELECT_TO_TARGET));
            head.setItemMeta(skullMeta);
            player.getOpenInventory().setItem(index++, head);
        }
    }

    /**
     * 게임 설정 GUI
     */
    public void openGameSettingGUI(Player player) {
        inv.put(player.getUniqueId(), Bukkit.createInventory(null, 9*3, GUI_GS_TITLE));
        player.openInventory(inv.get(player.getUniqueId()));
        tasks.put(player.getUniqueId(), TaskType.GAME_SETTING);

        // 거리 보이기 토글
        ItemStack distance = new ItemStack(Material.NAME_TAG);
        ItemMeta meta = distance.getItemMeta();

        meta.setDisplayName(GUI_GS_DISTANCE);
        meta.setLore(Arrays.asList(gameSettings.enable_show_distance ? GUI_ENABLED : GUI_DISABLED));
        distance.setItemMeta(meta);

        // 집결 모드
        ItemStack unite = new ItemStack(Material.TRIDENT);
        meta = unite.getItemMeta();

        meta.setDisplayName(GUI_GS_UNITE);
        meta.setLore(Arrays.asList(GUI_GS_UNITE_INFO, "", gameSettings.enable_unite ? GUI_ENABLED : GUI_DISABLED));
        unite.setItemMeta(meta);

        // 포만감
        ItemStack antiHunger = new ItemStack(Material.COOKED_BEEF);
        meta = antiHunger.getItemMeta();

        meta.setDisplayName(GUI_GS_ANTI_HUNGER);
        meta.setLore(Arrays.asList(GUI_GS_ANTI_HUNGER_INFO, "", gameSettings.enable_anti_hunger ? GUI_ENABLED : GUI_DISABLED));
        antiHunger.setItemMeta(meta);

        // 밸런스 모드
        ItemStack balance = new ItemStack(Material.IRON_SWORD);
        meta = balance.getItemMeta();

        meta.setDisplayName(GUI_GS_BALANCE);
        meta.setLore(Arrays.asList(
                GUI_GS_BALANCE_INFO_L1,
                GUI_GS_BALANCE_INFO_L2,
                GUI_GS_BALANCE_INFO_L3,
                "",
                gameSettings.enable_balance_mode ? GUI_ENABLED : GUI_DISABLED
        ));
        meta.addItemFlags(ItemFlag.HIDE_ATTRIBUTES);

        balance.setItemMeta(meta);

        // 아이템 배치
        player.getOpenInventory().setItem(10,distance);
        player.getOpenInventory().setItem(12,unite);
        player.getOpenInventory().setItem(14,antiHunger);
        player.getOpenInventory().setItem(16,balance);

    }

    /**
     * 타깃 기본템 설정 GUI
     */
    public void openTargetItemGUI(Player player) {
        inv.put(player.getUniqueId(), Bukkit.createInventory(null, 9*5, GUI_TI_TITLE));
        player.openInventory(inv.get(player.getUniqueId()));
        tasks.put(player.getUniqueId(), TaskType.TARGET_DEFALT_ITEM_SETTING);
        
        // 아이템 배치
        ItemMeta meta;

        // 없음
        ItemStack none = new ItemStack(Material.BARRIER);
        meta = none.getItemMeta();
        meta.setDisplayName(GUI_TI_NONE);
        meta.setLore(Arrays.asList(GUI_TI_NONE_INFO,(targetKitType == null ? GUI_TI_SELECTED : null)));

        none.setItemMeta(meta);

        // 나무 세트
        ItemStack woodSet = new ItemStack(Material.WOODEN_SWORD);
        meta = none.getItemMeta();
        meta.setDisplayName(GUI_TI_WOOD_SET);
        meta.setLore(Arrays.asList(GUI_TI_WOOD_SET_INFO,(targetKitType == TargetKitType.WOOD ? GUI_TI_SELECTED : null)));
        meta.addItemFlags(ItemFlag.HIDE_ATTRIBUTES);

        woodSet.setItemMeta(meta);

        // 돌 세트
        ItemStack stoneSet = new ItemStack(Material.STONE_SWORD);
        meta = none.getItemMeta();
        meta.setDisplayName(GUI_TI_STONE_SET);
        meta.setLore(Arrays.asList(GUI_TI_STONE_SET_INFO,(targetKitType == TargetKitType.STONE ? GUI_TI_SELECTED : null)));
        meta.addItemFlags(ItemFlag.HIDE_ATTRIBUTES);

        stoneSet.setItemMeta(meta);

        // 철 세트
        ItemStack ironSet = new ItemStack(Material.IRON_SWORD);
        meta = none.getItemMeta();
        meta.setDisplayName(GUI_TI_IRON_SET);
        meta.setLore(Arrays.asList(GUI_TI_IRON_SET_INFO,(targetKitType == TargetKitType.IRON ? GUI_TI_SELECTED : null)));
        meta.addItemFlags(ItemFlag.HIDE_ATTRIBUTES);

        ironSet.setItemMeta(meta);

        // 다이아몬드 세트
        ItemStack diamondSet = new ItemStack(Material.DIAMOND_SWORD);
        meta = none.getItemMeta();
        meta.setDisplayName(GUI_TI_DIAMOND_SET);
        meta.setLore(Arrays.asList(GUI_TI_DIAMOND_SET_INFO,(targetKitType == TargetKitType.DIAMOND ? GUI_TI_SELECTED : null)));
        meta.addItemFlags(ItemFlag.HIDE_ATTRIBUTES);

        diamondSet.setItemMeta(meta);

        // 네더라이트 세트
        ItemStack netheriteSet = new ItemStack(Material.NETHERITE_SWORD);
        meta = none.getItemMeta();
        meta.setDisplayName(GUI_TI_NETHERITE_SET);
        meta.setLore(Arrays.asList(GUI_TI_NETHERITE_SET_INFO,(targetKitType == TargetKitType.NETHERITE ? GUI_TI_SELECTED : null)));
        meta.addItemFlags(ItemFlag.HIDE_ATTRIBUTES);

        netheriteSet.setItemMeta(meta);

        player.getOpenInventory().setItem(10, none);
        player.getOpenInventory().setItem(12, woodSet);
        player.getOpenInventory().setItem(14, stoneSet);
        player.getOpenInventory().setItem(16, ironSet);
        player.getOpenInventory().setItem(28, diamondSet);
        player.getOpenInventory().setItem(30, netheriteSet);
    }

    /**
     * 타깃을 랜덤으로 설정합니다. (관전자는 제외됩니다.)
     */
    void UpdateRandomTarget() {
        ArrayList<Player> players = new ArrayList<>(Bukkit.getOnlinePlayers());
        players.removeIf(p -> p.getGameMode() == GameMode.SPECTATOR);
        targetUUID = players.get(new Random().nextInt(players.size()-1)).getUniqueId();
        Bukkit.broadcastMessage("");
        Bukkit.broadcastMessage("목표를 랜덤으로 설정했습니다. 목표는 §7"+Bukkit.getPlayer(targetUUID).getName()+"§f입니다.");
    }

    /**
     * 집결 모드에서 타깃을 제외한 플레이어들의 최대 체력을 갱신합니다.
     */
    BukkitTask UpdateMaxHealth() {
        return Bukkit.getScheduler().runTaskTimer(Main.ins, () -> {
            Player target = Bukkit.getPlayer(targetUUID);

            if(target != null || debugMode) {

                Main.DebugLog("최대 체력 갱신을 진행합니다.");

                ArrayList<Player> players = new ArrayList<>(Bukkit.getOnlinePlayers());

                // 타깃을 제외하기
                players.remove(target);
                Main.DebugLog(" -> 타깃을 제외한 플레이어 목록을 구함");

                HashMap<World, ArrayList<Player>> worldPlayers = new HashMap<>();

                // 같은 월드에 있는 플레이어들끼리 묶기
                for(World w : Bukkit.getWorlds()) {
                    worldPlayers.put(w, new ArrayList<>());
                    worldPlayers.get(w).addAll(Bukkit.getOnlinePlayers().stream().filter(p -> p != target && p.getWorld() == w).collect(Collectors.toList()));
                    Main.DebugLog(" -> "+w.getName()+" 월드에 있는 플레이어 인원: "+worldPlayers.get(w).size()+"봊");
                }

                Main.DebugLog(" -> 같은 월드에 있는 플레이어들끼리 묶었습니다.");

                // ---- 월드 별 최대 체력 구하기 (월드에 아무도 없을때는 아무것도 하지 않기)
                for(World w : Bukkit.getWorlds()) {

                    if(worldPlayers.get(w).size() >= 2) {

                        Main.DebugLog(" -> " + w.getName() + " 월드의 최대 체력을 구하는 중");

                        // 평균 벡터 구하기
                        Vector sum = new Vector(0, 0, 0);

                        for (Player p : worldPlayers.get(w)) {
                            sum.add(p.getLocation().toVector());
                        }

                        Main.DebugLog(" --> 벡터 총합: " + sum);

                        Vector avg = sum.multiply(1D / worldPlayers.get(w).size());

                        Main.DebugLog(" --> 평균 벡터: " + avg);

                        // 평균 벡터와 플레이어간의 거리에 따라 최대 체력 설정하기
                        // -> 평균 벡터와 가장 멀리 떨어진 거리를 구하기
                        double maxDistance = 0;

                        for (Player p : worldPlayers.get(w)) {
                            double distance = p.getLocation().toVector().distance(avg);

                            // 거리가 평균 벡터와 10 이상일때만 최대체력을 적용함
                            maxDistance = Math.max(maxDistance, distance);
                        }

                        // 거리가 10 이상 떨어져 있을 때만 최대 체력 조작
                        Main.DebugLog(" --> " + w.getName() + " 월드에서 평균 벡터로부터 가장 멀리 떨어져 있는 플레이어와 평균 벡터와의 거리: " + maxDistance);

                        if (maxDistance >= 15) {
                            for (Player p : worldPlayers.get(w)) p.setMaxHealth(Math.max(20D - (maxDistance / 2D), 1));
                        } else {
                            for (Player p : worldPlayers.get(w)) p.resetMaxHealth();
                        }

                        Main.DebugLog(" --> " + w.getName() + " 월드에서의 최대 체력 갱신 종료");
                    }
                }
            }
        },0,20);
    }

    /**
     * 플레이어의 1인칭 시점을 기준으로 특정 위치로 가기 위한 방향을 반환합니다.
     * @param player 기준 플레이어
     * @param targetLoc 목표 위치
     */
    public DirectionType getDirection(Player player, Location targetLoc) {

        // 목표까지 이동하기 위한 방향
        Vector targetDir = targetLoc.clone().subtract(player.getLocation()).toVector().normalize().setY(0);

        // 내가 바라보는 방향
        Vector myDir = player.getLocation().getDirection().normalize().setY(0);

        // 내가 바라보는 방향과 목표까지 이동하기 위한 방향을 선으로 그엇을 때 선이 충돌하는 부분의 라디안를 계산하기 (벡터 사이의 라디안 각도 계산)
        double radian = myDir.angle(targetDir);

        // 라디안 값을 각도 값으로 변환
        double angle = Math.toDegrees(radian);

        // 각도만 구하면 y축 대칭에서 오른쪽, 왼쪽 상관없이 같은 각도가 되므로 각도가 오른쪽으로 계속 돌게 하기
        // 이렇게하면 바라보는 방향이 y축 대칭이더라도 다른 부호를 가지게 됨 (벡터외적 구하는 거)
        Vector crossProduct = myDir.clone().crossProduct(targetDir);

        if (crossProduct.getY() > 0)
            angle = -angle;

        if(debugMode) {
            player.sendMessage("각도: "+angle);
        }

        // ---- 값 반환 시작
        if(22.5 <= angle && angle <= 67.5)
            return DirectionType.FORWARD_RIGHT;

        else if(67.5 <= angle && angle < 112.5)
            return DirectionType.RIGHT;

        else if(112.5 <= angle && angle < 157.5)
            return DirectionType.BACKWARD_RIGHT;

        else if((157.5 <= angle && angle <= 180) || (-179 <= angle && angle <= -157.5))
            return DirectionType.BACKWARD;

        else if(-157.5 <= angle && angle < -112.5)
            return DirectionType.BACKWARD_LEFT;

        else if(-112.5 <= angle && angle < -67.5)
            return DirectionType.LEFT;

        else if(-67.5 <= angle && angle < -22.5)
            return DirectionType.FORWARD_LEFT;

        else if(-22.5 <= angle && angle <= 22.5)
            return DirectionType.FORWARD;

        return DirectionType.NONE;
    }

    /**
     * 타깃을 제외한 플레이어들 목록을 반환합니다.
     * @return 타깃을 제외한 플레이어들 목록
     */
    public static List<Player> getPlayersWithoutTarget() {
        Player target = Bukkit.getPlayer(targetUUID);
        return Bukkit.getOnlinePlayers().stream().filter(p -> p != target).collect(Collectors.toList());
    }

    /**
     * 플레이어에 따라 설정될 엑션바의 문자열을 반환합니다. <br />
     * UpdateActionBar 메소드에서 호출됩니다.
     * @param player 기준 플레이어
     * @return 설정될 엑션바의 문자열
     */
    String getActionBarStr(Player player) {
        Player target = Bukkit.getPlayer(targetUUID);
        StringBuilder actionbar;

        // 같은 월드에 있지 않을 경우, 같은 월드에 있었을 때 타깃의 마지막 위치를 보여주기
        if(target.getWorld() != player.getWorld()) {
            Location lastLoc = targetLastLoc.getOrDefault(player.getWorld(), null);

            if(lastLoc != null)
                actionbar = new StringBuilder("방향§f §l"+getDirection(player,lastLoc).krName);
            else
                actionbar = new StringBuilder("방향 §c§l확인 불가");
        } else {
            actionbar = new StringBuilder("방향§f §l"+getDirection(player,target.getLocation()).krName);
        }

        if(gameSettings.enable_show_distance) {
            String distanceStr = player.getWorld() == target.getWorld() ? "§l"+Math.floor(player.getLocation().distance(target.getLocation()))+"M" : "§c§l측정할 수 없어요.";
            actionbar.append("§f  |  §f거리 "+distanceStr);
        }

        return actionbar.toString();
    }

    /**
     * 타깃을 제외한 플레이어들의 엑션바를 갱신합니다.
     */
    BukkitTask UpdateActionBar() {
        Main.ins.getLogger().log(Level.INFO,"엑션바 갱신 BukkitTask를 시작했습니다.");

        // 플레이어 별 마지막으로 출력된 엑션바 메세지
        HashMap<Player, String> lastActionBar = new HashMap<>();

        // 갱신 시간을 확인하기 위한 카운트
        AtomicInteger i = new AtomicInteger(0);

        // 전체 유저의 엑션바 메세지 갱신
        Runnable updateActionBar = (players) -> {
            Player target = Bukkit.getPlayer(targetUUID);

            players.forEach(p -> {

                // 엑션바에 띄울 문자열
                String actionbar = getActionBarStr(p);

                // 마지막 엑션바 문자열 저장
                lastActionBar.put(p, actionbar);

            });
        };

        updateActionBar.run(ManHunt.getPlayersWithoutTarget());

        return Bukkit.getScheduler().runTaskTimer(Main.ins, () -> {

            Player target = Bukkit.getPlayer(targetUUID);
            List<Player> players = ManHunt.getPlayersWithoutTarget();

            if(target != null) {

                // 갱신 시간 확인 카운트 증가
                i.getAndIncrement();

                // 갱신해야함
                // -> 갱신 시간 0으로 초기화
                if(i.get() >= gameSettings.update_actionbar_sec*4) {

                    // 갱신 카운터 초기화
                    i.set(0);

                    // 전체 유저의 엑션바 메세지 갱신
                    updateActionBar.run(players);
                }

                // 엑션바 출력
                players.forEach(p -> {
                    p.spigot().sendMessage(ChatMessageType.ACTION_BAR, new TextComponent(lastActionBar.getOrDefault(p, "")));
                });
            }

        },0,5);
    }

    /** 타깃에게 기본템 주기 */
    void giveDefaultItem() {
        Player target = Bukkit.getPlayer(targetUUID);

        ArrayList<ItemStack> items = new ArrayList<>();

        switch(targetKitType) {
            case WOOD:
                items.add(new ItemStack(Material.WOODEN_SWORD));
                items.add(new ItemStack(Material.WOODEN_PICKAXE));
                items.add(new ItemStack(Material.WOODEN_AXE));
                items.add(new ItemStack(Material.WOODEN_SHOVEL));
                break;

            case STONE:
                items.add(new ItemStack(Material.STONE_SWORD));
                items.add(new ItemStack(Material.STONE_PICKAXE));
                items.add(new ItemStack(Material.STONE_AXE));
                items.add(new ItemStack(Material.STONE_SHOVEL));
                break;

            case IRON:
                items.add(new ItemStack(Material.IRON_SWORD));
                items.add(new ItemStack(Material.IRON_PICKAXE));
                items.add(new ItemStack(Material.IRON_AXE));
                items.add(new ItemStack(Material.IRON_SHOVEL));
                break;

            case DIAMOND:
                items.add(new ItemStack(Material.DIAMOND_SWORD));
                items.add(new ItemStack(Material.DIAMOND_PICKAXE));
                items.add(new ItemStack(Material.DIAMOND_AXE));
                items.add(new ItemStack(Material.DIAMOND_SHOVEL));
                break;

            case NETHERITE:
                items.add(new ItemStack(Material.NETHERITE_SWORD));
                items.add(new ItemStack(Material.NETHERITE_PICKAXE));
                items.add(new ItemStack(Material.NETHERITE_AXE));
                items.add(new ItemStack(Material.NETHERITE_SHOVEL));
                break;
        }

        items.forEach(item -> target.getInventory().addItem(item));
    }

    /**
     * 타깃을 기다리는 타이머를 시작합니다. <br />
     * 타깃이 게임을 나갔을 때 호출됩니다.
     */
    public void StartWaitTarget() {

        // 타이머 시작 공지
        Bukkit.broadcastMessage(WAIT_TIMER_START);
        cantMove = true;

        updateWaitTarget = Bukkit.getScheduler().runTaskTimer(Main.ins,() -> {
            if(Bukkit.getPlayer(targetUUID) == null) {
                Bukkit.getOnlinePlayers().forEach(p -> p.sendTitle(WAIT_TIMER_TITLE, WAIT_TIMER_SUBTITLE,0,40,0));
            }
        },0,20);
    }

    /**
     * 타깃을 기다리는 타이머를 종료합니다. <br />
     * 타깃이 게임에 접속했을 때 호출됩니다
     */
    public void StopWaitTarget() {
        if(updateWaitTarget != null)
            updateWaitTarget.cancel();

        cantMove = false;
    }

    public static boolean isTarget(Player player) {
        return targetUUID != null && player.getUniqueId().toString().equals(targetUUID.toString());
    }

    BukkitTask StartRunawayTimer() {
        final String BOSSBAR_TITLE_STR = "§f§l이동 시작까지 남은시간";

        runAwayTimerBossBar = Bukkit.createBossBar(BOSSBAR_TITLE_STR, BarColor.WHITE, BarStyle.SOLID);

        Bukkit.getOnlinePlayers().forEach(runAwayTimerBossBar::addPlayer);

        // 이동 시작 카운트 다운 상태 활성화
        isStartCountDowning = true;

        // 움직임 차단
        cantMove = true;

        // 타깃을 제외한 나머지 인원에게 움직임 차단 타이틀 출력
        Bukkit.getOnlinePlayers().stream().filter(p -> !isTarget(p)).forEach(p -> {
            p.sendTitle(CANT_MOVE,"",0,20*gameSettings.start_time_sec,0);
        });

        final double decrement = 1D / (double)gameSettings.start_time_sec;

        // 남은 시간(초)
        AtomicInteger leftSec = new AtomicInteger(gameSettings.start_time_sec);

        return Bukkit.getScheduler().runTaskTimer(Main.ins, () -> {

            double _progress = runAwayTimerBossBar.getProgress() - decrement;

            if(_progress > 0) {

                leftSec.getAndDecrement();
                runAwayTimerBossBar.setProgress(_progress);
                runAwayTimerBossBar.setTitle(BOSSBAR_TITLE_STR+": "+leftSec.get()+"초");

            } else {
                // 이동 시작 카운트 다운 상태 비활성화
                isStartCountDowning = false;

                // 이동시작 타이틀 출력
                Bukkit.getOnlinePlayers().forEach(p -> p.sendTitle(MOVE_START,MOVE_START_INFO,0,50,15));

                // 움직임 차단 해제
                cantMove = false;

                // 보스바 제거
                runAwayTimerBossBar.removeAll();

                // 타이머 취소
                runAwayTimer.cancel();
            }

        },0,20);
    }

    public void Start(Player player) {
        // 2명 이상의 사람이 있어야 시작함
        if(Bukkit.getOnlinePlayers().stream().filter(p -> p.getGameMode() != GameMode.SPECTATOR).collect(Collectors.toList()).size() < 2) {
            player.sendMessage("§c게임을 시작할 수 없어요. 게임을 진행하기 위해서는 2명 이상의 플레이어가 필요해요.");
            return;
        }

        // 현재 타깃이 존재하지 않다면, 타깃을 랜덤으로 설정하기
        if(targetUUID == null) UpdateRandomTarget();

        // 게임 중 상태 활성화
        isGaming = true;

        // ---- 월드 생성 시작
        World oldManHuntWorld = Bukkit.getWorld("manhunt");

        if(oldManHuntWorld != null) {

            // 월드 언로드
            Bukkit.unloadWorld(oldManHuntWorld,false);

            // 월드 삭제
            Util.sendActionbarAll("§7기존의 멘헌트 세계를 삭제 중이에요..");
            Util.deleteDirectory(oldManHuntWorld.getWorldFolder().getAbsolutePath());
        }

        // ---- 월드 생성
        Util.sendActionbarAll("§7멘헌트 세계를 생성 중이에요..");

        WorldCreator worldCreator = new WorldCreator("manhunt");
        World manHuntWorld = worldCreator.createWorld();

        Util.sendActionbarAll("§7명령자를 멘헌트 세계로 이동시켰어요.");
        player.teleport(manHuntWorld.getSpawnLocation());

        // ---- 월드 세팅
        Util.sendActionbarAll("§7세계들을 설정하고 있어요..");

        player.getWorld().setTime(3000);

        // 모든 월드에 떨어진 아이템 없애기
        Util.sendActionbarAll("§7모든 세계의 아이템을 삭제하고 있어요..");
        for(World world : Bukkit.getWorlds()) {
            world.getEntities().stream().filter(en -> en.getType() == EntityType.DROPPED_ITEM).forEach(item -> item.remove());
            world.setGameRule(GameRule.DO_IMMEDIATE_RESPAWN, true);
        }

        // ---- 모든 플레이어에 관한 처리
        Util.sendActionbarAll("§7플레이어 상태를 초기화하고 있어요..");
        Bukkit.getOnlinePlayers().forEach(p -> {
            p.eject();
            p.setGameMode(GameMode.SURVIVAL);
            p.resetMaxHealth();
            p.getInventory().clear();
            p.setHealth(20);
            p.setFoodLevel(20);
            p.setBedSpawnLocation(player.getLocation(),true);
        });

        // ---- 모든 플레이어 랜덤 TP
        Bukkit.getScheduler().runTaskLater(Main.ins, () -> {
            Util.sendActionbarAll("§7모든 플레이어를 랜덤 TP하고 있어요..");

            Util.spreadPlayer(player.getLocation(), Bukkit.getOnlinePlayers().size() / 2);
        },5);

        // ---- 타깃 관련
        Player target = Bukkit.getPlayer(targetUUID);

        // 타깃 기본템 지급
        giveDefaultItem();
        Util.sendActionbarAll("§7타깃에게 기본템을 지급했어요.");

        // ---- 엑션바 갱신 시작
        updateActionBar = UpdateActionBar();
        Util.sendActionbarAll("§7엑션바 갱신을 시작합니다.");

        // ---- 게임 설정에 따른 출력 및 처리

        // 집결모드
        if(gameSettings.enable_unite) {
            Bukkit.broadcastMessage("");
            Bukkit.broadcastMessage("§c§l집결! §f타깃이 아닌 사람은 얼마나 뭉쳐다니는 지에 비례하여 최대 체력이 갱신됩니다.");
            updateMaxHealth = UpdateMaxHealth();
            Util.sendActionbarAll("§7최대 체력 갱신을 시작합니다.");
        }

        // 로리콘 모드
        if(gameSettings.lolicon_mode) {
            Bukkit.broadcastMessage("");
            Bukkit.broadcastMessage(LOLICON_INFO);

            // 원래 이름 저장
            originName = target.getName();

            PlayerProfile pp = target.getPlayerProfile();
            pp.setName("히유");
            target.setPlayerProfile(pp);
        }

        // 포만감 모드
        if(gameSettings.enable_anti_hunger) {
            Bukkit.broadcastMessage("");
            Bukkit.broadcastMessage(ANTI_HUNGER_INFO);
        }

        // 밸런스 모드
        if(gameSettings.enable_balance_mode) {
            Bukkit.broadcastMessage("");
            Bukkit.broadcastMessage(BALANCE_INFO);

            Util.sendActionbarAll("§7밸런스 모드에 따른 처리 중이에요.");

            Bukkit.getOnlinePlayers().forEach(p -> {
                p.setFoodLevel(10);
                p.addPotionEffect(new PotionEffect(PotionEffectType.REGENERATION, 100000, 0,false,false,false));
            });
        }

        // 타깃에게는 도망가라고 하기
        target.sendTitle("§f§l도망가세요!","1분 30초 동안 도망가야해요.");
        Bukkit.broadcastMessage("");
        Bukkit.broadcastMessage("타깃("+target.getName()+")을 제외한 인원은 1분 30초 후 움직일 수 있습니다.");
        Bukkit.broadcastMessage("");

        // 타깃을 제외한 나머지 인원은 20초 동안 가만히 있기
        cantMove = true;

        // 움직임 해제 카운트 다운 시작
        runAwayTimer = StartRunawayTimer();
    }

    public void Stop(RoleType winRole) {
        Main.ins.getLogger().log(Level.INFO,"게임을 중지하는 중");

        //TODO 이거 보스바 타이머 없애셈
        if(runAwayTimerBossBar != null)
            runAwayTimerBossBar.removeAll();

        // 게임 중 상태 비활성화
        isGaming = false;

        // 히유 이름 다시 되돌리기
        if(gameSettings.lolicon_mode) {
            if(originName != null) {
                Player target = Bukkit.getPlayer(targetUUID);

                PlayerProfile pp = target.getPlayerProfile();
                pp.setName(originName);
                target.setPlayerProfile(pp);
            }
        }

        if(updateActionBar != null) {
            updateActionBar.cancel();
            Main.ins.getLogger().log(Level.INFO," -> 액션바 갱신 중지");
        }

        if(updateMaxHealth != null) {
            updateMaxHealth.cancel();
            Main.ins.getLogger().log(Level.INFO," -> 최대 체력 갱신 중지");
        }

        if(runAwayTimer != null) {
            runAwayTimer.cancel();
            Main.ins.getLogger().log(Level.INFO," -> 이동 시작 카운트 다운 중지");
        }

        if(updateWaitTarget != null) {
            ManHunt.getInstance().StopWaitTarget();
            Main.ins.getLogger().log(Level.INFO," -> 타깃을 기다리는 타이머 중지");
        }

        ArrayList<Player> players = new ArrayList<>(Bukkit.getOnlinePlayers());

        // ---- 모든 플레이어 후처리
        // - 스폰 재 지정
        World world = Bukkit.getWorld("world");
        final Location WORLD_SPAWN = new Location(world,-6,4,-5);
        WORLD_SPAWN.setPitch(0);
        WORLD_SPAWN.setYaw(180);

        players.forEach(p -> {
            p.setBedSpawnLocation(WORLD_SPAWN);
            world.setSpawnLocation(WORLD_SPAWN);

            // 월드로 이동
            p.teleport(WORLD_SPAWN);
        });

        // ---- 승리 메세지 띄우기
        if(winRole != null) {

            // 소리 재생
            players.forEach(p -> p.playSound(p.getLocation(), Sound.UI_TOAST_CHALLENGE_COMPLETE, SoundCategory.MASTER,1,1));

            // 이긴 사람이 타깃
            if(winRole == RoleType.TARGET) {
                players.forEach(p -> {
                    if (ManHunt.isTarget(p)) {
                        p.sendTitle("§b§l승리!", "엔더유적에 진입했어요.", 0, 50, 20);
                    } else {
                        p.sendTitle("§c§l타깃을 놓침!", "타깃이 엔더유적에 진입했어요.", 0, 50, 20);
                    }
                });
            }

            // 이긴 사람이 술래
            else {
                players.forEach(p -> {
                    if (!ManHunt.isTarget(p)) {
                        p.sendTitle("§b§l승리!", "타깃을 잡았어요.", 0, 50, 20);
                    } else {
                        p.sendTitle("§c§l도망가지 못 함!", "엔더유적에 진입하지 못했어요.", 0, 50, 20);
                    }
                });
            }

        }

        targetUUID = null;
        Main.ins.getLogger().log(Level.INFO," -> 타깃 UUID 제거");

        cantMove = false;

        Bukkit.broadcastMessage("게임이 종료되었습니다.");
    }
}
