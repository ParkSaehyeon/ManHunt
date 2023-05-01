package me.saehyeon.manhunt.event;

import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Arrays;
import java.util.List;

public class onTabComplete implements TabCompleter {
    @Override
    public @Nullable List<String> onTabComplete(@NotNull CommandSender sender, @NotNull Command command, @NotNull String alias, @NotNull String[] args) {
        if(alias.equals("멘헌트")) {
            return Arrays.asList("시작","기본템설정","타깃수동설정","게임설정","중지","디버그인듯","테스트","엑션바갱신시간","상태","시작대기시간");
        }
        return null;
    }
}
