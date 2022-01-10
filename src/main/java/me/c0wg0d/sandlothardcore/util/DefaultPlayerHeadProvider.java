package me.c0wg0d.sandlothardcore.util;
/*
 * DefaultPlayerHeadProvider.java
 *
 * The MIT License (MIT)
 *
 * Copyright (c) 2014 Graham Howden <graham_howden1 at yahoo.co.uk>.
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 */

        import com.google.common.collect.Lists;
        import org.bukkit.*;
        import org.bukkit.block.Block;
        import org.bukkit.block.Skull;
        import org.bukkit.block.data.Rotatable;
        import org.bukkit.entity.Entity;
        import org.bukkit.entity.EntityType;
        import org.bukkit.entity.Player;
        import org.bukkit.inventory.ItemStack;
        import org.bukkit.inventory.meta.ItemMeta;
        import org.bukkit.inventory.meta.SkullMeta;

        import java.util.List;
        import java.util.Map;
        import java.util.UUID;

public class DefaultPlayerHeadProvider implements PlayerHeadProvider
{

    public static final String HEAD_NAME = ChatColor.GOLD + "Golden Head";

    /**
     * Gets the closest blockface to the entities facing direction
     *
     * @param entity the entity
     * @return block face
     */
    public static BlockFace2D getCardinalDirection(Entity entity)
    {
        return BlockFace2D.getClosest(Math.toRadians(entity.getLocation().getYaw()));
    }

    @Override
    public ItemStack getPlayerHead(String name)
    {
        ItemStack is = new ItemStack(Material.PLAYER_HEAD, 1);

        SkullMeta meta = (SkullMeta) is.getItemMeta();
        meta.setOwner(name);
        is.setItemMeta(meta);
        return is;
    }

    @Override
    public ItemStack getPlayerHead(Player player)
    {
        return getPlayerHead(player.getName());
    }

    @Override
    public ItemStack getGoldenHead()
    {
        ItemStack itemStack = new ItemStack(Material.GOLDEN_APPLE, 1);
        ItemMeta meta = itemStack.getItemMeta();
        meta.setDisplayName(HEAD_NAME);

        //add it's lore
        List<String> lore = Lists.newArrayList("Some say consuming the head of a", "fallen foe strengthens the blood");
        meta.setLore(lore);
        itemStack.setItemMeta(meta);

        return itemStack;
    }

    @Override
    public boolean isGoldenHead(ItemStack itemStack)
    {
        if(itemStack.getType() == Material.GOLDEN_APPLE) {
            ItemMeta im = itemStack.getItemMeta();

            if(im.hasDisplayName() && im.getDisplayName().equals(HEAD_NAME)) {
                return true;
            }
        }

        return false;
    }

    @Override
    public void addPlayerLore(ItemStack itemStack, String name)
    {
        ItemMeta meta = itemStack.getItemMeta();
        List<String> lore = meta.getLore();
        lore.add(ChatColor.AQUA + "Made from the head of: " + name);
        meta.setLore(lore);
        itemStack.setItemMeta(meta);
    }

    @Override
    public void addPlayerLore(ItemStack itemStack, Player player)
    {
        addPlayerLore(itemStack, player.getName());
    }

    @Override
    public void setBlockAsHead(Player p, Block headBlock)
    {
        setBlockAsHead(p.getName(), headBlock, getCardinalDirection(p));
    }

    @Override
    public void setBlockAsHead(String name, Block headBlock, BlockFace2D direction)
    {
        //set the type to skull
        //headBlock.setType(Material.SKULL);
        //noinspection deprecation
        //headBlock.setData((byte) 1); //TODO depreacted but no alternative yet

        //get the state to be a player skull for the player and set its rotation based on where the player was looking
        //Skull state = (Skull) headBlock.getState();
        //state.setSkullType(SkullType.PLAYER);
        //state.setOwner(name);
        //state.setRotation(direction.getBlockFace());
        //state.update();
        Player p = Bukkit.getPlayer(name);

        headBlock.setType(Material.PLAYER_HEAD);
        Skull state = (Skull) headBlock.getState();
        state.setOwningPlayer(Bukkit.getServer().getOfflinePlayer(p.getUniqueId()));
        Rotatable skullRotation = (Rotatable) state.getBlockData();
        skullRotation.setRotation(direction.getBlockFace());
        state.setBlockData(skullRotation);
        state.update(true);
    }
}