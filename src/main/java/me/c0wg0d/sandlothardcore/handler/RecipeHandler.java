package me.c0wg0d.sandlothardcore.handler;

import me.c0wg0d.sandlothardcore.SandlotHardcore;
import org.bukkit.Material;
import org.bukkit.inventory.Recipe;

import java.util.Iterator;

import static org.bukkit.Bukkit.getServer;

public class RecipeHandler {

    public static void register(SandlotHardcore plugin) {
        if(plugin.getConfig().getBoolean("options.disable-golden-apple-recipe", false)) {
            return;
        }
        Iterator<Recipe> recipeList = getServer().recipeIterator();
        Recipe recipe;
        while(recipeList.hasNext())
        {
            recipe = recipeList.next();
            if (recipe != null && recipe.getResult().getType() == Material.GOLDEN_APPLE)
            {
                // Can't do this in 1.12 anymore
                //recipeList.remove();
            }

            if (recipe != null && recipe.getResult().getType() == Material.FISHING_ROD)
            {
                // Can't do this in 1.12 anymore
                //recipeList.remove();
            }
        }
    }
}

