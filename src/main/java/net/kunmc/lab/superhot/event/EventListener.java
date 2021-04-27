package net.kunmc.lab.superhot.event;

import com.destroystokyo.paper.event.player.PlayerLaunchProjectileEvent;
import io.papermc.paper.event.entity.EntityMoveEvent;
import net.kunmc.lab.superhot.SuperHot;
import net.kunmc.lab.superhot.helper.Helper;
import org.bukkit.Material;
import org.bukkit.Particle;
import org.bukkit.Sound;
import org.bukkit.attribute.Attribute;
import org.bukkit.attribute.AttributeModifier;
import org.bukkit.entity.Player;
import org.bukkit.entity.Snowball;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;

import org.bukkit.event.entity.EntityPickupItemEvent;
import org.bukkit.event.entity.ProjectileHitEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.Vector;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

public class EventListener implements Listener {
    private final SuperHot plugin;

    public EventListener(SuperHot plugin) {
        this.plugin = plugin;
    }
    private static UUID ACCELERATION_ID = UUID.randomUUID();
    private static UUID DECELERATION_ID = UUID.randomUUID();
    private static AttributeModifier ACCELERATION = new AttributeModifier(ACCELERATION_ID, "Accelerate entity", 0.5D, AttributeModifier.Operation.MULTIPLY_SCALAR_1);
    private static AttributeModifier DECELERATION = new AttributeModifier(DECELERATION_ID, "Decelerate entity", -0.5D, AttributeModifier.Operation.MULTIPLY_SCALAR_1);

    @EventHandler
    public void onPlayerMove(PlayerMoveEvent event) {
        if(SuperHot.main) {
            if (Helper.clockHolder != null || ConstantEvent.kunMovementState == ConstantEvent.KunMovementState.Stopping) {
                if (!Helper.isClockHolder(event.getPlayer()) && !Helper.isKun(event.getPlayer())) {
                    event.setCancelled(true);
                }
            }
        }
    }
    @EventHandler
    public void onEntityMove(EntityMoveEvent event) {
        if(SuperHot.main) {
            if (Helper.clockHolder != null || ConstantEvent.kunMovementState == ConstantEvent.KunMovementState.Stopping) {
                if (!Helper.isClockHolder(event.getEntity()) && !Helper.isKun(event.getEntity())) {
                    event.setCancelled(true);
                }
            }
        }
    }

    @EventHandler
    public void onPlayerShoot(PlayerLaunchProjectileEvent event) {
        if(SuperHot.main) {
            if (ConstantEvent.kunMovementState == ConstantEvent.KunMovementState.Stopping || Helper.clockHolder != null) {
                new BukkitRunnable() {
                    @Override
                    public void run() {
                        Helper.freeze(event.getProjectile());
                    }
                }.runTaskLater(plugin, 4);
            }
        }
        }

    @EventHandler
    public void onUseItem(PlayerInteractEvent event) {
        if(SuperHot.main) {
            final Player player = event.getPlayer();
            final ItemStack itemstack = event.getItem();
            final Action action = event.getAction();
            if (itemstack == null) return;
            if (ConstantEvent.kunMovementState == ConstantEvent.KunMovementState.Stopping) {
                if (!Helper.isKun(player)) {
                    event.setCancelled(true);
                }
            } else if (Helper.clockHolder != null) {
                if (!Helper.isClockHolder(player)) {
                    event.setCancelled(true);
                }
            }
            if (action == Action.RIGHT_CLICK_AIR || action == Action.RIGHT_CLICK_BLOCK) {
                if (itemstack.getType().equals(Material.CLOCK)) {
                    if (Helper.clockHolder == null) {
                        Helper.clockHolder = player.getDisplayName();
                        player.getWorld().getEntities().stream()
                                .filter(e -> !Helper.isClockHolder(e))
                                .forEach(Helper::freeze);
                        player.sendActionBar("時間が止まった！");
                    } else if (Helper.clockHolder == player.getDisplayName()) {
                        player.getWorld().getEntities().stream()
                                .filter(e -> !Helper.isClockHolder(e))
                                .forEach(Helper::release);
                        Helper.clockHolder = null;
                        player.sendActionBar("時間が動き出した！");
                    }
                } else if (itemstack.getType().equals(Material.TRIPWIRE_HOOK) && !player.isSneaking()) {
                    Snowball bullet = player.launchProjectile(Snowball.class);
                    player.getWorld().playSound(player.getLocation(), Sound.UI_BUTTON_CLICK, 0.7F, 1);

                } else if (Helper.isKun(player)) {
                    if (player.isSneaking() && (itemstack.getType().equals(Material.TRIPWIRE_HOOK) || itemstack.getType().equals(Material.STONE_SWORD))) {
                        Helper.throwItem(player);
                    } else {
                        Helper.switchBody(player);
                    }
                }

            } else if (action == Action.LEFT_CLICK_AIR || action == Action.LEFT_CLICK_BLOCK) {
                if (itemstack.getType().equals(Material.STONE_SWORD)) {
                    List<Snowball> snowballNearBy = player.getLocation().getNearbyEntitiesByType(Snowball.class, 2).stream().collect(Collectors.toList());
                    snowballNearBy.stream().
                            forEach(s -> {
                                Helper.destroyBullet(s, player);
                            });
                }
            }
        }
    }

    @EventHandler
    public void onHitEntity(ProjectileHitEvent event) {
        if(SuperHot.main) {
            if (event.getEntity() instanceof Snowball && event.getHitEntity() instanceof Player) {
                Player p = (Player) event.getHitEntity();
                p.setHealth(0);
                p.spawnParticle(Particle.BLOCK_CRACK, p.getLocation().getX(), p.getLocation().getY(), p.getLocation().getZ(), 20, Material.REDSTONE_BLOCK.createBlockData());
                event.getEntity().remove();
            }
        }
    }

    @EventHandler
    public void onPickUpItem(EntityPickupItemEvent event) {
        if(SuperHot.main) {
            if (!Helper.isKun(event.getEntity()) && event.getItem().getCustomName().equalsIgnoreCase("throw")) {
                event.setCancelled(true);
            }
        }
    }

    @EventHandler
    public void onJoin(PlayerJoinEvent e){
        Player player = e.getPlayer();
        player.getAttribute(Attribute.GENERIC_ATTACK_SPEED).removeModifier(ACCELERATION);
        player.getAttribute(Attribute.GENERIC_ATTACK_SPEED).removeModifier(DECELERATION);
        player.getAttribute(Attribute.GENERIC_MOVEMENT_SPEED).removeModifier(ACCELERATION);
        player.getAttribute(Attribute.GENERIC_MOVEMENT_SPEED).removeModifier(DECELERATION);
        player.setAI(true);
        player.setVelocity(new Vector());
    }

    @EventHandler
    public void onLogout(PlayerQuitEvent e){
        Player player = e.getPlayer();
        player.getAttribute(Attribute.GENERIC_ATTACK_SPEED).removeModifier(ACCELERATION);
        player.getAttribute(Attribute.GENERIC_ATTACK_SPEED).removeModifier(DECELERATION);
        player.getAttribute(Attribute.GENERIC_MOVEMENT_SPEED).removeModifier(ACCELERATION);
        player.getAttribute(Attribute.GENERIC_MOVEMENT_SPEED).removeModifier(DECELERATION);
        player.setAI(true);
        player.setVelocity(new Vector());
   }

}
