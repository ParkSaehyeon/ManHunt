package me.saehyeon.manhunt.event;

import me.saehyeon.manhunt.Main;
import me.saehyeon.manhunt.ManHunt;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import static me.saehyeon.manhunt.ManHunt.gameSettings;
import static me.saehyeon.manhunt.Message.STATE_DISABLED;
import static me.saehyeon.manhunt.Message.STATE_ENABLED;

public class onCommand implements CommandExecutor {
    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        Player p = (Player)sender;

        if(label.equals("멘헌트")) {
            try {
                switch(args[0]) {
                    case "디버그인듯":
                        ManHunt.debugMode = !ManHunt.debugMode;
                        Bukkit.broadcastMessage("§7"+p.getName()+"§f로 인해 디버그 모드가 켜진듯");
                        break;

                    case "기본템설정":
                        ManHunt.getInstance().openTargetItemGUI(p);
                        break;

                    case "테스트":
                        p.sendMessage(ManHunt.targetUUID+", 내 UUID: "+p.getUniqueId());
                        break;

                    case "시작":
                        ManHunt.getInstance().Start(p);
                        break;

                    case "타깃수동설정":
                        ManHunt.getInstance().openTargetSettingGUI(p);
                        break;

                    case "게임설정":
                        ManHunt.getInstance().openGameSettingGUI(p);
                        break;

                    case "시작대기시간":
                        gameSettings.start_time_sec = Integer.parseInt(args[1]);
                        p.sendMessage("§7"+p.getName()+"§f에 의해 시간 대기 시간이 §7"+gameSettings.start_time_sec +"§f초로 설정되었습니다.");
                        break;

                    case "상태":
                        p.sendMessage("");
                        p.sendMessage("§f현재 멘헌트의 상태에요.");
                        p.sendMessage("§l[ 상태 ]");
                        p.sendMessage("§f시작 대기 시간§7  "+ gameSettings.start_time_sec+"초");
                        p.sendMessage("§f엑션바 갱신 시간§7  "+ gameSettings.update_actionbar_sec+"초"+(gameSettings.update_actionbar_sec == 0 ? "(기본값)" : ""));
                        p.sendMessage("");
                        p.sendMessage("§l[ 모드 ]");
                        p.sendMessage("§f허기 닳기 금지§7  "+(gameSettings.enable_anti_hunger ? STATE_ENABLED : STATE_DISABLED));
                        p.sendMessage("§f밸런스 모드§7  "+(gameSettings.enable_balance_mode ? STATE_ENABLED : STATE_DISABLED));
                        p.sendMessage("§f거리 보이기§7  "+(gameSettings.enable_show_distance ? STATE_ENABLED : STATE_DISABLED));
                        p.sendMessage("§f집결 모드§7  "+(gameSettings.enable_unite ? STATE_ENABLED : STATE_DISABLED));
                        break;

                    case "엑션바갱신시간":
                        gameSettings.update_actionbar_sec = Integer.parseInt(args[1]);
                        p.sendMessage("§7"+p.getName()+"§f에 의해 엑션바의 갱신시간이 §7"+gameSettings.update_actionbar_sec +"§f초로 설정되었습니다.");
                        break;

                    case "li":
                        gameSettings.lolicon_mode = !gameSettings.lolicon_mode;

                        if(gameSettings.lolicon_mode) {
                            Bukkit.getOnlinePlayers().forEach(_p -> _p.sendTitle("§d§l로리콘 모드", "§7" + p.getName() + "§f에 의해 활성화됨", 0, 50, 0));

                            Bukkit.getScheduler().runTaskLater(Main.ins, () -> {
                                Bukkit.getOnlinePlayers().forEach(_p -> _p.sendTitle("§f§l로§d§l리콘 모드", "§7" + p.getName() + "§f에 의해 활성화됨", 0, 50, 0));

                                Bukkit.getScheduler().runTaskLater(Main.ins, () -> {
                                    Bukkit.getOnlinePlayers().forEach(_p -> _p.sendTitle("§f§l로리§d§l콘 모드", "§7" + p.getName() + "§f에 의해 활성화됨", 0, 50, 0));

                                    Bukkit.getScheduler().runTaskLater(Main.ins, () -> {
                                        Bukkit.getOnlinePlayers().forEach(_p -> _p.sendTitle("§f§l로리콘§d§l 모드", "§7" + p.getName() + "§f에 의해 활성화됨", 0, 50, 0));

                                        Bukkit.getScheduler().runTaskLater(Main.ins, () -> {
                                            Bukkit.getOnlinePlayers().forEach(_p -> _p.sendTitle("§f§l로리콘 모§d§l드", "§7" + p.getName() + "§f에 의해 활성화됨", 0, 50, 0));

                                            Bukkit.getScheduler().runTaskLater(Main.ins, () -> {
                                                Bukkit.getOnlinePlayers().forEach(_p -> _p.sendTitle("§d§l로§f§l리콘 모드", "§7" + p.getName() + "§f에 의해 활성화됨", 0, 50, 0));

                                                Bukkit.getScheduler().runTaskLater(Main.ins, () -> {
                                                    Bukkit.getOnlinePlayers().forEach(_p -> _p.sendTitle("§d§l로리§f§l콘 모드", "§7" + p.getName() + "§f에 의해 활성화됨", 0, 50, 0));

                                                    Bukkit.getScheduler().runTaskLater(Main.ins, () -> {
                                                        Bukkit.getOnlinePlayers().forEach(_p -> _p.sendTitle("§d§l로리콘 §f§l모드", "§7" + p.getName() + "§f에 의해 활성화됨", 0, 50, 0));

                                                        Bukkit.getScheduler().runTaskLater(Main.ins, () -> {
                                                            Bukkit.getOnlinePlayers().forEach(_p -> _p.sendTitle("§d§l로리콘 모§f§l드", "§7" + p.getName() + "§f에 의해 활성화됨", 0, 50, 0));

                                                            Bukkit.getScheduler().runTaskLater(Main.ins, () -> {
                                                                Bukkit.getOnlinePlayers().forEach(_p -> _p.sendTitle("§d§l로리콘 모드", "§7" + p.getName() + "§f에 의해 활성화됨", 0, 50, 20));
                                                            }, 1);

                                                        }, 1);

                                                    }, 1);

                                                }, 1);

                                            }, 1);

                                        }, 1);

                                    }, 1);

                                }, 1);

                            }, 1);
                        } else {
                            Bukkit.broadcastMessage("§d§l로리콘 모드§f가 §7"+p.getName()+"§f에 의해 비활성화되었습니다.");
                        }
                        break;

                    case "중지":
                        ManHunt.getInstance().Stop();
                        break;
                    default:
                        p.sendMessage("§c알 수 없는 명령입니다. 사용법은 '/멘헌트 [시작/타깃수동설정/게임설정/기본템설정/중지/엑션바갱신시간]'입니다.");
                        break;
                }
            } catch (Exception e) {
                e.printStackTrace();
                p.sendMessage("§c올바르지 않은 명령입니다.");
            }
        }

        return false;
    }
}
