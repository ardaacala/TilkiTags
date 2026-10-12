package com.tilkicraft.tags;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class TextWidthTest {
    @Test void asciiAdvancesMatchTheAtlasAndIgnoreColourCodes() {
        assertEquals(4, TextWidth.width(" "));
        assertEquals(TextWidth.width("Arda"), TextWidth.width("§fAr§7da"));
        assertEquals(TextWidth.width("Arda") + 4, TextWidth.width("§lArda"));
    }
    @Test void paddingIsExactAndInvisibleInPlainText() {
        for (int pixels = 0; pixels <= 300; pixels++) assertEquals(pixels, TextWidth.width(TextWidth.pad(pixels)));
        assertTrue(TextWidth.pad(37).chars().allMatch(c -> c >= 0xeb80 && c < 0xeb88));
    }
    @Test void badgesUseTheGeneratedAdvancesAndTheCompactTilkiIsOneLine() {
        assertEquals(43, TextWidth.width(String.valueOf('')));
        assertTrue(TextWidth.width(String.valueOf('')) < TextWidth.width(String.valueOf('')));
    }
    @Test void fittedNamesNeverExceedTheBudget() {
        String fitted = TextWidth.fit("§fAVeryVeryLongNickname", 60);
        assertTrue(TextWidth.width(fitted) <= 60);
        assertTrue(fitted.endsWith(".."));
        assertEquals("§fArda", TextWidth.fit("§fArda", 60));
    }
}
