package com.anthonyhilyard.equipmentcompare;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import com.electronwill.nightconfig.core.Config;

import org.apache.commons.lang3.tuple.Pair;

import net.minecraft.util.ResourceLocation;
import net.minecraftforge.common.ForgeConfigSpec;
import net.minecraftforge.common.ForgeConfigSpec.BooleanValue;
import net.minecraftforge.common.ForgeConfigSpec.ConfigValue;
import net.minecraftforge.common.ForgeConfigSpec.LongValue;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.config.ModConfig;

public class EquipmentCompareConfig
{
	public static final ForgeConfigSpec SPEC;
	public static final EquipmentCompareConfig INSTANCE;

	public final BooleanValue defaultOn;
	public final BooleanValue strict;
	public final LongValue badgeBackgroundColor;
	public final LongValue badgeBorderStartColor;
	public final LongValue badgeBorderEndColor;
	public final BooleanValue overrideBadgeText;
	public final ConfigValue<String> badgeText;
	public final LongValue badgeTextColor;
	public final ConfigValue<List<? extends String>> blacklist;

	static
	{
		Config.setInsertionOrderPreserved(true);
		Pair<EquipmentCompareConfig, ForgeConfigSpec> specPair = new ForgeConfigSpec.Builder().configure(EquipmentCompareConfig::new);
		SPEC = specPair.getRight();
		INSTANCE = specPair.getLeft();
	}

	public EquipmentCompareConfig(ForgeConfigSpec.Builder build)
	{
		build.comment("Client Configuration").push("client").push("visual_options");

		overrideBadgeText = build.comment(" If badge_text should override the built-in translatable text.").define("override_badge_text", false);
		badgeText = build.comment(" The text shown on the badge above equipped tooltips.").define("badge_text", "Equipped");
		badgeTextColor = build.comment(" The color of the text shown on the badge above equipped tooltips.").defineInRange("badge_text_color", 0xFFFFFFFFL, 0x00000000L, 0xFFFFFFFFL);
		badgeBackgroundColor = build.comment(" The background color of the \"equipped\" badge.").defineInRange("badge_bg", 0xF0101000L, 0x00000000L, 0xFFFFFFFFL);
		badgeBorderStartColor = build.comment(" The start border color of the \"equipped\" badge.").defineInRange("badge_border_start", 0xD0AA9113L, 0x00000000L, 0xFFFFFFFFL);
		badgeBorderEndColor = build.comment(" The end border color of the \"equipped\" badge.").defineInRange("badge_border_end", 0x60C2850AL, 0x00000000L, 0xFFFFFFFFL);

		build.pop().push("control_options");

		defaultOn = build.comment(" If the comparison tooltip should show by default (pressing bound key hides).").define("default_on", false);
		strict = build.comment(" If tool comparisons should compare only the same types of tools (can't compare a sword to an axe, for example).").define("strict", false);
		blacklist = build.comment(" Blacklist of items to show comparisons for.  Add item IDs to prevent them from being compared when hovered over or equipped.").defineListAllowEmpty(Arrays.asList("blacklist"), () -> new ArrayList<String>(), e -> ResourceLocation.isValidResourceLocation((String)e) );

		build.pop().pop();
	}

	@SubscribeEvent
	public static void onLoad(ModConfig.Loading e)
	{
	}

}