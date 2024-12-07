package com.lst.auto_l.modules;

import today.opai.api.enums.EnumModuleCategory;
import today.opai.api.features.ExtensionModule;
import today.opai.api.interfaces.EventHandler;
import today.opai.api.interfaces.modules.special.ModuleKillAura;
import today.opai.api.interfaces.game.entity.LivingEntity;

import java.util.Arrays;
import java.util.List;
import java.util.Random;

import static com.lst.auto_l.LLL.openAPI;

public class AutoL extends ExtensionModule implements EventHandler {
    public AutoL() {
        super("Auto L", "Auto say 垃圾话 when u kill somebody", EnumModuleCategory.MISC);
        setEventHandler(this);

    }
    public List<String> Lwords= Arrays.asList("RiseClient-Elevate your Minecraft experience to new heights.",
            "Dominate the competition with RiseClient,the ultimate Minecraft cheating solution.",
            "RiseClient-Flawlessly bypass servers like Hypixel,Cubecraft,and BlocksMC.",
            "Experience the premium RiseClient,a year in the making with revamped designs and features.",
            "Cleanest GUI in gaming-RiseClient makes bypassing easy for all skill levels.",
            "Feature-filled and customizable,RiseClient has the module for every Minecraft fantasy.",
            "Personalize your cheat with RiseClient,designed to fit your style.",
            "RiseClient-Configurable to bypass any anticheat,keeping your gameplay fresh.",
            "Adaptable to any Minecraft version from 1.8 to 1.19.4,RiseClient is your go-to client.",
            "Secure and compliant,RiseClient guarantees 99.9%uptime and supports over 8 languages for a global gaming experience.");
    private LivingEntity target;
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
            openAPI.getLocalPlayer().sendChatMessage(getRandomElement(Lwords));
            isSent = true;
        }

        EventHandler.super.onTick();
    }
    private static <T> T getRandomElement(List<T> list) {
        if (list == null || list.isEmpty()) {
            return null; // 如果列表为空，返回null
        }
        Random random = new Random();
        int randomIndex = random.nextInt(list.size()); // 获取一个随机索引
        return list.get(randomIndex); // 返回对应索引的元素
    }

}
