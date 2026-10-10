package com.tilkicraft.tags;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.*;
import org.bukkit.configuration.file.YamlConfiguration;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class RankTest {
    private List<Rank> ranks() throws Exception {
        YamlConfiguration config=new YamlConfiguration();
        try(var reader=new InputStreamReader(Objects.requireNonNull(getClass().getResourceAsStream("/config.yml")),StandardCharsets.UTF_8)) {config.load(reader);}
        return Rank.load(config.getConfigurationSection("ranks"));
    }
    @Test void staffWinsOverHighestVipAndVipWinsOverMiningTier() throws Exception {
        var ranks=ranks();
        assertEquals("admin",Rank.choose(ranks,Set.of("admin","tilki","netherite"),Set.of()).id());
        assertEquals("tilki",Rank.choose(ranks,Set.of("tilki","megavipplus","netherite"),Set.of()).id());
        assertEquals("vip",Rank.choose(ranks,Set.of("vip","netherite"),Set.of()).id());
    }
    @Test void supportsPlusAliasesAndUnknownGroupsFallBackToPlayer() throws Exception {
        var ranks=ranks();
        assertEquals("vipplus",Rank.choose(ranks,Set.of("vip+"),Set.of()).id());
        assertEquals("netherite",Rank.choose(ranks,Set.of("nethrite"),Set.of()).id());
        assertEquals("oyuncu",Rank.choose(ranks,Set.of("unrelated"),Set.of()).id());
    }
    @Test void everyBadgeFitsTeamLimitsAndFallbackContainsNoPrivateGlyphs() throws Exception {
        var ranks=ranks(); assertEquals(17,ranks.size());Set<String> teamNames=new HashSet<>();
        for(Rank rank:ranks) {
            assertTrue(rank.prefix(true).length()<=64,rank.id());
            assertTrue(rank.teamName().length()<=16);assertTrue(teamNames.add(rank.teamName()));
            assertFalse(rank.prefix(false).chars().anyMatch(character->character>=0xe000 && character<=0xf8ff));
        }
    }
    @Test void chatDoesNotInterpretPercentSignsInNicknamesAsFormatArguments() {
        String format=Rank.chatFormat("{tag}{name} » {message}","VIP ","100%Tilki");
        assertEquals("VIP 100%Tilki » test %s",String.format(format,"unused","test %s"));
    }
    @Test void loadedBadgeIsOneGlyphWithoutADuplicateTextLabel() throws Exception {
        for(Rank rank:ranks()) {
            assertEquals(String.valueOf((char)(0xeb00+rank.glyph())),org.bukkit.ChatColor.stripColor(rank.badge(true)));
            assertEquals(org.bukkit.ChatColor.stripColor(rank.label()),org.bukkit.ChatColor.stripColor(rank.badge(false)));
        }
    }
}
