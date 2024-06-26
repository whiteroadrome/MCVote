package com.whiteroad;

import net.milkbowl.vault.economy.Economy;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.ChatColor;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.plugin.RegisteredServiceProvider;
import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.io.IOException;
import java.util.*;

public class Company extends JavaPlugin {
    private CP cp; // 기존 CP 클래스의 인스턴스를 저장하는 변수
    private Employee EM;

    public static class EconomyManager {
        private static Economy economy = null;

        public static boolean setupEconomy() {
            if (Bukkit.getServer().getPluginManager().getPlugin("Vault") == null) {
                return false;
            }
            RegisteredServiceProvider<Economy> rsp = Bukkit.getServer().getServicesManager().getRegistration(Economy.class);
            if (rsp == null) {
                return false;
            }
            economy = rsp.getProvider();
            return economy != null;
        }

        public static Economy getEconomy() {
            return economy;
        }
    }

    public static boolean checkMoney(Player player, double amount) {
        Economy economy = EconomyManager.getEconomy();
        if (economy != null) {
            return (amount <= economy.getBalance(player));
        }
        return false;
    }

    public static void giveMoney(Player player, double amount) {
        Economy economy = EconomyManager.getEconomy();
        if (economy != null) {
            economy.depositPlayer(player, amount);
        }
    }

    public static void takeMoney(Player player, double amount) {
        Economy economy = EconomyManager.getEconomy();
        if (economy != null) {
            economy.withdrawPlayer(player, amount);
        }
    }

    public static boolean stockBool = true;

    private static Company instance;

    public static Company getInstance() {
        return instance;
    }

    public static String weekValue = "0000000";

    @Override
    public void onEnable() {
        // 플러그인이 활성화될 때 실행되는 로직
        instance = this;
        if (!getDataFolder().exists()) {
            boolean result = getDataFolder().mkdirs();
            if (result) {
                getLogger().info("플러그인 폴더가 성공적으로 생성되었습니다.");
            } else {
                getLogger().severe("플러그인 폴더를 생성할 수 없습니다.");
            }
        }
        Bukkit.getConsoleSender().sendMessage(ChatColor.BLUE + "[Company]", ChatColor.WHITE + "VERSION:0.5 ON");
        Bukkit.getConsoleSender().sendMessage(ChatColor.RED + "[DANGER!]", ChatColor.WHITE + "Company is Alpha Version, Errors may occur!");
        cp = new CP(); // 기존 CP 클래스의 인스턴스 생성
        EM = new Employee();
        boolean isnewbool = false;
        EconomyManager.setupEconomy();
        getServer().getPluginManager().registerEvents(new EventListen(), this);

        File file = new File(instance.getDataFolder(), "PluginSetting.yml");
        if (!file.exists()) {
            try {
                isnewbool = file.createNewFile();
            } catch (IOException e) {
                e.fillInStackTrace(); // 오류 출력을 위해 변경했습니다.
            }
        }
        YamlConfiguration config = YamlConfiguration.loadConfiguration(file);

        if (isnewbool) {
            config.set("Plugin.Setting.Stock.Week","0000000");
            config.set("Plugin.Setting.Stock.Enable","false");
            config.set("Plugin.Setting.Stock.Multiply","10000");
            config.set("Plugin.Setting.Stock.Percentage","0.0005");
            // 기본 세팅 설정

            weekValue = config.getString("Plugin.Setting.Stock.Week");
            stockBool = Boolean.parseBoolean(config.getString("Plugin.Setting.Stock.Enable"));

            try {
                config.save(file);
            } catch (IOException e) {
                e.fillInStackTrace();
            }
        } else {
            weekValue = config.getString("Plugin.Setting.Stock.Week");
            stockBool = Boolean.parseBoolean(config.getString("Plugin.Setting.Stock.Enable"));
        }
        startRepeatingTask();
    }

    private void startRepeatingTask() {
        new BukkitRunnable() {
            @Override
            public void run() {
                Calendar calendar = Calendar.getInstance();
                if (Integer.parseInt("" + calendar.get(Calendar.SECOND)) == 0) {
                    Stock.Scalculate(0.0005);
                }
                if (Integer.parseInt("" + calendar.get(Calendar.HOUR_OF_DAY)) == 0) {
                    if (Integer.parseInt("" + calendar.get(Calendar.MINUTE)) == 0) {
                        if (Integer.parseInt("" + calendar.get(Calendar.SECOND)) == 0) {
                            CP.QuestReset();
                        }
                    }
                }
                int WeekDay = calendar.get(Calendar.DAY_OF_WEEK);

                if (weekValue.charAt(WeekDay) == '0') {
                    if (stockBool) {
                        stockBool = false;
                        Bukkit.broadcastMessage(ChatColor.YELLOW + "" + ChatColor.BOLD + "주식거래가 닫혔습니다!");
                    }
                }
                if (weekValue.charAt(WeekDay) == '1') {
                    if (!stockBool) {
                        stockBool = true;
                        Stock.Startcalc();
                        Bukkit.broadcastMessage(ChatColor.YELLOW + "" + ChatColor.BOLD + "주식시장이 오픈되었습니다!");
                    }
                }
            }
        }.runTaskTimer(this, 0L, 20L); // 0L은 지연 없이 즉시 시작, 20L은 20 ticks(1초)마다 반복
    }

