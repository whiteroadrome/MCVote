package com.whiteroad;

import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.HoverEvent;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.api.chat.hover.content.Text;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;

import java.io.File;
import java.io.IOException;
import java.util.*;

import org.bukkit.plugin.Plugin;

public class Employee {
    // 직원 관리 기능을 구현하는 클래스

    // 입사신청 처리 기능을 구현하는 메서드
    public void applyForJob(CommandSender sender) {
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

        ConfigurationSection section = config.getConfigurationSection("Data.Company");
        String cpname = "";
        boolean isboss = false;

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
            sender.sendMessage("회사를 소유하고 있지 않습니다.");
            return;
        }

        if (config.get("Data.Company." + cpname + ".MemberR.member") == null) {
            sender.sendMessage("현재 신청자가 없습니다!");
            return;
        }

        if (config.getString("Data.Company." + cpname + ".MemberR.IsEnable").equals("true")) {
            ConfigurationSection section02 = config.getConfigurationSection("Data.Company." + cpname + ".MemberR.member");
            for (String uidv : section02.getKeys(false)) {
                Player Gplayer = Bukkit.getPlayer(UUID.fromString(uidv));

                TextComponent message = new TextComponent(ChatColor.BOLD + "" + ChatColor.GREEN + "[수락하기]");
                message.setClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, ("/company accept " + Gplayer.getUniqueId())));
                message.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new Text("클릭하여 수락")));

                TextComponent message2 = new TextComponent(ChatColor.BOLD + "" + ChatColor.RED + "[거절하기]");
                message2.setClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, ("/company deny " + Gplayer.getUniqueId())));
                message2.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new Text("클릭하여 거절")));

                TextComponent message3 = new TextComponent(ChatColor.BOLD + "" + ChatColor.AQUA + "[UUID]");
                message3.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new Text(Gplayer.getUniqueId().toString())));

                sender.sendMessage(ChatColor.BOLD + "" + ChatColor.YELLOW + "[" + Gplayer.getName() + "]");
                sender.sendMessage(message,message2,message3);
                sender.sendMessage(" ");
            }
        }
    }

    public void acceptApplication(CommandSender sender, String uuid) {
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

        ConfigurationSection section = config.getConfigurationSection("Data.Company");
        String cpname = "";
        boolean isboss = false;
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

        if (cpname.equals("")) {
            sender.sendMessage("회사에 속한 상태가 아닙니다.");
            return;
        }

        if (grade.equals("vice") || grade.equals("boss")) {
            Calendar calendar = Calendar.getInstance();
            String nowdate = calendar.get(Calendar.YEAR) + "-" + calendar.get(Calendar.MONTH) + "-" + calendar.get(Calendar.DAY_OF_MONTH);
            config.set("Data.Company." + cpname + ".member." + uuid + ".grade", "member");
            config.set("Data.Company." + cpname + ".member." + uuid + ".joinDate", nowdate);
            config.set("Data.Company." + cpname + ".MemberR.member." + uuid,null);

            sender.sendMessage("승인하였습니다.");

            try {
                config.save(file);
            } catch (IOException e) {
                e.fillInStackTrace();
            }
        } else {
            sender.sendMessage(ChatColor.RED + "해당 작업을 수행할 권한이 없습니다.");
        }
    }

    // 승인 거절 기능을 구현하는 메서드
    public void denyApplication(CommandSender sender, String uuid) {
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

        ConfigurationSection section = config.getConfigurationSection("Data.Company");
        String cpname = "";
        boolean isboss = false;
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

        if (cpname.equals("")) {
            sender.sendMessage("회사에 속한 상태가 아닙니다.");
            return;
        }

        if (grade.equals("vice") || grade.equals("boss")) {
            config.set("Data.Company." + cpname + ".MemberR.member." + uuid,null);

            sender.sendMessage("거절하였습니다.");

            try {
                config.save(file);
            } catch (IOException e) {
                e.fillInStackTrace();
            }
        } else {
            sender.sendMessage(ChatColor.RED + "해당 작업을 수행할 권한이 없습니다.");
        }
    }

    // 해고 기능을 구현하는 메서드
    public void fireEmployee(CommandSender sender, String uuid) {
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

        ConfigurationSection section = config.getConfigurationSection("Data.Company");
        String cpname = "";
        boolean isboss = false;
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

        if (cpname.equals("")) {
            sender.sendMessage("회사에 속한 상태가 아닙니다.");
            return;
        }

        if (grade.equals("vice") || grade.equals("boss")) {
            config.set("Data.Company." + cpname + ".member." + uuid,null);
            sender.sendMessage("직원을 해고하였습니다.");

            try {
                config.save(file);
            } catch (IOException e) {
                e.fillInStackTrace();
            }
        } else {
            sender.sendMessage(ChatColor.RED + "해당 작업을 수행할 권한이 없습니다.");
        }
    }

    public void receiptCompany(CommandSender sender, String cpname) {
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

        ConfigurationSection section = config.getConfigurationSection("Data.Company");

        if (section != null) {
            for (String subKey : section.getKeys(false)) {
                if (config.getConfigurationSection("Data.Company." + subKey + ".member") != null) {
                    config.set("Data.Company." + subKey + ".MemberR.member." + ((Player) sender).getUniqueId(), sender.getName().toString());
                    sender.sendMessage("신청이 접수되었습니다!");
                    try {
                        config.save(file);
                    } catch (IOException e) {
                        e.fillInStackTrace();
                    }
                    return;
                }
            }
        }
        sender.sendMessage("회사가 존재하지 않습니다.");
    }

    // 직급 변경 기능을 구현하는 메서드
    public void changePosition(CommandSender sender, String uuid, String grade) {
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

        ConfigurationSection section = config.getConfigurationSection("Data.Company");
        String cpname = "";
        boolean isboss = false;
        String grade1 = "";

        if (section != null) {
            for (String subKey : section.getKeys(false)) {
                if (config.getConfigurationSection("Data.Company." + subKey + ".member") != null) {
                    if (config.get("Data.Company." + subKey + ".member." + ((Player) sender).getUniqueId()) != null) {
                        if (Objects.equals(Objects.requireNonNull(config.get("Data.Company." + subKey + ".member." + ((Player) sender).getUniqueId() + ".grade")).toString(), "boss")) {
                            cpname = subKey;
                            grade1 = config.getString("Data.Company." + subKey + ".member." + ((Player) sender).getUniqueId() + ".grade");
                        }
                    }
                }
            }
        }

        if (cpname.equals("")) {
            sender.sendMessage("회사에 속한 상태가 아닙니다.");
            return;
        }

        if (grade.equals("Director") || grade.equals("vice") || grade.equals("boss")){
            switch (grade) {
                case "사원":
                    config.set("Data.Company." + cpname + ".member." + uuid + ".grade", "member");
                    break;
                case "대리":
                    config.set("Data.Company." + cpname + ".member." + uuid + ".grade", "staff");
                    break;
                case "과장":
                    config.set("Data.Company." + cpname + ".member." + uuid + ".grade", "manager");
                    break;
                case "전무":
                    config.set("Data.Company." + cpname + ".member." + uuid + ".grade", "master");
                    break;
                case "부장":
                    config.set("Data.Company." + cpname + ".member." + uuid + ".grade", "director");
                    break;
                case "부사장":
                    config.set("Data.Company." + cpname + ".member." + uuid + ".grade", "vice");
                    break;
            }

            try {
                config.save(file);
            } catch (IOException e) {
                e.fillInStackTrace();
            }
            sender.sendMessage("직원의 직급을" + grade + "으로 변경합니다.");
            return;
        }
        sender.sendMessage(ChatColor.RED + "해당 작업을 수행할 권한이 없습니다.");
    }
}
