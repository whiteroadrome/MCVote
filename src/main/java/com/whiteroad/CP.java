package com.whiteroad;

import net.md_5.bungee.api.ChatMessageType;
import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.HoverEvent;
import net.md_5.bungee.api.chat.hover.content.Text;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import net.milkbowl.vault.economy.Economy;
import org.bukkit.Material;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;
import net.md_5.bungee.api.chat.TextComponent;

import java.io.File;
import java.io.IOException;
import java.util.*;
import java.util.List;
import java.util.Random;

public class CP {
    // 회사 관리 기능을 구현하는 클래스

    Plugin plugin = Company.getInstance();

    public static void renameKey(YamlConfiguration config, String oldKey, String newKey) {
        // 기존 키의 값을 가져옵니다.
        Object value = config.get(oldKey);
        if (value == null) {
            return;
        }

        if (oldKey.equals(newKey)) {
            return;
        }

        // 새 키에 기존 키의 값을 할당합니다.
        config.set(newKey, value);
        // 기존 키를 삭제합니다.
        config.set(oldKey, null);
    }

    // 회사 생성 기능을 구현하는 메서드
    public void createCompany(CommandSender sender, String cpname) {
        if (cpname != null) {
            File file = new File(plugin.getDataFolder(), "CompanyData.yml");

            if (!file.exists()) {
                try {
                    file.createNewFile();
                } catch (IOException e) {
                    e.fillInStackTrace(); // 오류 출력을 위해 변경했습니다.
                }
            }

            Player player = (Player) sender;
            YamlConfiguration config = YamlConfiguration.loadConfiguration(file);

            config.set("Data.View", "true");

            ConfigurationSection section = config.getConfigurationSection("Data.Company");

            boolean isMember = false;

            if (section != null) {
                for (String subKey : section.getKeys(false)) {
                    if (config.getConfigurationSection("Data.Company." + subKey + ".member") != null) {
                        for (String muid : Objects.requireNonNull(config.getConfigurationSection("Data.Company." + subKey + ".member")).getKeys(false)) {
                            if (Objects.equals(muid, player.getUniqueId().toString())) {
                                isMember = true;
                            }
                        }
                    }
                }
            }

            if (!isMember) {
                Calendar calendar = Calendar.getInstance();

                String nowdate = calendar.get(Calendar.YEAR) + "-" + calendar.get(Calendar.MONTH) + "-" + calendar.get(Calendar.DAY_OF_MONTH);

                config.set("Data.Company." + cpname + ".member." + ((Player) sender).getUniqueId() + ".grade", "boss");
                config.set("Data.Company." + cpname + ".member." + ((Player) sender).getUniqueId() + ".joinDate", nowdate);

                config.set("Data.Company." + cpname + ".Stock.toggle", "false");
                config.set("Data.Company." + cpname + ".Stock.value", "10000");
                config.set("Data.Company." + cpname + ".Stock.default", "10000");
                config.set("Data.Company." + cpname + ".Stock.buyc", "0");
                config.set("Data.Company." + cpname + ".Stock.sellc", "1000");
                config.set("Data.Company." + cpname + ".Stock.Percent.Buy", "0");
                config.set("Data.Company." + cpname + ".Stock.Percent.Sell", "0");

                config.set("Data.Company." + cpname + ".createDate", nowdate);
                config.set("Data.Company." + cpname + ".cType", "None");
                config.set("Data.Company." + cpname + ".cTypeIsChangeToday", "false");
                config.set("Data.Company." + cpname + ".MoneyBank", "0");
                config.set("Data.Company." + cpname + ".QuestClear", "0");
                config.set("Data.Company." + cpname + ".MemberCount", "1");

                config.set("Data.Company." + cpname + ".MemberR.IsEnable", "false");

                try {
                    config.save(file);
                } catch (IOException e) {
                    e.fillInStackTrace();
                }

                // 회사 생성 로직
                sender.sendMessage(ChatColor.GREEN + "당신의 회사 [" + cpname + "] 기업이 성공적으로 설립되었습니다!");
            } else {
                sender.sendMessage(ChatColor.RED + "당신은 이미 회사에 속한 상태입니다.");
            }
        } else {
            sender.sendMessage("회사명을 입력해주세요. [/company 창설 회사이름]");
        }
    }

