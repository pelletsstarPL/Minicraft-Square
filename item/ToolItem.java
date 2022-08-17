package minicraft.item;

import java.util.ArrayList;
import java.util.Random;

import minicraft.core.Game;
import minicraft.core.Renderer;
import minicraft.core.io.Localization;
import minicraft.entity.Entity;
import minicraft.entity.mob.EnemyMob;
import minicraft.entity.mob.Mob;
import minicraft.entity.mob.Zombie;
import minicraft.gfx.Sprite;
import minicraft.item.PotionType;

public class ToolItem extends Item {
	
	protected static ArrayList<Item> getAllInstances() {
		ArrayList<Item> items = new ArrayList<>();

		for (ToolType tool : ToolType.values()) {
			if (!tool.noLevel) {
				for (int lvl = 0; lvl <= 6; lvl++)
					items.add(new ToolItem(tool, lvl));
			} else {
				items.add(new ToolItem(tool));
			}
		}
		
		return items;
	}
	
	private Random random = new Random();
	
	public static final String[] LEVEL_NAMES = {"Wood", "Rock", "Iron", "Gold", "Gem","Zanite","Candy"}; // The names of the different levels. A later level means a stronger tool.
	
	public ToolType type; // Type of tool (Sword, hoe, axe, pickaxe, shovel)
	public int level; // Level of said tool
	public int dur; // The durability of the tool
	public int damage; // The damage of the tool
	
	/** Tool Item, requires a tool type (ToolType.Sword, ToolType.Axe, ToolType.Hoe, etc) and a level (0 = wood, 2 = iron, 4 = gem, etc) */
	public ToolItem(ToolType type, int level) {
		super(LEVEL_NAMES[level] + " " + type.name(), new Sprite(type.xPos, type.yPos + level, 0));
		this.type = type;
		this.level = level;
		this.damage = (level==6 ? 1 : level)*5 + 10;
		if(LEVEL_NAMES[level]!="Candy") {
			dur = type.durability * (level + 1); // Initial durability fetched from the ToolType
		}else{ dur=10;}
	}

	public ToolItem(ToolType type) {
		super(type.name(), new Sprite(type.xPos, type.yPos, 0));

		this.type = type;
		dur = type.durability;
	}
	
	/** Gets the name of this tool (and it's type) as a display string. */
	@Override
	public String getDisplayName() {
		if (!type.noLevel) return " " + Localization.getLocalized(LEVEL_NAMES[level]) + " " + Localization.getLocalized(type.toString());
		else return " " + Localization.getLocalized(type.toString());
	}
	
	public boolean isDepleted() {
		return dur <= 0 && type.durability > 0;
	}
	
	/** You can attack mobs with tools. */
	public boolean canAttack() {
		return type != ToolType.Shears;
	}
	
	public boolean payDurability() {
		if (dur <= 0) return false;
		if (!Game.isMode("creative")) dur--;
		return true;
	}
	public boolean payDurability(int damage) {
			/*if (dur <= 0) return false;
		int d = damage/((level*10) * 3 == 0 ? 1 :(level*10) * 3);
		if (!Game.isMode("creative")) dur -= d>4? 4: d<1? 1: d;*/
		if (dur <= 0) return false;
		if (!Game.isMode("creative")) dur--;
		return true;
	}
	public int getDamage() {
		//Litorom marked this conversation as resolved. Yeah i love gh copypastes
		return random.nextInt(5) + damage;
	}
	/** Gets the attack damage bonus from an item/tool (sword/axe) */
	public int getAttackDamageBonus(Entity e) {
		if (!payDurability())
			return 0;
		int axebonus=(Renderer.player.potioneffects.containsKey(PotionType.Power) ? 2*level : (Renderer.player.potioneffects.containsKey(PotionType.Weak) ? -2*level : 0));
		int swordbonus=(Renderer.player.potioneffects.containsKey(PotionType.Power) ? 3*level : (Renderer.player.potioneffects.containsKey(PotionType.Weak) ? -3*level : 0));
		int claymorebonus=(Renderer.player.potioneffects.containsKey(PotionType.Power) ? 4*level : (Renderer.player.potioneffects.containsKey(PotionType.Weak) ? -4*level : 0));
		double mobBonus=1;
		if(e instanceof EnemyMob){
			if(((EnemyMob) e).lvl >= 4)mobBonus=1.33;
		}
		if(Renderer.player.potioneffects.containsKey(PotionType.Power) && Renderer.player.potioneffects.containsKey(PotionType.Weak))axebonus=swordbonus=claymorebonus=0;
		if (e instanceof Mob) {
			int dmg = 0;
			if (type == ToolType.Axe) {
				if(LEVEL_NAMES[level]=="Candy"){
					dmg = (1) + random.nextInt(2);
				}else {
					dmg = (level + 1) * 2 + random.nextInt(4)+axebonus; // Wood axe damage: 2-5; gem axe damage: 10-13.
				}
			} else if (type == ToolType.Sword) {
			if(LEVEL_NAMES[level]=="Candy"){
				dmg= (2) + random.nextInt(2);
			}else {
				dmg= (level + 1) * 3 + random.nextInt(2 + level * level)+swordbonus; // Wood: 3-5 damage; gem: 15-32 damage.
			}
			} else if (type == ToolType.Claymore) {
			if(LEVEL_NAMES[level]=="Candy"){
				dmg = (3) + random.nextInt(6);
			}else {
				dmg = (int) (((level + 1) * 3 + random.nextInt(4 + level * level * 3)+claymorebonus)*mobBonus); // Wood: 3-6 damage; gem: 15-66 damage.
			}
			} else if (type == ToolType.Pickaxe) {
			dmg = (level + 1) * 1 + random.nextInt(4)+axebonus; // Wood: 3-6 damage; gem: 15-66 damage.
			} else dmg = 1; // All other tools do very little damage to mobs.
			if (!payDurability(dmg))
				return 0;
			else return dmg;
			}
		
		return 0;
	}
	
	@Override
	public String getData() {
		return super.getData() + "_" + dur;
	}
	
	/** Sees if this item equals another. */
	@Override
	public boolean equals(Item item) {
		if (item instanceof ToolItem) {
			ToolItem other = (ToolItem) item;
			return other.type == type && other.level == level;
		}
		return false;
	}
	
	@Override
	public int hashCode() { return type.name().hashCode() + level; }
	
	public ToolItem clone() {
		ToolItem ti;
		if (type.noLevel) {
			ti = new ToolItem(type);
		} else {
			ti = new ToolItem(type, level);
		}
		ti.dur = dur;
		return ti;
	}
}
