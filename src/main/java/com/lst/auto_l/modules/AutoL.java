package com.lst.auto_l.modules;

import today.opai.api.enums.EnumModuleCategory;
import today.opai.api.features.ExtensionModule;
import today.opai.api.interfaces.EventHandler;
import today.opai.api.interfaces.game.entity.LivingEntity;
import today.opai.api.interfaces.modules.special.ModuleKillAura;
import today.opai.api.interfaces.modules.values.BooleanValue;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Random;

import static com.lst.auto_l.LLL.openAPI;

public class AutoL extends ExtensionModule implements EventHandler {
    public AutoL() {
        super("Auto L", "sent L-words when u kill somebody", EnumModuleCategory.MISC);
        setEventHandler(this);
        // 获取 %appdata% 目录
        String appDataPath = System.getenv("APPDATA");
        if (appDataPath == null) {
            System.err.println("无法获取 APPDATA 路径");
            return;
        }
        // 拼接完整路径
        configPath = appDataPath + File.separator + "Opai" + File.separator + "autoL.txt";
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(new FileInputStream(configPath), StandardCharsets.UTF_8))) {
            String line;
            values=new ArrayList<>();
            while ((line = reader.readLine()) != null) {
                values.add(line);
            }
            System.out.println("已加载自定义垃圾话");
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
            System.out.println("垃圾话文件未找到,生成中");
        }catch (IOException e) {
            e.printStackTrace();
        }
        super.addValues(shout);
        super.addValues(antiSpam);
    }
    public List<String> values;
    private LivingEntity target;
    private final BooleanValue shout=openAPI.getValueManager().createBoolean("Shout",false);
    private final BooleanValue antiSpam=openAPI.getValueManager().createBoolean("AntiSpam",true);
    private String configPath;//配置地址
    public boolean isSent;
    @Override
    public void onTick() {
        ModuleKillAura killAura = (ModuleKillAura) openAPI.getModuleManager().getModule("KillAura");
        LivingEntity entity=killAura.getTarget();
        if (entity!=target&&entity!=null) {
            target = entity;
            isSent = false;
        }
        if (target.getHealth()==0.0&&!isSent) {
//            openAPI.printMessage(String.valueOf(target.getName()));
//            openAPI.printMessage(getRandomElement(Lwords));
            sentMessage();
            isSent = true;
        }
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
        String message="";
        if (shout.getValue())message+="/shout ";
        message+=getRandomElement(values).replaceAll("\\{player}", target.getName());
        if (antiSpam.getValue())message+=" "+generateRandomString(5);
        openAPI.getLocalPlayer().sendChatMessage(message);
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

}
