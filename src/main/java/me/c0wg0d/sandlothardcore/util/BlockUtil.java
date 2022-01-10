package me.c0wg0d.sandlothardcore.util;

import com.google.common.base.Optional;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.block.Skull;
import org.bukkit.block.data.Rotatable;
import org.bukkit.entity.Player;

import java.util.Arrays;
import java.util.Collection;

public class BlockUtil {
    private static final Collection<Material> FLUIDS = Arrays.asList(Material.WATER, Material.LAVA);

    public static boolean putHeadOnStake(Player p) {
        //head location
        Location head = p.getEyeLocation();
        //block the player's head is in
        Block headBlock = head.getBlock();

        //don't do anything if below the world
        if(headBlock.getY() < 0) {
            return false;
        }

        //get the closest non air block below the players feet
        Optional<Block> groundOptional = getClosestGround(headBlock.getRelative(BlockFace.DOWN, 2));
        if(!groundOptional.isPresent()) {
            return false;
        }

        Block ground = groundOptional.get();

        //get the block 2 above the ground
        Block skullBlock = ground.getRelative(BlockFace.UP, 2);

        //if it's not empty we can't place the block
        if(skullBlock == null || !skullBlock.isEmpty()) {
            return false;
        }

        headBlock.setType(Material.PLAYER_HEAD);
        Skull state = (Skull) headBlock.getState();
        state.setOwningPlayer(Bukkit.getServer().getOfflinePlayer(p.getUniqueId()));
        Rotatable skullRotation = (Rotatable) state.getBlockData();
        skullRotation.setRotation(BlockFace2D.getClosest(Math.toRadians(p.getLocation().getYaw())).getBlockFace());
        state.setBlockData(skullRotation);
        state.update(true);


        //get the space for a fence and set it if there's nothing there
        Block fenceBlock = ground.getRelative(BlockFace.UP);
        if(fenceBlock != null && fenceBlock.isEmpty()) {
            fenceBlock.setType(Material.NETHER_BRICK_FENCE);
        }
        //made successfully
        return true;
    }

    public static Optional<Block> getClosestGround(Block block)
    {
        Block loopBlock = block;
        //recurse until found
        while(true) {
            //if below the world return null
            if(loopBlock.getY() < 0) {
                return Optional.absent();
            }

            //if it's not empty return this block
            if(!loopBlock.isEmpty()) {
                return Optional.of(block);
            }
            loopBlock = loopBlock.getRelative(BlockFace.DOWN);
        }
    }

    public static boolean isBreathable(Block block) {
        return !block.getType().isSolid() && !isFluid(block);
    }

    public static boolean isFluid(Block block) {
        return FLUIDS.contains(block.getType());
    }
}
