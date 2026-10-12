package com.tilkicraft.tags;

import java.lang.reflect.Method;
import java.util.*;
import java.util.concurrent.*;
import net.luckperms.api.*;
import net.luckperms.api.model.user.User;
import net.luckperms.api.node.types.PermissionNode;
import net.luckperms.api.query.QueryOptions;
import org.bukkit.*;
import org.bukkit.command.*;
import org.bukkit.entity.Player;
import org.bukkit.event.*;
import org.bukkit.event.player.*;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scoreboard.*;

public final class TilkiTags extends JavaPlugin implements Listener, CommandExecutor, TabCompleter {
    private static final UUID PACK=UUID.fromString("70b41c61-5234-487b-a1fb-08c967cf3cd1");
    private LuckPerms luckPerms;
    private List<Rank> ranks=List.of();
    private final Set<UUID> loaded=ConcurrentHashMap.newKeySet();
    private final Map<UUID, Identity> identities=new ConcurrentHashMap<>();
    private final Map<UUID, String> oldTab=new HashMap<>(), lastTab=new HashMap<>();
    private final Map<Scoreboard,Set<String>> managed=new WeakHashMap<>();
    private final Set<String> conflicts=new HashSet<>();
    private volatile String chatTemplate;
    private volatile boolean chatEnabled;
    private String url;
    private byte[] hash;
    private Object essentials;
    private Method getUser, getNickname, placeholders;
    private boolean setupRunning;
    private record Identity(Rank rank,String nickname,String islandLevel) {}

