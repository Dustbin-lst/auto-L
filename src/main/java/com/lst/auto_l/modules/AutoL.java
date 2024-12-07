package com.lst.auto_l.modules;

import today.opai.api.enums.EnumModuleCategory;
import today.opai.api.features.ExtensionModule;
import today.opai.api.interfaces.EventHandler;
import today.opai.api.interfaces.game.entity.LivingEntity;
import today.opai.api.interfaces.game.entity.Player;
import today.opai.api.interfaces.game.world.World;
import today.opai.api.interfaces.modules.special.ModuleKillAura;
import today.opai.api.interfaces.modules.values.BooleanValue;
import today.opai.api.interfaces.modules.values.NumberValue;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.time.Instant;
import java.util.*;
import java.util.concurrent.atomic.AtomicReference;

import static com.lst.auto_l.LLL.openAPI;

public class AutoL extends ExtensionModule implements EventHandler {
    public AutoL() {
        super("Auto L", "sent L-words when u kill somebody", EnumModuleCategory.MISC);
        setEventHandler(this);
        // 获取 %appdata% 目录
        String appDataPath = System.getenv("APPDATA");
        if (appDataPath == null) {
            System.err.println("[AutoL]Config path not found!!!");
            throw new RuntimeException("Config path not found");
        }
        // 拼接完整路径
        configPath = appDataPath + File.separator + "Opai" + File.separator + "autoL.txt";
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(new FileInputStream(configPath), StandardCharsets.UTF_8))) {
            String line;
            values=new ArrayList<>();
            while ((line = reader.readLine()) != null) {
                values.add(line);
            }
            System.out.println("[AutoL]Config loaded");
        } catch (FileNotFoundException e) {
            values=Arrays.asList("RiseClient-Elevate your Minecraft experience to new heights.",
                    "Dominate the competition with RiseClient,the ultimate Minecraft cheating solution.",
                    "RiseClient-Flawlessly bypass servers like Hypixel,Cubecraft,and BlocksMC.",
                    "Experience the premium RiseClient,a year in the making with revamped designs and features.",
                    "Cleanest GUI in gaming-RiseClient makes bypassing easy for all skill levels.",
                    "Feature-filled and customizable,RiseClient has the module for every Minecraft fantasy.",
                    "Personalize your cheat with RiseClient,designed to fit your style.",
                    "RiseClient-Configurable to bypass any anticheat,keeping your gameplay fresh.",
                    "Adaptable to any Minecraft version from 1.8 to 1.19.4,RiseClient is your go-to client.",
                    "Secure and compliant,RiseClient guarantees 99.9%uptime and supports over 8 languages for a global gaming experience.");
            saveConfig();
            System.out.println("[AutoL]Config not found");
        }catch (IOException e) {
            e.printStackTrace();
        }
        super.addValues(shout);
        super.addValues(antiSpam);
        super.addValues(trackTimeout);
        super.addValues(messageCooldown);
    }
    public List<String> values;
    private TimerTask task = new TimerTask() {
        @Override
        public void run() {
            targetID=-1;
            System.out.println("[AutoL]target lost!!!");
        }
    };
    private int targetID=-1;
    private long messageTimeStamp=0;
    private String targetName;
    private final BooleanValue shout=openAPI.getValueManager().createBoolean("Shout",false);
    private final BooleanValue antiSpam=openAPI.getValueManager().createBoolean("AntiSpam",true);
    private final NumberValue trackTimeout=openAPI.getValueManager().createDouble("TrackTimeout",60,10,120,0.1);
    private final NumberValue messageCooldown=openAPI.getValueManager().createDouble("MessageCooldown",3,0,60,0.1);
    private String configPath;//配置地址

    @Override
    public void onTick() {
        try {
            ModuleKillAura killAura = (ModuleKillAura) openAPI.getModuleManager().getModule("KillAura");
            LivingEntity entity=killAura.getTarget();
            if (!Objects.equals(entity.getEntityId(), targetID)) {
                targetID = entity.getEntityId();
                try {
                    targetName=getPlayerByID(targetID).getName();
                    System.out.println("[AutoL]tracking "+ entity.getName());
                }catch (Exception e){
                    targetID=-1;
                    System.err.println("[AutoL]Not A Player");
                }
            }else{
                task.cancel();
                task = new TimerTask() {
                    @Override
                    public void run() {
                        targetID=-1;
                        System.out.println("[AutoL]target lost!!!");
                    }
                };
                new Timer().schedule(task, (long) (trackTimeout.getValue()*1000));
            }
        } catch (Exception e) {
            //System.out.println("killAura没开呢");
        }
        if (targetID!=-1)try {
            getPlayerByID(targetID);
        } catch (NullPointerException e) {
            sentMessage();
            targetID=-1;
            task.cancel();
        } catch (IllegalArgumentException e){
            System.out.println("[AutoL]NOT A PLAYER");
        }
    }

    @Override
    public void onLoadWorld() {
        targetID=-1;
    }

    private static <T> T getRandomElement(List<T> list) {
        if (list == null || list.isEmpty()) {
            return null; // 如果列表为空，返回null
        }
        Random random = new Random();
        int randomIndex = random.nextInt(list.size()); // 获取一个随机索引
        return list.get(randomIndex); // 返回对应索引的元素
    }
    private void saveConfig() {
        try (PrintWriter writer = new PrintWriter(new FileWriter(configPath))) {
            values.forEach(writer::println);
            System.out.println("内容已写入文件");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    private void sentMessage() {
        if(Instant.now().getEpochSecond()-messageTimeStamp<messageCooldown.getValue()){
            openAPI.printMessage("[AutoL]Message could not be sent because the interval was too short.");
            return;
        }
        String message="";
        if (shout.getValue())message+="/shout ";
        message+=getRandomElement(values).replaceAll("\\{player}", targetName);
        if (antiSpam.getValue())message+=" "+generateRandomString(5);
        openAPI.getLocalPlayer().sendChatMessage(message);
        messageTimeStamp= Instant.now().getEpochSecond();
    }

    private static String generateRandomString(int length) {
        String characters = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789";
        StringBuilder stringBuilder = new StringBuilder();
        Random random = new Random();

        for (int i = 0; i < length; i++) {
            int index = random.nextInt(characters.length());
            stringBuilder.append(characters.charAt(index));
        }

        return stringBuilder.toString();
    }
    private static Player getPlayerByID(int id) {
        AtomicReference<Player> output = new AtomicReference<>();
        openAPI.getWorld().getLoadedPlayerEntities().forEach(player -> {
            if (player.getEntityId() == id) {
                output.set(player);
            }
        });
        if (output.get() == null) {throw new NullPointerException("player not found");}
        return output.get();
    }

}
