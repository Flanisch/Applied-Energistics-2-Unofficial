package appeng.core.features;

import appeng.core.features.ColorsMetadataSection;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.util.JsonUtils;
import net.minecraft.client.resources.data.BaseMetadataSectionSerializer;

import java.lang.reflect.Type;
import java.util.Map;
import java.util.HashMap;

@SideOnly(Side.CLIENT)
public class ColorsMetadataSectionSerializer extends BaseMetadataSectionSerializer implements JsonSerializer
{
    public ColorsMetadataSection deserialize(JsonElement metadataColors, Type type, JsonDeserializationContext context)
    {
        // Default values
        Map<String, String> hexTextColorMap = new HashMap<String, String>()
        {{
            put( "title", "" );
            put( "text", "" );
            put( "value", "" );
            put( "nei", "" );
        }};
        Map<String, String> hexRectColorMap = new HashMap<String, String>()
        {{
            put( "main", "" );
            put( "secondary", "" );
        }};

        JsonObject jsonObject = JsonUtils.getJsonElementAsJsonObject( metadataColors, "metadata section" );
        if (jsonObject.has( "textColor" ))
        {
            JsonObject textColors = JsonUtils.func_152754_s( jsonObject, "textColor" );
            for ( String key : hexTextColorMap.keySet() )
            {
                hexTextColorMap.replace( key, JsonUtils.getJsonObjectStringFieldValueOrDefault( textColors, key, "" ));
            }
        }

        if (jsonObject.has( "rectColor" ))
        {
            JsonObject rectColors = JsonUtils.func_152754_s( jsonObject, "rectColor" );
            for ( String key : hexRectColorMap.keySet() )
            {
                hexRectColorMap.replace( key, JsonUtils.getJsonObjectStringFieldValueOrDefault( rectColors, key, "" ));
            }
        }
        return new ColorsMetadataSection( hexTextColorMap, hexRectColorMap );
    }

    public JsonElement serialize(ColorsMetadataSection colorsMetaSection, Type type, JsonSerializationContext context)
    {
        return new JsonObject();
    }

    public String getSectionName()
    {
        return "colors";
    }

    public JsonElement serialize(Object object, Type type, JsonSerializationContext context)
    {
        return this.serialize( (ColorsMetadataSection) object, type, context);
    }
}