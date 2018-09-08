package com.carrot.carrotshop;


import java.io.File;
import java.io.IOException;

import org.spongepowered.api.command.CommandSource;
import org.spongepowered.api.text.Text;
import org.spongepowered.api.text.format.TextColors;

import ninja.leaping.configurate.commented.CommentedConfigurationNode;
import ninja.leaping.configurate.hocon.HoconConfigurationLoader;
import ninja.leaping.configurate.loader.ConfigurationLoader;

public class ShopConfig
{
	private static File configFile;
	private static ConfigurationLoader<CommentedConfigurationNode> configManager;
	private static CommentedConfigurationNode config;

	public static void init(File rootDir)
	{
		configFile = new File(rootDir, "config.conf");
		configManager = HoconConfigurationLoader.builder().setPath(configFile.toPath()).build();
	}

	public static void load()
	{
		load(null);
	}

	public static void load(CommandSource src)
	{
		// load file
		try
		{
			if (!configFile.exists())
			{
				configFile.getParentFile().mkdirs();
				configFile.createNewFile();
				config = configManager.load();
				configManager.save(config);
			}
			config = configManager.load();
		}
		catch (IOException e)
		{
			CarrotShop.getLogger().error(Lang.CMD_CONFIG_RELOAD_FILE);
			e.printStackTrace();
			if (src != null)
			{
				src.sendMessage(Text.of(TextColors.RED, Lang.CMD_CONFIG_RELOAD_FILE));
			}
		}
		

		// check integrity

		config.getNode("taxes").setComment("Percentage of the displayed price that will not be given to shop owner. Note that this option might not work well if you are using special economy plugins such as the ones that use items as currency");
		Utils.ensurePositiveNumber(config.getNode("taxes", "other", "Heal"), 0);
		Utils.ensurePositiveNumber(config.getNode("taxes", "user", "Buy"), 0);
		Utils.ensurePositiveNumber(config.getNode("taxes", "user", "Sell"), 0);
		Utils.ensurePositiveNumber(config.getNode("taxes", "user", "DeviceOn"), 0);
		Utils.ensurePositiveNumber(config.getNode("taxes", "user", "DeviceOff"), 0);
		Utils.ensurePositiveNumber(config.getNode("taxes", "user", "Toggle"), 0);

		config.getNode("cost").setComment("Cost for creating a sign");
		Utils.ensurePositiveNumber(config.getNode("cost", "other", "Bank"), 0);
		Utils.ensurePositiveNumber(config.getNode("cost", "other", "Heal"), 0);
		Utils.ensurePositiveNumber(config.getNode("cost", "user", "Buy"), 0);
		Utils.ensurePositiveNumber(config.getNode("cost", "user", "Sell"), 0);
		Utils.ensurePositiveNumber(config.getNode("cost", "user", "DeviceOn"), 0);
		Utils.ensurePositiveNumber(config.getNode("cost", "user", "DeviceOff"), 0);
		Utils.ensurePositiveNumber(config.getNode("cost", "user", "Toggle"), 0);
		Utils.ensurePositiveNumber(config.getNode("cost", "user", "Trade"), 0);

		config.getNode("others", "emptyhand").setComment("Using signs require empty hands, see https://github.com/TheoKah/CarrotShop/issues/30");
		Utils.ensureBoolean(config.getNode("others", "emptyhand"), false);

		config.getNode("others", "currency").setComment("The default currency is not stored in this file for technical reasons. You may change it with ingame commands");

		save();
		if (src != null)
		{
			src.sendMessage(Text.of(TextColors.GREEN, Lang.CMD_CONFIG_RELOAD));
		}
	}

	public static void save()
	{
		try
		{
			configManager.save(config);
		}
		catch (IOException e)
		{
			CarrotShop.getLogger().error("Could not save config file !");
		}
	}

	public static CommentedConfigurationNode getNode(String... path)
	{
		return config.getNode((Object[]) path);
	}

	public static class Utils
	{
		public static void ensureString(CommentedConfigurationNode node, String def)
		{
			if (node.getString() == null)
			{
				node.setValue(def);
			}
		}

		public static void ensurePositiveNumber(CommentedConfigurationNode node, Number def)
		{
			if (!(node.getValue() instanceof Number) || node.getDouble(-1) < 0)
			{
				node.setValue(def);
			}
		}

		public static void ensureBoolean(CommentedConfigurationNode node, boolean def)
		{
			if (!(node.getValue() instanceof Boolean))
			{
				node.setValue(def);
			}
		}
	}
}