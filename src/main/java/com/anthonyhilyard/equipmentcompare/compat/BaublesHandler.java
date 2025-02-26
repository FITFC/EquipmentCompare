package com.anthonyhilyard.equipmentcompare.compat;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import lazy.baubles.api.BaublesAPI;
import lazy.baubles.api.bauble.IBauble;
import lazy.baubles.api.cap.IBaublesItemHandler;
import lazy.baubles.api.cap.BaublesCapabilities;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraftforge.common.util.LazyOptional;

public class BaublesHandler
{
	public static List<ItemStack> getBaublesMatchingSlot(PlayerEntity player, ItemStack itemStack)
	{
		List<ItemStack> result = new ArrayList<ItemStack>();

		// If this item isn't a bauble, return now.
		if (!itemStack.getCapability(BaublesCapabilities.ITEM_BAUBLE).isPresent())
		{
			return result;
		}

		// Get all the valid slots for the input bauble.
		IBauble bauble = itemStack.getCapability(BaublesCapabilities.ITEM_BAUBLE).resolve().get();
		Set<Integer> baubleSlots = Arrays.stream(bauble.getBaubleType(itemStack).getValidSlots()).boxed().collect(Collectors.toSet());
		LazyOptional<IBaublesItemHandler> allBaubles = BaublesAPI.getBaublesHandler(player);

		if (allBaubles.isPresent())
		{
			for (int i = 0; i < allBaubles.resolve().get().getSlots(); i++)
			{
				ItemStack stack = allBaubles.resolve().get().getStackInSlot(i);

				// If this bauble shares any valid slots with the input bauble, add it.
				if (stack != ItemStack.EMPTY)
				{
					IBauble equippedBauble = stack.getCapability(BaublesCapabilities.ITEM_BAUBLE).resolve().get();
					Set<Integer> sharedSlots = Arrays.stream(equippedBauble.getBaubleType(stack).getValidSlots()).boxed().collect(Collectors.toSet());
					sharedSlots.retainAll(baubleSlots);

					if (!sharedSlots.isEmpty())
					{
						result.add(stack);
					}
				}
			}
		}

		return result;
	}
}
