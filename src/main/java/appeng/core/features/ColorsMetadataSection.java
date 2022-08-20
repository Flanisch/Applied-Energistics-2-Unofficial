package appeng.core.features;

import appeng.core.AELog;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import java.util.Map;
import java.util.HashMap;
import net.minecraft.client.resources.data.IMetadataSection;

@SideOnly(Side.CLIENT)
public class ColorsMetadataSection implements IMetadataSection {
    private final Map<String, Integer> textColors;
    private final Map<String, String> hexTextColors;
    private final Map<String, Integer> rectColors;
    private final Map<String, String> hexRectColors;

    public ColorsMetadataSection(Map<String, String> hexTextColorMap, Map<String, String> hexRectColorMap) {
        this.hexTextColors = hexTextColorMap;
        this.textColors = convertHexMapToIntMap(hexTextColorMap);

        this.hexRectColors = hexRectColorMap;
        this.rectColors = convertHexMapToIntMap(hexRectColorMap);
    }

    private Map<String, Integer> convertHexMapToIntMap(Map <String, String> hexMap) {
        Map<String, Integer> intMap = new HashMap<>();

        for (String key : hexMap.keySet()) {
            int colorValue = -1;
            String hex = hexMap.get(key);
            try {
                if (!hex.isEmpty()) colorValue = Integer.parseUnsignedInt(hex,16);
            }
            catch (final NumberFormatException e) {
                AELog.warn("[AppEng] Couldn't format color correctly of " + key + " -> " + hex);
            }
            intMap.put(key, colorValue);
        }
        return intMap;
    }

    public int getTextColorOrDefault(String key, int defaultColor) {
        return sColorInMap(key, this.hexTextColors) ? defaultColor : this.textColors.get(key);
    }

    public int getRectColorOrDefault(String key, int defaultColor) {
        return sColorInMap(key, this.hexRectColors) ? defaultColor : this.rectColors.get(key);
    }

    private boolean sColorInMap(String key, Map<String,String> hexMap) {
        return hexMap.containsKey(key) && hexMap.get(key).isEmpty();
    }
}