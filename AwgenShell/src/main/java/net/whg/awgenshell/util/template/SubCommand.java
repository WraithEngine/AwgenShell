package net.whg.awgenshell.util.template;

import java.util.LinkedList;
import java.util.List;
import net.whg.awgenshell.perms.PermissionNode;

/**
 * Represents a compiled subcommand which checks if a given set of inputs match
 * this subcommand pattern.
 *
 * @author TheDudeFromCI
 */
public class SubCommand
{
	public static SubCommand compile(String pattern, SubCommandExecutor executor, PermissionNode permission,
			CommandFlagTemplate[] flags)
	{
		String[] parts = pattern.split("\\s");
		CommandTemplateArg[] args = new CommandTemplateArg[parts.length];

		for (int i = 0; i < args.length; i++)
			args[i] = compileArg(parts[i]);

		return new SubCommand(args, executor, permission, flags);

	}

	private static CommandTemplateArg compileArg(String word)
	{
		if (word.contains("|"))
		{
			String[] subwords = word.split("\\|");
			CommandTemplateArg[] subpatterns = new CommandTemplateArg[subwords.length];

			for (int i = 0; i < subwords.length; i++)
				subpatterns[i] = compileArg(subwords[i]);

			return new OrPattern(subpatterns);
		}

		if (word.startsWith("%"))
		{
			switch (word)
			{
				case "%n":
					return new EndOfLinePattern();

				case "%*":
					return new WildcardPattern(false, false);

				case "%**":
					return new WildcardPattern(true, true);

				case "%*+":
					return new WildcardPattern(true, false);

				case "%*?":
					return new WildcardPattern(false, false);

				case "%#":
					return new NumberPattern(false);

				case "%#f":
					return new NumberPattern(true);

				case "%{}":
					return new IndirectCommandPattern();

				case "%$":
					return new VariablePattern();

				case "%-":
					return new FlagsPattern();

				default:
					throw new IllegalArgumentException("Unknown pattern type! " + word);
			}
		}
		else
			return new DirectWordPattern(word);
	}

	private final CommandTemplateArg[] pattern;
	private final SubCommandExecutor executor;
	private final PermissionNode permission;
	private final CommandFlagTemplate[] flags;

	/**
	 * Creates a new subcommand with a given of compiled template arguments.
	 *
	 * @param pattern
	 *     - An array of compiled template arguments.
	 * @param executor
	 *     - The executor for this subcommand.
	 */
	public SubCommand(CommandTemplateArg[] pattern, SubCommandExecutor executor, PermissionNode permission,
			CommandFlagTemplate[] flags)
	{
		this.pattern = pattern;
		this.executor = executor;
		this.permission = permission;
		this.flags = flags;
	}

	/**
	 * Checks if the given set of inputs arguments matches this subcommand.
	 *
	 * @param args
	 *     - The input arguments
	 * @return True if this subcommand is a valid match, false otherwise.
	 */
	public boolean matches(List<InputArgument> args)
	{
		List<InputArgument> toPrune = new LinkedList<>();

		int offset = 0;
		int lastLength = 0;
		for (int i = 0; i < pattern.length; i++)
		{
			CommandTemplateArg a = pattern[i];

			int out;
			while (true)
			{
				out = a.matchArguments(args, offset, this);

				if (out > -1)
					break;

				if (i == 0)
					return false;

				CommandTemplateArg b = pattern[i - 1];
				int back = b.giveBack(args, this, offset, lastLength);

				lastLength -= back;
				offset -= back;

				if (back == 0)
					return false;
			}

			if (a.pruneArgs())
				for (int j = 0; j < out; j++)
					toPrune.add(args.get(j + offset));

			offset += out;
			lastLength = out;
		}

		for (InputArgument input : toPrune)
			args.remove(input);

		return true;
	}

	/**
	 * Gets the executor attached to this subcommand.
	 *
	 * @return - The executor.
	 */
	public SubCommandExecutor getExecutor()
	{
		return executor;
	}

	/**
	 * Gets the permission node required to use this subcommand. Value returns null
	 * if the parent permission node of the command is intended to be used instead.
	 *
	 * @return The required permission node. May be null.
	 */
	public PermissionNode getPermissions()
	{
		return permission;
	}

	/**
	 * Gets a list of all possible flags which can be applied to this subcommand.
	 *
	 * @return An array of all possible flags.
	 */
	public CommandFlagTemplate[] getFlags()
	{
		return flags;
	}
}
