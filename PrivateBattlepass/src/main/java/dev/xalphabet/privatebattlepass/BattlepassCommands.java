package dev.xalphabet.privatebattlepass;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.*;

public class BattlepassCommands implements CommandExecutor {
    private final BattlepassSystem battlepassSystem;

    public BattlepassCommands(BattlepassSystem battlepassSystem) {
        this.battlepassSystem = battlepassSystem;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (label.equalsIgnoreCase("battlepass")) {
            if (sender instanceof Player) {
                Player player = (Player) sender;
                int page = 1; // Default to page 1 or parse from args
                if (args.length > 0) {
                    try {
                        page = Integer.parseInt(args[0]);
                    } catch (NumberFormatException e) {
                        player.sendMessage(ChatColor.RED + "Invalid page number. Showing page 1.");
                    }
                }
                player.openInventory(battlepassSystem.getBattlepassGUI().getInventory(player, page));
                return true;
            }
        } else if (label.equalsIgnoreCase("addreward")) {
            if (args.length < 8) {
                sender.sendMessage(ChatColor.RED + "Usage: /addreward <page> <rewardName> <material> <slot> <displayName> <lore> <leftClickCommand> <rightClickCommand> [<requirementKey1> <requirementValue1> ...]");
                return false;
            }

            try {
                int page = Integer.parseInt(args[0]);
                String rewardName = args[1];
                String materialString = args[2].toUpperCase();
                Material material;
                try {
                    material = Material.valueOf(materialString);
                } catch (IllegalArgumentException e) {
                    sender.sendMessage(ChatColor.RED + "Invalid material: " + materialString);
                    return false;
                }
                int slot = Integer.parseInt(args[3]);

                if (battlepassSystem.isSlotOccupied(page, slot)) {
                    sender.sendMessage(ChatColor.RED + "Slot " + slot + " is already occupied on page " + page + ".");
                    return false;
                }

                String displayName = ChatColor.translateAlternateColorCodes('&', args[4]);
                String lore = args[5].replace("\\n", "\n");

                String leftClickCommand = args[6];
                String rightClickCommand = args[7];

                List<String> leftClickCommands = new ArrayList<>();
                leftClickCommands.add(leftClickCommand);

                List<String> rightClickCommands = new ArrayList<>();
                rightClickCommands.add(rightClickCommand);

                Map<String, Integer> requirements = parseRequirements(Arrays.copyOfRange(args, 8, args.length));

                // Format the lore to include requirements
                StringBuilder formattedLore = new StringBuilder(lore);
                for (Map.Entry<String, Integer> entry : requirements.entrySet()) {
                    formattedLore.append("\n&bRequirement: ").append(entry.getKey()).append("/").append(entry.getValue());
                }

                battlepassSystem.addReward(page, rewardName, material, slot, displayName, formattedLore.toString(), leftClickCommands, rightClickCommands, requirements);
                sender.sendMessage(ChatColor.GOLD + "" + ChatColor.BOLD + "[Battlepass++] " + ChatColor.BOLD + "" + ChatColor.WHITE + "Reward '" + rewardName + "' added to page " + page + " successfully!");

                return true;
            } catch (NumberFormatException e) {
                sender.sendMessage(ChatColor.RED + "Invalid number format in arguments.");
                return false;
            }
        }
        return false;
    }

    private Map<String, Integer> parseRequirements(String[] args) {
        Map<String, Integer> requirements = new HashMap<>();
        if (args.length % 2 != 0) {
            return requirements; // Invalid number of arguments
        }
        for (int i = 0; i < args.length; i += 2) {
            String key = args[i];
            try {
                int value = Integer.parseInt(args[i + 1]);
                requirements.put(key, value);
            } catch (NumberFormatException e) {
                // Ignore invalid requirement value
            }
        }
        return requirements;
    }
}
