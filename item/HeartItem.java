package minicraft.item;

import minicraft.core.Updater;
import minicraft.entity.Direction;
import minicraft.entity.mob.Player;
import minicraft.entity.particle.TextParticle;
import minicraft.gfx.Color;
import minicraft.gfx.Sprite;
import minicraft.level.Level;
import minicraft.level.tile.Tile;

import java.util.ArrayList;

public class HeartItem extends StackableItem {

    protected static ArrayList<Item> getAllInstances() {
        ArrayList<Item> items = new ArrayList<>();

        items.add(new HeartItem("Obsidian Heart",new Sprite(0,21,0), 5));

        return items;
    }

    private int health; // The amount of health to increase by.
    private int staminaCost; // The amount of stamina it costs to consume.

    private HeartItem(String name, Sprite  sprite, int health) { this(name, sprite, 1, health); }
    private HeartItem(String name,Sprite sprite, int count, int health) {
        super(name, sprite, count);
        this.health = health;
        staminaCost = 7;
    }

   //** What happens when the player uses the item on a tile */
    public boolean interactOn(Tile tile, Level level, int xt, int yt, Player player, Direction attackDir) {
        boolean success = false;

        if (Player.obsidianHP < 20) {
            level.add(new TextParticle("+"+health, player.x, player.y, Color.MAGENTA));
            Player.obsidianHP += health; // Permanent increase of health by health variable (Basically 5)
            player.health += health; // Adds health to the player when used. (Almost like absorbing the item's power first time)
            success = true;
        }
        else {
            Updater.notifyAll("Health boost from this src at max!"); // When at max, health cannot be increased more and doesn't consume item
            return false;
        }

        return super.interactOn(success);
    }

    @Override
    public boolean interactsWithWorld() { return false; }

    public HeartItem clone() {
        return new HeartItem(getName(), sprite, count, health);
    }
}
