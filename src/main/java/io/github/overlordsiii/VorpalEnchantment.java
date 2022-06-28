package io.github.overlordsiii;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.mojang.brigadier.StringReader;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import io.github.overlordsiii.config.Chances;
import io.github.overlordsiii.mixin.HorseEntityInvoker;
import org.apache.commons.io.IOUtils;

import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.EnchantmentTarget;
import net.minecraft.enchantment.Enchantments;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.boss.WitherEntity;
import net.minecraft.entity.boss.dragon.EnderDragonEntity;
import net.minecraft.entity.mob.CreeperEntity;
import net.minecraft.entity.mob.SkeletonEntity;
import net.minecraft.entity.mob.WitherSkeletonEntity;
import net.minecraft.entity.mob.ZombieEntity;
import net.minecraft.entity.mob.ZombieVillagerEntity;
import net.minecraft.entity.passive.AxolotlEntity;
import net.minecraft.entity.passive.BeeEntity;
import net.minecraft.entity.passive.CatEntity;
import net.minecraft.entity.passive.FoxEntity;
import net.minecraft.entity.passive.FrogEntity;
import net.minecraft.entity.passive.GoatEntity;
import net.minecraft.entity.passive.HorseEntity;
import net.minecraft.entity.passive.LlamaEntity;
import net.minecraft.entity.passive.MooshroomEntity;
import net.minecraft.entity.passive.PandaEntity;
import net.minecraft.entity.passive.ParrotEntity;
import net.minecraft.entity.passive.RabbitEntity;
import net.minecraft.entity.passive.SheepEntity;
import net.minecraft.entity.passive.StriderEntity;
import net.minecraft.entity.passive.VillagerEntity;
import net.minecraft.entity.passive.WolfEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.AxeItem;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtString;
import net.minecraft.nbt.StringNbtReader;
import net.minecraft.util.DyeColor;
import net.minecraft.util.Identifier;
import net.minecraft.util.Util;
import net.minecraft.util.registry.Registry;

import net.fabricmc.loader.api.FabricLoader;

