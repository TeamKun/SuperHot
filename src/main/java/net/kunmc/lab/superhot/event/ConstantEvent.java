package net.kunmc.lab.superhot.event;

import net.kunmc.lab.superhot.SuperHot;
import net.kunmc.lab.superhot.config.Config;
import net.kunmc.lab.superhot.helper.Helper;
import org.bukkit.Material;
import org.bukkit.Particle;
import org.bukkit.World;
import org.bukkit.entity.Item;
import org.bukkit.entity.Player;
import org.bukkit.entity.Snowball;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.Vector;


public class ConstantEvent extends BukkitRunnable{
    private final SuperHot plugin;
    public static KunMovementState kunMovementState = KunMovementState.Stopping;

    public ConstantEvent(SuperHot plugin) {
        this.plugin = plugin;
    }

    @Override
    public void run() {
        if(SuperHot.main) {
            Player kun = plugin.getServer().getPlayer(Config.timeFreezer);
            if (kun == null) {
                kunMovementState = KunMovementState.Disable;
                return;
            }
            if (kun != null) {
                World world = kun.getWorld();
                if (Helper.clockHolder == null && Config.superHotEnabled) {
                    if (Helper.isKunMoving(kun) && world != null) {
                        if (kun.isSprinting() && kunMovementState != KunMovementState.Running) {
                            world.getEntities().stream()
                                    .filter(e -> !Helper.isKun(e))
                                    .forEach(Helper::accelerate);
                            kunMovementState = KunMovementState.Running;
                            kun.sendActionBar("時間の流れが加速した！");
                        } else if (kun.isSneaking() && kunMovementState != KunMovementState.Sneaking) {
                            world.getEntities().stream()
                                    .filter(e -> !Helper.isKun(e))
                                    .forEach(Helper::decelerate);
                            kunMovementState = KunMovementState.Sneaking;
                            kun.sendActionBar("時間の流れが減速した！");
                        } else if (!kun.isSprinting() && !kun.isSneaking() && kunMovementState != KunMovementState.Walking) {
                            world.getEntities().stream()
                                    .filter(e -> !Helper.isKun(e))
                                    .forEach(Helper::release);
                            kunMovementState = KunMovementState.Walking;
                            kun.sendActionBar("時間の流れが元通りになった！");
                        }
                    } else if (!Helper.isKunMoving(kun) && kunMovementState != KunMovementState.Stopping) {
                        world.getEntities().stream()
                                .filter(e -> !Helper.isKun(e))
                                .forEach(Helper::freeze);
                        kunMovementState = KunMovementState.Stopping;
                        kun.sendActionBar("時間の流れが止まった！");
                    }
                }
                if (!Config.superHotEnabled)
                    kunMovementState = KunMovementState.Disable;
                world.getEntities().stream()
                        .forEach(e -> {
                            if (e instanceof Snowball) {
                                Snowball s0 = (Snowball) e;
                                Snowball s1 = s0.getLocation().getNearbyEntitiesByType(Snowball.class, 1).stream()
                                        .findFirst().orElse(null);
                                if (s1 != null && !s0.getUniqueId().toString().equalsIgnoreCase(s1.getUniqueId().toString())) {
                                    Helper.destroyBullet(s0, kun);
                                    Helper.destroyBullet(s1, kun);
                                }
                            } else if (e instanceof Item) {
                                String name = e.getCustomName();
                                if (name != null) {
                                    if (name.equalsIgnoreCase("throw")
                                            && !e.getVelocity().equals(new Vector(0, 0, 0))) {

                                        Item i = (Item) e;
                                        Snowball s = i.getLocation().getNearbyEntitiesByType(Snowball.class, 1).stream()
                                                .findFirst().orElse(null);
                                        if (s != null) {
                                            Helper.destroyBullet(s, kun);
                                        }
                                        Player p = i.getLocation().getNearbyEntitiesByType(Player.class, 0.5).stream()
                                                .findFirst().orElse(null);
                                        if (p != null) {
                                            if (!Helper.isKun(p)) {
                                                p.setHealth(0);
                                                p.spawnParticle(Particle.BLOCK_CRACK, p.getLocation().getX(), p.getLocation().getY(), p.getLocation().getZ(), 20, Material.REDSTONE_BLOCK.createBlockData());
                                            }
                                        }
                                    }
                                }
                            }
                        });
            }
        }
    }

    public enum KunMovementState {
        Running,
        Sneaking,
        Walking,
        Stopping,
        Disable
    }
}
