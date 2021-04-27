package net.kunmc.lab.superhot.helper;

import net.kunmc.lab.superhot.SuperHot;
import net.kunmc.lab.superhot.config.Config;
import net.kunmc.lab.superhot.event.ConstantEvent;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Particle;
import org.bukkit.attribute.Attribute;
import org.bukkit.attribute.AttributeModifier;
import org.bukkit.block.Block;
import org.bukkit.entity.*;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.Vector;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

public class Helper {
    private static SuperHot plugin = SuperHot.getPlugin();
    private static double lastKunXPos = 0;
    private static double lastKunZPos = 0;
    private static Map<UUID, Vector> projectileVelocity = new HashMap<>();
    private static UUID ACCELERATION_ID = UUID.randomUUID();
    private static UUID DECELERATION_ID = UUID.randomUUID();
    private static AttributeModifier ACCELERATION = new AttributeModifier(ACCELERATION_ID, "Accelerate entity", 0.5D, AttributeModifier.Operation.MULTIPLY_SCALAR_1);
    private static AttributeModifier DECELERATION = new AttributeModifier(DECELERATION_ID, "Decelerate entity", -0.5D, AttributeModifier.Operation.MULTIPLY_SCALAR_1);
    public static String clockHolder;

    public Helper(SuperHot plugin) {
        this.plugin = plugin;
    }

    public static void freeze(Entity entity) {
        if(SuperHot.main) {
            entity.setGravity(false);
            if (entity instanceof LivingEntity) {
                LivingEntity living = (LivingEntity) entity;
                living.getAttribute(Attribute.GENERIC_MOVEMENT_SPEED).removeModifier(ACCELERATION);
                living.getAttribute(Attribute.GENERIC_MOVEMENT_SPEED).removeModifier(DECELERATION);
                living.setAI(false);
                living.setVelocity(new Vector(0, 0, 0));
                if (living instanceof Player) {
                    Player player = (Player) living;
                    player.getAttribute(Attribute.GENERIC_ATTACK_SPEED).removeModifier(ACCELERATION);
                    player.getAttribute(Attribute.GENERIC_ATTACK_SPEED).removeModifier(DECELERATION);
                }
            } else if (entity instanceof Projectile) {
                Projectile projectile = (Projectile) entity;
                projectileVelocity.put(projectile.getUniqueId(), projectile.getVelocity());
                projectile.setVelocity(new Vector().zero());
            } else if (entity instanceof Item) {
                Item item = (Item) entity;
                projectileVelocity.put((item.getUniqueId()), item.getVelocity());
                item.setVelocity(new Vector().zero());
            }
        }
    }

    public static void accelerate(Entity entity) {
        if(SuperHot.main) {
            entity.setGravity(true);
            if (entity instanceof LivingEntity) {
                LivingEntity living = (LivingEntity) entity;
                living.getAttribute(Attribute.GENERIC_MOVEMENT_SPEED).removeModifier(ACCELERATION);
                living.getAttribute(Attribute.GENERIC_MOVEMENT_SPEED).removeModifier(DECELERATION);
                living.getAttribute(Attribute.GENERIC_MOVEMENT_SPEED).addModifier(ACCELERATION);
                living.setAI(true);
                living.setVelocity(new Vector());
                if (living instanceof Player) {
                    Player player = (Player) living;
                    player.getAttribute(Attribute.GENERIC_ATTACK_SPEED).removeModifier(ACCELERATION);
                    player.getAttribute(Attribute.GENERIC_ATTACK_SPEED).removeModifier(DECELERATION);
                    player.getAttribute(Attribute.GENERIC_ATTACK_SPEED).addModifier(ACCELERATION);
                }
            } else if (entity instanceof Projectile) {
                Projectile projectile = (Projectile) entity;
                if (projectileVelocity.containsKey(projectile.getUniqueId())) {
                    projectile.setVelocity(projectileVelocity.get(projectile.getUniqueId()).multiply(1.5));
                }
            } else if (entity instanceof Item) {
                Item item = (Item) entity;
                if (projectileVelocity.containsKey(item.getUniqueId())) {
                    item.setVelocity(projectileVelocity.get(item.getUniqueId()).multiply(1.5));
                }
            }
        }
    }

