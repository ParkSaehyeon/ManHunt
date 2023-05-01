package me.saehyeon.manhunt;

import org.bukkit.configuration.file.YamlConfiguration;

import java.io.*;
import java.nio.file.Files;
import java.util.Base64;
import java.util.logging.Level;

public class GameSettings implements Serializable {
    public boolean enable_show_distance = false;
    public boolean enable_unite = false;
    public boolean enable_anti_hunger = false;
    public boolean enable_balance_mode = false;
    public int start_time_sec = 5;
    public boolean lolicon_mode = false;
    public int update_actionbar_sec = 20;

    public GameSettings() {}

    public static void save() {
        try {
            ByteArrayOutputStream bos = new ByteArrayOutputStream();
            ObjectOutputStream out = new ObjectOutputStream(bos);
            out.writeObject(ManHunt.gameSettings);
            out.flush();
            out.close();
            bos.close();

            String serializedObject = Base64.getEncoder().encodeToString(bos.toByteArray());
            File file = new File(Main.ins.getDataFolder(), "settings.json");
            FileWriter w = new FileWriter(file);

            w.write(serializedObject);

            w.close();

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void load() {
        File file = new File(Main.ins.getDataFolder(), "settings.json");

        try {
            if(!Files.exists(file.toPath())) {
                file.createNewFile();
                return;
            }

            String serializedObject = Files.readAllLines(file.toPath()).get(0);
            byte[] data = Base64.getDecoder().decode(serializedObject);
            ObjectInputStream in = new ObjectInputStream(new ByteArrayInputStream(data));
            ManHunt.gameSettings = (GameSettings) in.readObject();
            in.close();
            Main.ins.getLogger().log(Level.INFO,"");
        } catch (IOException e) {
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            throw new RuntimeException(e);
        }
    }
}