    @Override
    public void onDisable() {
        // 플러그인이 비활성화될 때 실행되는 로직
        Bukkit.getConsoleSender().sendMessage(ChatColor.BLUE + "[Company]", ChatColor.WHITE + "VERSION:0.5 OFF");
    }

    @Override
    public boolean onCommand(@NotNull CommandSender sender, Command command, @NotNull String label, String[] args) {
        // 명령어 처리 로직을 작성
        if (command.getName().equals("company")) {
            if (args.length == 0) {
                SendMessage.help(sender);
                return false;
            }
            switch (args[0].toLowerCase()) {
                case "창설":
                    // 회사 창설 기능을 CP 클래스의 메서드로 전달
                    if (args.length > 1) {
                        cp.createCompany(sender, args[1]);
                    } else {
                        cp.createCompany(sender, null);
                    }
                    return true;
                case "폐업":
                    // 회사 삭제 기능을 CP 클래스의 메서드로 전달
                    if (args.length > 1) {
                        cp.deleteCompany(sender, args[1]);
                    } else {
                        cp.deleteCompany(sender, null);
                    }
                    return true;
                case "리스트":
                    if (args.length > 1) {
                        int integ;
                        try {
                            integ = Integer.parseInt(args[1]);
                        } catch(Exception e) {
                            sender.sendMessage("페이지 값은 정수여야 합니다.");
                            return false;
                        }
                        if (integ < 1) {
                            cp.getCompanyList(sender, "1");
                        } else {
                            cp.getCompanyList(sender, args[1]);
                        }
                    } else {
                        cp.getCompanyList(sender, "1");
                    }
                    return true;
                case "정보":
                    if (args.length > 1) {
                        cp.getCompanyInfo(sender, args[1]);
                    } else {
                        sender.sendMessage("추가 요소를 입력해주세요. [정보 (회사명)]");
                        return false;
                    }
                case "관리":
                    // 회사 정보 수정 기능을 CP 클래스의 메서드로 전달
                    if (args.length > 2) {
                        cp.updateCompanyInfo(sender, args[1], args[2]);
                    } else {
                        sender.sendMessage("추가 요소를 입력해주세요. [관리 (항목) (값)]");
                        return false;
                    }
                    return true;
                case "주식시장":
                    if (args.length < 2) {
                        sender.sendMessage("추가 오소를 입력해주세요. [켜기/끄기]");
                        return false;
                    }
                    Stock.Sdate(sender, args[1]);
                    return true;
                case "주식요일제":
                    if (args.length < 2) {
                        sender.sendMessage("추가 오소를 입력해주세요. [7자리의 2진수 값]");
                        return false;
                    }
                    Stock.Sweek(sender, args[1]);
                    return true;
                case "주식거래":
                    Stock.BSStock(sender, args[1], args[2]);
                    return true;
                case "일일업무":
                    CP.QuestOpen((Player) sender);
                    return true;
                case "입사목록":
                    EM.applyForJob(sender);
                    return true;
                case "입사신청":
                    if (args.length > 1) {
                        EM.receiptCompany(sender,args[1]);
                        return true;
                    } else {
                        sender.sendMessage("회사명을 입력해주세요!");
                        return false;
                    }
                case "gradeset":
                    EM.changePosition(sender,args[1],args[2]);
                    return true;
                case "fire":
                    EM.fireEmployee(sender,args[1]);
                    return  true;
                case "accept": // 빠른입력 X
                    EM.acceptApplication(sender,args[1]);
                    return  true;
                case "deny": // 빠른입력 X
                    EM.denyApplication(sender,args[1]);
                    return  true;
                case "t":
                    CP.QuestReset();
                    return true;
                case "t2":
                    sender.sendMessage(weekValue);
                    return true;
                default:
                    SendMessage.help(sender);
                    return false;
            }
        }
        // 다른 명령어들에 대한 처리 로직 추가
        return false;
    }
}
