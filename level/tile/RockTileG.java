package minicraft.level.tile;

import minicraft.core.Game;
import minicraft.core.io.Settings;
import minicraft.core.io.Sound;
import minicraft.entity.Direction;
import minicraft.entity.Entity;
import minicraft.entity.mob.Mob;
import minicraft.entity.mob.Player;
import minicraft.entity.particle.SmashParticle;
import minicraft.entity.particle.TextParticle;
import minicraft.gfx.Color;
import minicraft.gfx.ConnectorSprite;
import minicraft.gfx.Screen;
import minicraft.gfx.Sprite;
import minicraft.item.Item;
import minicraft.item.Items;
import minicraft.item.ToolItem;
import minicraft.item.ToolType;
import minicraft.level.Level;

// This is the normal stone you see underground and on the surface, that drops coal and stone.

public class RockTileG extends Tile {
	int depthB=0;
	int check(Level level){
		if(level.depth==-2) depthB=3;
		else depthB=0;
		return 0;
	};
	private ConnectorSprite sprite = new ConnectorSprite(RockTileG.class, new Sprite(4, 6, 3, 3, 1, 3), new Sprite(21, 8, 2, 2, 1, 3), new Sprite(21, 6, 2, 2, 1, 3));
	private boolean dropCoal = false;
	private int maxHealth = 50;

	private int damage;

	protected RockTileG(String name) {
		super(name, (ConnectorSprite)null);
		csprite = sprite;
	}
	
	public void render(Screen screen, Level level, int x, int y) {
		if(level.depth==-3 || level.depth==-2)sprite=new ConnectorSprite(RockTileG.class, new Sprite(15, 6, 3, 3, 1, 3), new Sprite(21, 8, 2, 2, 1, 3), new Sprite(22, 6, 2, 2, 1, 3));
		else sprite = new ConnectorSprite(RockTileG.class, new Sprite(18, 6, 3, 3, 1, 3), new Sprite(21, 8, 2, 2, 1, 3), new Sprite(21, 6, 2, 2, 1, 3));
		sprite.sparse.color = DirtTile.dCol(level.depth);
		sprite.render(screen, level, x, y);
	}
	
	public boolean mayPass(Level level, int x, int y, Entity e) {
		return false;
	}
	
	public boolean hurt(Level level, int x, int y, Mob source, int dmg, Direction attackDir) {
		hurt(level, x, y, dmg);
		return true;
	}

	public boolean interact(Level level, int xt, int yt, Player player, Item item, Direction attackDir) {
		if (item instanceof ToolItem) {
			ToolItem tool = (ToolItem) item;
			int staminaPay=(4-tool.level < 2 ? 2 : 4-tool.level);
			int dmg = random.nextInt(10) + tool.damage;
			if (tool.level!=6 && tool.type == ToolType.Pickaxe && player.payStamina(staminaPay) && tool.payDurability(dmg)) {
				// Drop coal since we use a pickaxe.
				dropCoal = true;
				hurt(level, xt, yt, dmg);
				return true;
			}else if (tool.level==6 && tool.type == ToolType.Pickaxe && player.payStamina(staminaPay) && tool.payDurability(dmg)) {
				dmg=random.nextInt(8)+2;
				hurt(level, xt, yt, dmg);
			}
		}
		return false;
	}

	public void hurt(Level level, int x, int y, int dmg) {
		damage = level.getData(x, y) + dmg;

		if (Game.isMode("Creative")) {
			dmg = damage = maxHealth;
			dropCoal = true;
		}

		level.add(new SmashParticle(x * 16, y * 16));
		Sound.monsterHurt.play();

		level.add(new TextParticle("" + dmg, x * 16 + 8, y * 16 + 8, Color.RED));
		if (damage >= maxHealth) {
			if (dropCoal) {
				level.dropItem(x*16+8, y*16+8, 1, 3, Items.get("Stone"));
				int coal = 0;
				if(!Settings.get("diff").equals("Hard")) {
					coal++;
				}
				level.dropItem(x * 16 + 8, y * 16 + 8, coal, coal + 1, Items.get("Coal"));
			} else {
				level.dropItem(x * 16 + 8, y * 16 + 8, 2, 4, Items.get("Stone"));
			}
			level.setTile(x, y, Tiles.get("Ground rock"));
		} else {
			level.setData(x, y, damage);
		}
	}

	public boolean tick(Level level, int xt, int yt) {
		damage = level.getData(xt, yt);
		if (damage > 0) {
			level.setData(xt, yt, damage - 1);
			return true;
		}
		return false;
	}
}
