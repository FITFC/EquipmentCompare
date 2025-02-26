package com.anthonyhilyard.equipmentcompare.gui;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.anthonyhilyard.equipmentcompare.EquipmentCompare;
import com.anthonyhilyard.equipmentcompare.EquipmentCompareConfig;
import com.anthonyhilyard.equipmentcompare.Loader;
import com.anthonyhilyard.iceberg.events.RenderTooltipExtEvent;
import com.anthonyhilyard.iceberg.util.Tooltips;
import com.mojang.blaze3d.matrix.MatrixStack;

import org.apache.commons.lang3.exception.ExceptionUtils;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.renderer.IRenderTypeBuffer;
import net.minecraft.client.renderer.Rectangle2d;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.MobEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.EquipmentSlotType;
import net.minecraft.inventory.container.Slot;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.vector.Matrix4f;
import net.minecraft.util.text.Color;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.ITextProperties;
import net.minecraft.util.text.LanguageMap;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.util.text.Style;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraftforge.fml.client.gui.GuiUtils;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.ModList;

public class ComparisonTooltips
{
	private static void drawTooltip(MatrixStack matrixStack, ItemStack itemStack, Rectangle2d rect, List<ITextComponent> tooltipLines, FontRenderer font, Screen screen, int maxWidth, boolean showBadge, boolean centeredTitle, int index)
	{
		int bgColor = (int)EquipmentCompareConfig.INSTANCE.badgeBackgroundColor.get().longValue();
		int borderStartColor = (int)EquipmentCompareConfig.INSTANCE.badgeBorderStartColor.get().longValue();
		int borderEndColor = (int)EquipmentCompareConfig.INSTANCE.badgeBorderEndColor.get().longValue();
		
		Style textColor = Style.EMPTY.withColor(Color.fromRgb((int)EquipmentCompareConfig.INSTANCE.badgeTextColor.get().longValue()));
		ITextProperties equippedBadge;

		if (EquipmentCompareConfig.INSTANCE.overrideBadgeText.get())
		{
			equippedBadge = new StringTextComponent(EquipmentCompareConfig.INSTANCE.badgeText.get()).withStyle(textColor);
		}
		else
		{
			equippedBadge = new TranslationTextComponent("equipmentcompare.general.badgeText").withStyle(textColor);
		}

		boolean constrainToRect = false;

		if (showBadge)
		{
			if (rect.getY() + rect.getHeight() + 4 > screen.height)
			{
				rect = new Rectangle2d(rect.getX(), screen.height - rect.getHeight() - 4, rect.getWidth(), rect.getHeight());
			}

			matrixStack.pushPose();
			matrixStack.translate(0, 0, 401);
			IRenderTypeBuffer.Impl renderType = IRenderTypeBuffer.immediate(Tessellator.getInstance().getBuilder());
			Matrix4f matrix = matrixStack.last().pose();
			int badgeOffset = 0;

			// Draw the "equipped" badge.
			// If legendary tooltips is installed, AND this item needs a custom border display the badge lower and without a border.
			if (ModList.get().isLoaded("legendarytooltips"))
			{
				// Fire a color event to properly update the background color if needed.
				RenderTooltipExtEvent.Color colorEvent = new RenderTooltipExtEvent.Color(itemStack, tooltipLines, matrixStack, rect.getX(), rect.getY(), font, bgColor, borderStartColor, borderEndColor, showBadge, index);
				if (!MinecraftForge.EVENT_BUS.post(colorEvent))
				{
					bgColor = colorEvent.getBackgroundStart();
				}
				else
				{
					bgColor = GuiUtils.DEFAULT_BACKGROUND_COLOR;
				}
				
				constrainToRect = true;
				badgeOffset = 6;

				GuiUtils.drawGradientRect(matrix, -1, rect.getX() + 1,					 rect.getY() - 17 + badgeOffset, rect.getX() + rect.getWidth() + 7, rect.getY() - 16 + badgeOffset, bgColor, bgColor);
				GuiUtils.drawGradientRect(matrix, -1, rect.getX(),						 rect.getY() - 16 + badgeOffset, rect.getX() + 1, 					rect.getY() - 4 + badgeOffset,  bgColor, bgColor);
				GuiUtils.drawGradientRect(matrix, -1, rect.getX() + rect.getWidth() + 7, rect.getY() - 16 + badgeOffset, rect.getX() + rect.getWidth() + 8,	rect.getY() - 4 + badgeOffset,  bgColor, bgColor);
				GuiUtils.drawGradientRect(matrix, -1, rect.getX() + 1,					 rect.getY() - 16 + badgeOffset, rect.getX() + rect.getWidth() + 7, rect.getY() - 6 + badgeOffset,  bgColor, bgColor);
			}
			else
			{
				GuiUtils.drawGradientRect(matrix, -1, rect.getX() + 1,					 rect.getY() - 17 + badgeOffset, rect.getX() + rect.getWidth() + 7, rect.getY() - 16 + badgeOffset, bgColor, bgColor);
				GuiUtils.drawGradientRect(matrix, -1, rect.getX(),						 rect.getY() - 16 + badgeOffset, rect.getX() + 1, 					rect.getY() - 4 + badgeOffset,  bgColor, bgColor);
				GuiUtils.drawGradientRect(matrix, -1, rect.getX() + rect.getWidth() + 7, rect.getY() - 16 + badgeOffset, rect.getX() + rect.getWidth() + 8,	rect.getY() - 4 + badgeOffset,  bgColor, bgColor);
				GuiUtils.drawGradientRect(matrix, -1, rect.getX() + 1,					 rect.getY() - 4 + badgeOffset,  rect.getX() + rect.getWidth() + 7, rect.getY() - 3 + badgeOffset,  bgColor, bgColor);
				GuiUtils.drawGradientRect(matrix, -1, rect.getX() + 1,					 rect.getY() - 16 + badgeOffset, rect.getX() + rect.getWidth() + 7, rect.getY() - 4 + badgeOffset,  bgColor, bgColor);
				GuiUtils.drawGradientRect(matrix, -1, rect.getX() + 1,					 rect.getY() - 15 + badgeOffset, rect.getX() + 2, 					rect.getY() - 5 + badgeOffset,  borderStartColor, borderEndColor);
				GuiUtils.drawGradientRect(matrix, -1, rect.getX() + rect.getWidth() + 6, rect.getY() - 15 + badgeOffset, rect.getX() + rect.getWidth() + 7, rect.getY() - 5 + badgeOffset,  borderStartColor, borderEndColor);
				GuiUtils.drawGradientRect(matrix, -1, rect.getX() + 1,					 rect.getY() - 16 + badgeOffset, rect.getX() + rect.getWidth() + 7, rect.getY() - 15 + badgeOffset, borderStartColor, borderStartColor);
				GuiUtils.drawGradientRect(matrix, -1, rect.getX() + 1,					 rect.getY() - 5 + badgeOffset,  rect.getX() + rect.getWidth() + 7, rect.getY() - 4 + badgeOffset,  borderEndColor,   borderEndColor);
			}

			font.drawInBatch(LanguageMap.getInstance().getVisualOrder(equippedBadge), (float)rect.getX() + (rect.getWidth() - font.width(equippedBadge)) / 2 + 4, (float)rect.getY() - 14 + badgeOffset, -1, true, matrixStack.last().pose(), renderType, false, 0x000000, 0xF000F0);
			renderType.endBatch();
			matrixStack.popPose();
		}

		Tooltips.renderItemTooltip(itemStack, matrixStack, new Tooltips.TooltipInfo(tooltipLines, font), rect, screen.width, screen.height, GuiUtils.DEFAULT_BACKGROUND_COLOR, GuiUtils.DEFAULT_BACKGROUND_COLOR, GuiUtils.DEFAULT_BORDER_COLOR_START, GuiUtils.DEFAULT_BORDER_COLOR_END, showBadge, constrainToRect, centeredTitle, index);
	}

