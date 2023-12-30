package minicraft.core.io;

import java.awt.DisplayMode;
import java.awt.GraphicsEnvironment;
import java.util.HashMap;

import minicraft.core.Game;
import minicraft.level.Level;
import minicraft.saveload.Save;
import minicraft.screen.SkinDisplay;
import minicraft.screen.entry.ArrayEntry;
import minicraft.screen.entry.BooleanEntry;
import minicraft.screen.entry.RangeEntry;

public class Settings {
	
	private static HashMap<String, ArrayEntry> options = new HashMap<>();
	
	static {
		options.put("fps", new RangeEntry("Max FPS", 30, 400, getRefreshRate())); // Has to check if the game is running in a headless mode. If it doesn't set the fps to 60

		options.put("diff", new ArrayEntry<>("Difficulty", "Easy", "Normal", "Hard"));
		options.get("diff").setSelection(1);
		options.put("coloredgui", new BooleanEntry("Colored gui", true));
		options.put("statdisplay", new ArrayEntry<>("Stats display", "Traditional","Shortened"));
		options.get("statdisplay").setSelection(0);
		options.put("mode", new ArrayEntry<>("Game Mode", "Survival", "Creative", "Hardcore", "Score"));
		
		options.put("scoretime", new ArrayEntry<>("Time (Score Mode)", 10, 20, 40, 60, 120));
		options.get("scoretime").setValueVisibility(10, false);
		options.get("scoretime").setValueVisibility(120, false);
		
		options.put("sound", new BooleanEntry("Sound", true));
		options.put("soundno", new BooleanEntry("Sound when can't use", true));
		options.put("soundmob", new ArrayEntry<>("Mob sounds system", "New", "Old") );
		options.put("soundambient", new BooleanEntry("Ambience", true));
		options.put("autosave", new BooleanEntry("Autosave", true));
		
		options.put("size", new ArrayEntry<>("World Size", 128, 256, 512));
		options.put("dominantbiome", new ArrayEntry<>("Dominant biome", "Normal", "Forest", "Desert","Drylands", "Plain", "Swamp","Taiga","Tundra"));
		options.put("theme", new ArrayEntry<>("World theme", "Normal", "Hell"));
		options.put("type", new ArrayEntry<>("Terrain Type", "Island", "Box", "Mountain", "Irregular"));
		options.put("biomedominace", new RangeEntry("Dom. biom. dominance", 0, 5, 0));
		options.put("stonemass", new RangeEntry("Stonemass level", -1, 5, 0));

		options.put("unlockedskin", new BooleanEntry("Wear Suit", false));
		options.put("skinon", new BooleanEntry("Wear Suit", false));
		
		options.put("language", new ArrayEntry<>("Language", true, false, Localization.getLanguages()));
		options.get("language").setValue(Localization.getSelectedLanguage());
		
		
		options.get("mode").setChangeAction(value ->
			options.get("scoretime").setVisible("Score".equals(value))
		);
		
		/*options.get("unlockedskin").setChangeAction(value ->
				options.get("skinon").setVisible(Save.AirWizard2Beaten)
		);*/

		options.get("skinon").setChangeAction(value -> {
			if (Game.player != null) {
				Game.player.suitOn = (boolean) value;
			}
		});

		options.put("textures", new ArrayEntry<>("textures", "Original", "Custom"));
		options.get("textures").setSelection(0);

		options.put("potionsn", new RangeEntry("Displayed potions amount", 2,5,3));
		options.put("potiontxtlen", new ArrayEntry<>("Displayed text length", 0,3,5,7));
		options.put("displayside", new ArrayEntry<>("Side of display", "Right","Left"));
		options.put("displayicon", new BooleanEntry("Display icon", true));
	}
	
	public static void init() {}
	
	// Returns the value of the specified option
	public static Object get(String option) { return options.get(option.toLowerCase()).getValue(); }
	
	// Returns the index of the value in the list of values for the specified option
	public static int getIdx(String option) { return options.get(option.toLowerCase()).getSelection(); }
	
	// Return the ArrayEntry object associated with the given option name.
	public static ArrayEntry getEntry(String option) { return options.get(option.toLowerCase()); }
	
	// Sets the value of the given option name, to the given value, provided it is a valid value for that option.
	public static void set(String option, Object value) {
		options.get(option.toLowerCase()).setValue(value);
	}
	
	// Sets the index of the value of the given option, provided it is a valid index
	public static void setIdx(String option, int idx) {
		options.get(option.toLowerCase()).setSelection(idx);
	}

	private static int getRefreshRate() {
		if (GraphicsEnvironment.isHeadless()) return 60;

		int hz = GraphicsEnvironment.getLocalGraphicsEnvironment().getDefaultScreenDevice().getDisplayMode().getRefreshRate();
		if (hz == DisplayMode.REFRESH_RATE_UNKNOWN) return 60;
		if (hz > 300) return 60;
		if (10 > hz) return 60;
		return hz;
	}
}