    // 회사 삭제 기능을 구현하는 메서드
    public void deleteCompany(CommandSender sender, String cpnamecheck) {
        // 회사 삭제 로직
        if (cpnamecheck != null) {
            File file = new File(plugin.getDataFolder(), "CompanyData.yml");

            if (!file.exists()) {
                try {
                    file.createNewFile();
                } catch (IOException e) {
                    e.fillInStackTrace(); // 오류 출력을 위해 변경했습니다.
                }
            }

            YamlConfiguration config = YamlConfiguration.loadConfiguration(file);

            config.set("Data.View", "true");

            ConfigurationSection section = config.getConfigurationSection("Data.Company");

            boolean isboss = false;
            String cpname = "";

            if (section != null) {
                for (String subKey : section.getKeys(false)) {
                    if (config.getConfigurationSection("Data.Company." + subKey + ".member") != null) {
                        if (config.get("Data.Company." + subKey + ".member." + ((Player) sender).getUniqueId()) != null) {
                            if (Objects.equals(Objects.requireNonNull(config.get("Data.Company." + subKey + ".member." + ((Player) sender).getUniqueId() + ".grade")).toString(), "boss")) {
                                cpname = subKey;
                                isboss = true;
                            }
                        }
                    }
                }
            }

            if (!isboss) {
                sender.sendMessage(ChatColor.RED + "당신은 회사를 소유하고 있지 않습니다.");
            } else if (cpnamecheck.equals(cpname)) {
                config.set("Data.Company." + cpname, null);
                try {
                    config.save(file);
                } catch (IOException e) {
                    e.fillInStackTrace();
                }
                sender.sendMessage(ChatColor.GREEN + "폐업을 진행하였습니다.");
            } else {
                sender.sendMessage(ChatColor.RED + "회사명이 올바르지 않습니다.");
            }
        } else {
            sender.sendMessage("회사를 폐업하려면 회사명을 입력해주세요. [/company 폐업 회사이름]");
        }
    }

    // 회사 정보 조회 기능을 구현하는 메서드
    public void getCompanyList(CommandSender sender, String num) {
        File file = new File(plugin.getDataFolder(), "CompanyData.yml");

        if (!file.exists()) {
            try {
                file.createNewFile();
            } catch (IOException e) {
                e.fillInStackTrace(); // 오류 출력을 위해 변경했습니다.
            }
        }

        int pagenum = Integer.parseInt(num) - 1;
        YamlConfiguration config = YamlConfiguration.loadConfiguration(file);

        config.set("Data.View", "true");

        ConfigurationSection section = config.getConfigurationSection("Data.Company");

        if (section == null) {
            sender.sendMessage("회사가 하나도 없습니다...");
            return;
        }

        List<String> cplist = List.copyOf(section.getKeys(false));

        sender.sendMessage(ChatColor.YELLOW + "" + ChatColor.BOLD + "회사 목록 [" + ChatColor.AQUA + (pagenum + 1) + ChatColor.YELLOW + ChatColor.BOLD + "페이지]");

        sender.sendMessage(" ");

        for (int i = 0; i < 10; i++) {
            String cpnv;

            if (cplist.size() <= i + (pagenum * 10)) {
                break;
            }

            cpnv = cplist.get(i + (pagenum * 10));

            TextComponent message = new TextComponent(ChatColor.BOLD + "[" + cpnv + "]");

            message.setClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, ("/company 정보 " + cpnv)));

