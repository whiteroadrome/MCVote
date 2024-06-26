package com.whiteroad;

import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.io.IOException;
import java.util.*;


public class Stock {
    private JavaPlugin plugin;
    private Map<String, List<String>> userStocks = new HashMap<>();
    private File stockOpenFile;
    private FileConfiguration stockOpenConfig;
    private File sDateFile;
    private FileConfiguration sDateConfig;
    static Random random = new Random();


    public static void Startcalc() {
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

        double resultv = 0.0;

        if (section != null) {
            for (String subKey : section.getKeys(false)) {
                // 각 하위 키와 해당 값 출력
                System.out.println(subKey + ": " + section.get(subKey));
                resultv = 0.0;
                if ((config.get("Data.Company." + subKey + ".Stock.value") != null) && (config.get("Data.Company." + subKey + ".Stock.default") != null) && Objects.equals((config.get("Data.Company." + subKey + ".Stock.toggle")), "true")) {

                    ConfigurationSection subsection = config.getConfigurationSection("Data.Company." + subKey + ".member");

                    if (subsection != null) {
                        List<String> mlist = List.copyOf(subsection.getKeys(false));
                        resultv = mlist.size() * 10000; // 직원당 시작가 설정하기
                        config.set("Data.Company." + subKey + ".Stock.default", resultv);
                        config.set("Data.Company." + subKey + ".Stock.value", resultv);
                        config.set("Data.Company." + subKey + ".Stock.Percent", "0");
                    }
                    try {
                        config.save(file);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }
        }
    }
    public static void Scalculate(Double pvalue) {

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

        double resultv = 0.0;
        double defaultv = 0.0;

        if (section != null) {
            for (String subKey : section.getKeys(false)) {
                // 각 하위 키와 해당 값 출력
                System.out.println(subKey + ": " + section.get(subKey));
                resultv = 0.0;
                defaultv = 0.0;
                if ((config.get("Data.Company." + subKey + ".Stock.value") != null) && (config.get("Data.Company." + subKey + ".Stock.default") != null) && Objects.equals((config.get("Data.Company." + subKey + ".Stock.toggle")), "true")) {
                    resultv = Double.parseDouble(config.getString("Data.Company." + subKey + ".Stock.value"));
                    defaultv = Double.parseDouble(config.getString("Data.Company." + subKey + ".Stock.default"));

                    double changeper = 50;
                    double changevalue = 0;

                    double percentv = (Double.parseDouble(config.getString("Data.Company." + subKey + ".Stock.Percent.Sell")) - Double.parseDouble(config.getString("Data.Company." + subKey + ".Stock.Percent.Buy"))) / (Double.parseDouble(config.getString("Data.Company." + subKey + ".Stock.Percent.Buy"))+Double.parseDouble(config.getString("Data.Company." + subKey + ".Stock.Percent.Sell")));
                    double questv = Double.parseDouble(config.getString("Data.Company." + subKey + ".QuestClear")) / Double.parseDouble(config.getString("Data.Company." + subKey + ".MemberCount"));
                    changeper += (percentv * 25) + (questv/20);

                    if (random.nextInt(100) >= changeper) {
                        double changeval = (Math.sqrt(defaultv / resultv) * 25) + ((Math.sqrt(defaultv / resultv) * 25) * ((random.nextInt(12001) - 6000) * 0.0001));
                        changeval *= pvalue; // 기본 0.0005
                        changevalue = resultv + (resultv * changeval);
                        config.set("Data.Company." + subKey + ".Stock.value", Math.round(changevalue));
                    } else {
                        double changeval = (Math.sqrt(resultv / defaultv) * 25) + ((Math.sqrt(resultv / defaultv) * 25) * ((random.nextInt(12001) - 6000) * 0.0001));
                        changeval *= pvalue; // 기본 0.0005
                        changevalue = resultv - (resultv * changeval);
                        config.set("Data.Company." + subKey + ".Stock.value", Math.round(changevalue));
                    }
                    try {
                        config.save(file);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }
        }
    }

    public Stock(JavaPlugin plugin) {
        this.plugin = plugin;

        // StockUser.yml 파일 로드
        stockOpenFile = new File(plugin.getDataFolder(), "StockUser.yml");
        if (!stockOpenFile.exists()) {
            plugin.saveResource("StockUser.yml", false);
        }
        stockOpenConfig = YamlConfiguration.loadConfiguration(stockOpenFile);

        // Sdate.yml 파일 로드
        sDateFile = new File(plugin.getDataFolder(), "Sdate.yml");
        if (!sDateFile.exists()) {
            plugin.saveResource("Sdate.yml", false);
        }
        sDateConfig = YamlConfiguration.loadConfiguration(sDateFile);
    }

    public static void BSStock(CommandSender sender, String cpname, String countS) {
        int count = Integer.parseInt(countS);
        Plugin plugin = Company.getInstance();
        File file = new File(plugin.getDataFolder(), "CompanyData.yml");

        if (!file.exists()) {
            try {
                file.createNewFile();
            } catch (IOException e) {
                e.fillInStackTrace(); // 오류 출력을 위해 변경했습니다.
            }
        }

        File file2 = new File(plugin.getDataFolder(), "StockUser.yml");

        if (!file2.exists()) {
            try {
                file2.createNewFile();
            } catch (IOException e) {
                e.fillInStackTrace(); // 오류 출력을 위해 변경했습니다.
            }
        }

        Player player = (Player) sender;
        YamlConfiguration config = YamlConfiguration.loadConfiguration(file);
        YamlConfiguration config2 = YamlConfiguration.loadConfiguration(file2);

        ConfigurationSection section = config.getConfigurationSection("Data.Company");

        if (section != null) {
            for (String subKey : section.getKeys(false)) {
                if (subKey.equals(cpname) && (config.getString("Data.Company." + cpname + ".Stock.toggle").equals("true"))) {
                    int buyc = Integer.parseInt(config.getString("Data.Company." + cpname + ".Stock.buyc"));
                    int sellc = Integer.parseInt(config.getString("Data.Company." + cpname + ".Stock.sellc"));
                    int price = Integer.parseInt(config.getString("Data.Company." + cpname + ".Stock.value"));
                    if (count > 0) {
                        if (config2.get(player.getUniqueId() + ".BuyList." + cpname + ".count") == null) {
                            config2.set(player.getUniqueId() + ".BuyList." + cpname + ".count","0");
                        }
                        if (!Company.checkMoney(player,(price*count))) {
                            sender.sendMessage(ChatColor.RED + "돈이 충분하지 않습니다.");
                            return;
                        }
                        if (sellc < count) {
                            sender.sendMessage(ChatColor.RED + "이 회사의 주식 상한을 초과합니다.");
                            return;
                        }
                        Company.takeMoney(player,(price*count));
                        config.set("Data.Company." + cpname + ".MoneyBank",""+(Double.parseDouble(config.getString("Data.Company." + cpname + ".MoneyBank"))+(price*count)));
                        config.set("Data.Company." + cpname + ".Stock.buyc",""+(buyc+(count)));
                        config.set("Data.Company." + cpname + ".Stock.sellc",""+(sellc-(count)));
                        config2.set(player.getUniqueId() + ".BuyList." + cpname + ".count",""+(Integer.parseInt(config2.getString(player.getUniqueId() + ".BuyList." + cpname + ".count"))+count));
                        sender.sendMessage(ChatColor.GREEN + "주식 " + ChatColor.YELLOW + count + ChatColor.GREEN + "주를 성공적으로 매수했어요! (가격 : " + ChatColor.YELLOW + (price*count) + ChatColor.GREEN + "원)");
                    } else if (count < 0) {
                        count *= -1;
                        if (config2.get(player.getUniqueId() + ".BuyList." + cpname + ".count") == null) {
                            config2.set(player.getUniqueId() + ".BuyList." + cpname + ".count","0");
                        }
                        if (Integer.parseInt(config2.getString(player.getUniqueId() + ".BuyList." + cpname + ".count")) < count) {
                            sender.sendMessage(ChatColor.RED + "보유한 주식이 충분하지 않습니다.");
                            return;
                        }
                        if (buyc < count) {
                            sender.sendMessage(ChatColor.RED + "이 회사의 주식 상한을 초과합니다.");
                            return;
                        }
                        Company.giveMoney(player,(price*count));
                        config.set("Data.Company." + cpname + ".MoneyBank",""+(Double.parseDouble(config.getString("Data.Company." + cpname + ".MoneyBank"))-(price*count)));
                        config.set("Data.Company." + cpname + ".Stock.buyc",""+(buyc-(count)));
                        config.set("Data.Company." + cpname + ".Stock.sellc",""+(sellc+(count)));
                        config2.set(player.getUniqueId() + ".BuyList." + cpname + ".count",""+(Integer.parseInt(config2.getString(player.getUniqueId() + ".BuyList." + cpname + ".count"))-count));
                        sender.sendMessage(ChatColor.GREEN + "주식 " + ChatColor.YELLOW + count + ChatColor.GREEN + "주를 성공적으로 매도했어요! (가격 : " + ChatColor.YELLOW + (price*count) + ChatColor.GREEN + "원)");
                    } else {
                        sender.sendMessage(ChatColor.RED + "개수를 올바르게 입력하여 주세요.");
                        return;
                    }
                    try {
                        config.save(file);
                    } catch (IOException e) {
                        e.fillInStackTrace();
                    }
                    try {
                        config2.save(file2);
                    } catch (IOException e) {
                        e.fillInStackTrace();
                    }
                    return;
                }
            }
        }
    }

    // 주식 시장이 열려 있는지 확인하는 메서드
    public boolean isStockMarketOpen() {
        if (stockOpenFile.exists()) {
            return stockOpenConfig.getBoolean("stock_market_open");
        } else {
            // stock_open.yml 파일이 없으면 기본값으로 true 반환
            return true;
        }
    }

    // 주식 시장을 토글하는 메서드
    public void toggleStockMarket(boolean isOpen) {
        if (stockOpenFile.exists()) {
            stockOpenConfig.set("stock_market_open", isOpen);
            try {
                stockOpenConfig.save(stockOpenFile);
            } catch (IOException ex) {
                ex.printStackTrace();
            }
        }
    }

    // 회사가 상장되어 있는지 확인하는 메서드
    public boolean isCompanyListed(String companyName) {
        return stockOpenConfig.getBoolean("companies." + companyName.toLowerCase() + ".listed", false);
    }

    // 회사의 상장 여부를 토글하는 메서드
    public void toggleCompanyListing(String companyName, boolean isListed) {
        stockOpenConfig.set("companies." + companyName.toLowerCase() + ".listed", isListed);
        try {
            stockOpenConfig.save(stockOpenFile);
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }

    // 주식을 구매하는 메서드
    public void buyStocks(Player player, String companyName, int quantity) {
        // Sdate가 true인지 확인
        if (sDateFile.exists() && sDateConfig.getBoolean("Sdate", false)) {
            // 주식 시장이 열려 있는지 확인
            if (isStockMarketOpen()) {
                // 회사가 상장되어 있는지 확인
                if (isCompanyListed(companyName)) {
                    // 플레이어가 충분한 돈을 가지고 있는지 확인
                    // Vault 플러그인을 통해 구현되어야 함

                    // 주식 구매
                    player.sendMessage(quantity + "개의 " + companyName + " 주식을 구매하였습니다.");
                } else {
                    player.sendMessage("해당 회사는 주식 시장에 상장되어 있지 않습니다.");
                }
            } else {
                player.sendMessage("주식 시장이 현재 닫혀있습니다.");
            }
        } else {
            player.sendMessage("주식 거래 시간이 아닙니다.");
        }
    }

    // 주식을 판매하는 메서드
    public void sellStocks(Player player, String companyName, int quantity) {
        // Sdate가 true인지 확인
        if (sDateFile.exists() && sDateConfig.getBoolean("Sdate", false)) {
            // 주식 시장이 열려 있는지 확인
            if (isStockMarketOpen()) {
                // 회사가 상장되어 있는지 확인
                if (isCompanyListed(companyName)) {
                    // 주식 판매
                    player.sendMessage(quantity + "개의 " + companyName + " 주식을 판매하였습니다.");
                } else {
                    player.sendMessage("해당 회사는 주식 시장에 상장되어 있지 않습니다.");
                }
            } else {
                player.sendMessage("주식 시장이 현재 닫혀있습니다.");
            }
        } else {
            player.sendMessage("주식 거래 시간이 아닙니다.");
        }
    }
    // 유저의 주식 정보를 가져오는 메서드 (예시)
    public Map<String, Integer> getUserStocks(UUID userUUID) {
        // stock_prices.yml 파일에서 주어진 유저의 주식 정보를 가져오는 로직
        File pricesFile = new File(plugin.getDataFolder(), "stock_prices.yml");
        FileConfiguration pricesConfig = YamlConfiguration.loadConfiguration(pricesFile);

        Map<String, Integer> userStocks = new HashMap<>();
        if (pricesConfig.isConfigurationSection("users." + userUUID)) {
            ConfigurationSection userSection = pricesConfig.getConfigurationSection("users." + userUUID);
            for (String companyName : userSection.getKeys(false)) {
                int quantity = userSection.getInt(companyName, 0);
                userStocks.put(companyName, quantity);
            }
        }
        return userStocks;
    }

    // 총 주식 가격을 계산하는 메서드
    public double calculateTotalStockPrice(String nickname) {
        double totalPrice = 0.0;

        // 사용자가 보유한 주식 목록 가져오기
        List<String> userStockList = userStocks.get(nickname);

        // 주식 시장이 열려 있고 사용자가 주식을 보유하고 있는 경우에만 가격 계산
        if (isStockMarketOpen() && userStockList != null) {
            for (String companyName : userStockList) {
                // 회사가 상장되어 있고 주식의 가격을 가져오는 로직
                if (isCompanyListed(companyName)) {
                    // 주식의 가격을 가져오는 로직은 여기에 구현
                    double stockPrice = getStockPrice(companyName); // 가격 가져오는 메서드는 사용자가 구현해야 함
                    totalPrice += stockPrice;
                }
            }
        }

        return totalPrice;
    }

    public double getStockPrice(String companyName) {
        // 가져오기 로직
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

        double resultv = 0.0;

        if (section != null) {
            for (String subKey : section.getKeys(false)) {
                // 각 하위 키와 해당 값 출력
                System.out.println(subKey + ": " + section.get(subKey));
                if (subKey.equals(companyName)) {
                    if (config.get("Data.Company." + subKey + ".Stock.value") != null) {
                        resultv = config.getDouble("Data.Company." + subKey + ".Stock.value");
                    }
                }
            }
        }

        return resultv;
    }


    public static void Sweek(CommandSender sender, String value) {
        if (value.length() == 7 && (value.chars().filter(c -> c == '0').count() + value.chars().filter(c -> c == '1').count() == 7)) {


            Plugin plugin = Company.getInstance();
            File file = new File(plugin.getDataFolder(), "PluginSetting.yml");
            if (!file.exists()) {
                try {
                    file.createNewFile();
                } catch (IOException e) {
                    e.fillInStackTrace(); // 오류 출력을 위해 변경했습니다.
                }
            }
            YamlConfiguration config = YamlConfiguration.loadConfiguration(file);

            String textlist = "일월화수목금토";
            StringBuilder weektext = new StringBuilder();

            for (int i = 0;i<7;i++) {
                if (value.charAt(i) == '1') {
                    weektext.append(textlist.charAt(i));
                }
            }

            config.set("Plugin.Setting.Stock.Week",value);

            try {
                config.save(file);
            } catch (IOException e) {
                e.fillInStackTrace();
            }

            sender.sendMessage("성공적으로 설정되었습니다. (" + weektext + ")");
        } else {
            sender.sendMessage("올바른 설정값을 입력하세요!");
        }
    }

    public static void Sdate(CommandSender sender, String bool) {
        if (!Objects.equals(bool, "켜기") || !Objects.equals(bool, "끄기")) {
            sender.sendMessage("올바른 항목을 입력하세요. [켜기/끄기]");
        } else {
            if (Objects.equals(bool, "켜기")) {
                Company.stockBool = true;
                Startcalc(); // 켜지는 동시에 각 회사 주식 시작가 산정
                sender.sendMessage("성공적으로 변경되었습니다.");
            } else if (Objects.equals(bool, "끄기")) {
                Company.stockBool = false;
                sender.sendMessage("성공적으로 변경되었습니다.");
            } else {
                sender.sendMessage("예외가 발생하였습니다.");
            }
        }
    }

}