	public static boolean render(MatrixStack matrixStack, int x, int y, Slot hoveredSlot, Minecraft minecraft, FontRenderer font, Screen screen)
	{
		ItemStack itemStack = hoveredSlot != null ? hoveredSlot.getItem() : ItemStack.EMPTY;
		return render(matrixStack, x, y, itemStack, minecraft, font, screen);
	}
	
	@SuppressWarnings("unchecked")
	public static boolean render(MatrixStack matrixStack, int x, int y, ItemStack itemStack, Minecraft minecraft, FontRenderer font, Screen screen)
	{
		// The screen must be valid to render tooltips.
		if (screen == null)
		{
			return false;
		}

		if (minecraft.player.inventory.getCarried().isEmpty() && !itemStack.isEmpty() && !EquipmentCompareConfig.INSTANCE.blacklist.get().contains(itemStack.getItem().getRegistryName().toString()))
		{
			// If this is a piece of equipment and we are already wearing the same type, display an additional tooltip as well.
			EquipmentSlotType slot = MobEntity.getEquipmentSlotForItem(itemStack);

			List<ItemStack> equippedItems = new ArrayList<ItemStack>();
			ItemStack equippedItem = minecraft.player.getItemBySlot(slot);
		
			boolean checkItem = true;

			// For held items, only check items with durability.
			if (slot == EquipmentSlotType.MAINHAND)
			{
				// Ensure both items are comparable.
				// Any item with durability can be compared.
				if (!itemStack.getItem().canBeDepleted() || !equippedItem.getItem().canBeDepleted())
				{
					checkItem = false;
				}
				// If strict comparisons are enabled, only compare items of the same type.
				else if (EquipmentCompareConfig.INSTANCE.strict.get())
				{
					if (!itemStack.getItem().getClass().equals(equippedItem.getItem().getClass()))
					{
						checkItem = false;
					}
				}
			}

			if (checkItem)
			{
				equippedItems.add(equippedItem);
				equippedItems.remove(ItemStack.EMPTY);
				equippedItems.remove(itemStack);
			}

			// If Curios is installed, check for equipped curios to compare as well.
			if (ModList.get().isLoaded("curios"))
			{
				try
				{
					equippedItems.addAll((List<ItemStack>) Class.forName("com.anthonyhilyard.equipmentcompare.compat.CuriosHandler").getMethod("getCuriosMatchingSlot", LivingEntity.class, ItemStack.class).invoke(null, minecraft.player, itemStack));
				}
				catch (Exception e)
				{
					Loader.LOGGER.error(ExceptionUtils.getStackTrace(e));
				}
			}

			// If Baubles is installed, check for equipped baubles to compare as well.
			if (ModList.get().isLoaded("baubles"))
			{
				try
				{
					equippedItems.addAll((List<ItemStack>) Class.forName("com.anthonyhilyard.equipmentcompare.compat.BaublesHandler").getMethod("getBaublesMatchingSlot", PlayerEntity.class, ItemStack.class).invoke(null, minecraft.player, itemStack));
				}
				catch (Exception e)
				{
					Loader.LOGGER.error(ExceptionUtils.getStackTrace(e));
				}
			}

			// Filter blacklisted items.
			equippedItems.removeIf(stack -> EquipmentCompareConfig.INSTANCE.blacklist.get().contains(stack.getItem().getRegistryName().toString()));

			// Make sure we don't compare an item to itself (can happen with Curios slots).
			equippedItems.remove(itemStack);

			if (!equippedItems.isEmpty() && (EquipmentCompare.tooltipActive ^ EquipmentCompareConfig.INSTANCE.defaultOn.get()))
			{
				int maxWidth = ((screen.width - (equippedItems.size() * 16)) / (equippedItems.size() + 1));
				FontRenderer itemFont = itemStack.getItem().getFontRenderer(itemStack);
				if (itemFont == null)
				{
					itemFont = font;
				}

				boolean centeredTitle = false, enforceMinimumWidth = false;

				// If Legendary Tooltips is loaded, check if we need to center the title or enforce a minimum width.
				if (ModList.get().isLoaded("legendarytooltips"))
				{
					try
					{
						centeredTitle = (boolean)Class.forName("com.anthonyhilyard.equipmentcompare.compat.LegendaryTooltipsHandler").getMethod("getCenteredTitle").invoke(null, new Object[]{});
						enforceMinimumWidth = (boolean)Class.forName("com.anthonyhilyard.equipmentcompare.compat.LegendaryTooltipsHandler").getMethod("getEnforceMinimumWidth").invoke(null, new Object[]{});
					}
					catch (Exception e)
					{
						Loader.LOGGER.error(ExceptionUtils.getStackTrace(e));
					}
				}

				List<ITextComponent> itemStackTooltipLines = screen.getTooltipFromItem(itemStack);
				Rectangle2d itemStackRect = Tooltips.calculateRect(itemStack, matrixStack, itemStackTooltipLines, x, y, screen.width, screen.height, maxWidth, itemFont, enforceMinimumWidth ? 48 : 0, centeredTitle);
				if (x + itemStackRect.getWidth() + 12 > screen.width)
				{
					itemStackRect = new Rectangle2d(screen.width - itemStackRect.getWidth() - 24, itemStackRect.getY(), itemStackRect.getWidth(), itemStackRect.getHeight());
				}
				else
				{
					itemStackRect = new Rectangle2d(itemStackRect.getX() - 2, itemStackRect.getY(), itemStackRect.getWidth(), itemStackRect.getHeight());
				}

				Map<ItemStack, Rectangle2d> tooltipRects = new HashMap<ItemStack, Rectangle2d>();
				Map<ItemStack, List<ITextComponent>> tooltipLines = new HashMap<ItemStack, List<ITextComponent>>();

				Rectangle2d previousRect = itemStackRect;
				boolean firstRect = true;

				// Keep track of the tooltip index.
				int tooltipIndex = 1;

				// Set up tooltip rects.
				for (ItemStack thisItem : equippedItems)
				{
					if (thisItem.getItem().getFontRenderer(thisItem) != null)
					{
						itemFont = thisItem.getItem().getFontRenderer(thisItem);
					}

					List<ITextComponent> equippedTooltipLines = screen.getTooltipFromItem(thisItem);
					Rectangle2d equippedRect = Tooltips.calculateRect(itemStack, matrixStack, equippedTooltipLines, x - previousRect.getWidth() - 14, y, screen.width, screen.height, maxWidth, itemFont, enforceMinimumWidth ? 48 : 0, centeredTitle);
					ITextProperties equippedBadge;

					if (EquipmentCompareConfig.INSTANCE.overrideBadgeText.get())
					{
						equippedBadge = new StringTextComponent(EquipmentCompareConfig.INSTANCE.badgeText.get());
					}
					else
					{
						equippedBadge = new TranslationTextComponent("equipmentcompare.general.badgeText");
					}
					
					// Fix equippedRect x coordinate.
					int tooltipWidth = equippedRect.getWidth();
					equippedRect = new Rectangle2d(equippedRect.getX(), equippedRect.getY(), Math.max(equippedRect.getWidth(), itemFont.width(equippedBadge) + 8), equippedRect.getHeight());

					if (firstRect)
					{
						equippedRect = new Rectangle2d(previousRect.getX() - equippedRect.getWidth() - 16 - (equippedRect.getWidth() - tooltipWidth) / 2, equippedRect.getY(), equippedRect.getWidth(), equippedRect.getHeight());
						firstRect = false;
					}
					else
					{
						equippedRect = new Rectangle2d(previousRect.getX() - equippedRect.getWidth() - 4 - (equippedRect.getWidth() - tooltipWidth) / 2, equippedRect.getY(), equippedRect.getWidth(), equippedRect.getHeight());
					}

					tooltipRects.put(thisItem, equippedRect);
					tooltipLines.put(thisItem, equippedTooltipLines);
					previousRect = equippedRect;
				}

				// Fix rects to fit onscreen, if possible.
				// If the last rect (which is the left-most one) is off the screen, move all the rects over.
				int xOffset = -tooltipRects.get(equippedItems.get(equippedItems.size() - 1)).getX();
				if (xOffset > 0)
				{
					// Move the equipped rects.
					for (ItemStack thisItem : equippedItems)
					{
						Rectangle2d equippedRect = tooltipRects.get(thisItem);
						tooltipRects.replace(thisItem, new Rectangle2d(equippedRect.getX() + xOffset, equippedRect.getY(), equippedRect.getWidth(), equippedRect.getHeight()));
					}

					// Move the hovered item rect.
					itemStackRect = new Rectangle2d(itemStackRect.getX() + xOffset, itemStackRect.getY(), itemStackRect.getWidth(), itemStackRect.getHeight());
				}

				tooltipIndex = 1;

				// Now draw them all.
				for (ItemStack thisItem : equippedItems)
				{
					drawTooltip(matrixStack, thisItem, tooltipRects.get(thisItem), tooltipLines.get(thisItem), font, screen, maxWidth, true, centeredTitle, tooltipIndex++);
				}
				drawTooltip(matrixStack, itemStack, itemStackRect, itemStackTooltipLines, font, screen, maxWidth, false, centeredTitle, 0);

				return true;
			}
			// Otherwise display the tooltip normally.
			else
			{
				return false;
			}
		}
		return false;
	}
}
