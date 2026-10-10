import java.awt.*;
import java.awt.image.BufferedImage;
import java.nio.file.*;
import java.security.MessageDigest;
import java.util.*;
import java.util.List;
import java.util.zip.*;
import javax.imageio.ImageIO;

/** Original deterministic pixel badges, designed for nine-pixel text lines. */
class BuildTagsPack {
    static final String[] IDS={"oyuncu","rehber","mod","admin","kurucu","toprak","tas","bronz","demir","altin","elmas","netherite","vip","vipplus","megavip","megavipplus","tilki"};
    static final String[] LABELS={"OYUNCU","REHBER","MOD","ADMIN","KURUCU","TOPRAK","TAŞ","BRONZ","DEMİR","ALTIN","ELMAS","NETHERITE","VIP","VIP+","MEGAVIP","MEGAVIP+","Tilki"};
    static final int[] COLORS={0xc1c8d2,0x67dc86,0x2dbb73,0xf45b56,0xe64d55,0xb7884d,0xa2a6ae,0xcd8c54,0xd7e6eb,0xffd356,0x65e5ed,0x89778f,0xffce4e,0xffdf81,0xcb80ff,0xedb0ff,0xff9735};
    static Graphics2D g;
    static void color(int rgb) {g.setColor(new Color(rgb));}
    static void rect(int color,int x,int y,int width,int height) {color(color);g.fillRect(x,y,width,height);}
    static void poly(int color,int... points) {
        Polygon polygon=new Polygon();for(int i=0;i<points.length;i+=2) polygon.addPoint(points[i],points[i+1]);
        color(color);g.fillPolygon(polygon);
    }
    static void gem(int base) {
        poly(0x10131b,1,6,5,2,11,2,15,6,8,15);
        poly(base,2,6,5,3,11,3,14,6,8,13);
        poly(0xf0ffff,5,3,8,3,5,6,3,6);
        poly(0x327b95,2,7,6,7,8,13);
        poly(0xa0ffff,6,7,12,7,8,13);
    }
    static void crown(int base) {
        poly(0x17121c,1,4,5,7,8,2,11,7,15,4,13,13,3,13);
        poly(base,2,5,5,8,8,3,11,8,14,5,12,12,4,12);
        rect(0xffed9a,4,10,8,1);rect(0xffffff,7,4,2,2);rect(0xd45b67,7,9,2,2);
    }
    static void star(int base) {
        poly(0x19131a,8,1,10,5,15,6,12,10,12,15,8,12,4,15,4,10,1,6,6,5);
        poly(base,8,3,10,6,13,7,10,9,11,12,8,10,5,12,6,9,3,7,6,6);
        rect(0xfff4c1,7,6,2,2);
    }
    static void plus() {rect(0x17131e,10,0,6,7);rect(0xffffff,12,1,2,5);rect(0xffffff,10,3,6,1);}
    static BufferedImage icon(int index) {
        BufferedImage image=new BufferedImage(32,32,BufferedImage.TYPE_INT_ARGB);
        g=image.createGraphics();g.scale(2,2);int base=COLORS[index];
        switch(index) {
            case 0 -> {rect(0x161b24,3,2,10,12);rect(base,4,3,8,10);rect(0xecc6a1,5,6,6,5);rect(0x263446,5,4,6,3);rect(0x253448,6,8,1,1);rect(0x253448,9,8,1,1);rect(0xf6e0c0,7,10,2,1);}
            case 1 -> {rect(0x14251b,1,3,14,11);rect(base,2,4,6,8);rect(0x319d5c,8,4,6,8);rect(0xeaffde,3,5,4,1);rect(0xeaffde,3,7,4,1);rect(0xcef8bf,9,5,3,1);rect(0xffd357,7,1,2,4);}
            case 2,3 -> {poly(0x121a21,2,2,14,2,14,9,8,15,2,9);poly(base,3,3,13,3,13,9,8,13,3,9);poly(0xeaffff,8,4,5,8,8,8,7,11,11,6,8,6);}
            case 4 -> {rect(0x743649,0,8,3,3);rect(0x743649,13,8,3,3);crown(0xffbe44);}
            case 5 -> {poly(0x282016,1,5,8,1,15,5,15,12,8,16,1,12);poly(0x896039,2,6,8,9,8,14,2,11);poly(0xb7844d,8,9,14,6,14,11,8,14);poly(0x75bc48,2,5,8,2,14,5,8,8);rect(0xcba16a,4,9,2,1);rect(0x654429,11,9,2,2);}
            case 6 -> {poly(0x20242d,1,5,8,1,15,5,15,12,8,16,1,12);poly(0x747d8a,2,6,8,9,8,14,2,11);poly(0x939da9,8,9,14,6,14,11,8,14);poly(0xc9cfd5,2,5,8,2,14,5,8,8);rect(0x5d6874,4,9,2,1);rect(0x5d6874,11,9,2,2);}
            case 7,8,9,11 -> {poly(0x17171e,1,8,5,3,12,3,15,8,13,12,2,12);poly(base,2,8,5,4,11,4,14,8,12,11,3,11);poly(index==11?0xafa1b1:0xfff5c8,5,4,11,4,13,7,4,7);rect(index==11?0x4b3d55:0x835b37,3,9,9,1);if(index==11)rect(0xe39851,7,6,2,1);}
            case 10 -> gem(base);
            case 12,13 -> {rect(0x6d4524,2,12,3,3);rect(0x6d4524,11,12,3,3);star(base);if(index==13)plus();}
            case 14,15 -> {gem(base);rect(0x703f9d,1,11,3,3);rect(0x703f9d,12,11,3,3);rect(0xffd56d,6,0,1,3);rect(0xffd56d,9,0,1,3);if(index==15)plus();}
            case 16 -> {
                poly(0x261a20,1,4,6,7,10,7,15,4,14,13,8,16,2,13);
                poly(0xff982b,2,5,6,8,10,8,14,5,13,12,8,15,3,12);
                poly(0xf4d4ac,3,6,5,8,3,10);poly(0xf4d4ac,13,6,11,8,13,10);
                poly(0xffe5b9,3,11,6,11,8,13,10,11,13,11,11,14,8,15,5,14);
                rect(0x251b21,5,10,2,1);rect(0x251b21,9,10,2,1);rect(0x251b21,7,12,2,1);
                poly(0x4b2b1a,4,1,6,3,8,0,10,3,12,1,11,6,5,6);
                poly(0xffd75c,5,2,6,4,8,1,10,4,11,2,10,5,6,5);rect(0xfff0af,7,3,2,1);
            }
        }
        g.dispose();image.setRGB(31,0,0x01ffffff);return image;
    }
    // Five-by-seven pixel lettering remains legible without depending on system fonts.
    static final Map<Character,String> LETTERS=Map.ofEntries(
        Map.entry('A',"01110/11011/11011/11111/11011/11011/11011"),
        Map.entry('B',"11110/11011/11011/11110/11011/11011/11110"),
        Map.entry('C',"01111/11000/11000/11000/11000/11000/01111"),
        Map.entry('D',"11110/11011/11011/11011/11011/11011/11110"),
        Map.entry('E',"11111/11000/11000/11110/11000/11000/11111"),
        Map.entry('F',"11111/10000/10000/11110/10000/10000/10000"),
        Map.entry('G',"01111/10000/10000/10111/10001/10001/01111"),
        Map.entry('H',"11011/11011/11011/11111/11011/11011/11011"),
        Map.entry('I',"1111/0110/0110/0110/0110/0110/1111"),
        Map.entry('İ',"0110/0000/1111/0110/0110/0110/1111"),
        Map.entry('K',"11011/11011/11110/11100/11110/11011/11011"),
        Map.entry('L',"1100/1100/1100/1100/1100/1100/1111"),
        Map.entry('M',"1100011/1110111/1111111/1101011/1100011/1100011/1100011"),
        Map.entry('N',"110011/111011/111011/110111/110111/110011/110011"),
        Map.entry('O',"01110/11011/11011/11011/11011/11011/01110"),
        Map.entry('P',"11110/11011/11011/11110/11000/11000/11000"),
        Map.entry('R',"11110/11011/11011/11110/11110/11011/11011"),
        Map.entry('S',"01111/11000/11000/01110/00011/00011/11110"),
        Map.entry('Ş',"01111/11000/01110/00011/00011/11110/01100"),
        Map.entry('T',"111111/001100/001100/001100/001100/001100/001100"),
        Map.entry('U',"11011/11011/11011/11011/11011/11011/01110"),
        Map.entry('V',"110011/110011/110011/110011/011110/011110/001100"),
        Map.entry('Y',"110011/110011/011110/001100/001100/001100/001100"),
        Map.entry('Z',"11111/00011/00110/01100/11000/11000/11111"),
        Map.entry('+',"00000/00100/00100/11111/00100/00100/00000"),
        Map.entry('i',"11/00/11/11/11/11/11"),
        Map.entry('l',"11/11/11/11/11/11/11"),
        Map.entry('k',"1100/1100/1101/1110/1110/1101/1101")
    );
    static BufferedImage badge(int index) {
        int textWidth=LABELS[index].chars().map(c->LETTERS.get((char)c).split("/")[0].length()+1).sum()-1;
        int width=17+textWidth;
        BufferedImage image=new BufferedImage(width*2,18,BufferedImage.TYPE_INT_ARGB);
        Graphics2D draw=image.createGraphics(); draw.scale(2,2);
        int accent=index==0?0x58bee8:COLORS[index];
        draw.setColor(new Color(0x11131d));draw.fillRect(1,0,width-2,9);draw.fillRect(0,1,width,7);
        draw.setColor(new Color(accent));draw.fillRect(2,0,width-4,1);draw.fillRect(2,8,width-4,1);draw.fillRect(0,2,1,5);draw.fillRect(width-1,2,1,5);
        draw.setColor(new Color(accent).darker().darker());draw.fillRect(1,1,width-2,7);
        draw.setColor(new Color(0x152638));draw.fillRect(12,2,width-14,5);
        draw.setColor(new Color(255,255,255,35));draw.fillRect(12,1,width-14,1);
        draw.setRenderingHint(RenderingHints.KEY_INTERPOLATION,RenderingHints.VALUE_INTERPOLATION_NEAREST_NEIGHBOR);
        draw.drawImage(icon(index),1,0,10,9,null);
        int cursor=14;
        for(int letter=0;letter<LABELS[index].length();letter++) {
            char character=LABELS[index].charAt(letter);
            String pattern=LETTERS.get(character);
            if(pattern==null) throw new IllegalArgumentException("Missing badge letter: "+character);
            String[] rows=pattern.split("/");int x=cursor, columns=rows[0].length();
            draw.setColor(new Color(0x080e18));
            for(int y=0;y<7;y++)for(int column=0;column<columns;column++)if(rows[y].charAt(column)=='1')draw.fillRect(x+column+1,y+2,1,1);
            draw.setColor(index==16?new Color(0xffeeb7):Color.WHITE);
            for(int y=0;y<7;y++)for(int column=0;column<columns;column++)if(rows[y].charAt(column)=='1')draw.fillRect(x+column,y+1,1,1);
            cursor+=columns+1;
        }
        draw.dispose();return image;
    }
    public static void main(String[] args) throws Exception {
        Path root=Path.of("resourcepack");
        Path textures=root.resolve("assets/tilkitags/textures/font");Files.createDirectories(textures);
        List<String> providers=new ArrayList<>();List<BufferedImage> icons=new ArrayList<>();
        for(int i=0;i<IDS.length;i++) {
            BufferedImage image=badge(i);icons.add(image);ImageIO.write(image,"png",textures.resolve(IDS[i]+".png").toFile());
            providers.add("{\"type\":\"bitmap\",\"file\":\"tilkitags:font/"+IDS[i]+".png\",\"height\":9,\"ascent\":8,\"chars\":[\"\\u"+String.format("%04x",0xeb00+i)+"\"]}");
        }
        write(root,"pack.mcmeta","{\"pack\":{\"pack_format\":46,\"description\":\"TilkiCraft - 17 original rank badges | 1.21.4\"}}\n");
        write(root,"assets/tilkitags/font/badges.json","{\"providers\":["+String.join(",",providers)+"]}\n");
        for(String font:List.of("default","uniform"))write(root,"assets/minecraft/font/"+font+".json","{\"providers\":[{\"type\":\"reference\",\"id\":\"tilkitags:badges\"}]}\n");
        Files.createDirectories(Path.of("dist"));Path zip=Path.of("dist/TilkiTags-ResourcePack.zip");
        try(var out=new ZipOutputStream(Files.newOutputStream(zip));var files=Files.walk(root)) {
            for(Path file:files.filter(Files::isRegularFile).sorted().toList()) {
                ZipEntry entry=new ZipEntry(root.relativize(file).toString().replace('\\','/'));entry.setTime(0);out.putNextEntry(entry);Files.copy(file,out);out.closeEntry();
            }
        }
        String hash=HexFormat.of().formatHex(MessageDigest.getInstance("SHA-1").digest(Files.readAllBytes(zip)));
        Files.writeString(Path.of("dist/resourcepack-sha1.txt"),hash+"\n");
        BufferedImage preview=new BufferedImage(1200,680,BufferedImage.TYPE_INT_RGB);g=preview.createGraphics();
        rect(0x181b23,0,0,1200,680);g.setFont(new Font("SansSerif",Font.BOLD,26));color(0xffb04e);g.drawString("TILKICRAFT • RÜTBE ROZETLERİ",30,44);
        g.setFont(new Font("SansSerif",Font.PLAIN,15));color(0xaab1c2);g.drawString("17 özgün piksel simge • Sohbet / TAB / Oyuncu üstü • Tasarım önizlemesi",30,72);
        for(int i=0;i<17;i++) {
            int x=25+i%4*295,y=100+i/4*110;rect(0x242936,x,y,280,96);rect(COLORS[i],x,y,3,96);
            g.drawImage(icons.get(i),x+12,y+13,icons.get(i).getWidth()*3/2,27,null);
            g.setFont(new Font("SansSerif",Font.PLAIN,15));color(0xdce0e8);g.drawString("Arda  »  Merhaba!",x+12,y+70);
        }
        g.dispose();Files.createDirectories(Path.of("art"));ImageIO.write(preview,"png",Path.of("art/tags-preview.png").toFile());
        System.out.println("Built 17 rank badges. SHA1: "+hash);
    }
    static void write(Path root,String path,String text) throws Exception {Path file=root.resolve(path);Files.createDirectories(file.getParent());Files.writeString(file,text);}
}