            message.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new Text("클릭하여 정보 확인")));

            sender.sendMessage(message);
        }
    }

    public void getCompanyInfo(CommandSender sender, String cpname) {
        //회사 정보 확인 로직
        File file = new File(plugin.getDataFolder(), "CompanyData.yml");

        if (!file.exists()) {
            try {
                file.createNewFile();
            } catch (IOException e) {
                e.fillInStackTrace(); // 오류 출력을 위해 변경했습니다.
            }
        }

        YamlConfiguration config = YamlConfiguration.loadConfiguration(file);

        config.set("Data.View", "true");

        if (config.get("Data.Company." + cpname) == null) {
            sender.sendMessage("존재하지 않는 회사입니다.");
        } else {
            sender.sendMessage("" + ChatColor.GREEN + ChatColor.BOLD + cpname + " 기업의 정보");

            sender.sendMessage(" ");

            int membercount = List.copyOf(config.getConfigurationSection("Data.Company." + cpname + ".member").getKeys(false)).size();
            String createdate = config.getString("Data.Company." + cpname + ".createDate");

            String boss = "";

            if (config.getConfigurationSection("Data.Company." + cpname + ".member") != null) {
                for (String muid : Objects.requireNonNull(config.getConfigurationSection("Data.Company." + cpname + ".member")).getKeys(false)) {
                    if (Objects.equals(config.get("Data.Company." + cpname + ".member." + muid + ".grade").toString(), "boss")) {
                        boss = Bukkit.getPlayer(UUID.fromString(muid)).getName();
                    }
                }
            }

            sender.sendMessage("총 인원 수 : " + ChatColor.AQUA + membercount);
            sender.sendMessage("설립일자 : " + ChatColor.AQUA + createdate);
            sender.sendMessage("대표이사 : " + ChatColor.AQUA + boss);

            sender.sendMessage(" ");
        }
    }

    // 회사 정보 수정 기능을 구현하는 메서드
    public void updateCompanyInfo(CommandSender sender, String type, String value) {
        // 회사 정보 수정 로직

        File file = new File(plugin.getDataFolder(), "CompanyData.yml");

        if (!file.exists()) {
            try {
                file.createNewFile();
            } catch (IOException e) {
                e.fillInStackTrace(); // 오류 출력을 위해 변경했습니다.
            }
        }

        YamlConfiguration config = YamlConfiguration.loadConfiguration(file);

        config.set("Data.View", "true");

        ConfigurationSection section = config.getConfigurationSection("Data.Company");

        boolean isboss = false;
        String cpname = "";
        String grade = "";

        if (section != null) {
            for (String subKey : section.getKeys(false)) {
                if (config.getConfigurationSection("Data.Company." + subKey + ".member") != null) {
                    if (config.get("Data.Company." + subKey + ".member." + ((Player) sender).getUniqueId()) != null) {
                        if (Objects.equals(Objects.requireNonNull(config.get("Data.Company." + subKey + ".member." + ((Player) sender).getUniqueId() + ".grade")).toString(), "boss")) {
                            cpname = subKey;
                            isboss = true;
                            grade = config.getString("Data.Company." + subKey + ".member." + ((Player) sender).getUniqueId() + ".grade");
                        }
                    }
                }
            }
        }


        switch (type) {
            case "회사명":
                if (isboss) {
                    renameKey(config, "Data.Company." + cpname, "Data.Company." + value);
                    sender.sendMessage("회사명을 [" + value + "] 로 변경하였습니다.");
                } else {
                    sender.sendMessage(ChatColor.RED + "해당 작업을 수행할 권한이 없습니다.");
                }
                break;
            case "직종":
                if (isboss) {
                    switch (value) {
                        case "농업":
                            if (!util.removeTicketNBT((Player) sender, Material.PAPER,-1)) {
                                sender.sendMessage(ChatColor.RED + "[회사 직종 변경권] 을 소지하고 있지 않습니다.");
                                break;
                            }
                            config.set("Data.Company." + cpname + ".cType", "farm");
                            sender.sendMessage("회사 업종을 [농업]으로 수정하였습니다.");
                            config.set("Data",QuestLoopLogic(config,cpname));
                            config.set("Data.Company." + cpname + ".cTypeIsChangeToday","true");
                            break;
                        case "어업":
                            if (!util.removeTicketNBT((Player) sender, Material.PAPER,-1)) {
                                sender.sendMessage(ChatColor.RED + "[회사 직종 변경권] 을 소지하고 있지 않습니다.");
                                break;
                            }
                            config.set("Data.Company." + cpname + ".cType", "fish");
                            sender.sendMessage("회사 업종을 [어업]으로 수정하였습니다.");
                            config.set("Data",QuestLoopLogic(config,cpname));
                            config.set("Data.Company." + cpname + ".cTypeIsChangeToday","true");
                            break;
                        case "광업":
                            if (!util.removeTicketNBT((Player) sender, Material.PAPER,-1)) {
                                sender.sendMessage(ChatColor.RED + "[회사 직종 변경권] 을 소지하고 있지 않습니다.");
                                break;
                            }
                            config.set("Data.Company." + cpname + ".cType", "mine");
                            sender.sendMessage("회사 업종을 [광업]으로 수정하였습니다.");
                            config.set("Data",QuestLoopLogic(config,cpname));
                            config.set("Data.Company." + cpname + ".cTypeIsChangeToday","true");
                            break;
                        case "수렵업":
                            if (!util.removeTicketNBT((Player) sender, Material.PAPER,-1)) {
                                sender.sendMessage(ChatColor.RED + "[회사 직종 변경권] 을 소지하고 있지 않습니다.");
                                break;
                            }
                            config.set("Data.Company." + cpname + ".cType", "hunt");
                            sender.sendMessage("회사 업종을 [수렵업]으로 수정하였습니다.");
                            config.set("Data",QuestLoopLogic(config,cpname));
                            config.set("Data.Company." + cpname + ".cTypeIsChangeToday","true");
                            break;
                        default:
                            sender.sendMessage("올바른 업종을 입력해주세요.");
                            break;
                    }
                } else {
                    sender.sendMessage(ChatColor.RED + "해당 작업을 수행할 권한이 없습니다.");
                }
                break;
            case "주식시장":
                if (grade.equals("director") || grade.equals("master") || grade.equals("vice") || grade.equals("boss")) {
                    switch (value) {
                        case "열기":
                            config.set("Data.Company." + cpname + ".Stock.toggle", "true");
                            sender.sendMessage("회사 주식 시장을 열었습니다.");
                            break;
                        case "닫기":
                            config.set("Data.Company." + cpname + ".Stock.toggle", "false");
                            sender.sendMessage("회사 주식 시장을 닫았습니다.");
                            break;
                        default:
                            sender.sendMessage("올바른 값을 입력해주세요.");
                            break;
                    }
                } else {
                    sender.sendMessage(ChatColor.RED + "해당 작업을 수행할 권한이 없습니다.");
                }
                break;
            case "월급":
                if (grade.equals("vice") || grade.equals("boss")) {
                    config.set("Data.Company." + cpname + ".Salary",value);
                } else {
                    sender.sendMessage(ChatColor.RED + "해당 작업을 수행할 권한이 없습니다.");
                }
            default:
                sender.sendMessage("올바른 항목을 입력해주세요.");
                break;
        }
        try {
            config.save(file);
        } catch (IOException e) {
            e.fillInStackTrace();
        }
    }

    public static void CompanyMoneyManage(CommandSender sender, String bool, String value) {
        Plugin plugin = Company.getInstance();

        File file = new File(plugin.getDataFolder(), "CompanyData.yml");

        if (!file.exists()) {
            try {
                file.createNewFile();
            } catch (IOException e) {
                e.fillInStackTrace(); // 오류 출력을 위해 변경했습니다.
            }
        }

        Player player = (Player) sender;
        YamlConfiguration config = YamlConfiguration.loadConfiguration(file);

        config.set("Data.View", "true");

        ConfigurationSection section = config.getConfigurationSection("Data.Company");

        boolean isMember = false;
        String pcom = "";
        String grade = "";

        if (section != null) {
            for (String subKey : section.getKeys(false)) {
                if (config.getConfigurationSection("Data.Company." + subKey + ".member") != null) {
                    for (String muid : Objects.requireNonNull(config.getConfigurationSection("Data.Company." + subKey + ".member")).getKeys(false)) {
                        if (Objects.equals(muid, player.getUniqueId().toString())) {
                            isMember = true;
                            pcom = subKey;
                            grade = config.getString("Data.Company." + subKey + ".member." + ((Player) sender).getUniqueId() + ".grade");
                        }
                    }
                }
            }
        }

        if (grade.equals("vice") || grade.equals("boss")) {
            if (bool.equals("입금")) {
                if (Company.checkMoney(player,Integer.parseInt(value))) {
                    config.set("Data.Company." + pcom + ".Salary", (Integer.parseInt(config.getString("Data.Company." + pcom + ".Salary")) + Integer.parseInt(value)));
                    Economy economy = Company.EconomyManager.getEconomy();
                    Company.takeMoney(player, Integer.parseInt(value));
                    sender.sendMessage(ChatColor.GREEN + "입금이 완료되었습니다!");
                } else {
                    sender.sendMessage(ChatColor.RED + "충분한 돈을 가지고 있지 않습니다.");
                }
            } else if (bool.equals("출금")) {
                if ((Integer.parseInt(config.getString("Data.Company." + pcom + ".Salary")) - Integer.parseInt(value)) >= 0) {
                    config.set("Data.Company." + pcom + ".Salary", (Integer.parseInt(config.getString("Data.Company." + pcom + ".Salary")) - Integer.parseInt(value)));
                    Economy economy = Company.EconomyManager.getEconomy();
                    Company.giveMoney(player, Integer.parseInt(value));
                    sender.sendMessage(ChatColor.GREEN + "출금이 완료되었습니다!");
                } else {
                    sender.sendMessage(ChatColor.RED + "법인계좌의 잔액이 부족합니다.");
                }
            }
        } else {
            sender.sendMessage(ChatColor.RED + "해당 작업을 수행할 권한이 없습니다.");
        }
    }

    public static void QuestCheckKill(Player player, String type) {
        Plugin plugin = Company.getInstance();

        File file = new File(plugin.getDataFolder(), "CompanyData.yml");

        if (!file.exists()) {
            try {
                file.createNewFile();
            } catch (IOException e) {
                e.fillInStackTrace(); // 오류 출력을 위해 변경했습니다.
            }
        }

        YamlConfiguration config = YamlConfiguration.loadConfiguration(file);

        config.set("Data.View", "true");

        ConfigurationSection section = config.getConfigurationSection("Data.Company");

        boolean isMember = false;
        String pcom = "";

        if (section != null) {
            for (String subKey : section.getKeys(false)) {
                if (config.getConfigurationSection("Data.Company." + subKey + ".member") != null) {
                    for (String muid : Objects.requireNonNull(config.getConfigurationSection("Data.Company." + subKey + ".member")).getKeys(false)) {
                        if (Objects.equals(muid, player.getUniqueId().toString())) {
                            isMember = true;
                            pcom = subKey;
                        }
                    }
                }
            }
        }

        if (isMember) {
            String qType = config.getString("Data.Company."+pcom+".member." + player.getUniqueId().toString() + ".quest.id");
            if (type.equals(qType)) {
                if (Objects.equals(config.getString("Data.Company." + pcom + ".member." + player.getUniqueId().toString() + ".quest.count"), "")) {
                    config.set("Data.Company."+pcom+".member." + player.getUniqueId().toString() + ".quest.count","0");
                }
                int defcount = Integer.parseInt(config.getString("Data.Company."+pcom+".member." + player.getUniqueId().toString() + ".quest.count"));
                config.set("Data.Company."+pcom+".member." + player.getUniqueId().toString() + ".quest.count","" + (defcount+1));
                try {
                    config.save(file);
                } catch (IOException e) {
                    e.fillInStackTrace();
                }
            }
        }
    }

    public static void QuestCheckItem(Player player, String type, Integer ecount) {
        Plugin plugin = Company.getInstance();

        File file = new File(plugin.getDataFolder(), "CompanyData.yml");

        if (!file.exists()) {
            try {
                file.createNewFile();
            } catch (IOException e) {
                e.fillInStackTrace(); // 오류 출력을 위해 변경했습니다.
            }
        }

        YamlConfiguration config = YamlConfiguration.loadConfiguration(file);

        config.set("Data.View", "true");

        ConfigurationSection section = config.getConfigurationSection("Data.Company");

        boolean isMember = false;
        String pcom = "";

        if (section != null) {
            for (String subKey : section.getKeys(false)) {
                if (config.getConfigurationSection("Data.Company." + subKey + ".member") != null) {
                    for (String muid : Objects.requireNonNull(config.getConfigurationSection("Data.Company." + subKey + ".member")).getKeys(false)) {
                        if (Objects.equals(muid, player.getUniqueId().toString())) {
                            isMember = true;
                            pcom = subKey;
                        }
                    }
                }
            }
        }

        if (isMember) {
            String qType = config.getString("Data.Company."+pcom+".member." + player.getUniqueId().toString() + ".quest.id");
            if (type.equals(qType)) {
                if (Objects.equals(config.getString("Data.Company." + pcom + ".member." + player.getUniqueId().toString() + ".quest.count"), "")) {
                    config.set("Data.Company."+pcom+".member." + player.getUniqueId().toString() + ".quest.count","0");
                }
                int defcount = Integer.parseInt(config.getString("Data.Company."+pcom+".member." + player.getUniqueId().toString() + ".quest.count"));
                int maxcount = Integer.parseInt(config.getString("Data.Company."+pcom+".member." + player.getUniqueId().toString() + ".quest.maxcount"));
                if ((defcount + ecount) >= maxcount) {
                    config.set("Data.Company."+pcom+".member." + player.getUniqueId().toString() + ".quest.count","" + (maxcount));
                    QuestActionbar(player,-1);
                } else {
                    config.set("Data.Company." + pcom + ".member." + player.getUniqueId().toString() + ".quest.count", "" + (defcount + ecount));
                    QuestActionbar(player,defcount + ecount);
                }
                try {
                    config.save(file);
                } catch (IOException e) {
                    e.fillInStackTrace();
                }
            }
        }
    }

    public static void QuestOpen(Player player) {
        Plugin plugin = Company.getInstance();
        Random random = new Random();

        File file = new File(plugin.getDataFolder(), "CompanyData.yml");

        if (!file.exists()) {
            try {
                file.createNewFile();
            } catch (IOException e) {
                e.fillInStackTrace(); // 오류 출력을 위해 변경했습니다.
            }
        }

        YamlConfiguration config = YamlConfiguration.loadConfiguration(file);

        config.set("Data.View", "true");

        ConfigurationSection section = config.getConfigurationSection("Data.Company");

        boolean isMember = false;
        String pcom = "";

        if (section != null) {
            for (String subKey : section.getKeys(false)) {
                if (config.getConfigurationSection("Data.Company." + subKey + ".member") != null) {
                    for (String muid : Objects.requireNonNull(config.getConfigurationSection("Data.Company." + subKey + ".member")).getKeys(false)) {
                        if (Objects.equals(muid, player.getUniqueId().toString())) {
                            String qtxt = "";
                            if (config.get("Data.Company." + subKey + ".member." + player.getUniqueId() + ".quest.id") == null) {
                                player.sendMessage("아직 업무가 주어지지 않았습니다!");
                                config.set("Data",QuestRLogic(config,subKey,player.getUniqueId().toString()));
                                try {
                                    config.save(file);
                                } catch (IOException e) {
                                    e.fillInStackTrace();
                                }
                            }
                            switch (config.getString("Data.Company." + subKey + ".member." + player.getUniqueId() + ".quest.id")) {
                                case "wheat_farm":
                                    qtxt = "밀을 수확하세요.";
                                    break;
                                case "beet_farm":
                                    qtxt = "비트를 수확하세요.";
                                    break;
                                case "potato_farm":
                                    qtxt = "감자를 수확하세요.";
                                    break;
                                case "carrot_farm":
                                    qtxt = "당근을 수확하세요.";
                                    break;
                                case "cobble_mine":
                                    qtxt = "조약돌을 채굴하세요.";
                                    break;
                                case "coal_mine":
                                    qtxt = "석탄을 채굴하세요.";
                                    break;
                                case "iron_mine":
                                    qtxt = "철광석을 채굴하세요.";
                                    break;
                                case "gold_mine":
                                    qtxt = "금광석을 채굴하세요.";
                                    break;
                                case "diamond_mine":
                                    qtxt = "다이아몬드를 채굴하세요.";
                                    break;
                                case "emerald_mine":
                                    qtxt = "에메랄드를 채굴하세요.";
                                    break;
                                case "cow_hunt":
                                    qtxt = "소를 사냥하세요.";
                                    break;
                                case "pig_hunt":
                                    qtxt = "돼지를 사냥하세요.";
                                    break;
                                case "rabbit_hunt":
                                    qtxt = "토끼를 사냥하세요.";
                                    break;
                                case "leather_hunt":
                                    qtxt = "사냥으로 가죽을 획득하세요.";
                                    break;
                                case "meat_hunt":
                                    qtxt = "사냥으로 고기를 획득하세요.";
                                    break;
                                case "enemy_hunt":
                                    qtxt = "몬스터를 처치하세요.";
                                    break;
                                case "salmon_fish":
                                    qtxt = "낚시로 연어를 낚으세요.";
                                    break;
                                case "cod_fish":
                                    qtxt = "낚시로 대구를 낚으세요.";
                                    break;
                                case "puffer_fish":
                                    qtxt = "낚시로 복어를 낚으세요.";
                                    break;
                            }

                            String count;
                            if (Objects.equals(config.getString("Data.Company." + pcom + ".member." + player.getUniqueId().toString() + ".quest.count"), "")) {
                                config.set("Data.Company."+pcom+".member." + player.getUniqueId().toString() + ".quest.count","0");
                            }
                            count = config.getString("Data.Company." + subKey + ".member." + player.getUniqueId() + ".quest.count");
                            player.sendMessage(ChatColor.AQUA + "- 오늘의 일일 업무 -");
                            player.sendMessage(TextComponent.fromLegacyText(ChatColor.YELLOW + qtxt + " [" + count + "/" + config.getString("Data.Company." + subKey + ".member." + player.getUniqueId().toString() + ".quest.maxcount") + "]"));
                        }
                    }
                }
            }
        }
    }

    public static Object QuestLoopLogic(YamlConfiguration config, String subKey) {
        for (String muid : Objects.requireNonNull(config.getConfigurationSection("Data.Company." + subKey + ".member")).getKeys(false)) {
            config.set("Data",QuestRLogic(config,subKey,muid));
        }
        return config.get("Data");
    }

    public static Object QuestRLogic(YamlConfiguration config, String subKey, String muid) {
        Random random = new Random();
        List<String> questList = new ArrayList<>();
        List<Integer> questminrange = new ArrayList<>();
        List<Integer> questmaxrange = new ArrayList<>();
        int qlCount = 0;
        if (Objects.equals(config.getString("Data.Company." + subKey + ".cType"), "farm")) {
            questList.add("wheat_farm");
            questList.add("beet_farm");
            questList.add("potato_farm");
            questList.add("carrot_farm");
            questminrange.add(32);
            questminrange.add(64);
            questminrange.add(64);
            questminrange.add(64);
            questmaxrange.add(64);
            questmaxrange.add(128);
            questmaxrange.add(128);
            questmaxrange.add(128);
            qlCount = 4;
        } else if (Objects.equals(config.getString("Data.Company." + subKey + ".cType"), "mine")) {
            questList.add("cobble_mine");
            questList.add("coal_mine");
            questList.add("iron_mine");
            questList.add("gold_mine");
            questList.add("diamond_mine");
            questList.add("emerald_mine");
            questminrange.add(64);
            questminrange.add(32);
            questminrange.add(32);
            questminrange.add(32);
            questminrange.add(20);
            questminrange.add(10);
            questmaxrange.add(128);
            questmaxrange.add(64);
            questmaxrange.add(64);
            questmaxrange.add(64);
            questmaxrange.add(30);
            questmaxrange.add(20);
            qlCount = 6;
        } else if (Objects.equals(config.getString("Data.Company." + subKey + ".cType"), "hunt")) {
            questList.add("leather_hunt");
            questList.add("meat_hunt");
            questList.add("enemy_hunt");
            questList.add("cow_hunt");
            questList.add("pig_hunt");
            questList.add("rabbit_hunt");
            questminrange.add(16);
            questminrange.add(20);
            questminrange.add(20);
            questminrange.add(10);
            questminrange.add(10);
            questminrange.add(4);
            questmaxrange.add(32);
            questmaxrange.add(40);
            questmaxrange.add(30);
            questmaxrange.add(20);
            questmaxrange.add(20);
            questmaxrange.add(8);
            qlCount = 6;
        } else if (Objects.equals(config.getString("Data.Company." + subKey + ".cType"), "fish")) {
            questList.add("salmon_fish");
            questList.add("cod_fish");
            questList.add("puffer_fish");
            questminrange.add(10);
            questminrange.add(10);
            questminrange.add(5);
            questmaxrange.add(20);
            questmaxrange.add(20);
            questmaxrange.add(10);
            qlCount = 3;
        }
        if (qlCount == 0) {
            return config.get("Data");
        }
        int randomql = random.nextInt(qlCount);
        int randomcount = random.nextInt(questmaxrange.get(randomql)-questminrange.get(randomql) + 1)+questminrange.get(randomql);
        config.set("Data.Company." + subKey + ".member." + muid + ".quest.id",questList.get(randomql));
        config.set("Data.Company." + subKey + ".member." + muid + ".quest.maxcount",(""+randomcount));
        config.set("Data.Company." + subKey + ".member." + muid + ".quest.count","0");
        return config.get("Data");
    }

    public static void QuestActionbar(Player player, Integer count) {
        Random random = new Random();

        Plugin plugin = Company.getInstance();

        File file = new File(plugin.getDataFolder(), "CompanyData.yml");

        if (!file.exists()) {
            try {
                file.createNewFile();
            } catch (IOException e) {
                e.fillInStackTrace(); // 오류 출력을 위해 변경했습니다.
            }
        }

        YamlConfiguration config = YamlConfiguration.loadConfiguration(file);

        config.set("Data.View", "true");

        ConfigurationSection section = config.getConfigurationSection("Data.Company");

        boolean isMember = false;
        String pcom = "";

        if (section != null) {
            for (String subKey : section.getKeys(false)) {
                if (config.getConfigurationSection("Data.Company." + subKey + ".member") != null) {
                    for (String muid : Objects.requireNonNull(config.getConfigurationSection("Data.Company." + subKey + ".member")).getKeys(false)) {
                        if (Objects.equals(muid, player.getUniqueId().toString())) {
                            String qtxt = "";
                            switch (config.getString("Data.Company." + subKey + ".member." + player.getUniqueId() + ".quest.id")) {
                                case "wheat_farm":
                                    qtxt = "밀을 수확하세요.";
                                    break;
                                case "beet_farm":
                                    qtxt = "비트를 수확하세요.";
                                    break;
                                case "potato_farm":
                                    qtxt = "감자를 수확하세요.";
                                    break;
                                case "carrot_farm":
                                    qtxt = "당근을 수확하세요.";
                                    break;
                                case "cobble_mine":
                                    qtxt = "조약돌을 채굴하세요.";
                                    break;
                                case "coal_mine":
                                    qtxt = "석탄을 채굴하세요.";
                                    break;
                                case "iron_mine":
                                    qtxt = "철광석을 채굴하세요.";
                                    break;
                                case "gold_mine":
                                    qtxt = "금광석을 채굴하세요.";
                                    break;
                                case "diamond_mine":
                                    qtxt = "다이아몬드를 채굴하세요.";
                                    break;
                                case "emerald_mine":
                                    qtxt = "에메랄드를 채굴하세요.";
                                    break;
                                case "cow_hunt":
                                    qtxt = "소를 사냥하세요.";
                                    break;
                                case "pig_hunt":
                                    qtxt = "돼지를 사냥하세요.";
                                    break;
                                case "rabbit_hunt":
                                    qtxt = "토끼를 사냥하세요.";
                                    break;
                                case "leather_hunt":
                                    qtxt = "사냥으로 가죽을 획득하세요.";
                                    break;
                                case "meat_hunt":
                                    qtxt = "사냥으로 고기를 획득하세요.";
                                    break;
                                case "enemy_hunt":
                                    qtxt = "몬스터를 처치하세요.";
                                    break;
                                case "salmon_fish":
                                    qtxt = "낚시로 연어를 낚으세요.";
                                    break;
                                case "cod_fish":
                                    qtxt = "낚시로 대구를 낚으세요.";
                                    break;
                                case "puffer_fish":
                                    qtxt = "낚시로 복어를 낚으세요.";
                                    break;
                            }
                            if (count == -1) {
                                player.sendMessage(ChatMessageType.ACTION_BAR,TextComponent.fromLegacyText(ChatColor.GREEN + qtxt + " [" + config.getString("Data.Company." + subKey + ".member." + player.getUniqueId().toString() + ".quest.maxcount") + "/" + config.getString("Data.Company." + subKey + ".member." + player.getUniqueId().toString() + ".quest.maxcount") + "]"));
                                // player.sendActionBar(ChatColor.GREEN + qtxt + " [" + config.getString("Data.Company." + subKey + ".member.quest.maxcount") + "/" + config.getString("Data.Company." + subKey + ".member.quest.maxcount") + "]");
                            } else {
                                player.sendMessage(ChatMessageType.ACTION_BAR,TextComponent.fromLegacyText(ChatColor.YELLOW + qtxt + " [" + count + "/" + config.getString("Data.Company." + subKey + ".member." + player.getUniqueId().toString() + ".quest.maxcount") + "]"));
                                // player.sendActionBar(ChatColor.YELLOW + qtxt + " [" + count + "/" + config.getString("Data.Company." + subKey + ".member.quest.maxcount") + "]");
                            }
                        }
                    }
                }
            }
        }
    }

    public static void QuestReset() {
        Random random = new Random();

        Plugin plugin = Company.getInstance();

        File file = new File(plugin.getDataFolder(), "CompanyData.yml");

        if (!file.exists()) {
            try {
                file.createNewFile();
            } catch (IOException e) {
                e.fillInStackTrace(); // 오류 출력을 위해 변경했습니다.
            }
        }

        YamlConfiguration config = YamlConfiguration.loadConfiguration(file);

        config.set("Data.View", "true");

        ConfigurationSection section = config.getConfigurationSection("Data.Company");

        boolean isMember = false;
        String pcom = "";

        if (section != null) {
            for (String subKey : section.getKeys(false)) {
                if (config.getConfigurationSection("Data.Company." + subKey + ".member") != null) {
                    for (String muid : Objects.requireNonNull(config.getConfigurationSection("Data.Company." + subKey + ".member")).getKeys(false)) {
                        config.set("Data",QuestRLogic(config,subKey,muid));
                    }
                }
            }
        }
        try {
            config.save(file);
        } catch (IOException e) {
            e.fillInStackTrace();
        }
    }
}