    public static void decelerate(Entity entity) {
        if(SuperHot.main) {
            entity.setGravity(true);
            if (entity instanceof LivingEntity) {
                LivingEntity living = (LivingEntity) entity;
                living.getAttribute(Attribute.GENERIC_MOVEMENT_SPEED).removeModifier(ACCELERATION);
                living.getAttribute(Attribute.GENERIC_MOVEMENT_SPEED).removeModifier(DECELERATION);
                living.getAttribute(Attribute.GENERIC_MOVEMENT_SPEED).addModifier(DECELERATION);
                living.setAI(true);
                living.setVelocity(new Vector());
                if (living instanceof Player) {
                    Player player = (Player) living;
                    player.getAttribute(Attribute.GENERIC_ATTACK_SPEED).removeModifier(ACCELERATION);
                    player.getAttribute(Attribute.GENERIC_ATTACK_SPEED).removeModifier(DECELERATION);
                    player.getAttribute(Attribute.GENERIC_ATTACK_SPEED).addModifier(DECELERATION);
                }
            } else if (entity instanceof Projectile) {
                Projectile projectile = (Projectile) entity;
                if (projectileVelocity.containsKey(projectile.getUniqueId())) {
                    projectile.setVelocity(projectileVelocity.get(projectile.getUniqueId()).multiply(0.5));
                }
            } else if (entity instanceof Item) {
                Item item = (Item) entity;
                if (projectileVelocity.containsKey(item.getUniqueId())) {
                    item.setVelocity(projectileVelocity.get(item.getUniqueId()).multiply(0.5));
                }
            }
        }
    }

    public static void release(Entity entity) {
        if(SuperHot.main) {
            entity.setGravity(true);
            if (entity instanceof LivingEntity) {
                LivingEntity living = (LivingEntity) entity;
                living.getAttribute(Attribute.GENERIC_MOVEMENT_SPEED).removeModifier(ACCELERATION);
                living.getAttribute(Attribute.GENERIC_MOVEMENT_SPEED).removeModifier(DECELERATION);
                living.setAI(true);
                living.setVelocity(new Vector());
                if (living instanceof Player) {
                    Player player = (Player) living;
                    player.getAttribute(Attribute.GENERIC_ATTACK_SPEED).removeModifier(ACCELERATION);
                    player.getAttribute(Attribute.GENERIC_ATTACK_SPEED).removeModifier(DECELERATION);
                }
            } else if (entity instanceof Projectile) {
                Projectile projectile = (Projectile) entity;
                if (projectileVelocity.containsKey(projectile.getUniqueId())) {
                    projectile.setVelocity(projectileVelocity.get(projectile.getUniqueId()));
                }
            } else if (entity instanceof Item) {
                Item item = (Item) entity;
                if (projectileVelocity.containsKey(item.getUniqueId())) {
                    item.setVelocity(projectileVelocity.get(item.getUniqueId()));
                }
            }
        }
    }

    public static void destroyBullet(Snowball snowball, Player player) {
        if(SuperHot.main) {
            snowball.remove();
            player.spawnParticle(Particle.BLOCK_CRACK, snowball.getLocation().getX(), snowball.getLocation().getY(), snowball.getLocation().getZ(), 20, Material.BLACK_CONCRETE.createBlockData());
        }
    }

    public static void switchBody(Player player) {
        if(SuperHot.main) {
            List<Block> sightBlocks = player.getLineOfSight(null, 32);
            sightBlocks.stream().
                    forEach(b -> {
                        Player target = b.getLocation().getNearbyEntitiesByType(Player.class, 0.5).stream()
                                .findFirst().orElse(null);
                        if (target != null) {
                            Location kunLoc = player.getLocation();
                            Inventory kunInv = player.getInventory();
                            Location targetLoc = target.getLocation();
                            Inventory targetInv = target.getInventory();
                            player.teleport(targetLoc);
                            player.getInventory().setContents(targetInv.getContents());
                            player.updateInventory();
                            target.teleport(kunLoc);
                            target.getInventory().setContents(kunInv.getContents());
                            target.updateInventory();
                        }
                    });
        }
    }

    public static void throwItem(Player player) {
        if(SuperHot.main) {
            ItemStack item = player.getInventory().getItemInMainHand();
            Item itemThrow = player.getWorld().dropItem(new Location(player.getWorld(), player.getLocation().getX(), player.getLocation().getY() + 1, player.getLocation().getZ()), item);
            itemThrow.setVelocity(player.getEyeLocation().getDirection().multiply(1.3F));
            itemThrow.setCustomName("throw");
            itemThrow.setCustomNameVisible(false);
            player.getInventory().setItemInMainHand(new ItemStack(Material.AIR));
            new BukkitRunnable() {
                @Override
                public void run() {
                    if (ConstantEvent.kunMovementState == ConstantEvent.KunMovementState.Stopping || Helper.clockHolder != null) {
                        Helper.freeze(itemThrow);
                    }
                }
            }.runTaskLater(plugin, 4);
        }
    }

    public static boolean isKun(Entity entity) {
        if (!Config.superHotEnabled) return false;
        return entity.getName().equalsIgnoreCase(Config.timeFreezer);
    }

    public static boolean isKunMoving(Player kun) {
        Double currentXPos = kun.getLocation().getX();
        Double currentZPos = kun.getLocation().getZ();
        int diffX = currentXPos.compareTo(lastKunXPos);
        int diffZ = currentZPos.compareTo(lastKunZPos);
        lastKunXPos = currentXPos;
        lastKunZPos = currentZPos;
        return diffX != 0 || diffZ != 0;
    }

    public static boolean isClockHolder(Entity entity) {
        return entity.getName().equalsIgnoreCase(clockHolder);
    }
}
