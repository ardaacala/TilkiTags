package com.tilkicraft.tags;

import java.util.*;
import org.bukkit.ChatColor;
import org.bukkit.configuration.ConfigurationSection;

public record Rank(String id, Set<String> groups, int priority, String label, ChatColor color, ChatColor messageColor, int glyph) {
    public static List<Rank> load(ConfigurationSection section) {
        if (section==null) throw new IllegalArgumentException("ranks section missing");
        List<Rank> ranks=new ArrayList<>(); Set<Integer> glyphs=new HashSet<>();
        for(String id:section.getKeys(false)) {
            ConfigurationSection value=section.getConfigurationSection(id);
            if (value==null || !id.matches("[a-z0-9_]{1,20}")) throw new IllegalArgumentException("Invalid rank id: "+id);
            int priority=value.getInt("priority"); int glyph=value.getInt("glyph",-1);
            if (priority<0 || priority>9999 || glyph<0 || glyph>16 || !glyphs.add(glyph)) throw new IllegalArgumentException("Invalid/duplicate rank glyph or priority: "+id);
            Set<String> groups=new HashSet<>(); value.getStringList("groups").forEach(group->groups.add(group.toLowerCase(Locale.ROOT)));
            ChatColor color=ChatColor.valueOf(value.getString("name-color","WHITE"));
            if (!color.isColor()) throw new IllegalArgumentException("Expected a name color: "+id);
            ChatColor messageColor=ChatColor.valueOf(value.getString("message-color","WHITE").toUpperCase(Locale.ROOT));
            if (!messageColor.isColor()) throw new IllegalArgumentException("Expected a message color: "+id);
            String label=ChatColor.translateAlternateColorCodes('&',value.getString("label",id));
            if (label.length()>52 || label.contains("%") || label.contains("\n")) throw new IllegalArgumentException("Rank label too long or invalid: "+id);
            ranks.add(new Rank(id,Set.copyOf(groups),priority,label,color,messageColor,glyph));
        }
        if (ranks.stream().noneMatch(rank->rank.id.equals("oyuncu"))) throw new IllegalArgumentException("oyuncu fallback is required");
        ranks.sort(Comparator.comparingInt(Rank::priority).reversed().thenComparing(Rank::id));
        return List.copyOf(ranks);
    }
    public String prefix(boolean icons) {
        return badge(icons)+" "+ChatColor.WHITE;
    }
    public static String whiteName(String nickname) {
        return ChatColor.WHITE+ChatColor.stripColor(ChatColor.translateAlternateColorCodes('&',nickname));
    }
    public static String cleanLevel(String value) {
        if(value==null) return "0";
        value=ChatColor.stripColor(ChatColor.translateAlternateColorCodes('&',value)).trim();
        return value.isEmpty() || value.length()>16 || !value.matches("[0-9.,+\\- kKmMbBtT]+") ? "0" : value;
    }
    public String badge(boolean icons) {
        return (icons?ChatColor.WHITE.toString()+(char)(0xeb00+glyph):label)+ChatColor.RESET;
    }
    public String teamName() { return "tt"+String.format(Locale.ROOT,"%04d",9999-priority)+"_"+glyph; }
    public boolean matches(Set<String> memberships, Set<String> explicitRanks) {
        return explicitRanks.contains(id) || groups.stream().anyMatch(memberships::contains);
    }
    public static Rank choose(List<Rank> ranked,Set<String> groups,Set<String> permissions) {
        return ranked.stream().filter(rank->rank.matches(groups,permissions)).findFirst()
                .orElseGet(()->ranked.stream().filter(rank->rank.id.equals("oyuncu")).findFirst().orElseThrow());
    }
    public static String chatFormat(String template,String prefix,String nickname) {
        return chatFormat(template,prefix,nickname,null);
    }
    public static String chatFormat(String template,String prefix,String nickname,ChatColor messageColor) {
        // Escape injected percent signs before Java's chat formatter processes %2$s.
        String marker="\u0001MESSAGE\u0001";
        String coloredMarker=(messageColor==null?"":messageColor.toString())+marker;
        String format=template.replace("{message}",coloredMarker).replace("{tag}",prefix).replace("{name}",whiteName(nickname));
        format=ChatColor.translateAlternateColorCodes('&',format).replace("%","%%");
        return format.replace(marker,"%2$s");
    }
}