    @Override public void onEnable() {
        saveDefaultConfig(); luckPerms=LuckPermsProvider.get();
        try { readSettings(); } catch (RuntimeException ex) { getLogger().severe(ex.getMessage()); Bukkit.getPluginManager().disablePlugin(this); return; }
        bindEssentials();
        Bukkit.getPluginManager().registerEvents(this,this);
        PluginCommand tagCommand=Objects.requireNonNull(getCommand("tilkitags"));tagCommand.setExecutor(this);tagCommand.setTabCompleter(this);
        Bukkit.getScheduler().runTaskTimer(this,this::refresh,1,10);
        for(Player player:Bukkit.getOnlinePlayers()) request(player);
    }
    private void readSettings() {
        String previousUrl=getConfig().getString("pack.url","");
        if (previousUrl.startsWith("https://github.com/ardaacala/TilkiTags/releases/download/") && !previousUrl.contains("/v1.3.4/")) {
            getConfig().set("pack.url","https://github.com/ardaacala/TilkiTags/releases/download/v1.3.4/TilkiTags-ResourcePack.zip");
            getConfig().set("pack.sha1","5d7e15795c1a75dc6e8736536901e4f297bb880c");
            saveConfig();
        }
        if (getConfig().isConfigurationSection("ranks.tilki") && !"&6&lTilki".equals(getConfig().getString("ranks.tilki.label"))) {
            getConfig().set("ranks.tilki.label","&6&lTilki"); saveConfig();
        }
        List<Rank> candidate=Rank.load(getConfig().getConfigurationSection("ranks"));
        String template=getConfig().getString("chat.format","{tag}{name}&8 » &f{message}");
        if (!template.contains("{message}") || !template.contains("{name}")) throw new IllegalArgumentException("chat.format needs {name} and {message}");
        String candidateUrl=getConfig().getString("pack.url",""); byte[] candidateHash=null;
        if (!candidateUrl.isBlank()) {
            candidateHash=HexFormat.of().parseHex(getConfig().getString("pack.sha1",""));
            if (!candidateUrl.startsWith("https://") || candidateHash.length!=20) throw new IllegalArgumentException("Pack requires HTTPS and 40-character SHA1");
        }
        ranks=candidate;chatTemplate=template;chatEnabled=getConfig().getBoolean("chat.enabled",true);url=candidateUrl;hash=candidateHash;
    }
    private void bindEssentials() {
        placeholders=null;
        var papi=Bukkit.getPluginManager().getPlugin("PlaceholderAPI");
        if (papi!=null && papi.isEnabled()) try {
            placeholders=Class.forName("me.clip.placeholderapi.PlaceholderAPI",true,papi.getClass().getClassLoader()).getMethod("setPlaceholders",OfflinePlayer.class,String.class);
        } catch (ReflectiveOperationException ex) { getLogger().warning("PlaceholderAPI hook unavailable; island level defaults to 0."); }
        essentials=Bukkit.getPluginManager().getPlugin("Essentials");
        if (essentials!=null) try {
            getUser=essentials.getClass().getMethod("getUser",UUID.class);
            getNickname=getUser.getReturnType().getMethod("getNickname");
        } catch (ReflectiveOperationException ex) { essentials=null; getLogger().warning("Essentials nickname hook unavailable; real names will be used."); }
    }
    private String islandLevel(Player player) {
        if(placeholders!=null) try {
            return Rank.cleanLevel((String)placeholders.invoke(null,player,getConfig().getString("island-level.placeholder","%superior_island_level_format%")));
        } catch(ReflectiveOperationException ignored) { }
        return "0";
    }
    private String identityPrefix(Identity identity,boolean icons) {
        return "§7"+identity.islandLevel()+" §8▪ "+identity.rank().prefix(icons);
    }
    private String nickname(Player player) {
        if (essentials!=null) try {
            Object user=getUser.invoke(essentials,player.getUniqueId());
            String nick=user==null?null:(String)getNickname.invoke(user);
            if (nick!=null && !nick.isBlank()) return Rank.whiteName(nick);
        } catch (ReflectiveOperationException ignored) { }
        return Rank.whiteName(player.getName());
    }
    private Rank resolve(Player player) {
        User user=luckPerms.getUserManager().getUser(player.getUniqueId());
        Set<String> memberships=new HashSet<>(), nodes=new HashSet<>();
        if (user!=null) {
            QueryOptions options=luckPerms.getContextManager().getQueryOptions(user).orElse(luckPerms.getContextManager().getStaticQueryOptions());
            user.getInheritedGroups(options).forEach(group->memberships.add(group.getName().toLowerCase(Locale.ROOT)));
            Set<String> exactNodes=new HashSet<>();
            user.resolveInheritedNodes(options).forEach(node->{ if(node instanceof PermissionNode permission) exactNodes.add(permission.getPermission()); });
            for(Rank rank:ranks) {
                String key="tilkitags.rank."+rank.id();
                if(exactNodes.contains(key) && user.getCachedData().getPermissionData(options).checkPermission(key).asBoolean()) nodes.add(rank.id());
            }
        }
        return Rank.choose(ranks,memberships,nodes);
    }
    /** A stable image badge, independent of other viewers loading their packs. */
    public String getRankBadge(Player subject,Player viewer) {
        Identity identity=identities.get(subject.getUniqueId());
        Rank rank=identity==null?resolve(subject):identity.rank();
        return rank.badge(true);
    }
    private void refresh() {
        List<Player> online=new ArrayList<>(Bukkit.getOnlinePlayers());
        Set<String> onlineNames=new HashSet<>();
        Map<Scoreboard,Boolean> boards=new IdentityHashMap<>();
        for(Player player:online) {
            UUID id=player.getUniqueId(); Rank rank=resolve(player);
            identities.put(id,new Identity(rank,nickname(player),islandLevel(player))); onlineNames.add(player.getName());
            if (getConfig().getBoolean("tab.enabled",true)) {
                oldTab.putIfAbsent(id,player.getPlayerListName());
                String value=identityPrefix(identities.get(id),true)+identities.get(id).nickname();
                if (!value.equals(player.getPlayerListName())) player.setPlayerListName(value);
                lastTab.put(id,value);
            }
            boards.merge(player.getScoreboard(),loaded.contains(id),(first,second)->first&&second);
        }
        if (!getConfig().getBoolean("nametag.enabled",true)) return;
        for(var viewed:boards.entrySet()) {
            Scoreboard board=viewed.getKey(); Set<String> owned=managed.computeIfAbsent(board,key->new HashSet<>());
            for(String name:new HashSet<>(owned)) {
                Team team=board.getTeam(name);
                if (team!=null) for(String entry:new HashSet<>(team.getEntries())) if(!onlineNames.contains(entry)) team.removeEntry(entry);
                if(team!=null && team.getEntries().isEmpty()) { team.unregister(); owned.remove(name); }
            }
            for(Player player:online) {
                Rank rank=identities.get(player.getUniqueId()).rank();
                Team previous=board.getEntryTeam(player.getName());
                if (previous!=null && !owned.contains(previous.getName()) && getConfig().getBoolean("nametag.respect-existing-teams",true)) {
                    if(conflicts.add(player.getName())) getLogger().warning("Another plugin owns "+player.getName()+"'s nametag team; keeping it. Disable the other nametag provider to use TilkiTags.");
                    continue;
                }
                String teamName="tt"+String.format(Locale.ROOT,"%04d%08x",9999-rank.priority(),player.getEntityId()); Team team=board.getTeam(teamName);
                if (team!=null && !owned.contains(teamName)) continue;
                if (team==null) { team=board.registerNewTeam(teamName); owned.add(teamName); }
                team.setPrefix(identityPrefix(identities.get(player.getUniqueId()),true)); team.setColor(ChatColor.WHITE);
                team.setOption(Team.Option.NAME_TAG_VISIBILITY,Team.OptionStatus.ALWAYS);
                if (!team.hasEntry(player.getName())) team.addEntry(player.getName());
            }
        }
    }
    private void request(Player player) {
        if(SharedPack.active()) { if(SharedPack.loaded(player)) loaded.add(player.getUniqueId()); return; }
        if(hash==null) return; UUID id=player.getUniqueId();
        Bukkit.getScheduler().runTaskLater(this,()->{
            Player current=Bukkit.getPlayer(id);
            if(!SharedPack.active() && current!=null && hash!=null) current.addResourcePack(PACK,url,hash,getConfig().getString("pack.prompt"),getConfig().getBoolean("pack.required",true));
        },Math.max(1,getConfig().getLong("pack.delay-ticks",140)));
    }
    @EventHandler public void join(PlayerJoinEvent event) { request(event.getPlayer()); }
    @EventHandler public void quit(PlayerQuitEvent event) {
        UUID id=event.getPlayer().getUniqueId(); loaded.remove(id); identities.remove(id); oldTab.remove(id); lastTab.remove(id); conflicts.remove(event.getPlayer().getName());
    }
    @EventHandler public void pack(PlayerResourcePackStatusEvent event) {
        if(!SharedPack.matches(PACK,event.getID())) return;
        if(event.getStatus()==PlayerResourcePackStatusEvent.Status.SUCCESSFULLY_LOADED) loaded.add(event.getPlayer().getUniqueId());
        else if(event.getStatus()!=PlayerResourcePackStatusEvent.Status.ACCEPTED && event.getStatus()!=PlayerResourcePackStatusEvent.Status.DOWNLOADED) loaded.remove(event.getPlayer().getUniqueId());
    }
    @EventHandler(priority=EventPriority.HIGH,ignoreCancelled=true)
    public void chat(AsyncPlayerChatEvent event) {
        if(!chatEnabled) return;
        Identity identity=identities.get(event.getPlayer().getUniqueId()); if(identity==null) return;
        boolean icons=true;
        event.setFormat(Rank.chatFormat(chatTemplate,identityPrefix(identity,icons),identity.nickname(),identity.rank().messageColor()));
    }
    @Override public boolean onCommand(CommandSender sender,Command command,String label,String[] args) {
        String action=args.length==0?"list":args[0].toLowerCase(Locale.ROOT);
        if(action.equals("list") || action.equals("preview")) {
            boolean icons=sender instanceof Player;
            for(Rank rank:ranks) sender.sendMessage(rank.prefix(icons)+"TilkiCraft");
            return true;
        }
        if(!sender.hasPermission("tilkitags.admin")) { sender.sendMessage("Yetkin yok."); return true; }
        if(action.equals("setup")) { setup(sender); return true; }
        if(action.equals("messagecolor")) {
            if(args.length!=3) { sender.sendMessage("Kullanım: /tilkitags messagecolor <tag> <renk>"); return true; }
            String rankId=args[1].toLowerCase(Locale.ROOT);
            if(ranks.stream().noneMatch(rank->rank.id().equals(rankId))) { sender.sendMessage("Bilinmeyen tag: "+rankId); return true; }
            String color=args[2].toUpperCase(Locale.ROOT);
            try { Rank.messageColor(color); }
            catch(IllegalArgumentException ex) { sender.sendMessage("Bilinmeyen renk. Örnek: ORANGE, RED, GOLD, GREEN, AQUA, WHITE"); return true; }
            getConfig().set("ranks."+rankId+".message-color",color);saveConfig();readSettings();
            sender.sendMessage(rankId+" sohbet mesajı rengi "+color+" olarak ayarlandı.");return true;
        }
        if(action.equals("reload")) {
            reloadConfig();
            try {
                readSettings(); cleanup(); loaded.clear();
                for(Player player:Bukkit.getOnlinePlayers()) request(player);
                sender.sendMessage("TilkiTags ayarları yenilendi.");
            } catch(RuntimeException ex) { sender.sendMessage("Ayar hatası: "+ex.getMessage()); }
            return true;
        }
        return false;
    }
    @Override public List<String> onTabComplete(CommandSender sender,Command command,String alias,String[] args) {
        if(args.length==1)return List.of("list","preview","setup","reload","messagecolor").stream().filter(value->value.startsWith(args[0].toLowerCase(Locale.ROOT))).toList();
        if(args.length==2 && args[0].equalsIgnoreCase("messagecolor"))return ranks.stream().map(Rank::id).filter(id->id.startsWith(args[1].toLowerCase(Locale.ROOT))).toList();
        if(args.length==3 && args[0].equalsIgnoreCase("messagecolor"))return java.util.stream.Stream.concat(java.util.stream.Stream.of("ORANGE"),Arrays.stream(ChatColor.values()).filter(ChatColor::isColor).map(Enum::name)).distinct().filter(name->name.startsWith(args[2].toUpperCase(Locale.ROOT))).toList();
        return List.of();
    }
    private void setup(CommandSender sender) {
        if(setupRunning) { sender.sendMessage("Kurulum zaten çalışıyor."); return; }
        setupRunning=true;
        List<CompletableFuture<Void>> work=new ArrayList<>();
        for(Rank rank:ranks) work.add(luckPerms.getGroupManager().createAndLoadGroup(rank.id()).thenCompose(group->{
            group.data().add(PermissionNode.builder("tilkitags.rank."+rank.id()).value(true).build());
            return luckPerms.getGroupManager().saveGroup(group);
        }));
        CompletableFuture.allOf(work.toArray(CompletableFuture[]::new)).whenComplete((unused,error)->{
            if(!isEnabled()) return;
            Bukkit.getScheduler().runTask(this,()->{
                setupRunning=false;
                sender.sendMessage(error==null?"17 kozmetik grup hazır. Atama: /lp user <oyuncu> parent add <grup>":"Bazı gruplar kaydedilemedi; konsolu kontrol et.");
                if(error!=null) getLogger().log(java.util.logging.Level.SEVERE,"LuckPerms setup failed",error);
            });
        });
        sender.sendMessage("Kozmetik gruplar hazırlanıyor; yönetici/oyun yetkisi eklenmez.");
    }
    private void cleanup() {
        for(var board:managed.entrySet()) for(String name:board.getValue()) { Team team=board.getKey().getTeam(name); if(team!=null) team.unregister(); }
        managed.clear(); conflicts.clear();
        for(Player player:Bukkit.getOnlinePlayers()) {
            UUID id=player.getUniqueId();
            if(lastTab.containsKey(id) && lastTab.get(id).equals(player.getPlayerListName())) player.setPlayerListName(oldTab.get(id));
        }
        oldTab.clear();lastTab.clear();
    }
    @Override public void onDisable() { cleanup(); identities.clear();loaded.clear(); }
}
