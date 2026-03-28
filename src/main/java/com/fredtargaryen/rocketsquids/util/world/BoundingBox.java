// Copyright 2026 FredTargaryen and contributors
// See README.md for full copyright notice and contributor info
package com.fredtargaryen.rocketsquids.util.world;

import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.AABB;

public class BoundingBox {
    /** Creates an AABB boundbox of the spesified radius
     * @param center BlockPos for the center of the bounding box
     * @param radiusSize double for the radius or size of the bounding box
     * @return returns an AABB bounding box
     */
    public AABB cubeFromPosition(BlockPos center, int radiusSize) {
        radiusSize = Math.abs(radiusSize);
        BlockPos start = center.offset(radiusSize, radiusSize, radiusSize);
        BlockPos end = center.offset(-radiusSize, -radiusSize, -radiusSize);
        return new AABB(start, end);
    }

    /** Creates an AABB boundbox of the spesified radius then clamps it to the minHeight & maxHeight respectively.
     * @param center BlockPos for the center of the bounding box
     * @param radiusSize double for the radius or size of the bounding box
     * @param maxHeight maximum Y level to clamp to
     * @param minHeight minimum Y level to clamp to
     * @return returns an AABB bounding box
     */
    public AABB cubeFromPositionClamped(BlockPos center, int radiusSize, int maxHeight, int minHeight) {
        radiusSize = Math.abs(radiusSize);
        BlockPos start = center.offset(radiusSize, Math.max(radiusSize, maxHeight), radiusSize);
        BlockPos end = center.offset(-radiusSize, -Math.min(radiusSize, minHeight), -radiusSize);
        return new AABB(start, end);
    }

    /** Creates an AABB boundbox of the spesified radius then clamps it to the height limit in box directions.
     * @param center BlockPos for the center of the bounding box
     * @param radiusSize double for the radius or size of the bounding box
     * @param level Level to get the min & max build heights from
     * @return returns an AABB bounding box
     */
    public AABB cubeFromPositionClamped(BlockPos center, int radiusSize, Level level) {
        return cubeFromPositionClamped(center, radiusSize, level.getMaxBuildHeight(), level.getMinBuildHeight());
    }

    /** Creates an AABB boundbox of the spesified radius then clamps it to the height limit in box directions.
     * @param center BlockPos for the center of the bounding box
     * @param radiusSize double for the radius or size of the bounding box
     * @param level ServerLevel to get the min & max build heights from
     * @return returns an AABB bounding box
     */
    public AABB cubeFromPositionClamped(BlockPos center, int radiusSize, ServerLevel level) {
        return cubeFromPositionClamped(center, radiusSize, level.getMaxBuildHeight(), level.getMinBuildHeight());
    }
}
