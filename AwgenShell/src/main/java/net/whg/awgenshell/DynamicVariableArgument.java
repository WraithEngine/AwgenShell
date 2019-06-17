package net.whg.awgenshell;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class DynamicVariableArgument implements CommandArgument
{
	private Logger logger = LoggerFactory.getLogger(DynamicVariableArgument.class);

	private CommandList commandList;
	private CommandSender sender;
	private String line;

	public DynamicVariableArgument(CommandList commandList, CommandSender sender, String line)
	{
		this.commandList = commandList;
		this.sender = sender;
		this.line = line;
	}

	@Override
	public String getValue()
	{
		try
		{
			CommandSet set = CommandParser.parse(commandList, sender, line);
			commandList.executeCommandSet(set);
			CommandVariable var = set.getFinalOutput();

			if (var == null)
				return "";

			return var.getValue();
		}
		catch (Exception exception)
		{
			logger.error("Failed to get dynamic variable value!", exception);
			return "";
		}
	}

	@Override
	public String toString()
	{
		return String.format("$[%s]", line);
	}
}
