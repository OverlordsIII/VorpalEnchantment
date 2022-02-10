package io.github.overlordsiii;

import java.util.HashMap;
import java.util.Map;

import io.github.overlordsiii.config.Chances;

import net.minecraft.block.Blocks;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.EnchantmentTarget;
import net.minecraft.enchantment.Enchantments;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.boss.dragon.EnderDragonEntity;
import net.minecraft.entity.mob.CreeperEntity;
import net.minecraft.entity.mob.SkeletonEntity;
import net.minecraft.entity.mob.WitherSkeletonEntity;
import net.minecraft.entity.mob.ZombieEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.AxeItem;
import net.minecraft.item.Item;
import net.minecraft.item.ItemGroup;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.item.SkullItem;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtHelper;
import net.minecraft.util.Rarity;
import net.minecraft.util.Util;

public class VorpalEnchantment extends Enchantment {

	// Mojank hardcodes heads
	private static Map<Class<? extends LivingEntity>, Item> entityToItem = Util.make(new HashMap<>(), (map) -> {
		map.put(ZombieEntity.class, Items.ZOMBIE_HEAD);
		map.put(CreeperEntity.class, Items.CREEPER_HEAD);
		map.put(EnderDragonEntity.class, Items.DRAGON_HEAD);
		map.put(SkeletonEntity.class, Items.SKELETON_SKULL);
		map.put(WitherSkeletonEntity.class, Items.WITHER_SKELETON_SKULL);
		map.put(PlayerEntity.class, Items.PLAYER_HEAD);
	});

	protected VorpalEnchantment(Rarity weight, EquipmentSlot[] slotTypes) {
		super(weight, EnchantmentTarget.WEAPON, slotTypes);
	}

	@Override
	public int getMaxLevel() {
		return 3;
	}



	/**
	 * {@return whether this enchantment can exist on an item stack with the
	 * {@code other} enchantment}
	 *
	 * @param other
	 */
	@Override
	protected boolean canAccept(Enchantment other) {
		return super.canAccept(other) && !other.equals(Enchantments.FORTUNE) && !other.equals(Enchantments.LOOTING) && !other.equals(Enchantments.SILK_TOUCH);
	}

	@Override
	public boolean isAcceptableItem(ItemStack stack) {
		if (VorpalEnchantmentMod.CONFIG.axeWielding) {
			return stack.getItem() instanceof AxeItem || super.isAcceptableItem(stack);
		}
		return super.isAcceptableItem(stack);
	}

	@Override
	public void onTargetDamaged(LivingEntity user, Entity target, int level) {
		if (!target.isAlive()) {
			if (calculateChances(level)) {
				if (entityToItem.containsKey(target.getClass())) {

					target.dropItem(entityToItem.get(target.getClass()));
				}
			}
		}
	}

	private boolean calculateChances(int lvl) {
		double calc = Math.random();
		Chances chance = VorpalEnchantmentMod.CONFIG.chances;
		double chancePercentage = (lvl == 1 ? chance.getLv1Chance() : (lvl == 2 ? chance.getLv2Chance() : chance.getLv3Chance()));
		return calc < chancePercentage;
	}

}
