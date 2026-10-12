import java.awt.*;
import java.awt.image.BufferedImage;
import java.nio.file.*;
import java.security.MessageDigest;
import java.util.*;
import java.util.List;
import java.util.zip.*;
import javax.imageio.ImageIO;

/** Rank badges designed for nine-pixel text lines. */
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
        BufferedImage image=new BufferedImage(64,64,BufferedImage.TYPE_INT_ARGB);
        g=image.createGraphics();g.scale(4,4);int base=COLORS[index];
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
        g.dispose();image.setRGB(63,0,0x01ffffff);return image;
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
    static BufferedImage badge(int index) throws Exception {
        if(index==16 && Files.isRegularFile(Path.of("art/tilki-tag-source.png")))return customTilkiBadge(Path.of("art/tilki-tag-source.png"));
        Font font=Font.createFont(Font.TRUETYPE_FONT,Path.of("../TilkiInterface/resourcepack/assets/tilkiinterface/font/fonts/modern.ttf").toFile()).deriveFont(Font.BOLD,8f);
        var context=new java.awt.font.FontRenderContext(null,true,true);
        int width=18+(int)Math.ceil(font.getStringBounds(LABELS[index],context).getWidth());
        if(width*3>256) throw new IllegalStateException("HD badge exceeds atlas: "+LABELS[index]);
        BufferedImage image=new BufferedImage(width*3,27,BufferedImage.TYPE_INT_ARGB);
        Graphics2D draw=image.createGraphics();draw.scale(3,3);
        draw.setRenderingHint(RenderingHints.KEY_ANTIALIASING,RenderingHints.VALUE_ANTIALIAS_ON);
        draw.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING,RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
        draw.setRenderingHint(RenderingHints.KEY_FRACTIONALMETRICS,RenderingHints.VALUE_FRACTIONALMETRICS_ON);
        draw.setRenderingHint(RenderingHints.KEY_INTERPOLATION,RenderingHints.VALUE_INTERPOLATION_BICUBIC);
        int accent=index==0?0x58bee8:COLORS[index];
        draw.setColor(new Color(0x10151e));draw.fillRoundRect(0,0,width,9,3,3);
        draw.setColor(new Color(accent));draw.setStroke(new BasicStroke(.6f));draw.drawRoundRect(0,0,width-1,8,3,3);
        draw.setPaint(new GradientPaint(0,1,new Color(accent).darker().darker(),0,8,new Color(0x101c2b)));
        draw.fillRoundRect(1,1,width-2,7,2,2);draw.drawImage(icon(index),1,0,10,9,null);
        draw.setFont(font);draw.setColor(new Color(0x080e18));draw.drawString(LABELS[index],14.4f,7.5f);
        draw.setColor(index==16?new Color(0xffeeb7):Color.WHITE);draw.drawString(LABELS[index],14f,7.1f);
        draw.dispose();image.setRGB(0,0,0);image.setRGB(width*3-1,0,0x01ffffff);return image;
    }
    // A chat line is nine pixels; the rank badges fill exactly one line. The Tilki
    // badge keeps the reference artwork and is taller on purpose: it rises above its
    // line (bottom stays on the baseline) so the crowned fox stays readable.
    static final int LINE_HEIGHT=9,TILKI_HEIGHT=14,SHEET_OVERSAMPLE=5;
    /** Cuts the 17 stacked badges, in IDS order, out of the dark reference sheet. */
    static List<BufferedImage> sheetBadges(Path path) throws Exception {
        BufferedImage source=ImageIO.read(path.toFile());if(source==null)throw new IllegalArgumentException("Invalid badge sheet");
        List<int[]> rows=new ArrayList<>();int start=-1;
        for(int y=0;y<=source.getHeight();y++) {
            int lit=0;if(y<source.getHeight())for(int x=0;x<source.getWidth();x++)if(bright(source.getRGB(x,y)))lit++;
            if(lit>3&&start<0)start=y;
            if(lit<=3&&start>=0){rows.add(new int[]{start,y});start=-1;}
        }
        if(rows.size()!=IDS.length)throw new IllegalStateException("Expected "+IDS.length+" badges in sheet, found "+rows.size());
        List<BufferedImage> badges=new ArrayList<>();
        for(int[] row:rows) {
            int minX=source.getWidth(),maxX=0;
            for(int y=row[0];y<row[1];y++)for(int x=0;x<source.getWidth();x++)if(bright(source.getRGB(x,y))){minX=Math.min(minX,x);maxX=Math.max(maxX,x);}
            int top=Math.max(0,row[0]-3),bottom=Math.min(source.getHeight(),row[1]+3);minX=Math.max(0,minX-3);maxX=Math.min(source.getWidth()-1,maxX+3);
            BufferedImage cut=transparentBackground(source.getSubimage(minX,top,maxX-minX+1,bottom-top));
            badges.add(lineBadge(cut,badges.size()==16?TILKI_HEIGHT:LINE_HEIGHT));
        }
        return badges;
    }
    static BufferedImage customTilkiBadge(Path path) throws Exception {
        BufferedImage source=ImageIO.read(path.toFile());if(source==null)throw new IllegalArgumentException("Invalid Tilki badge PNG");
        return lineBadge(transparentBackground(source),TILKI_HEIGHT);
    }
    /** Scales trimmed artwork to the given logical height in HD and forces the full advance width. */
    static BufferedImage lineBadge(BufferedImage cut,int height) {
        int targetHeight=height*SHEET_OVERSAMPLE,targetWidth=Math.min(255,(int)Math.round(cut.getWidth()*(double)targetHeight/cut.getHeight()));
        BufferedImage badge=areaScale(cut,targetWidth,targetHeight);
        badge.setRGB(0,0,0);badge.setRGB(targetWidth-1,0,0x01ffffff);return badge;
    }
    static boolean bright(int pixel) {return Math.max((pixel>>16)&255,Math.max((pixel>>8)&255,pixel&255))>60;}
    /** Removes the background reachable from the edges and trims to the remaining artwork. */
    static BufferedImage transparentBackground(BufferedImage source) {
        int width=source.getWidth(),height=source.getHeight(),background=source.getRGB(0,0),br=(background>>16)&255,bg=(background>>8)&255,bb=background&255;
        boolean[] outside=new boolean[width*height];ArrayDeque<Point> queue=new ArrayDeque<>();
        for(int x=0;x<width;x++){queue.add(new Point(x,0));queue.add(new Point(x,height-1));}
        for(int y=0;y<height;y++){queue.add(new Point(0,y));queue.add(new Point(width-1,y));}
        while(!queue.isEmpty()) {
            Point point=queue.removeFirst();int x=point.x,y=point.y,index=y*width+x;if(outside[index])continue;
            int pixel=source.getRGB(x,y),r=(pixel>>16)&255,green=(pixel>>8)&255,b=pixel&255;
            if(Math.max(Math.max(Math.abs(r-br),Math.abs(green-bg)),Math.abs(b-bb))>30)continue;
            outside[index]=true;
            if(x>0)queue.add(new Point(x-1,y));if(x+1<width)queue.add(new Point(x+1,y));if(y>0)queue.add(new Point(x,y-1));if(y+1<height)queue.add(new Point(x,y+1));
        }
        int minX=width,minY=height,maxX=0,maxY=0;
        for(int y=0;y<height;y++)for(int x=0;x<width;x++)if(!outside[y*width+x]){minX=Math.min(minX,x);minY=Math.min(minY,y);maxX=Math.max(maxX,x);maxY=Math.max(maxY,y);}
        BufferedImage result=new BufferedImage(maxX-minX+1,maxY-minY+1,BufferedImage.TYPE_INT_ARGB);
        for(int y=minY;y<=maxY;y++)for(int x=minX;x<=maxX;x++)if(!outside[y*width+x])result.setRGB(x-minX,y-minY,source.getRGB(x,y)|0xff000000);
        return result;
    }
    /** Box-filter downscale with premultiplied alpha, so cut edges stay clean. */
    static BufferedImage areaScale(BufferedImage source,int width,int height) {
        BufferedImage result=new BufferedImage(width,height,BufferedImage.TYPE_INT_ARGB);
        double sx=source.getWidth()/(double)width,sy=source.getHeight()/(double)height;
        for(int y=0;y<height;y++)for(int x=0;x<width;x++) {
            double a=0,r=0,g=0,b=0,total=0;
            for(int yy=(int)(y*sy);yy<Math.min(source.getHeight(),Math.ceil((y+1)*sy));yy++) {
                double wy=Math.min(yy+1,(y+1)*sy)-Math.max(yy,y*sy);
                for(int xx=(int)(x*sx);xx<Math.min(source.getWidth(),Math.ceil((x+1)*sx));xx++) {
                    double weight=wy*(Math.min(xx+1,(x+1)*sx)-Math.max(xx,x*sx));int pixel=source.getRGB(xx,yy);double alpha=((pixel>>>24)/255.0)*weight;
                    a+=alpha;r+=((pixel>>16)&255)*alpha;g+=((pixel>>8)&255)*alpha;b+=(pixel&255)*alpha;total+=weight;
                }
            }
            int alpha=(int)Math.round(255*a/total);
            // The text shader discards nearly transparent pixels anyway.
            if(alpha<26){result.setRGB(x,y,0);continue;}
            result.setRGB(x,y,(alpha<<24)|((int)Math.round(r/a)<<16)|((int)Math.round(g/a)<<8)|(int)Math.round(b/a));
        }
        return result;
    }
    public static void main(String[] args) throws Exception {
        Path root=Path.of("resourcepack");
        Path textures=root.resolve("assets/tilkitags/textures/font");Files.createDirectories(textures);
        List<String> providers=new ArrayList<>();List<BufferedImage> icons=new ArrayList<>();
        Path sheet=Path.of("art/tags-source.jpg");
        List<BufferedImage> fromSheet=Files.isRegularFile(sheet)?sheetBadges(sheet):null;
        for(int i=0;i<IDS.length;i++) {
            Path texture=textures.resolve(IDS[i]+".png");
            BufferedImage image=fromSheet!=null?fromSheet.get(i):i==16||!Files.isRegularFile(texture)?badge(i):ImageIO.read(texture.toFile());
            icons.add(image);ImageIO.write(image,"png",texture.toFile());
            // Every badge fills exactly one chat line, so neighbouring lines never overlap.
            // Bottom on the line baseline (glyph top = baseline + 7 - ascent); extra height rises above.
            int height=i==16?TILKI_HEIGHT:LINE_HEIGHT;
            providers.add("{\"type\":\"bitmap\",\"file\":\"tilkitags:font/"+IDS[i]+".png\",\"height\":"+height+",\"ascent\":"+(height-1)+",\"chars\":[\"\\u"+String.format("%04x",0xeb00+i)+"\"]}");
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
        // Chat simulation at GUI scale 3: consecutive 27 px lines must not overlap.
        g.setRenderingHint(RenderingHints.KEY_INTERPOLATION,RenderingHints.VALUE_INTERPOLATION_BICUBIC);
        g.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING,RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
        for(int i=0;i<17;i++) {
            int x=30+i/9*590,y=100+i%9*LINE_HEIGHT*3,badgeHeight=(i==16?TILKI_HEIGHT:LINE_HEIGHT)*3,scaledWidth=icons.get(i).getWidth()*badgeHeight/icons.get(i).getHeight();
            g.setColor(new Color(0,0,0,i%2==0?120:90));g.fillRect(x-6,y,560,LINE_HEIGHT*3);
            g.setFont(new Font("SansSerif",Font.BOLD,19));color(0xaaaaaa);g.drawString("12 ▪",x,y+21);
            g.drawImage(icons.get(i),x+48,y+LINE_HEIGHT*3-badgeHeight,scaledWidth,badgeHeight,null);
            color(0xffffff);g.drawString("Arda",x+56+scaledWidth,y+21);color(0x555555);g.drawString("»",x+106+scaledWidth,y+21);color(0xffffff);g.drawString("Merhaba!",x+124+scaledWidth,y+21);
        }
        g.dispose();Files.createDirectories(Path.of("art"));ImageIO.write(preview,"png",Path.of("art/tags-preview.png").toFile());
        System.out.println("Built 17 rank badges. SHA1: "+hash);
    }
    static void write(Path root,String path,String text) throws Exception {Path file=root.resolve(path);Files.createDirectories(file.getParent());Files.writeString(file,text);}
}
