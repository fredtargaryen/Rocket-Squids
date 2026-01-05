// This code was written from via referencing the usage of IStorage in MIT Licensed Code,
// reverse engineering from there and using documentation
// (like this from Oracle https://docs.oracle.com/javase/tutorial/java/generics/types.html).
// It WAS NOT written by referencing IStorage as part of the
// MinecraftForge project due to the conflicting licenses. This new non derivative code is
// licensed under the MIT License: http://www.opensource.org/licenses/mit-license.php
package com.fredtargaryen.rocketsquids.util.capability;

import net.minecraft.core.Direction;
import net.minecraft.nbt.Tag;
import net.minecraftforge.common.capabilities.Capability;

public interface IStorage<T> {
    Tag writeNBT (Capability<T> cap, T instance, Direction direction);

    void readNBT (Capability<T> cap, T instance, Direction direction, Tag tag);
}
