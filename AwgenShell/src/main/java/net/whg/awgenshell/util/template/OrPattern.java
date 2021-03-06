package net.whg.awgenshell.util.template;

import java.util.List;

/**
 * A special type of pattern built of subpatterns. This pattern returns the
 * offset of the first non-negative output from all of the given input patterns
 * in order. If all input patterns return -1, this pattern also returns -1.
 *
 * @author TheDudeFromCI
 */
public class OrPattern implements CommandTemplateArg
{
	private final CommandTemplateArg[] patterns;

	public OrPattern(CommandTemplateArg[] patterns)
	{
		this.patterns = patterns;
	}

	@Override
	public int matchArguments(List<InputArgument> args, int offset, SubCommand sub)
	{
		for (CommandTemplateArg pattern : patterns)
		{
			int len = pattern.matchArguments(args, offset, sub);
			if (len >= 0)
				return len;
		}

		return -1;
	}
}
