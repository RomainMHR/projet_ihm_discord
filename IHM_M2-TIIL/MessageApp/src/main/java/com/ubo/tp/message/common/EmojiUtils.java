package main.java.com.ubo.tp.message.common;

import java.util.HashMap;
import java.util.Map;

/**
 * Utilitaire pour le remplacement des textes par des emojis.
 */
public class EmojiUtils {

    private static final Map<String, String> EMOJI_MAP = new HashMap<>();

    private EmojiUtils() {
        // Constructeur privé pour cacher celui par défaut
    }

    static {
        EMOJI_MAP.put(":smile:", "😊");
        EMOJI_MAP.put(":sad:", "😢");
        EMOJI_MAP.put(":smirk:", "😏");
        EMOJI_MAP.put(":heart:", "❤️");
        EMOJI_MAP.put(":thumbsup:", "👍");
        EMOJI_MAP.put(":thumbsdown:", "👎");
        EMOJI_MAP.put(":laugh:", "😂");
        EMOJI_MAP.put(":party:", "🥳");
        EMOJI_MAP.put(":mad:", "😠");
        EMOJI_MAP.put(":cool:", "😎");
    }

    /**
     * Remplace les raccourcis texte (ex: :smile:) par leur emoji correspondant.
     * 
     * @param text Le texte original.
     * @return Le texte avec les emojis.
     */
    public static String replaceEmojis(String text) {
        if (text == null || text.isEmpty()) {
            return text;
        }
        String result = text;
        for (Map.Entry<String, String> entry : EMOJI_MAP.entrySet()) {
            result = result.replace(entry.getKey(), entry.getValue());
        }
        return result;
    }
}
