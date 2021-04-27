package net.kunmc.lab.superhot;

import net.kunmc.lab.superhot.config.Config;
import net.kunmc.lab.superhot.event.ConstantEvent;
import net.kunmc.lab.superhot.event.EventListener;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.attribute.Attribute;
import org.bukkit.attribute.AttributeModifier;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.util.Vector;

import java.util.UUID;


public final class SuperHot extends JavaPlugin {

    public static SuperHot plugin;
    private static UUID ACCELERATION_ID = UUID.randomUUID();
    private static UUID DECELERATION_ID = UUID.randomUUID();
    private static AttributeModifier ACCELERATION = new AttributeModifier(ACCELERATION_ID, "Accelerate entity", 0.5D, AttributeModifier.Operation.MULTIPLY_SCALAR_1);
    private static AttributeModifier DECELERATION = new AttributeModifier(DECELERATION_ID, "Decelerate entity", -0.5D, AttributeModifier.Operation.MULTIPLY_SCALAR_1);

    public static boolean main = false;

    public static SuperHot getPlugin() {
        return plugin;
    }

    @Override
    public void onEnable() {
        // Plugin startup logic
        this.getCommand("SuperHot").getExecutor();
        this.getLogger().info("Enabled SUPER HOT Plugin!");
        this.register();
        plugin = this;
        new ConstantEvent(this).runTaskTimer(plugin, 0, 3);
        Config.load(false);
        removeAttribute();
    }

    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String Label, String[] args){
        if(cmd.getName().equals("SuperHot")){
            if(args.length==0){
                if(main){
                    sender.sendMessage(ChatColor.GREEN+"SuperHotプラグインを無効にしました！");
                    main = false;
                }else{
                    sender.sendMessage(ChatColor.GREEN+"SuperHotプラグインを有効にしました！");
                    main = true;
                }
                removeAttribute();
            }else{
                sender.sendMessage(ChatColor.RED+"コマンドの形式が異なります！正しい形式:/SuperHot");
            }
        }
        return false;
    }

    @Override
    public void onDisable() {
        // Plugin shutdown logic
        this.getLogger().info("Disabled SUPER HOT Plugin!");
        removeAttribute();
    }

    public static void removeAttribute(){
        Bukkit.getOnlinePlayers().forEach(player -> {
            player.getAttribute(Attribute.GENERIC_ATTACK_SPEED).removeModifier(ACCELERATION);
            player.getAttribute(Attribute.GENERIC_ATTACK_SPEED).removeModifier(DECELERATION);
            player.getAttribute(Attribute.GENERIC_MOVEMENT_SPEED).removeModifier(ACCELERATION);
            player.getAttribute(Attribute.GENERIC_MOVEMENT_SPEED).removeModifier(DECELERATION);
            player.setAI(true);
            player.setVelocity(new Vector());
        });
    }

    private void register() {
        this.getServer().getPluginManager().registerEvents(new EventListener(this), this);
    }
}