// Warning: This code is bad/ugly
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

	private boolean triggered = false;

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
	 * @param other enchantment to see it can be applied
	 */
	@Override
	protected boolean canAccept(Enchantment other) {
		if (VorpalEnchantmentMod.CONFIG.worksWithLooting) {
			return super.canAccept(other);
		}
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
	// on target damaged runs twice, so we do a little workaround to fix that
	public void onTargetDamaged(LivingEntity user, Entity target, int level) {
		triggered = !triggered;
		if (!triggered) {
			return;
		}
		if (!target.isAlive()) {
			if (calculateChances(level)) {
				if (entityToItem.containsKey(target.getClass())) {
					Item item = entityToItem.get(target.getClass());
					ItemStack stack = new ItemStack(item);

					if (item.equals(Items.PLAYER_HEAD) && target instanceof PlayerEntity playerTarget) {
						stack.getOrCreateNbt().put("SkullOwner", NbtString.of(playerTarget.getName().getString()));
					}

					target.dropStack(stack);
				} else {
					//really ugly stuff starts here
					String id = Registry.ENTITY_TYPE.getId(target.getType()).toString();
					JsonObject mobheadsObject;
					try {
						mobheadsObject = JsonParser.parseString(IOUtils.toString(Objects.requireNonNull(VorpalEnchantment.class.getResourceAsStream("/assets/vorpal-enchantment/mobheads.json")), StandardCharsets.UTF_8)).getAsJsonObject();
					} catch (IOException e) {
						throw new RuntimeException(e);
					}

					if (target instanceof VillagerEntity villagerEntity) {
						id = modifyVillagerKey(villagerEntity);
					} else if (target instanceof ZombieVillagerEntity zombieVillagerEntity) {
						id = modifyZombieVillagerKey(zombieVillagerEntity);
					} else if (target instanceof GoatEntity goatEntity) {
						id = modifyGoatKey(goatEntity);
					} else if (target instanceof SheepEntity sheepEntity) {
						id = modifySheepKey(sheepEntity);
					} else if (target instanceof CatEntity catEntity) {
						id = modifyCatKey(catEntity);
					} else if (target instanceof FoxEntity foxEntity) {
						id = modifyFoxKey(foxEntity);
					} else if (target instanceof WitherEntity witherEntity) {
						id = modifyWitherKey(witherEntity);
					} else if (target instanceof StriderEntity striderEntity) {
						id = modifyStriderKey(striderEntity);
					} else if (target instanceof AxolotlEntity axolotlEntity) {
						id = modifyAxolotlKey(axolotlEntity);
					} else if (target instanceof MooshroomEntity mooshroomEntity) {
						id = modifyMooshroomKey(mooshroomEntity);
					} else if (target instanceof PandaEntity pandaEntity) {
						id = modifyPandaKey(pandaEntity);
					} else if (target instanceof RabbitEntity rabbitEntity) {
						id = modifyRabbitKey(rabbitEntity);
					} else if (target instanceof LlamaEntity llamaEntity) {
						id = modifyLlamaKey(llamaEntity);
					} else if (target instanceof BeeEntity beeEntity) {
						id = modifyBeeKey(beeEntity);
					} else if (target instanceof WolfEntity wolfEntity) {
						id = modifyWolfKey(wolfEntity);
					} else if (target instanceof FrogEntity frogEntity) {
						id = modifyFrogKey(frogEntity);
					} else if (target instanceof HorseEntity horseEntity) {
						id = modifyHorseKey(horseEntity);
					} else if (target instanceof ParrotEntity parrotEntity) {
						id = modifyParrotKey(parrotEntity);
					}

					if (mobheadsObject.has(id)) {
						NbtCompound compound;
						try {
							compound = new StringNbtReader(new StringReader(mobheadsObject.get(id).getAsString())).parseCompound();
						} catch (CommandSyntaxException e) {
							throw new RuntimeException(e);
						}

						NbtCompound skullOwnerCompound = compound.getCompound("SkullOwner");

						ItemStack playerHead = Items.PLAYER_HEAD.getDefaultStack();
						playerHead.getOrCreateNbt().put("SkullOwner", skullOwnerCompound);

						target.dropStack(playerHead);
					}
				}
			}
		}
	}

	private static String modifyParrotKey(ParrotEntity parrotEntity) {
		return "minecraft:parrot" + parrotEntity.getVariant();
	}

	private static String modifyHorseKey(HorseEntity horseEntity) {
		return "minecraft:horse" + ((HorseEntityInvoker) horseEntity).callGetVariant();
	}

	private static String modifyFrogKey(FrogEntity frogEntity) {
		Identifier id = Registry.FROG_VARIANT.getId(frogEntity.getVariant());
		String frogType = Objects.requireNonNull(id).toString().substring(id.toString().indexOf(":" + 1));

		return "minecraft:frog_" + frogType;
	}

	private static String modifyWolfKey(WolfEntity wolfEntity) {
		if (wolfEntity.getAngerTime() > 0) {
			return "minecraft:angry_wolf";
		}

		return "minecraft:wolf";
	}

	private static String modifyBeeKey(BeeEntity beeEntity) {
		int angerTime = beeEntity.getAngerTime();
		boolean nectar = beeEntity.hasNectar();
		if (angerTime == 0) {
			return "minecraft:bee_0_" + nectar;
		}

		return "minecraft:bee_" + nectar;
	}

	private static String modifyLlamaKey(LlamaEntity llamaEntity) {
		return "minecraft:llama" + llamaEntity.getVariant();
	}

	private static String modifyRabbitKey(RabbitEntity rabbitEntity) {
		if (rabbitEntity.hasCustomName()) {
			if (rabbitEntity.getCustomName().getString().equals("Toast")) {
				return "minecraft:rabbit_toast";
			}
		}

		return "minecraft:rabbit" + rabbitEntity.getRabbitType();
	}

	private static String modifyPandaKey(PandaEntity pandaEntity) {
		PandaEntity.Gene gene = pandaEntity.getMainGene();
		if (gene == PandaEntity.Gene.NORMAL) {
			return "minecraft:panda";
		}

		return "minecraft:panda_" + gene.getName();
	}

	private static String modifyMooshroomKey(MooshroomEntity mooshroomEntity) {
		return "minecraft:mooshroom_" + mooshroomEntity.getMooshroomType().name().toLowerCase(Locale.ROOT);
	}

	private static String modifyAxolotlKey(AxolotlEntity axolotlEntity) {
		return "minecraft:axolotl" + axolotlEntity.getVariant().getId();
	}

	private static String modifyStriderKey(StriderEntity striderEntity) {
		if (striderEntity.isCold()) {
			return "minecraft:freezing_strider";
		}

		return "minecraft:strider";
	}

	private static String modifyWitherKey(WitherEntity witherEntity) {
		if (witherEntity.getInvulnerableTimer() > 0) {
			return "minecraft:invulnerable_wither";
		}

		return "minecraft:wither";
	}

	private static String modifyFoxKey(FoxEntity foxEntity) {
		int id = foxEntity.getFoxType().getId();
		return "minecraft:fox" + id;
	}

	private static String modifyCatKey(CatEntity catEntity) {
		Identifier id = Registry.CAT_VARIANT.getId(catEntity.getVariant());
		String catType = Objects.requireNonNull(id).toString().substring(id.toString().indexOf(":" + 1));

		return "minecraft:cat_" + catType;
	}

	private static String modifyVillagerKey(VillagerEntity entity) {
		return Registry.VILLAGER_PROFESSION.getId(entity.getVillagerData().getProfession()).toString();
	}

	private static String modifyZombieVillagerKey(ZombieVillagerEntity entity) {
		return "minecraft:zombie_villager_" + entity.getVillagerData().getProfession().id();
	}

	private static String modifyGoatKey(GoatEntity entity) {
		boolean screaming = entity.isScreaming();
		return "minecraft:goat" + screaming;
	}

	private static String modifySheepKey(SheepEntity entity) {
		if (entity.getCustomName() != null) {
			if (entity.getCustomName().getString().equals("jeb_")) {
				return "minecraft:sheep_jeb_sheep";
			}
		}

		DyeColor color = entity.getColor();
		return "minecraft:sheep_" + color.getName();
	}


	private static boolean calculateChances(int lvl) {
		double calc = Math.random();
		Chances chance = VorpalEnchantmentMod.CONFIG.chances;
		double chancePercentage = (lvl == 1 ? chance.getLv1Chance() : (lvl == 2 ? chance.getLv2Chance() : chance.getLv3Chance()));
		return calc < chancePercentage;
	}



}
